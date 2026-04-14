package cms.component.message;

import cms.repository.message.RemindRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 提醒缓存管理
 */
@Component("remindCacheManager")
public class RemindCacheManager {

    @Resource RemindRepository remindRepository;

    /**
     * 查询缓存 查询未读提醒数量
     * @param userId 用户Id
     * @return
     */
    @Cacheable(value="remindCacheManager_cache_findUnreadRemindByUserId",key="#userId")
    public Long query_cache_findUnreadRemindByUserId(Long userId){
        return remindRepository.findUnreadRemindByUserId(userId);
    }
    /**
     * 删除缓存 查询未读提醒数量
     * @param userId 用户Id
     * @return
     */
    @CacheEvict(value="remindCacheManager_cache_findUnreadRemindByUserId",key="#userId")
    public void delete_cache_findUnreadRemindByUserId(Long userId){
    }
}
