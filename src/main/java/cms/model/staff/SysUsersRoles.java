package cms.model.staff;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户角色表
 * @author Administrator
 *
 */
@Entity
@Getter
@Setter
public class SysUsersRoles implements Serializable{
	@Serial
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
}
