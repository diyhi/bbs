package cms.web.action.staff;



import javax.annotation.Resource;

import cms.bean.staff.StaffLoginLog;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 员工登录日志管理
 *
 */
@Component("staffLoginLogManage")
public class StaffLoginLogManage {
	private static final Logger logger = LogManager.getLogger(StaffLoginLogManage.class);
	
    @Resource StaffLoginLogConfig staffLoginLogConfig;
	
	/**
	 * 取得员工登录日志Id的员工Id哈希值(后N位)
	 * @param staffLoginLogId 员工登录日志Id
	 * @return
	 */
    public int getStaffLoginLogUserId(String staffLoginLogId){
    	String after_staffId = staffLoginLogId.substring(staffLoginLogId.length()-4, staffLoginLogId.length());
    	return Integer.parseInt(after_staffId);
    } 
    
    /**
     * 生成员工登录日志Id
     * 登录日志Id由32位uuid+员工Id哈希值后4位组成
     * @param staffId 员工Id
     * @return
     */
    public String createStaffLoginLogId(String staffId){
    	//员工Id哈希值后N位,哈希值有可能负数，所以要计算绝对值
    	String afterStaffId = String.format("%04d", Math.abs(staffId.hashCode()%10000));
    	String id = UUIDUtil.getUUID32()+afterStaffId;
    	return id;
    }
   
    
    /**
     * 校验员工登录日志Id
     * 员工登录日志Id要先判断是否36位并且最后4位是数字
     * @param staffLoginLogId 员工登录日志Id
     * @return
     */
    public boolean verificationStaffLoginLogId(String staffLoginLogId){
    	if(staffLoginLogId != null && !"".equals(staffLoginLogId.trim())){
    		if(staffLoginLogId.length() == 36){
    			String after_staffId = staffLoginLogId.substring(staffLoginLogId.length()-4, staffLoginLogId.length());		
    			boolean verification = Verification.isPositiveIntegerZero(after_staffId);//数字
				if(verification){
					return true;
				}
    		}
    	}
    	return false;
    }
    
    /**
     * 生成员工登录日志对象
     * @return
     */
    public Object createStaffLoginLogObject(StaffLoginLog staffLoginLog){
    	//表编号
		int tableNumber = staffLoginLogConfig.staffLoginLogIdRemainder(staffLoginLog.getId());
		if(tableNumber == 0){//默认对象为UserLoginLog
			return staffLoginLog;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.staff.StaffLoginLog_"+tableNumber);
				Object object = c.newInstance();
				
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
			}	
		}
		return null;
    }
    
    

    
    
}
