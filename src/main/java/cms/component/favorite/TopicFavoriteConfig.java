package cms.component.favorite;

import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 话题收藏配置
 *
 */
@Component("topicFavoriteConfig")
public class TopicFavoriteConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.topicFavoriteConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	/**
	  * 根据话题收藏Id查询分配到表编号
	  * 根据话题收藏Id和话题收藏分表数量求余
	  * @param topicFavoriteId 收藏夹Id
	  * 注意：话题收藏Id要先判断最后4位是不是数字
	  * favoriteManage.verificationTopicFavoriteId(?)
	  * @return
	 */
	public Integer topicFavoriteIdRemainder(String topicFavoriteId){
	   int topicId = this.getTopicFavoriteId(topicFavoriteId);
	   return topicId % this.getTableQuantity();
	} 
   /**
    * 根据话题Id查询分配到表编号
    * 根据话题Id和话题收藏分表数量求余(用户Id后四位)
    * @param topicId 话题Id
    * @return
    */
	public Integer topicIdRemainder(Long topicId){
	   	//选取得后N位话题Id
	   	String afterTopicId = String.format("%04d", topicId%10000);
	   	return Integer.parseInt(afterTopicId) % this.getTableQuantity();
	}


    /**
     * 取得话题收藏Id的话题Id(后N位)
     * @param topicFavoriteId 话题收藏Id
     * @return
     */
    public int getTopicFavoriteId(String topicFavoriteId){
        String[] idGroup = topicFavoriteId.split("_");
        Long topicId = Long.parseLong(idGroup[0]);

        //选取得后N位话题Id
        String after_topicId = String.format("%04d", Math.abs(topicId)%10000);
        return Integer.parseInt(after_topicId);
    }

    /**
     * 生成话题收藏Id
     * 话题收藏Id格式（话题Id_用户Id）
     * @param topicId 话题Id
     * @param userId 用户Id
     * @return
     */
    public String createTopicFavoriteId(Long topicId,Long userId){
        return topicId+"_"+userId;
    }
    /**
     * 校验话题收藏Id
     * 话题收藏Id要先判断最后4位是不是数字
     * @param topicFavoriteId 话题收藏Id
     * @return
     */
    public boolean verificationTopicFavoriteId(String topicFavoriteId){
        if(topicFavoriteId != null && !"".equals(topicFavoriteId.trim())){
            String[] idGroup = topicFavoriteId.split("_");
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
