package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.frontendModule.SectionCacheManager;
import cms.component.frontendModule.SectionComponent;
import cms.model.frontendModule.Section;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.SectionClientService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前台站点栏目服务
 */
@Service
public class SectionClientServiceImpl implements SectionClientService {
    @Resource
    SectionCacheManager sectionCacheManager;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    SettingRepository settingRepository;
    @Resource
    SectionComponent sectionComponent;


    /**
     * 获取站点栏目
     * @return
     */
    public List<Section> getSectionList(){
        return sectionCacheManager.query_cache_getSectionList();
    }
}
