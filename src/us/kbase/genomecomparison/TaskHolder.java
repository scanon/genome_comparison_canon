package us.kbase.genomecomparison;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.kbase.auth.AuthToken;
import us.kbase.common.service.Tuple3;
import us.kbase.common.service.Tuple4;
import us.kbase.common.service.UObject;
import us.kbase.genomeannotation.GenomeAnnotationClient;
import us.kbase.genomeannotation.GenomeTO;
import us.kbase.kbasegenomes.ContigSet;
import us.kbase.kbasegenomes.Feature;
import us.kbase.kbasegenomes.Genome;
import us.kbase.userandjobstate.InitProgress;
import us.kbase.userandjobstate.Results;
import us.kbase.userandjobstate.UserAndJobStateClient;
import us.kbase.workspace.ObjectData;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.ObjectSaveData;
import us.kbase.workspace.SaveObjectsParams;
import us.kbase.workspace.WorkspaceClient;

public class TaskHolder {
	private Map<String, Task> taskMap = new HashMap<String, Task>();
	private LinkedList<Task> taskQueue = new LinkedList<Task>();
	private Thread[] allThreads;
	private boolean needToStop = false;
	private final Object idleMonitor = new Object();
	private final File tempDir;
	private final File blastBin;
	
	//private static final String wsUrl = "https://kbase.us/services/workspace/";
	private static final String wsUrl = "http://140.221.84.209:7058/";
    //private static final String jobSrvUrl = "http://140.221.84.180:7083";
    private static final String jobSrvUrl = "https://kbase.us/services/userandjobstate/";
    private static final String gaUrl = "https://kbase.us/services/genome_annotation";
    
    private static final int MAX_ERROR_MESSAGE_LEN = 190;
	
	public TaskHolder(int threadCount, File tempDir, File blastBin) {
		this.tempDir = tempDir;
		this.blastBin = blastBin;
		allThreads = new Thread[threadCount];
		for (int i = 0; i < allThreads.length; i++) {
			allThreads[i] = startNewThread(i);
		}
	}

	public String addTask(BlastProteomesParams params, String authToken) throws Exception {
		String outRef = params.getOutputWs() + "/" + params.getOutputId();
		return addTask(params, authToken, "Blast proteomes of two genomes", outRef);
	}

	public String addTask(AnnotateGenomeParams params, String authToken) throws Exception {
		String outRef = params.getOutGenomeWs() + "/" + params.getOutGenomeId();
		return addTask(params, authToken, "Blast proteomes of two genomes", outRef);
	}

	private synchronized String addTask(Object params, String authToken, String description, String outRef) throws Exception {
		String jobId = createQueuedTaskJob(description, authToken);
		Task task = new Task(jobId, params, authToken, outRef);
		taskQueue.addLast(task);
		taskMap.put(task.getJobId(), task);
		synchronized (idleMonitor) {
			idleMonitor.notify();
		}
		return jobId;
	}
	
	private synchronized void removeTask(Task task) {
		taskMap.remove(task.getJobId());
	}
	
	public synchronized Task getTask(String jobId) {
		return taskMap.get(jobId);
	}
	
	private synchronized Task gainNewTask() {
		if (taskQueue.size() > 0) {
			Task ret = taskQueue.removeFirst();
			return ret;
		}
		return null;
	}

