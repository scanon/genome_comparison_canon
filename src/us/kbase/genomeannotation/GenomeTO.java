
package us.kbase.genomeannotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: genomeTO</p>
 * <pre>
 * All of the information about particular genome
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "scientific_name",
    "domain",
    "genetic_code",
    "source",
    "source_id",
    "contigs",
    "features"
})
public class GenomeTO {

    @JsonProperty("id")
    private String id;
    @JsonProperty("scientific_name")
    private String scientificName;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("genetic_code")
    private Long geneticCode;
    @JsonProperty("source")
    private String source;
    @JsonProperty("source_id")
    private String sourceId;
    @JsonProperty("contigs")
    private List<Contig> contigs;
    @JsonProperty("features")
    private List<Feature> features;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public GenomeTO withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("scientific_name")
    public String getScientificName() {
        return scientificName;
    }

    @JsonProperty("scientific_name")
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public GenomeTO withScientificName(String scientificName) {
        this.scientificName = scientificName;
        return this;
    }

    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    public GenomeTO withDomain(String domain) {
        this.domain = domain;
        return this;
    }

    @JsonProperty("genetic_code")
    public Long getGeneticCode() {
        return geneticCode;
    }

    @JsonProperty("genetic_code")
    public void setGeneticCode(Long geneticCode) {
        this.geneticCode = geneticCode;
    }

    public GenomeTO withGeneticCode(Long geneticCode) {
        this.geneticCode = geneticCode;
        return this;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    public GenomeTO withSource(String source) {
        this.source = source;
        return this;
    }

    @JsonProperty("source_id")
    public String getSourceId() {
        return sourceId;
    }

    @JsonProperty("source_id")
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public GenomeTO withSourceId(String sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    @JsonProperty("contigs")
    public List<Contig> getContigs() {
        return contigs;
    }

    @JsonProperty("contigs")
    public void setContigs(List<Contig> contigs) {
        this.contigs = contigs;
    }

    public GenomeTO withContigs(List<Contig> contigs) {
        this.contigs = contigs;
        return this;
    }

    @JsonProperty("features")
    public List<Feature> getFeatures() {
        return features;
    }

    @JsonProperty("features")
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public GenomeTO withFeatures(List<Feature> features) {
        this.features = features;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return ((((((((((((((((((("GenomeTO"+" [id=")+ id)+", scientificName=")+ scientificName)+", domain=")+ domain)+", geneticCode=")+ geneticCode)+", source=")+ source)+", sourceId=")+ sourceId)+", contigs=")+ contigs)+", features=")+ features)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
