package us.kbase.genomeannotation;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import us.kbase.common.service.JsonClientCaller;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.Tuple2;
import us.kbase.common.service.Tuple4;

/**
 * <p>Original spec-file module name: GenomeAnnotation</p>
 * <pre>
 * API Access to the Genome Annotation Service.
 * Provides support for gene calling, functional annotation, re-annotation. Use to extract annotation in
 * formation about an existing genome, or to create new annotations.
 * </pre>
 */
public class GenomeAnnotationClient {
    private JsonClientCaller caller;

    public GenomeAnnotationClient(URL url) {
        caller = new JsonClientCaller(url);
    }

	public void setConnectionReadTimeOut(Integer milliseconds) {
		this.caller.setConnectionReadTimeOut(milliseconds);
	}

    /**
     * <p>Original spec-file function name: genomeTO_to_reconstructionTO</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.ReconstructionTO ReconstructionTO} (original type "reconstructionTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ReconstructionTO genomeTOToReconstructionTO(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<ReconstructionTO>> retType = new TypeReference<List<ReconstructionTO>>() {};
        List<ReconstructionTO> res = caller.jsonrpcCall("GenomeAnnotation.genomeTO_to_reconstructionTO", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: genomeTO_to_feature_data</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of original type "fid_data_tuples" &rarr; list of original type "fid_data_tuple" &rarr; tuple of size 4: original type "fid", original type "md5", original type "location" (a "location" refers to a sequence of regions) &rarr; list of original type "region_of_dna" (A region of DNA is maintained as a tuple of four components: the contig the beginning position (from 1) the strand the length We often speak of "a region".  By "location", we mean a sequence of regions from the same genome (perhaps from distinct contigs).) &rarr; tuple of size 4: original type "contig_id", parameter "begin" of Long, parameter "strand" of String, parameter "length" of Long, original type "function"
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public List<Tuple4<String, String, List<Tuple4<String, Long, String, Long>>, String>> genomeTOToFeatureData(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<List<Tuple4<String, String, List<Tuple4<String, Long, String, Long>>, String>>>> retType = new TypeReference<List<List<Tuple4<String, String, List<Tuple4<String, Long, String, Long>>, String>>>>() {};
        List<List<Tuple4<String, String, List<Tuple4<String, Long, String, Long>>, String>>> res = caller.jsonrpcCall("GenomeAnnotation.genomeTO_to_feature_data", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: reconstructionTO_to_roles</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.ReconstructionTO ReconstructionTO} (original type "reconstructionTO")
     * @return   instance of list of original type "role"
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public List<String> reconstructionTOToRoles(ReconstructionTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<List<String>>> retType = new TypeReference<List<List<String>>>() {};
        List<List<String>> res = caller.jsonrpcCall("GenomeAnnotation.reconstructionTO_to_roles", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: reconstructionTO_to_subsystems</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.ReconstructionTO ReconstructionTO} (original type "reconstructionTO")
     * @return   instance of original type "variant_subsystem_pairs" &rarr; list of original type "variant_of_subsystem" &rarr; tuple of size 2: original type "subsystem", original type "variant"
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public List<Tuple2<String, String>> reconstructionTOToSubsystems(ReconstructionTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<List<Tuple2<String, String>>>> retType = new TypeReference<List<List<Tuple2<String, String>>>>() {};
        List<List<Tuple2<String, String>>> res = caller.jsonrpcCall("GenomeAnnotation.reconstructionTO_to_subsystems", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: annotate_genome</p>
     * <pre>
     * * Given a genome object populated with contig data, perform gene calling
     * * and functional annotation and return the annotated genome.
     * *
     * *  NOTE: Many of these "transformations" modify the input hash and
     * *        copy the pointer.  Be warned.
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO annotateGenome(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.annotate_genome", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: call_selenoproteins</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO callSelenoproteins(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.call_selenoproteins", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: call_pyrrolysoproteins</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO callPyrrolysoproteins(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.call_pyrrolysoproteins", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: call_RNAs</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO callRNAs(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.call_RNAs", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: call_CDSs</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO callCDSs(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.call_CDSs", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: find_close_neighbors</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO findCloseNeighbors(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.find_close_neighbors", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: assign_functions_to_CDSs</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO assignFunctionsToCDSs(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.assign_functions_to_CDSs", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: annotate_proteins</p>
     * <pre>
     * * Given a genome object populated with feature data, reannotate
     * * the features that have protein translations. Return the updated
     * * genome object.
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO annotateProteins(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.annotate_proteins", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: call_CDSs_by_projection</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO callCDSsByProjection(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.call_CDSs_by_projection", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: get_strep_suis_repeats</p>
     * <pre>
     * * Interface to Strep repeats and "boxes" tools
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO getStrepSuisRepeats(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.get_strep_suis_repeats", args, retType, true, false);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: get_strep_pneumo_repeats</p>
     * <pre>
     * </pre>
     * @param   arg1   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @return   instance of type {@link us.kbase.genomeannotation.GenomeTO GenomeTO} (original type "genomeTO")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GenomeTO getStrepPneumoRepeats(GenomeTO arg1) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(arg1);
        TypeReference<List<GenomeTO>> retType = new TypeReference<List<GenomeTO>>() {};
        List<GenomeTO> res = caller.jsonrpcCall("GenomeAnnotation.get_strep_pneumo_repeats", args, retType, true, false);
        return res.get(0);
    }
}
