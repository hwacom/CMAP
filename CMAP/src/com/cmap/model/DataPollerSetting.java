package com.cmap.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		name = "data_poller_setting",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"setting_id"})
		}
		)
public class DataPollerSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "setting_id", unique = true)
	private String settingId;

	@Column(name = "data_type", nullable = false)
	private String dataType;

	@Column(name = "method", nullable = false)
	private String method;

	@Column(name = "special_var_setting", nullable = true)
	private String specialVarSetting;

	@Column(name = "file_path", nullable = false)
	private String filePath;

	@Column(name = "file_name_regex", nullable = true)
	private String fileNameRegex;

	@Column(name = "file_name_format", nullable = true)
	private String fileNameFormat;

	@Column(name = "file_charset", nullable = true)
	private String fileCharset;

	@Column(name = "source_ip", nullable = false)
	private String sourceIp;

	@Column(name = "source_port", nullable = false)
	private String sourcePort;

	@Column(name = "login_account", nullable = false)
	private String loginAccount;

	@Column(name = "login_password", nullable = false)
	private String loginPassword;

	@Column(name = "split_symbol", nullable = false)
	private String splitSymbol;

	@Column(name = "record_by_day", nullable = false)
	private String recordByDay;

	@Column(name = "record_by_day_interval", nullable = true)
	private String recordByDayInterval;

	@Column(name = "record_by_day_clean", nullable = true)
	private String recordByDayClean;

	@Column(name = "record_by_day_refer_field", nullable = true)
	private String recordByDayReferField;

	@Column(name = "delete_source_file", nullable = false)
	private String deleteSourceFile;

	@Column(name = "backup_source_file", nullable = false)
	private String backupSourceFile;

	@Column(name = "backup_file_path", nullable = true)
	private String backupFilePath;

	@Column(name = "delete_flag", nullable = true)
	private String deleteFlag;

	@Column(name = "delete_time", nullable = true)
	private Timestamp deleteTime;

	@Column(name = "delete_by", nullable = true)
	private String deleteBy;

	@Column(name = "create_time", nullable = false)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = false)
	private String createBy;

	@Column(name = "update_time", nullable = false)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = false)
	private String updateBy;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dataPollerSetting")
	private List<DataPollerMapping> dataPollerMappings;

	public DataPollerSetting() {
		super();
	}

	public DataPollerSetting(String settingId, String dataType, String method, String specialVarSetting,
			String filePath, String fileNameRegex, String fileNameFormat, String fileCharset, String sourceIp,
			String sourcePort, String loginAccount, String loginPassword, String splitSymbol, String recordByDay,
			String recordByDayInterval, String recordByDayClean, String recordByDayReferField, String deleteSourceFile,
			String backupSourceFile, String backupFilePath, String deleteFlag, Timestamp deleteTime, String deleteBy,
			Timestamp createTime, String createBy, Timestamp updateTime, String updateBy,
			List<DataPollerMapping> dataPollerMappings) {
		super();
		this.settingId = settingId;
		this.dataType = dataType;
		this.method = method;
		this.specialVarSetting = specialVarSetting;
		this.filePath = filePath;
		this.fileNameRegex = fileNameRegex;
		this.fileNameFormat = fileNameFormat;
		this.fileCharset = fileCharset;
		this.sourceIp = sourceIp;
		this.sourcePort = sourcePort;
		this.loginAccount = loginAccount;
		this.loginPassword = loginPassword;
		this.splitSymbol = splitSymbol;
		this.recordByDay = recordByDay;
		this.recordByDayInterval = recordByDayInterval;
		this.recordByDayClean = recordByDayClean;
		this.recordByDayReferField = recordByDayReferField;
		this.deleteSourceFile = deleteSourceFile;
		this.backupSourceFile = backupSourceFile;
		this.backupFilePath = backupFilePath;
		this.deleteFlag = deleteFlag;
		this.deleteTime = deleteTime;
		this.deleteBy = deleteBy;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
		this.dataPollerMappings = dataPollerMappings;
	}

	public String getSettingId() {
		return settingId;
	}

	public void setSettingId(String settingId) {
		this.settingId = settingId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getSpecialVarSetting() {
		return specialVarSetting;
	}

	public void setSpecialVarSetting(String specialVarSetting) {
		this.specialVarSetting = specialVarSetting;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileNameRegex() {
		return fileNameRegex;
	}

	public void setFileNameRegex(String fileNameRegex) {
		this.fileNameRegex = fileNameRegex;
	}

	public String getFileNameFormat() {
		return fileNameFormat;
	}

	public void setFileNameFormat(String fileNameFormat) {
		this.fileNameFormat = fileNameFormat;
	}

	public String getFileCharset() {
		return fileCharset;
	}

	public void setFileCharset(String fileCharset) {
		this.fileCharset = fileCharset;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getLoginAccount() {
		return loginAccount;
	}

	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getSplitSymbol() {
		return splitSymbol;
	}

	public void setSplitSymbol(String splitSymbol) {
		this.splitSymbol = splitSymbol;
	}

	public String getRecordByDay() {
		return recordByDay;
	}

	public void setRecordByDay(String recordByDay) {
		this.recordByDay = recordByDay;
	}

	public String getRecordByDayInterval() {
		return recordByDayInterval;
	}

	public void setRecordByDayInterval(String recordByDayInterval) {
		this.recordByDayInterval = recordByDayInterval;
	}

	public String getRecordByDayClean() {
		return recordByDayClean;
	}

	public void setRecordByDayClean(String recordByDayClean) {
		this.recordByDayClean = recordByDayClean;
	}

	public String getRecordByDayReferField() {
		return recordByDayReferField;
	}

	public void setRecordByDayReferField(String recordByDayReferField) {
		this.recordByDayReferField = recordByDayReferField;
	}

	public String getDeleteSourceFile() {
		return deleteSourceFile;
	}

	public void setDeleteSourceFile(String deleteSourceFile) {
		this.deleteSourceFile = deleteSourceFile;
	}

	public String getBackupSourceFile() {
		return backupSourceFile;
	}

	public void setBackupSourceFile(String backupSourceFile) {
		this.backupSourceFile = backupSourceFile;
	}

	public String getBackupFilePath() {
		return backupFilePath;
	}

	public void setBackupFilePath(String backupFilePath) {
		this.backupFilePath = backupFilePath;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Timestamp getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Timestamp deleteTime) {
		this.deleteTime = deleteTime;
	}

	public String getDeleteBy() {
		return deleteBy;
	}

	public void setDeleteBy(String deleteBy) {
		this.deleteBy = deleteBy;
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

	public List<DataPollerMapping> getDataPollerMappings() {
		return dataPollerMappings;
	}

	public void setDataPollerMappings(List<DataPollerMapping> dataPollerMappings) {
		this.dataPollerMappings = dataPollerMappings;
	}
}
