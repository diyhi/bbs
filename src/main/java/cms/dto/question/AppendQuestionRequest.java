package cms.dto.question;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 追加问题表单
 */
@Getter
@Setter
public class AppendQuestionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -7796406301550061833L;

    /** 问题Id  **/
    private Long questionId;
    /** 追加问题项Id **/
    private String appendQuestionItemId;
    /** 富文本内容 **/
    private String content;
    /** 是否为Markdown内容 **/
    private Boolean isMarkdown;
    /** Markdown内容 **/
    private String markdownContent;
}
