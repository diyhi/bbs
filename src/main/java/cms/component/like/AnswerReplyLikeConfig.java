package cms.component.like;


import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 答案回复点赞配置
 *
 */
@Component("answerReplyLikeConfig")
public class AnswerReplyLikeConfig {

    @Resource LikeConfig likeConfig;
	
	/** 分表数量 **/
	@Value("${bbs.sharding.answerReplyLikeConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;
	
	/**
	  * 根据答案回复点赞Id查询分配到表编号
	  * 根据答案回复点赞Id和答案回复点赞分表数量求余
	  * @param answerReplyLikeId 答案回复点赞Id
	  * 注意：答案回复点赞Id要先判断最后4位是不是数字
	  * likeManage.verificationItemLikeId(?)
	  * @return
	 */
	public Integer answerReplyLikeIdRemainder(String answerReplyLikeId){
	   int answerReplyId = likeConfig.getItemLikeId(answerReplyLikeId);
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
