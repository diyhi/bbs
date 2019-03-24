package cms.web.action.favorite;

import javax.annotation.Resource;

/**
 * 收藏夹配置
 *
 */
public class FavoritesConfig {
	@Resource FavoriteManage favoriteManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据收藏夹Id查询分配到表编号
	  * 根据收藏夹Id和收藏夹分表数量求余
	  * @param favoriteId 收藏夹Id
	  * 注意：收藏夹Id要先判断是否36位并且最后4位是数字
	  * favoriteManage.verificationFavoriteId(?)
	  * @return
	 */
	public Integer favoriteIdRemainder(String favoriteId){
	   int userId = favoriteManage.getFavoriteId(favoriteId);
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
	 
}
