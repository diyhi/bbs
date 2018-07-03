package cms.bean.template;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 版块---话题相关--话题
 *
 */
public class Forum_TopicRelated_Topic implements Serializable{
	private static final long serialVersionUID = 975106163387929473L;

	/** 版块---话题相关--话题  Id **/
	private String topic_id;
	
	/** 话题展示数量 **/
	private Integer topic_quantity;
	/** 排序 **/
	private Integer topic_sort;
	/** 更多 **/
	private String topic_more;
	/** 更多选中文本 **/
	private String topic_moreValue;
	
	/** 每页显示记录数 **/
	private Integer  topic_maxResult;
	/** 页码显示总数 **/
	private Integer  topic_pageCount;
	
	/** 标签Id **/
	private Long topic_tagId;
	/** 标签名称 **/
	private String topic_tagName;
	/** 是否传递标签参数 **/
	private boolean topic_tag_transferPrameter = false;
	
	/** 选择推荐话题 key:话题Id value:话题名称**/
	private Map<Long,String> topic_recommendTopicList = new LinkedHashMap<Long,String>();

	public String getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(String topic_id) {
		this.topic_id = topic_id;
	}

	public Integer getTopic_quantity() {
		return topic_quantity;
	}

	public void setTopic_quantity(Integer topic_quantity) {
		this.topic_quantity = topic_quantity;
	}

	public Integer getTopic_sort() {
		return topic_sort;
	}

	public void setTopic_sort(Integer topic_sort) {
		this.topic_sort = topic_sort;
	}

	public String getTopic_more() {
		return topic_more;
	}

	public void setTopic_more(String topic_more) {
		this.topic_more = topic_more;
	}

	public String getTopic_moreValue() {
		return topic_moreValue;
	}

	public void setTopic_moreValue(String topic_moreValue) {
		this.topic_moreValue = topic_moreValue;
	}

	public Integer getTopic_maxResult() {
		return topic_maxResult;
	}

	public void setTopic_maxResult(Integer topic_maxResult) {
		this.topic_maxResult = topic_maxResult;
	}

	public Integer getTopic_pageCount() {
		return topic_pageCount;
	}

	public void setTopic_pageCount(Integer topic_pageCount) {
		this.topic_pageCount = topic_pageCount;
	}

	public Long getTopic_tagId() {
		return topic_tagId;
	}

	public void setTopic_tagId(Long topic_tagId) {
		this.topic_tagId = topic_tagId;
	}

	public String getTopic_tagName() {
		return topic_tagName;
	}

	public void setTopic_tagName(String topic_tagName) {
		this.topic_tagName = topic_tagName;
	}

	public boolean isTopic_tag_transferPrameter() {
		return topic_tag_transferPrameter;
	}

	public void setTopic_tag_transferPrameter(boolean topic_tag_transferPrameter) {
		this.topic_tag_transferPrameter = topic_tag_transferPrameter;
	}

	public Map<Long, String> getTopic_recommendTopicList() {
		return topic_recommendTopicList;
	}

	public void setTopic_recommendTopicList(Map<Long, String> topic_recommendTopicList) {
		this.topic_recommendTopicList = topic_recommendTopicList;
	}
	
	
	
}
