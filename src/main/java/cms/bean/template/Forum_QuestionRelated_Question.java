package cms.bean.template;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 版块---问题相关--问题
 *
 */
public class Forum_QuestionRelated_Question implements Serializable{
	private static final long serialVersionUID = -3923468591547131979L;

	/** 版块---问题相关--问题  Id **/
	private String question_id;
	
	/** 问题展示数量 **/
	private Integer question_quantity;
	/** 排序 **/
	private Integer question_sort;
	/** 更多 **/
	private String question_more;
	/** 更多选中文本 **/
	private String question_moreValue;
	
	/** 每页显示记录数 **/
	private Integer  question_maxResult;
	/** 页码显示总数 **/
	private Integer  question_pageCount;
	
	/** 标签Id **/
	private Long question_tagId;
	/** 标签名称 **/
	private String question_tagName;
	/** 是否传递标签参数 **/
	private boolean question_tag_transferPrameter = false;
	
	
	/** 过滤条件  **/
	private Integer question_filterCondition;
	/** 是否传递过滤参数 **/
	private boolean question_filterCondition_transferPrameter = false;
	
	
	/** 选择推荐问题 key:问题Id value:问题名称**/
	private Map<Long,String> question_recommendQuestionList = new LinkedHashMap<Long,String>();


	public String getQuestion_id() {
		return question_id;
	}


	public void setQuestion_id(String question_id) {
		this.question_id = question_id;
	}


	public Integer getQuestion_quantity() {
		return question_quantity;
	}


	public void setQuestion_quantity(Integer question_quantity) {
		this.question_quantity = question_quantity;
	}


	public Integer getQuestion_sort() {
		return question_sort;
	}


	public void setQuestion_sort(Integer question_sort) {
		this.question_sort = question_sort;
	}


	public String getQuestion_more() {
		return question_more;
	}


	public void setQuestion_more(String question_more) {
		this.question_more = question_more;
	}


	public String getQuestion_moreValue() {
		return question_moreValue;
	}


	public void setQuestion_moreValue(String question_moreValue) {
		this.question_moreValue = question_moreValue;
	}


	public Integer getQuestion_maxResult() {
		return question_maxResult;
	}


	public void setQuestion_maxResult(Integer question_maxResult) {
		this.question_maxResult = question_maxResult;
	}


	public Integer getQuestion_pageCount() {
		return question_pageCount;
	}


	public void setQuestion_pageCount(Integer question_pageCount) {
		this.question_pageCount = question_pageCount;
	}


	public Long getQuestion_tagId() {
		return question_tagId;
	}


	public void setQuestion_tagId(Long question_tagId) {
		this.question_tagId = question_tagId;
	}


	public String getQuestion_tagName() {
		return question_tagName;
	}


	public void setQuestion_tagName(String question_tagName) {
		this.question_tagName = question_tagName;
	}


	public boolean isQuestion_tag_transferPrameter() {
		return question_tag_transferPrameter;
	}


	public void setQuestion_tag_transferPrameter(boolean question_tag_transferPrameter) {
		this.question_tag_transferPrameter = question_tag_transferPrameter;
	}


	public Integer getQuestion_filterCondition() {
		return question_filterCondition;
	}


	public void setQuestion_filterCondition(Integer question_filterCondition) {
		this.question_filterCondition = question_filterCondition;
	}


	public boolean isQuestion_filterCondition_transferPrameter() {
		return question_filterCondition_transferPrameter;
	}


	public void setQuestion_filterCondition_transferPrameter(boolean question_filterCondition_transferPrameter) {
		this.question_filterCondition_transferPrameter = question_filterCondition_transferPrameter;
	}


	public Map<Long, String> getQuestion_recommendQuestionList() {
		return question_recommendQuestionList;
	}


	public void setQuestion_recommendQuestionList(Map<Long, String> question_recommendQuestionList) {
		this.question_recommendQuestionList = question_recommendQuestionList;
	}

	
}
