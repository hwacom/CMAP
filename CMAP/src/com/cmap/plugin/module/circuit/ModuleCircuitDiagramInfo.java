package com.cmap.plugin.module.circuit;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="module_circuit_diagram_info")
public class ModuleCircuitDiagramInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private String id;

    @Column(name = "CIRCLE_ID", nullable = true)
    private String circleId;

    @Column(name = "CIRCLE_NAME", nullable = false)
    private String circleName;

    @Column(name = "STATION_NAME", nullable = false)
    private String stationName;
    
    @Column(name = "STATION_ENG_NAME", nullable = false)
    private String stationEngName;
    
    @Column(name = "NE_NAME", nullable = false)
    private String neName;
    
    @Column(name = "E1GW_IP", nullable = true)
    private String e1gwIp;
    
    @Column(name = "SR_PREFIX_SID", nullable = true)
    private String srPrefixSid;

    @Column(name = "HOSTNAME", nullable = true)
    private String hostname;
    
    @Column(name = "LOCAL_AREA_CSR_IP", nullable = true)
    private String localAreaCsrIp;
    
    @Column(name = "LOCAL_AREA_CSR_SR_SID", nullable = true)
    private String localAreaCsrSrSid;
    
    @Column(name = "remark", nullable = true)
    private String remark;
    
    @Column(name = "delete_flag", nullable = false)
	private String deleteFlag;
	
	@Column(name = "delete_time", nullable = true)
	private Timestamp deleteTime;
	
	@Column(name = "delete_by", nullable = true)
	private String deleteBy;
	
    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "create_by", nullable = false)
    private String createBy;

	public ModuleCircuitDiagramInfo() {
		super();
	}

	public ModuleCircuitDiagramInfo(String id, String circleId, String circleName, String stationName,
			String stationEngName, String neName, String e1gwIp, String srPrefixSid, String hostname,
			String localAreaCsrIp, String localAreaCsrSrSid, String remark, String deleteFlag, Timestamp deleteTime,
			String deleteBy, Timestamp createTime, String createBy) {
		super();
		this.id = id;
		this.circleId = circleId;
		this.circleName = circleName;
		this.stationName = stationName;
		this.stationEngName = stationEngName;
		this.neName = neName;
		this.e1gwIp = e1gwIp;
		this.srPrefixSid = srPrefixSid;
		this.hostname = hostname;
		this.localAreaCsrIp = localAreaCsrIp;
		this.localAreaCsrSrSid = localAreaCsrSrSid;
		this.remark = remark;
		this.deleteFlag = deleteFlag;
		this.deleteTime = deleteTime;
		this.deleteBy = deleteBy;
		this.createTime = createTime;
		this.createBy = createBy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCircleId() {
		return circleId;
	}

	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}

	public String getCircleName() {
		return circleName;
	}

	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}

	public String getStatinName() {
		return stationName;
	}

	public void setStatinName(String stationName) {
		this.stationName = stationName;
	}

	public String getStatinEngName() {
		return stationEngName;
	}

	public void setStatinEngName(String stationEngName) {
		this.stationEngName = stationEngName;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getE1gwIp() {
		return e1gwIp;
	}

	public void setE1gwIp(String e1gwIp) {
		this.e1gwIp = e1gwIp;
	}

	public String getSrPrefixSid() {
		return srPrefixSid;
	}

	public void setSrPrefixSid(String srPrefixSid) {
		this.srPrefixSid = srPrefixSid;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getLocalAreaCsrIp() {
		return localAreaCsrIp;
	}

	public void setLocalAreaCsrIp(String localAreaCsrIp) {
		this.localAreaCsrIp = localAreaCsrIp;
	}

	public String getLocalAreaCsrSrSid() {
		return localAreaCsrSrSid;
	}

	public void setLocalAreaCsrSrSid(String localAreaCsrSrSid) {
		this.localAreaCsrSrSid = localAreaCsrSrSid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Timestamp getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Timestamp deleteTime) {
		this.deleteTime = deleteTime;
	}

	public String getDeleteBy() {
		return deleteBy;
	}

	public void setDeleteBy(String deleteBy) {
		this.deleteBy = deleteBy;
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
