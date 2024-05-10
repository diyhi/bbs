package cms.web.action.like;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 答案点赞配置
 *
 */
public class AnswerLikeConfig {
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
	  * 根据答案点赞Id查询分配到表编号
	  * 根据答案点赞Id和答案点赞分表数量求余
	  * @param answerLikeId 答案点赞Id
	  * 注意：答案点赞Id要先判断最后4位是不是数字
	  * likeManage.verificationTopicLikeId(?)
	  * @return
	 */
	public Integer answerLikeIdRemainder(String answerLikeId){
	   int answerId = likeManage.getItemLikeId(answerLikeId);
	   return answerId % this.getTableQuantity();
	} 
   /**
    * 根据答案Id查询分配到表编号
    * 根据答案Id和答案点赞分表数量求余(用户Id后四位)
    * @param answerId 答案Id
    * @return
    */
	public Integer answerIdRemainder(Long answerId){
	   	//选取得后N位答案Id
	   	String afterAnswerId = String.format("%04d", answerId%10000);
	   	return Integer.parseInt(afterAnswerId) % this.getTableQuantity();
	}
	 
}
