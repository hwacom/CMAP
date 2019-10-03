package com.cmap.plugin.module.ip.maintain;

import com.cmap.service.vo.CommonServiceVO;

public class IpMaintainServiceVO extends CommonServiceVO {

    private String queryGroup;
    private String queryIp;
    private String queryMac;

    private String settingId;
    private String groupId;
    private String groupName;
    private String ipAddr;
    private String macAddr;
    private String ipDesc;
    private String remark;
    private String updateBy;
    private String updateTimeStr;

    private String modifyIpAddr;
    private String modifyMacAddr;
    private String modifyIpDesc;

    public String getQueryGroup() {
        return queryGroup;
    }
    public void setQueryGroup(String queryGroup) {
        this.queryGroup = queryGroup;
    }
    public String getQueryIp() {
        return queryIp;
    }
    public void setQueryIp(String queryIp) {
        this.queryIp = queryIp;
    }
    public String getQueryMac() {
        return queryMac;
    }
    public void setQueryMac(String queryMac) {
        this.queryMac = queryMac;
    }
    public String getSettingId() {
        return settingId;
    }
    public void setSettingId(String settingId) {
        this.settingId = settingId;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    @Override
    public String getIpAddr() {
        return ipAddr;
    }
    @Override
    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }
    @Override
    public String getMacAddr() {
        return macAddr;
    }
    @Override
    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }
    public String getIpDesc() {
        return ipDesc;
    }
    public void setIpDesc(String ipDesc) {
        this.ipDesc = ipDesc;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getUpdateBy() {
        return updateBy;
    }
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
    public String getUpdateTimeStr() {
        return updateTimeStr;
    }
    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }
    public String getModifyIpAddr() {
        return modifyIpAddr;
    }
    public void setModifyIpAddr(String modifyIpAddr) {
        this.modifyIpAddr = modifyIpAddr;
    }
    public String getModifyMacAddr() {
        return modifyMacAddr;
    }
    public void setModifyMacAddr(String modifyMacAddr) {
        this.modifyMacAddr = modifyMacAddr;
    }
    public String getModifyIpDesc() {
        return modifyIpDesc;
    }
    public void setModifyIpDesc(String modifyIpDesc) {
        this.modifyIpDesc = modifyIpDesc;
    }
}
