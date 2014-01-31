
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
import us.kbase.common.service.Tuple3;
import us.kbase.common.service.Tuple4;


/**
 * <p>Original spec-file type: feature</p>
 * <pre>
 * represents a feature on the genome
 * location on the contig with a type,
 * and if a protein has translation,
 * any aliases associated
 * current history of annoation in style of SEED
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "location",
    "type",
    "function",
    "protein_translation",
    "aliases",
    "annotations"
})
public class Feature {

    @JsonProperty("id")
    private java.lang.String id;
    @JsonProperty("location")
    private List<Tuple4 <String, Long, String, Long>> location;
    @JsonProperty("type")
    private java.lang.String type;
    @JsonProperty("function")
    private java.lang.String function;
    @JsonProperty("protein_translation")
    private java.lang.String proteinTranslation;
    @JsonProperty("aliases")
    private List<String> aliases;
    @JsonProperty("annotations")
    private List<Tuple3 <String, String, Long>> annotations;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("id")
    public java.lang.String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(java.lang.String id) {
        this.id = id;
    }

    public Feature withId(java.lang.String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("location")
    public List<Tuple4 <String, Long, String, Long>> getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(List<Tuple4 <String, Long, String, Long>> location) {
        this.location = location;
    }

    public Feature withLocation(List<Tuple4 <String, Long, String, Long>> location) {
        this.location = location;
        return this;
    }

    @JsonProperty("type")
    public java.lang.String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(java.lang.String type) {
        this.type = type;
    }

    public Feature withType(java.lang.String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("function")
    public java.lang.String getFunction() {
        return function;
    }

    @JsonProperty("function")
    public void setFunction(java.lang.String function) {
        this.function = function;
    }

    public Feature withFunction(java.lang.String function) {
        this.function = function;
        return this;
    }

    @JsonProperty("protein_translation")
    public java.lang.String getProteinTranslation() {
        return proteinTranslation;
    }

    @JsonProperty("protein_translation")
    public void setProteinTranslation(java.lang.String proteinTranslation) {
        this.proteinTranslation = proteinTranslation;
    }

    public Feature withProteinTranslation(java.lang.String proteinTranslation) {
        this.proteinTranslation = proteinTranslation;
        return this;
    }

    @JsonProperty("aliases")
    public List<String> getAliases() {
        return aliases;
    }

    @JsonProperty("aliases")
    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public Feature withAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    @JsonProperty("annotations")
    public List<Tuple3 <String, String, Long>> getAnnotations() {
        return annotations;
    }

    @JsonProperty("annotations")
    public void setAnnotations(List<Tuple3 <String, String, Long>> annotations) {
        this.annotations = annotations;
    }

    public Feature withAnnotations(List<Tuple3 <String, String, Long>> annotations) {
        this.annotations = annotations;
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
        return ((((((((((((((((("Feature"+" [id=")+ id)+", location=")+ location)+", type=")+ type)+", function=")+ function)+", proteinTranslation=")+ proteinTranslation)+", aliases=")+ aliases)+", annotations=")+ annotations)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
