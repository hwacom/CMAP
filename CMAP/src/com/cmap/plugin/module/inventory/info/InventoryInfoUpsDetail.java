package com.cmap.plugin.module.inventory.info;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		name = "module_inventory_info_ups_detail",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"DEVICE_ID"})
		}
		)
public class InventoryInfoUpsDetail {

	@Id
	@Column(name = "DEVICE_ID", unique = true)
	private String deviceId;
	
	@Column(name = "SITE", nullable = true)
	private String site;
	
	@Column(name = "BUILDING", nullable = true)
	private String building;
	
	@Column(name = "KVA", nullable = true)
	private String kva;
	
	@Column(name = "IDC_NAME", nullable = true)
	private String idcName;
	
	@Column(name = "BUY_DATE", nullable = true)
	private String buyDate;
	
	@Column(name = "update_time", nullable = true)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = true)
	private String updateBy;
	
	public InventoryInfoUpsDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InventoryInfoUpsDetail(String deviceId, String site, String building, String kva, String idcName,
			String buyDate, Timestamp updateTime, String updateBy) {
		super();
		this.deviceId = deviceId;
		this.site = site;
		this.building = building;
		this.kva = kva;
		this.idcName = idcName;
		this.buyDate = buyDate;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getKva() {
		return kva;
	}

	public void setKva(String kva) {
		this.kva = kva;
	}

	public String getIdcName() {
		return idcName;
	}

	public void setIdcName(String idcName) {
		this.idcName = idcName;
	}

	public String getBuyDate() {
		return buyDate;
	}

	public void setBuyDate(String buyDate) {
		this.buyDate = buyDate;
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
