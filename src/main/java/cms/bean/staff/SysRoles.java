package cms.bean.staff;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;


/**
 * 角色表
 * @author Administrator
 *
 */
@Entity
public class SysRoles implements Serializable{
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
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
	

}
