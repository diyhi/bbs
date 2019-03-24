package cms.web.action.favorite;

import javax.annotation.Resource;

/**
 * 话题收藏配置
 *
 */
public class TopicFavoriteConfig {
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
	  * 根据话题收藏Id查询分配到表编号
	  * 根据话题收藏Id和话题收藏分表数量求余
	  * @param topicFavoriteId 收藏夹Id
	  * 注意：话题收藏Id要先判断最后4位是不是数字
	  * favoriteManage.verificationTopicFavoriteId(?)
	  * @return
	 */
	public Integer topicFavoriteIdRemainder(String topicFavoriteId){
	   int topicId = favoriteManage.getTopicFavoriteId(topicFavoriteId);
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
	 
}
