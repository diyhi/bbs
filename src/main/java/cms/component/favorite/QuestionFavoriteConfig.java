package cms.component.favorite;


import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 问题收藏配置
 *
 */
@Component("questionFavoriteConfig")
public class QuestionFavoriteConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.questionFavoriteConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	/**
	  * 根据问题收藏Id查询分配到表编号
	  * 根据问题收藏Id和问题收藏分表数量求余
	  * @param questionFavoriteId 收藏夹Id
	  * 注意：问题收藏Id要先判断最后4位是不是数字
	  * favoriteManage.verificationQuestionFavoriteId(?)
	  * @return
	 */
	public Integer questionFavoriteIdRemainder(String questionFavoriteId){
	   int questionId = this.getQuestionFavoriteId(questionFavoriteId);
	   return questionId % this.getTableQuantity();
	} 
   /**
    * 根据问题Id查询分配到表编号
    * 根据问题Id和问题收藏分表数量求余(用户Id后四位)
    * @param questionId 问题Id
    * @return
    */
	public Integer questionIdRemainder(Long questionId){
	   	//选取得后N位问题Id
	   	String afterQuestionId = String.format("%04d", questionId%10000);
	   	return Integer.parseInt(afterQuestionId) % this.getTableQuantity();
	}

    /**
     * 取得问题收藏Id的问题Id(后N位)
     * @param questionFavoriteId 问题收藏Id
     * @return
     */
    public int getQuestionFavoriteId(String questionFavoriteId){
        String[] idGroup = questionFavoriteId.split("_");
        Long questionId = Long.parseLong(idGroup[0]);

        //选取得后N位问题Id
        String after_questionId = String.format("%04d", Math.abs(questionId)%10000);
        return Integer.parseInt(after_questionId);
    }

    /**
     * 生成问题收藏Id
     * 问题收藏Id格式（问题Id_用户Id）
     * @param questionId 问题Id
     * @param userId 用户Id
     * @return
     */
    public String createQuestionFavoriteId(Long questionId,Long userId){
        return questionId+"_"+userId;
    }
    /**
     * 校验问题收藏Id
     * 问题收藏Id要先判断最后4位是不是数字
     * @param questionFavoriteId 问题收藏Id
     * @return
     */
    public boolean verificationQuestionFavoriteId(String questionFavoriteId){
        if(questionFavoriteId != null && !"".equals(questionFavoriteId.trim())){
            String[] idGroup = questionFavoriteId.split("_");
            for(String id : idGroup){
                boolean verification = Verification.isPositiveIntegerZero(id);//数字
                if(!verification){
                    return false;
                }
                return true;
            }

        }
        return false;
    }

}
