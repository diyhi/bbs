package cms.component.topic;

import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 话题取消隐藏配置
 *
 */
@Component("topicUnhideConfig")
public class TopicUnhideConfig {

	
	/** 分表数量 **/
	@Value("${bbs.sharding.topicUnhideConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;
	

	/**
	  * 根据话题取消隐藏Id查询分配到表编号
	  * 根据'话题取消隐藏Id'和'话题取消隐藏'分表数量求余
	  * @param topicUnhideId 话题取消隐藏Id
	  * 校验话题取消隐藏Id使用方法: topicManage.verificationTopicUnhideId(?)
	  * @return
	 */
	public Integer topicUnhideIdRemainder(String topicUnhideId){
	   int topicId = this.analysisTopicId(topicUnhideId);
	   return topicId % this.getTableQuantity();
	} 
  /**
   * 根据话题Id查询分配到表编号
   * 根据话题Id和话题'话题取消隐藏'分表数量求余(用户Id后四位)
   * @param topicId 话题Id
   * @return
   */
	public Integer topicIdRemainder(Long topicId){
	   	//选取得后N位话题Id
	   	String afterTopicId = String.format("%04d", topicId%10000);
	   	return Integer.parseInt(afterTopicId) % this.getTableQuantity();
	}


    /**
     * 生成'话题取消隐藏Id'
     * 话题取消隐藏Id格式（取消隐藏的用户Id-隐藏标签类型-话题Id）
     * @param userId 用户Id
     * @param hideTagTypeId 隐藏标签类型Id
     * @param topicId 话题Id
     * @return
     */
    public String createTopicUnhideId(Long userId,Integer hideTagTypeId,Long topicId){
        return userId+"_"+hideTagTypeId+"_"+topicId;
    }

    /**
     * 解析'话题取消隐藏Id'的话题Id(后N位)
     * @param topicUnhideId 话题取消隐藏Id
     * @return
     */
    public int analysisTopicId(String topicUnhideId){
        String[] idGroup = topicUnhideId.split("_");
        Long topicId = Long.parseLong(idGroup[2]);

        //选取得后N位话题Id
        String after_topicId = String.format("%04d", Math.abs(topicId)%10000);
        return Integer.parseInt(after_topicId);
    }


    /**
     * 校验话题取消隐藏Id
     * @param topicUnhideId 话题取消隐藏Id
     * @return
     */
    public boolean verificationTopicUnhideId(String topicUnhideId){
        if(topicUnhideId != null && !"".equals(topicUnhideId.trim())){
            String[] idGroup = topicUnhideId.split("_");
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
