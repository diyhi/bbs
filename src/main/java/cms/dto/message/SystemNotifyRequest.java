package cms.dto.message;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统通知表单
 */
@Getter
@Setter
public class SystemNotifyRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -1223082944505763215L;

    /** Id  **/
    private Long systemNotifyId;
    /** 通知内容 **/
    private String content;
}
