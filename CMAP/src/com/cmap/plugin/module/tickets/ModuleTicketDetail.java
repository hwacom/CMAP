package com.cmap.plugin.module.tickets;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="module_ticket_detail",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"DETAIL_ID"})
		})
public class ModuleTicketDetail implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "DETAIL_ID", unique = true)
    private String detailId;

    @Column(name = "LIST_ID", unique = true)
    private Long listId;
    
    @Column(name = "DETAIL_OWNER", nullable = false)
    private String detailOwner;

    @Column(name = "CONTENT", nullable = true)
    private String content;
    
    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "create_by", nullable = false)
    private String createBy;

    public ModuleTicketDetail() {
        super();
	}

	public ModuleTicketDetail(String detailId, Long listId, String detailOwner, String content, Timestamp createTime,
			String createBy) {
		super();
		this.detailId = detailId;
		this.listId = listId;
		this.detailOwner = detailOwner;
		this.content = content;
		this.createTime = createTime;
		this.createBy = createBy;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public Long getListId() {
		return listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}

	public String getDetailOwner() {
		return detailOwner;
	}

	public void setDetailOwner(String detailOwner) {
		this.detailOwner = detailOwner;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
