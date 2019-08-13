package com.cmap.plugin.module.ip.mapping;

import java.util.Date;
import com.cmap.service.vo.CommonServiceVO;

public class IpMappingServiceVO extends CommonServiceVO {

    private Date executeDate;
    private String groupId;
    private String deviceId;
    private String interfaceId;
    private String macAddress;
    private String ipAddress;
    private String portId;

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
    public String getInterfaceId() {
        return interfaceId;
    }
    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }
    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
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
    public Date getExecuteDate() {
        return executeDate;
    }
    public void setExecuteDate(Date executeDate) {
        this.executeDate = executeDate;
    }
}
