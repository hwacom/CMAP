package com.cmap.service.vo;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"hash",
"varValue",
"reason"
})
public class ProvisionParameterVO {

	@JsonProperty("hash")
	private String hash;
	@JsonProperty("varValue")
	private List<List<String>> varValue = null;
	@JsonProperty("reason")
	private String reason;
	
	@JsonProperty("hash")
	public String getHash() {
		return hash;
	}
	@JsonProperty("hash")
	public void setHash(String hash) {
		this.hash = hash;
	}
	@JsonProperty("varValue")
	public List<List<String>> getVarValue() {
		return varValue;
	}
	@JsonProperty("varValue")
	public void setVarValue(List<List<String>> varValue) {
		this.varValue = varValue;
	}
	@JsonProperty("reason")
	public String getReason() {
		return reason;
	}
	@JsonProperty("reason")
	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("varValue", varValue).append("reason", reason).toString();
	}
}