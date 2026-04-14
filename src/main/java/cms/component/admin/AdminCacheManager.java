package cms.component.admin;

import cms.dto.admin.AdminAccessToken;
import cms.dto.admin.AdminRefreshToken;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;


/**
 * 员工缓存管理
 */
@Component("adminCacheManager")
public class AdminCacheManager {
    /**
     * 添加刷新令牌
     * @param refreshToken 刷新令牌
     * @param adminRefreshToken 管理员刷新令牌
     */
    @CachePut(value="adminCacheManager_cache_refreshToken",key="#refreshToken")
    public AdminRefreshToken addRefreshToken(String refreshToken, AdminRefreshToken adminRefreshToken) {
        return adminRefreshToken;
    }

    /**
     * 添加访问令牌
     * @param accessToken 访问令牌
     * @param adminAccessToken 管理员访问令牌
     */
    @CachePut(value="adminCacheManager_cache_accessToken",key="#accessToken")
    public AdminAccessToken addAccessToken(String accessToken, AdminAccessToken adminAccessToken) {
        return adminAccessToken;
    }

    /**
     * 根据刷新令牌获取管理员
     * @param refreshToken 刷新令牌
     * @return
     */
    @Cacheable(value="adminCacheManager_cache_refreshToken",key="#refreshToken")
    public AdminRefreshToken getAdminRefreshTokenByRefreshToken(String refreshToken) {
        return null;
    }
    /**
     * 根据访问令牌获取管理员
     * @param accessToken 访问令牌
     * @return
     */
    @Cacheable(value="adminCacheManager_cache_accessToken",key="#accessToken")
    public AdminAccessToken getAdminAccessTokenByAccessToken(String accessToken) {
        return null;
    }



    /**
     * 删除刷新令牌
     * @param refreshToken 刷新令牌
     * @return
     */
    @CacheEvict(value="adminCacheManager_cache_refreshToken",key="#refreshToken")
    public void deleteRefreshToken(String refreshToken){
    }
    /**
     * 删除访问令牌
     * @param accessToken 访问令牌
     * @return
     */
    @CacheEvict(value="adminCacheManager_cache_accessToken",key="#accessToken")
    public void deleteAccessToken(String accessToken){
    }



}
