package cms.web.action.follow;



import javax.annotation.Resource;

import cms.bean.follow.Follower;
import cms.service.follow.FollowService;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 粉丝管理
 *
 */
@Component("followerManage")
public class FollowerManage {
	private static final Logger logger = LogManager.getLogger(FollowerManage.class);
	
    @Resource FollowerConfig followerConfig;
    @Resource FollowService followService;
    
    
    /**
	 * 取得粉丝Id的用户Id(后N位)
	 * @param followerId 关注Id
	 * @return
	 */
    public int getFollowerAfterId(String followerId){
    	String[] idGroup = followerId.split("-");
    	Long userId = Long.parseLong(idGroup[1]);
    	String afterUserId = String.format("%04d", userId%10000);
    	return Integer.parseInt(afterUserId);
    } 
    
    /**
     * 生成粉丝Id
     * 粉丝Id由(对方用户Id-粉丝的用户Id)组成
     * @param userId 用户Id
     * @param friendUserId 对方用户Id
     * @return
     */
    public String createFollowerId(Long userId,Long friendUserId){
    	return String.valueOf(friendUserId)+"-"+String.valueOf(userId);
    }
    /**
     * 校验粉丝Id
     * 粉丝Id要判断按横杆分割后是数字
     * @param followerId 粉丝Id
     * @return
     */
    public boolean verificationFollowerId(String followerId){
    	if(followerId != null && !"".equals(followerId.trim())){
    		String[] idGroup = followerId.split("-");
    		if(idGroup.length ==2){
    			boolean verification_1 = Verification.isPositiveIntegerZero(idGroup[0]);//数字
    			boolean verification_2 = Verification.isPositiveIntegerZero(idGroup[1]);//数字
    			if(verification_1 && verification_2){
					return true;
				}
    		}
    	}
    	return false;
    }
    
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
				c = Class.forName("cms.bean.follow.Follower_"+tableNumber);
				Object object = c.newInstance();
				
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
			}	
		}
		return null;
    }
   
   
    /**
   	 * 查询缓存 查询粉丝总数
   	 * @param userName 用户名称
   	 * @return
   	 */
   	@Cacheable(value="followerManage_cache_followerCount",key="#userName")
   	public Long query_cache_followerCount(Long userId, String userName){
   		return followService.findFollowerCountByUserName(userId, userName);
   	}
   	/**
   	 * 删除缓存 粉丝总数
   	 * @param userName 用户名称
   	 * @return
   	 */
   	@CacheEvict(value="followerManage_cache_followerCount",key="#userName")
   	public void delete_cache_followerCount(String userName){
   	}
    
    
}
