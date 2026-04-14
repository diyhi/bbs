package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.component.TextFilterComponent;
import cms.component.statistic.PageViewComponent;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台访问量统计控制器
 */
@RestController
public class StatisticController {
    @Resource
    PageViewComponent pageViewComponent;
    /**
     * 添加统计数据
     * @param url 当前访问页面URL
     * @param referrer 上一访问页面URL
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1151100)
    @RequestMapping(value="/statistic/add", method= RequestMethod.GET)
    public String add(String url, String referrer,
                      HttpServletRequest request, HttpServletResponse response) {
        if(url != null && !url.trim().isEmpty()){
            //统计访问量
            pageViewComponent.addPV(request,url,referrer);
        }
        return "";
    }
}
