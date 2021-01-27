package com.cmap.service.vo;

public class UserRightServiceVO extends CommonServiceVO {

	/*
	 * 查詢使用欄位
	 */	
	private String inputAccount;
	private String inputUserName;
	private String inputUserGroup;
	
	/*
	 * 查詢結果呈顯使用欄位
	 */
	private String id;
	private String account;
	private String userName;
	private String password;
	private String isAdmin;
	private String userGroup;
	private String userGroupStr;
	private String loginMode;
	private String remark;
	private String createBy;
	private String createTimeStr;
	private String updateBy;
	private String updateTimeStr;
	
	/*
	 * 修改使用欄位
	 */
	private String modifyAccount;
	private String modifyUserName;
	private String modifyPassword;
	private String modifyUserGroup;
	private String modifyRemark;
	
	public String getInputAccount() {
		return inputAccount;
	}
	public void setInputAccount(String inputAccount) {
		this.inputAccount = inputAccount;
	}
	public String getInputUserName() {
		return inputUserName;
	}
	public void setInputUserName(String inputUserName) {
		this.inputUserName = inputUserName;
	}
	public String getInputUserGroup() {
		return inputUserGroup;
	}
	public void setInputUserGroup(String inputUserGroup) {
		this.inputUserGroup = inputUserGroup;
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
	public String getUserGroupStr() {
		return userGroupStr;
	}
	public void setUserGroupStr(String userGroupStr) {
		this.userGroupStr = userGroupStr;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getCreateTimeStr() {
		return createTimeStr;
	}
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public String getUpdateTimeStr() {
		return updateTimeStr;
	}
	public void setUpdateTimeStr(String updateTimeStr) {
		this.updateTimeStr = updateTimeStr;
	}
	public String getModifyAccount() {
		return modifyAccount;
	}
	public void setModifyAccount(String modifyAccount) {
		this.modifyAccount = modifyAccount;
	}
	public String getModifyUserName() {
		return modifyUserName;
	}
	public void setModifyUserName(String modifyUserName) {
		this.modifyUserName = modifyUserName;
	}
	public String getModifyPassword() {
		return modifyPassword;
	}
	public void setModifyPassword(String modifyPassword) {
		this.modifyPassword = modifyPassword;
	}
	public String getModifyUserGroup() {
		return modifyUserGroup;
	}
	public void setModifyUserGroup(String modifyUserGroup) {
		this.modifyUserGroup = modifyUserGroup;
	}
	public String getLoginMode() {
		return loginMode;
	}
	public void setLoginMode(String loginMode) {
		this.loginMode = loginMode;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getModifyRemark() {
		return modifyRemark;
	}
	public void setModifyRemark(String modifyRemark) {
		this.modifyRemark = modifyRemark;
	}
	
}
