package cms.component.message;

import cms.repository.message.PrivateMessageRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 私信缓存管理
 */
@Component("privateMessageCacheManager")
public class PrivateMessageCacheManager {
    @Resource PrivateMessageRepository privateMessageRepository;

    /**
     * 查询缓存 查询未读私信数量
     * @param userId 用户Id
     * @return
     */
    @Cacheable(value="privateMessageCacheManager_cache_findUnreadPrivateMessageByUserId",key="#userId")
    public Long query_cache_findUnreadPrivateMessageByUserId(Long userId){
        return privateMessageRepository.findUnreadPrivateMessageByUserId(userId);
    }
    /**
     * 删除缓存 查询未读私信数量
     * @param userId 用户Id
     * @return
     */
    @CacheEvict(value="privateMessageCacheManager_cache_findUnreadPrivateMessageByUserId",key="#userId")
    public void delete_cache_findUnreadPrivateMessageByUserId(Long userId){
    }
}
