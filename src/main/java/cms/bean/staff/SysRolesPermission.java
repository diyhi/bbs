package cms.bean.staff;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 角色权限表
 * @author Administrator
 *
 */
@Entity
public class SysRolesPermission implements Serializable{
	private static final long serialVersionUID = -6883616170304647037L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/** 角色Id **/
	private String roleId;
	/** 权限Id **/
	private String permissionId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}

}
