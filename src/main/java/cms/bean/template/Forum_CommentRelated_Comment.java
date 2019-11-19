package cms.bean.template;

import java.io.Serializable;

/**
 * 版块---评论相关--评论
 *
 */
public class Forum_CommentRelated_Comment implements Serializable{
	private static final long serialVersionUID = -864847290929316753L;

	/** 版块---评论相关--评论  Id **/
	private String comment_id;
	
	/** 每页显示记录数 **/
	private Integer  comment_maxResult;
	/** 页码显示总数 **/
	private Integer  comment_pageCount;

	/** 排序 **/
	private Integer comment_sort;

	public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}

	public Integer getComment_maxResult() {
		return comment_maxResult;
	}

	public void setComment_maxResult(Integer comment_maxResult) {
		this.comment_maxResult = comment_maxResult;
	}

	public Integer getComment_pageCount() {
		return comment_pageCount;
	}

	public void setComment_pageCount(Integer comment_pageCount) {
		this.comment_pageCount = comment_pageCount;
	}

	public Integer getComment_sort() {
		return comment_sort;
	}

	public void setComment_sort(Integer comment_sort) {
		this.comment_sort = comment_sort;
	}
	


	
}
