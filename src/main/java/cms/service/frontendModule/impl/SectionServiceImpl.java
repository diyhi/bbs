package cms.service.frontendModule.impl;


import cms.component.JsonComponent;
import cms.component.frontendModule.SectionCacheManager;
import cms.component.frontendModule.SectionComponent;
import cms.config.BusinessException;
import cms.model.frontendModule.Section;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import cms.service.frontendModule.SectionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 站点栏目服务
 */
@Service
public class SectionServiceImpl implements SectionService {

    @Resource
    SectionCacheManager sectionCacheManager;
    @Resource
    SectionComponent sectionComponent;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;

    private final List<Integer> linkModeCode = Arrays.asList(1,2,3);

    /**
     * 添加站点栏目
     * @param sectionForm 站点栏目表单
     * @param request 请求信息
     */
    public void addSection(Section sectionForm, HttpServletRequest request){
        Map<String, String> errors = new HashMap<String, String>();


        validateSectionFields(sectionForm, errors);

        //多语言扩展  key:字段-语言（例如：name-en_US） value:内容
        Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
            List<String> languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
            if(languageFormExtensionCodeList != null && languageFormExtensionCodeList.size() >0){
                for(String languageFormExtensionCode : languageFormExtensionCodeList){

                    String nameParameter = request.getParameter("name-"+languageFormExtensionCode);
                    if(nameParameter != null && !nameParameter.trim().isEmpty()){
                        if(nameParameter.length() >30){
                            errors.put("name-"+languageFormExtensionCode, "不能大于30个字符");
                        }else{
                            multiLanguageExtensionMap.put("name-"+languageFormExtensionCode, nameParameter);
                        }
                    }

                }
            }
        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        Section section = new Section();
        section.setName(sectionForm.getName());
        section.setUrl(sectionForm.getUrl() == null ? "" : sectionForm.getUrl().trim());
        section.setLinkMode(sectionForm.getLinkMode());
        section.setMultiLanguageExtensionMap(multiLanguageExtensionMap);
        section.setSort(sectionForm.getSort());
        section.setParentId(sectionForm.getParentId() == null ? 0 : sectionForm.getParentId());

        sectionComponent.addSection(section);
        sectionCacheManager.delete_cache_getSectionList();
    }

    /**
     * 修改站点栏目
     * @param sectionForm 站点栏目表单
     * @param request 请求信息
     */
    public void editSection(Section sectionForm, HttpServletRequest request){
        Map<String, String> errors = new HashMap<String, String>();

        if (sectionForm.getId() == null) {
            throw new BusinessException(Map.of("sectionId", "Id不能为空"));
        }

        validateSectionFields(sectionForm, errors);

        //多语言扩展  key:字段-语言（例如：name-en_US） value:内容
        Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
            List<String> languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
            if(languageFormExtensionCodeList != null && languageFormExtensionCodeList.size() >0){
                for(String languageFormExtensionCode : languageFormExtensionCodeList){

                    String nameParameter = request.getParameter("name-"+languageFormExtensionCode);
                    if(nameParameter != null && !nameParameter.trim().isEmpty()){
                        if(nameParameter.length() >30){
                            errors.put("name-"+languageFormExtensionCode, "不能大于30个字符");
                        }else{
                            multiLanguageExtensionMap.put("name-"+languageFormExtensionCode, nameParameter);
                        }
                    }

                }
            }
        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        Section section = new Section();
        section.setId(sectionForm.getId());
        section.setName(sectionForm.getName());
        section.setUrl(sectionForm.getUrl() == null ? "" : sectionForm.getUrl().trim());
        section.setLinkMode(sectionForm.getLinkMode());
        section.setMultiLanguageExtensionMap(multiLanguageExtensionMap);
        section.setSort(sectionForm.getSort());
        section.setParentId(sectionForm.getParentId() == null ? 0 : sectionForm.getParentId());

        sectionComponent.updateSection(section);
        sectionCacheManager.delete_cache_getSectionList();
    }


    /**
     * 删除站点栏目
     * @param sectionId 站点栏目Id
     */
    public void deleteSection(Integer sectionId){
        if (sectionId == null) {
            throw new BusinessException(Map.of("sectionId", "Id不能为空"));
        }
        sectionComponent.deleteSection(sectionId);
        sectionCacheManager.delete_cache_getSectionList();
    }


    /**
     * 验证站点栏目的字段
     *
     * @param sectionForm 站点栏目表单
     * @param errors           错误信息
     */
    private void validateSectionFields(Section sectionForm, Map<String, String> errors) {
        if (sectionForm.getName() == null || sectionForm.getName().trim().isEmpty()) {
            errors.put("name", "名称不能为空");
        }
        if (sectionForm.getLinkMode() == null || !linkModeCode.contains(sectionForm.getLinkMode())) {
            errors.put("linkMode", "请选择链接方式");
        }else{
            if(sectionForm.getLinkMode().equals(2) || sectionForm.getLinkMode().equals(3)) {//外部URL 和 内部URL
                if (sectionForm.getUrl() == null || sectionForm.getUrl().trim().isEmpty()) {
                    errors.put("url", "URL不能为空");
                }
            }
        }
        if(errors.isEmpty()) {
            if(sectionForm.getLinkMode().equals(2) && !sectionComponent.validURL(sectionForm.getUrl().trim())){
                errors.put("url", "不是正确的网址");
            }
            if(sectionForm.getLinkMode().equals(3)){
                if(sectionForm.getUrl().trim().matches("/.+?")){
                    errors.put("url", "不能以/开头");
                }
                if(sectionForm.getUrl().trim().matches(".*/{2,}.*")){
                    errors.put("url", "左斜杆不能连续出现");
                }
            }
        }

        if (sectionForm.getSort() != null && sectionForm.getSort() >99999999) {
            errors.put("sort", "排序号不能超过99999999");
        }


    }
}
