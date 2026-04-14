package cms.component.like;


import cms.utils.Verification;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 答案点赞配置
 *
 */
@Component("answerLikeConfig")
public class AnswerLikeConfig {

    @Resource LikeConfig likeConfig;
	
	/** 分表数量 **/
	@Value("${bbs.sharding.answerLikeConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;
	
	/**
	  * 根据答案点赞Id查询分配到表编号
	  * 根据答案点赞Id和答案点赞分表数量求余
	  * @param answerLikeId 答案点赞Id
	  * 注意：答案点赞Id要先判断最后4位是不是数字
	  * likeManage.verificationTopicLikeId(?)
	  * @return
	 */
	public Integer answerLikeIdRemainder(String answerLikeId){
	   int answerId = likeConfig.getItemLikeId(answerLikeId);
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
