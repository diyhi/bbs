package cms.web.action.like;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 问题点赞配置
 *
 */
public class QuestionLikeConfig {
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
	  * 根据问题点赞Id查询分配到表编号
	  * 根据问题点赞Id和问题点赞分表数量求余
	  * @param questionLikeId 问题点赞Id
	  * 注意：问题点赞Id要先判断最后4位是不是数字
	  * likeManage.verificationTopicLikeId(?)
	  * @return
	 */
	public Integer questionLikeIdRemainder(String questionLikeId){
	   int questionId = likeManage.getItemLikeId(questionLikeId);
	   return questionId % this.getTableQuantity();
	} 
   /**
    * 根据问题Id查询分配到表编号
    * 根据问题Id和问题点赞分表数量求余(用户Id后四位)
    * @param questionId 问题Id
    * @return
    */
	public Integer questionIdRemainder(Long questionId){
	   	//选取得后N位问题Id
	   	String afterQuestionId = String.format("%04d", questionId%10000);
	   	return Integer.parseInt(afterQuestionId) % this.getTableQuantity();
	}
	 
}
