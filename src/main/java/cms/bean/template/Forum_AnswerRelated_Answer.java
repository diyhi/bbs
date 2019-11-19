package cms.bean.template;

import java.io.Serializable;

/**
 * 版块---答案相关--答案
 *
 */
public class Forum_AnswerRelated_Answer implements Serializable{
	private static final long serialVersionUID = 3265049655611795163L;

	/** 版块---答案相关--答案  Id **/
	private String answer_id;
	
	/** 每页显示记录数 **/
	private Integer  answer_maxResult;
	/** 页码显示总数 **/
	private Integer  answer_pageCount;

	/** 排序 **/
	private Integer answer_sort;

	public String getAnswer_id() {
		return answer_id;
	}

	public void setAnswer_id(String answer_id) {
		this.answer_id = answer_id;
	}

	public Integer getAnswer_maxResult() {
		return answer_maxResult;
	}

	public void setAnswer_maxResult(Integer answer_maxResult) {
		this.answer_maxResult = answer_maxResult;
	}

	public Integer getAnswer_pageCount() {
		return answer_pageCount;
	}

	public void setAnswer_pageCount(Integer answer_pageCount) {
		this.answer_pageCount = answer_pageCount;
	}

	public Integer getAnswer_sort() {
		return answer_sort;
	}

	public void setAnswer_sort(Integer answer_sort) {
		this.answer_sort = answer_sort;
	}

	

	
}
