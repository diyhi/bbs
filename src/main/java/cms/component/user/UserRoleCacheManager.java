package cms.component.user;

import cms.model.user.UserRoleGroup;
import cms.repository.user.UserRoleRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 员工缓存管理
 */
@Component("userRoleCacheManager")
public class UserRoleCacheManager {
    @Resource UserRoleRepository userRoleRepository;
    /**
     * 查询缓存 根据用户名称查询角色组
     * @param userName 用户名称
     * @return
     */
    @Cacheable(value="userRoleCacheManager_cache_findRoleGroupByUserName",key="#userName")
    public List<UserRoleGroup> query_cache_findRoleGroupByUserName(String userName){
        return userRoleRepository.findRoleGroupByUserName(userName);
    }
    /**
     * 删除缓存 根据用户名称查询角色组
     * @param userName 用户名称
     * @return
     */
    @CacheEvict(value="userRoleCacheManager_cache_findRoleGroupByUserName",key="#userName")
    public void delete_cache_findRoleGroupByUserName(String userName){
    }

}
