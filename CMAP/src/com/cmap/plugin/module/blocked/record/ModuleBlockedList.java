package com.cmap.plugin.module.blocked.record;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="module_blocked_list")
public class ModuleBlockedList implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "list_id", unique = true)
    private String listId;

    @Column(name = "group_id", nullable = true)
    private String groupId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "block_type", nullable = false)
    private String blockType;
    
    @Column(name = "ip_address", nullable = true)
    private String ipAddress;
    
    @Column(name = "mac_address", nullable = true)
    private String macAddress;
    
    @Column(name = "port_id", nullable = true)
    private String port;
    
    @Column(name = "global_value", nullable = true)
    private String globalValue;
    
    @Column(name = "status_flag", nullable = false)
    private String statusFlag;

    @Column(name = "script_code")
    private String scriptCode;
    
    @Column(name = "script_name", nullable = true)
    private String scriptName;
    
    @Column(name = "block_time", nullable = false)
    private Timestamp blockTime;

    @Column(name = "block_by", nullable = false)
    private String blockBy;

    @Column(name = "block_reason", nullable = false)
    private String blockReason;

    @Column(name = "open_time", nullable = true)
    private Timestamp openTime;

    @Column(name = "open_by", nullable = true)
    private String openBy;

    @Column(name = "open_reason", nullable = true)
    private String openReason;
    
    @Column(name = "remark", nullable = true)
    private String remark;
    
    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "create_by", nullable = false)
    private String createBy;

    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

    @Column(name = "update_by", nullable = false)
    private String updateBy;

    
    public ModuleBlockedList() {
        super();
    }

	public ModuleBlockedList(String listId, String groupId, String deviceId, String blockType, String ipAddress,
			String macAddress, String port, String globalValue, String statusFlag, String scriptCode, String scriptName,
			Timestamp blockTime, String blockBy, String blockReason, Timestamp openTime, String openBy,
			String openReason, String remark, Timestamp createTime, String createBy, Timestamp updateTime,
			String updateBy) {
        super();
        this.listId = listId;
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.blockType = blockType;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.port = port;
        this.globalValue = globalValue;
        this.statusFlag = statusFlag;
        this.scriptCode = scriptCode;
        this.scriptName = scriptName;
        this.blockTime = blockTime;
        this.blockBy = blockBy;
        this.blockReason = blockReason;
        this.openTime = openTime;
        this.openBy = openBy;
        this.openReason = openReason;        
        this.remark = remark;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getBlockType() {
		return blockType;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getGlobalValue() {
		return globalValue;
	}

	public void setGlobalValue(String globalValue) {
		this.globalValue = globalValue;
	}

	public String getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(String statusFlag) {
		this.statusFlag = statusFlag;
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public Timestamp getBlockTime() {
		return blockTime;
	}

	public void setBlockTime(Timestamp blockTime) {
		this.blockTime = blockTime;
	}

	public String getBlockBy() {
		return blockBy;
	}

	public void setBlockBy(String blockBy) {
		this.blockBy = blockBy;
	}

	public String getBlockReason() {
		return blockReason;
	}

	public void setBlockReason(String blockReason) {
		this.blockReason = blockReason;
	}

	public Timestamp getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Timestamp openTime) {
		this.openTime = openTime;
	}

	public String getOpenBy() {
		return openBy;
	}

	public void setOpenBy(String openBy) {
		this.openBy = openBy;
	}

	public String getOpenReason() {
		return openReason;
	}

	public void setOpenReason(String openReason) {
		this.openReason = openReason;
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
