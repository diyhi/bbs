package cms.component;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 验证码缓存管理
 */
@Component("captchaCacheManager")
public class CaptchaCacheManager {

    /**
     * 生成验证码
     * @param captchaKey 验证码KEY
     * @param captcha 验证码
     * @return
     */
    @CachePut(value="captchaCacheManager_cache_captcha",key="#captchaKey")
    public String saveCaptcha(String captchaKey,String captcha){
        return captcha;
    }
    /**
     * 查询验证码
     * 使用 @Cacheable：缓存存在则返回，不存在则执行方法（返回null）
     */
    @Cacheable(value="captchaCacheManager_cache_captcha", key="#captchaKey")
    public String getCaptcha(String captchaKey) {
        return null;
    }
    /**
     * 删除验证码
     * @param captchaKey 验证码KEY
     * @return
     */
    @CacheEvict(value="captchaCacheManager_cache_captcha",key="#captchaKey")
    public void deleteCaptcha(String captchaKey){
    }



}
