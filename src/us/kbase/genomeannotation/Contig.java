
package us.kbase.genomeannotation;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: contig</p>
 * <pre>
 * Data for DNA contig
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "dna"
})
public class Contig {

    @JsonProperty("id")
    private String id;
    @JsonProperty("dna")
    private String dna;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public Contig withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("dna")
    public String getDna() {
        return dna;
    }

    @JsonProperty("dna")
    public void setDna(String dna) {
        this.dna = dna;
    }

    public Contig withDna(String dna) {
        this.dna = dna;
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
        return ((((((("Contig"+" [id=")+ id)+", dna=")+ dna)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
