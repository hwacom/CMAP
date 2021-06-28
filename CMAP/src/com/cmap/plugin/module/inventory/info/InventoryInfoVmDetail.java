package com.cmap.plugin.module.inventory.info;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		name = "module_inventory_info_vm_detail",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"DEVICE_ID"})
		}
		)
public class InventoryInfoVmDetail {

	@Id
	@Column(name = "DEVICE_ID", unique = true)
	private String deviceId;
	
	@Column(name = "SITE", nullable = true)
	private String site;
	
	@Column(name = "SITE_MOD_RSN", nullable = true)
	private String siteModRsn;
		
	@Column(name = "CLASS_USE", nullable = true)
	private String classUse;
	
	@Column(name = "CLASSUSE_MOD_RSN", nullable = true)
	private String classUseModRsn;
	
	@Column(name = "CLASS_PRIMARY", nullable = true)
	private String classPrimary;
	
	@Column(name = "ISDMZ", nullable = true)
	private String isdmz;
		
	@Column(name = "OS_NAME", nullable = true)
	private String osName;
	
	@Column(name = "OS_VERSION", nullable = true)
	private String osVersion;
	
	@Column(name = "OS_BIT", nullable = true)
	private String osBit;
	
	@Column(name = "CPU_AMOUNT", nullable = true)
	private String cpuAmount;
	
	@Column(name = "MEMORY_SIZE", nullable = true)
	private String memorySize;
	
	@Column(name = "HD_SPECIFICATION", nullable = true)
	private String hdSpecification;
	
	@Column(name = "TAPE_SPECIFICATION", nullable = true)
	private String tapeSpecification;
	
	@Column(name = "HD_SIZE", nullable = true)
	private String hdSize;
	
	@Column(name = "PUBLIC_LAN_IP", nullable = true)
	private String publicLanIp;
	
	@Column(name = "BACKUP_LAN_IP", nullable = true)
	private String backupLanIp;
		
	@Column(name = "MANAGER", nullable = true)
	private String manager;
	
	@Column(name = "MANAGER2", nullable = true)
	private String manager2;
	
	@Column(name = "SERVICE_TYPE", nullable = true)
	private String serviceType;
	
	@Column(name = "BUILDING", nullable = true)
	private String building;
	
	@Column(name = "HYPERVISOR", nullable = true)
	private String hypervisor;
	
	@Column(name = "update_time", nullable = true)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = true)
	private String updateBy;
	
	public InventoryInfoVmDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InventoryInfoVmDetail(String deviceId, String site, String siteModRsn, String classUse,
			String classUseModRsn, String classPrimary, String isdmz, String osName, String osVersion, String osBit,
			String cpuAmount, String memorySize, String hdSpecification, String tapeSpecification, String hdSize,
			String publicLanIp, String backupLanIp, String manager, String manager2, String serviceType,
			String building, String hypervisor, Timestamp updateTime, String updateBy) {
		super();
		this.deviceId = deviceId;
		this.site = site;
		this.siteModRsn = siteModRsn;
		this.classUse = classUse;
		this.classUseModRsn = classUseModRsn;
		this.classPrimary = classPrimary;
		this.isdmz = isdmz;
		this.osName = osName;
		this.osVersion = osVersion;
		this.osBit = osBit;
		this.cpuAmount = cpuAmount;
		this.memorySize = memorySize;
		this.hdSpecification = hdSpecification;
		this.tapeSpecification = tapeSpecification;
		this.hdSize = hdSize;
		this.publicLanIp = publicLanIp;
		this.backupLanIp = backupLanIp;
		this.manager = manager;
		this.manager2 = manager2;
		this.serviceType = serviceType;
		this.building = building;
		this.hypervisor = hypervisor;
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

	public String getSiteModRsn() {
		return siteModRsn;
	}

	public void setSiteModRsn(String siteModRsn) {
		this.siteModRsn = siteModRsn;
	}

	public String getClassUse() {
		return classUse;
	}

	public void setClassUse(String classUse) {
		this.classUse = classUse;
	}

	public String getClassUseModRsn() {
		return classUseModRsn;
	}

	public void setClassUseModRsn(String classUseModRsn) {
		this.classUseModRsn = classUseModRsn;
	}

	public String getClassPrimary() {
		return classPrimary;
	}

	public void setClassPrimary(String classPrimary) {
		this.classPrimary = classPrimary;
	}

	public String getIsdmz() {
		return isdmz;
	}

	public void setIsdmz(String isdmz) {
		this.isdmz = isdmz;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getOsBit() {
		return osBit;
	}

	public void setOsBit(String osBit) {
		this.osBit = osBit;
	}

	public String getCpuAmount() {
		return cpuAmount;
	}

	public void setCpuAmount(String cpuAmount) {
		this.cpuAmount = cpuAmount;
	}

	public String getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(String memorySize) {
		this.memorySize = memorySize;
	}

	public String getHdSpecification() {
		return hdSpecification;
	}

	public void setHdSpecification(String hdSpecification) {
		this.hdSpecification = hdSpecification;
	}

	public String getTapeSpecification() {
		return tapeSpecification;
	}

	public void setTapeSpecification(String tapeSpecification) {
		this.tapeSpecification = tapeSpecification;
	}

	public String getHdSize() {
		return hdSize;
	}

	public void setHdSize(String hdSize) {
		this.hdSize = hdSize;
	}

	public String getPublicLanIp() {
		return publicLanIp;
	}

	public void setPublicLanIp(String publicLanIp) {
		this.publicLanIp = publicLanIp;
	}

	public String getBackupLanIp() {
		return backupLanIp;
	}

	public void setBackupLanIp(String backupLanIp) {
		this.backupLanIp = backupLanIp;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getManager2() {
		return manager2;
	}

	public void setManager2(String manager2) {
		this.manager2 = manager2;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getHypervisor() {
		return hypervisor;
	}

	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
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
