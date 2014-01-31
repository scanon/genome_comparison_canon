
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
 * <p>Original spec-file type: reconstructionTO</p>
 * <pre>
 * Metabolic reconstruction
 * represents the set of subsystems that we infer are present in this genome
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "subsystems",
    "bindings",
    "assignments"
})
public class ReconstructionTO {

    @JsonProperty("subsystems")
    private List<us.kbase.common.service.Tuple2 <String, String>> subsystems;
    @JsonProperty("bindings")
    private List<us.kbase.common.service.Tuple2 <String, String>> bindings;
    @JsonProperty("assignments")
    private List<us.kbase.common.service.Tuple2 <String, String>> assignments;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("subsystems")
    public List<us.kbase.common.service.Tuple2 <String, String>> getSubsystems() {
        return subsystems;
    }

    @JsonProperty("subsystems")
    public void setSubsystems(List<us.kbase.common.service.Tuple2 <String, String>> subsystems) {
        this.subsystems = subsystems;
    }

    public ReconstructionTO withSubsystems(List<us.kbase.common.service.Tuple2 <String, String>> subsystems) {
        this.subsystems = subsystems;
        return this;
    }

    @JsonProperty("bindings")
    public List<us.kbase.common.service.Tuple2 <String, String>> getBindings() {
        return bindings;
    }

    @JsonProperty("bindings")
    public void setBindings(List<us.kbase.common.service.Tuple2 <String, String>> bindings) {
        this.bindings = bindings;
    }

    public ReconstructionTO withBindings(List<us.kbase.common.service.Tuple2 <String, String>> bindings) {
        this.bindings = bindings;
        return this;
    }

    @JsonProperty("assignments")
    public List<us.kbase.common.service.Tuple2 <String, String>> getAssignments() {
        return assignments;
    }

    @JsonProperty("assignments")
    public void setAssignments(List<us.kbase.common.service.Tuple2 <String, String>> assignments) {
        this.assignments = assignments;
    }

    public ReconstructionTO withAssignments(List<us.kbase.common.service.Tuple2 <String, String>> assignments) {
        this.assignments = assignments;
        return this;
    }

    @JsonAnyGetter
    public Map<java.lang.String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(java.lang.String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public java.lang.String toString() {
        return ((((((((("ReconstructionTO"+" [subsystems=")+ subsystems)+", bindings=")+ bindings)+", assignments=")+ assignments)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
