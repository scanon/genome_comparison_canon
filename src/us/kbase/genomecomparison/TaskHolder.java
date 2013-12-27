package us.kbase.genomecomparison;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import us.kbase.common.service.Tuple3;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TaskHolder {
	private Map<String, Task> taskMap = new HashMap<String, Task>();
	private LinkedList<Task> taskQueue = new LinkedList<Task>();
	private Thread[] allThreads;
	private boolean needToStop = false;
	private final Object idleMonitor = new Object();
	private final File tempDir;
	private final File blastBin;
	
	public TaskHolder(int threadCount, File tempDir, File blastBin) {
		this.tempDir = tempDir;
		this.blastBin = blastBin;
		allThreads = new Thread[threadCount];
		for (int i = 0; i < allThreads.length; i++) {
			startNewThread(i);
		}
	}
	
	public synchronized String addTask(BlastProteomesParams params, String authToken) {
		String jobId = createTaskJob(authToken);
		Task task = new Task(jobId, params, authToken);
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
			changeTaskState(task, "running", token, null);
			Map<String, String> proteome1 = extractProteome(task.getParams().getGenome1ws(), 
					task.getParams().getGenome1id(), token);
			Map<String, String> proteome2 = extractProteome(task.getParams().getGenome2ws(), 
					task.getParams().getGenome2id(), token);
			final Map<String, List<InnerHit>> data1 = new LinkedHashMap<String, List<InnerHit>>();
		    final Map<String, List<InnerHit>> data2 = new LinkedHashMap<String, List<InnerHit>>();
			String maxEvalue = task.getParams().getMaxEvalue() == null ? "1e-10" : task.getParams().getMaxEvalue();
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
							ret = Long.compare(o2.getPercentOfBestScore(), o1.getPercentOfBestScore());
						}
					}
					return ret;
				}
			};
			Double subBbhPercentParam = task.getParams().getSubBbhPercent();
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
				.withGenome1ws(task.getParams().getGenome1ws())
				.withGenome1id(task.getParams().getGenome1id())
				.withGenome2ws(task.getParams().getGenome2ws())
				.withGenome2id(task.getParams().getGenome2id())
				.withProteome1names(prot1names).withProteome1map(prot1map)
				.withProteome2names(prot2names).withProteome2map(prot2map)
				.withData1(data1new).withData2(data2new);
			saveResult(task.getParams().getOutputWs(), task.getParams().getOutputId(), token, res);
			changeTaskState(task, "done", token, null);
		}catch(Throwable e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.close();
			try {
				changeTaskState(task, "error", token, sw.toString());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
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

	private String createTaskJob(String token) {
		//throw new IllegalStateException("Method is not supported yet");
		System.out.println("Task was created");
		return "job0001";
	}
	
	private void changeTaskState(Task task, String state, String token, String errorMessage) {
		if ((!state.equals("running")) && (!state.equals("done")) && (!state.equals("error")))
			throw new IllegalStateException("Unknown job state: " + state);
		//throw new IllegalStateException("Method is not supported yet");
		System.out.println("Task state was changed: " + state);
		if (errorMessage != null)
		System.out.println(errorMessage);
	}
	
	private Map<String, String> extractProteome(String ws, String genomeId, String token) {
		//throw new IllegalStateException("Method is not supported yet");
		return FastaReader.readFromFile(new File("/Users/rsutormin/Work/2013-12-16_genome_cmp/proteome.fa"));
	}
	
	private void saveResult(String ws, String id, String token, ProteomeComparison res) throws Exception {
		//throw new IllegalStateException("Method is not supported yet");
		new ObjectMapper().writeValue(new File("/Users/rsutormin/Work/2013-12-16_genome_cmp/cmp.json"), res);
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
						System.out.println("RNA thread " + (num + 1) + " was stoped");
					}
				},"RNA thread " + (num + 1));
		ret.start();
		return ret;
	}
	
	public static void main(String[] args) {
		TaskHolder th = new TaskHolder(1, new File("/Users/rsutormin/Work/2013-12-16_genome_cmp/"), 
				new File("/Users/rsutormin/Work/2013-12-16_genome_cmp/blast"));
		th.addTask(new BlastProteomesParams().withGenome1ws("").withGenome1id("")
				.withGenome2ws("").withGenome2id("").withOutputWs("").withOutputId(""), "");
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
}