package com.cmap.service.vo;

import java.util.Date;

import com.cmap.service.impl.jobs.BaseJobImpl.Result;

public class CommonServiceVO {

	private Integer startNum;
	private Integer pageLength;
	private String searchColumn;
	private String searchValue;
	private String orderColumn;
	private String orderDirection;

	private Result jobExcuteResult;
	private String jobExcuteResultRecords;
	private String jobExcuteRemark;

	private String ipAddr;
	private String macAddr;
	private String actionBy;
	private Date actionTime;

	public Integer getStartNum() {
		return startNum;
	}
	public void setStartNum(Integer startNum) {
		this.startNum = startNum;
	}
	public Integer getPageLength() {
		return pageLength;
	}
	public void setPageLength(Integer pageLength) {
		this.pageLength = pageLength;
	}
	public String getSearchColumn() {
		return searchColumn;
	}
	public void setSearchColumn(String searchColumn) {
		this.searchColumn = searchColumn;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public String getOrderColumn() {
		return orderColumn;
	}
	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}
	public String getOrderDirection() {
		return orderDirection;
	}
	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}
	public String getJobExcuteResultRecords() {
		return jobExcuteResultRecords;
	}
	public void setJobExcuteResultRecords(String jobExcuteResultRecords) {
		this.jobExcuteResultRecords = jobExcuteResultRecords;
	}
	public String getJobExcuteRemark() {
		return jobExcuteRemark;
	}
	public void setJobExcuteRemark(String jobExcuteRemark) {
		this.jobExcuteRemark = jobExcuteRemark;
	}
	public Result getJobExcuteResult() {
		return jobExcuteResult;
	}
	public void setJobExcuteResult(Result jobExcuteResult) {
		this.jobExcuteResult = jobExcuteResult;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getMacAddr() {
		return macAddr;
	}
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}
	public String getActionBy() {
		return actionBy;
	}
	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}
	public Date getActionTime() {
		return actionTime;
	}
	public void setActionTime(Date actionTime) {
		this.actionTime = actionTime;
	}
}
