package cms.web.action.cache;

import javax.annotation.Resource;
import net.sf.ehcache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * 缓存API管理
 *
 */
@Component("cacheManage")
public class CacheManage {
	
	@Resource(name = "cacheManagerFactory")
	private CacheManager ehCacheManager;
	
	@Resource MemcachedManager memcachedManager;
	
	/** 选择缓存  memcached  ehcache**/
	@Resource(name = "selectCache")
	private SelectCache selectCache;
	
	/**
	 * 获取缓存
	 * @param cacheName 缓存名称
	 * @param key 缓存Key
	 */
	public Object getCache(String cacheName,final String key){
		if("ehcache".equals(selectCache.getCacheName())){//如果使用EhCache缓存
			
			//获取ehcache配置文件中的一个cache 
			net.sf.ehcache.Cache cache = ehCacheManager.getCache(cacheName);
			
		
			Object obj = cache.get(key);
			if(obj != null){
				return cache.get(key).getObjectValue();
				
			}
			
		}else{//如果使用Memcached缓存
			return memcachedManager.getCache(cacheName, key);
		}
		return null;
	}

	/**
	 * 更新超时时间
	 * @param cacheName 缓存名称
	 * @param key 缓存Key
	 * @param expireTime 过期时间 单位/秒; 设置为0表明该元素永不过期
	 */
	public void touch(String cacheName,final String key,final Integer expireTime){
		if("ehcache".equals(selectCache.getCacheName())){//如果使用EhCache缓存
			//获取ehcache配置文件中的一个cache 
			net.sf.ehcache.Cache cache = ehCacheManager.getCache(cacheName);
			Object obj = cache.get(key);
			if(obj != null){
				cache.get(key).setTimeToLive(expireTime);//设定对象允许存在于缓存中的最长时间，以秒为单位
			//	cache.get(key).setTimeToIdle(expireTime);//设定允许对象处于空闲状态的最长时间，以秒为单位
			}
			
		}else{//如果使用Memcached缓存
			//其中touch用于设置数据新的超时时间，getAndTouch则是在获取数据的同时更新超时时间。
			//例如用memcached存储session，可以在每次get的时候更新下数据的超时时间来保活。
			//请注意，这四个方法仅在使用memcached 1.6并且使用二进制协议的时候有效
			memcachedManager.touch(cacheName,key,expireTime);
		}
	}
	
	/**
	 * 添加缓存
	 * @param cacheName 缓存名称
	 * @param key 缓存Key
	 * @param value 缓存value
	 * @param expireTime 过期时间 单位/秒; 设置为0表明该元素永不过期
	
	public void addCache(String cacheName,final String key,final Object value,final Integer expireTime){
		if("ehcache".equals(selectCache.getCacheName())){//如果使用EhCache缓存
			//获取ehcache配置文件中的一个cache 
			net.sf.ehcache.Cache cache = ehCacheManager.getCache(cacheName);
			// 添加数据到缓存中 
			net.sf.ehcache.Element element = new net.sf.ehcache.Element(key, value);
			cache.put(element);
		}else{//如果使用Memcached缓存
			memcachedManager.addCache(cacheName, key, value, expireTime);
		}
		
	}**/
	/**
	 * 添加缓存
	 * @param cacheName 缓存名称
	 * @param key 缓存Key
	 * @param value 缓存value
	 */
	public void addCache(String cacheName,final String key,final Object value){
		if("ehcache".equals(selectCache.getCacheName())){//如果使用EhCache缓存
			//获取ehcache配置文件中的一个cache 
			net.sf.ehcache.Cache cache = ehCacheManager.getCache(cacheName);
			// 添加数据到缓存中 
			net.sf.ehcache.Element element = new net.sf.ehcache.Element(key, value);
			cache.put(element);
		}else{//如果使用Memcached缓存
			//获取过期时间
			long l = ehCacheManager.getEhcache(cacheName).getCacheConfiguration().getTimeToLiveSeconds();

			memcachedManager.addCache(cacheName, key, value, new Long(l).intValue());
		}
		
	}
	
	/**
	 * 删除缓存
	 * @param cacheName 缓存名称
	 * @param key 缓存Key
	 */
	public void deleteCache(String cacheName,final String key){
		if("ehcache".equals(selectCache.getCacheName())){//如果使用EhCache缓存
			//获取ehcache配置文件中的一个cache 
			net.sf.ehcache.Cache cache = ehCacheManager.getCache(cacheName);
			
			cache.remove(key);
			
		}else{//如果使用Memcached缓存
			memcachedManager.deleteCache(cacheName, key);
		}
	}
	/**
	 * 清空缓存
	 * @param cacheName 缓存名称
	 */
	public void clearCache(String cacheName){
		if("ehcache".equals(selectCache.getCacheName())){//如果使用EhCache缓存
			//获取ehcache配置文件中的一个cache 
			net.sf.ehcache.Cache cache = ehCacheManager.getCache(cacheName);
			cache.removeAll();
			
		}else{//如果使用Memcached缓存
			memcachedManager.clearCache(cacheName);
		}
		
	}
	
	/**
	 * 清空所有缓存
	 */
	public void clearAllCache(){
		//获取所有缓存名称
		String[] names = ehCacheManager.getCacheNames();
		if(names != null && names.length >0){
			for(String cacheName : names){
				this.clearCache(cacheName);
			}
		}
	}
	
	
}
