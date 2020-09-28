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

    @Column(name = "TYPE", nullable = false)
    private String type;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "E1", nullable = false)
    private String e1Name;
    
    @Column(name = "E1_IP", nullable = true)
    private String e1Ip;
    
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

	public ModuleCircuitDiagramInfo(String id, String circleId, String circleName, String type, String name,
			String e1Name, String e1Ip, String remark, String deleteFlag, Timestamp deleteTime, String deleteBy,
			Timestamp createTime, String createBy) {
		super();
		this.id = id;
		this.circleId = circleId;
		this.circleName = circleName;
		this.type = type;
		this.name = name;
		this.e1Name = e1Name;
		this.e1Ip = e1Ip;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getE1Name() {
		return e1Name;
	}

	public void setE1Name(String e1Name) {
		this.e1Name = e1Name;
	}

	public String getE1Ip() {
		return e1Ip;
	}

	public void setE1Ip(String e1Ip) {
		this.e1Ip = e1Ip;
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
