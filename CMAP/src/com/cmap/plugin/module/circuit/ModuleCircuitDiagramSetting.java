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

    @Column(name = "E1_IP", nullable = true)
    private String e1Ip;

    @Column(name = "E1_NODE", nullable = false)
    private String e1Node;

    @Column(name = "PORT", nullable = false)
    private String port;
        
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

	public ModuleCircuitDiagramSetting(String settingId, String e1Ip, String e1Node, String port, String remark,
			Timestamp createTime, String createBy, Timestamp updateTime, String updateBy) {
		super();
		this.settingId = settingId;
		this.e1Ip = e1Ip;
		this.e1Node = e1Node;
		this.port = port;
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

	public String getE1Ip() {
		return e1Ip;
	}

	public void setE1Ip(String e1Ip) {
		this.e1Ip = e1Ip;
	}

	public String getE1Node() {
		return e1Node;
	}

	public void setE1Node(String e1Node) {
		this.e1Node = e1Node;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
