package com.cmap.plugin.module.circuit;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="module_circuit_e1open_list")
public class ModuleCircuitE1OpenList implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "LIST_ID", unique = true)
    private String id;

    @Column(name = "DEVICE_ID", nullable = false)
    private String deviceId;

    @Column(name = "CIRCLE_NAME", nullable = false)
    private String circleName;

    @Column(name = "STATION_NAME", nullable = false)
    private String stationName;
    
    @Column(name = "STATION_ENG_NAME", nullable = false)
    private String stationEngName;
    
    @Column(name = "NE_NAME", nullable = false)
    private String neName;
    
    @Column(name = "SOURCE_SID", nullable = false)
    private String srcSid;
    
    @Column(name = "DESTINATION_SID", nullable = false)
    private String dstSid;
    
    @Column(name = "LOCAL_AREA_CSR_IP", nullable = false)
    private String localCsrIp;

    @Column(name = "REMOTE_AREA_CSR_IP", nullable = false)
    private String remoteCsrIp;
    
    @Column(name = "SOURCE_E1GW_IP", nullable = false)
    private String srcE1gwIp;
    
    @Column(name = "DESTINATION_E1GW_IP", nullable = false)
    private String dstE1gwIp;

    @Column(name = "SOURCE_PORT_NUMBER", nullable = true)
    private String srcPortNumber;
    
    @Column(name = "DESTINATION_PORT_NUMBER", nullable = true)
    private String dstPortNumber;
    
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

	public ModuleCircuitE1OpenList(String id, String deviceId, String circleName, String stationName,
			String stationEngName, String neName, String srcSid, String dstSid, String localCsrIp, String remoteCsrIp,
			String srcE1gwIp, String dstE1gwIp, String srcPortNumber, String dstPortNumber, String remark,
			Timestamp createTime, String createBy, Timestamp updateTime, String updateBy) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.circleName = circleName;
		this.stationName = stationName;
		this.stationEngName = stationEngName;
		this.neName = neName;
		this.srcSid = srcSid;
		this.dstSid = dstSid;
		this.localCsrIp = localCsrIp;
		this.remoteCsrIp = remoteCsrIp;
		this.srcE1gwIp = srcE1gwIp;
		this.dstE1gwIp = dstE1gwIp;
		this.srcPortNumber = srcPortNumber;
		this.dstPortNumber = dstPortNumber;
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

	public String getCircleName() {
		return circleName;
	}

	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getStationEngName() {
		return stationEngName;
	}

	public void setStationEngName(String stationEngName) {
		this.stationEngName = stationEngName;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getSrcSid() {
		return srcSid;
	}

	public void setSrcSid(String srcSid) {
		this.srcSid = srcSid;
	}

	public String getDstSid() {
		return dstSid;
	}

	public void setDstSid(String dstSid) {
		this.dstSid = dstSid;
	}

	public String getLocalCsrIp() {
		return localCsrIp;
	}

	public void setLocalCsrIp(String localCsrIp) {
		this.localCsrIp = localCsrIp;
	}

	public String getRemoteCsrIp() {
		return remoteCsrIp;
	}

	public void setRemoteCsrIp(String remoteCsrIp) {
		this.remoteCsrIp = remoteCsrIp;
	}

	public String getSrcE1gwIp() {
		return srcE1gwIp;
	}

	public void setSrcE1gwIp(String srcE1gwIp) {
		this.srcE1gwIp = srcE1gwIp;
	}

	public String getDstE1gwIp() {
		return dstE1gwIp;
	}

	public void setDstE1gwIp(String dstE1gwIp) {
		this.dstE1gwIp = dstE1gwIp;
	}

	public String getSrcPortNumber() {
		return srcPortNumber;
	}

	public void setSrcPortNumber(String srcPortNumber) {
		this.srcPortNumber = srcPortNumber;
	}

	public String getDstPortNumber() {
		return dstPortNumber;
	}

	public void setDstPortNumber(String dstPortNumber) {
		this.dstPortNumber = dstPortNumber;
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
