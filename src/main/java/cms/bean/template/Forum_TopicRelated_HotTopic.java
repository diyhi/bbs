package cms.bean.template;

import java.io.Serializable;

/**
 * 热门话题
 *
 */
public class Forum_TopicRelated_HotTopic implements Serializable{
	private static final long serialVersionUID = 5980498481240294504L;

	/** 版块---话题相关--热门话题  Id **/
	private String hotTopic_id;
	
	/** 显示记录数 **/
	private Integer  hotTopic_maxResult;

	public String getHotTopic_id() {
		return hotTopic_id;
	}

	public void setHotTopic_id(String hotTopic_id) {
		this.hotTopic_id = hotTopic_id;
	}

	public Integer getHotTopic_maxResult() {
		return hotTopic_maxResult;
	}

	public void setHotTopic_maxResult(Integer hotTopic_maxResult) {
		this.hotTopic_maxResult = hotTopic_maxResult;
	}

	
}
