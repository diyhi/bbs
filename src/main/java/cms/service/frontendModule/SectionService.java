package cms.service.frontendModule;

import cms.model.frontendModule.Section;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 站点栏目服务
 */
public interface SectionService {

    /**
     * 添加站点栏目
     * @param sectionForm 站点栏目表单
     * @param request 请求信息
     */
    public void addSection(Section sectionForm, HttpServletRequest request);
    /**
     * 修改站点栏目
     * @param sectionForm 站点栏目表单
     * @param request 请求信息
     */
    public void editSection(Section sectionForm, HttpServletRequest request);
    /**
     * 删除站点栏目
     * @param sectionId 站点栏目Id
     */
    public void deleteSection(Integer sectionId);
}
