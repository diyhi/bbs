package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.component.fileSystem.FileComponent;
import cms.model.links.Links;
import cms.service.frontend.LinkClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 友情链接控制器
 */
@RestController("frontendLinkController")
public class LinkController {
    @Resource
    FileComponent fileComponent;
    @Resource
    LinkClientService linkClientService;

    /**
     * 友情链接列表
     * @param  request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6090100)
    public List<Links> linksList(HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);

        return linkClientService.getLinksList(fileServerAddress);
    }
}
