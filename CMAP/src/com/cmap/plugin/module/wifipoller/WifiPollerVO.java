package com.cmap.plugin.module.wifipoller;

import java.util.ArrayList;
import java.util.List;

import com.cmap.service.vo.CommonServiceVO;

public class WifiPollerVO extends CommonServiceVO {

    private String queryClientMac;
    private String queryClientIp;
    private String queryGroupId;
    private String queryApName;
    private String querySsid;
    private String queryDateBegin;
    private String queryDateEnd;
    private String queryTimeBegin;
    private String queryTimeEnd;
    private String queryUserName;
    // for getWifiDetailData
    private String queryStartTime; //as Datetime
    private String queryEndTime; //as Datetime

	private List<WifiPollerVO> matchedList = new ArrayList<>();
	private int totalCount = 0;
	
	private String groupName;
    private String clientMac;
    private String startTime;
    private String endTime;
    private String userName;
    private String clientIp;
    private String apName;
    private String ssid;
    private String totalTraffic;
    private String uploadTraffic;
    private String downloadTraffic;
    
	public String getQueryClientMac() {
		return queryClientMac;
	}
	public String getQueryApName() {
		return queryApName;
	}
	public void setQueryApName(String queryApName) {
		this.queryApName = queryApName;
	}
	public String getQuerySsid() {
		return querySsid;
	}
	public void setQuerySsid(String querySsid) {
		this.querySsid = querySsid;
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
	public String getQueryStartTime() {
		return queryStartTime;
	}
	public void setQueryStartTime(String queryStartTime) {
		this.queryStartTime = queryStartTime;
	}
	public String getQueryEndTime() {
		return queryEndTime;
	}
	public void setQueryEndTime(String queryEndTime) {
		this.queryEndTime = queryEndTime;
	}
	public String getQueryUserName() {
		return queryUserName;
	}
	public void setQueryUserName(String queryUserName) {
		this.queryUserName = queryUserName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getClientMac() {
		return clientMac;
	}
	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getApName() {
		return apName;
	}
	public void setApName(String apName) {
		this.apName = apName;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getTotalTraffic() {
		return totalTraffic;
	}
	public void setTotalTraffic(String totalTraffic) {
		this.totalTraffic = totalTraffic;
	}
	public String getUploadTraffic() {
		return uploadTraffic;
	}
	public void setUploadTraffic(String uploadTraffic) {
		this.uploadTraffic = uploadTraffic;
	}
	public String getDownloadTraffic() {
		return downloadTraffic;
	}
	public void setDownloadTraffic(String downloadTraffic) {
		this.downloadTraffic = downloadTraffic;
	}
	public List<WifiPollerVO> getMatchedList() {
		return matchedList;
	}
	public void setMatchedList(List<WifiPollerVO> matchedList) {
		this.matchedList = matchedList;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
