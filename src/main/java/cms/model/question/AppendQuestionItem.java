package cms.model.question;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 追加问题项
 * @author Gao
 *
 */
@Getter
@Setter
public class AppendQuestionItem implements Serializable{
	@Serial
    private static final long serialVersionUID = -8096694358931284202L;
	/** Id **/
	private String id;
	/** 追加内容 **/
	private String content;
	/** 是否使用Markdown **/
	private Boolean isMarkdown;
	/** Markdown内容 **/
	private String markdownContent;
	/** 追加时间 **/
	private LocalDateTime postTime;

}
