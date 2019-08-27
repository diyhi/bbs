package cms.bean.user;

import java.io.Serializable;

/**
 * 用户资源
 *
 */
public class UserResource implements Serializable{
	private static final long serialVersionUID = -3605121621902616792L;

	/** 编号 **/
	private Integer code;
	/** 名称 **/
	private String name;
	/** 资源组编号 **/
	private Integer resourceGroupCode;
	
	/** 选中 **/
	private Boolean selected = false;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getResourceGroupCode() {
		return resourceGroupCode;
	}
	public void setResourceGroupCode(Integer resourceGroupCode) {
		this.resourceGroupCode = resourceGroupCode;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	
	
	
	
}
