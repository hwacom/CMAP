package com.cmap.plugin.module.mobilepoller;

import java.util.ArrayList;
import java.util.List;

import com.cmap.service.vo.CommonServiceVO;

public class MobilePollerVO extends CommonServiceVO {

    private String queryClientSUPI;
    private String queryClientNumber;
    private String queryGroupId;
    private String queryCellName;
    private String querySsid;
    private String queryDateBegin;
    private String queryDateEnd;
    private String queryTimeBegin;
    private String queryTimeEnd;
    // for getWifiDetailData
    private String queryStartTime; //as Datetime
    private String queryEndTime; //as Datetime

	private List<MobilePollerVO> matchedList = new ArrayList<>();
	private int totalCount = 0;
	
	private String groupName;
    private String clientSUPI;
    private String startTime;
    private String endTime;
    private String clientNumber;
    private String apName;
    private String ssid;
    private String totalTraffic;
    private String uploadTraffic;
    private String downloadTraffic;
    
	public String getQueryClientSUPI() {
		return queryClientSUPI;
	}
	public String getQueryCellName() {
		return queryCellName;
	}
	public void setQueryCellName(String queryCellName) {
		this.queryCellName = queryCellName;
	}
	public String getQuerySsid() {
		return querySsid;
	}
	public void setQuerySsid(String querySsid) {
		this.querySsid = querySsid;
	}
	public void setQueryClientSUPI(String queryClientSUPI) {
		this.queryClientSUPI = queryClientSUPI;
	}
	public String getQueryClientNumber() {
		return queryClientNumber;
	}
	public void setQueryClientNumber(String queryClientNumber) {
		this.queryClientNumber = queryClientNumber;
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
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getClientSUPI() {
		return clientSUPI;
	}
	public void setClientSUPI(String clientSUPI) {
		this.clientSUPI = clientSUPI;
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
	public String getClientNumber() {
		return clientNumber;
	}
	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}
	public String getCellName() {
		return apName;
	}
	public void setCellName(String apName) {
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
	public List<MobilePollerVO> getMatchedList() {
		return matchedList;
	}
	public void setMatchedList(List<MobilePollerVO> matchedList) {
		this.matchedList = matchedList;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