	private void runTask(Task task) {
		String token = task.getAuthToken();
		try {
			changeTaskStateIntoRunning(task, token);
			Object params = task.getParams();
			if (params instanceof BlastProteomesParams) {
				runBlastProteomes(token, (BlastProteomesParams)params);
			} else {
				runAnnotateGenome(token, (AnnotateGenomeParams)params);
			}
			completeTaskState(task, token, null, null);
		} catch (Throwable e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.close();
			try {
				String errMsg = null;
				if (e.getMessage() == null) {
					errMsg = e.getClass().getSimpleName();
				} else {
					errMsg = "Error: " + e.getMessage();
				}
				if (errMsg.length() > MAX_ERROR_MESSAGE_LEN)
					errMsg = errMsg.substring(0, MAX_ERROR_MESSAGE_LEN - 3) + "...";
				completeTaskState(task, token, errMsg, sw.toString());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void runBlastProteomes(String token,
			BlastProteomesParams params) throws Exception {
		List<InnerFeature> features1 = extractProteome(params.getGenome1ws(), 
				params.getGenome1id(), token);
		Map<String, String> proteome1 = featuresToProtMap(features1);
		List<InnerFeature> features2 = extractProteome(params.getGenome2ws(), 
				params.getGenome2id(), token);
		Map<String, String> proteome2 = featuresToProtMap(features2);
		final Map<String, List<InnerHit>> data1 = new LinkedHashMap<String, List<InnerHit>>();
		final Map<String, List<InnerHit>> data2 = new LinkedHashMap<String, List<InnerHit>>();
		String maxEvalue = params.getMaxEvalue() == null ? "1e-10" : params.getMaxEvalue();
		BlastStarter.run(tempDir, proteome1, proteome2, blastBin, maxEvalue, new BlastStarter.ResultCallback() {
			@Override
			public void proteinPair(String name1, String name2, double ident,
					int alnLen, int mismatch, int gapopens, int qstart, int qend,
					int tstart, int tend, String eval, double bitScore) {
				InnerHit h = new InnerHit().withId1(name1).withId2(name2).withScore(bitScore);
				List<InnerHit> l1 = data1.get(name1);
				if (l1 == null) {
					l1 = new ArrayList<InnerHit>();
					data1.put(name1, l1);
				}
				l1.add(h);
				List<InnerHit> l2 = data2.get(name2);
				if (l2 == null) {
					l2 = new ArrayList<InnerHit>();
					data2.put(name2, l2);
				}
				l2.add(h);
			}
		});
		Comparator<InnerHit> hcmp = new Comparator<InnerHit>() {
			@Override
			public int compare(InnerHit o1, InnerHit o2) {
				int ret = Double.compare(o2.getScore(), o1.getScore());
				if (ret == 0) {
					if (o1.getPercentOfBestScore() != null && o2.getPercentOfBestScore() != null) {
						ret = Utils.compare(o2.getPercentOfBestScore(), o1.getPercentOfBestScore());
					}
				}
				return ret;
			}
		};
		Double subBbhPercentParam = params.getSubBbhPercent();
		double subBbhPercent = subBbhPercentParam == null ? 90 : subBbhPercentParam;
		for (Map.Entry<String, List<InnerHit>> entry : data1.entrySet()) 
			Collections.sort(entry.getValue(), hcmp);
		for (Map.Entry<String, List<InnerHit>> entry : data2.entrySet()) 
			Collections.sort(entry.getValue(), hcmp);
		for (Map.Entry<String, List<InnerHit>> entry : data1.entrySet()) {
			List<InnerHit> l = entry.getValue();
			double best1 = l.get(0).getScore();
			for (InnerHit h : l) {
				double best2 = getBestScore(h.getId2(), data2);
				h.setPercentOfBestScore(Math.round(h.getScore() * 100.0 / Math.max(best1, best2) + 1e-6));
			}
			for (int pos = l.size() - 1; pos > 0; pos--) 
				if (l.get(pos).getPercentOfBestScore() < subBbhPercent)
					l.remove(pos);
			Collections.sort(entry.getValue(), hcmp);
		}
		for (Map.Entry<String, List<InnerHit>> entry : data2.entrySet()) {
			List<InnerHit> l = entry.getValue();
			double best2 = l.get(0).getScore();
			for (InnerHit h : l) {
				double best1 = getBestScore(h.getId1(), data1);
				h.setPercentOfBestScore(Math.round(h.getScore() * 100.0 / Math.max(best1, best2) + 1e-6));
			}
			for (int pos = l.size() - 1; pos > 0; pos--) 
				if (l.get(pos).getPercentOfBestScore() < subBbhPercent)
					l.remove(pos);
			Collections.sort(entry.getValue(), hcmp);
		}
		List<String> prot1names = new ArrayList<String>();
		Map<String, Long> prot1map = new HashMap<String, Long>();
		linkedMapToPos(proteome1, prot1names, prot1map);
		List<String> prot2names = new ArrayList<String>();
		Map<String, Long> prot2map = new HashMap<String, Long>();
		linkedMapToPos(proteome2, prot2names, prot2map);
		List<List<Tuple3<Long, Long, Long>>> data1new = new ArrayList<List<Tuple3<Long, Long, Long>>>();
		for (String prot1name : prot1names) {
			List<Tuple3<Long, Long, Long>> hits = new ArrayList<Tuple3<Long, Long, Long>>();
			data1new.add(hits);
			List<InnerHit> ihits = data1.get(prot1name);
			if (ihits == null)
				continue;
			for (InnerHit ih : ihits) {
				Tuple3<Long, Long, Long> h = new Tuple3<Long, Long, Long>()
						.withE1(prot2map.get(ih.getId2())).withE2(Math.round(ih.getScore() * 100))
						.withE3(ih.getPercentOfBestScore());
				hits.add(h);
			}
		}
		List<List<Tuple3<Long, Long, Long>>> data2new = new ArrayList<List<Tuple3<Long, Long, Long>>>();
		for (String prot2name : prot2names) {
			List<Tuple3<Long, Long, Long>> hits = new ArrayList<Tuple3<Long, Long, Long>>();
			data2new.add(hits);
			List<InnerHit> ihits = data2.get(prot2name);
			if (ihits == null)
				continue;
			for (InnerHit ih : ihits) {
				Tuple3<Long, Long, Long> h = new Tuple3<Long, Long, Long>()
						.withE1(prot1map.get(ih.getId1())).withE2(Math.round(ih.getScore() * 100))
						.withE3(ih.getPercentOfBestScore());
				hits.add(h);
			}
		}
		ProteomeComparison res = new ProteomeComparison()
			.withSubBbhPercent(subBbhPercent)
			.withMaxEvalue(maxEvalue)
			.withGenome1ws(params.getGenome1ws())
			.withGenome1id(params.getGenome1id())
			.withGenome2ws(params.getGenome2ws())
			.withGenome2id(params.getGenome2id())
			.withProteome1names(prot1names)
			.withProteome1map(prot1map)
			.withProteome2names(prot2names)
			.withProteome2map(prot2map)
			.withData1(data1new)
			.withData2(data2new);
		saveResult(params.getOutputWs(), params.getOutputId(), token, res);
	}
	
	public void runAnnotateGenome(String token,
			AnnotateGenomeParams params) throws Exception {
		ObjectData genomeData = createWsClient(token).getObjects(Arrays.asList(
				new ObjectIdentity().withRef(params.getInGenomeWs() + "/" + params.getInGenomeId()))).get(0);
		Genome genome = genomeData.getData().asClassInstance(Genome.class);
		String contigSetRef = genome.getContigsetRef();
		UObject contigSetObj = createWsClient(token).getObjects(Arrays.asList(
				new ObjectIdentity().withRef(contigSetRef))).get(0).getData();
		ContigSet contigSet = contigSetObj.asClassInstance(ContigSet.class);
		List<us.kbase.genomeannotation.Contig> gtoContigs = new ArrayList<us.kbase.genomeannotation.Contig>();
		for (us.kbase.kbasegenomes.Contig origContig : contigSet.getContigs()) {
			gtoContigs.add(new us.kbase.genomeannotation.Contig().withId(origContig.getId()).withDna(origContig.getSequence()));
		}
		GenomeTO gto = new GenomeTO().withContigs(gtoContigs).withDomain(genome.getDomain())
				.withFeatures(Collections.<us.kbase.genomeannotation.Feature>emptyList())
				.withGeneticCode(genome.getGeneticCode()).withId(genome.getId())
				.withScientificName(genome.getScientificName()).withSource(genome.getSource())
				.withSourceId(genome.getSourceId());
		new ObjectMapper().writeValue(new File("GenomeTO.json"), gto);
		GenomeAnnotationClient gc = new GenomeAnnotationClient(new URL(gaUrl));
		if (params.getSeedAnnotationOnly() == null || params.getSeedAnnotationOnly() == 0) {
			gto = gc.annotateGenome(gto);
		} else {
			gto = gc.annotateProteins(gto);
		}
		List<Feature> featuresToSave = UObject.transformObjectToObject(gto.getFeatures(), new TypeReference<List<Feature>>() {});
		genome.setFeatures(featuresToSave);
		ObjectSaveData data = new ObjectSaveData().withData(new UObject(genome)).withType("KBaseGenomes.Genome");
		try {
			long objid = Long.parseLong(params.getOutGenomeId());
			data.withObjid(objid);
		} catch (NumberFormatException ex) {
			data.withName(params.getOutGenomeId());
		}
		createWsClient(token).saveObjects(new SaveObjectsParams().withWorkspace(params.getOutGenomeWs()).withObjects(
				Arrays.asList(data)));
	}
	
	private static Map<String, String> featuresToProtMap(List<InnerFeature> features) {
		Map<String, String> ret = new LinkedHashMap<String, String>();
		for (InnerFeature inf : features)
			ret.put(inf.protName, inf.seq);
		return ret;
	}
	
	private static void linkedMapToPos(Map<String, String> linked, List<String> arr, 
			Map<String, Long> posMap) {
		for (String name: linked.keySet()) {
			long pos = arr.size();
			arr.add(name);
			posMap.put(name, pos);
		}
	}
	
	private static double getBestScore(String name, Map<String, List<InnerHit>> data) {
		List<InnerHit> l = data.get(name);
		if (l == null || l.isEmpty())
			return 0;
		return l.get(0).getScore();
	}

	public static WorkspaceClient createWsClient(String token) throws Exception {
		WorkspaceClient ret = new WorkspaceClient(new URL(wsUrl), new AuthToken(token));
		ret.setAuthAllowedForHttp(true);
		return ret;
	}

	public static UserAndJobStateClient createJobClient(String token) throws Exception {
		UserAndJobStateClient ret = new UserAndJobStateClient(new URL(jobSrvUrl), new AuthToken(token));
		ret.setAuthAllowedForHttp(true);
		return ret;
	}

	private String createQueuedTaskJob(String description, String token) throws Exception {
		return createJobClient(token).createAndStartJob(token, "queued", description, 
				new InitProgress().withPtype("none"), null);
	}

	private void changeTaskStateIntoRunning(Task task, String token) throws Exception {
		createJobClient(token).updateJob(task.getJobId(), token, "running", null);
	}

	private void completeTaskState(Task task, String token, String errorMessage, String errorStacktrace) throws Exception {
		if (errorMessage == null) {
			createJobClient(token).completeJob(task.getJobId(), token, "done", null, 
					new Results().withWorkspaceurl(wsUrl).withWorkspaceids(
							Arrays.asList(task.getOutRef())));
		} else {
			createJobClient(token).completeJob(task.getJobId(), token, errorMessage, 
					errorStacktrace, new Results()); 
		}
	}

	private static List<InnerFeature> extractProteome(String ws, String genomeId, String token) throws Exception {
		UObject genomeObj = createWsClient(token).getObjects(
				Arrays.asList(new ObjectIdentity().withRef(ws + "/" + genomeId))).get(0).getData();
		Genome genome = genomeObj.asClassInstance(Genome.class);
		List<InnerFeature> ret = new ArrayList<InnerFeature>();
		for (Feature feature : genome.getFeatures()) {
			String type = feature.getType();
			if (!type.equals("CDS"))
				continue;
			InnerFeature inf = new InnerFeature();
			inf.protName = feature.getId();
			inf.seq = feature.getProteinTranslation();
			Tuple4<String, Long, String, Long> location = feature.getLocation().get(0);
			inf.contigName = location.getE1();
			int realStart = (int)(long)location.getE2();
			String dir = location.getE3();
			int len = (int)(long)location.getE4();
			inf.start = dir.equals("+") ? realStart : (realStart - len);
			inf.stop = dir.equals("+") ? (realStart + len) : realStart;
			ret.add(inf);
		}
		Collections.sort(ret, new Comparator<InnerFeature>() {
			@Override
			public int compare(InnerFeature o1, InnerFeature o2) {
				int ret = o1.contigName.compareTo(o2.contigName);
				if (ret == 0) {
					ret = Utils.compare(o1.start, o2.start);
					if (ret == 0)
						ret = Utils.compare(o1.stop, o2.stop);
				}
				return ret;
			}
		});
		return ret;
	}
	
	private void saveResult(String ws, String id, String token, ProteomeComparison res) throws Exception {
		ObjectSaveData data = new ObjectSaveData().withData(new UObject(res)).withType("GenomeComparison.ProteomeComparison");
		try {
			long objid = Long.parseLong(id);
			data.withObjid(objid);
		} catch (NumberFormatException ex) {
			data.withName(id);
		}
		createWsClient(token).saveObjects(new SaveObjectsParams().withWorkspace(ws).withObjects(
				Arrays.asList(data)));
	}
	
	public void stopAllThreads() {
		needToStop = true;
		for (Thread t : allThreads)
			t.interrupt();
	}
	
	private Thread startNewThread(final int num) {
		Thread ret = new Thread(
				new Runnable() {
					@Override
					public void run() {
						while (!needToStop) {
							Task task = gainNewTask();
							if (task != null) {
								runTask(task);
								removeTask(task);
							} else {
								int seconds = 55 + (int)(10 * Math.random());
								synchronized (idleMonitor) {
									try {
										idleMonitor.wait(TimeUnit.SECONDS.toMillis(seconds));
									} catch (InterruptedException e) {
										if (!needToStop)
											e.printStackTrace();
									}
								}
							}
						}
						System.out.println("Task thread " + (num + 1) + " was stoped");
					}
				},"Task thread " + (num + 1));
		ret.start();
		return ret;
	}

	private static class InnerHit {

		private String id1;
		private String id2;
		private Double score;
		private Long percentOfBestScore;

		public String getId1() {
			return id1;
		}

		public InnerHit withId1(String id1) {
			this.id1 = id1;
			return this;
		}

		public String getId2() {
			return id2;
		}

		public InnerHit withId2(String id2) {
			this.id2 = id2;
			return this;
		}

		public Double getScore() {
			return score;
		}

		public InnerHit withScore(Double score) {
			this.score = score;
			return this;
		}

		public Long getPercentOfBestScore() {
			return percentOfBestScore;
		}

		public void setPercentOfBestScore(Long percentOfBestScore) {
			this.percentOfBestScore = percentOfBestScore;
		}
	}
	
	private static class InnerFeature {
		String protName;
		String seq;
		String contigName;
		int start;
		int stop;
	}
}