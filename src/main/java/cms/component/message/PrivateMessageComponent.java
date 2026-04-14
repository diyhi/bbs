package cms.component.message;


import cms.model.message.PrivateMessage;
import jakarta.annotation.Resource;
import org.springframework.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 私信组件
 *
 */
@Component("privateMessageComponent")
public class PrivateMessageComponent {
	private static final Logger logger = LogManager.getLogger(PrivateMessageComponent.class);
	
    @Resource PrivateMessageConfig privateMessageConfig;

    
    

    
    /**
     * 生成私信对象
     * @return
     */
    public Object createPrivateMessageObject(PrivateMessage privateMessage){
    	//表编号
		int tableNumber = privateMessageConfig.privateMessageIdRemainder(privateMessage.getId());
		if(tableNumber == 0){//默认对象为PrivateMessage
			return privateMessage;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.model.message.PrivateMessage_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
				BeanCopier copier = BeanCopier.create(PrivateMessage.class,object.getClass(), false); 
			
				copier.copy(privateMessage,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成私信对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成私信对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成私信对象",e);
		        }
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成私信对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成私信对象",e);
                }
            }
		}
		return null;
    }
    
    


    
    
    
    
}
