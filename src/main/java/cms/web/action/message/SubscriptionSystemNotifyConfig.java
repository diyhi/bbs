package cms.web.action.message;

import javax.annotation.Resource;

/**
 * 订阅系统通知配置
 *
 */
public class SubscriptionSystemNotifyConfig {
	@Resource SubscriptionSystemNotifyManage subscriptionSystemNotifyManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据订阅系统通知Id查询分配到表编号
	  * 根据订阅系统通知Id和订阅系统通知分表数量求余
	  * @param subscriptionSystemNotifyId 订阅系统通知Id
	  * 注意：私信Id要先判断是否36位并且最后4位是数字
	  * subscriptionSystemNotifyManage.verificationSubscriptionSystemNotifyId(?)
	  * @return
	 */
	public Integer subscriptionSystemNotifyIdRemainder(String subscriptionSystemNotifyId){
	   int userId = subscriptionSystemNotifyManage.getSubscriptionSystemNotifyId(subscriptionSystemNotifyId);
	   return userId % this.getTableQuantity();
	} 
   /**
    * 根据订阅系统通知Id查询分配到表编号
    * 根据订阅系统通知Id和订阅系统通知分表数量求余(用户Id后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer userIdRemainder(Long userId){
	   	//选取得后N位用户Id
	   	String afterUserId = String.format("%04d", userId%10000);
	   	return Integer.parseInt(afterUserId) % this.getTableQuantity();
	}
	 
}
