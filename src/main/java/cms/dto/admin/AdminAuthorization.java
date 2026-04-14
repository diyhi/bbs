package cms.dto.admin;

import cms.dto.user.AccessUser;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


/**
 * 管理员授权
 * @author Gao
 *
 */
@Getter
@Setter
public class AdminAuthorization implements Serializable{
    @Serial
    private static final long serialVersionUID = -1380588690452956884L;

    /** 访问令牌 **/
	private String access_token = "";
	/** 刷新令牌 **/
	private String refresh_token = "";
    /** 令牌类型 例如bearer **/
    private String token_type = "";
	public AdminAuthorization() {}

    public AdminAuthorization(String access_token, String refresh_token, String token_type) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
    }
}
