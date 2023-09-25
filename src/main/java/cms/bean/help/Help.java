package cms.bean.help;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


/**
 * 帮助
 *
 */
@Entity
@Table(indexes = {@Index(name="help_idx", columnList="helpTypeId,visible")})
public class Help implements Serializable{
	private static final long serialVersionUID = 2548461575859641680L;
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
	/** 帮助分类Id **/
	private Long helpTypeId;
	/** 帮助分类名称 **/
	@Transient
	private String helpTypeName;
	
	/** 帮助名称 **/
	@Column(length=200)
	private String name;
	/** 帮助内容 **/
	@Lob
	private String content;
	/** 是否使用Markdown **/
	private Boolean isMarkdown;
	/** Markdown内容 **/
	@Lob
	private String markdownContent;
	/** 发表时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date times = new Date();
	
	/** 是否可见 **/
	private boolean visible = true;

	
	
	public Help() {}
	public Help(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getHelpTypeId() {
		return helpTypeId;
	}

	public void setHelpTypeId(Long helpTypeId) {
		this.helpTypeId = helpTypeId;
	}

	public String getHelpTypeName() {
		return helpTypeName;
	}

	public void setHelpTypeName(String helpTypeName) {
		this.helpTypeName = helpTypeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTimes() {
		return times;
	}

	public void setTimes(Date times) {
		this.times = times;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public Boolean getIsMarkdown() {
		return isMarkdown;
	}
	public void setIsMarkdown(Boolean isMarkdown) {
		this.isMarkdown = isMarkdown;
	}
	public String getMarkdownContent() {
		return markdownContent;
	}
	public void setMarkdownContent(String markdownContent) {
		this.markdownContent = markdownContent;
	}
	
	
	
}
