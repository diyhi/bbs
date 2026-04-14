package cms.component.thirdParty;

import cms.dto.thirdParty.WeiXinOpenId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 第三方服务缓存管理
 */
@Component("thirdPartyCacheManager")
public class ThirdPartyCacheManager {

    /**
     * 添加微信第三方用户的唯一标识票据
     * @param weiXinCode 微信第三方用户的唯一标识票据
     * @param weiXinOpenId 微信返回的openid
     */
    @CachePut(value="thirdPartyCacheManager_cache_weiXinOpenId",key="#weiXinCode")
    public WeiXinOpenId addWeiXinOpenId(String weiXinCode, WeiXinOpenId weiXinOpenId) {
        return weiXinOpenId;
    }
    /**
     * 根据微信第三方用户的唯一标识票据获取微信openid
     * @param weiXinCode 微信第三方用户的唯一标识票据
     * @return
     */
    @Cacheable(value="thirdPartyCacheManager_cache_weiXinOpenId",key="#weiXinCode")
    public WeiXinOpenId getWeiXinOpenId(String weiXinCode) {
        return null;
    }

    /**
     * 删除微信第三方用户的唯一标识票据
     * @param weiXinCode 微信第三方用户的唯一标识票据
     * @return
     */
    @CacheEvict(value="thirdPartyCacheManager_cache_weiXinOpenId",key="#weiXinCode")
    public void deleteWeiXinOpenId(String weiXinCode){
    }
}
