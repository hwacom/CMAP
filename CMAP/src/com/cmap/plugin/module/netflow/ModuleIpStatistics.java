package com.cmap.plugin.module.netflow;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		name = "module_ip_statistics",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"id"})
		}
)
public class ModuleIpStatistics {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@Column(name = "group_id", nullable = false)
	private String groupId;

	@Column(name = "stat_date", nullable = false)
    private Date statDate;

	@Column(name = "ip_address", nullable = false)
	private String ipAddress;

	@Column(name = "upload_traffic", nullable = true)
	private BigInteger uploadTraffic;

	@Column(name = "download_traffic", nullable = true)
    private BigInteger downloadTraffic;

	@Column(name = "create_time", nullable = false)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = false)
	private String createBy;

	@Column(name = "update_time", nullable = false)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = false)
	private String updateBy;

	public ModuleIpStatistics() {
		super();
	}

    public ModuleIpStatistics(Integer id, String groupId, Date statDate, String ipAddress,
            BigInteger uploadTraffic, BigInteger downloadTraffic, Timestamp createTime,
            String createBy, Timestamp updateTime, String updateBy) {
        super();
        this.id = id;
        this.groupId = groupId;
        this.statDate = statDate;
        this.ipAddress = ipAddress;
        this.uploadTraffic = uploadTraffic;
        this.downloadTraffic = downloadTraffic;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Date getStatDate() {
        return statDate;
    }

    public void setStatDate(Date statDate) {
        this.statDate = statDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public BigInteger getUploadTraffic() {
        return uploadTraffic;
    }

    public void setUploadTraffic(BigInteger uploadTraffic) {
        this.uploadTraffic = uploadTraffic;
    }

    public BigInteger getDownloadTraffic() {
        return downloadTraffic;
    }

    public void setDownloadTraffic(BigInteger downloadTraffic) {
        this.downloadTraffic = downloadTraffic;
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
