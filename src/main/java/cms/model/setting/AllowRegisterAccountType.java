package cms.model.setting;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 允许注册账号类型
 * @author Gao
 *
 */
@Getter
@Setter
public class AllowRegisterAccountType implements Serializable{
	@Serial
    private static final long serialVersionUID = 9165054829593116204L;

	/** 本地账号密码用户 **/
	private boolean local = false;
	/** 手机用户 **/
	private boolean mobile = false;
	/** 邮箱用户 **/
	private boolean email = false;
	/** 微信用户 **/
	private boolean weChat = false;
	/** 其他用户 **/
	private boolean other = false;

}
