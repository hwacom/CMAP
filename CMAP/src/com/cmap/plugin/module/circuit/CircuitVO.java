package com.cmap.plugin.module.circuit;

import com.cmap.service.vo.CommonServiceVO;

public class CircuitVO extends CommonServiceVO {

    private String queryCircleId;
    private String queryType;
    private String queryName;

    private String circleId;
    private String circleName;
    private String type;
    private String name;
    private String e1Name;
    private String e1Ip;
    private String remark;
    private String settingName;
    private String settingValue;
	    
	private boolean isAdmin = false;

	public String getQueryCircleId() {
		return queryCircleId;
	}

	public void setQueryCircleId(String queryCircleId) {
		this.queryCircleId = queryCircleId;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
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

	public String getSettingName() {
		return settingName;
	}

	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}

	public String getSettingValue() {
		return settingValue;
	}

	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
}
