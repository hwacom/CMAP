package com.cmap.dao.vo;

import java.util.ArrayList;
import java.util.List;

public class ScriptInfoDAOVO extends CommonDAOVO {

	private List<String> queryScriptTypeCode = new ArrayList<>();
	private String queryScriptInfoId;
	private String queryScriptCode;
	private String querySystemDefault;
	
	private boolean isAdmin = false;

	public List<String> getQueryScriptTypeCode() {
		return queryScriptTypeCode;
	}
	public void setQueryScriptTypeCode(List<String> queryScriptTypeCode) {
		this.queryScriptTypeCode = queryScriptTypeCode;
	}
	public String getQueryScriptInfoId() {
		return queryScriptInfoId;
	}
	public void setQueryScriptInfoId(String queryScriptInfoId) {
		this.queryScriptInfoId = queryScriptInfoId;
	}
	public String getQueryScriptCode() {
		return queryScriptCode;
	}
	public void setQueryScriptCode(String queryScriptCode) {
		this.queryScriptCode = queryScriptCode;
	}
	public String getQuerySystemDefault() {
		return querySystemDefault;
	}
	public void setQuerySystemDefault(String querySystemDefault) {
		this.querySystemDefault = querySystemDefault;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}
