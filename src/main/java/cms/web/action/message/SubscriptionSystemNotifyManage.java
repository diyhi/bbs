package cms.web.action.message;



import javax.annotation.Resource;

import cms.bean.message.SubscriptionSystemNotify;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 订阅系统通知管理
 *
 */
@Component("subscriptionSystemNotifyManage")
public class SubscriptionSystemNotifyManage {
	private static final Logger logger = LogManager.getLogger(SubscriptionSystemNotifyManage.class);
	
    @Resource SubscriptionSystemNotifyConfig subscriptionSystemNotifyConfig;
	
	/**
	 * 取得订阅系统通知Id的用户Id(后N位)
	 * @param subscriptionSystemNotify 订阅系统通知Id
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
    
    /**
     * 生成订阅系统通知对象
     * @return
     */
    public Object createSubscriptionSystemNotifyObject(SubscriptionSystemNotify subscriptionSystemNotify){	
    	//表编号
		int tableNumber = subscriptionSystemNotifyConfig.subscriptionSystemNotifyIdRemainder(subscriptionSystemNotify.getId());
		if(tableNumber == 0){//默认对象为SubscriptionSystemNotify
			return subscriptionSystemNotify;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.message.SubscriptionSystemNotify_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(SubscriptionSystemNotify.class,object.getClass(), false); 
			
				copier.copy(subscriptionSystemNotify,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成订阅系统通知对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成订阅系统通知对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成订阅系统通知对象",e);
		        }
			}	
		}
		return null;
    }
    
    

    
    
}
