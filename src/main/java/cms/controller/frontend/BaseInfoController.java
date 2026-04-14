package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.service.frontend.BaseInfoClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台基本信息控制器
 */
@RestController
public class BaseInfoController {

    @Resource
    BaseInfoClientService baseInfoService;

    /**
     * 查询基本信息
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1150400)
    @RequestMapping(value="/baseInfo",method= RequestMethod.GET)
    public Map<String,Object> baseInfo(HttpServletRequest request) {
        return baseInfoService.getBaseInfo(request);
    }

    /**
     * 查询默认消息
     * 由cms.config.PageHandlerInterceptor.java跳转到本方法
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1150450)
    @RequestMapping(value="/message",method= RequestMethod.GET)
    public String message(HttpServletResponse response) {
        return baseInfoService.getMessage(response);
    }

}
