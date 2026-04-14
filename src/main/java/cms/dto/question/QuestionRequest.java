package cms.dto.question;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 问题表单
 */
@Getter
@Setter
public class QuestionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6220555161403587178L;

    /** 问题Id **/
    private Long questionId;
    /** 标签Id集合 **/
    private Long[] tagId;
    /** 标签名称 **/
    private String tagName;
    /** 标题 **/
    private String title;
    /** 是否允许回答 **/
    private Boolean allow;
    /** 状态 **/
    private Integer status;
    /** 积分 **/
    private Long point;
    /** 金额 **/
    private BigDecimal amount;
    /** 富文本内容 **/
    private String content;
    /** 是否为Markdown内容 **/
    private Boolean isMarkdown;
    /** Markdown内容 **/
    private String markdownContent;
    /** 排序 **/
    private Integer sort;
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
}
