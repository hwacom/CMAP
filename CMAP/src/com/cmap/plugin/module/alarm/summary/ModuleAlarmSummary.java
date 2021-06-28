package com.cmap.plugin.module.alarm.summary;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="module_alarm_summary",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"ALARM_ID"})
		})
public class ModuleAlarmSummary implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALARM_ID", unique = true)
    private Long alarmId;

    @Column(name = "group_id", nullable = false)
	private String groupId;

	@Column(name = "group_name", nullable = true)
	private String groupName;

	@Column(name = "device_id", nullable = false)
	private String deviceId;

	@Column(name = "device_name", nullable = true)
	private String deviceName;

	@Column(name = "sensor_id", nullable = false)
	private String sensorId;

	@Column(name = "sensor_name", nullable = true)
	private String sensorName;
	
	@Column(name = "sensor_type", nullable = true)
	private String sensorType;
	
    @Column(name = "ALARM_DATA_STATUS", nullable = false)
    private String alarmDataStatus;
    
    @Column(name = "ALARM_STATUS", nullable = false)
    private String alarmStatus;
    
    @Column(name = "alarm_time", nullable = false)
    private Timestamp alarmTime;
    
    @Column(name = "close_time", nullable = false)
    private Timestamp closeTime;
    
    @Column(name = "last_Value", nullable = false)
    private String lastValue;
    
    @Column(name = "MESSAGE", nullable = false)
    private String message;
    
    @Column(name = "PRIORITY", nullable = false)
    private String priority;
    
    @Column(name = "remark", nullable = true)
    private String remark;
    
    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

    @Column(name = "update_by", nullable = false)
    private String updateBy;

    public ModuleAlarmSummary() {
        super();
    }

	public ModuleAlarmSummary(Long alarmId, String groupId, String groupName, String deviceId, String deviceName,
			String sensorId, String sensorName, String sensorType, String alarmDataStatus, String alarmStatus,
			Timestamp alarmTime, Timestamp closeTime, String lastValue, String message, String priority, String remark,
			Timestamp updateTime, String updateBy) {
		super();
		this.alarmId = alarmId;
		this.groupId = groupId;
		this.groupName = groupName;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.sensorId = sensorId;
		this.sensorName = sensorName;
		this.sensorType = sensorType;
		this.alarmDataStatus = alarmDataStatus;
		this.alarmStatus = alarmStatus;
		this.alarmTime = alarmTime;
		this.closeTime = closeTime;
		this.lastValue = lastValue;
		this.message = message;
		this.priority = priority;
		this.remark = remark;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	public Long getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(Long alarmId) {
		this.alarmId = alarmId;
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

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public String getAlarmDataStatus() {
		return alarmDataStatus;
	}

	public void setAlarmDataStatus(String alarmDataStatus) {
		this.alarmDataStatus = alarmDataStatus;
	}

	public String getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(String alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public Timestamp getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(Timestamp alarmTime) {
		this.alarmTime = alarmTime;
	}

	public Timestamp getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Timestamp closeTime) {
		this.closeTime = closeTime;
	}

	public String getLastValue() {
		return lastValue;
	}

	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
