package com.cmap.dao.vo;

public class SysMailDAOVO {

    private String subject;
    private String[] mailTo;
    private String[] mailCc;
    private String[] mailBcc;
    private String remark;

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String[] getMailTo() {
        return mailTo;
    }
    public void setMailTo(String[] mailTo) {
        this.mailTo = mailTo;
    }
    public String[] getMailCc() {
        return mailCc;
    }
    public void setMailCc(String[] mailCc) {
        this.mailCc = mailCc;
    }
    public String[] getMailBcc() {
        return mailBcc;
    }
    public void setMailBcc(String[] mailBcc) {
        this.mailBcc = mailBcc;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
