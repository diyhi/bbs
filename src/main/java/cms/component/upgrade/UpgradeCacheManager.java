package cms.component.upgrade;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 升级缓存管理
 */
@Component("upgradeCacheManager")
public class UpgradeCacheManager {

    /**
     * 查询/添加任务运行标记
     * @param count 次数  -1为查询方式
     * @return
     */
    @Cacheable(value="upgradeCacheManager_cache_taskRunMark",key="'taskRunMark'")
    public Long taskRunMark_add(Long count){
        return count;
    }
    /**
     * 删除任务运行标记
     * @return
     */
    @CacheEvict(value="upgradeCacheManager_cache_taskRunMark",key="'taskRunMark'")
    public void taskRunMark_delete(){
    }

}
