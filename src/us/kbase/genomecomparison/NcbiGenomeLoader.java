package us.kbase.genomecomparison;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class NcbiGenomeLoader {
	private static final Pattern tabDiv = Pattern.compile(Pattern.quote("\t"));
	private static final Pattern semiDiv = Pattern.compile(Pattern.quote(";"));
	private static final Pattern pipeDiv = Pattern.compile(Pattern.quote("|"));
	
	public static Map<String, String> loadProteome(File workDir, String genomeName) throws Exception {
		Map<String, String> ret = new LinkedHashMap<String, String>();
		for (Feature f : loadFeatures(workDir, genomeName))
			ret.put(f.protName, f.translation);
		return ret;
	}
	
	public static List<Feature> loadFeatures(File workDir, String genomeName) throws Exception {
		File dir = new File(workDir, genomeName);
		if (!dir.exists())
			throw new IllegalStateException("Directory doesn't exist: " + dir.getAbsolutePath());
		File geneFile = new File(dir, "genes.gff");
		if (!geneFile.exists())
			throw new IllegalStateException("File doesn't exist: " + geneFile.getAbsolutePath());
		File protFile = new File(dir, "proteins.faa");
		if (!protFile.exists())
			throw new IllegalStateException("File doesn't exist: " + protFile.getAbsolutePath());
		Map<String, String> prot2seq = FastaReader.readFromFile(protFile);
		Map<String, String> map2 = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : prot2seq.entrySet()) {
			String protName = entry.getKey();
			protName = pipeDiv.split(protName)[3];
			map2.put(protName, entry.getValue());
		}
		prot2seq = map2;
		BufferedReader geneBr = new BufferedReader(new FileReader(geneFile));
		List<Feature> proteins = new ArrayList<Feature>();
		try {
			while (true) {
				String l = geneBr.readLine();
				if (l == null)
					break;
				l = l.trim();
				if (l.isEmpty() || l.startsWith("#"))
					continue;
				String[] parts = tabDiv.split(l);
				if (!parts[2].equals("CDS"))
					continue;
				Feature f = new Feature();
				f.contigName = parts[0];
				f.start = Integer.parseInt(parts[3]);
				f.stop = Integer.parseInt(parts[4]);
				f.dir = Integer.parseInt(parts[6] + "1");
				String[] parts2 = semiDiv.split(parts[8]);
				for (String p2 : parts2) {
					if (p2.startsWith("Name=")) {
						f.protName = p2.substring(5);
					} else if (p2.startsWith("Note=")) {
						f.description = p2.substring(5);
					}
				}
				String protSeq = prot2seq.get(f.protName);
				if (protSeq == null)
					throw new IllegalStateException("No protein sequence for " + f.protName);
				f.translation = protSeq;
				proteins.add(f);
			}
		} finally {
			geneBr.close();
		}
		Collections.sort(proteins, new Comparator<Feature>() {
			@Override
			public int compare(Feature o1, Feature o2) {
				int ret = Utils.compare(o1.start, o2.start);
				if (ret == 0)
					ret = Utils.compare(o1.stop, o2.stop);
				return ret;
			}
		});
		return proteins;
	}
	
	public static class Feature {
		public String protName;
		public String contigName;
		public int start;
		public int stop;
		public int dir;
		public String description;
		public String translation;
		
		public int getRealStart() {
			return dir > 0 ? start : stop;
		}
		
		public int getLength() {
			return stop + 1 - start;
		}
		
		public String getDirChar() {
			return dir > 0 ? "+" : "-";
		}
	}
}
