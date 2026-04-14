package cms.dto.frontendModule;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 问题表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5713365360915060828L;
    /** 标签Id集合 **/
    private Long[] tagId;
    /** 标题 **/
    private String title;
    /** 内容 **/
    private String content;
    /** 是否使用Markdown **/
    private Boolean isMarkdown;
    /** Markdown内容 **/
    private String markdownContent;
    /** 悬赏金额 **/
    private BigDecimal amount;
    /** 悬赏积分 **/
    private Long point;
    /** 是否发起投票 **/
    private Boolean isVote;
    /** 投票标题 **/
    private String voteTitle;
    /** 投票选项文本集合 **/
    private String[] voteOptionTextList;
    /** 投票最大可选数 **/
    private Integer voteMaxChoice;
    /** 投票截止时间 **/
    private String voteEndDate;
    /** 验证码键 **/
    private String captchaKey;
    /** 验证码值 **/
    private String captchaValue;
}
