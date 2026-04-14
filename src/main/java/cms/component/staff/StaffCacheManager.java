package cms.component.staff;

import cms.dto.admin.PermissionMenu;
import cms.model.staff.SysUsers;
import cms.repository.staff.StaffRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * 员工缓存管理
 */
@Component("staffCacheManager")
public class StaffCacheManager {

    @Resource StaffRepository staffRepository;
    @Resource StaffComponent staffComponent;

    /**
     * 查询员工对应的权限菜单 -- 缓存
     * @param userAccount 用户账号
     * @return
     */
    @Cacheable(value="staffCacheManager_cache_queryStaffPermissionMenu",key="#userAccount")
    public List<PermissionMenu> query_staffPermissionMenu(String userAccount){
        return staffComponent.queryStaffPermissionMenu(userAccount);
    }
    /**
     * 删除员工对应的权限菜单 -- 缓存
     * @param userAccount 用户账号
     * @return
     */
    @CacheEvict(value="staffCacheManager_cache_queryStaffPermissionMenu",key="#userAccount")
    public void delete_staffPermissionMenu(String userAccount){

    }
    /**
     * 清空员工对应的权限菜单 -- 缓存
     * @return
     */
    @CacheEvict(value="staffCacheManager_cache_queryStaffPermissionMenu",allEntries=true)
    public void clear_staffPermissionMenu(){

    }


    /**
     * 查询所有权限 -- 缓存
     * @return
     */
    @Cacheable(value="staffCacheManager_cache_queryAllAuthorities")
    public Collection<GrantedAuthority> query_allAuthorities(){
        return staffRepository.loadAllAuthorities();
    }
    /**
     * 清空所有权限 -- 缓存
     */
    @CacheEvict(value="staffCacheManager_cache_queryAllAuthorities",allEntries=true)
    public void clear_allAuthorities(){
    }



    /**
     * 查询员工的权限 -- 缓存
     * @param userAccount 用户账号
     * @return
     */
    @Cacheable(value="staffCacheManager_cache_queryUserAuthoritiesByName",key="#userAccount")
    public Collection<GrantedAuthority> query_userAuthoritiesByName(String userAccount){
        return staffRepository.loadUserAuthoritiesByName(userAccount);
    }
    /**
     * 删除员工的权限 -- 缓存
     * @param userAccount 用户账号
     * @return
     */
    @CacheEvict(value="staffCacheManager_cache_queryUserAuthoritiesByName",key="#userAccount")
    public void delete_userAuthoritiesByName(String userAccount){
    }
    /**
     * 清空员工的权限 -- 缓存
     * @return
     */
    @CacheEvict(value="staffCacheManager_cache_queryUserAuthoritiesByName",allEntries=true)
    public void clear_userAuthoritiesByName(){
    }






    /**
     * 查询员工安全摘要
     * @param staffName 员工名称
     * @return
     */
    @Cacheable(value="staffCacheManager_cache_staffSecurityDigest",key="#staffName")
    public String query_staffSecurityDigest(String staffName){
        return staffRepository.findSecurityDigestByStaffName(staffName);
    }
    /**
     * 删除缓存员工安全摘要
     * @param staffName 员工名称
     * @return
     */
    @CacheEvict(value="staffCacheManager_cache_staffSecurityDigest",key="#staffName")
    public void delete_staffSecurityDigest(String staffName){

    }


    /**
     * 增加 登录失败次数
     * @param staffName 员工名称
     * @param count 总数
     */
    @CachePut(value="staffCacheManager_cache_loginFailureCount",key="#staffName")
    public Integer addLoginFailureCount(String staffName,Integer count){
        return count;
    }
    /**
     * 查询 登录失败次数
     * @param staffName 员工名称
     * @return
     */
    @Cacheable(value="staffCacheManager_cache_loginFailureCount",key="#staffName")
    public Integer getLoginFailureCount(String staffName){
        return null;
    }
    /**
     * 删除 登录失败次数
     * @param staffName 员工名称
     */
    @CacheEvict(value="staffCacheManager_cache_loginFailureCount",key="#staffName")
    public void deleteLoginFailureCount(String staffName){
    }


    /**
     * 查询缓存 根据账号查询当前员工
     * @param userAccount 员工账号
     * @return
     */
    @Cacheable(value="staffCacheManager_cache_findByUserAccount",key="#userAccount")
    public SysUsers query_cache_findByUserAccount(String userAccount){
        return staffRepository.findByUserAccount(userAccount);
    }
    /**
     * 删除缓存 根据账号查询当前员工
     * @param userAccount 员工账号
     */
    @CacheEvict(value="staffCacheManager_cache_findByUserAccount",key="#userAccount")
    public void delete_cache_findByUserAccount(String userAccount){
    }

    /**
     * 查询缓存 根据呢称查询当前员工
     * @param nickname 呢称
     * @return
     */
    @Cacheable(value="staffCacheManager_cache_findByNickname",key="#nickname")
    public SysUsers query_cache_findByNickname(String nickname){
        return staffRepository.findByNickname(nickname);
    }
    /**
     * 删除缓存 根据呢称查询当前员工
     * @param nickname 呢称
     */
    @CacheEvict(value="staffCacheManager_cache_findByNickname",key="#nickname")
    public void delete_cache_findByNickname(String nickname){
    }
}
