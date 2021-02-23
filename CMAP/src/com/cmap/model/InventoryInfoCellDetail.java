package com.cmap.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		name = "inventory_info_cell_detail",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"DEVICE_ID"})
		}
		)
public class InventoryInfoCellDetail {

	@Id
	@Column(name = "DEVICE_ID", unique = true)
	private String deviceId;

	@Column(name = "FREQUENCY_BAND", nullable = true)
	private String frequencyBand;

	@Column(name = "AMF_IP_ADDRESS", nullable = true)
	private String amfIpAddress;

	@Column(name = "ENODEB_TYPE", nullable = true)
	private String enodebType;

	@Column(name = "GNB_ID", nullable = true)
	private String gnbId;

	@Column(name = "CELL_IDENTITY", nullable = true)
	private String cellIdentify;

	@Column(name = "PHYSICAL_CELL_GROUP_ID", nullable = true)
	private String physicalCellGroupId;

	@Column(name = "PHYSICAL_CELL_ID", nullable = true)
	private String physicalCellId;
	
	@Column(name = "PLMN", nullable = true)
	private String plmn;
	
	@Column(name = "ARFCN", nullable = true)
	private String arfcn;
	
	@Column(name = "BAND_WIDTH", nullable = true)
	private String bandWidth;

	@Column(name = "CURRENT_TX_POWER", nullable = true)
	private String currentTxPower;
	
	@Column(name = "MODIFY_FLAG", nullable = true)
	private String modifyFlag;
	
	@Column(name = "REMARK", nullable = true)
	private String remark;
	
	@Column(name = "create_time", nullable = true)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = true)
	private String createBy;

	@Column(name = "update_time", nullable = true)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = true)
	private String updateBy;
	
	public InventoryInfoCellDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InventoryInfoCellDetail(String deviceId, String frequencyBand, String amfIpAddress, String enodebType,
			String gnbId, String cellIdentify, String physicalCellGroupId, String physicalCellId, String plmn,
			String arfcn, String bandWidth, String currentTxPower, String modifyFlag, String remark,
			Timestamp createTime, String createBy, Timestamp updateTime, String updateBy) {
		super();
		this.deviceId = deviceId;
		this.frequencyBand = frequencyBand;
		this.amfIpAddress = amfIpAddress;
		this.enodebType = enodebType;
		this.gnbId = gnbId;
		this.cellIdentify = cellIdentify;
		this.physicalCellGroupId = physicalCellGroupId;
		this.physicalCellId = physicalCellId;
		this.plmn = plmn;
		this.arfcn = arfcn;
		this.bandWidth = bandWidth;
		this.currentTxPower = currentTxPower;
		this.modifyFlag = modifyFlag;
		this.remark = remark;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getFrequencyBand() {
		return frequencyBand;
	}

	public void setFrequencyBand(String frequencyBand) {
		this.frequencyBand = frequencyBand;
	}

	public String getAmfIpAddress() {
		return amfIpAddress;
	}

	public void setAmfIpAddress(String amfIpAddress) {
		this.amfIpAddress = amfIpAddress;
	}

	public String getEnodebType() {
		return enodebType;
	}

	public void setEnodebType(String enodebType) {
		this.enodebType = enodebType;
	}

	public String getGnbId() {
		return gnbId;
	}

	public void setGnbId(String gnbId) {
		this.gnbId = gnbId;
	}

	public String getCellIdentify() {
		return cellIdentify;
	}

	public void setCellIdentify(String cellIdentify) {
		this.cellIdentify = cellIdentify;
	}

	public String getPhysicalCellGroupId() {
		return physicalCellGroupId;
	}

	public void setPhysicalCellGroupId(String physicalCellGroupId) {
		this.physicalCellGroupId = physicalCellGroupId;
	}

	public String getPhysicalCellId() {
		return physicalCellId;
	}

	public void setPhysicalCellId(String physicalCellId) {
		this.physicalCellId = physicalCellId;
	}

	public String getPlmn() {
		return plmn;
	}

	public void setPlmn(String plmn) {
		this.plmn = plmn;
	}

	public String getArfcn() {
		return arfcn;
	}

	public void setArfcn(String arfcn) {
		this.arfcn = arfcn;
	}

	public String getBandWidth() {
		return bandWidth;
	}

	public void setBandWidth(String bandWidth) {
		this.bandWidth = bandWidth;
	}

	public String getCurrentTxPower() {
		return currentTxPower;
	}

	public void setCurrentTxPower(String currentTxPower) {
		this.currentTxPower = currentTxPower;
	}

	public String getModifyFlag() {
		return modifyFlag;
	}

	public void setModifyFlag(String modifyFlag) {
		this.modifyFlag = modifyFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
