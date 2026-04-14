package cms.dto.topic;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 回复表单
 */
@Getter
@Setter
public class ReplyRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6229104550158460542L;

    /** 回复Id  **/
    private Long replyId;
    /** 评论Id  **/
    private Long commentId;
    /** 内容 **/
    private String content;

    /** 对方回复Id **/
    private Long friendReplyId;

    /** 状态 **/
    private Integer status;
}
