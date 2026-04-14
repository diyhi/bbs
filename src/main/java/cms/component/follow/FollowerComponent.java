package cms.component.follow;



import cms.model.follow.Follower;
import cms.repository.follow.FollowRepository;
import jakarta.annotation.Resource;
import org.springframework.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 粉丝组件
 *
 */
@Component("followerComponent")
public class FollowerComponent {
	private static final Logger logger = LogManager.getLogger(FollowerComponent.class);
	
    @Resource
    FollowerConfig followerConfig;




    
    /**
     * 生成粉丝对象
     * @return
     */
    public Object createFollowerObject(Follower follower){
    	//表编号
		int tableNumber = followerConfig.followerIdRemainder(follower.getId());
		if(tableNumber == 0){//默认对象
			return follower;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.model.follow.Follower_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
				BeanCopier copier = BeanCopier.create(Follower.class,object.getClass(), false); 
			
				copier.copy(follower,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成粉丝对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成粉丝对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成粉丝对象",e);
		        }
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成粉丝对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成粉丝对象",e);
                }
            }
		}
		return null;
    }
   
   

    
}
