package com.cmap.plugin.module.inventory.info;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		name = "module_inventory_info_storage_detail",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"DEVICE_ID"})
		}
		)
public class InventoryInfoStorageDetail {

	@Id
	@Column(name = "DEVICE_ID", unique = true)
	private String deviceId;
	
	@Column(name = "IDC_ID", nullable = true)
	private String idcId;
	
	@Column(name = "SITE", nullable = true)
	private String site;
	
	@Column(name = "RACK_ID", nullable = true)
	private String rackId;
	
	@Column(name = "FROM_RACK_UNIT", nullable = true)
	private String fromRackUnit;
	
	@Column(name = "TO_RACK_UNIT", nullable = true)
	private String toRackUnit;
	
	@Column(name = "DESCRIPTIONS", nullable = true)
	private String descriptions;
	
	@Column(name = "CLASS_USE", nullable = true)
	private String classUse;
	
	@Column(name = "STORAGE_TYPE", nullable = true)
	private String storageType;
	
	@Column(name = "PROTOCAL_LINK", nullable = true)
	private String protocalLink;
	
	@Column(name = "CONTROL_FIRMWARE", nullable = true)
	private String controlFirmware;
	
	@Column(name = "ACCET_NUMBER", nullable = true)
	private String accetNumber;
	
	@Column(name = "BUY_DATE", nullable = true)
	private String buyDate;
	
	@Column(name = "TOTAL_SPACE", nullable = true)
	private String totalSpace;
	
	@Column(name = "USED_SPACE", nullable = true)
	private String usedSpace;
	
	@Column(name = "LEFT_SPACE", nullable = true)
	private String leftSpace;
	
	@Column(name = "update_time", nullable = true)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = true)
	private String updateBy;
	
	public InventoryInfoStorageDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InventoryInfoStorageDetail(String deviceId, String idcId, String site, String rackId, String fromRackUnit,
			String toRackUnit, String descriptions, String classUse, String storageType, String protocalLink,
			String controlFirmware, String accetNumber, String buyDate, String totalSpace, String usedSpace,
			String leftSpace, Timestamp updateTime, String updateBy) {
		super();
		this.deviceId = deviceId;
		this.idcId = idcId;
		this.site = site;
		this.rackId = rackId;
		this.fromRackUnit = fromRackUnit;
		this.toRackUnit = toRackUnit;
		this.descriptions = descriptions;
		this.classUse = classUse;
		this.storageType = storageType;
		this.protocalLink = protocalLink;
		this.controlFirmware = controlFirmware;
		this.accetNumber = accetNumber;
		this.buyDate = buyDate;
		this.totalSpace = totalSpace;
		this.usedSpace = usedSpace;
		this.leftSpace = leftSpace;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getIdcId() {
		return idcId;
	}

	public void setIdcId(String idcId) {
		this.idcId = idcId;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getRackId() {
		return rackId;
	}

	public void setRackId(String rackId) {
		this.rackId = rackId;
	}

	public String getFromRackUnit() {
		return fromRackUnit;
	}

	public void setFromRackUnit(String fromRackUnit) {
		this.fromRackUnit = fromRackUnit;
	}

	public String getToRackUnit() {
		return toRackUnit;
	}

	public void setToRackUnit(String toRackUnit) {
		this.toRackUnit = toRackUnit;
	}

	public String getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}

	public String getClassUse() {
		return classUse;
	}

	public void setClassUse(String classUse) {
		this.classUse = classUse;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public String getProtocalLink() {
		return protocalLink;
	}

	public void setProtocalLink(String protocalLink) {
		this.protocalLink = protocalLink;
	}

	public String getControlFirmware() {
		return controlFirmware;
	}

	public void setControlFirmware(String controlFirmware) {
		this.controlFirmware = controlFirmware;
	}

	public String getAccetNumber() {
		return accetNumber;
	}

	public void setAccetNumber(String accetNumber) {
		this.accetNumber = accetNumber;
	}

	public String getBuyDate() {
		return buyDate;
	}

	public void setBuyDate(String buyDate) {
		this.buyDate = buyDate;
	}

	public String getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(String totalSpace) {
		this.totalSpace = totalSpace;
	}

	public String getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(String usedSpace) {
		this.usedSpace = usedSpace;
	}

	public String getLeftSpace() {
		return leftSpace;
	}

	public void setLeftSpace(String leftSpace) {
		this.leftSpace = leftSpace;
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
