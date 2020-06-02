package com.cmap.plugin.module.iptracepoller;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		name = "module_ip_trace",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"client_ip", "start_time"})
		}
)
public class ModuleIpTrace {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "data_id", unique = true)
	private Long dataId;
    
	@Column(name = "client_ip", nullable = false)
	private String clientIp;

	@Column(name = "start_time", nullable = false)
    private Date startTime;

	@Column(name = "end_time", nullable = true)
	private Date endTime;

	@Column(name = "client_mac", nullable = false)
	private String clientMac;
	
	@Column(name = "group_id", nullable = false)
	private String groupId;
	
	@Column(name = "group_name", nullable = false)
	private String groupName;
	
	@Column(name = "device_id", nullable = false)
	private String deviceId;
	
	@Column(name = "device_name", nullable = false)
	private String deviceName;
	
	@Column(name = "device_model", nullable = false)
	private String deviceModel;
	
	@Column(name = "port_id", nullable = false)
	private String portId;
	
	@Column(name = "port_name", nullable = false)
	private String portName;

	@Column(name = "create_time", nullable = false)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = false)
	private String createBy;

	@Column(name = "update_time", nullable = false)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = false)
	private String updateBy;

	public ModuleIpTrace() {
		super();
	}
    
	public ModuleIpTrace(Long dataId, String clientIp, Date startTime, Date endTime, String clientMac,
			String groupId, String groupName, String deviceId, String deviceName, String deviceModel, String portId,
			String portName, Timestamp createTime, String createBy, Timestamp updateTime, String updateBy) {
		super();
		this.dataId = dataId;
		this.clientIp = clientIp;
		this.startTime = startTime;
		this.endTime = endTime;
		this.clientMac = clientMac;
		this.groupId = groupId;
		this.groupName = groupName;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.deviceModel = deviceModel;
		this.portId = portId;
		this.portName = portName;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
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

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getPortId() {
		return portId;
	}

	public void setPortId(String portId) {
		this.portId = portId;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
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
