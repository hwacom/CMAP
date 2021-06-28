package com.cmap.plugin.module.inventory.info;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		name = "module_inventory_info_server_detail",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"DEVICE_ID"})
		}
		)
public class InventoryInfoServerDetail {

	@Id
	@Column(name = "DEVICE_ID", unique = true)
	private String deviceId;
	
	@Column(name = "SITE", nullable = true)
	private String site;
	
	@Column(name = "SITE_MOD_RSN", nullable = true)
	private String siteModRsn;
	
	@Column(name = "ACCET_NUMBER", nullable = true)
	private String accetNumber;
	
	@Column(name = "CLASS_USE", nullable = true)
	private String classUse;
	
	@Column(name = "CLASSUSE_MOD_RSN", nullable = true)
	private String classUseModRsn;

	@Column(name = "STATUS_MOD_RSN", nullable = true)
	private String statusModRsn;
	
	@Column(name = "CLASS_PRIMARY", nullable = true)
	private String classPrimary;
	
	@Column(name = "ISDMZ", nullable = true)
	private String isdmz;
	
	@Column(name = "IDC_ID", nullable = true)
	private String idcId;
	
	@Column(name = "RACK_ID", nullable = true)
	private String rackId;
	
	@Column(name = "ENCLOSURE_NO", nullable = true)
	private String enclosureNo;
	
	@Column(name = "FROM_RACK_UNIT", nullable = true)
	private String fromRackUnit;
	
	@Column(name = "TO_RACK_UNIT", nullable = true)
	private String toRackUnit;
	
	@Column(name = "OS_NAME", nullable = true)
	private String osName;
	
	@Column(name = "OS_VERSION", nullable = true)
	private String osVersion;
	
	@Column(name = "OS_BIT", nullable = true)
	private String osBit;
	
	@Column(name = "MACHINE_TYPE", nullable = true)
	private String machineType;
	
	@Column(name = "CPU_SPECIFICATION", nullable = true)
	private String cpuSpecification;
	
	@Column(name = "CPU_CORE", nullable = true)
	private String cpuCore;
	
	@Column(name = "CPU_SPEED", nullable = true)
	private String cpuSpeed;
	
	@Column(name = "CPU_AMOUNT", nullable = true)
	private String cpuAmount;
	
	@Column(name = "MEMORY_SPECIFICATION", nullable = true)
	private String memorySpecification;
	
	@Column(name = "MEMORY_SIZE", nullable = true)
	private String memorySize;
	
	@Column(name = "HD_SPECIFICATION", nullable = true)
	private String hdSpecification;
	
	@Column(name = "PUBLIC_LAN_IP", nullable = true)
	private String publicLanIp;
	
	@Column(name = "CLUSTER_IP", nullable = true)
	private String clusterIp;
	
	@Column(name = "BACKUP_LAN_IP", nullable = true)
	private String backupLanIp;
	
	@Column(name = "PRIVATE_LAN_IP", nullable = true)
	private String privateLanIp;
	
	@Column(name = "MANAGER", nullable = true)
	private String manager;
	
	@Column(name = "MANAGER2", nullable = true)
	private String manager2;
	
	@Column(name = "BUY_DATE", nullable = true)
	private String buyDate;
	
	@Column(name = "START_DATE", nullable = true)
	private String startDate;
	
	@Column(name = "WE_DATE", nullable = true)
	private String weDate;
	
	@Column(name = "INSERT_RSN", nullable = true)
	private String insertRsn;
	
	@Column(name = "SERVICE_TYPE", nullable = true)
	private String serviceType;
	
	@Column(name = "BIOS_FIRMWARE", nullable = true)
	private String biosFirmware;
	
	@Column(name = "MANAGEMENT_IP", nullable = true)
	private String managementIp;
	
