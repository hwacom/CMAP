package com.cmap.service.vo;

import java.util.ArrayList;
import java.util.List;

public class ScriptServiceVO extends CommonServiceVO {

	private List<String> queryScriptTypeCode = new ArrayList<>();

	private String scriptInfoId;
	private String scriptListId;
	private String scriptTypeId;

	private String scriptDefault;
	private String scriptCode;
	private String scriptName;
	private String scriptStepOrder;
	private String scriptContent;
	private String expectedTerminalSymbol;
	private String output;
	private String headCuttingLines;
	private String tailCuttingLines;
	private String remark;
	private String errorSymbol;
	private String repeatFlag;
	private String scriptDescription;
	private String scriptTypeName;
	private String scriptMode;
	private String deviceModel;
	private String scriptSleepTime;
	
	private String actionScript;
	private String actionScriptVariable;
	private String actionScriptRemark;
	private String checkScript;
	private String checkScriptRemark;
	private String checkKeyword;

	private String createTimeStr;
	private String createBy;
	private String updateTimeStr;
	private String updateBy;

	private Boolean enableModify;
	private boolean isAdmin;
	
	public String getScriptInfoId() {
		return scriptInfoId;
	}
	public void setScriptInfoId(String scriptInfoId) {
		this.scriptInfoId = scriptInfoId;
	}
	public String getScriptListId() {
		return scriptListId;
	}
	public void setScriptListId(String scriptListId) {
		this.scriptListId = scriptListId;
	}
	public String getScriptTypeId() {
		return scriptTypeId;
	}
	public void setScriptTypeId(String scriptTypeId) {
		this.scriptTypeId = scriptTypeId;
	}
	public String getScriptDefault() {
		return scriptDefault;
	}
	public void setScriptDefault(String scriptDefault) {
		this.scriptDefault = scriptDefault;
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
	public String getScriptStepOrder() {
		return scriptStepOrder;
	}
	public void setScriptStepOrder(String scriptStepOrder) {
		this.scriptStepOrder = scriptStepOrder;
	}
	public String getScriptContent() {
		return scriptContent;
	}
	public void setScriptContent(String scriptContent) {
		this.scriptContent = scriptContent;
	}
	public String getExpectedTerminalSymbol() {
		return expectedTerminalSymbol;
	}
	public void setExpectedTerminalSymbol(String expectedTerminalSymbol) {
		this.expectedTerminalSymbol = expectedTerminalSymbol;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getHeadCuttingLines() {
		return headCuttingLines;
	}
	public void setHeadCuttingLines(String headCuttingLines) {
		this.headCuttingLines = headCuttingLines;
	}
	public String getTailCuttingLines() {
		return tailCuttingLines;
	}
	public void setTailCuttingLines(String tailCuttingLines) {
		this.tailCuttingLines = tailCuttingLines;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getErrorSymbol() {
		return errorSymbol;
	}
	public void setErrorSymbol(String errorSymbol) {
		this.errorSymbol = errorSymbol;
	}
	public String getRepeatFlag() {
		return repeatFlag;
	}
	public void setRepeatFlag(String repeatFlag) {
		this.repeatFlag = repeatFlag;
	}
	public String getScriptDescription() {
		return scriptDescription;
	}
	public void setScriptDescription(String scriptDescription) {
		this.scriptDescription = scriptDescription;
	}
	public String getScriptTypeName() {
		return scriptTypeName;
	}
	public void setScriptTypeName(String scriptTypeName) {
		this.scriptTypeName = scriptTypeName;
	}
	public String getScriptMode() {
		return scriptMode;
	}
	public void setScriptMode(String scriptMode) {
		this.scriptMode = scriptMode;
	}
	public String getScriptSleepTime() {
		return scriptSleepTime;
	}
	public void setScriptSleepTime(String scriptSleepTime) {
		this.scriptSleepTime = scriptSleepTime;
	}
	public String getActionScript() {
		return actionScript;
	}
	public void setActionScript(String actionScript) {
		this.actionScript = actionScript;
	}
	public String getActionScriptVariable() {
		return actionScriptVariable;
	}
	public void setActionScriptVariable(String actionScriptVariable) {
		this.actionScriptVariable = actionScriptVariable;
	}
	public String getActionScriptRemark() {
		return actionScriptRemark;
	}
	public void setActionScriptRemark(String actionScriptRemark) {
		this.actionScriptRemark = actionScriptRemark;
	}
	public String getCheckScript() {
		return checkScript;
	}
	public void setCheckScript(String checkScript) {
		this.checkScript = checkScript;
	}
	public String getCheckScriptRemark() {
		return checkScriptRemark;
	}
	public void setCheckScriptRemark(String checkScriptRemark) {
		this.checkScriptRemark = checkScriptRemark;
	}
	public String getCheckKeyword() {
		return checkKeyword;
	}
	public void setCheckKeyword(String checkKeyword) {
		this.checkKeyword = checkKeyword;
	}
	public String getCreateTimeStr() {
		return createTimeStr;
	}
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getUpdateTimeStr() {
		return updateTimeStr;
	}
	public void setUpdateTimeStr(String updateTimeStr) {
		this.updateTimeStr = updateTimeStr;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public List<String> getQueryScriptTypeCode() {
		return queryScriptTypeCode;
	}
	public void setQueryScriptTypeCode(List<String> queryScriptTypeCode) {
		this.queryScriptTypeCode = queryScriptTypeCode;
	}
	public Boolean getEnableModify() {
		return enableModify;
	}
	public void setEnableModify(Boolean enableModify) {
		this.enableModify = enableModify;
	}
    public String getDeviceModel() {
        return deviceModel;
    }
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}
