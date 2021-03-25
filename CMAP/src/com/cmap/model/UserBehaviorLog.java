package com.cmap.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * USER_OPERATION_LOG Entity
 * @author Owen Chang
 *
 */
@Entity
@Table(name="user_behavior_log")
public class UserBehaviorLog implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "LOG_ID", nullable = false)
	private String logId;

	@Column(name = "USER_ACCOUNT", nullable = false)
	private String userAccount;
	
	@Column(name = "USER_NAME", nullable = false)
	private String userName;

	@Column(name = "BEHAVIOR", nullable = false)
	private String behavior;

	@Column(name = "TARGET_PATH", nullable = false)
	private String targetPath;

	@Column(name = "DESCRIPTION", nullable = false)
	private String description;

	@Column(name = "BEHAVIOR_TIME", nullable = false)
	private Timestamp behaviorTime;

	public UserBehaviorLog() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserBehaviorLog(String logId, String userAccount, String userName, String behavior, String targetPath,
			String description, Timestamp behaviorTime) {
		super();
		this.logId = logId;
		this.userAccount = userAccount;
		this.userName = userName;
		this.behavior = behavior;
		this.targetPath = targetPath;
		this.description = description;
		this.behaviorTime = behaviorTime;
	}

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBehavior() {
		return behavior;
	}

	public void setBehavior(String behavior) {
		this.behavior = behavior;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getBehaviorTime() {
		return behaviorTime;
	}

	public void setBehaviorTime(Timestamp behaviorTime) {
		this.behaviorTime = behaviorTime;
	}

}
