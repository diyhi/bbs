package cms.web.action.user;


import javax.annotation.Resource;

import cms.bean.user.UserDynamic;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 用户动态管理
 *
 */
@Component("userDynamicManage")
public class UserDynamicManage {
	private static final Logger logger = LogManager.getLogger(UserDynamicManage.class);
	
    @Resource UserDynamicConfig userDynamicConfig;

	/**
	 * 取得用户动态Id的用户Id(后N位)
	 * @param userDynamicId 用户动态Id
	 * @return
	 */
    public int getUserDynamicUserId(String userDynamicId){
    	String after_userId = userDynamicId.substring(userDynamicId.length()-4, userDynamicId.length());
    	return Integer.parseInt(after_userId);
    } 
    
    /**
     * 生成用户动态Id
     * 用户动态Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createUserDynamicId(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	String id = UUIDUtil.getUUID32()+afterUserId;
    	return id;
    }
    /**
     * 校验用户动态Id
     * 用户动态Id要先判断是否36位并且最后4位是数字
     * @param userDynamicId 用户动态Id
     * @return
     */
    public boolean verificationUserDynamicId(String userDynamicId){
    	if(userDynamicId != null && !"".equals(userDynamicId.trim())){
    		if(userDynamicId.length() == 36){
    			String after_userId = userDynamicId.substring(userDynamicId.length()-4, userDynamicId.length());		
    			boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
				if(verification){
					return true;
				}
    		}
    	}
    	return false;
    }
    
    /**
     * 生成用户动态对象
     * @return
     */
    public Object createUserDynamicObject(UserDynamic userDynamic){
    	//表编号
		int tableNumber = userDynamicConfig.userDynamicIdRemainder(userDynamic.getId());
		if(tableNumber == 0){//默认对象为PaymentLog
			return userDynamic;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.user.UserDynamic_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(UserDynamic.class,object.getClass(), false); 
			
				copier.copy(userDynamic,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成用户动态对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成用户动态对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成用户动态对象",e);
		        }
			}	
		}
		return null;
    }
    
}
