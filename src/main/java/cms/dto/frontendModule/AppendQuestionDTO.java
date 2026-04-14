package cms.dto.frontendModule;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 追加问题表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppendQuestionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1453449223866973735L;

    /** 问题Id **/
    private Long questionId;
    /** 内容 **/
    private String content;
    /** 是否使用Markdown **/
    private Boolean isMarkdown;
    /** Markdown内容 **/
    private String markdownContent;
    /** 验证码键 **/
    private String captchaKey;
    /** 验证码值 **/
    private String captchaValue;
}