	@Column(name = "update_time", nullable = true)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = true)
	private String updateBy;
	
	public InventoryInfoServerDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InventoryInfoServerDetail(String deviceId, String site, String siteModRsn, String accetNumber,
			String classUse, String classUseModRsn, String statusModRsn, String classPrimary, String isdmz,
			String idcId, String rackId, String enclosureNo, String fromRackUnit, String toRackUnit, String osName,
			String osVersion, String osBit, String machineType, String cpuSpecification, String cpuCore,
			String cpuSpeed, String cpuAmount, String memorySpecification, String memorySize, String hdSpecification,
			String publicLanIp, String clusterIp, String backupLanIp, String privateLanIp, String manager,
			String manager2, String buyDate, String startDate, String weDate, String insertRsn, 
			String serviceType, String biosFirmware, String managementIp, Timestamp updateTime, String updateBy) {
		super();
		this.deviceId = deviceId;
		this.site = site;
		this.siteModRsn = siteModRsn;
		this.accetNumber = accetNumber;
		this.classUse = classUse;
		this.classUseModRsn = classUseModRsn;
		this.statusModRsn = statusModRsn;
		this.classPrimary = classPrimary;
		this.isdmz = isdmz;
		this.idcId = idcId;
		this.rackId = rackId;
		this.enclosureNo = enclosureNo;
		this.fromRackUnit = fromRackUnit;
		this.toRackUnit = toRackUnit;
		this.osName = osName;
		this.osVersion = osVersion;
		this.osBit = osBit;
		this.machineType = machineType;
		this.cpuSpecification = cpuSpecification;
		this.cpuCore = cpuCore;
		this.cpuSpeed = cpuSpeed;
		this.cpuAmount = cpuAmount;
		this.memorySpecification = memorySpecification;
		this.memorySize = memorySize;
		this.hdSpecification = hdSpecification;
		this.publicLanIp = publicLanIp;
		this.clusterIp = clusterIp;
		this.backupLanIp = backupLanIp;
		this.privateLanIp = privateLanIp;
		this.manager = manager;
		this.manager2 = manager2;
		this.buyDate = buyDate;
		this.startDate = startDate;
		this.weDate = weDate;
		this.insertRsn = insertRsn;
		this.serviceType = serviceType;
		this.biosFirmware = biosFirmware;
		this.managementIp = managementIp;
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

	public String getAccetNumber() {
		return accetNumber;
	}

	public void setAccetNumber(String accetNumber) {
		this.accetNumber = accetNumber;
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

	public String getStatusModRsn() {
		return statusModRsn;
	}

	public void setStatusModRsn(String statusModRsn) {
		this.statusModRsn = statusModRsn;
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

	public String getIdcId() {
		return idcId;
	}

	public void setIdcId(String idcId) {
		this.idcId = idcId;
	}

	public String getRackId() {
		return rackId;
	}

	public void setRackId(String rackId) {
		this.rackId = rackId;
	}

	public String getEnclosureNo() {
		return enclosureNo;
	}

	public void setEnclosureNo(String enclosureNo) {
		this.enclosureNo = enclosureNo;
	}

	public String getFromRackUnit() {
		return fromRackUnit;
	}

	public void setFromRackUnit(String fromRackUnit) {
		this.fromRackUnit = fromRackUnit;
	}

	public String getToRackUnit() {
		return toRackUnit;
	}

	public void setToRackUnit(String toRackUnit) {
		this.toRackUnit = toRackUnit;
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

	public String getMachineType() {
		return machineType;
	}

	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}

	public String getCpuSpecification() {
		return cpuSpecification;
	}

	public void setCpuSpecification(String cpuSpecification) {
		this.cpuSpecification = cpuSpecification;
	}

	public String getCpuCore() {
		return cpuCore;
	}

	public void setCpuCore(String cpuCore) {
		this.cpuCore = cpuCore;
	}

	public String getCpuSpeed() {
		return cpuSpeed;
	}

	public void setCpuSpeed(String cpuSpeed) {
		this.cpuSpeed = cpuSpeed;
	}

	public String getCpuAmount() {
		return cpuAmount;
	}

	public void setCpuAmount(String cpuAmount) {
		this.cpuAmount = cpuAmount;
	}

	public String getMemorySpecification() {
		return memorySpecification;
	}

	public void setMemorySpecification(String memorySpecification) {
		this.memorySpecification = memorySpecification;
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

	public String getPublicLanIp() {
		return publicLanIp;
	}

	public void setPublicLanIp(String publicLanIp) {
		this.publicLanIp = publicLanIp;
	}

	public String getClusterIp() {
		return clusterIp;
	}

	public void setClusterIp(String clusterIp) {
		this.clusterIp = clusterIp;
	}

	public String getBackupLanIp() {
		return backupLanIp;
	}

	public void setBackupLanIp(String backupLanIp) {
		this.backupLanIp = backupLanIp;
	}

	public String getPrivateLanIp() {
		return privateLanIp;
	}

	public void setPrivateLanIp(String privateLanIp) {
		this.privateLanIp = privateLanIp;
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

	public String getBuyDate() {
		return buyDate;
	}

	public void setBuyDate(String buyDate) {
		this.buyDate = buyDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getWeDate() {
		return weDate;
	}

	public void setWeDate(String weDate) {
		this.weDate = weDate;
	}

	public String getInsertRsn() {
		return insertRsn;
	}

	public void setInsertRsn(String insertRsn) {
		this.insertRsn = insertRsn;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getBiosFirmware() {
		return biosFirmware;
	}

	public void setBiosFirmware(String biosFirmware) {
		this.biosFirmware = biosFirmware;
	}

	public String getManagementIp() {
		return managementIp;
	}

	public void setManagementIp(String managementIp) {
		this.managementIp = managementIp;
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
