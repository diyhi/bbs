package cms.model.staff;

import java.io.Serial;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;


/**
 * 角色表
 * @author Administrator
 *
 */
@Entity
@Getter
@Setter
public class SysRoles implements Serializable{
	@Serial
    private static final long serialVersionUID = -7744346111615183222L;
	
	@Id @Column(length=32)
	private String id;
	/** 名称 **/
	private String name;
	/** 备注 **/
	private String remarks;

	
	/** 当前登录用户权限是否拥有本权限  **/
	@Transient
	private boolean logonUserPermission = false;
	/** 是否选中 **/
	@Transient
	private boolean selected =  false;
}
