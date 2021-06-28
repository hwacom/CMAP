package com.cmap.plugin.module.alarm.summary;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="module_alarm_summary_log",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"LOG_ID"})
		})
public class ModuleAlarmSummaryLog implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "LOG_ID", unique = true)
    private String logId;

    @Column(name = "ALARM_ID", unique = true)
    private Long alarmId;
    
    @Column(name = "CONTENT", nullable = true)
    private String content;
    
    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "create_by", nullable = false)
    private String createBy;

    public ModuleAlarmSummaryLog() {
        super();
	}

	public ModuleAlarmSummaryLog(String logId, Long alarmId, String content, Timestamp createTime, String createBy) {
		super();
		this.logId = logId;
		this.alarmId = alarmId;
		this.content = content;
		this.createTime = createTime;
		this.createBy = createBy;
	}

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public Long getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(Long alarmId) {
		this.alarmId = alarmId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
