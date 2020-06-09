package com.cmap.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
	name = "prtg_account_mapping",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"id"})
	}
)
public class PrtgAccountMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true)
	private String id;

	@Column(name = "prtg_account", nullable = false)
	private String prtgAccount;

	@Column(name = "prtg_username")
	private String prtgUsername;
	
	@Column(name = "net_flow_output_map_url", nullable = true)
    private String netFlowOutputMapUrl;
	
	@Column(name = "remark", nullable = true)
	private String remark;

	@Column(name = "create_time", nullable = false)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = false)
	private String createBy;

	public PrtgAccountMapping() {
		super();
	}

	public PrtgAccountMapping(String id, String prtgAccount, String prtgUsername, String remark, Timestamp createTime,
			String createBy, String netFlowOutputMapUrl) {
		super();
		this.id = id;
		this.prtgAccount = prtgAccount;
		this.prtgUsername = prtgUsername;
		this.remark = remark;
		this.createTime = createTime;
		this.createBy = createBy;
		this.netFlowOutputMapUrl = netFlowOutputMapUrl;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrtgAccount() {
        return prtgAccount;
    }

    public void setPrtgAccount(String prtgAccount) {
        this.prtgAccount = prtgAccount;
    }

    public String getPrtgUsername() {
		return prtgUsername;
	}

	public void setPrtgUsername(String prtgUsername) {
		this.prtgUsername = prtgUsername;
	}

	public String getNetFlowOutputMapUrl() {
		return netFlowOutputMapUrl;
	}

	public void setNetFlowOutputMapUrl(String netFlowOutputMapUrl) {
		this.netFlowOutputMapUrl = netFlowOutputMapUrl;
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
