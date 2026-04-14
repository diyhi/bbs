package cms.component.like;


import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 问题点赞配置
 *
 */
@Component("questionLikeConfig")
public class QuestionLikeConfig {
    @Resource LikeConfig likeConfig;
	
	/** 分表数量 **/
	@Value("${bbs.sharding.questionLikeConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	/**
	  * 根据问题点赞Id查询分配到表编号
	  * 根据问题点赞Id和问题点赞分表数量求余
	  * @param questionLikeId 问题点赞Id
	  * 注意：问题点赞Id要先判断最后4位是不是数字
	  * likeManage.verificationTopicLikeId(?)
	  * @return
	 */
	public Integer questionLikeIdRemainder(String questionLikeId){
	   int questionId = likeConfig.getItemLikeId(questionLikeId);
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
