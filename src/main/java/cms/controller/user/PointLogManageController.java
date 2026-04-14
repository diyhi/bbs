package cms.controller.user;


import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.user.PointLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 积分日志管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/pointLog/manage")
public class PointLogManageController {

    @Resource
    PointLogService pointLogService;
    @Resource FileComponent fileComponent;


    /**
     * 积分日志管理 详细显示
     * @param pointLogId 积分日志Id
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=show",method= RequestMethod.GET)
    public RequestResult show(String pointLogId,String userName,
                        HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = pointLogService.getPointLogDetail(pointLogId,userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);

    }
}
