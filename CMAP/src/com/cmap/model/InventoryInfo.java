package com.cmap.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		name = "inventory_info",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"DEVICE_ID"})
		}
		)
public class InventoryInfo {

	@Id
	@Column(name = "DEVICE_ID", unique = true)
	private String deviceId;

	@Column(name = "PROBE", nullable = false)
	private String probe;

	@Column(name = "GROUP", nullable = false)
	private String group;

	@Column(name = "DEVICE_NAME", nullable = false)
	private String deviceName;

	@Column(name = "DEVICE_IP", nullable = false)
	private String deviceIp;

	@Column(name = "DEIVCE_TYPE", nullable = true)
	private String deviceType;

	@Column(name = "BRAND", nullable = true)
	private String brand;

	@Column(name = "MODEL", nullable = true)
	private String model;
	
	@Column(name = "SYSTEM_VERSION", nullable = true)
	private String systemVersion;
	
	@Column(name = "SERIAL_NUMBER", nullable = true)
	private String serialNumber;

	@Column(name = "MANUFACTURE_DATE", nullable = true)
	private String manufactureDate;
	
	@Column(name = "create_time", nullable = true)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = true)
	private String createBy;

	public InventoryInfo() {

	}

	public InventoryInfo(String deviceId, String probe, String group, String deviceName, String deviceIp,
			String deviceType, String brand, String model, String systemVersion, String serialNumber,
			String manufactureDate, Timestamp createTime, String createBy) {
		super();
		this.deviceId = deviceId;
		this.probe = probe;
		this.group = group;
		this.deviceName = deviceName;
		this.deviceIp = deviceIp;
		this.deviceType = deviceType;
		this.brand = brand;
		this.model = model;
		this.systemVersion = systemVersion;
		this.serialNumber = serialNumber;
		this.manufactureDate = manufactureDate;
		this.createTime = createTime;
		this.createBy = createBy;
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

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
}
