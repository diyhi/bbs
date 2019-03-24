package cms.web.action.favorite;



import javax.annotation.Resource;

import cms.bean.favorite.Favorites;
import cms.bean.favorite.TopicFavorite;
import cms.service.favorite.FavoriteService;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 收藏管理
 *
 */
@Component("favoriteManage")
public class FavoriteManage {
	private static final Logger logger = LogManager.getLogger(FavoriteManage.class);
	
    @Resource FavoritesConfig favoritesConfig;
    @Resource FavoriteService favoriteService;
    @Resource TopicFavoriteConfig topicFavoriteConfig;
    
    
    
	/**
	 * 取得收藏夹Id的用户Id(后N位)
	 * @param favoriteId 收藏夹Id
	 * @return
	 */
    public int getFavoriteId(String favoriteId){
    	String after_userId = favoriteId.substring(favoriteId.length()-4, favoriteId.length());
    	return Integer.parseInt(after_userId);
    } 
    
    /**
     * 生成收藏夹Id
     * 收藏夹Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createFavoriteId(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	String id = UUIDUtil.getUUID32()+afterUserId;
    	return id;
    }
    /**
     * 校验收藏夹Id
     * 收藏夹Id要先判断是否36位并且最后4位是数字
     * @param favoriteId 收藏夹Id
     * @return
     */
    public boolean verificationFavoriteId(String favoriteId){
    	if(favoriteId != null && !"".equals(favoriteId.trim())){
    		if(favoriteId.length() == 36){
    			String after_userId = favoriteId.substring(favoriteId.length()-4, favoriteId.length());		
    			boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
				if(verification){
					return true;
				}
    		}
    	}
    	return false;
    }
    
    /**
     * 生成收藏夹对象
     * @return
     */
    public Object createFavoriteObject(Favorites favorites){
    	//表编号
		int tableNumber = favoritesConfig.favoriteIdRemainder(favorites.getId());
		if(tableNumber == 0){//默认对象为PrivateMessage
			return favorites;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.favorite.Favorites_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(Favorites.class,object.getClass(), false); 
			
				copier.copy(favorites,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成收藏夹对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成收藏夹对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成收藏夹对象",e);
		        }
			}	
		}
		return null;
    }
    
    
    
    /**---------------------------------------------- 话题收藏 ----------------------------------------------**/
    /**
	 * 取得话题收藏Id的话题Id(后N位)
	 * @param topicFavoriteId 话题收藏Id
	 * @return
	 */
    public int getTopicFavoriteId(String topicFavoriteId){
    	String[] idGroup = topicFavoriteId.split("_");
    	Long topicId = Long.parseLong(idGroup[0]);
    	
    	//选取得后N位话题Id
    	String after_topicId = String.format("%04d", Math.abs(topicId)%10000);
    	return Integer.parseInt(after_topicId);
    } 
    
    /**
     * 生成话题收藏Id
     * 话题收藏Id格式（话题Id_用户Id）
     * @param topicId 话题Id
     * @param userId 用户Id
     * @return
     */
    public String createTopicFavoriteId(Long topicId,Long userId){
    	return topicId+"_"+userId;
    }
    /**
     * 校验话题收藏Id
     * 话题收藏Id要先判断最后4位是不是数字
     * @param topicFavoriteId 话题收藏Id
     * @return
     */
    public boolean verificationTopicFavoriteId(String topicFavoriteId){
    	if(topicFavoriteId != null && !"".equals(topicFavoriteId.trim())){
    		String[] idGroup = topicFavoriteId.split("_");
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
     * 生成话题收藏对象
     * @return
     */
    public Object createTopicFavoriteObject(TopicFavorite topicFavorite){
    	//表编号
		int tableNumber = topicFavoriteConfig.topicFavoriteIdRemainder(topicFavorite.getId());
		if(tableNumber == 0){//默认对象
			return topicFavorite;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.favorite.TopicFavorite_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(TopicFavorite.class,object.getClass(), false); 
			
				copier.copy(topicFavorite,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题收藏对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题收藏对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题收藏对象",e);
		        }
			}	
		}
		return null;
    }
    
    
    
    
    
    

    /**
	 * 查询缓存 查询话题收藏
	 * @param topicFavoriteId 话题收藏Id
	 * @return
	 */
	@Cacheable(value="favoriteManage_cache_findTopicFavoriteById",key="#topicFavoriteId")
	public TopicFavorite query_cache_findTopicFavoriteById(String topicFavoriteId){
		return favoriteService.findTopicFavoriteById(topicFavoriteId);
	}
	/**
	 * 删除缓存 话题收藏
	 * @param topicFavoriteId 话题收藏Id
	 * @return
	 */
	@CacheEvict(value="favoriteManage_cache_findTopicFavoriteById",key="#topicFavoriteId")
	public void delete_cache_findTopicFavoriteById(String topicFavoriteId){
	}
    
    
	/**
	 * 查询缓存 根据话题Id查询被收藏数量
	 * @param topicId 话题Id
	 * @return
	 */
	@Cacheable(value="favoriteManage_cache_findFavoriteCountByTopicId",key="#topicId")
	public Long query_cache_findFavoriteCountByTopicId(Long topicId){
		return favoriteService.findFavoriteCountByTopicId(topicId);
	}
	/**
	 * 删除缓存 根据话题Id查询被收藏数量
	 * @param topicId 话题Id
	 * @return
	 */
	@CacheEvict(value="favoriteManage_cache_findFavoriteCountByTopicId",key="#topicId")
	public void delete_cache_findFavoriteCountByTopicId(Long topicId){
	}
    
}
