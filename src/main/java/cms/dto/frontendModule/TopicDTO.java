package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 话题表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 687289666568838137L;

    /** 话题Id **/
    private Long topicId;
    /** 标签Id **/
    private Long tagId;
    /** 标题 **/
    private String title;
    /** 内容 **/
    private String content;
    /** 是否使用Markdown **/
    private Boolean isMarkdown;
    /** Markdown内容 **/
    private String markdownContent;
    /** 红包类型 **/
    private Integer type;
    /** 总金额 **/
    private String totalAmount;
    /** 单个金额 **/
    private String singleAmount;
    /** 发放数量 **/
    private String giveQuantity;
    /** 是否发起投票 **/
    private Boolean isVote;
    /** 投票标题 **/
    private String voteTitle;
    /** 投票选项Id集合 **/
    private String[] voteOptionIdList;
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
