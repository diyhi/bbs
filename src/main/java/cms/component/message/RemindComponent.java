package cms.component.message;




import cms.model.message.Remind;
import cms.repository.message.RemindRepository;

import jakarta.annotation.Resource;
import org.springframework.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 提醒组件
 *
 */
@Component("remindComponent")
public class RemindComponent {
	private static final Logger logger = LogManager.getLogger(RemindComponent.class);
	
    @Resource RemindConfig remindConfig;


    
    /**
     * 生成提醒对象
     * @return
     */
    public Object createRemindObject(Remind remind){	
    	//表编号
		int tableNumber = remindConfig.remindIdRemainder(remind.getId());
		if(tableNumber == 0){//默认对象为remind
			return remind;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.model.message.Remind_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
				BeanCopier copier = BeanCopier.create(Remind.class,object.getClass(), false);
			
				copier.copy(remind,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成提醒对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成提醒对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成提醒对象",e);
		        }
			}catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成提醒对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成提醒对象",e);
                }
            }
		}
		return null;
    }
    
    



    
}
