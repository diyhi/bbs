package cms.bean.template;

import java.io.Serializable;

/**
 * 相似话题
 *
 */
public class Forum_TopicRelated_LikeTopic implements Serializable{
	private static final long serialVersionUID = 3774218953546027277L;

	/** 版块---话题相关--相似话题  Id **/
	private String likeTopic_id;
	
	/** 显示记录数 **/
	private Integer  likeTopic_maxResult;

	public String getLikeTopic_id() {
		return likeTopic_id;
	}

	public void setLikeTopic_id(String likeTopic_id) {
		this.likeTopic_id = likeTopic_id;
	}

	public Integer getLikeTopic_maxResult() {
		return likeTopic_maxResult;
	}

	public void setLikeTopic_maxResult(Integer likeTopic_maxResult) {
		this.likeTopic_maxResult = likeTopic_maxResult;
	}
	
	
}
