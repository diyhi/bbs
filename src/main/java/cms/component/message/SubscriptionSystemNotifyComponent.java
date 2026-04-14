package cms.component.message;




import cms.model.message.SubscriptionSystemNotify;
import jakarta.annotation.Resource;
import org.springframework.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 订阅系统通知组件
 *
 */
@Component("subscriptionSystemNotifyComponent")
public class SubscriptionSystemNotifyComponent {
	private static final Logger logger = LogManager.getLogger(SubscriptionSystemNotifyComponent.class);
	
    @Resource SubscriptionSystemNotifyConfig subscriptionSystemNotifyConfig;
	

    
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
				c = Class.forName("cms.model.message.SubscriptionSystemNotify_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成订阅系统通知对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成订阅系统通知对象",e);
                }
            }
		}
		return null;
    }
    
    

    
    
}
