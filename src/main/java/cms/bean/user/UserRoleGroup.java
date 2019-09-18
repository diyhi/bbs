package cms.bean.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 用户角色组(保存用户所属的角色)
 *
 */
@Entity
@Table(indexes = {@Index(name="userRoleGroup_1_idx", columnList="userName,validPeriodEnd"),@Index(name="userRoleGroup_3_idx", columnList="userRoleId,userName")})
public class UserRoleGroup implements Serializable{
	private static final long serialVersionUID = -1406832022665184431L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=30)
	private String userName;
	
	/** 角色Id **/
	@Column(length=32)
	private String userRoleId;

	/** 有效期结束  默认：2999-1-1 0:0 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date validPeriodEnd;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(String userRoleId) {
		this.userRoleId = userRoleId;
	}

	public Date getValidPeriodEnd() {
		return validPeriodEnd;
	}

	public void setValidPeriodEnd(Date validPeriodEnd) {
		this.validPeriodEnd = validPeriodEnd;
	}
	
	
}
