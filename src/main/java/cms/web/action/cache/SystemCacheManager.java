package cms.web.action.cache;


import java.util.Collection;
import java.util.LinkedHashSet;

import net.rubyeye.xmemcached.MemcachedClient;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;

import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.util.Assert;

/**
 * 系统缓存管理
 *
 */
public class SystemCacheManager extends AbstractTransactionSupportingCacheManager {

	private net.sf.ehcache.CacheManager ehCacheManager;
	
	private MemcachedClient memcachedClient;
	private SelectCache selectCache;
	
	private MemcachedManager memcachedManager;
	
	public SystemCacheManager(){}
	public SystemCacheManager(net.sf.ehcache.CacheManager cacheManager,MemcachedClient memcachedClient,
			SelectCache selectCache,MemcachedManager memcachedManager) {
		this.ehCacheManager = cacheManager;
		this.memcachedClient = memcachedClient;
		this.selectCache = selectCache;
		this.memcachedManager = memcachedManager;
	}




	public net.sf.ehcache.CacheManager getEhCacheManager() {
		return ehCacheManager;
	}

	public void setEhCacheManager(net.sf.ehcache.CacheManager ehCacheManager) {
		this.ehCacheManager = ehCacheManager;
	}

	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
	public SelectCache getSelectCache() {
		return selectCache;
	}

	public void setSelectCache(SelectCache selectCache) {
		this.selectCache = selectCache;
	}
	public MemcachedManager getMemcachedManager() {
		return memcachedManager;
	}
	public void setMemcachedManager(MemcachedManager memcachedManager) {
		this.memcachedManager = memcachedManager;
	}
	
	
	@Override
	public void afterPropertiesSet() {
		
		if("ehcache".equals(selectCache.getCacheName())){//如果使用EhCache缓存
			if (getEhCacheManager() == null) {
				setEhCacheManager(EhCacheManagerUtils.buildCacheManager());	
			}
		}else{// memcached
		}
		
		super.afterPropertiesSet();
	}


	@Override
	protected Collection<Cache> loadCaches() {
		
		if("ehcache".equals(selectCache.getCacheName())){
			Status status = getEhCacheManager().getStatus();
			if (!Status.STATUS_ALIVE.equals(status)) {
				throw new IllegalStateException(
						"An 'alive' EhCache CacheManager is required - current cache is " + status.toString());
			}

			String[] names = getEhCacheManager().getCacheNames();
			Collection<Cache> caches = new LinkedHashSet<Cache>(names.length);
			for (String name : names) {
				caches.add(new EhCacheCache(getEhCacheManager().getEhcache(name)));
			}
			return caches;
		}else{// memcached
			net.sf.ehcache.CacheManager cacheManager = getEhCacheManager();
			Assert.notNull(cacheManager, "A backing EhCache CacheManager is required");
			Status status = cacheManager.getStatus();
			if (!Status.STATUS_ALIVE.equals(status)) {
				throw new IllegalStateException(
						"An 'alive' Memcached CacheManager is required - current cache is " + status.toString());
			}
	
			
			String[] names = cacheManager.getCacheNames();
			Collection<Cache> caches = new LinkedHashSet<Cache>(names.length);
			for (String name : names) {
				long l = cacheManager.getEhcache(name).getCacheConfiguration().getTimeToLiveSeconds();
				caches.add(new MemcacheCache(memcachedClient,memcachedManager,name,new Long(l).intValue()));
			}
			return caches;	
		}

	}
	
	@Override
	protected Cache getMissingCache(String name) {
		if("ehcache".equals(selectCache.getCacheName())){
			// Check the EhCache cache again (in case the cache was added at runtime)
			Ehcache ehcache = getEhCacheManager().getEhcache(name);
			if (ehcache != null) {
				return new EhCacheCache(ehcache);
			}
		}else{// memcached
			Ehcache ehcache = getEhCacheManager().getEhcache(name);
			if (ehcache != null) {
				return new MemcacheCache(memcachedClient,memcachedManager,name,new Long(ehcache.getCacheConfiguration().getTimeToLiveSeconds()).intValue());
			}
		}
		return null;
	}

}
