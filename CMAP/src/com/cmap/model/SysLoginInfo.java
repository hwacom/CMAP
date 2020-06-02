package com.cmap.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(
	name = "sys_login_info",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"ID"})
	}
)
public class SysLoginInfo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name = "ID", unique = true)
	private String id;

	@Column(name = "SESSION_ID", nullable = false)
	private String sessionId;
	
	@Column(name = "IP_ADDR", nullable = true)
	private String ipAddr;
	
	@Column(name = "ACCOUNT", nullable = false)
	private String account;

	@Column(name = "USER_NAME", nullable = true)
	private String userName;
	
	@Column(name = "LOGIN_TIME", nullable = true)
	private Timestamp loginTime;
	
	@Column(name = "LOGOUT_TIME", nullable = true)
	private Timestamp logoutTime;

	public SysLoginInfo() {
		super();
	}

	public SysLoginInfo(String id, String sessionId, String ipAddr, String account, String userName,
			Timestamp loginTime, Timestamp logoutTime) {
		super();
		this.id = id;
		this.sessionId = sessionId;
		this.ipAddr = ipAddr;
		this.account = account;
		this.userName = userName;
		this.loginTime = loginTime;
		this.logoutTime = logoutTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Timestamp getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}

	public Timestamp getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Timestamp logoutTime) {
		this.logoutTime = logoutTime;
	}
}
