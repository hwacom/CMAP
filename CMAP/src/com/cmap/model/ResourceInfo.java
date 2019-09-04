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
		name = "resource_info",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"id"})
		}
		)
public class ResourceInfo {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "id", unique = true)
	private String id;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	@Column(name = "file_ext_name", nullable = false)
	private String fileExtName;

	@Column(name = "file_full_name", nullable = false)
	private String fileFullName;

	@Column(name = "content_type", nullable = true)
	private String contentType;

	@Column(name = "status_flag", nullable = false)
	private String statusFlag;

	@Column(name = "download_times", nullable = false)
	private Integer downloadTimes;

	@Column(name = "remark", nullable = true)
	private String remark;

	@Column(name = "delete_time", nullable = true)
    private Timestamp deleteTime;

    @Column(name = "delete_by", nullable = true)
    private String deleteBy;

	@Column(name = "create_time", nullable = false)
	private Timestamp createTime;

	@Column(name = "create_by", nullable = false)
	private String createBy;

	@Column(name = "update_time", nullable = false)
	private Timestamp updateTime;

	@Column(name = "update_by", nullable = false)
	private String updateBy;

	public ResourceInfo() {
		super();
	}

    public ResourceInfo(String id, String fileName, String fileExtName, String fileFullName,
            String contentType, String statusFlag, Integer downloadTimes, String remark,
            Timestamp deleteTime, String deleteBy, Timestamp createTime, String createBy,
            Timestamp updateTime, String updateBy) {
        super();
        this.id = id;
        this.fileName = fileName;
        this.fileExtName = fileExtName;
        this.fileFullName = fileFullName;
        this.contentType = contentType;
        this.statusFlag = statusFlag;
        this.downloadTimes = downloadTimes;
        this.remark = remark;
        this.deleteTime = deleteTime;
        this.deleteBy = deleteBy;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtName() {
        return fileExtName;
    }

    public void setFileExtName(String fileExtName) {
        this.fileExtName = fileExtName;
    }

    public String getFileFullName() {
        return fileFullName;
    }

    public void setFileFullName(String fileFullName) {
        this.fileFullName = fileFullName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(String statusFlag) {
        this.statusFlag = statusFlag;
    }

    public Integer getDownloadTimes() {
        return downloadTimes;
    }

    public void setDownloadTimes(Integer downloadTimes) {
        this.downloadTimes = downloadTimes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Timestamp getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Timestamp deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getDeleteBy() {
        return deleteBy;
    }

    public void setDeleteBy(String deleteBy) {
        this.deleteBy = deleteBy;
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
