
package us.kbase.genomecomparison;

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
 * <p>Original spec-file type: ProteomeComparison</p>
 * <pre>
 * string genome1ws - workspace of genome1
 * string genome1id - id of genome1
 * string genome2ws - workspace of genome2
 * string genome2id - id of genome2
 * float sub_bbh_percent - optional parameter, minimum percent of bit score compared to best bit score, default is 90
 * string max_evalue -  optional parameter, maximum evalue, default is 1e-10
 * list<string> proteome1names - names of genes of genome1
 * mapping<string, int> proteome1map - map from genes of genome1 to their positions
 * list<string> proteome2names - names of genes of genome2
 * mapping<string, int> proteome2map - map from genes of genome2 to their positions
 * list<list<hit>> data1 - outer list iterates over positions of genome1 gene names, inner list iterates over hits from given gene1 to genome2
 * list<list<hit>> data2 - outer list iterates over positions of genome2 gene names, inner list iterates over hits from given gene2 to genome1
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "genome1ws",
    "genome1id",
    "genome2ws",
    "genome2id",
    "sub_bbh_percent",
    "max_evalue",
    "proteome1names",
    "proteome1map",
    "proteome2names",
    "proteome2map",
    "data1",
    "data2"
})
public class ProteomeComparison {

    @JsonProperty("genome1ws")
    private java.lang.String genome1ws;
    @JsonProperty("genome1id")
    private java.lang.String genome1id;
    @JsonProperty("genome2ws")
    private java.lang.String genome2ws;
    @JsonProperty("genome2id")
    private java.lang.String genome2id;
    @JsonProperty("sub_bbh_percent")
    private Double subBbhPercent;
    @JsonProperty("max_evalue")
    private java.lang.String maxEvalue;
    @JsonProperty("proteome1names")
    private List<String> proteome1names;
    @JsonProperty("proteome1map")
    private Map<String, Long> proteome1map;
    @JsonProperty("proteome2names")
    private List<String> proteome2names;
    @JsonProperty("proteome2map")
    private Map<String, Long> proteome2map;
    @JsonProperty("data1")
    private List<List<us.kbase.common.service.Tuple3 <Long, Long, Long>>> data1;
    @JsonProperty("data2")
    private List<List<us.kbase.common.service.Tuple3 <Long, Long, Long>>> data2;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("genome1ws")
    public java.lang.String getGenome1ws() {
        return genome1ws;
    }

    @JsonProperty("genome1ws")
    public void setGenome1ws(java.lang.String genome1ws) {
        this.genome1ws = genome1ws;
    }

    public ProteomeComparison withGenome1ws(java.lang.String genome1ws) {
        this.genome1ws = genome1ws;
        return this;
    }

    @JsonProperty("genome1id")
    public java.lang.String getGenome1id() {
        return genome1id;
    }

    @JsonProperty("genome1id")
    public void setGenome1id(java.lang.String genome1id) {
        this.genome1id = genome1id;
    }

    public ProteomeComparison withGenome1id(java.lang.String genome1id) {
        this.genome1id = genome1id;
        return this;
    }

    @JsonProperty("genome2ws")
    public java.lang.String getGenome2ws() {
        return genome2ws;
    }

    @JsonProperty("genome2ws")
    public void setGenome2ws(java.lang.String genome2ws) {
        this.genome2ws = genome2ws;
    }

    public ProteomeComparison withGenome2ws(java.lang.String genome2ws) {
        this.genome2ws = genome2ws;
        return this;
    }

    @JsonProperty("genome2id")
    public java.lang.String getGenome2id() {
        return genome2id;
    }

    @JsonProperty("genome2id")
    public void setGenome2id(java.lang.String genome2id) {
        this.genome2id = genome2id;
    }

    public ProteomeComparison withGenome2id(java.lang.String genome2id) {
        this.genome2id = genome2id;
        return this;
    }

    @JsonProperty("sub_bbh_percent")
    public Double getSubBbhPercent() {
        return subBbhPercent;
    }

