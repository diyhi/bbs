package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.dto.frontendModule.ImageAd;
import cms.service.frontend.ImageAdClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台图片广告控制器
 */
@RestController
public class ImageAdController {
    @Resource
    ImageAdClientService imageAdClientService;
    /**
     * 图片广告
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6100100)
    public List<ImageAd> imageAdList(HttpServletRequest request){
        return imageAdClientService.getImageAdList(request);
    }
}
