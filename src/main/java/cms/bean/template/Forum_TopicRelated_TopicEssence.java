package cms.bean.template;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 版块---话题相关--话题精华
 *
 */
public class Forum_TopicRelated_TopicEssence implements Serializable{
	private static final long serialVersionUID = 5919976916881666241L;

	/** 版块---话题相关--话题精华Id **/
	private String topicEssence_id;
	
	/** 排序 **/
	private Integer topicEssence_sort;

	/** 每页显示记录数 **/
	private Integer  topicEssence_maxResult;
	/** 页码显示总数 **/
	private Integer  topicEssence_pageCount;
	
	public String getTopicEssence_id() {
		return topicEssence_id;
	}
	public void setTopicEssence_id(String topicEssence_id) {
		this.topicEssence_id = topicEssence_id;
	}
	public Integer getTopicEssence_sort() {
		return topicEssence_sort;
	}
	public void setTopicEssence_sort(Integer topicEssence_sort) {
		this.topicEssence_sort = topicEssence_sort;
	}
	public Integer getTopicEssence_maxResult() {
		return topicEssence_maxResult;
	}
	public void setTopicEssence_maxResult(Integer topicEssence_maxResult) {
		this.topicEssence_maxResult = topicEssence_maxResult;
	}
	public Integer getTopicEssence_pageCount() {
		return topicEssence_pageCount;
	}
	public void setTopicEssence_pageCount(Integer topicEssence_pageCount) {
		this.topicEssence_pageCount = topicEssence_pageCount;
	}
	
	
	
}
