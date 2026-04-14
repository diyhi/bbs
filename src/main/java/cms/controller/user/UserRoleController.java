package cms.controller.user;


import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.user.UserRole;
import cms.service.user.UserRoleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 用户角色控制器
 * @author Administrator
 *
 */
@RestController
public class UserRoleController {

    @Resource
    UserRoleService userRoleService;
    @Resource FileComponent fileComponent;


    /**
     * 用户角色列表
     * @return
     */
    @RequestMapping("/control/userRole/list")
    public RequestResult userRoleList(){
        List<UserRole> userRoleList = userRoleService.getAllRole();
        return new RequestResult(ResultCode.SUCCESS, userRoleList);
    }


}
