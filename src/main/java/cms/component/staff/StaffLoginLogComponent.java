package cms.component.staff;





import cms.model.staff.StaffLoginLog;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import jakarta.annotation.Resource;
import org.springframework.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 员工登录日志组件
 *
 */
@Component("staffLoginLogComponent")
public class StaffLoginLogComponent {
	private static final Logger logger = LogManager.getLogger(StaffLoginLogComponent.class);

    @Resource StaffLoginLogShardingConfig staffLoginLogShardingConfig;
	

    
    /**
     * 生成员工登录日志对象
     * @return
     */
    public Object createStaffLoginLogObject(StaffLoginLog staffLoginLog){
    	//表编号
		int tableNumber = staffLoginLogShardingConfig.staffLoginLogIdRemainder(staffLoginLog.getId());
		if(tableNumber == 0){//默认对象为UserLoginLog
			return staffLoginLog;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.model.staff.StaffLoginLog_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
				BeanCopier copier = BeanCopier.create(StaffLoginLog.class,object.getClass(), false); 
			
				copier.copy(staffLoginLog,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
    	            logger.error("生成员工登录日志对象",e);
    	        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
    	            logger.error("生成员工登录日志对象",e);
    	        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
    	            logger.error("生成员工登录日志对象",e);
    	        }
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成员工登录日志对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成员工登录日志对象",e);
                }
            }
        }
		return null;
    }

}
