package cms.bean.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 模板数据
 *
 */
public class TemplateData implements Serializable{
	private static final long serialVersionUID = -8937602857803100791L;
	
	/** 模板目录数据 **/
	private Templates templates = null;
	/** 布局数据 **/
	private List<Layout> layoutList = new ArrayList<Layout>();
	/** 版块数据 **/
	private List<Forum> forumList = new ArrayList<Forum>();
	
	
	public Templates getTemplates() {
		return templates;
	}
	public void setTemplates(Templates templates) {
		this.templates = templates;
	}
	public List<Layout> getLayoutList() {
		return layoutList;
	}
	public void setLayoutList(List<Layout> layoutList) {
		this.layoutList = layoutList;
	}
	public List<Forum> getForumList() {
		return forumList;
	}
	public void setForumList(List<Forum> forumList) {
		this.forumList = forumList;
	}
	
	
}
