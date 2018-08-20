package cms.bean.template;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * 版块管理
 **/
@Entity
public class Forum implements Serializable{
	private static final long serialVersionUID = 4228740311736894921L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 布局Id**/
	@Column(length=36)
	private String layoutId;
	/** 版块名称 **/
	@Column(length=40)
	private String name;
	/** 选择版块模板 **/ 
	@Column(length=80)
	private String module;
	/** 模板显示类型    单层:monolayer  多层:multilayer  分页:page   实体对象:entityBean  集合:collection   **/ 
	@Column(length=10)
	private String displayType;
	/** 版块类型 **/
	@Column(length=40)
	private String forumType;
	/** 版块子类型 **/
	@Column(length=40)
	private String forumChildType;

	/** 生成版块引用代码 **/
	@Column(length=100)
	private String referenceCode;
	/** 模板目录名称 **/
	@Column(length=40)
	private String dirName;
	/** 布局类型**/
	private Integer layoutType;
	/** 布局文件**/ 
	@Column(length=40)
	private String layoutFile;
	/** 查询方式   0:数据库   1: lucene索引  **/
	private Integer queryMode = 0;
	
	/** 调用方式  1.引用代码  2.调用对象 **/
	private Integer invokeMethod = 1;

	/**存放JSON格式扩展表单值  版块扩展对象类型**/
	@Lob
	private String formValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(String layoutId) {
		this.layoutId = layoutId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
	public String getForumType() {
		return forumType;
	}

	public void setForumType(String forumType) {
		this.forumType = forumType;
	}

	public String getForumChildType() {
		return forumChildType;
	}

	public void setForumChildType(String forumChildType) {
		this.forumChildType = forumChildType;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}
	public String getLayoutFile() {
		return layoutFile;
	}

	public void setLayoutFile(String layoutFile) {
		this.layoutFile = layoutFile;
	}

	public String getFormValue() {
		return formValue;
	}

	public void setFormValue(String formValue) {
		this.formValue = formValue;
	}

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public Integer getQueryMode() {
		return queryMode;
	}

	public void setQueryMode(Integer queryMode) {
		this.queryMode = queryMode;
	}

	public Integer getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(Integer layoutType) {
		this.layoutType = layoutType;
	}

	public Integer getInvokeMethod() {
		return invokeMethod;
	}

	public void setInvokeMethod(Integer invokeMethod) {
		this.invokeMethod = invokeMethod;
	}
	
}
