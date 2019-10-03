package com.cmap.plugin.module.ip.maintain;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="module_ip_data_setting")
public class ModuleIpDataSetting implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "setting_id", unique = true)
    private String settingId;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(name = "ip_addr", nullable = false)
    private String ipAddr;

    @Column(name = "mac_addr", nullable = false)
    private String macAddr;

    @Column(name = "ip_desc", nullable = false)
    private String ipDesc;

    @Column(name = "remark", nullable = true)
    private String remark;

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "create_by", nullable = false)
    private String createBy;

    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

    @Column(name = "update_by", nullable = false)
    private String updateBy;

    public ModuleIpDataSetting() {
        super();
    }

    public ModuleIpDataSetting(String settingId, String groupId, String ipAddr, String macAddr,
            String ipDesc, String remark, Timestamp createTime, String createBy,
            Timestamp updateTime, String updateBy) {
        super();
        this.settingId = settingId;
        this.groupId = groupId;
        this.ipAddr = ipAddr;
        this.macAddr = macAddr;
        this.ipDesc = ipDesc;
        this.remark = remark;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
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

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getMacAddr() {
        return macAddr;
    }

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
