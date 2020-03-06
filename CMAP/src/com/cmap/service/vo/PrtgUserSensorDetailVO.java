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
@JsonPropertyOrder({"objid", "sensor", "device", "parentid"})
public class PrtgUserSensorDetailVO {

    @JsonProperty("objid")
    private String objid;
    @JsonProperty("sensor")
    private String sensor;
    @JsonProperty("device")
    private String device;
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

    @JsonProperty("sensor")
    public String getSensor() {
        return sensor;
    }

    @JsonProperty("sensor")
    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    @JsonProperty("device")
    public String getDevice() {
        return device;
    }

    @JsonProperty("device")
    public void setDevice(String device) {
        this.device = device;
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
        return new ToStringBuilder(this).append("objid", objid).append("sensor", sensor)
                .append("device", device).append("parentid", parentid)
                .append("additionalProperties", additionalProperties).toString();
    }

}
