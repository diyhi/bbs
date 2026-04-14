package cms.model.staff;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色权限表
 * @author Administrator
 *
 */
@Entity
@Getter
@Setter
public class SysRolesPermission implements Serializable{
	@Serial
    private static final long serialVersionUID = -6883616170304647037L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/** 角色Id **/
	private String roleId;
	/** 权限Id **/
	private String permissionId;

}
