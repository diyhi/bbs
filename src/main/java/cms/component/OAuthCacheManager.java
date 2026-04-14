package cms.component;

import cms.dto.user.AccessUser;
import cms.dto.user.RefreshUser;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 开放授权缓存管理
 */
@Component("oAuthCacheManager")
public class OAuthCacheManager {

    /**
     * 添加刷新令牌
     * @param refreshToken 刷新令牌
     * @param refreshUser 刷新用户
     */
    @CachePut(value="oAuthCacheManager_cache_refreshToken",key="#refreshToken")
    public RefreshUser addRefreshToken(String refreshToken, RefreshUser refreshUser) {
        return refreshUser;
    }

    /**
     * 添加访问令牌
     * @param accessToken 访问令牌
     * @param accessUser 访问用户 仅userId userName两个字段有值
     */
    @CachePut(value="oAuthCacheManager_cache_accessToken",key="#accessToken")
    public AccessUser addAccessToken(String accessToken, AccessUser accessUser) {
        return accessUser;
    }
    /**
     * 添加第三方用户的唯一标识(OpenId绑定到刷新令牌)
     * @param openId 第三方用户的唯一标识
     * @param refreshToken 刷新令牌号
     */
    @CachePut(value="oAuthCacheManager_cache_openId",key="#openId")
    public String addOpenId(String openId, String refreshToken) {
        return refreshToken;
    }

    /**
     * 根据刷新令牌获取刷新用户
     * @param refreshToken
     * @return
     */
    @Cacheable(value="oAuthCacheManager_cache_refreshToken",key="#refreshToken")
    public RefreshUser getRefreshUserByRefreshToken(String refreshToken) {
        return null;
    }
    /**
     * 根据访问令牌获取用户
     * @param accessToken
     * @return
     */
    @Cacheable(value="oAuthCacheManager_cache_accessToken",key="#accessToken")
    public AccessUser getAccessUserByAccessToken(String accessToken) {
        return null;
    }
    /**
     * 根据第三方用户的唯一标识获取刷新令牌号
     * @param openId 第三方用户的唯一标识
     * @return
     */
    @Cacheable(value="oAuthCacheManager_cache_openId",key="#openId")
    public String getRefreshTokenByOpenId(String openId) {
        return null;
    }


    /**
     * 删除刷新令牌
     * @param refreshToken
     * @return
     */
    @CacheEvict(value="oAuthCacheManager_cache_refreshToken",key="#refreshToken")
    public void deleteRefreshToken(String refreshToken){
    }
    /**
     * 删除访问令牌
     * @param accessToken
     * @return
     */
    @CacheEvict(value="oAuthCacheManager_cache_accessToken",key="#accessToken")
    public void deleteAccessToken(String accessToken){
    }


    /**
     * 删除第三方用户的唯一标识
     * @param openId 第三方用户的唯一标识
     * @return
     */
    @CacheEvict(value="oAuthCacheManager_cache_openId",key="#openId")
    public void deleteOpenId(String openId){
    }
}
