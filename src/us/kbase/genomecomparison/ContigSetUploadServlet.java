package us.kbase.genomecomparison;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import us.kbase.common.service.Tuple3;
import us.kbase.common.service.Tuple4;
import us.kbase.common.service.UObject;
import us.kbase.genomecomparison.gbk.GbkCallback;
import us.kbase.genomecomparison.gbk.GbkLocation;
import us.kbase.genomecomparison.gbk.GbkParser;
import us.kbase.genomecomparison.gbk.GbkQualifier;
import us.kbase.genomecomparison.gbk.GbkSubheader;
import us.kbase.kbasegenomes.Contig;
import us.kbase.kbasegenomes.ContigSet;
import us.kbase.kbasegenomes.Feature;
import us.kbase.kbasegenomes.Genome;
import us.kbase.workspace.ObjectSaveData;
import us.kbase.workspace.SaveObjectsParams;
import us.kbase.workspace.WorkspaceClient;

public class ContigSetUploadServlet extends HttpServlet {
	private static final long serialVersionUID = -1L;
	
    private String configPath = null;
    
    public void init(ServletConfig servletConfig) throws ServletException {
        configPath = servletConfig.getInitParameter("config_file");
    }

    private File getTempDir() throws IOException {
		File tempDir = new File(".");
		if (configPath != null) {
			File f = new File(configPath);
			if (f.exists()) {
				Properties props = new Properties();
				props.load(new FileInputStream(f));
				if (props.containsKey("temp.dir"))
					tempDir = new File(props.getProperty("temp.dir"));
			}
		}
		return tempDir;
    }
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int maxMemoryFileSize = 50 * 1024 * 1024;
		File dir = getTempDir();
		DiskFileItemFactory factory = new DiskFileItemFactory(maxMemoryFileSize, dir);
		ServletFileUpload upload = new ServletFileUpload(factory);
		FileItem file = null;
		try {
			Stat.addUploader(dir);
			String token = null;
			String ws = null;
			String id = null;
			String type = null;
			List<?> items = upload.parseRequest(request);
			Iterator<?> it = items.iterator();
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (item.isFormField()) {
					if (item.getFieldName().equals("token")) {
						token = item.getString();
					} else if (item.getFieldName().equals("ws")) {
						ws = item.getString();
					} else if (item.getFieldName().equals("id")) {
						id = item.getString();
					} else if (item.getFieldName().equals("type")) {
						type = item.getString();
					} else {
						throw new ServletException("Unknown parameter: " + item.getFieldName());
					}
				} else if (item.getFieldName().equals("file"))  {
					long size = item.getSize();
					file = item;
					if (size > maxMemoryFileSize) {
						throw new ServletException("File size is too large: " + size + " > " + maxMemoryFileSize);
					}
				} else {
					throw new ServletException("Unknown parameter: " + item.getFieldName());
				}
			}
			check(token, "token");
			check(ws, "ws");
			check(id, "id");
			check(type, "type");
			check(file, "file");
			if (type.equals("contigfasta")) {
				FastaReader fr = new FastaReader(new InputStreamReader(file.getInputStream()));
				Map<String, String> contigIdToSeq = fr.readAll();
				fr.close();
				if (contigIdToSeq.size() == 0)
					throw new ServletException("Data was not defined or empty");
				List<Contig> contigList = new ArrayList<Contig>();
				for (String contigId : contigIdToSeq.keySet()) {
					String seq = contigIdToSeq.get(contigId);
					contigList.add(new Contig().withId(contigId).withName(contigId).withSequence(seq)
							.withLength((long)seq.length()).withMd5("md5"));
				}
				ContigSet contigSet = new ContigSet().withContigs(contigList).withId(id).withMd5("md5").withName(id)
						.withSource("User uploaded data").withSourceId("USER").withType("Organism");
				WorkspaceClient wc = TaskHolder.createWsClient(token);
				ObjectSaveData data = new ObjectSaveData().withName(id).withType("KBaseGenomes.ContigSet").withData(new UObject(contigSet));
				try {
					data.withObjid(Long.parseLong(id));
				} catch (NumberFormatException ex) {
					data.withName(id);
				}
				wc.saveObjects(new SaveObjectsParams().withWorkspace(ws).withObjects(Arrays.asList(data)));
				response.getOutputStream().write("Contig Set was successfuly uploaded".getBytes());
			} else if (type.equals("genomegbk")) {
				uploadGbk(file.getInputStream(), ws, id, token);
				response.getOutputStream().write("Genome was successfuly uploaded".getBytes());
			} else {
				throw new ServletException("Unknown file type: " + type);
			}
		} catch (Throwable ex) {
			ex.printStackTrace(new PrintStream(response.getOutputStream()));
		} finally {
			Stat.delUploader(dir);
			if (file != null)
				file.delete();
		}
	}
	
	public static void uploadGbk(InputStream is, String ws, String id, String token) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		final Map<String, Contig> contigMap = new LinkedHashMap<String, Contig>();
		final Genome genome = new Genome()
				.withComplete(1L).withDomain("Bacteria").withGeneticCode(11L).withId(id)
				.withNumContigs(1L).withSource("NCBI").withSourceId("NCBI");
		final List<Feature> features = new ArrayList<Feature>();
		GbkParser.parse(br, new GbkCallback() {
			@Override
			public void setGenome(String genomeName, int taxId) throws Exception {
				genome.withScientificName(genomeName);
				if (genome.getTaxonomy() == null)
					genome.withTaxonomy("Taxonomy ID: " + taxId);
			}
			@Override
			public void addSeqPart(String contigName, int seqPartIndex, String seqPart,
					int commonLen) throws Exception {
				Contig contig = contigMap.get(contigName);
				if (contig == null) {
					contig = new Contig().withId(contigName).withName(contigName).withMd5("md5")
							.withSequence("");
					contigMap.put(contigName, contig);
				}
				contig.withSequence(contig.getSequence() + seqPart);
			}
			@Override
			public void addHeader(String contigName, String headerType, String value,
					List<GbkSubheader> items) throws Exception {
				if (headerType.equals("SOURCE")) {
					String genomeName = value;
					genome.withScientificName(genomeName);
					for (GbkSubheader sub : items) {
						if (sub.type.equals("ORGANISM")) {
							String taxPath = sub.getValue();
							if (taxPath.endsWith("."))
								taxPath = taxPath.substring(0, taxPath.length() - 1).trim();
							genome.withTaxonomy(taxPath + "; " + genomeName);
						}
					}
				}
			}
			@Override
			public void addFeature(String contigName, String featureType, int strand,
					int start, int stop, List<GbkLocation> locations,
					List<GbkQualifier> props) throws Exception {
				Feature f = null;
				if (featureType.equals("CDS")) {
					 f = new Feature().withType("CDS");
				} else if (featureType.toUpperCase().endsWith("RNA")) {
					 f = new Feature().withType("rna");
				}
				if (f == null)
					return;
				List<Tuple4<String, Long, String, Long>> locList = new ArrayList<Tuple4<String, Long, String, Long>>();
				for (GbkLocation loc : locations) {
					long realStart = loc.strand > 0 ? loc.start : loc.stop;
					String dir = loc.strand > 0 ? "+" : "-";
					long len = loc.stop + 1 - loc.start;
					locList.add(new Tuple4<String, Long, String, Long>().withE1(contigName)
							.withE2(realStart).withE3(dir).withE4(len));
				}
				f.withLocation(locList).withAnnotations(new ArrayList<Tuple3<String, String, Long>>());
				f.withAliases(new ArrayList<String>());
				for (GbkQualifier prop : props) {
					if (prop.type.equals("locus_tag")) {
						f.setId(prop.getValue());
					} else if (prop.type.equals("translation")) {
						String seq = prop.getValue();
						f.withProteinTranslation(seq).withProteinTranslationLength((long)seq.length());
					} else if (prop.type.equals("note")) {
						f.setFunction(prop.getValue());
					} else if (prop.type.equals("product")) {
						if (f.getFunction() == null)
							f.setFunction(prop.getValue());
					} else if (prop.type.equals("gene")) {
						if (f.getId() == null)
							f.setId(prop.getValue());
						f.getAliases().add(prop.getValue());
					} else if (prop.type.equals("protein_id")) {
						f.getAliases().add(prop.getValue());
					}
				}
				features.add(f);
			}
		});
		if (contigMap.size() == 0) {
			throw new ServletException("GBK-file has no DNA-sequence");
		}
		WorkspaceClient wc = TaskHolder.createWsClient(token);
		String contigId = id + ".contigset";
		List<Long> contigLengths = new ArrayList<Long>();
		long dnaLen = 0;
		for (Contig contig : contigMap.values()) {
			if (contig.getSequence() == null || contig.getSequence().length() == 0) {
				throw new ServletException("Contig " + contig.getId() + " has no DNA-sequence");
			}
			contig.withLength((long)contig.getSequence().length());
			contigLengths.add(contig.getLength());
			dnaLen += contig.getLength();
		}
		ContigSet contigSet = new ContigSet().withContigs(new ArrayList<Contig>(contigMap.values()))
				.withId(id).withMd5("md5").withName(id)
				.withSource("User uploaded data").withSourceId("USER").withType("Organism");
		wc.saveObjects(new SaveObjectsParams().withWorkspace(ws)
				.withObjects(Arrays.asList(new ObjectSaveData().withName(contigId)
						.withType("KBaseGenomes.ContigSet").withData(new UObject(contigSet)))));
		genome.withContigIds(new ArrayList<String>(contigMap.keySet())).withContigLengths(contigLengths)
				.withDnaSize(dnaLen).withContigsetRef(ws + "/" + contigId).withFeatures(features)
				.withGcContent(TaskHolder.calculateGcContent(contigSet));
		Map<String, String> meta = new LinkedHashMap<String, String>();
		meta.put("Scientific name", genome.getScientificName());
		wc.saveObjects(new SaveObjectsParams().withWorkspace(ws)
				.withObjects(Arrays.asList(new ObjectSaveData().withName(id).withMeta(meta)
						.withType("KBaseGenomes.Genome").withData(new UObject(genome)))));
	}
	
	private static void check(Object obj, String param) throws ServletException {
		if (obj == null)
			throw new ServletException("Parameter " + param + " wasn't defined");
	}
	
	public static void main(String[] args) throws Exception {
		int port = 18888;
		if (args.length == 1)
			port = Integer.parseInt(args[0]);
		Server jettyServer = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		jettyServer.setHandler(context);
		context.addServlet(new ServletHolder(new ContigSetUploadServlet()),"/uploader");
		jettyServer.start();
		jettyServer.join();
	}
}
