package cms.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员访问令牌
 */
@Getter
@Setter
public class AdminAccessToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 8522623669433715266L;

    /** 刷新令牌 **/
    private String refreshToken;
    /** 员工Id **/
    private String userId;
    /** 员工账号 **/
    private String userAccount;
    /** 安全摘要 **/
    private String securityDigest;

    public AdminAccessToken() {}

    public AdminAccessToken(String refreshToken, String userId, String userAccount, String securityDigest) {
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.userAccount = userAccount;
        this.securityDigest = securityDigest;
    }
}
