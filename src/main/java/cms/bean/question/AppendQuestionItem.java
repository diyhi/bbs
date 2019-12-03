package cms.bean.question;

import java.io.Serializable;
import java.util.Date;

/**
 * 追加问题项
 * @author Gao
 *
 */
public class AppendQuestionItem implements Serializable{
	private static final long serialVersionUID = -8096694358931284202L;
	/** Id **/
	private String id;
	/** 追加内容 **/
	private String content;
	/** 追加时间 **/
	private Date postTime;

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getPostTime() {
		return postTime;
	}
	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}
	
	
}
