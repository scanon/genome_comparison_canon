
package us.kbase.genomecomparison;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: annotate_genome_params</p>
 * <pre>
 * string in_genome_ws - workspace of input genome
 * string in_genome_id - id of input genome
 * string out_genome_ws - workspace of output genome
 * string out_genome_id - future id of output genome
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "in_genome_ws",
    "in_genome_id",
    "out_genome_ws",
    "out_genome_id"
})
public class AnnotateGenomeParams {

    @JsonProperty("in_genome_ws")
    private String inGenomeWs;
    @JsonProperty("in_genome_id")
    private String inGenomeId;
    @JsonProperty("out_genome_ws")
    private String outGenomeWs;
    @JsonProperty("out_genome_id")
    private String outGenomeId;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("in_genome_ws")
    public String getInGenomeWs() {
        return inGenomeWs;
    }

    @JsonProperty("in_genome_ws")
    public void setInGenomeWs(String inGenomeWs) {
        this.inGenomeWs = inGenomeWs;
    }

    public AnnotateGenomeParams withInGenomeWs(String inGenomeWs) {
        this.inGenomeWs = inGenomeWs;
        return this;
    }

    @JsonProperty("in_genome_id")
    public String getInGenomeId() {
        return inGenomeId;
    }

    @JsonProperty("in_genome_id")
    public void setInGenomeId(String inGenomeId) {
        this.inGenomeId = inGenomeId;
    }

    public AnnotateGenomeParams withInGenomeId(String inGenomeId) {
        this.inGenomeId = inGenomeId;
        return this;
    }

    @JsonProperty("out_genome_ws")
    public String getOutGenomeWs() {
        return outGenomeWs;
    }

    @JsonProperty("out_genome_ws")
    public void setOutGenomeWs(String outGenomeWs) {
        this.outGenomeWs = outGenomeWs;
    }

    public AnnotateGenomeParams withOutGenomeWs(String outGenomeWs) {
        this.outGenomeWs = outGenomeWs;
        return this;
    }

    @JsonProperty("out_genome_id")
    public String getOutGenomeId() {
        return outGenomeId;
    }

    @JsonProperty("out_genome_id")
    public void setOutGenomeId(String outGenomeId) {
        this.outGenomeId = outGenomeId;
    }

    public AnnotateGenomeParams withOutGenomeId(String outGenomeId) {
        this.outGenomeId = outGenomeId;
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
        return ((((((((((("AnnotateGenomeParams"+" [inGenomeWs=")+ inGenomeWs)+", inGenomeId=")+ inGenomeId)+", outGenomeWs=")+ outGenomeWs)+", outGenomeId=")+ outGenomeId)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
