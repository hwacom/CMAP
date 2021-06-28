package com.cmap.service.vo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InventoryInfoVO extends CommonServiceVO {
	/*
	 * 查詢使用欄位
	 */
	private String queryDevice;
	private List<String> queryDeviceList = new ArrayList<String>();
	private String queryProbe;
	private String queryDeviceName;
	private String queryDeviceType;
	private String queryGroupName;//For自定義群組
	private String queryIP;
	private String queryModel;
	private boolean queryDiffOnly;
	
	private String deviceId;
	private String probe;
	private String groupName;
	//For Export File
	private String groupName1;
	private String groupName2;
	private String groupName3;
	private String groupName4;
	private String deviceName;
	private String deviceIp;
	private String deviceType;
	private String brand;
	private String model;
	private String systemVersion;
	private String serialNumber;
	private String manufactureDate;
	private String status;
	private String syncFlag;
	private String diffrenceComparison;
	private Timestamp uploadTime;
	private String uploadTimeStr;
	private String custodian;
	private String department;
	private String user;
	private String northFlag;
	private String remark;
	private String deleteFlag;
	private Timestamp deleteTime;
	private String deleteBy;
	private String deleteRsn;
	private Timestamp createTime;
	private String createTimeStr;
	private String createBy;
	private Timestamp updateTime;
	private String updateBy;
	
	/*
	 * 修改使用欄位
	 */
	private String modifyProbe;
	private String modifyGroup;
	private String modifyDeviceName;
	private String modifyDeviceIp;
	private String modifyDeviceType;
	private String modifyBrand;
	private String modifyModel;
	private String modifySystemVersion;
	private String modifySerialNumber;
	private String modifyManufactureDate;
	private String modifyStatus;
	private String modifySyncFlag;
	private String modifyCustodian;
	private String modifyDepartment;
	private String modifyUser;
	private String modifyNorthFlag;
	private String modifyRemark;
	
	public InventoryInfoVO() {
		super();
		// TODO Auto-generated constructor stub
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

	public String getQueryProbe() {
		return queryProbe;
	}

	public void setQueryProbe(String queryProbe) {
		this.queryProbe = queryProbe;
	}

	public String getQueryDeviceName() {
		return queryDeviceName;
	}

	public void setQueryDeviceName(String queryDeviceName) {
		this.queryDeviceName = queryDeviceName;
	}

	public String getQueryDeviceType() {
		return queryDeviceType;
	}

	public void setQueryDeviceType(String queryDeviceType) {
		this.queryDeviceType = queryDeviceType;
	}

	public String getQueryGroupName() {
		return queryGroupName;
	}

	public void setQueryGroupName(String queryGroupName) {
		this.queryGroupName = queryGroupName;
	}

	public String getQueryIP() {
		return queryIP;
	}

	public void setQueryIP(String queryIP) {
		this.queryIP = queryIP;
	}

	public String getQueryModel() {
		return queryModel;
	}

	public void setQueryModel(String queryModel) {
		this.queryModel = queryModel;
	}

	public boolean isQueryDiffOnly() {
		return queryDiffOnly;
	}

	public void setQueryDiffOnly(boolean queryDiffOnly) {
		this.queryDiffOnly = queryDiffOnly;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getProbe() {
		return probe;
	}

	public void setProbe(String probe) {
		this.probe = probe;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName1() {
		return groupName1;
	}

	public void setGroupName1(String groupName1) {
		this.groupName1 = groupName1;
	}

	public String getGroupName2() {
		return groupName2;
	}

	public void setGroupName2(String groupName2) {
		this.groupName2 = groupName2;
	}

	public String getGroupName3() {
		return groupName3;
	}

	public void setGroupName3(String groupName3) {
		this.groupName3 = groupName3;
	}

	public String getGroupName4() {
		return groupName4;
	}

	public void setGroupName4(String groupName4) {
		this.groupName4 = groupName4;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getManufactureDate() {
		return manufactureDate;
	}

	public void setManufactureDate(String manufactureDate) {
		this.manufactureDate = manufactureDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSyncFlag() {
		return syncFlag;
	}

	public void setSyncFlag(String syncFlag) {
		this.syncFlag = syncFlag;
	}

	public String getDiffrenceComparison() {
		return diffrenceComparison;
	}

	public void setDiffrenceComparison(String diffrenceComparison) {
		this.diffrenceComparison = diffrenceComparison;
	}

	public Timestamp getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Timestamp uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getUploadTimeStr() {
		return uploadTimeStr;
	}

	public void setUploadTimeStr(String uploadTimeStr) {
		this.uploadTimeStr = uploadTimeStr;
	}

	public String getCustodian() {
		return custodian;
	}

	public void setCustodian(String custodian) {
		this.custodian = custodian;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getNorthFlag() {
		return northFlag;
	}

	public void setNorthFlag(String northFlag) {
		this.northFlag = northFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getDeleteRsn() {
		return deleteRsn;
	}

	public void setDeleteRsn(String deleteRsn) {
		this.deleteRsn = deleteRsn;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
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

	public String getModifyProbe() {
		return modifyProbe;
	}

	public void setModifyProbe(String modifyProbe) {
		this.modifyProbe = modifyProbe;
	}

	public String getModifyGroup() {
		return modifyGroup;
	}

	public void setModifyGroup(String modifyGroup) {
		this.modifyGroup = modifyGroup;
	}

	public String getModifyDeviceName() {
		return modifyDeviceName;
	}

	public void setModifyDeviceName(String modifyDeviceName) {
		this.modifyDeviceName = modifyDeviceName;
	}

	public String getModifyDeviceIp() {
		return modifyDeviceIp;
	}

	public void setModifyDeviceIp(String modifyDeviceIp) {
		this.modifyDeviceIp = modifyDeviceIp;
	}

	public String getModifyDeviceType() {
		return modifyDeviceType;
	}

	public void setModifyDeviceType(String modifyDeviceType) {
		this.modifyDeviceType = modifyDeviceType;
	}

	public String getModifyBrand() {
		return modifyBrand;
	}

	public void setModifyBrand(String modifyBrand) {
		this.modifyBrand = modifyBrand;
	}

	public String getModifyModel() {
		return modifyModel;
	}

	public void setModifyModel(String modifyModel) {
		this.modifyModel = modifyModel;
	}

	public String getModifySystemVersion() {
		return modifySystemVersion;
	}

	public void setModifySystemVersion(String modifySystemVersion) {
		this.modifySystemVersion = modifySystemVersion;
	}

	public String getModifySerialNumber() {
		return modifySerialNumber;
	}

	public void setModifySerialNumber(String modifySerialNumber) {
		this.modifySerialNumber = modifySerialNumber;
	}

	public String getModifyManufactureDate() {
		return modifyManufactureDate;
	}

	public void setModifyManufactureDate(String modifyManufactureDate) {
		this.modifyManufactureDate = modifyManufactureDate;
	}

	public String getModifyStatus() {
		return modifyStatus;
	}

	public void setModifyStatus(String modifyStatus) {
		this.modifyStatus = modifyStatus;
	}

	public String getModifySyncFlag() {
		return modifySyncFlag;
	}

	public void setModifySyncFlag(String modifySyncFlag) {
		this.modifySyncFlag = modifySyncFlag;
	}

	public String getModifyCustodian() {
		return modifyCustodian;
	}

	public void setModifyCustodian(String modifyCustodian) {
		this.modifyCustodian = modifyCustodian;
	}

	public String getModifyDepartment() {
		return modifyDepartment;
	}

	public void setModifyDepartment(String modifyDepartment) {
		this.modifyDepartment = modifyDepartment;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public String getModifyNorthFlag() {
		return modifyNorthFlag;
	}

	public void setModifyNorthFlag(String modifyNorthFlag) {
		this.modifyNorthFlag = modifyNorthFlag;
	}

	public String getModifyRemark() {
		return modifyRemark;
	}

	public void setModifyRemark(String modifyRemark) {
		this.modifyRemark = modifyRemark;
	}
	
}
