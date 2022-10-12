package cms.bean.template;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

/**
 * 模板目录管理
 *
 */
@Entity
public class Templates implements Serializable{
	private static final long serialVersionUID = -7953037804710631257L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;		
	/** 模板名称 **/
	private String name;
	/** 模板目录名称 **/
	@Column(length=40)
	private String dirName;
	/** 缩略图后缀  图片默认名称：templates **/
	@Column(length=20)
	private String thumbnailSuffix;
	/** 文件名称 **/
	@Transient
	private String fileName;
	/** 是否使用 **/ //默认true /新建false
	@Column(nullable=false)
	private Boolean uses = false;
	/** 站点栏目 JSON数据**/
	@Lob
	private String columns;
	/** 是否验证CSRF **/
	@Column(nullable=false)
	private Boolean verifyCSRF = true;
	/** 模板简介 **/
	@Lob
	private String introduction;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public Boolean getUses() {
		return uses;
	}

	public void setUses(Boolean uses) {
		this.uses = uses;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getThumbnailSuffix() {
		return thumbnailSuffix;
	}

	public void setThumbnailSuffix(String thumbnailSuffix) {
		this.thumbnailSuffix = thumbnailSuffix;
	}

	public Boolean getVerifyCSRF() {
		return verifyCSRF;
	}

	public void setVerifyCSRF(Boolean verifyCSRF) {
		this.verifyCSRF = verifyCSRF;
	}


}
