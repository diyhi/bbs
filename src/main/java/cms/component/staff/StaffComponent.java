package cms.component.staff;

import cms.dto.admin.PermissionMenu;
import cms.model.staff.SysUsers;
import cms.repository.staff.ACLRepository;
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
 * 员工组件
 */
@Component("staffComponent")
public class StaffComponent {

    @Resource StaffRepository staffRepository;
    @Resource ACLRepository aclRepository;


    /**
     * 查询用户的权限菜单
     * @param userAccount 用户账号
     * @return
     */
    public List<PermissionMenu> queryStaffPermissionMenu(String userAccount){

        SysUsers sysUsers = staffRepository.findByUserAccount(userAccount);

        if(sysUsers != null && sysUsers.isEnabled()){

            if(sysUsers.isIssys()){//超级用户
                return aclRepository.findAllPermissionMenu();
            }else{
                //权限Id集合
                List<String> sysPermissionIdList = staffRepository.findPermissionIdByUserAccount(userAccount);
                if(sysPermissionIdList != null && sysPermissionIdList.size() >0){
                    return aclRepository.findPermissionMenuByPermissionIdList(sysPermissionIdList);
                }
            }
        }
        return null;
    }

}
