package com.cmap.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.cmap.Constants;

@Entity
@Table(
	name = "user_right_setting",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"ID"})
	}
)
public class UserRightSetting implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name = "ID", unique = true)
	private String id;

	@Column(name = "ACCOUNT", nullable = false)
	private String account;

	@Column(name = "USERNAME", nullable = true)
	private String userName;
	
	@Column(name = "PASSWORD", nullable = true)
	private String password;
	
	@Column(name = "IS_ADMIN", nullable = false)
	private String isAdmin = Constants.DATA_N;

	@Column(name = "USER_GROUP", nullable = false)
	private String userGroup;

	@Column(name = "LOGIN_MODE", nullable = false)
	private String loginMode;
	
	@Column(name = "DELETE_FLAG", nullable = false)
	private String deleteFlag = Constants.DATA_N;

	@Column(name = "DELETE_TIME", nullable = true)
	private Timestamp deleteTime;

	@Column(name = "DELETE_BY", nullable = true)
	private String deleteBy;

	@Column(name = "CREATE_TIME", nullable = true)
	private Timestamp createTime;

	@Column(name = "CREATE_BY", nullable = true)
	private String createBy;

	@Column(name = "UPDATE_TIME", nullable = true)
	private Timestamp updateTime;

	@Column(name = "UPDATE_BY", nullable = true)
	private String updateBy;

	public UserRightSetting() {
		super();
	}

	public UserRightSetting(String id, String account, String userName, String password, String isAdmin,
			String userGroup, String deleteFlag, Timestamp deleteTime,
			String deleteBy, Timestamp createTime, String createBy, Timestamp updateTime, String updateBy) {
		super();
		this.id = id;
		this.account = account;
		this.userName = userName;
		this.password = password;
		this.isAdmin = isAdmin;
		this.userGroup = userGroup;
		this.deleteFlag = deleteFlag;
		this.deleteTime = deleteTime;
		this.deleteBy = deleteBy;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getLoginMode() {
		return loginMode;
	}

	public void setLoginMode(String loginMode) {
		this.loginMode = loginMode;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Timestamp getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Timestamp deleteTime) {
		this.deleteTime = deleteTime;
	}

	public String getDeleteBy() {
		return deleteBy;
	}

	public void setDeleteBy(String deleteBy) {
		this.deleteBy = deleteBy;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
}
