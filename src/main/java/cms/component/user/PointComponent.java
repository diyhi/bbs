package cms.component.user;



import cms.model.user.PointLog;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 积分组件
 *
 */
@Component("pointComponent")
public class PointComponent {
	private static final Logger logger = LogManager.getLogger(PointComponent.class);
	
    @Resource PointLogConfig pointLogConfig;


    /**
     * 生成积分日志对象
     * @return
     */
    public Object createPointLogObject(PointLog pointLog){
    	//表编号
		int tableNumber = pointLogConfig.pointLogIdRemainder(pointLog.getId());
		if(tableNumber == 0){//默认对象为PaymentLog
			return pointLog;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.model.user.PointLog_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
				BeanCopier copier = BeanCopier.create(PointLog.class,object.getClass(), false); 
			
				copier.copy(pointLog,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成积分日志对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成积分日志对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成积分日志对象",e);
		        }
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成积分日志对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成积分日志对象",e);
                }
            }
		}
		return null;
    }
    
}
