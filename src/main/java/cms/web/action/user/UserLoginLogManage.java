package cms.web.action.user;



import javax.annotation.Resource;

import cms.bean.user.UserLoginLog;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 用户登录日志管理
 *
 */
@Component("userLoginLogManage")
public class UserLoginLogManage {
	private static final Logger logger = LogManager.getLogger(UserLoginLogManage.class);
	
    @Resource UserLoginLogConfig userLoginLogConfig;
	
	/**
	 * 取得用户登录日志Id的用户Id(后N位)
	 * @param userLoginLogId 用户登录日志Id
	 * @return
	 */
    public int getUserLoginLogUserId(String userLoginLogId){
    	String after_userId = userLoginLogId.substring(userLoginLogId.length()-4, userLoginLogId.length());
    	return Integer.parseInt(after_userId);
    } 
    
    /**
     * 生成用户登录日志Id
     * 积分Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createUserLoginLogId(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	String id = UUIDUtil.getUUID32()+afterUserId;
    	return id;
    }
    /**
     * 校验用户登录日志Id
     * 用户登录日志Id要先判断是否36位并且最后4位是数字
     * @param userLoginLogId 用户登录日志Id
     * @return
     */
    public boolean verificationUserLoginLogId(String userLoginLogId){
    	if(userLoginLogId != null && !"".equals(userLoginLogId.trim())){
    		if(userLoginLogId.length() == 36){
    			String after_userId = userLoginLogId.substring(userLoginLogId.length()-4, userLoginLogId.length());		
    			boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
				if(verification){
					return true;
				}
    		}
    	}
    	return false;
    }
    
    /**
     * 生成用户登录日志对象
     * @return
     */
    public Object createUserLoginLogObject(UserLoginLog userLoginLog){
    	//表编号
		int tableNumber = userLoginLogConfig.userLoginLogIdRemainder(userLoginLog.getId());
		if(tableNumber == 0){//默认对象为UserLoginLog
			return userLoginLog;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.user.UserLoginLog_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(UserLoginLog.class,object.getClass(), false); 
			
				copier.copy(userLoginLog,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成用户登录日志对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成用户登录日志对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成用户登录日志对象",e);
		        }
			}	
		}
		return null;
    }
    
    

    
    
}
