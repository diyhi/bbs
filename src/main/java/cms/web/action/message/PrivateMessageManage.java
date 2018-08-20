package cms.web.action.message;



import javax.annotation.Resource;

import cms.bean.message.PrivateMessage;
import cms.service.message.PrivateMessageService;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 私信管理
 *
 */
@Component("privateMessageManage")
public class PrivateMessageManage {
	private static final Logger logger = LogManager.getLogger(PrivateMessageManage.class);
	
    @Resource PrivateMessageConfig privateMessageConfig;
    @Resource PrivateMessageService privateMessageService;
    
    
	/**
	 * 取得私信Id的用户Id(后N位)
	 * @param privateMessageId 私信Id
	 * @return
	 */
    public int getPrivateMessageId(String privateMessageId){
    	String after_userId = privateMessageId.substring(privateMessageId.length()-4, privateMessageId.length());
    	return Integer.parseInt(after_userId);
    } 
    
    /**
     * 生成私信Id
     * 私信Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createPrivateMessageId(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	String id = UUIDUtil.getUUID32()+afterUserId;
    	return id;
    }
    /**
     * 校验私信Id
     * 私信Id要先判断是否36位并且最后4位是数字
     * @param privateMessageId 私信Id
     * @return
     */
    public boolean verificationPrivateMessageId(String privateMessageId){
    	if(privateMessageId != null && !"".equals(privateMessageId.trim())){
    		if(privateMessageId.length() == 36){
    			String after_userId = privateMessageId.substring(privateMessageId.length()-4, privateMessageId.length());		
    			boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
				if(verification){
					return true;
				}
    		}
    	}
    	return false;
    }
    
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
				c = Class.forName("cms.bean.message.PrivateMessage_"+tableNumber);
				Object object = c.newInstance();
				
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
			}	
		}
		return null;
    }
    
    

    /**
	 * 查询缓存 查询未读私信数量
	 * @param userId 用户Id
	 * @return
	 */
	@Cacheable(value="privateMessageManage_cache_findUnreadPrivateMessageByUserId",key="#userId")
	public Long query_cache_findUnreadPrivateMessageByUserId(Long userId){
		return privateMessageService.findUnreadPrivateMessageByUserId(userId);
	}
	/**
	 * 删除缓存 查询未读私信数量
	 * @param userId 用户Id
	 * @return
	 */
	@CacheEvict(value="privateMessageManage_cache_findUnreadPrivateMessageByUserId",key="#userId")
	public void delete_cache_findUnreadPrivateMessageByUserId(Long userId){
	}
    
    
    
    
}
