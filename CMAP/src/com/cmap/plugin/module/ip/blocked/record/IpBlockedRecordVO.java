package com.cmap.plugin.module.ip.blocked.record;

import java.util.List;
import com.cmap.service.vo.CommonServiceVO;

public class IpBlockedRecordVO extends CommonServiceVO {

    private String queryListId;
    private String queryGroupId;
    private String queryDeviceId;
    private String queryIpAddress;
    private List<String> queryStatusFlag;
    private List<String> queryExcludeStatusFlag;
    private String queryBeginDate;
    private String queryEndDate;

    private List<String> queryGroupIdList;
    private List<String> queryDeviceIdList;

    private String listId;
    private String groupId;
    private String deviceId;
    private String ipAddress;
    private String ipDesc;
    private String statusFlag;
    private String blockTimeStr;
    private String blockBy;
    private String blockReason;
    private String openTimeStr;
    private String openBy;
    private String openReason;
    private String remark;
    private String updateTimeStr;
    private String updateBy;
	private String scriptCode;
	private String scriptName;
    
    private String groupName;
    private String deviceName;

	private boolean isAdmin = false;
	
    public String getQueryGroupId() {
        return queryGroupId;
    }
    public void setQueryGroupId(String queryGroupId) {
        this.queryGroupId = queryGroupId;
    }
    public String getQueryDeviceId() {
        return queryDeviceId;
    }
    public void setQueryDeviceId(String queryDeviceId) {
        this.queryDeviceId = queryDeviceId;
    }
    public String getQueryIpAddress() {
        return queryIpAddress;
    }
    public void setQueryIpAddress(String queryIpAddress) {
        this.queryIpAddress = queryIpAddress;
    }
    public List<String> getQueryStatusFlag() {
        return queryStatusFlag;
    }
    public void setQueryStatusFlag(List<String> queryStatusFlag) {
        this.queryStatusFlag = queryStatusFlag;
    }
    public List<String> getQueryExcludeStatusFlag() {
        return queryExcludeStatusFlag;
    }
    public void setQueryExcludeStatusFlag(List<String> queryExcludeStatusFlag) {
        this.queryExcludeStatusFlag = queryExcludeStatusFlag;
    }
    public String getQueryBeginDate() {
        return queryBeginDate;
    }
    public void setQueryBeginDate(String queryBeginDate) {
        this.queryBeginDate = queryBeginDate;
    }
    public String getQueryEndDate() {
        return queryEndDate;
    }
    public void setQueryEndDate(String queryEndDate) {
        this.queryEndDate = queryEndDate;
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
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getStatusFlag() {
        return statusFlag;
    }
    public void setStatusFlag(String statusFlag) {
        this.statusFlag = statusFlag;
    }
    public String getBlockTimeStr() {
        return blockTimeStr;
    }
    public void setBlockTimeStr(String blockTimeStr) {
        this.blockTimeStr = blockTimeStr;
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
    public String getOpenTimeStr() {
        return openTimeStr;
    }
    public void setOpenTimeStr(String openTimeStr) {
        this.openTimeStr = openTimeStr;
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
	public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
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
	public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public List<String> getQueryGroupIdList() {
        return queryGroupIdList;
    }
    public void setQueryGroupIdList(List<String> queryGroupIdList) {
        this.queryGroupIdList = queryGroupIdList;
    }
    public List<String> getQueryDeviceIdList() {
        return queryDeviceIdList;
    }
    public void setQueryDeviceIdList(List<String> queryDeviceIdList) {
        this.queryDeviceIdList = queryDeviceIdList;
    }
    public String getIpDesc() {
        return ipDesc;
    }
    public void setIpDesc(String ipDesc) {
        this.ipDesc = ipDesc;
    }
    public String getQueryListId() {
        return queryListId;
    }
    public void setQueryListId(String queryListId) {
        this.queryListId = queryListId;
    }
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}
