package com.cmap.plugin.module.wifipoller;

import java.util.ArrayList;
import java.util.List;

import com.cmap.plugin.module.netflow.NetFlowVO;
import com.cmap.service.vo.CommonServiceVO;

public class WifiPollerVO extends CommonServiceVO {

    private String queryClientMac;
    private String queryClientIp;
    private String queryApName;
    private String querySsid;
    private String queryDate;
    private String queryTimeBegin;
    private String queryTimeEnd;

	private List<WifiPollerVO> matchedList = new ArrayList<>();
	private int totalCount = 0;
	
    private String clientMac;
    private String startTime;
    private String endTime;
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
