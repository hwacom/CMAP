package com.cmap.plugin.module.tickets;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="module_ticket_list",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"list_id"})
		})
public class ModuleTicketList implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id", unique = true)
    private Long listId;

    @Column(name = "ALARM_ID", unique = true)
    private Long alarmId;
    
    @Column(name = "STATUS", nullable = false)
    private String status;
    
    @Column(name = "OWNER_TYPE", nullable = false)
    private String ownerType;

    @Column(name = "OWNER", nullable = false)
    private String owner;

    @Column(name = "SUBJECT", nullable = false)
    private String subject;
    
    @Column(name = "MESSAGE", nullable = false)
    private String message;
    
    @Column(name = "PRIORITY", nullable = false)
    private String priority;
    
    @Column(name = "remark", nullable = true)
    private String remark;
    
    @Column(name = "MAIL_FLAG", nullable = false)
    private String mailFlag;
    
    @Column(name = "EXEC_FLAG", nullable = false)
    private String execFlag;
    
    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "create_by", nullable = false)
    private String createBy;

    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

    @Column(name = "update_by", nullable = false)
    private String updateBy;

    public ModuleTicketList() {
        super();
    }

	public ModuleTicketList(Long listId, Long alarmId, String status, String ownerType, String owner, String subject,
			String message, String priority, String remark, String mailFlag, String execFlag, Timestamp createTime,
			String createBy, Timestamp updateTime, String updateBy) {
		super();
		this.listId = listId;
		this.alarmId = alarmId;
		this.status = status;
		this.ownerType = ownerType;
		this.owner = owner;
		this.subject = subject;
		this.message = message;
		this.priority = priority;
		this.remark = remark;
		this.mailFlag = mailFlag;
		this.execFlag = execFlag;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	public Long getListId() {
		return listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}
	
	public Long getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(Long alarmId) {
		this.alarmId = alarmId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public String getMailFlag() {
		return mailFlag;
	}

	public void setMailFlag(String mailFlag) {
		this.mailFlag = mailFlag;
	}

	public String getExecFlag() {
		return execFlag;
	}

	public void setExecFlag(String execFlag) {
		this.execFlag = execFlag;
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
