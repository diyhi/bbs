package cms.controller.user;


import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.user.UserRoleRequest;
import cms.service.user.UserRoleService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 用户角色管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/userRole/manage")
public class UserRoleManageController {

    @Resource
    UserRoleService userRoleService;
    @Resource FileComponent fileComponent;


    /**
     * 用户角色管理 添加界面显示
     */
    @RequestMapping(params="method=add",method=RequestMethod.GET)
    public RequestResult addUI() {
        Map<String,Object> returnValue = userRoleService.getAddUserRoleViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 用户角色管理 添加
     * @param userRoleRequest 用户角色表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=add", method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute UserRoleRequest userRoleRequest, BindingResult result,
                             HttpServletRequest request) {
        userRoleService.addUserRole(userRoleRequest,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户角色   修改界面显示
     * @param userRoleId 用户角色Id
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.GET)
    public RequestResult editUI(String userRoleId) {
        Map<String,Object> returnValue = userRoleService.getEditUserRoleViewModel(userRoleId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 用户角色   修改
     * @param userRoleRequest 用户角色表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute UserRoleRequest userRoleRequest, BindingResult result,
                              HttpServletRequest request) {
        userRoleService.editUserRole(userRoleRequest,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户角色管理 设为默认
     * @param id 用户角色Id
     * @param defaultRole 是否设为默认角色
     * @return
     */
    @RequestMapping(params="method=setAsDefault",method= RequestMethod.POST)
    public RequestResult setAsDefault(String id,Boolean defaultRole) {
        userRoleService.editToDefaultRole(id,defaultRole);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户角色管理 删除
     * @param id 用户角色Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(String id) {
        userRoleService.deleteUserRole(id);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
}