    @JsonProperty("sub_bbh_percent")
    public void setSubBbhPercent(Double subBbhPercent) {
        this.subBbhPercent = subBbhPercent;
    }

    public ProteomeComparison withSubBbhPercent(Double subBbhPercent) {
        this.subBbhPercent = subBbhPercent;
        return this;
    }

    @JsonProperty("max_evalue")
    public java.lang.String getMaxEvalue() {
        return maxEvalue;
    }

    @JsonProperty("max_evalue")
    public void setMaxEvalue(java.lang.String maxEvalue) {
        this.maxEvalue = maxEvalue;
    }

    public ProteomeComparison withMaxEvalue(java.lang.String maxEvalue) {
        this.maxEvalue = maxEvalue;
        return this;
    }

    @JsonProperty("proteome1names")
    public List<String> getProteome1names() {
        return proteome1names;
    }

    @JsonProperty("proteome1names")
    public void setProteome1names(List<String> proteome1names) {
        this.proteome1names = proteome1names;
    }

    public ProteomeComparison withProteome1names(List<String> proteome1names) {
        this.proteome1names = proteome1names;
        return this;
    }

    @JsonProperty("proteome1map")
    public Map<String, Long> getProteome1map() {
        return proteome1map;
    }

    @JsonProperty("proteome1map")
    public void setProteome1map(Map<String, Long> proteome1map) {
        this.proteome1map = proteome1map;
    }

    public ProteomeComparison withProteome1map(Map<String, Long> proteome1map) {
        this.proteome1map = proteome1map;
        return this;
    }

    @JsonProperty("proteome2names")
    public List<String> getProteome2names() {
        return proteome2names;
    }

    @JsonProperty("proteome2names")
    public void setProteome2names(List<String> proteome2names) {
        this.proteome2names = proteome2names;
    }

    public ProteomeComparison withProteome2names(List<String> proteome2names) {
        this.proteome2names = proteome2names;
        return this;
    }

    @JsonProperty("proteome2map")
    public Map<String, Long> getProteome2map() {
        return proteome2map;
    }

    @JsonProperty("proteome2map")
    public void setProteome2map(Map<String, Long> proteome2map) {
        this.proteome2map = proteome2map;
    }

    public ProteomeComparison withProteome2map(Map<String, Long> proteome2map) {
        this.proteome2map = proteome2map;
        return this;
    }

    @JsonProperty("data1")
    public List<List<us.kbase.common.service.Tuple3 <Long, Long, Long>>> getData1() {
        return data1;
    }

    @JsonProperty("data1")
    public void setData1(List<List<us.kbase.common.service.Tuple3 <Long, Long, Long>>> data1) {
        this.data1 = data1;
    }

    public ProteomeComparison withData1(List<List<us.kbase.common.service.Tuple3 <Long, Long, Long>>> data1) {
        this.data1 = data1;
        return this;
    }

    @JsonProperty("data2")
    public List<List<us.kbase.common.service.Tuple3 <Long, Long, Long>>> getData2() {
        return data2;
    }

    @JsonProperty("data2")
    public void setData2(List<List<us.kbase.common.service.Tuple3 <Long, Long, Long>>> data2) {
        this.data2 = data2;
    }

    public ProteomeComparison withData2(List<List<us.kbase.common.service.Tuple3 <Long, Long, Long>>> data2) {
        this.data2 = data2;
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
        return ((((((((((((((((((((((((((("ProteomeComparison"+" [genome1ws=")+ genome1ws)+", genome1id=")+ genome1id)+", genome2ws=")+ genome2ws)+", genome2id=")+ genome2id)+", subBbhPercent=")+ subBbhPercent)+", maxEvalue=")+ maxEvalue)+", proteome1names=")+ proteome1names)+", proteome1map=")+ proteome1map)+", proteome2names=")+ proteome2names)+", proteome2map=")+ proteome2map)+", data1=")+ data1)+", data2=")+ data2)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
