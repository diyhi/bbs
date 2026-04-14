package cms.controller.user;


import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.user.PointLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 积分日志控制器
 * @author Administrator
 *
 */
@RestController
public class PointLogController {

    @Resource
    PointLogService pointLogService;
    @Resource FileComponent fileComponent;


    /**
     * 积分日志列表
     * @param pageForm 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/pointLog/list")
    public RequestResult pointLogList(PageForm pageForm,String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = pointLogService.getPointLogList(pageForm.getPage(),userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }



}
