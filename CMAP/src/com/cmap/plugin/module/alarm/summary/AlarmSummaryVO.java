package com.cmap.plugin.module.alarm.summary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.cmap.service.vo.CommonServiceVO;

public class AlarmSummaryVO extends CommonServiceVO {

    private Long queryAlarmId;
    private String querySensorType;
    private String queryStatus;
    private List<String> queryDataStatus = new ArrayList<String>();
    private String queryMessage;
    private String queryDateBegin;
	private String queryDateEnd;
	private String queryTimeBegin;
	private String queryTimeEnd;
	
	private Long alarmId;
	private String groupId;
	private String groupName;
	private String deviceId;
	private String deviceName;
	private String sensorId;
	private String sensorName;
	private String sensorType;
    private String alarmDataStatus;
    private String alarmStatus;
    private Timestamp alarmTime;
    private String alarmTimeStr;
    private Timestamp closeTime;
    private String closeTimeStr;
    private String lastValue;
    private String message;
    private String priority;
    private String remark;
    private String listId;
    private Timestamp updateTime;
    private String updateTimeStr;
    private String updateBy;
    
	public Long getQueryAlarmId() {
		return queryAlarmId;
	}
	public void setQueryAlarmId(Long queryAlarmId) {
		this.queryAlarmId = queryAlarmId;
	}
	public String getQuerySensorType() {
		return querySensorType;
	}
	public void setQuerySensorType(String querySensorType) {
		this.querySensorType = querySensorType;
	}
	public String getQueryStatus() {
		return queryStatus;
	}
	public void setQueryStatus(String queryStatus) {
		this.queryStatus = queryStatus;
	}
	public List<String> getQueryDataStatus() {
		return queryDataStatus;
	}
	public void setQueryDataStatus(List<String> queryDataStatus) {
		this.queryDataStatus = queryDataStatus;
	}
	public String getQueryMessage() {
		return queryMessage;
	}
	public void setQueryMessage(String queryMessage) {
		this.queryMessage = queryMessage;
	}
	public String getQueryDateBegin() {
		return queryDateBegin;
	}
	public void setQueryDateBegin(String queryDateBegin) {
		this.queryDateBegin = queryDateBegin;
	}
	public String getQueryDateEnd() {
		return queryDateEnd;
	}
	public void setQueryDateEnd(String queryDateEnd) {
		this.queryDateEnd = queryDateEnd;
	}
	public String getQueryTimeBegin() {
		return queryTimeBegin;
	}
	public void setQueryTimeBegin(String queryTimeBegin) {
		this.queryTimeBegin = queryTimeBegin;
	}
	public String getQueryTimeEnd() {
		return queryTimeEnd;
	}
	public void setQueryTimeEnd(String queryTimeEnd) {
		this.queryTimeEnd = queryTimeEnd;
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
	public String getAlarmTimeStr() {
		return alarmTimeStr;
	}
	public void setAlarmTimeStr(String alarmTimeStr) {
		this.alarmTimeStr = alarmTimeStr;
	}
	public Timestamp getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(Timestamp closeTime) {
		this.closeTime = closeTime;
	}
	public String getCloseTimeStr() {
		return closeTimeStr;
	}
	public void setCloseTimeStr(String closeTimeStr) {
		this.closeTimeStr = closeTimeStr;
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
	public String getListId() {
		return listId;
	}
	public void setListId(String listId) {
		this.listId = listId;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateTimeStr() {
		return updateTimeStr;
	}
	public void setUpdateTimeStr(String updateTimeStr) {
		this.updateTimeStr = updateTimeStr;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
}
