package cms.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员刷新令牌
 */
@Getter
@Setter
public class AdminRefreshToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 5435558282198897558L;

    /** 访问令牌 **/
    private String accessToken;
    /** 员工Id **/
    private String userId;
    /** 员工账号 **/
    private String userAccount;
    /** 安全摘要 **/
    private String securityDigest;

    public AdminRefreshToken() {}

    public AdminRefreshToken(String accessToken, String userId, String userAccount, String securityDigest) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.userAccount = userAccount;
        this.securityDigest = securityDigest;
    }
}