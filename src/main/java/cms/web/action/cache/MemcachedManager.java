package cms.web.action.cache;

import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientCallable;
import net.rubyeye.xmemcached.exception.MemcachedException;

/**
 * Memcached缓存操作
 *
 */
public class MemcachedManager {	
	private MemcachedClient memcachedClient;
	
	private static final Logger logger = LogManager.getLogger(MemcachedManager.class);
	
	
	public MemcachedManager(){}
	public MemcachedManager(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}
	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
	/**-----------------------------  缓存API  --------------------------------**/
	/**
	 * 获取缓存
	 * @param cacheName 缓存名称
	 * @param key 缓存Key
	 */
	public Object getCache(String cacheName,final String key){
		//获取命名空间内的a对应的值 
		try {
			Object aValue = memcachedClient.withNamespace(cacheName, 
                 new MemcachedClientCallable<Object>() { 
						public Object call(MemcachedClient client) throws MemcachedException, InterruptedException, TimeoutException { 
							return client.get(key); 
						} 
                 });
			return aValue;
		} catch (MemcachedException e) {
				// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存错误",e);
	        }
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存被中断",e);
	        }
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存超时",e);
	        }
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
		try {
			memcachedClient.withNamespace(cacheName, 
                new MemcachedClientCallable<Void>() { 

                        public Void call(MemcachedClient client) throws MemcachedException, InterruptedException, TimeoutException { 
                               //set和add方法的不同之处是add方法不允许key值相同，如果第二次add的key相同，则存储失败，而set方法允许key相同，如果相同，则替换该key对应的value。 
                        		//set --> 第一个参数:键; 第二个参数:过期时间（单位秒），超过这个时间,memcached将这个数据替换出去，0表示永久存储（默认是一个月); 第三个参数:值
                        	//a,b,c都在namespace下 
                                client.touch(key, expireTime);
                                return null; 
                        } 
                });
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存错误",e);
	        }
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存被中断",e);
	        }
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存超时",e);
	        }
		} 
	}
	
	/**
	 * 添加缓存
	 * @param cacheName 缓存名称
	 * @param key 缓存Key
	 * @param value 缓存value
	 * @param expireTime 过期时间 单位/秒; 设置为0表明该元素永不过期
	 */
	public void addCache(String cacheName,final String key,final Object value,final Integer expireTime){
		if(value != null){
			try {
				memcachedClient.withNamespace(cacheName, 
	                new MemcachedClientCallable<Void>() { 

	                        public Void call(MemcachedClient client) throws MemcachedException, InterruptedException, TimeoutException { 
	                               //set和add方法的不同之处是add方法不允许key值相同，如果第二次add的key相同，则存储失败，而set方法允许key相同，如果相同，则替换该key对应的value。 
	                        		//set --> 第一个参数:键; 第二个参数:过期时间（单位秒），超过这个时间,memcached将这个数据替换出去，0表示永久存储（默认是一个月); 第三个参数:值
	                        	//a,b,c都在namespace下 
	                                client.set(key,expireTime,value); 
	                                return null; 
	                        } 
	                });
			} catch (MemcachedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("获取 Memcached 缓存错误",e);
		        }
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("获取 Memcached 缓存被中断",e);
		        }
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("获取 Memcached 缓存超时",e);
		        }
			}
		}	
	}
	/**
	 * 追加缓存
	 * @param cacheName 缓存名称
	 * @param key 缓存Key
	 * @param value 缓存value
	 */
	public boolean appendCache(String cacheName,final String key,final Object value){
		if(value != null){
			try {
				Boolean aValue = memcachedClient.withNamespace(cacheName, 
	                new MemcachedClientCallable<Boolean>() { 

	                        public Boolean call(MemcachedClient client) throws MemcachedException, InterruptedException, TimeoutException { 
	                               //set和add方法的不同之处是add方法不允许key值相同，如果第二次add的key相同，则存储失败，而set方法允许key相同，如果相同，则替换该key对应的value。 
	                        		//set --> 第一个参数:键; 第二个参数:过期时间（单位秒），超过这个时间,memcached将这个数据替换出去，0表示永久存储（默认是一个月); 第三个参数:值
	                        	//a,b,c都在namespace下 
	                                
	                                return client.append(key,value); 
	                        } 
	                });
				return aValue;
			} catch (MemcachedException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("获取 Memcached 缓存错误",e);
		        }
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("获取 Memcached 缓存被中断",e);
		        }
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("获取 Memcached 缓存超时",e);
		        }
			}
		}
		
		return false; 
	}
	
	/**
	 * 删除缓存
	 * @param cacheName 缓存名称
	 * @param key 缓存Key
	 */
	public void deleteCache(String cacheName,final String key){
		try {
			memcachedClient.withNamespace(cacheName, 
                new MemcachedClientCallable<Void>() { 
                        public Void call(MemcachedClient client) throws MemcachedException, InterruptedException, TimeoutException { 
                                client.delete(key);
                                return null; 
                        } 
                });
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存错误",e);
	        }
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存被中断",e);
	        }
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存超时",e);
	        }
		}
		
	}
	/**
	 * 清空缓存
	 * @param cacheName 缓存名称
	 */
	public void clearCache(String cacheName){
		try {
			//使得命名空间失效 
			memcachedClient.invalidateNamespace(cacheName);
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存错误",e);
	        }
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存被中断",e);
	        }
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取 Memcached 缓存超时",e);
	        }
		}
	}
		
	
}
