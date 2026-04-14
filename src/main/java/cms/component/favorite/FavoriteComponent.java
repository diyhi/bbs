package cms.component.favorite;




import cms.model.favorite.Favorites;
import cms.model.favorite.QuestionFavorite;
import cms.model.favorite.TopicFavorite;
import cms.repository.favorite.FavoriteRepository;
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
 * 收藏组件
 *
 */
@Component("favoriteComponent")
public class FavoriteComponent {
	private static final Logger logger = LogManager.getLogger(FavoriteComponent.class);
	
    @Resource FavoritesConfig favoritesConfig;

    @Resource TopicFavoriteConfig topicFavoriteConfig;
    @Resource QuestionFavoriteConfig questionFavoriteConfig;
    
    

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
				c = Class.forName("cms.model.favorite.Favorites_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成收藏夹对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成收藏夹对象",e);
                }
            }
		}
		return null;
    }
    
    
    
    /**---------------------------------------------- 话题收藏 ----------------------------------------------**/

    
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
				c = Class.forName("cms.model.favorite.TopicFavorite_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成话题收藏对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成话题收藏对象",e);
                }
            }
		}
		return null;
    }
    
    
    /**---------------------------------------------- 问题收藏 ----------------------------------------------**/

    
    /**
     * 生成问题收藏对象
     * @return
     */
    public Object createQuestionFavoriteObject(QuestionFavorite questionFavorite){
    	//表编号
		int tableNumber = questionFavoriteConfig.questionFavoriteIdRemainder(questionFavorite.getId());
		if(tableNumber == 0){//默认对象
			return questionFavorite;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.model.favorite.QuestionFavorite_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
				BeanCopier copier = BeanCopier.create(QuestionFavorite.class,object.getClass(), false); 
			
				copier.copy(questionFavorite,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成问题收藏对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成问题收藏对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成问题收藏对象",e);
		        }
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成问题收藏对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成问题收藏对象",e);
                }
            }
		}
		return null;
    }
    

}
