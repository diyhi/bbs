package cms.cache;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;

/**
 * 缓存API管理
 *
 */
@Component("cacheApiManager")
public class CacheApiManager {

    @Lazy @Resource javax.cache.CacheManager jCacheManager;

    /** 选择缓存产品**/
    @Value("${spring.cache.type}")
    private String selectCache;


    /**
     * 获取缓存
     * @param cacheName 缓存名称
     * @param key 缓存Key
     */
    public Object getCache(String cacheName,final String key){
        //第一个参数: 缓存名称  第二个参数: key的类型   第三个参数: value的类型
        javax.cache.Cache<Object, Object> preConfigured = jCacheManager.getCache(cacheName, Object.class, Object.class);

        return preConfigured.get(key);
    }

    /**
     * 添加缓存
     * @param cacheName 缓存名称
     * @param key 缓存Key
     * @param value 缓存value
     */
    public void addCache(String cacheName,final String key,final Object value){
        javax.cache.Cache<Object, Object> preConfigured = jCacheManager.getCache(cacheName, Object.class, Object.class);
        preConfigured.put(key, value);
    }

    /**
     * 删除缓存
     * @param cacheName 缓存名称
     * @param key 缓存Key
     */
    public void deleteCache(String cacheName,final String key){
        javax.cache.Cache<Object, Object> preConfigured = jCacheManager.getCache(cacheName, Object.class, Object.class);
        preConfigured.remove(key);
    }
    /**
     * 清空缓存
     * @param cacheName 缓存名称
     */
    public void clearCache(String cacheName){
        javax.cache.Cache<Object, Object> preConfigured = jCacheManager.getCache(cacheName, Object.class, Object.class);
        preConfigured.clear();

    }

    /**
     * 清空所有缓存
     */
    public void clearAllCache(){
        //获取所有缓存名称
        for (String cacheName : jCacheManager.getCacheNames()) {
            //javax.cache.Cache<Object, Object> jcache = jCacheManager.getCache(cacheName);
            //缓存名称
            //this.clearCache(jcache.getName());
            this.clearCache(cacheName);
        }

    }

}
