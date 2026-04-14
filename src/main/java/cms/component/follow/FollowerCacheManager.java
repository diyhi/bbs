package cms.component.follow;


import cms.repository.follow.FollowRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 粉丝缓存管理
 */
@Component("followerCacheManager")
public class FollowerCacheManager {
    @Resource
    FollowRepository followRepository;

    /**
     * 查询缓存 查询粉丝总数
     * @param userName 用户名称
     * @return
     */
    @Cacheable(value="followerCacheManager_cache_followerCount",key="#userName")
    public Long query_cache_followerCount(Long userId, String userName){
        return followRepository.findFollowerCountByUserName(userId, userName);
    }
    /**
     * 删除缓存 粉丝总数
     * @param userName 用户名称
     * @return
     */
    @CacheEvict(value="followerCacheManager_cache_followerCount",key="#userName")
    public void delete_cache_followerCount(String userName){
    }

}
