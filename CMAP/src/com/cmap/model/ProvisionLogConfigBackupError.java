package com.cmap.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(
		name = "provision_log_config_backup_error",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"LOG_ERROR_ID"})
		}
		)
public class ProvisionLogConfigBackupError {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "LOG_ERROR_ID", unique = true)
	private String logErrorId;

	@Column(name = "log_master_id", nullable = false)
	private String logMasterId;
	
	@Column(name = "device_id", nullable = false)
	private String deviceId;

	@Column(name = "device_name", nullable = true)
	private String deviceName;

	@Column(name = "device_ip", nullable = true)
	private String deviceIp;

	@Column(name = "device_model", nullable = true)
    private String deviceModel;
	
	@Column(name = "result", nullable = false)
	private String result;

	@Column(name = "message", nullable = true)
	private String message;

	@Column(name = "script_code", nullable = false)
	private String scriptCode;

	@Column(name = "remark", nullable = true)
	private String remark;
	
	@Column(name = "begin_time", nullable = false)
	private Timestamp beginTime;

	@Column(name = "end_time", nullable = false)
	private Timestamp endTime;

	@Column(name = "spend_time_in_seconds", nullable = false)
	private Integer spendTimeInSeconds;

	@Column(name = "retry_times", nullable = false)
	private Integer retryTimes;

	@Column(name = "process_log", nullable = true)
	private String processLog;

	@Column(name = "create_time", nullable = false)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = false)
	private String createBy;

	public ProvisionLogConfigBackupError() {
		super();
	}

	public ProvisionLogConfigBackupError(String logErrorId, String logMasterId, String deviceId, String deviceName,
			String deviceIp, String deviceModel, String result, String message, String scriptCode, String remark,
			Timestamp beginTime, Timestamp endTime, Integer spendTimeInSeconds, Integer retryTimes, String processLog,
			Timestamp createTime, String createBy) {
		super();
		this.logErrorId = logErrorId;
		this.logMasterId = logMasterId;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.deviceIp = deviceIp;
		this.deviceModel = deviceModel;
		this.result = result;
		this.message = message;
		this.scriptCode = scriptCode;
		this.remark = remark;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.spendTimeInSeconds = spendTimeInSeconds;
		this.retryTimes = retryTimes;
		this.processLog = processLog;
		this.createTime = createTime;
		this.createBy = createBy;
	}

	public String getLogErrorId() {
		return logErrorId;
	}

	public void setLogErrorId(String logErrorId) {
		this.logErrorId = logErrorId;
	}

	public String getLogMasterId() {
		return logMasterId;
	}

	public void setLogMasterId(String logMasterId) {
		this.logMasterId = logMasterId;
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

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Timestamp getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Timestamp beginTime) {
		this.beginTime = beginTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Integer getSpendTimeInSeconds() {
		return spendTimeInSeconds;
	}

	public void setSpendTimeInSeconds(Integer spendTimeInSeconds) {
		this.spendTimeInSeconds = spendTimeInSeconds;
	}

	public Integer getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(Integer retryTimes) {
		this.retryTimes = retryTimes;
	}

	public String getProcessLog() {
		return processLog;
	}

	public void setProcessLog(String processLog) {
		this.processLog = processLog;
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
