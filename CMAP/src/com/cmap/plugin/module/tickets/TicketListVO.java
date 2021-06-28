package com.cmap.plugin.module.tickets;

import com.cmap.service.vo.CommonServiceVO;

public class TicketListVO extends CommonServiceVO {

    private String queryListId;
    private String queryStatus;
    private String queryOwnerType;
    private String queryOwner;
    private String queryPriority;
    private String queryDateBegin;
	private String queryDateEnd;
	private String queryTimeBegin;
	private String queryTimeEnd;
	
    private String listId;
    private Long alarmId;
    private String status;
    private String ownerType;
    private String owner;
    private String ownerStr;
    private String subject;
    private String message;
    private String priority;
    private String remark;
    private String mailFlag;
    private String execFlag;
    private String updateTimeStr;
    private String updateBy;
    
	public String getQueryListId() {
		return queryListId;
	}
	public void setQueryListId(String queryListId) {
		this.queryListId = queryListId;
	}
	public String getQueryStatus() {
		return queryStatus;
	}
	public void setQueryStatus(String queryStatus) {
		this.queryStatus = queryStatus;
	}
	public String getQueryOwnerType() {
		return queryOwnerType;
	}
	public void setQueryOwnerType(String queryOwnerType) {
		this.queryOwnerType = queryOwnerType;
	}
	public String getQueryOwner() {
		return queryOwner;
	}
	public void setQueryOwner(String queryOwner) {
		this.queryOwner = queryOwner;
	}
	public String getQueryPriority() {
		return queryPriority;
	}
	public void setQueryPriority(String queryPriority) {
		this.queryPriority = queryPriority;
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
	public String getListId() {
		return listId;
	}
	public void setListId(String listId) {
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
	public String getOwnerStr() {
		return ownerStr;
	}
	public void setOwnerStr(String ownerStr) {
		this.ownerStr = ownerStr;
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
