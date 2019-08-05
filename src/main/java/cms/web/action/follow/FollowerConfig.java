package cms.web.action.follow;

import javax.annotation.Resource;

/**
 * 粉丝配置
 *
 */
public class FollowerConfig {
	@Resource FollowerManage followerManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据粉丝Id查询分配到表编号
	  * 根据粉丝Id和粉丝分表数量求余
	  * @param followerId 粉丝Id
	  * 注意：粉丝Id要验证
	  * followerManage.verificationFollowerId(?)
	  * @return
	 */
	public Integer followerIdRemainder(String followerId){
	   int userId = followerManage.getFollowerAfterId(followerId);
	   return userId % this.getTableQuantity();
	} 
   /**
    * 根据用户Id查询分配到表编号
    * 根据用户Id和粉丝分表数量求余(用户Id后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer userIdRemainder(Long userId){
	   	//选取得后N位用户Id
	   	String afterUserId = String.format("%04d", userId%10000);
	   	return Integer.parseInt(afterUserId) % this.getTableQuantity();
	}
	 
}
