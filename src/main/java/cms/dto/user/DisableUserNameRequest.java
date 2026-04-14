package cms.dto.user;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 禁止的用户名称表单
 */
@Getter
@Setter
public class DisableUserNameRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8595954734728803685L;

    /** Id **/
    private Integer disableUserNameId;
    /** 禁止的用户名称 **/
    private String name;


}
