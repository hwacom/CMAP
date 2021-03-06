package com.cmap.plugin.module.iptracepoller;

import java.util.ArrayList;
import java.util.List;

import com.cmap.service.vo.CommonServiceVO;

public class IpTracePollerVO extends CommonServiceVO {

    private String queryClientMac;
    private String queryClientIp;
	private String queryGroupId;
	private String queryDevice;
    private String queryDateBegin;
    private String queryDateEnd;
    private String queryTimeBegin;
    private String queryTimeEnd;
    private String queryFromDateTime;
    private boolean queryOnLineOnly;

	private List<IpTracePollerVO> matchedList = new ArrayList<>();
	private int totalCount = 0;
	
    private String clientIp;
    private String ipDesc;
    private String startTime;
    private String endTime;
    private String clientMac;
	private String groupName;
	private String deviceName;
	private String deviceModel; //目前前端不顯示此欄位
	private String portName;
	private String portDescription;
    private String showMsg;//NetFlow關連查詢IP資料顯示infoMessage
	
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
	public String getQueryDateBegin() {
		return queryDateBegin;
	}
	public void setQueryDateBegin(String queryDateBegin) {
		this.queryDateBegin = queryDateBegin;
	}
	public String getQueryDateEnd() {
		return queryDateEnd;
	}
	public void setQueryDateEnd(String queryDateEnd) {
		this.queryDateEnd = queryDateEnd;
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
	public boolean isQueryOnLineOnly() {
		return queryOnLineOnly;
	}
	public void setQueryOnLineOnly(boolean queryOnLineOnly) {
		this.queryOnLineOnly = queryOnLineOnly;
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
	public String getIpDesc() {
		return ipDesc;
	}
	public void setIpDesc(String ipDesc) {
		this.ipDesc = ipDesc;
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
	public String getPortDescription() {
		return portDescription;
	}
	public void setPortDescription(String portDescription) {
		this.portDescription = portDescription;
	}
	public String getQueryDevice() {
		return queryDevice;
	}
	public void setQueryDevice(String queryDevice) {
		this.queryDevice = queryDevice;
	}
	public String getQueryFromDateTime() {
		return queryFromDateTime;
	}
	public void setQueryFromDateTime(String queryFromDateTime) {
		this.queryFromDateTime = queryFromDateTime;
	}
	public String getShowMsg() {
		return showMsg;
	}
	public void setShowMsg(String showMsg) {
		this.showMsg = showMsg;
	}
    
}
