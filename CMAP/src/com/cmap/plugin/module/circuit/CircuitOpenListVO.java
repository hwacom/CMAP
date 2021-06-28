package com.cmap.plugin.module.circuit;

import java.sql.Timestamp;

import com.cmap.service.vo.CommonServiceVO;

public class CircuitOpenListVO extends CommonServiceVO {

    private String querySrcE1gwSID;
    private String queryDstE1gwSID;
    private String querySrcPort;
    private String queryDstPort;
    private String querySrcE1gwIP;
    private String queryDstE1gwIP;
    
    private String id;
    private String deviceId;
    private String circleName;
    private String stationName;
    private String stationEngName;
    private String neName;
    private String srcSid;
    private String dstSid;
    private String localCsrIp;
    private String remoteCsrIp;
    private String srcE1gwIp;
    private String dstE1gwIp;
    private String srcPortNumber;
    private String dstPortNumber;
    private String remark;
	private Timestamp updateTime;
	private String updateTimeStr;
	private String updateBy;
	
	public String getQuerySrcE1gwSID() {
		return querySrcE1gwSID;
	}
	public void setQuerySrcE1gwSID(String querySrcE1gwSID) {
		this.querySrcE1gwSID = querySrcE1gwSID;
	}
	public String getQueryDstE1gwSID() {
		return queryDstE1gwSID;
	}
	public void setQueryDstE1gwSID(String queryDstE1gwSID) {
		this.queryDstE1gwSID = queryDstE1gwSID;
	}
	public String getQuerySrcPort() {
		return querySrcPort;
	}
	public void setQuerySrcPort(String querySrcPort) {
		this.querySrcPort = querySrcPort;
	}
	public String getQueryDstPort() {
		return queryDstPort;
	}
	public void setQueryDstPort(String queryDstPort) {
		this.queryDstPort = queryDstPort;
	}
	public String getQuerySrcE1gwIP() {
		return querySrcE1gwIP;
	}
	public void setQuerySrcE1gwIP(String querySrcE1gwIP) {
		this.querySrcE1gwIP = querySrcE1gwIP;
	}
	public String getQueryDstE1gwIP() {
		return queryDstE1gwIP;
	}
	public void setQueryDstE1gwIP(String queryDstE1gwIP) {
		this.queryDstE1gwIP = queryDstE1gwIP;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getCircleName() {
		return circleName;
	}
	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getStationEngName() {
		return stationEngName;
	}
	public void setStationEngName(String stationEngName) {
		this.stationEngName = stationEngName;
	}
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getSrcSid() {
		return srcSid;
	}
	public void setSrcSid(String srcSid) {
		this.srcSid = srcSid;
	}
	public String getDstSid() {
		return dstSid;
	}
	public void setDstSid(String dstSid) {
		this.dstSid = dstSid;
	}
	public String getLocalCsrIp() {
		return localCsrIp;
	}
	public void setLocalCsrIp(String localCsrIp) {
		this.localCsrIp = localCsrIp;
	}
	public String getRemoteCsrIp() {
		return remoteCsrIp;
	}
	public void setRemoteCsrIp(String remoteCsrIp) {
		this.remoteCsrIp = remoteCsrIp;
	}
	public String getSrcE1gwIp() {
		return srcE1gwIp;
	}
	public void setSrcE1gwIp(String srcE1gwIp) {
		this.srcE1gwIp = srcE1gwIp;
	}
	public String getDstE1gwIp() {
		return dstE1gwIp;
	}
	public void setDstE1gwIp(String dstE1gwIp) {
		this.dstE1gwIp = dstE1gwIp;
	}
	public String getSrcPortNumber() {
		return srcPortNumber;
	}
	public void setSrcPortNumber(String srcPortNumber) {
		this.srcPortNumber = srcPortNumber;
	}
	public String getDstPortNumber() {
		return dstPortNumber;
	}
	public void setDstPortNumber(String dstPortNumber) {
		this.dstPortNumber = dstPortNumber;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateTimeStr() {
		return updateTimeStr;
	}
	public void setUpdateTimeStr(String updateTimeStr) {
		this.updateTimeStr = updateTimeStr;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

}
