package cms.controller.admin;

import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.admin.AdminService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 后台框架页面控制器
 */
@RestController
public class AdminManageController {
    @Resource FileComponent fileComponent;
    @Resource AdminService adminService;

    /**
     * 后台管理框架页
     * @param request 请求信息
     * @return 请求结果
     */
    @RequestMapping("control/manage/index")
    public RequestResult framework(HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = adminService.queryAdminInfo(fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS,returnValue);
    }

    /**
     * 后台首页
     * @return 请求结果
     * @throws Exception
     */
    @RequestMapping("control/manage/home")
    public RequestResult home(){
        Map<String,Object> returnValue = adminService.adminOverview();
        return new RequestResult(ResultCode.SUCCESS,returnValue);
    }
}
