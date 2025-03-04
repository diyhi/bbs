package cms.bean.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

/**
 * 角色
 *
 */
@Entity
public class UserRole implements Serializable{
	private static final long serialVersionUID = -2873847246959858813L;
	
	
	@Id @Column(length=32)
	private String id;
	/** 名称 **/
	@Column(length=50)
	private String name;
	/** 多语言扩展  存储JSON格式的multiLanguageExtensionMap属性值    key:字段-语言（例如：name-en_US） value:内容 **/
	@Lob
	private String multiLanguageExtension;
	/** 多语言扩展  key:字段-语言（例如：name-en_US） value:内容**/
	@Transient
	private Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();
	/** 备注 **/
	@Lob
	private String remark;
	/** 排序 **/
	private Integer sort = 0;
	/** 是否默认角色 **/
	private Boolean defaultRole = false;
	
	/** 资源 List<UserResourceGroup>的JSON格式值**/
	@Lob
	private String userResourceFormat;
	

	/** 是否选中 **/
	@Transient
	private boolean selected =  false;
	/** 有效期结束**/
	@Transient
	private Date validPeriodEnd;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Boolean getDefaultRole() {
		return defaultRole;
	}
	public void setDefaultRole(Boolean defaultRole) {
		this.defaultRole = defaultRole;
	}
	public String getUserResourceFormat() {
		return userResourceFormat;
	}
	public void setUserResourceFormat(String userResourceFormat) {
		this.userResourceFormat = userResourceFormat;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Date getValidPeriodEnd() {
		return validPeriodEnd;
	}
	public void setValidPeriodEnd(Date validPeriodEnd) {
		this.validPeriodEnd = validPeriodEnd;
	}
	public String getMultiLanguageExtension() {
		return multiLanguageExtension;
	}
	public void setMultiLanguageExtension(String multiLanguageExtension) {
		this.multiLanguageExtension = multiLanguageExtension;
	}
	public Map<String, String> getMultiLanguageExtensionMap() {
		return multiLanguageExtensionMap;
	}
	public void setMultiLanguageExtensionMap(Map<String, String> multiLanguageExtensionMap) {
		this.multiLanguageExtensionMap = multiLanguageExtensionMap;
	}
	
	
}
