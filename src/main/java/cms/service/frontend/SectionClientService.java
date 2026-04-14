package cms.service.frontend;

import cms.model.frontendModule.Section;

import java.util.List;

/**
 * 前台站点栏目服务接口
 */
public interface SectionClientService {
    /**
     * 获取站点栏目
     * @return
     */
    public List<Section> getSectionList();
}
