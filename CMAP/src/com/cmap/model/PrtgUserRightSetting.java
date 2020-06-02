package com.cmap.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(
		name = "prtg_user_right_setting",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"setting_id"})
		}
		)
public class PrtgUserRightSetting {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "setting_id", unique = true)
	private String settingId;

	@Column(name = "prtg_account", nullable = false)
	private String prtgAccount;

	@Column(name = "setting_type", nullable = false)
	private String settingType;

	@Column(name = "setting_value", nullable = false)
	private String settingValue;

	@Column(name = "setting_name", nullable = false)
	private String settingName;
	
	@Column(name = "parent_node", nullable = true)
    private String parentNode;

	@Column(name = "remark", nullable = true)
	private String remark;

	@Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "create_by", nullable = false)
    private String createBy;

	public PrtgUserRightSetting() {
		super();
	}

    public PrtgUserRightSetting(String settingId, String prtgAccount, String settingType,
            String settingValue,  String settingName, String parentNode, String remark, Timestamp createTime,
            String createBy) {
        super();
        this.settingId = settingId;
        this.prtgAccount = prtgAccount;
        this.settingType = settingType;
        this.settingValue = settingValue;
        this.settingName = settingName;
        this.parentNode = parentNode;
        this.remark = remark;
        this.createTime = createTime;
        this.createBy = createBy;
    }

    public String getSettingId() {
        return settingId;
    }

    public void setSettingId(String settingId) {
        this.settingId = settingId;
    }

    public String getPrtgAccount() {
        return prtgAccount;
    }

    public void setPrtgAccount(String prtgAccount) {
        this.prtgAccount = prtgAccount;
    }

    public String getSettingType() {
        return settingType;
    }

    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getSettingName() {
		return settingName;
	}

	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}

	public String getParentNode() {
        return parentNode;
    }

    public void setParentNode(String parentNode) {
        this.parentNode = parentNode;
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
}
