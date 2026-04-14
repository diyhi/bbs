package cms.component.follow;

import cms.model.follow.Follow;
import cms.repository.follow.FollowRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 关注缓存管理
 */
@Component("followCacheManager")
public class FollowCacheManager {
    @Resource FollowRepository followRepository;
    /**
     * 查询缓存 查询关注
     * @param followId 关注Id
     * @return
     */
    @Cacheable(value="followCacheManager_cache_findById",key="#followId")
    public Follow query_cache_findById(String followId){
        return followRepository.findById(followId);
    }
    /**
     * 删除缓存 关注
     * @param followId 关注Id
     * @return
     */
    @CacheEvict(value="followCacheManager_cache_findById",key="#followId")
    public void delete_cache_findById(String followId){
    }


    /**
     * 查询缓存 我关注的用户的更新标记
     * @param userName 用户名称
     * @param uuid 标记Id
     * @return
     */
    @Cacheable(value="followCacheManager_cache_userUpdateFlag",key="#userName")
    public String query_cache_userUpdateFlag(String userName,String uuid){
        return uuid;
    }
    /**
     * 删除缓存 我关注的用户的更新标记
     * @param userName 用户名称
     * @return
     */
    @CacheEvict(value="followCacheManager_cache_userUpdateFlag",key="#userName")
    public void delete_cache_userUpdateFlag(String userName){
    }

    /**
     * 查询缓存 根据用户名称查询所有关注
     * @param userId 用户Id
     * @param userName 用户名称
     * @return
     */
    @Cacheable(value="followCacheManager_cache_findAllFollow",key="#userName")
    public List<Follow> query_cache_findAllFollow(Long userId, String userName){
        return followRepository.findAllFollow(userId,userName);
    }
    /**
     * 删除缓存 根据用户名称查询所有关注
     * @param userName 用户名称
     * @return
     */
    @CacheEvict(value="followCacheManager_cache_findAllFollow",key="#userName")
    public void delete_cache_findAllFollow(String userName){
    }

    /**
     * 查询缓存 查询关注总数
     * @param userName 用户名称
     * @return
     */
    @Cacheable(value="followCacheManager_cache_followCount",key="#userName")
    public Long query_cache_followCount(Long userId, String userName){
        return followRepository.findFollowCountByUserName(userId, userName);
    }
    /**
     * 删除缓存 关注总数
     * @param userName 用户名称
     * @return
     */
    @CacheEvict(value="followCacheManager_cache_followCount",key="#userName")
    public void delete_cache_followCount(String userName){
    }
}
