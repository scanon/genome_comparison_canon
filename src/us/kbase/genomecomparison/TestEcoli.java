package us.kbase.genomecomparison;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.UObject;
import us.kbase.genomecomparison.NcbiGenomeLoader.Feature;
import us.kbase.workspaceservice.GetJobsParams;
import us.kbase.workspaceservice.GetObjectOutput;
import us.kbase.workspaceservice.GetObjectParams;
import us.kbase.workspaceservice.ObjectData;
import us.kbase.workspaceservice.SaveObjectParams;

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

	public static void main(String[] args) throws Exception {
		//runBlast(0, 6);
		createImage("proteome_cmp_0_1");
		//uploadGenome(genomeNames[6]);
	}
	
	private static String getAuthToken() throws Exception {
		return AuthService.login("nardevuser1", "nardevuser2").getToken().toString();
	}
	
	@SuppressWarnings("unchecked")
	public static void runBlast(int genome1, int genome2) throws Exception {
		String outId = "proteome_cmp_" + genome1 + "_" + genome2;
		String token = getAuthToken();
		//TaskHolder th = new TaskHolder(1, dir, new File("blast/macosx"));
		GenomeComparisonClient cl = new GenomeComparisonClient(new URL("http://140.221.85.98:8283/jsonrpc"), new AuthToken(token));
		cl.setAuthAllowedForHttp(true);
		String jobId = cl.blastProteomes(new BlastProteomesParams()
				.withGenome1ws(ws).withGenome1id(genomeNames[genome1])
				.withGenome2ws(ws).withGenome2id(genomeNames[genome2])
				.withOutputWs(ws).withOutputId(outId));
		long time = System.currentTimeMillis();
		while (true) {
			Map<String, Object> jobMap = (Map<String, Object>)TaskHolder.createWsClient(token).getJobs(
					new GetJobsParams().withAuth(token).withJobids(Arrays.asList(jobId))).get(0);
			String status = "" + jobMap.get("status");
			System.out.println("Task status: " + status);
			if (status.equals("done")) {
				Map<String, String> jobData = (Map<String, String>)jobMap.get("jobdata");
				String error = jobData.get("error");
				if (error != null) {
					System.out.println("Error: " + error);
				} else {
					createImage(outId);
				}
				break;
			}
			Thread.sleep(5000);
		}
		System.out.println("Time: " + (System.currentTimeMillis() - time) + " ms.");
		//th.stopAllThreads();
	}

	private static void createImage(String cmpId) throws Exception {
		String token = getAuthToken();
		GetObjectOutput out = TaskHolder.createWsClient(token).getObject(
				new GetObjectParams().withWorkspace(ws)
				.withType("ProteomeComparison").withId(cmpId));
		ProteomeComparison cmp = UObject.transformObjectToObject(out.getData(), ProteomeComparison.class);
		new ObjectMapper().writeValue(new File(dir, cmpId + ".json"), cmp);
		int w = cmp.getProteome1names().size() * 25 / 100;
		int h = cmp.getProteome2names().size() * 25 / 100;
		ComparisonImage.saveImage(cmp, 0, 0, w, h, 25, new File(dir, cmpId + ".png"));
	}
	
	private static void uploadGenome(String genomeName) throws Exception {
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
	}
}
