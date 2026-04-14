package cms.component.like;

import cms.utils.UUIDUtil;
import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 点赞配置
 *
 */
@Component("likeConfig")
public class LikeConfig {
	/** 分表数量 **/
	@Value("${bbs.sharding.likeConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	/**
	  * 根据点赞Id查询分配到表编号
	  * 根据点赞Id和点赞分表数量求余
	  * @param likeId 点赞Id
	  * 注意：点赞Id要先判断是否36位并且最后4位是数字
	  * likeManage.verificationLikeId(?)
	  * @return
	 */
	public Integer likeIdRemainder(String likeId){
	   int userId = this.getLikeId(likeId);
	   return userId % this.getTableQuantity();
	} 
   /**
    * 根据用户Id查询分配到表编号
    * 根据用户Id和点赞分表数量求余(用户Id后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer userIdRemainder(Long userId){
	   	//选取得后N位用户Id
	   	String afterUserId = String.format("%04d", userId%10000);
	   	return Integer.parseInt(afterUserId) % this.getTableQuantity();
	}

    /**
     * 取得点赞Id的用户Id(后N位)
     * @param likeId 点赞Id
     * @return
     */
    public int getLikeId(String likeId){
        String after_userId = likeId.substring(likeId.length()-4, likeId.length());
        return Integer.parseInt(after_userId);
    }

    /**
     * 生成点赞Id
     * 点赞Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createLikeId(Long userId){
        //选取得后N位用户Id
        String afterUserId = String.format("%04d", userId%10000);
        String id = UUIDUtil.getUUID32()+afterUserId;
        return id;
    }
    /**
     * 校验点赞Id
     * 点赞Id要先判断是否36位并且最后4位是数字
     * @param likeId 点赞Id
     * @return
     */
    public boolean verificationLikeId(String likeId){
        if(likeId != null && !"".equals(likeId.trim())){
            if(likeId.length() == 36){
                String after_userId = likeId.substring(likeId.length()-4, likeId.length());
                boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
                if(verification){
                    return true;
                }
            }
        }
        return false;
    }



    /**
     * 取得项目点赞Id的项目Id(后N位)   例如：取得话题点赞Id的话题Id(后N位)
     * @param itemLikeId 项目点赞Id  例如：话题点赞Id
     * @return
     */
    public int getItemLikeId(String itemLikeId){
        String[] idGroup = itemLikeId.split("_");
        Long itemId = Long.parseLong(idGroup[0]);

        //选取得后N位项目Id
        String after_itemId = String.format("%04d", Math.abs(itemId)%10000);
        return Integer.parseInt(after_itemId);
    }

    /**
     * 生成项目点赞Id  例如：生成话题点赞Id  话题点赞Id格式（话题Id_用户Id）
     * @param itemId 项目Id  例如：话题Id
     * @param userId 用户Id
     * @return
     */
    public String createItemLikeId(Long itemId,Long userId){
        return itemId+"_"+userId;
    }
    /**
     * 校验项目点赞Id  例如：校验话题点赞Id
     * 话题点赞Id要先判断最后4位是不是数字
     * @param itemLikeId 项目点赞Id  例如：话题点赞Id
     * @return
     */
    public boolean verificationItemLikeId(String itemLikeId){
        if(itemLikeId != null && !"".equals(itemLikeId.trim())){
            String[] idGroup = itemLikeId.split("_");
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
