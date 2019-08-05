package cms.web.action.like;

import javax.annotation.Resource;

/**
 * 点赞配置
 *
 */
public class LikeConfig {
	@Resource LikeManage likeManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据点赞Id查询分配到表编号
	  * 根据点赞Id和点赞分表数量求余
	  * @param likeId 点赞Id
	  * 注意：点赞Id要先判断是否36位并且最后4位是数字
	  * likeManage.verificationLikeId(?)
	  * @return
	 */
	public Integer likeIdRemainder(String likeId){
	   int userId = likeManage.getLikeId(likeId);
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
	 
}
