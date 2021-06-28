package com.cmap.plugin.module.circuit;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="module_circuit_diagram_setting")
public class ModuleCircuitDiagramSetting implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "setting_id", unique = true)
    private String settingId;

    @Column(name = "SR_PREFIX_SID", nullable = true)
    private String srPrefixSid;
    
    @Column(name = "E1GW_IP", nullable = true)
    private String e1gwIp;

    @Column(name = "PORT_NUMBER", nullable = false)
    private String port;
    
    @Column(name = "USED_FLAG", nullable = true)
    private String usedFlag;
    
    @Column(name = "remark", nullable = true)
    private String remark;
    	
    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "create_by", nullable = false)
    private String createBy;

    @Column(name = "update_time", nullable = true)
	private Timestamp updateTime;
	
	@Column(name = "update_by", nullable = true)
	private String updateBy;
	
	public ModuleCircuitDiagramSetting() {
		super();
	}

	public ModuleCircuitDiagramSetting(String settingId, String srPrefixSid, String e1gwIp, String port,
			String usedFlag, String remark, Timestamp createTime, String createBy, Timestamp updateTime,
			String updateBy) {
		super();
		this.settingId = settingId;
		this.srPrefixSid = srPrefixSid;
		this.e1gwIp = e1gwIp;
		this.port = port;
		this.usedFlag = usedFlag;
		this.remark = remark;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	public String getSettingId() {
		return settingId;
	}

	public void setSettingId(String settingId) {
		this.settingId = settingId;
	}

	public String getSrPrefixSid() {
		return srPrefixSid;
	}

	public void setSrPrefixSid(String srPrefixSid) {
		this.srPrefixSid = srPrefixSid;
	}

	public String getE1gwIp() {
		return e1gwIp;
	}

	public void setE1gwIp(String e1gwIp) {
		this.e1gwIp = e1gwIp;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsedFlag() {
		return usedFlag;
	}

	public void setUsedFlag(String usedFlag) {
		this.usedFlag = usedFlag;
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
