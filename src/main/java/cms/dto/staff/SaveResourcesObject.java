package cms.dto.staff;

import cms.model.staff.SysPermission;
import cms.model.staff.SysPermissionResources;
import cms.model.staff.SysResources;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;



/**
 * 保存资源对象
 * @author Administrator
 *
 */
@Getter
@Setter
public class SaveResourcesObject implements Serializable{
	@Serial
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
}
