package cms.dto.topic;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 评论表单
 */
@Getter
@Setter
public class CommentRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6617163128100821334L;

    /** 评论Id  **/
    private Long commentId;
    /** 话题Id  **/
    private Long topicId;
    /** 富文本内容 **/
    private String content;
    /** 是否为Markdown内容 **/
    private Boolean isMarkdown;
    /** Markdown内容 **/
    private String markdownContent;
    /** 状态 **/
    private Integer status;
}
