package cms.web.action.like;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 答案回复点赞配置
 *
 */
public class AnswerReplyLikeConfig {
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
	  * 根据答案回复点赞Id查询分配到表编号
	  * 根据答案回复点赞Id和答案回复点赞分表数量求余
	  * @param answerReplyLikeId 答案回复点赞Id
	  * 注意：答案回复点赞Id要先判断最后4位是不是数字
	  * likeManage.verificationItemLikeId(?)
	  * @return
	 */
	public Integer answerReplyLikeIdRemainder(String answerReplyLikeId){
	   int answerReplyId = likeManage.getItemLikeId(answerReplyLikeId);
	   return answerReplyId % this.getTableQuantity();
	} 
   /**
    * 根据答案回复Id查询分配到表编号
    * 根据答案回复Id和答案回复点赞分表数量求余(用户Id后四位)
    * @param answerReplyId 答案回复Id
    * @return
    */
	public Integer answerReplyIdRemainder(Long answerReplyId){
	   	//选取得后N位答案回复Id
	   	String afterAnswerReplyId = String.format("%04d", answerReplyId%10000);
	   	return Integer.parseInt(afterAnswerReplyId) % this.getTableQuantity();
	}
	 
}
