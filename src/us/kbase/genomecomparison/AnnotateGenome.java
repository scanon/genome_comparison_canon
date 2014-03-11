package us.kbase.genomecomparison;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import us.kbase.common.service.UObject;
import us.kbase.genomeannotation.GenomeAnnotationClient;
import us.kbase.genomeannotation.GenomeTO;
import us.kbase.kbasegenomes.Contig;
import us.kbase.kbasegenomes.ContigSet;
import us.kbase.kbasegenomes.Feature;
import us.kbase.kbasegenomes.Genome;
import us.kbase.workspace.ObjectData;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.ObjectSaveData;
import us.kbase.workspace.SaveObjectsParams;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnnotateGenome {
    private static final String gaUrl = "https://kbase.us/services/genome_annotation";

	public static void run(String token, AnnotateGenomeParams params, GenomeCmpConfig cfg) throws Exception {
		ObjectData genomeData = cfg.getObjectStorage().getObjects(token, Arrays.asList(
				new ObjectIdentity().withRef(params.getInGenomeWs() + "/" + params.getInGenomeId()))).get(0);
		Genome genome = genomeData.getData().asClassInstance(Genome.class);
		String contigSetRef = genome.getContigsetRef();
		UObject contigSetObj = cfg.getObjectStorage().getObjects(token, Arrays.asList(
				new ObjectIdentity().withRef(contigSetRef))).get(0).getData();
		ContigSet contigSet = contigSetObj.asClassInstance(ContigSet.class);
		List<us.kbase.genomeannotation.Contig> gtoContigs = new ArrayList<us.kbase.genomeannotation.Contig>();
		for (us.kbase.kbasegenomes.Contig origContig : contigSet.getContigs()) {
			gtoContigs.add(new us.kbase.genomeannotation.Contig().withId(origContig.getId()).withDna(origContig.getSequence()));
		}
		List<us.kbase.genomeannotation.Feature> featuresTO;
		if (params.getSeedAnnotationOnly() == null || params.getSeedAnnotationOnly() == 0) {
			featuresTO = Collections.<us.kbase.genomeannotation.Feature>emptyList();
		} else {
			featuresTO = UObject.transformObjectToObject(genome.getFeatures(), 
					new TypeReference<List<us.kbase.genomeannotation.Feature>>() {});
		}
		GenomeTO gto = new GenomeTO().withContigs(gtoContigs).withDomain(genome.getDomain())
				.withFeatures(featuresTO).withGeneticCode(genome.getGeneticCode())
				.withId(genome.getId()).withScientificName(genome.getScientificName())
				.withSource(genome.getSource()).withSourceId(genome.getSourceId());
		new ObjectMapper().writeValue(new File("GenomeTO.json"), gto);
		GenomeAnnotationClient gc = new GenomeAnnotationClient(new URL(gaUrl));
		if (params.getSeedAnnotationOnly() == null || params.getSeedAnnotationOnly() == 0) {
			gto = gc.annotateGenome(gto);
		} else {
			gto = gc.annotateProteins(gto);
		}
		List<Feature> featuresToSave = UObject.transformObjectToObject(gto.getFeatures(), new TypeReference<List<Feature>>() {});
		genome.setFeatures(featuresToSave);
		if (genome.getGcContent() == null)
			genome.setGcContent(calculateGcContent(contigSet));
		Map<String, String> meta = genomeData.getInfo().getE11();
		if (meta == null)
			meta = new LinkedHashMap<String, String>();
		if (!meta.containsKey("Scientific name")) {
			meta.put("Scientific name", genome.getScientificName());
		}
		ObjectSaveData data = new ObjectSaveData().withData(new UObject(genome)).withType("KBaseGenomes.Genome").withMeta(meta);
		try {
			long objid = Long.parseLong(params.getOutGenomeId());
			data.withObjid(objid);
		} catch (NumberFormatException ex) {
			data.withName(params.getOutGenomeId());
		}
		cfg.getObjectStorage().saveObjects(token, new SaveObjectsParams().withWorkspace(params.getOutGenomeWs()).withObjects(
				Arrays.asList(data)));
	}
	
	public static double calculateGcContent(ContigSet contigs) {
		int at = 0;
		int gc = 0;
		for (Contig contig : contigs.getContigs()) {
			String seq = contig.getSequence();
			for (int i = 0; i < seq.length(); i++) {
				char ch = seq.charAt(i);
				if (ch == 'g' || ch == 'G' || ch == 'c' || ch == 'C') {
					gc++;
				} else if (ch == 'a' || ch == 'A' || ch == 't' || ch == 'T') {
					at++;
				}
			}
		}
		return (0.0 + gc) / (at + gc);
	}
}
