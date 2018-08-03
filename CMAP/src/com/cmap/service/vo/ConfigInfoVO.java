package com.cmap.service.vo;

public class ConfigInfoVO implements Cloneable {

	private String groupId;
	private String groupName;
	private String deviceId;
	private String deviceName;
	private String configFileDirPath;
	private String configContent;
	private String configFileName;
	private String systemVersion;
	private String configType;
	
	private String deviceIp;
	private String account;
	private String password;
	private String enablePassword;
	
	private String tFtpIP;
	private String tFtpFilePath;
	
	private String ftpIP;
	private Integer ftpPort;
	private String ftpAccount;
	private String ftpPassword;

	@Override
	public Object clone() throws CloneNotSupportedException {
	    return super.clone();
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getConfigFileDirPath() {
		return configFileDirPath;
	}

	public void setConfigFileDirPath(String configFileDirPath) {
		this.configFileDirPath = configFileDirPath;
	}

	public String getConfigContent() {
		return configContent;
	}

	public void setConfigContent(String configContent) {
		this.configContent = configContent;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String gettFtpIP() {
		return tFtpIP;
	}

	public void settFtpIP(String tFtpIP) {
		this.tFtpIP = tFtpIP;
	}

	public String gettFtpFilePath() {
		return tFtpFilePath;
	}

	public void settFtpFilePath(String tFtpFilePath) {
		this.tFtpFilePath = tFtpFilePath;
	}

	public String getFtpIP() {
		return ftpIP;
	}

	public void setFtpIP(String ftpIP) {
		this.ftpIP = ftpIP;
	}

	public String getFtpAccount() {
		return ftpAccount;
	}

	public void setFtpAccount(String ftpAccount) {
		this.ftpAccount = ftpAccount;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getEnablePassword() {
		return enablePassword;
	}

	public void setEnablePassword(String enablePassword) {
		this.enablePassword = enablePassword;
	}

	public Integer getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(Integer ftpPort) {
		this.ftpPort = ftpPort;
	}
}
