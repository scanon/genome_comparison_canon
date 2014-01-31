package us.kbase.genomecomparison;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

import us.kbase.common.service.UObject;
import us.kbase.kbasegenomes.Contig;
import us.kbase.kbasegenomes.ContigSet;
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
		DiskFileItemFactory factory = new DiskFileItemFactory(maxMemoryFileSize, getTempDir());
		ServletFileUpload upload = new ServletFileUpload(factory);
		FileItem file = null;
		try {
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
			}
			response.getOutputStream().write("Contig Set was successfuly uploaded".getBytes());
		} catch (Exception ex) {
			/*PrintWriter pw = new PrintWriter(new FileWriter(new File("log.txt"), true));
			ex.printStackTrace(pw);
			pw.close();*/
			ex.printStackTrace(new PrintStream(response.getOutputStream()));
			/*if (ex instanceof ServletException)
				throw (ServletException)ex;
			if (ex instanceof IOException)
				throw (IOException)ex;
			throw new ServletException(ex);*/
		} finally {
			if (file != null)
				file.delete();
		}
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
