package com.cmap.service.vo;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"objid", "group", "device", "host", "parentid"})
public class PrtgUserDeviceDetailVO {

    @JsonProperty("objid")
    private String objid;
    @JsonProperty("group")
    private String group;
    @JsonProperty("device")
    private String device;
    @JsonProperty("host")
    private String host;
    @JsonProperty("parentid")
    private String parentid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("objid")
    public String getObjid() {
        return objid;
    }

    @JsonProperty("objid")
    public void setObjid(String objid) {
        this.objid = objid;
    }

    @JsonProperty("group")
    public String getGroup() {
        return group;
    }

    @JsonProperty("group")
    public void setGroup(String group) {
        this.group = group;
    }

    @JsonProperty("device")
    public String getDevice() {
        return device;
    }

    @JsonProperty("device")
    public void setDevice(String device) {
        this.device = device;
    }

    @JsonProperty("host")
    public String getHost() {
        return host;
    }

    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty("parentid")
    public String getParentid() {
        return parentid;
    }

    @JsonProperty("parentid")
    public void setParentid(String parentid) {
        this.parentid = parentid;
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
        return new ToStringBuilder(this).append("objid", objid).append("group", group)
                .append("device", device).append("host", host).append("parentid", parentid)
                .append("additionalProperties", additionalProperties).toString();
    }

}
