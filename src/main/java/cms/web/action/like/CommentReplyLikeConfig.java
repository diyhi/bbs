package cms.web.action.like;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 评论回复点赞配置
 *
 */
public class CommentReplyLikeConfig {
	@Resource LikeManage likeManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据评论回复点赞Id查询分配到表编号
	  * 根据评论回复点赞Id和评论回复点赞分表数量求余
	  * @param commentReplyLikeId 评论回复点赞Id
	  * 注意：评论回复点赞Id要先判断最后4位是不是数字
	  * likeManage.verificationItemLikeId(?)
	  * @return
	 */
	public Integer commentReplyLikeIdRemainder(String commentReplyLikeId){
	   int commentReplyId = likeManage.getItemLikeId(commentReplyLikeId);
	   return commentReplyId % this.getTableQuantity();
	} 
   /**
    * 根据评论回复Id查询分配到表编号
    * 根据评论回复Id和评论回复点赞分表数量求余(用户Id后四位)
    * @param commentReplyId 评论回复Id
    * @return
    */
	public Integer commentReplyIdRemainder(Long commentReplyId){
	   	//选取得后N位评论回复Id
	   	String afterCommentReplyId = String.format("%04d", commentReplyId%10000);
	   	return Integer.parseInt(afterCommentReplyId) % this.getTableQuantity();
	}
	 
}
