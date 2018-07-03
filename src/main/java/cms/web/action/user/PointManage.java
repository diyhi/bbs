package cms.web.action.user;


import javax.annotation.Resource;

import cms.bean.user.PointLog;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 积分管理
 *
 */
@Component("pointManage")
public class PointManage {
	private static final Logger logger = LogManager.getLogger(PointManage.class);
	
    @Resource PointLogConfig pointLogConfig;

	/**
	 * 取得积分日志Id的用户Id(后N位)
	 * @param pointLogId 积分日志Id
	 * @return
	 */
    public int getPointLogUserId(String pointLogId){
    	String after_userId = pointLogId.substring(pointLogId.length()-4, pointLogId.length());
    	return Integer.parseInt(after_userId);
    } 
    
    /**
     * 生成积分Id
     * 积分Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createPointLogId(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	String id = UUIDUtil.getUUID32()+afterUserId;
    	return id;
    }
    /**
     * 校验积分Id
     * 积分Id要先判断是否36位并且最后4位是数字
     * @param pointLogId 积分Id
     * @return
     */
    public boolean verificationPointLogId(String pointLogId){
    	if(pointLogId != null && !"".equals(pointLogId.trim())){
    		if(pointLogId.length() == 36){
    			String after_userId = pointLogId.substring(pointLogId.length()-4, pointLogId.length());		
    			boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
				if(verification){
					return true;
				}
    		}
    	}
    	return false;
    }
    
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
				c = Class.forName("cms.bean.user.PointLog_"+tableNumber);
				Object object = c.newInstance();
				
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
			}	
		}
		return null;
    }
    
}
