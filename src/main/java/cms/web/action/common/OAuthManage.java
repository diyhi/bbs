package cms.web.action.common;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import cms.bean.user.AccessUser;
import cms.bean.user.RefreshUser;
import cms.utils.WebUtil;

/**
 * 开放授权管理
 *
 */
@Component("oAuthManage")
public class OAuthManage {
	@Resource OAuthManage oAuthManage;
	
	/**
	 * 添加刷新令牌
	 * @param refreshToken
	 * @param accessToken 访问令牌
	 */
	@CachePut(value="oAuthManage_cache_refreshToken",key="#refreshToken")
    public RefreshUser addRefreshToken(String refreshToken, RefreshUser refreshUser) {
		return refreshUser;
    }

    /**
     * 添加访问令牌
     * @param accessToken
     * @param user 仅userId userName两个字段有值
     */
	@CachePut(value="oAuthManage_cache_accessToken",key="#accessToken")
    public AccessUser addAccessToken(String accessToken, AccessUser accessUser) {
		return accessUser;
    }
    
	/**
     * 根据刷新令牌获取刷新用户
     * @param refreshToken
     * @return
     */
	@Cacheable(value="oAuthManage_cache_refreshToken",key="#refreshToken")
    public RefreshUser getRefreshUserByRefreshToken(String refreshToken) {
    	return null;
    }
    /**
     * 根据访问令牌获取用户
     * @param accessToken
     * @return
     */
	@Cacheable(value="oAuthManage_cache_accessToken",key="#accessToken")
    public AccessUser getAccessUserByAccessToken(String accessToken) {
    	return null;
    }
	/**
	 * 删除刷新令牌
	 * @param refreshToken
	 * @return
	 */
	@CacheEvict(value="oAuthManage_cache_refreshToken",key="#refreshToken")
	public void deleteRefreshToken(String refreshToken){
	}
	/**
	 * 删除访问令牌
	 * @param accessToken
	 * @return
	 */
	@CacheEvict(value="oAuthManage_cache_accessToken",key="#accessToken")
	public void deleteAccessToken(String accessToken){
	}
    
    
	/**
	 * 获取登录用户
	 */
	public AccessUser getUserName(HttpServletRequest request){
		String accessToken = WebUtil.getCookieByName(request, "cms_accessToken");
		if(accessToken != null && !"".equals(accessToken.trim())){
			AccessUser accessUser = oAuthManage.getAccessUserByAccessToken(accessToken.trim());
			return accessUser;
		}
		return null;
	}
	
    
    
    

}
