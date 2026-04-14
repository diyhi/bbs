package cms.dto.staff;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限对象(模块权限)
 * @author Administrator
 *
 */
@Getter
@Setter
public class PermissionObject implements Serializable{
	@Serial
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
}
