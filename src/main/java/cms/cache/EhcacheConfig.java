package cms.cache;

import jakarta.annotation.Resource;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.cache.Caching;
import java.time.temporal.ChronoUnit;

/**
 * Ehcache缓存配置
 */
@Configuration
@EnableCaching // 启用缓存功能
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "jcache")
public class EhcacheConfig {
    @Resource
    Environment environment;


    @Bean
    public org.springframework.cache.CacheManager cacheManager() {

        return new JCacheCacheManager(jCacheManager());
    }

    @Bean(destroyMethod = "close")
    public javax.cache.CacheManager jCacheManager() {
        //配置默认缓存属性
        XmlConfiguration xmlConfig = new XmlConfiguration(getClass().getResource("/ehcache.xml"));
        EhcacheCachingProvider provider = (EhcacheCachingProvider) Caching.getCachingProvider();
        javax.cache.CacheManager jCache = provider.getCacheManager(provider.getDefaultURI(), xmlConfig);

        //管理员 - 刷新令牌
        Long refreshToken = environment.getProperty("bbs.admin.refreshToken-expiration", Long.class, 2592000L);
        javax.cache.configuration.Configuration<Object, Object> configuration_refreshToken =
                Eh107Configuration.fromEhcacheCacheConfiguration(
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                        Object.class,
                                        Object.class,
                                        ResourcePoolsBuilder.heap(99999999))
                                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(java.time.Duration.of(refreshToken, ChronoUnit.SECONDS)))
                                .build()
                );
        if (jCache.getCache("adminCacheManager_cache_refreshToken", Object.class, Object.class) != null) {
            jCache.destroyCache("adminCacheManager_cache_refreshToken");
        }
        jCache.createCache("adminCacheManager_cache_refreshToken", configuration_refreshToken);

        //管理员 - 访问令牌
        Long accessToken = environment.getProperty("bbs.admin.accessToken-expiration", Long.class, 7200L);
        javax.cache.configuration.Configuration<Object, Object> configuration_accessToken =
                Eh107Configuration.fromEhcacheCacheConfiguration(
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                        Object.class,
                                        Object.class,
                                        ResourcePoolsBuilder.heap(99999999))
                                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(java.time.Duration.of(accessToken, ChronoUnit.SECONDS)))
                                .build()
                );
        if (jCache.getCache("adminCacheManager_cache_accessToken", Object.class, Object.class) != null) {
            jCache.destroyCache("adminCacheManager_cache_accessToken");
        }
        jCache.createCache("adminCacheManager_cache_accessToken", configuration_accessToken);

        return jCache;
    }
}
