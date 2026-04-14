package cms.component.message;

import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 订阅系统通知配置
 *
 */
@Component("subscriptionSystemNotifyConfig")
public class SubscriptionSystemNotifyConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.subscriptionSystemNotifyConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	/**
	  * 根据订阅系统通知Id查询分配到表编号
	  * 根据订阅系统通知Id和订阅系统通知分表数量求余
	  * @param subscriptionSystemNotifyId 订阅系统通知Id
	  * 注意：私信Id要先判断是否36位并且最后4位是数字
	  * subscriptionSystemNotifyManage.verificationSubscriptionSystemNotifyId(?)
	  * @return
	 */
	public Integer subscriptionSystemNotifyIdRemainder(String subscriptionSystemNotifyId){
	   int userId = this.getSubscriptionSystemNotifyId(subscriptionSystemNotifyId);
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

    /**
     * 取得订阅系统通知Id的用户Id(后N位)
     * @param subscriptionSystemNotifyId 订阅系统通知Id
     * @return
     */
    public int getSubscriptionSystemNotifyId(String subscriptionSystemNotifyId){
        String after_userId = subscriptionSystemNotifyId.substring(subscriptionSystemNotifyId.length()-4, subscriptionSystemNotifyId.length());
        return Integer.parseInt(after_userId);
    }

    /**
     * 生成订阅系统通知Id
     * 订阅系统通知Id由18位系统通知Id + 18位用户Id组成
     * @param systemNotifyId 系统通知Id
     * @param userId 用户Id
     * @return
     */
    public String createSubscriptionSystemNotifyId(Long systemNotifyId,Long userId){
        if(systemNotifyId < 999999999999999999L && userId < 999999999999999999L){
            // 0 代表前面补充0
            // 4 代表长度为4
            // d 代表参数为正数型
            String before = String.format("%018d", systemNotifyId);//补0
            String after = String.format("%018d", userId);//补0
            return before + after;
        }
        return null;
    }
    /**
     * 校验订阅系统通知Id
     * 订阅系统通知Id要先判断是否36位并且最后4位是数字
     * @param subscriptionSystemNotifyId 私信Id
     * @return
     */
    public boolean verificationSubscriptionSystemNotifyId(String subscriptionSystemNotifyId){
        if(subscriptionSystemNotifyId != null && !"".equals(subscriptionSystemNotifyId.trim())){
            if(subscriptionSystemNotifyId.length() == 36){
                String after_userId = subscriptionSystemNotifyId.substring(subscriptionSystemNotifyId.length()-4, subscriptionSystemNotifyId.length());
                boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
                if(verification){
                    return true;
                }
            }
        }
        return false;
    }
}
