package cms.bean.template;

import java.io.Serializable;

/**
 * 自定义HTML
 *
 */
public class CustomHTML implements Serializable{
	private static final long serialVersionUID = -8187953994941497433L;
	
	/** 版块标题 **/
	private String forumTitle;
	/** 用户自定义HTML内容 **/
	private String content;
	
	public String getForumTitle() {
		return forumTitle;
	}
	public void setForumTitle(String forumTitle) {
		this.forumTitle = forumTitle;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
	
}
