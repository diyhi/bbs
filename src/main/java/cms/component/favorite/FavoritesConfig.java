package cms.component.favorite;


import cms.utils.UUIDUtil;
import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 收藏夹配置
 *
 */
@Component("favoritesConfig")
public class FavoritesConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.favoritesConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	/**
	  * 根据收藏夹Id查询分配到表编号
	  * 根据收藏夹Id和收藏夹分表数量求余
	  * @param favoriteId 收藏夹Id
	  * 注意：收藏夹Id要先判断是否36位并且最后4位是数字
	  * favoriteManage.verificationFavoriteId(?)
	  * @return
	 */
	public Integer favoriteIdRemainder(String favoriteId){
	   int userId = this.getFavoriteId(favoriteId);
	   return userId % this.getTableQuantity();
	} 
   /**
    * 根据收藏夹Id查询分配到表编号
    * 根据收藏夹Id和收藏夹分表数量求余(用户Id后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer userIdRemainder(Long userId){
	   	//选取得后N位用户Id
	   	String afterUserId = String.format("%04d", userId%10000);
	   	return Integer.parseInt(afterUserId) % this.getTableQuantity();
	}


    /**
     * 取得收藏夹Id的用户Id(后N位)
     * @param favoriteId 收藏夹Id
     * @return
     */
    public int getFavoriteId(String favoriteId){
        String after_userId = favoriteId.substring(favoriteId.length()-4, favoriteId.length());
        return Integer.parseInt(after_userId);
    }

    /**
     * 生成收藏夹Id
     * 收藏夹Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createFavoriteId(Long userId){
        //选取得后N位用户Id
        String afterUserId = String.format("%04d", userId%10000);
        String id = UUIDUtil.getUUID32()+afterUserId;
        return id;
    }
    /**
     * 校验收藏夹Id
     * 收藏夹Id要先判断是否36位并且最后4位是数字
     * @param favoriteId 收藏夹Id
     * @return
     */
    public boolean verificationFavoriteId(String favoriteId){
        if(favoriteId != null && !"".equals(favoriteId.trim())){
            if(favoriteId.length() == 36){
                String after_userId = favoriteId.substring(favoriteId.length()-4, favoriteId.length());
                boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
                if(verification){
                    return true;
                }
            }
        }
        return false;
    }

}
