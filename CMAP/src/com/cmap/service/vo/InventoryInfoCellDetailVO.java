package com.cmap.service.vo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InventoryInfoCellDetailVO extends CommonServiceVO {
	/*
	 * 查詢使用欄位
	 */
	private String queryDevice;
	private List<String> queryDeviceList = new ArrayList<String>();
	
	private String deviceId;
	private String frequencyBand;
	private String amfIpAddress;
	private String enodebType;
	private String gnbId;
	private String cellIdentify;
	private String physicalCellGroupId;
	private String physicalCellId;
	private String plmn;
	private String arfcn;
	private String bandWidth;
	private String currentTxPower;
	private String modifyFlag;	
	private String remark;
	private Timestamp createTime;
	private String createTimeStr;
	private String createBy;
	private Timestamp updateTime;
	private String updateBy;
		
	public InventoryInfoCellDetailVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InventoryInfoCellDetailVO(String queryDevice, List<String> queryDeviceList, String deviceId,
			String frequencyBand, String amfIpAddress, String enodebType, String gnbId, String cellIdentify,
			String physicalCellGroupId, String physicalCellId, String plmn, String arfcn, String bandWidth,
			String currentTxPower, String modifyFlag, String remark, Timestamp createTime, String createTimeStr,
			String createBy, Timestamp updateTime, String updateBy) {
		super();
		this.queryDevice = queryDevice;
		this.queryDeviceList = queryDeviceList;
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
		this.createTimeStr = createTimeStr;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
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

}
