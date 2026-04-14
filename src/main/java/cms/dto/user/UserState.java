package cms.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户状态
 *
 */
@Getter
@Setter
public class UserState implements Serializable{
	@Serial
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

}
