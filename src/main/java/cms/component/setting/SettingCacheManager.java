package cms.component.setting;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;


/**
 * 设置缓存管理
 */
@Component("settingCacheManager")
public class SettingCacheManager {

    /**
     * 增加 用户每分钟提交次数
     * @param module 模块
     * @param userName 用户名称
     * @param count 次数
     * @return
     */
    @CachePut(value="settingCacheManager_cache_submitQuantity",key="#module + '_' + #userName")
    public Integer addSubmitQuantity(String module,String userName,Integer count){
        return count;
    }
    /**
     * 查询 用户每分钟提交次数
     * @param module 模块
     * @param userName 用户名称
     * @return
     */
    @Cacheable(value="settingCacheManager_cache_submitQuantity",key="#module + '_' + #userName")
    public Integer getSubmitQuantity(String module,String userName){
        return null;
    }
    /**
     * 删除 用户每分钟提交次数
     * @param module 模块
     * @param userName 用户名称
     * @return
     */
    @CacheEvict(value="settingCacheManager_cache_submitQuantity",key="#module + '_' + #userName")
    public void deleteSubmitQuantity(String module,String userName){
    }

}
