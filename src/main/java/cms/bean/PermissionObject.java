package cms.bean;

import java.io.Serializable;

/**
 * 权限对象(模块权限)
 * @author Administrator
 *
 */
public class PermissionObject implements Serializable{
	private static final long serialVersionUID = 9098365744132987263L;
	
	/** URL **/
	private String url;
	/** 请求对象 **/
	private String methods;
	
	
	/** 权限名称 **/
	private String permissionName;
	
	/** -------------------模块权限------------------- **/
	/** 资源表模块 **/
	private String module;
	/** 权限表Id **/
	private String permissionId;
	/** 权限表说明 **/
	private String remarks;
	/** 是否附加URL true:是附加URL  false:不是附加URL**/
	private boolean appendUrl = false;
	
	/** 当前登录用户权限是否拥有本权限  **/
	private boolean logonUserPermission = false;
	/** 是否选中 **/
	private boolean selected =  false;

	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethods() {
		return methods;
	}
	public void setMethods(String methods) {
		this.methods = methods;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public boolean isLogonUserPermission() {
		return logonUserPermission;
	}
	public void setLogonUserPermission(boolean logonUserPermission) {
		this.logonUserPermission = logonUserPermission;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public boolean isAppendUrl() {
		return appendUrl;
	}
	public void setAppendUrl(boolean appendUrl) {
		this.appendUrl = appendUrl;
	}


}
