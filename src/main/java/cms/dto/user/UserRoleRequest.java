package cms.dto.user;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户角色表单
 */
@Getter
@Setter
public class UserRoleRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -505660233685532294L;

    /** 用户角色Id **/
    private String userRoleId;
    /** 名称 **/
    private String name;
    /** 排序 **/
    private Integer sort;

    /** 用户资源组编号 **/
    private String[] resourceCode;

}
