package cms.dto.question;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 答案回复表单
 */
@Getter
@Setter
public class AnswerReplyRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6348539948979582416L;

    /** 答案回复Id  **/
    private Long answerReplyId;
    /** 评论Id  **/
    private Long answerId;
    /** 内容 **/
    private String content;

    /** 对方回复Id **/
    private Long friendReplyId;

    /** 状态 **/
    private Integer status;
}
