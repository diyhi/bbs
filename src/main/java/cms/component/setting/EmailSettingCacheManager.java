package cms.component.setting;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 邮箱设置缓存管理
 */
@Component("emailSettingCacheManager")
public class EmailSettingCacheManager {

    /**
     * 保存邮箱验证码标记
     * @param module 模块 1.绑定邮箱  2.更换绑定邮箱第一步  3.更换绑定邮箱第二步   100.注册   200.登录 300.找回密码
     * @param platformUserId 平台用户Id
     * @param email 邮箱
     * @param emailCode 验证码
     * @return
     */
    @CachePut(value="emailSettingCacheManager_cache_emailCode",key="#module+'_'+#platformUserId+'_'+#email")
    public String saveEmailCode(Integer module,String platformUserId,String email,String emailCode){
        return emailCode;
    }

    /**
     * 获取邮箱验证码标记
     * @param module 模块 1.绑定邮箱  2.更换绑定邮箱第一步  3.更换绑定邮箱第二步   100.注册   200.登录 300.找回密码
     * @param platformUserId 平台用户Id
     * @param email 邮箱
     * @return
     */
    @Cacheable(value="emailSettingCacheManager_cache_emailCode",key="#module+'_'+#platformUserId+'_'+#email")
    public String getEmailCode(Integer module,String platformUserId,String email){
        return null;
    }


    /**
     * 删除邮箱验证码标记
     * @param module 模块
     * @param platformUserId 平台用户Id
     * @param email 邮箱
     * @return
     */
    @CacheEvict(value="emailSettingCacheManager_cache_emailCode",key="#module+'_'+#platformUserId+'_'+#email")
    public void deleteEmailCode(Integer module,String platformUserId,String email){
    }
}
