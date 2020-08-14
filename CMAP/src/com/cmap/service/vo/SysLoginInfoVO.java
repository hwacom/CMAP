package com.cmap.service.vo;

public class SysLoginInfoVO extends CommonServiceVO {
	/*
	 * 查詢使用欄位
	 */
	private String queryDateBegin;
	private String queryDateEnd;
	private String queryTimeBegin;
	private String queryTimeEnd;
	private String queryUserAccount;
	
	private String sessionId;
	private String ipAddr;
	private String account;
	private String userName;
	private String loginTimeStr;
	private String logoutTimeStr;
	
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
	public String getQueryUserAccount() {
		return queryUserAccount;
	}
	public void setQueryUserAccount(String queryUserAccount) {
		this.queryUserAccount = queryUserAccount;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLoginTimeStr() {
		return loginTimeStr;
	}
	public void setLoginTimeStr(String loginTimeStr) {
		this.loginTimeStr = loginTimeStr;
	}
	public String getLogoutTimeStr() {
		return logoutTimeStr;
	}
	public void setLogoutTimeStr(String logoutTimeStr) {
		this.logoutTimeStr = logoutTimeStr;
	}

	
}
