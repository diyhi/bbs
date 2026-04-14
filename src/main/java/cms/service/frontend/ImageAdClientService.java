package cms.service.frontend;


import cms.dto.frontendModule.ImageAd;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 前台图片广告服务接口
 */
public interface ImageAdClientService {
    /**
     * 获取图片广告列表
     * @param request 请求信息
     * @return
     */
    public List<ImageAd> getImageAdList(HttpServletRequest request);

}
