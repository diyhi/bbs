package cms.web.action.cache;

/**
 * 选择缓存
 *
 */
public class SelectCache {
	/** 启用何种缓存 memcached  ehcache **/
	private String cacheName;
	
	public SelectCache() {
	}
	public SelectCache(String cacheName) {
		this.cacheName = cacheName;
	}
	public String getCacheName() {
		return cacheName;
	}
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	
}
