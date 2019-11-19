package cms.bean.template;

import java.io.Serializable;

/**
 * 相似问题
 *
 */
public class Forum_QuestionRelated_LikeQuestion implements Serializable{
	private static final long serialVersionUID = 2445018023877339559L;

	/** 版块---问题相关--相似问题  Id **/
	private String likeQuestion_id;
	
	/** 显示记录数 **/
	private Integer  likeQuestion_maxResult;

	public String getLikeQuestion_id() {
		return likeQuestion_id;
	}

	public void setLikeQuestion_id(String likeQuestion_id) {
		this.likeQuestion_id = likeQuestion_id;
	}

	public Integer getLikeQuestion_maxResult() {
		return likeQuestion_maxResult;
	}

	public void setLikeQuestion_maxResult(Integer likeQuestion_maxResult) {
		this.likeQuestion_maxResult = likeQuestion_maxResult;
	}

	
}
