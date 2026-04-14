package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.frontendModule.FrontendApiComponent;
import cms.dto.frontendModule.ImageAd;
import cms.model.frontendModule.ConfigImageAd;
import cms.model.frontendModule.FrontendApi;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.ImageAdClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 前台图片广告服务
 */
@Service
public class ImageAdClientServiceImpl implements ImageAdClientService {
    @Resource
    FrontendApiComponent frontendApiComponent;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;

    /**
     * 获取图片广告列表
     * @param request 请求信息
     * @return
     */
    public List<ImageAd> getImageAdList(HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }

        // 如果不是 List，直接跳过
        if (!(frontendApi.getConfigObject() instanceof List<?> list) || list.isEmpty()) {
            return null;
        }
        if (!(list.getFirst() instanceof ConfigImageAd)) {
            return null;
        }

        List<String> languageFormExtensionCodeList = null;
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
            languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
        }

        @SuppressWarnings("unchecked")
        List<ConfigImageAd> configImageAdList = (List<ConfigImageAd>) list;

        List<ImageAd> imageAdList = new ArrayList<ImageAd>();
        for(ConfigImageAd configImageAd : configImageAdList){
            ImageAd imageAd = new ImageAd();
            imageAd.setName(configImageAd.getName());
            imageAd.setLink(configImageAd.getLink());
            imageAd.setPath(configImageAd.getImagePath());

            imageAdList.add(imageAd);
        }
        return imageAdList;
    }
}
