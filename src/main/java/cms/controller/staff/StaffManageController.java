package cms.controller.staff;


import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.staff.SysUsers;
import cms.service.staff.StaffService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 员工管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/staff/manage")
public class StaffManageController {
    @Resource StaffService staffService;
    @Resource FileComponent fileComponent;


    /**
     * 员工管理 添加界面显示
     * @return
     */
    @RequestMapping(params="method=addStaff",method= RequestMethod.GET)
    public RequestResult addStaffUI(){

        // 获取当前已认证用户的信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        boolean isSysAdmin = false;

        // 从 Authentication 对象中获取已认证用户的详细信息
        if (authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        if (authentication.getPrincipal() instanceof SysUsers) {
            isSysAdmin = ((SysUsers) authentication.getPrincipal()).isIssys();
        }

        Map<String,Object> returnValue = staffService.getAddStaffViewModel(username,isSysAdmin);
        return new RequestResult(ResultCode.SUCCESS,returnValue);
    }

    /**
     * 员工管理 添加
     * @param sysUsersForm 员工表单
     * @param result 存储校验信息
     * @param repeatPassword 重复密码
     * @param sysRolesId 角色Id集合
     * @param width 图片宽
     * @param height 图片高
     * @param x X轴
     * @param y Y轴
     * @param file 头像文件
     * @return
     */
    @RequestMapping(params="method=addStaff",method=RequestMethod.POST)
    public RequestResult addStaff(@ModelAttribute SysUsers sysUsersForm, BindingResult result,
                                  String repeatPassword, String[] sysRolesId, Integer width, Integer height, Integer x, Integer y,
                                  MultipartFile file){

        staffService.addStaff(sysUsersForm,repeatPassword,sysRolesId, width, height, x, y,file);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 员工管理 修改界面显示
     * @param userId 员工Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editStaff",method=RequestMethod.GET)
    public RequestResult editStaffUI(String userId,
                              HttpServletRequest request){

        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = staffService.getEditStaffViewModel(userId,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS,returnValue);
    }

    /**
     * 员工管理 修改
     * @param sysUsersForm 员工表单
     * @param result 存储校验信息
     * @param userId 员工Id
     * @param repeatPassword 重复密码
     * @param sysRolesId 角色Id集合
     * @param width 图片宽
     * @param height 图片高
     * @param x X轴
     * @param y Y轴
     * @param file 头像文件
     * @return
     */
    @RequestMapping(params="method=editStaff",method=RequestMethod.POST)
    public RequestResult editStaff(@ModelAttribute SysUsers sysUsersForm, BindingResult result,
                            String userId,String repeatPassword,String[] sysRolesId,
                            Integer width, Integer height, Integer x, Integer y,MultipartFile file) {

        staffService.editStaff(sysUsersForm,userId,repeatPassword,sysRolesId, width, height, x, y,file);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 员工管理 修改自身信息界面显示
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editSelfInfo",method=RequestMethod.GET)
    public RequestResult editSelfInfoUI(HttpServletRequest request){

        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = staffService.getEditStaffSelfViewModel(fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS,returnValue);

    }

    /**
     * 员工管理 修改自身信息
     * @param sysUsersForm 员工表单
     * @param result 存储校验信息
     * @param repeatPassword 重复密码
     * @param sysRolesId 角色Id集合
     * @param width 图片宽
     * @param height 图片高
     * @param x X轴
     * @param y Y轴
     * @param file 头像文件
     * @return
     */
    @RequestMapping(params="method=editSelfInfo",method=RequestMethod.POST)
    public RequestResult editSelfInfo(@ModelAttribute SysUsers sysUsersForm, BindingResult result,
                               String repeatPassword, String[] sysRolesId, Integer width, Integer height, Integer x, Integer y,
                               MultipartFile file){

        staffService.editSelfInfo(sysUsersForm,repeatPassword,sysRolesId, width, height, x, y,file);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 员工管理 删除
     * @param userId 员工Id
     * @return
     */
    @RequestMapping(params="method=deleteStaff",method=RequestMethod.POST)
    public RequestResult deleteStaff(String userId){
        staffService.deleteStaff(userId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

}
