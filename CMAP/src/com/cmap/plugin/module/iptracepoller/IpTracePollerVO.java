package com.cmap.plugin.module.iptracepoller;

import java.util.ArrayList;
import java.util.List;

import com.cmap.service.vo.CommonServiceVO;

public class IpTracePollerVO extends CommonServiceVO {

    private String queryClientMac;
    private String queryClientIp;
	private String queryGroupId;
    private String queryDate;
    private String queryTimeBegin;
    private String queryTimeEnd;

	private List<IpTracePollerVO> matchedList = new ArrayList<>();
	private int totalCount = 0;
	
    private String clientIp;
    private String startTime;
    private String endTime;
    private String clientMac;
	private String groupName;
	private String deviceName;
	private String deviceModel; //目前前端不顯示此欄位
	private String portName;
	
	public String getQueryClientMac() {
		return queryClientMac;
	}
	public void setQueryClientMac(String queryClientMac) {
		this.queryClientMac = queryClientMac;
	}
	public String getQueryClientIp() {
		return queryClientIp;
	}
	public void setQueryClientIp(String queryClientIp) {
		this.queryClientIp = queryClientIp;
	}
	public String getQueryGroupId() {
		return queryGroupId;
	}
	public void setQueryGroupId(String queryGroupId) {
		this.queryGroupId = queryGroupId;
	}
	public String getQueryDate() {
		return queryDate;
	}
	public void setQueryDate(String queryDate) {
		this.queryDate = queryDate;
	}
	public String getQueryTimeBegin() {
		return queryTimeBegin;
	}
	public void setQueryTimeBegin(String queryTimeBegin) {
		this.queryTimeBegin = queryTimeBegin;
	}
	public String getQueryTimeEnd() {
		return queryTimeEnd;
	}
	public void setQueryTimeEnd(String queryTimeEnd) {
		this.queryTimeEnd = queryTimeEnd;
	}
	public List<IpTracePollerVO> getMatchedList() {
		return matchedList;
	}
	public void setMatchedList(List<IpTracePollerVO> matchedList) {
		this.matchedList = matchedList;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getClientMac() {
		return clientMac;
	}
	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
    
}
