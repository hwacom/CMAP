package com.cmap.service.vo;

import java.sql.Timestamp;

public class InventoryInfoVO extends CommonServiceVO {
	/*
	 * 查詢使用欄位
	 */
	private String queryProbe;
	private String queryDeviceName;
	private String queryDeviceType;
	private String queryBrand;
	private String queryModel;
	
	private String deviceId;
	private String probe;
	private String group;
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
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
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
}
