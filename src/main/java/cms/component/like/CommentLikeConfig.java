package cms.component.like;

import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 评论点赞配置
 *
 */
@Component("commentLikeConfig")
public class CommentLikeConfig {
    @Resource LikeConfig likeConfig;
	
	/** 分表数量 **/
	@Value("${bbs.sharding.commentLikeConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	/**
	  * 根据评论点赞Id查询分配到表编号
	  * 根据评论点赞Id和评论点赞分表数量求余
	  * @param commentLikeId 评论点赞Id
	  * 注意：评论点赞Id要先判断最后4位是不是数字
	  * likeManage.verificationTopicLikeId(?)
	  * @return
	 */
	public Integer commentLikeIdRemainder(String commentLikeId){
	   int commentId = likeConfig.getItemLikeId(commentLikeId);
	   return commentId % this.getTableQuantity();
	} 
   /**
    * 根据评论Id查询分配到表编号
    * 根据评论Id和评论点赞分表数量求余(用户Id后四位)
    * @param commentId 评论Id
    * @return
    */
	public Integer commentIdRemainder(Long commentId){
	   	//选取得后N位评论Id
	   	String afterCommentId = String.format("%04d", commentId%10000);
	   	return Integer.parseInt(afterCommentId) % this.getTableQuantity();
	}
	 
}
