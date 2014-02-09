package us.kbase.genomecomparison;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

public class Stat {
	private static int queuedTasks = 0;
	private static int runningTasks = 0;
	private static int uploaders = 0;
	
	public static synchronized void addQueued(File dir) {
		queuedTasks++;
		flush(dir);
	}
	
	public static synchronized void queuedToRunning(File dir) {
		queuedTasks--;
		runningTasks++;
		flush(dir);
	}

	public static synchronized void endRunning(File dir) {
		runningTasks--;
		flush(dir);
	}

	public static synchronized void addUploader(File dir) {
		uploaders++;
		flush(dir);
	}

	public static synchronized void delUploader(File dir) {
		uploaders--;
		flush(dir);
	}

	private static synchronized void flush(File dir) {
		long max = Runtime.getRuntime().maxMemory();
		long tot = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		print(dir, "queued=" + queuedTasks + ", running=" + runningTasks + ", " +
					"uploaders=" + uploaders + ", mem(max/total/free)=" + max + "/" + tot + "/" + free);
	}
	
	private static synchronized void print(File dir, String text) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(new File(dir, "stat.log"), true));
			pw.println("" + new Date() + ": " + text);
			pw.close();
		} catch (Throwable t) {}
	}
}
