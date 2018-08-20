package cms.bean;

import java.io.Serializable;

import cms.bean.staff.SysPermission;
import cms.bean.staff.SysPermissionResources;
import cms.bean.staff.SysResources;

/**
 * 保存资源对象
 * @author Administrator
 *
 */
public class SaveResourcesObject implements Serializable{
	private static final long serialVersionUID = 6466768197362232270L;

	/** 权限表 **/
	private SysPermission sysPermission;

	/** 资源表 **/
	private SysResources sysResources;
	/** 权限资源表 **/
	private SysPermissionResources sysPermissionResources;
	
	public SaveResourcesObject() {
	}
	public SaveResourcesObject(SysResources sysResources,
			SysPermission sysPermission,SysPermissionResources sysPermissionResources) {
		this.sysResources = sysResources;
		this.sysPermission = sysPermission;
		this.sysPermissionResources = sysPermissionResources;
	}

	public SysPermission getSysPermission() {
		return sysPermission;
	}

	public void setSysPermission(SysPermission sysPermission) {
		this.sysPermission = sysPermission;
	}

	public SysResources getSysResources() {
		return sysResources;
	}

	public void setSysResources(SysResources sysResources) {
		this.sysResources = sysResources;
	}
	public SysPermissionResources getSysPermissionResources() {
		return sysPermissionResources;
	}
	public void setSysPermissionResources(
			SysPermissionResources sysPermissionResources) {
		this.sysPermissionResources = sysPermissionResources;
	}
	
	
}
