package cms.bean.fulltext;

import java.io.Serializable;

import cms.bean.question.Question;
import cms.bean.topic.Topic;

/**
 * 搜索结果
 * @author Gao
 *
 */
public class SearchResult implements Serializable{
	private static final long serialVersionUID = 1492732938859028084L;
	/** 索引模块 10:话题 20：问题 **/
	private Integer indexModule;
	/** 话题 **/
	private Topic topic;
	/** 问题 **/
	private Question question;
	
	public Integer getIndexModule() {
		return indexModule;
	}
	public void setIndexModule(Integer indexModule) {
		this.indexModule = indexModule;
	}
	public Topic getTopic() {
		return topic;
	}
	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	
	
}
