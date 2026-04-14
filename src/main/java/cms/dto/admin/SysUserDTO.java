package cms.dto.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 员工展示
 */
@Getter
@Setter
public class SysUserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6983057748259464854L;

    //用户id
    private String userId;

    //用户账号 ，具有唯一性
    private String userAccount;

    //姓名
    private String fullName;

    //用户备注
    private String userDesc;

    //是否启用。
    private boolean enabled = true;
    //用户的职位：比如主任、经理等。
    private String userDuty;


    //是否是超级用户。
    private boolean issys = false;

    /** 安全摘要 **/
    private String securityDigest;

    /** 呢称 **/
    private String nickname;
    /** 头像路径 不写入数据库**/
    private String avatarPath;
    /** 头像名称 **/
    private String avatarName;


    /** 当前登录用户权限是否拥有本权限  **/
    private boolean logonUserPermission = false;


    //实现了UserDetails之后的相关变量
    private  String password;
    private  String username;
    private Set<GrantedAuthority> authorities;
    private  boolean accountNonExpired;//账号过期
    private  boolean accountNonLocked;//账号是否锁定
    private  boolean credentialsNonExpired;//凭证是否过期
}
