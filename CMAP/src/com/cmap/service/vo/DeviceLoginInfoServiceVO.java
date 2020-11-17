package com.cmap.service.vo;

import java.util.ArrayList;
import java.util.List;

public class DeviceLoginInfoServiceVO extends CommonServiceVO {

	/*
	 * 查詢使用欄位
	 */	
	private String queryGroup;
	private List<String> queryGroupList = new ArrayList<String>();
	private String queryDevice;
	private List<String> queryDeviceList = new ArrayList<String>();
	
	/*
	 * 查詢結果呈顯使用欄位
	 */
	private String deviceId;
	private String connectionMode;
	private String loginAccount;	
	private String loginPassword;
	private String enablePassword;
	private String enableBackup;	
	private String communityString;
	private Integer udpPort;
	private String configBackupMode;
	private String fileServer;
	private String step;
	private String remark;
	private String updateBy;
	private String updateTimeStr;
	
	/*
	 * 修改使用欄位
	 */
	private String modifyConnectionMode;
	private String modifyLoginAccount;
	private String modifyLoginPassword;
	private String modifyEnablePassword;
	private String modifyEnableBackup;
	private String modifyStep;
	private String modifyFileServer;

	
	public String getQueryGroup() {
		return queryGroup;
	}
	public void setQueryGroup(String queryGroup) {
		this.queryGroup = queryGroup;
	}
	public List<String> getQueryGroupList() {
		return queryGroupList;
	}
	public void setQueryGroupList(List<String> queryGroupList) {
		this.queryGroupList = queryGroupList;
	}
	public String getQueryDevice() {
		return queryDevice;
	}
	public void setQueryDevice(String queryDevice) {
		this.queryDevice = queryDevice;
	}
	public List<String> getQueryDeviceList() {
		return queryDeviceList;
	}
	public void setQueryDeviceList(List<String> queryDeviceList) {
		this.queryDeviceList = queryDeviceList;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getConnectionMode() {
		return connectionMode;
	}
	public void setConnectionMode(String connectionMode) {
		this.connectionMode = connectionMode;
	}
	public String getLoginAccount() {
		return loginAccount;
	}
	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}
	public String getLoginPassword() {
		return loginPassword;
	}
	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	public String getEnablePassword() {
		return enablePassword;
	}
	public void setEnablePassword(String enablePassword) {
		this.enablePassword = enablePassword;
	}
	public String getEnableBackup() {
		return enableBackup;
	}
	public void setEnableBackup(String enableBackup) {
		this.enableBackup = enableBackup;
	}
	public String getCommunityString() {
		return communityString;
	}
	public void setCommunityString(String communityString) {
		this.communityString = communityString;
	}
	public Integer getUdpPort() {
		return udpPort;
	}
	public void setUdpPort(Integer udpPort) {
		this.udpPort = udpPort;
	}
	public String getConfigBackupMode() {
		return configBackupMode;
	}
	public void setConfigBackupMode(String configBackupMode) {
		this.configBackupMode = configBackupMode;
	}
	public String getFileServer() {
		return fileServer;
	}
	public void setFileServer(String fileServer) {
		this.fileServer = fileServer;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public String getModifyConnectionMode() {
		return modifyConnectionMode;
	}
	public void setModifyConnectionMode(String modifyConnectionMode) {
		this.modifyConnectionMode = modifyConnectionMode;
	}
	public String getModifyLoginAccount() {
		return modifyLoginAccount;
	}
	public void setModifyLoginAccount(String modifyLoginAccount) {
		this.modifyLoginAccount = modifyLoginAccount;
	}
	public String getModifyLoginPassword() {
		return modifyLoginPassword;
	}
	public void setModifyLoginPassword(String modifyLoginPassword) {
		this.modifyLoginPassword = modifyLoginPassword;
	}
	public String getModifyEnablePassword() {
		return modifyEnablePassword;
	}
	public void setModifyEnablePassword(String modifyEnablePassword) {
		this.modifyEnablePassword = modifyEnablePassword;
	}
	public String getModifyEnableBackup() {
		return modifyEnableBackup;
	}
	public void setModifyEnableBackup(String modifyEnableBackup) {
		this.modifyEnableBackup = modifyEnableBackup;
	}
	public String getModifyStep() {
		return modifyStep;
	}
	public void setModifyStep(String modifyStep) {
		this.modifyStep = modifyStep;
	}
	public String getModifyFileServer() {
		return modifyFileServer;
	}
	public void setModifyFileServer(String modifyFileServer) {
		this.modifyFileServer = modifyFileServer;
	}

	
}
