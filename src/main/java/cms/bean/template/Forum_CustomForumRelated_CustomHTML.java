package cms.bean.template;

import java.io.Serializable;

/**
 * 版块---自定义版块--用户自定义HTML
 *
 */
public class Forum_CustomForumRelated_CustomHTML implements Serializable{
	private static final long serialVersionUID = -7907604516432123901L;

	/** 用户自定义HTML Id **/
	private String html_id;
	
	/** 用户自定义HTML内容 **/
	private String html_content;

	public String getHtml_id() {
		return html_id;
	}

	public void setHtml_id(String html_id) {
		this.html_id = html_id;
	}

	public String getHtml_content() {
		return html_content;
	}

	public void setHtml_content(String html_content) {
		this.html_content = html_content;
	}
	
}
