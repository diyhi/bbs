package cms.controller.staff;


import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.staff.PermissionObject;
import cms.model.staff.SysResources;
import cms.model.staff.SysRoles;
import cms.service.staff.ACLService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 访问控制管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/acl/manage")
public class ACLManageController {
    @Resource
    ACLService aclService;

    /**
     * 查询模块名称分页显示
     *
     * @param pageForm 分页
     * @return
     */
    @RequestMapping(params = "method=queryModuleName", method = RequestMethod.GET)
    public RequestResult queryModuleName(PageForm pageForm){

        PageView<SysResources> pageView = aclService.getModuleNames(pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }



    /**
     * 角色管理 添加界面显示
     * @return
     */
    @RequestMapping(params="method=addRoles",method=RequestMethod.GET)
    public RequestResult addRolesUI(){
        LinkedHashMap<String, List<PermissionObject>> returnValue = aclService.getAddRolesViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 角色管理 添加
     * @param sysRolesForm 角色表单
     * @param sysPermissionId 权限Id集合
     * @return
     */
    @RequestMapping(params="method=addRoles",method=RequestMethod.POST)
    public RequestResult addRoles(SysRoles sysRolesForm,String[] sysPermissionId){
        aclService.addRoles(sysRolesForm,sysPermissionId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 角色管理 修改页面显示
     * @param rolesId 角色Id
     * @return
     */
    @RequestMapping(params="method=editRoles",method=RequestMethod.GET)
    public RequestResult editRolesUI(String rolesId){
        Map<String,Object> returnValue = aclService.getEditRolesViewModel(rolesId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 角色管理 修改
     * @param sysRolesForm 角色表单
     * @param sysPermissionId 权限Id集合
     * @param rolesId 角色Id
     * @return
     */
    @RequestMapping(params="method=editRoles",method=RequestMethod.POST)
    public RequestResult editRoles(SysRoles sysRolesForm, String[] sysPermissionId,String rolesId){
        aclService.editRoles(sysRolesForm,sysPermissionId,rolesId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 角色管理 删除
     * @param rolesId 角色Id
     * @return
     */
    @RequestMapping(params="method=deleteRoles",method=RequestMethod.POST)
    public RequestResult deleteRoles(String rolesId) {
        aclService.deleteRoles(rolesId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
}