package com.cmap.plugin.module.circuit;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="module_circuit_e1open_list")
public class ModuleCircuitE1OpenList implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private String id;

    @Column(name = "DEVICE_ID", nullable = false)
    private String deviceId;

    @Column(name = "IP_ADDRESS", nullable = true)
    private String ipAddress;

    @Column(name = "PORT_ID", nullable = true)
    private String portId;
    
    @Column(name = "TUNNEL_ID", nullable = true)
    private String tunnelId;
    
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
	
	public ModuleCircuitE1OpenList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ModuleCircuitE1OpenList(String id, String deviceId, String ipAddress, String portId, String tunnelId,
			String remark, Timestamp createTime, String createBy, Timestamp updateTime, String updateBy) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.ipAddress = ipAddress;
		this.portId = portId;
		this.tunnelId = tunnelId;
		this.remark = remark;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPortId() {
		return portId;
	}

	public void setPortId(String portId) {
		this.portId = portId;
	}

	public String getTunnelId() {
		return tunnelId;
	}

	public void setTunnelId(String tunnelId) {
		this.tunnelId = tunnelId;
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
