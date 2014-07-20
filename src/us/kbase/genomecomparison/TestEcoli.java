package us.kbase.genomecomparison;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;

import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.Tuple7;
import us.kbase.kbasegenomes.Genome;
import us.kbase.userandjobstate.UserAndJobStateClient;
import us.kbase.workspace.GetModuleInfoParams;
import us.kbase.workspace.ObjectData;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.RegisterTypespecParams;
import us.kbase.workspace.WorkspaceClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestEcoli {
	private static final File dir = new File("temp");
	private static final String ws = "nardevuser1_home";
	private static final String[] genomeNames = {
		"Escherichia_coli_042",
		"Escherichia_coli_Xuzhou21",
		"Escherichia_coli_P12b",
		"Escherichia_coli_ETEC_H10407",
		"Escherichia_coli_BW2952",
		"Escherichia_coli_O127_H6_E2348_69",
		"Escherichia_coli_UM146"
	};
	//private static final String srvUrl = "http://140.221.85.98:7123/jsonrpc";
	private static final String srvUrl = "http://localhost:7123";
	
	public static void main(String[] args) throws Exception {
		//runBlast("Shewanella_ANA_3_uid58347", "Shewanella_baltica_BA175_uid52601");
		//createImage("proteome_cmp_0_2");
		//uploadGenome(genomeNames[6]);
		uploadSpec();
		//annotate();
		/*ContigSetUploadServlet.uploadGbk(new FileInputStream(new File("test/NC_008577.gbk")), 
				"nardevuser1:home", "Shewanella_ANA_3.genome", getAuthToken());*/
	}
	
	private static String getAuthToken() throws Exception {
		return AuthService.login("nardevuser1", "*****").getToken().toString();
	}
	
	public static void runBlast(String genome1name, String genome2name) throws Exception {
		String outId = genome1name + "_vs_" + genome2name + ".protcmp";
		String token = getAuthToken();
		GenomeComparisonClient cl = new GenomeComparisonClient(new URL(srvUrl), new AuthToken(token));
		cl.setAuthAllowedForHttp(true);
		String jobId = cl.blastProteomes(new BlastProteomesParams()
				.withGenome1ws(ws).withGenome1id(genome1name + ".genome")
				.withGenome2ws(ws).withGenome2id(genome2name + ".genome")
				.withOutputWs(ws).withOutputId(outId));
		long time = System.currentTimeMillis();
		while (true) {
			UserAndJobStateClient ujs = GenomeCmpConfig.createJobClient(token);
			Tuple7<String, String, String, Long, String, Long, Long> data = ujs.getJobStatus(jobId);
			String status = data.getE3();
    		Long complete = data.getE6();
    		Long wasError = data.getE7();
			System.out.println("Task status: " + status);
			if (complete == 1L) {
				if (wasError == 0L)
					createImage(outId);
				break;
			}
			Thread.sleep(5000);
		}
		System.out.println("Time: " + (System.currentTimeMillis() - time) + " ms.");
	}

	private static void annotate() throws Exception {
		String token = getAuthToken();
		String ws = "nardevuser1:home";
		String genomeId = "Shewanella_ANA_3.genome";
		long time = System.currentTimeMillis();
		try {
			GenomeCmpConfig cfg = new GenomeCmpConfig(1, new File("tmp"), null, null, null);
			AnnotateGenome.run(token, 
					new AnnotateGenomeParams().withInGenomeWs(ws).withInGenomeId(genomeId)
					.withOutGenomeWs(ws).withOutGenomeId(genomeId).withSeedAnnotationOnly(1L), cfg);
			ObjectData genomeData = GenomeCmpConfig.createWsClient(token).getObjects(Arrays.asList(
					new ObjectIdentity().withRef(ws + "/" + genomeId))).get(0);
			Genome genome = genomeData.getData().asClassInstance(Genome.class);
			System.out.println(genome.getFeatures().subList(0, 300));
		} finally {
			System.out.println("Time: " + (System.currentTimeMillis() - time) + " ms.");
		}
	}
	
	private static void createImage(String cmpId) throws Exception {
		String token = getAuthToken();
		ProteomeComparison cmp = ComparisonImage.loadCmpObject(ws, cmpId, token);
		new ObjectMapper().writeValue(new File(dir, cmpId + ".json"), cmp);
		int w = cmp.getProteome1names().size() * 25 / 100;
		int h = cmp.getProteome2names().size() * 25 / 100;
		ComparisonImage.saveImage(cmp, 0, 0, w, h, 25, new File(dir, cmpId + ".png"));
	}
	
	/*private static void uploadGenome(String genomeName) throws Exception {
		File workDir = new File("data"); 
		List<Feature> proteins = NcbiGenomeLoader.loadFeatures(workDir, genomeName);
		Map<String, Object> genome = new LinkedHashMap<String, Object>();
		genome.put("id", genomeName);
		genome.put("scientific_name", genomeName);
		genome.put("source", "NCBI");
		genome.put("domain", "Bacteria");
		genome.put("source_id", "NCBI");
		genome.put("genetic_code", 11);
		List<Object> features = new ArrayList<Object>();
		genome.put("features", features);
		for (Feature prot : proteins) {
			Map<String, Object> feature = new LinkedHashMap<String, Object>();
			feature.put("protein_translation", prot.translation);
			List<?> location = Arrays.asList(Arrays.asList(prot.contigName, prot.getRealStart(), prot.getDirChar(), prot.getLength()));
			feature.put("location", location);
			feature.put("function", prot.description);
			feature.put("aliases", Collections.EMPTY_LIST);
			feature.put("id", prot.protName);
			feature.put("type", "CDS");
			feature.put("annotation", Collections.EMPTY_LIST);
			features.add(feature);
		}
		String token = getAuthToken();
		//TaskHolder.createWsClient(token).addType(new AddTypeParams().withAuth(token).withType("ProteomeComparison"));
		ObjectData savedData = new ObjectData();
		savedData.getAdditionalProperties().putAll(genome);
		TaskHolder.createWsClient(token).saveObject(new SaveObjectParams().withAuth(token)
				.withWorkspace(ws).withType("Genome").withId(genomeName)
				.withData(savedData));
	}*/
	
	private static void uploadSpec() throws Exception {
		String token = AuthService.login("rsutormin", "").getToken().toString();  //getAuthToken();
		String wsUrl = "https://kbase.us/services/ws/";  //"http://dev04.berkeley.kbase.us:7058";
		WorkspaceClient wc = GenomeCmpConfig.createWsClient(token, wsUrl);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(new File("GenomeComparison.spec")));
		while (true) {
			String l = br.readLine();
			if (l == null)
				break;
			sb.append(l).append("\n");
		}
		br.close();
		String spec = sb.toString();
		wc.registerTypespec(new RegisterTypespecParams().withSpec(spec).withNewTypes(
				Arrays.<String>asList("ProteomeComparison")).withDryrun(0L));
		wc.releaseModule("GenomeComparison");
		System.out.println(wc.getModuleInfo(new GetModuleInfoParams().withMod("GenomeComparison")));
	}
}
