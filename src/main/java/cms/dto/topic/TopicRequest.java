package cms.dto.topic;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 话题表单
 */
@Getter
@Setter
public class TopicRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8360444403565960972L;

    /** Id **/
    private Long topicId;
    /** 标签Id **/
    private Long tagId;
    /** 标题 **/
    private String title;
    /** 允许评论 **/
    private Boolean allow;
    /** 状态 **/
    private Integer status;
    /** 精华 **/
    private Boolean essence;
    /** 内容 **/
    private String content;
    /** 是否使用Markdown **/
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
