package com.cmap.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
	name = "prtg_account_mapping",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"id"})
	}
)
public class PrtgAccountMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true)
	private String id;

	@Column(name = "source_id", nullable = false)
	private String sourceId;
	
	@Column(name = "prtg_account", nullable = false)
	private String prtgAccount;
	
	@Column(name = "prtg_password", nullable = false)
	private String prtgPassword;
	
	@Column(name = "remark", nullable = true)
	private String remark;
	
	@Column(name = "create_time", nullable = false)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = false)
	private String createBy;

	public PrtgAccountMapping() {
		super();
	}

	public PrtgAccountMapping(String id, String sourceId, String prtgAccount, String prtgPassword, String remark,
			Timestamp createTime, String createBy) {
		super();
		this.id = id;
		this.sourceId = sourceId;
		this.prtgAccount = prtgAccount;
		this.prtgPassword = prtgPassword;
		this.remark = remark;
		this.createTime = createTime;
		this.createBy = createBy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getPrtgAccount() {
		return prtgAccount;
	}

	public void setPrtgAccount(String prtgAccount) {
		this.prtgAccount = prtgAccount;
	}

	public String getPrtgPassword() {
		return prtgPassword;
	}

	public void setPrtgPassword(String prtgPassword) {
		this.prtgPassword = prtgPassword;
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

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
}
