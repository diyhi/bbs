package cms.bean.staff;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 用户角色表
 * @author Administrator
 *
 */
@Entity
public class SysUsersRoles implements Serializable{
	private static final long serialVersionUID = 3748392796372099307L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=30)
	private String userAccount;
	
	/** 角色Id **/
	private String roleId;
	
	
	
	public SysUsersRoles() {}
	public SysUsersRoles(String userAccount, String roleId) {
		this.userAccount = userAccount;
		this.roleId = roleId;
	}
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
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	
}
