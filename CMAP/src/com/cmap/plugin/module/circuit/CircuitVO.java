package com.cmap.plugin.module.circuit;

import java.util.List;

public class CircuitVO {

    private String queryCircleId;
    private String queryStationEngName;
    private String queryNeName;
    private List<String> queryE1gwIpList;
    private List<String> querySrPrefixSidList;
    
    private String statinEngName;
    private String neName;
    private String e1gwIp;
    private String srPrefixSid;
    private String localAreaCsrIp;
    private String portNumber;
    private String remark;
	    
	private boolean isAdmin = false;

	public String getQueryCircleId() {
		return queryCircleId;
	}

	public void setQueryCircleId(String queryCircleId) {
		this.queryCircleId = queryCircleId;
	}

	public String getQueryStationEngName() {
		return queryStationEngName;
	}

	public void setQueryStationEngName(String queryStationEngName) {
		this.queryStationEngName = queryStationEngName;
	}

	public String getQueryNeName() {
		return queryNeName;
	}

	public void setQueryNeName(String queryNeName) {
		this.queryNeName = queryNeName;
	}
	
	public List<String> getQueryE1gwIpList() {
		return queryE1gwIpList;
	}

	public void setQueryE1gwIpList(List<String> queryE1gwIpList) {
		this.queryE1gwIpList = queryE1gwIpList;
	}

	public List<String> getQuerySrPrefixSidList() {
		return querySrPrefixSidList;
	}
	
	public void setQuerySrPrefixSidList(List<String> querySrPrefixSidList) {
		this.querySrPrefixSidList = querySrPrefixSidList;
	}
	
	public String getStatinEngName() {
		return statinEngName;
	}

	public void setStatinEngName(String statinEngName) {
		this.statinEngName = statinEngName;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getE1gwIp() {
		return e1gwIp;
	}

	public void setE1gwIp(String e1gwIp) {
		this.e1gwIp = e1gwIp;
	}

	public String getSrPrefixSid() {
		return srPrefixSid;
	}

	public void setSrPrefixSid(String srPrefixSid) {
		this.srPrefixSid = srPrefixSid;
	}

	public String getLocalAreaCsrIp() {
		return localAreaCsrIp;
	}

	public void setLocalAreaCsrIp(String localAreaCsrIp) {
		this.localAreaCsrIp = localAreaCsrIp;
	}

	public String getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

}
