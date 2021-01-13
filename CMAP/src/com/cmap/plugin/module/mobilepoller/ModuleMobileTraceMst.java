package com.cmap.plugin.module.mobilepoller;

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
		name = "module_mobile_trace_mst",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"client_SUPI", "start_time"})
		}
)
public class ModuleMobileTraceMst {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "data_id", unique = true)
	private Long dataId;
    
	@Column(name = "client_SUPI", nullable = false)
	private String clientSUPI;

	@Column(name = "start_time", nullable = false)
    private Date startTime;

	@Column(name = "end_time", nullable = true)
	private Date endTime;

	@Column(name = "client_Number", nullable = false)
	private String clientNumber;
	
	@Column(name = "cell_name", nullable = false)
	private String apName;
	
	@Column(name = "ssid", nullable = false)
	private String ssid;
	
	@Column(name = "total_traffic", nullable = true)
    private Double totalTraffic = new Double(0);

	@Column(name = "upload_traffic", nullable = true)
	private Double uploadTraffic = new Double(0);

	@Column(name = "download_traffic", nullable = true)
    private Double downloadTraffic = new Double(0);

	@Column(name = "create_time", nullable = false)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = false)
	private String createBy;

	@Column(name = "update_time", nullable = false)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = false)
	private String updateBy;

	public ModuleMobileTraceMst() {
		super();
	}

    public ModuleMobileTraceMst( Long dataId, String clientSUPI, Date startTime, Date endTime, String clientNumber,
    		String apName, String ssid, Double totalTraffic, Double uploadTraffic, Double downloadTraffic,
    		Timestamp createTime, String createBy, Timestamp updateTime, String updateBy) {
        super();
        this.dataId = dataId;
        this.clientSUPI = clientSUPI;
        this.startTime = startTime;
        this.endTime = endTime;
        this.clientNumber = clientNumber;
        this.apName = apName;
        this.ssid = ssid;
        this.totalTraffic = totalTraffic;
        this.uploadTraffic = uploadTraffic;
        this.downloadTraffic = downloadTraffic;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }
    
	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	public String getClientSUPI() {
		return clientSUPI;
	}

	public void setClientSUPI(String clientSUPI) {
		this.clientSUPI = clientSUPI;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getCellName() {
		return apName;
	}

	public void setCellName(String apName) {
		this.apName = apName;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public Double getTotalTraffic() {
		return totalTraffic;
	}

	public void setTotalTraffic(Double totalTraffic) {
		this.totalTraffic = totalTraffic;
	}

	public Double getUploadTraffic() {
		return uploadTraffic;
	}

	public void setUploadTraffic(Double uploadTraffic) {
		this.uploadTraffic = uploadTraffic;
	}

	public Double getDownloadTraffic() {
		return downloadTraffic;
	}

	public void setDownloadTraffic(Double downloadTraffic) {
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
