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
		name = "mib_value_mapping",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"mapping_id"})
		}
		)
public class MibValueMapping {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "mapping_id", unique = true)
	private String mappingId;

	@Column(name = "oid_table", nullable = false)
	private String oidTable;
	
	@Column(name = "oid_name", nullable = false)
	private String oidName;

	@Column(name = "entry_value", nullable = false)
	private String entryValue;

	@Column(name = "entry_value_desc", nullable = false)
	private String entryValueDesc;
	
	@Column(name = "ui_present_type", nullable = false)
	private String uiPresentType;

	@Column(name = "remark", nullable = true)
	private String remark;

	@Column(name = "create_time", nullable = true)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = true)
	private String createBy;

	@Column(name = "update_time", nullable = true)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = true)
	private String updateBy;

	public MibValueMapping() {
		super();
	}

	public MibValueMapping(String mappingId, String oidTable, String oidName, String entryValue, String entryValueDesc,
			String uiPresentType, String remark, Timestamp createTime, String createBy, Timestamp updateTime,
			String updateBy) {
		super();
		this.mappingId = mappingId;
		this.oidTable = oidTable;
		this.oidName = oidName;
		this.entryValue = entryValue;
		this.entryValueDesc = entryValueDesc;
		this.uiPresentType = uiPresentType;
		this.remark = remark;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}

	public String getOidTable() {
		return oidTable;
	}

	public void setOidTable(String oidTable) {
		this.oidTable = oidTable;
	}

	public String getOidName() {
		return oidName;
	}

	public void setOidName(String oidName) {
		this.oidName = oidName;
	}

	public String getEntryValue() {
		return entryValue;
	}

	public void setEntryValue(String entryValue) {
		this.entryValue = entryValue;
	}

	public String getEntryValueDesc() {
		return entryValueDesc;
	}

	public void setEntryValueDesc(String entryValueDesc) {
		this.entryValueDesc = entryValueDesc;
	}

	public String getUiPresentType() {
		return uiPresentType;
	}

	public void setUiPresentType(String uiPresentType) {
		this.uiPresentType = uiPresentType;
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
