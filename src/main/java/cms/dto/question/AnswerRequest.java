package cms.dto.question;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 答案表单
 */
@Getter
@Setter
public class AnswerRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 8492113633452581210L;

    /** 答案Id  **/
    private Long answerId;
    /** 问题Id  **/
    private Long questionId;
    /** 富文本内容 **/
    private String content;
    /** 是否为Markdown内容 **/
    private Boolean isMarkdown;
    /** Markdown内容 **/
    private String markdownContent;
    /** 状态 **/
    private Integer status;
}
