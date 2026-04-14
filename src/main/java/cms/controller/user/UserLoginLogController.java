package cms.controller.user;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.user.UserLoginLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户登录日志控制器
 *
 */
@RestController
public class UserLoginLogController {
    @Resource
    UserLoginLogService userLoginLogService;
    @Resource
    FileComponent fileComponent;
    /**
     * 用户登录日志列表
     * @param pageForm 页码
     * @param id 用户Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/userLoginLog/list")
    public RequestResult userLoginLogList(PageForm pageForm,Long id, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = userLoginLogService.getUserLoginLogList(pageForm.getPage(),id,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }
}
