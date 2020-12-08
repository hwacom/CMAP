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
	private String queryBrand;
	private String queryModel;
	private boolean queryModifyOnly;
	
	private String deviceId;
	private String probe;
	private String groupName;
	private String deviceName;
	private String deviceIp;
	private String deviceType;
	private String brand;
	private String model;
	private String systemVersion;
	private String serialNumber;
	private String manufactureDate;
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
	
	public InventoryInfoVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InventoryInfoVO(String queryDevice, List<String> queryDeviceList, String queryProbe, String queryDeviceName,
			String queryDeviceType, String queryGroupName, String queryBrand, String queryModel,
			boolean queryModifyOnly, String deviceId, String probe, String groupName, String deviceName,
			String deviceIp, String deviceType, String brand, String model, String systemVersion, String serialNumber,
			String manufactureDate, Timestamp createTime, String createTimeStr, String createBy, Timestamp updateTime,
			String updateBy, String modifyProbe, String modifyGroup, String modifyDeviceName, String modifyDeviceIp,
			String modifyDeviceType, String modifyBrand, String modifyModel, String modifySystemVersion,
			String modifySerialNumber, String modifyManufactureDate) {
		super();
		this.queryDevice = queryDevice;
		this.queryDeviceList = queryDeviceList;
		this.queryProbe = queryProbe;
		this.queryDeviceName = queryDeviceName;
		this.queryDeviceType = queryDeviceType;
		this.queryGroupName = queryGroupName;
		this.queryBrand = queryBrand;
		this.queryModel = queryModel;
		this.queryModifyOnly = queryModifyOnly;
		this.deviceId = deviceId;
		this.probe = probe;
		this.groupName = groupName;
		this.deviceName = deviceName;
		this.deviceIp = deviceIp;
		this.deviceType = deviceType;
		this.brand = brand;
		this.model = model;
		this.systemVersion = systemVersion;
		this.serialNumber = serialNumber;
		this.manufactureDate = manufactureDate;
		this.createTime = createTime;
		this.createTimeStr = createTimeStr;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
		this.modifyProbe = modifyProbe;
		this.modifyGroup = modifyGroup;
		this.modifyDeviceName = modifyDeviceName;
		this.modifyDeviceIp = modifyDeviceIp;
		this.modifyDeviceType = modifyDeviceType;
		this.modifyBrand = modifyBrand;
		this.modifyModel = modifyModel;
		this.modifySystemVersion = modifySystemVersion;
		this.modifySerialNumber = modifySerialNumber;
		this.modifyManufactureDate = modifyManufactureDate;
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

	public String getQueryBrand() {
		return queryBrand;
	}

	public void setQueryBrand(String queryBrand) {
		this.queryBrand = queryBrand;
	}

	public String getQueryModel() {
		return queryModel;
	}

	public void setQueryModel(String queryModel) {
		this.queryModel = queryModel;
	}

	public boolean isQueryModifyOnly() {
		return queryModifyOnly;
	}

	public void setQueryModifyOnly(boolean queryModifyOnly) {
		this.queryModifyOnly = queryModifyOnly;
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

}
