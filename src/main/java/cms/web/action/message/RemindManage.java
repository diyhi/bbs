package cms.web.action.message;



import javax.annotation.Resource;

import cms.bean.message.Remind;
import cms.service.message.RemindService;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 提醒管理
 *
 */
@Component("remindManage")
public class RemindManage {
	private static final Logger logger = LogManager.getLogger(RemindManage.class);
	
    @Resource RemindConfig remindConfig;
    @Resource RemindService remindService;
	/**
	 * 取得提醒Id的用户Id(后N位)
	 * @param remindId 提醒Id
	 * @return
	 */
    public int getRemindId(String remindId){
    	String after_userId = remindId.substring(remindId.length()-4, remindId.length());
    	return Integer.parseInt(after_userId);
    } 
    
    /**
     * 生成提醒Id
     * 提醒Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createRemindId(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	String id = UUIDUtil.getUUID32()+afterUserId;
    	return id;
    }
    /**
     * 校验提醒Id
     * 提醒Id要先判断是否36位并且最后4位是数字
     * @param remindId 提醒Id
     * @return
     */
    public boolean verificationRemindId(String remindId){
    	if(remindId != null && !"".equals(remindId.trim())){
    		if(remindId.length() == 36){
    			String after_userId = remindId.substring(remindId.length()-4, remindId.length());		
    			boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
				if(verification){
					return true;
				}
    		}
    	}
    	return false;
    }
    
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
				c = Class.forName("cms.bean.message.Remind_"+tableNumber);
				Object object = c.newInstance();
				
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
			}	
		}
		return null;
    }
    
    


    /**
	 * 查询缓存 查询未读提醒数量
	 * @param userId 用户Id
	 * @return
	 */
	@Cacheable(value="remindManage_cache_findUnreadRemindByUserId",key="#userId")
	public Long query_cache_findUnreadRemindByUserId(Long userId){
		return remindService.findUnreadRemindByUserId(userId);
	}
	/**
	 * 删除缓存 查询未读提醒数量
	 * @param userId 用户Id
	 * @return
	 */
	@CacheEvict(value="remindManage_cache_findUnreadRemindByUserId",key="#userId")
	public void delete_cache_findUnreadRemindByUserId(Long userId){
	}
    
}
