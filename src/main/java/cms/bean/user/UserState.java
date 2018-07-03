package cms.bean.user;

import java.io.Serializable;

/**
 * 用户状态
 *
 */
public class UserState implements Serializable{
	private static final long serialVersionUID = -6339247671671106838L;
	
	/** 安全摘要 **/
	private Long securityDigest;
	/** 用户状态 **/
	private Integer state;
	
	
	public UserState() {}
	public UserState(Long securityDigest, Integer state) {
		this.securityDigest = securityDigest;
		this.state = state;
	}
	public Long getSecurityDigest() {
		return securityDigest;
	}
	public void setSecurityDigest(Long securityDigest) {
		this.securityDigest = securityDigest;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	
}
