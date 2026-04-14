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
 * 权限资源表
 * @author Administrator
 *
 */
@Entity
@Getter
@Setter
public class SysPermissionResources implements Serializable{
	@Serial
    private static final long serialVersionUID = -142532252536637931L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 权限Id **/
	private String permissionId;
	/** 资源Id **/
	private String resourceId;
}
