package com.cmap.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

import com.mchange.v1.lang.holders.ThreadSafeIntHolder;

@Entity
@Table(
		name = "provision_api_access_log",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"CHECK_HASH"})
		}
		)
public class ProvisionApiAccessLog {

	@Id
	@Column(name = "CHECK_HASH", unique = true)
	private String checkHash;

	@Column(name = "USER_IP", nullable = true)
	private String userIp;
	
	@Column(name = "user_name", nullable = false)
	private String userName;
	
	@Column(name = "script_code", nullable = false)
	private String scriptCode;

	@Column(name = "DEVICE_ID", nullable = false)
	private String deviceIds;

	@Column(name = "var_key", nullable = true)
	private String varKey;

	@Column(name = "ACCESS_TIME", nullable = false)
	private Timestamp accessTime;

	@Column(name = "ACTION", nullable = false)
	private String action;

	@Column(name = "ACTION_USER", nullable = false)
	private String actionUser;
	
	@Column(name = "ACTION_IP", nullable = false)
	private String actionIp;
	
	@Column(name = "ACTION_TIME", nullable = false)
	private Timestamp actionTime;
	
	public ProvisionApiAccessLog() {
		super();
	}

	public ProvisionApiAccessLog(String checkHash, String userName, String scriptCode, String deviceIds, String varKey, String userIp,
			Timestamp accessTime, String action, String actionUser, String actionIp, Timestamp actionTime) {
		super();
		this.checkHash = checkHash;
		this.userName = userName;
		this.scriptCode = scriptCode;
		this.deviceIds = deviceIds;
		this.varKey = varKey;
		this.userIp = userIp;
		this.accessTime = accessTime;
		this.action = action;
		this.actionUser = actionUser;
		this.actionIp = actionIp;
		this.actionTime = actionTime;
	}

	public String getCheckHash() {
		return checkHash;
	}

	public void setCheckHash(String checkHash) {
		this.checkHash = checkHash;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

	public String getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(String deviceIds) {
		this.deviceIds = deviceIds;
	}

	public String getVarKey() {
		return varKey;
	}

	public void setVarKey(String varKey) {
		this.varKey = varKey;
	}

	public Timestamp getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(Timestamp accessTime) {
		this.accessTime = accessTime;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionUser() {
		return actionUser;
	}

	public void setActionUser(String actionUser) {
		this.actionUser = actionUser;
	}

	public String getActionIp() {
		return actionIp;
	}

	public void setActionIp(String actionIp) {
		this.actionIp = actionIp;
	}

	public Timestamp getActionTime() {
		return actionTime;
	}

	public void setActionTime(Timestamp actionTime) {
		this.actionTime = actionTime;
	}

}
