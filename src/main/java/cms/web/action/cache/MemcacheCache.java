package cms.web.action.cache;

import java.util.concurrent.Callable;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import net.rubyeye.xmemcached.MemcachedClient;

/**
 * Memcached缓存
 *
 */
public class MemcacheCache implements Cache {
	
	private MemcachedManager memcachedManager;
	
	private final MemcachedClient cache;
	/** 命名空间 **/
	private final String namespace;
	/** 过期时间 **/
	private final Integer expireTime;
//	public MemcacheCache(){}
	/**
	 * Create an {@link EhCacheCache} instance.
	 * @param ehcache backing Ehcache instance
	 */
	public MemcacheCache(MemcachedClient cache,MemcachedManager memcachedManager,String namespace,Integer expireTime) {
		Assert.notNull(cache, "Memcached不能为null");
	//	Status status = ehcache.getStatus();
	//	Assert.isTrue(Status.STATUS_ALIVE.equals(status),
	//			"An 'alive' Ehcache is required - current cache is " + status.toString());
		this.cache = cache;
		this.memcachedManager = memcachedManager;
		this.namespace = namespace;
		this.expireTime = expireTime;
	}
	

	/**
	 * 缓存的名字
	 */
	@Override
	public final String getName() {
		return this.namespace;
	}

	/**
	 * 得到底层使用的缓存
	 */
	@Override
	public final MemcachedClient getNativeCache() {
		return this.cache;
	}

	/**
	 * 根据key得到一个ValueWrapper，然后调用其get方法获取值
	 */
	@Override
	public ValueWrapper get(Object key) {
		Object aValue = memcachedManager.getCache(getName(), objectToString(key));
		return (aValue != null ? new SimpleValueWrapper(aValue) : null);
	}
	
	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		
		/**
		ValueWrapper val = get(key);
		if (val != null) {
			return (T) val.get();
		}
		
		
		ehcache实现
		Element element = lookup(key);
		if (element != null) {
			return (T) element.getObjectValue();
		}
		else {
			this.cache.acquireWriteLockOnKey(key);//获取给定Key的Write锁
			try {
				element = lookup(key); // One more attempt with the write lock
				if (element != null) {
					return (T) element.getObjectValue();
				}
				else {
					return loadValue(key, valueLoader);
				}
			}
			finally {
				this.cache.releaseWriteLockOnKey(key);//释放所持有的给定Key的Write锁 
			}
		}
		
		
		Redis实现
		BinaryRedisCacheElement rce = new BinaryRedisCacheElement(new RedisCacheElement(new RedisCacheKey(key).usePrefix(
				cacheMetadata.getKeyPrefix()).withKeySerializer(redisOperations.getKeySerializer()), valueLoader),
				cacheValueAccessor);

		ValueWrapper val = get(key);
		if (val != null) {
			return (T) val.get();
		}

		RedisWriteThroughCallback callback = new RedisWriteThroughCallback(rce, cacheMetadata);

		try {
			byte[] result = (byte[]) redisOperations.execute(callback);
			return (T) (result == null ? null : cacheValueAccessor.deserializeIfNecessary(result));
		} catch (RuntimeException e) {
			throw CacheValueRetrievalExceptionFactory.INSTANCE.create(key, valueLoader, e);
		}
		**/
		
		
		
		//未实现
		return null;
	}

	/**
	 * 根据key，和value的类型直接获取value
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object aValue = memcachedManager.getCache(getName(), objectToString(key));
		Object new_value = (aValue != null ? aValue : null);
		if (new_value != null && type != null && !type.isInstance(new_value)) {
			throw new IllegalStateException("缓存的值不是所需的类型 [" + type.getName() + "]: " + new_value);
		}
		return (T) new_value;
	}

	/**
	 * 存数据
	 */
	@Override
	public void put(Object key, Object value) {
		//this.cache.put(new Element(key, value));
		memcachedManager.addCache(getName(), objectToString(key), value, expireTime);
	}
	
	/**
	 * 根据key删数据
	 */
	@Override
	public void evict(Object key) {
	//	this.cache.remove(key);
		memcachedManager.deleteCache(getName(),objectToString(key));
	}

	/**
	 * 清空数据
	 */
	@Override
	public void clear() {
		memcachedManager.clearCache(getName());
	}
	
	private static String objectToString(Object object) {  
		if (object == null) {  
			return null;  
		} else if (object instanceof String) {  
		    return (String) object;  
		} else {  
			return object.toString();  
		}  
	}

	/**
	 * 如果值不存在，则添加
	 */
	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		this.put(key, value);
	    return this.get(key);
	    //return new SimpleValueWrapper(value);
	}
}
