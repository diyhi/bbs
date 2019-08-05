package cms.web.action.like;



import javax.annotation.Resource;

import cms.bean.like.Like;
import cms.bean.like.TopicLike;
import cms.service.like.LikeService;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 点赞管理
 *
 */
@Component("likeManage")
public class LikeManage {
	private static final Logger logger = LogManager.getLogger(LikeManage.class);
	
    @Resource LikeConfig likeConfig;
    @Resource LikeService likeService;
    @Resource TopicLikeConfig topicLikeConfig;
    
    
    
	/**
	 * 取得点赞Id的用户Id(后N位)
	 * @param likeId 点赞Id
	 * @return
	 */
    public int getLikeId(String likeId){
    	String after_userId = likeId.substring(likeId.length()-4, likeId.length());
    	return Integer.parseInt(after_userId);
    } 
    
    /**
     * 生成点赞Id
     * 点赞Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createLikeId(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	String id = UUIDUtil.getUUID32()+afterUserId;
    	return id;
    }
    /**
     * 校验点赞Id
     * 点赞Id要先判断是否36位并且最后4位是数字
     * @param likeId 点赞Id
     * @return
     */
    public boolean verificationLikeId(String likeId){
    	if(likeId != null && !"".equals(likeId.trim())){
    		if(likeId.length() == 36){
    			String after_userId = likeId.substring(likeId.length()-4, likeId.length());		
    			boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
				if(verification){
					return true;
				}
    		}
    	}
    	return false;
    }
    
    /**
     * 生成点赞对象
     * @return
     */
    public Object createLikeObject(Like like){
    	//表编号
		int tableNumber = likeConfig.likeIdRemainder(like.getId());
		if(tableNumber == 0){//默认对象
			return like;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.like.Like_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(Like.class,object.getClass(), false); 
			
				copier.copy(like,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成点赞对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成点赞对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成点赞对象",e);
		        }
			}	
		}
		return null;
    }
    
    
    
    /**---------------------------------------------- 话题点赞 ----------------------------------------------**/
    /**
	 * 取得话题点赞Id的话题Id(后N位)
	 * @param topicLikeId 话题点赞Id
	 * @return
	 */
    public int getTopicLikeId(String topicLikeId){
    	String[] idGroup = topicLikeId.split("_");
    	Long topicId = Long.parseLong(idGroup[0]);
    	
    	//选取得后N位话题Id
    	String after_topicId = String.format("%04d", Math.abs(topicId)%10000);
    	return Integer.parseInt(after_topicId);
    } 
    
    /**
     * 生成话题点赞Id
     * 话题点赞Id格式（话题Id_用户Id）
     * @param topicId 话题Id
     * @param userId 用户Id
     * @return
     */
    public String createTopicLikeId(Long topicId,Long userId){
    	return topicId+"_"+userId;
    }
    /**
     * 校验话题点赞Id
     * 话题点赞Id要先判断最后4位是不是数字
     * @param topicLikeId 话题点赞Id
     * @return
     */
    public boolean verificationTopicLikeId(String topicLikeId){
    	if(topicLikeId != null && !"".equals(topicLikeId.trim())){
    		String[] idGroup = topicLikeId.split("_");
    		for(String id : idGroup){
    			boolean verification = Verification.isPositiveIntegerZero(id);//数字
    			if(!verification){
    				return false;
    			}
    			return true;
    		}	
			
    	}
    	return false;
    }
    
    
    /**
     * 生成话题点赞对象
     * @return
     */
    public Object createTopicLikeObject(TopicLike topicLike){
    	//表编号
		int tableNumber = topicLikeConfig.topicLikeIdRemainder(topicLike.getId());
		if(tableNumber == 0){//默认对象
			return topicLike;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.like.TopicLike_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(TopicLike.class,object.getClass(), false); 
			
				copier.copy(topicLike,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题点赞对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题点赞对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题点赞对象",e);
		        }
			}	
		}
		return null;
    }
    
    
    
    
    
    

    /**
	 * 查询缓存 查询话题点赞
	 * @param topicLikeId 话题点赞Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findTopicLikeById",key="#topicLikeId")
	public TopicLike query_cache_findTopicLikeById(String topicLikeId){
		return likeService.findTopicLikeById(topicLikeId);
	}
	/**
	 * 删除缓存 话题点赞
	 * @param topicLikeId 话题点赞Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findTopicLikeById",key="#topicLikeId")
	public void delete_cache_findTopicLikeById(String topicLikeId){
	}
    
    
	/**
	 * 查询缓存 根据话题Id查询被点赞数量
	 * @param topicId 话题Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findLikeCountByTopicId",key="#topicId")
	public Long query_cache_findLikeCountByTopicId(Long topicId){
		return likeService.findLikeCountByTopicId(topicId);
	}
	/**
	 * 删除缓存 根据话题Id查询被点赞数量
	 * @param topicId 话题Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findLikeCountByTopicId",key="#topicId")
	public void delete_cache_findLikeCountByTopicId(Long topicId){
	}
    
}
