package com.cmap.service.vo;

import java.io.InputStream;

public class ResourceServiceVO {

    private String queryId;

    private String id;
    private String fileName;
    private String fileExtName;
    private String fileFullName;
    private String contentType;
    private String statusFlag;
    private String remark;
    private InputStream inputStream;

    public String getQueryId() {
        return queryId;
    }
    public void setQueryId(String queryId) {
        this.queryId = queryId;
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
    public InputStream getInputStream() {
        return inputStream;
    }
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
