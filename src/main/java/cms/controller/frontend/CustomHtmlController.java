package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.service.frontend.CustomHTMLClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台自定义HTML控制器
 */
@RestController("frontendCustomHtmlController")
public class CustomHtmlController {

    @Resource
    CustomHTMLClientService customHTMLClientService;
    /**
     * 自定义HTML
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6150100)
    public String customHTML(HttpServletRequest request){
        return customHTMLClientService.getCustomHTML(request);
    }
}
