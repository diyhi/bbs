package cms.component.sms;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 员工缓存管理
 */
@Component("smsCacheManager")
public class SmsCacheManager {

    /**
     * 缓存增加用户发短信次数
     * @param platformUserId 平台用户Id
     * @param count 发送总数
     */
    @Cacheable(value="smsCacheManager_cache_sentSmsCount",key="#platformUserId")
    public Integer addSentSmsCount(String platformUserId,Integer count){
        return count;
    }
    /**
     * 删除缓存用户发短信次数
     * @param platformUserId 平台用户Id
     */
    @CacheEvict(value="smsCacheManager_cache_sentSmsCount",key="#platformUserId")
    public void deleteSentSmsCount(String platformUserId){
    }



    /**
     * 生成手机验证码标记
     * @param module 模块 1.绑定手机  2.更换绑定手机第一步  3.更换绑定手机第二步   100.注册   200.登录 300.找回密码
     * @param platformUserId 平台用户Id
     * @param mobile 手机号
     * @param mobile 验证码
     * @return
     */
    @Cacheable(value="smsCacheManager_cache_smsCode",key="#module+'_'+#platformUserId+'_'+#mobile")
    public String smsCode_generate(Integer module,String platformUserId,String mobile,String smsCode){
        return smsCode;
    }
    /**
     * 删除手机验证码标记
     * @param module 模块
     * @param platformUserId 平台用户Id
     * @param mobile 手机号
     * @return
     */
    @CacheEvict(value="smsCacheManager_cache_smsCode",key="#module+'_'+#platformUserId+'_'+#mobile")
    public void smsCode_delete(Integer module,String platformUserId,String mobile){
    }

    /**
     * 更换绑定手机验证码标记
     * @param userId 用户Id
     * @param mobile 手机号
     * @param allow 是否允许
     * @return
     */
    @Cacheable(value="smsCacheManager_cache_replaceCode",key="#userId+'_'+#mobile")
    public boolean replaceCode_generate(Long userId,String mobile,boolean allow){
        return allow;
    }
    /**
     * 删除绑定手机验证码标记
     * @param userId 用户Id
     * @param mobile 手机号
     * @return
     */
    @CacheEvict(value="smsCacheManager_cache_replaceCode",key="#userId+'_'+#mobile")
    public void replaceCode_delete(Long userId,String mobile){
    }
}
