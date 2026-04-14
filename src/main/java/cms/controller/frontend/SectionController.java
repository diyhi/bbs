package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.component.frontendModule.SectionCacheManager;
import cms.model.frontendModule.Section;
import cms.service.frontend.SectionClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台站点栏目控制器
 */
@RestController
public class SectionController {

    @Resource
    SectionClientService sectionClientService;
    /**
     * 站点栏目列表
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6120100)
    public List<Section> sectionList(HttpServletRequest request){
        return sectionClientService.getSectionList();
    }
}
