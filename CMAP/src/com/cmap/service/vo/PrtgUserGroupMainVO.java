package com.cmap.service.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"prtg-version", "treesize", "groups"})
public class PrtgUserGroupMainVO {

    @JsonProperty("prtg-version")
    private String prtgVersion;
    @JsonProperty("treesize")
    private Integer treesize;
    @JsonProperty("groups")
    private List<PrtgUserGroupDetailVO> groups = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("prtg-version")
    public String getPrtgVersion() {
        return prtgVersion;
    }

    @JsonProperty("prtg-version")
    public void setPrtgVersion(String prtgVersion) {
        this.prtgVersion = prtgVersion;
    }

    @JsonProperty("treesize")
    public Integer getTreesize() {
        return treesize;
    }

    @JsonProperty("treesize")
    public void setTreesize(Integer treesize) {
        this.treesize = treesize;
    }

    @JsonProperty("groups")
    public List<PrtgUserGroupDetailVO> getGroups() {
        return groups;
    }

    @JsonProperty("groups")
    public void setGroups(List<PrtgUserGroupDetailVO> groups) {
        this.groups = groups;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("prtgVersion", prtgVersion)
                .append("treesize", treesize).append("groups", groups)
                .append("additionalProperties", additionalProperties).toString();
    }

}
