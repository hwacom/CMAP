package com.cmap.plugin.module.port.status.viewer;

import com.cmap.service.vo.CommonServiceVO;

public class PortStatusViewerVO extends CommonServiceVO {

	private String queryGroupId;
	private String queryDeviceId;
	
	private String groupId;
	private String groupName;
	private String deviceId;
	private String deviceName;
	private String portIndex;
	private String portName;
	private String portAdminStatus;
	private String portAdminStatusDesc;
	private String portAdminStatusPresentType;
	private String portOperStatus;
	private String portOperStatusDesc;
	private String portOperStatusPresentType;
	private String speed;
	
	public String getQueryGroupId() {
		return queryGroupId;
	}
	public void setQueryGroupId(String queryGroupId) {
		this.queryGroupId = queryGroupId;
	}
	public String getQueryDeviceId() {
		return queryDeviceId;
	}
	public void setQueryDeviceId(String queryDeviceId) {
		this.queryDeviceId = queryDeviceId;
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
	public String getPortIndex() {
		return portIndex;
	}
	public void setPortIndex(String portIndex) {
		this.portIndex = portIndex;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public String getPortAdminStatus() {
		return portAdminStatus;
	}
	public void setPortAdminStatus(String portAdminStatus) {
		this.portAdminStatus = portAdminStatus;
	}
	public String getPortAdminStatusDesc() {
		return portAdminStatusDesc;
	}
	public void setPortAdminStatusDesc(String portAdminStatusDesc) {
		this.portAdminStatusDesc = portAdminStatusDesc;
	}
	public String getPortAdminStatusPresentType() {
		return portAdminStatusPresentType;
	}
	public void setPortAdminStatusPresentType(String portAdminStatusPresentType) {
		this.portAdminStatusPresentType = portAdminStatusPresentType;
	}
	public String getPortOperStatus() {
		return portOperStatus;
	}
	public void setPortOperStatus(String portOperStatus) {
		this.portOperStatus = portOperStatus;
	}
	public String getPortOperStatusDesc() {
		return portOperStatusDesc;
	}
	public void setPortOperStatusDesc(String portOperStatusDesc) {
		this.portOperStatusDesc = portOperStatusDesc;
	}
	public String getPortOperStatusPresentType() {
		return portOperStatusPresentType;
	}
	public void setPortOperStatusPresentType(String portOperStatusPresentType) {
		this.portOperStatusPresentType = portOperStatusPresentType;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
}
