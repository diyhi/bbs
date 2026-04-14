package cms.service.setting.impl;

import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.lucene.QuestionIndexCacheManager;
import cms.component.lucene.TopicIndexCacheManager;
import cms.component.setting.SettingCacheManager;
import cms.component.setting.SettingComponent;
import cms.config.BusinessException;
import cms.model.setting.EditorTag;
import cms.model.setting.SystemNode;
import cms.model.setting.SystemSetting;
import cms.repository.frontendModule.FrontendApiRepository;
import cms.repository.frontendModule.FrontendSettingsRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.upgrade.UpgradeRepository;
import cms.service.setting.SystemSettingService;
import cms.service.staff.ACLService;
import cms.utils.CommentedProperties;
import cms.utils.Verification;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 系统设置服务
 */
@Service
public class SystemSettingServiceImpl implements SystemSettingService {
    private static final Logger logger = LogManager.getLogger(SystemSettingServiceImpl.class);

    @Resource SettingRepository settingRepository;
    @Resource JsonComponent jsonComponent;
    @Resource SettingCacheManager settingCacheManager;
    @Resource SettingComponent settingComponent;
    @Resource TopicIndexCacheManager topicIndexCacheManager;
    @Resource QuestionIndexCacheManager questionIndexCacheManager;
    @Resource FileComponent fileComponent;
    @Resource
    FrontendSettingsRepository frontendSettingsRepository;
    @Resource
    FrontendApiRepository frontendApiRepository;
    @Resource DataSource dataSource;
    @Resource
    ACLService aclService;

    @Resource
    UpgradeRepository upgradeRepository;

    /** 选择缓存产品**/
    @Value("${spring.cache.type}")
    private String selectCache;

    /**
     * 获取修改系统设置界面信息
     * @return
     */
    public Map<String, Object> getEditSystemSettingViewModel() {
        Map<String,Object> returnValue = new LinkedHashMap<String,Object>();

        SystemSetting systemSetting = settingRepository.findSystemSetting();

        if(systemSetting.getLanguageSwitching() != null && !systemSetting.getLanguageSwitching().trim().isEmpty()){
            List<String> languageSwitchingCodeList = jsonComponent.toObject(systemSetting.getLanguageSwitching(), List.class);
            systemSetting.setLanguageSwitchingCodeList(languageSwitchingCodeList);
        }
        if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
            List<String> languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
            systemSetting.setLanguageFormExtensionCodeList(languageFormExtensionCodeList);
        }
        if(systemSetting.getAllowRegisterAccountType() != null && !systemSetting.getAllowRegisterAccountType().trim().isEmpty()){
            List<Integer> allowRegisterAccountTypeCodeList = jsonComponent.toObject(systemSetting.getAllowRegisterAccountType(), List.class);
            systemSetting.setAllowRegisterAccountTypeCodeList(allowRegisterAccountTypeCodeList);
        }
        if(systemSetting.getAllowLoginAccountType() != null && !systemSetting.getAllowLoginAccountType().trim().isEmpty()){
            List<Integer> allowLoginAccountTypeCodeList = jsonComponent.toObject(systemSetting.getAllowLoginAccountType(), List.class);
            systemSetting.setAllowLoginAccountTypeCodeList(allowLoginAccountTypeCodeList);
        }


        if(systemSetting.getEditorTag() != null && !systemSetting.getEditorTag().trim().isEmpty()){
            EditorTag editorTag = jsonComponent.toObject(systemSetting.getEditorTag(), EditorTag.class);
            if(editorTag != null){
                systemSetting.setEditorTagObject(editorTag);
            }
        }

        if(systemSetting.getTopicEditorTag() != null && !systemSetting.getTopicEditorTag().trim().isEmpty()){
            EditorTag editorTag = jsonComponent.toObject(systemSetting.getTopicEditorTag(), EditorTag.class);
            if(editorTag != null){
                systemSetting.setTopicEditorTagObject(editorTag);
            }
        }
        if(systemSetting.getQuestionEditorTag() != null && !systemSetting.getQuestionEditorTag().trim().isEmpty()){
            EditorTag editorTag = jsonComponent.toObject(systemSetting.getQuestionEditorTag(), EditorTag.class);
            if(editorTag != null){
                systemSetting.setQuestionEditorTagObject(editorTag);
            }
        }
        if(systemSetting.getAnswerEditorTag() != null && !systemSetting.getAnswerEditorTag().trim().isEmpty()){
            EditorTag editorTag = jsonComponent.toObject(systemSetting.getAnswerEditorTag(), EditorTag.class);
            if(editorTag != null){
                systemSetting.setAnswerEditorTagObject(editorTag);
            }
        }


        returnValue.put("supportAccountType",settingComponent.getSupportAccountType());//支持账户类型


        //允许上传图片格式
        List<String> imageUploadFormatList = CommentedProperties.readRichTextAllowImageUploadFormat();
        returnValue.put("imageUploadFormatList",imageUploadFormatList);

        //允许上传文件格式
        List<String> fileUploadFormatList = CommentedProperties.readRichTextAllowFileUploadFormat();
        returnValue.put("fileUploadFormatList",fileUploadFormatList);

        //允许上传视频格式
        List<String> videoUploadFormatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
        returnValue.put("videoUploadFormatList",videoUploadFormatList);

        returnValue.put("systemSetting",systemSetting);
        return returnValue;
    }

    /**
     * 修改系统设置
     * @param systemSettingForm 系统设置表单
     */
    public void editSystemSetting(SystemSetting systemSettingForm){
        systemSettingForm.setLanguageSwitching(jsonComponent.toJSONString(systemSettingForm.getLanguageSwitchingCodeList()));
        systemSettingForm.setLanguageFormExtension(jsonComponent.toJSONString(systemSettingForm.getLanguageFormExtensionCodeList()));
        systemSettingForm.setAllowRegisterAccountType(jsonComponent.toJSONString(systemSettingForm.getAllowRegisterAccountTypeCodeList()));
        systemSettingForm.setAllowLoginAccountType(jsonComponent.toJSONString(systemSettingForm.getAllowLoginAccountTypeCodeList()));
        systemSettingForm.setTopicEditorTag(jsonComponent.toJSONString(systemSettingForm.getTopicEditorTagObject()));
        systemSettingForm.setEditorTag(jsonComponent.toJSONString(systemSettingForm.getEditorTagObject()));
        systemSettingForm.setQuestionEditorTag(jsonComponent.toJSONString(systemSettingForm.getQuestionEditorTagObject()));
        systemSettingForm.setAnswerEditorTag(jsonComponent.toJSONString(systemSettingForm.getAnswerEditorTagObject()));

        systemSettingForm.setId(1);
        systemSettingForm.setVersion(new Date().getTime());
        settingRepository.updateSystemSetting(systemSettingForm);
    }

    /**
     * 重建话题全文索引
     */
    public void rebuildTopicIndex(){
        Long count = topicIndexCacheManager.taskRunMark_add(-1L);

        if(count >=0L){
            throw new BusinessException(Map.of("rebuildTopicIndex", "任务正在运行"));
        }
        settingComponent.addAllTopicIndex();
    }
    /**
     * 重建问题全文索引
     */
    public void rebuildQuestionIndex(){
        Long count = questionIndexCacheManager.taskRunMark_add(-1L);

        if(count >=0L){
            throw new BusinessException(Map.of("rebuildQuestionIndex", "任务正在运行"));
        }
        settingComponent.addAllQuestionIndex();
    }



    /**
     * 删除浏览量数据
     * @param beforeTime 删除指定时间之前的数据
     */
    public void deletePageViewData(String beforeTime){
        if(beforeTime == null || beforeTime.trim().isEmpty()) {
            throw new BusinessException(Map.of("deletePageViewData_beforeTime", "时间不能为空"));
        }
        boolean beforeTimeVerification = Verification.isTime_minute(beforeTime.trim());
        if(!beforeTimeVerification) {
            throw new BusinessException(Map.of("deletePageViewData_beforeTime", "时间格式错误"));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        settingComponent.executeDeletePageViewData(LocalDateTime.parse(beforeTime.trim()+":00", formatter));
    }

    /**
     * 删除用户登录日志数据
     * @param beforeTime 删除指定时间之前的数据
     */
    public void deleteUserLoginLogData(String beforeTime){
        if(beforeTime == null || beforeTime.trim().isEmpty()) {
            throw new BusinessException(Map.of("deleteUserLoginLogData_beforeTime", "时间不能为空"));
        }
        boolean deleteUserLoginLogData_beforeTimeVerification = Verification.isTime_minute(beforeTime.trim());
        if(!deleteUserLoginLogData_beforeTimeVerification) {
            throw new BusinessException(Map.of("deleteUserLoginLogData_beforeTime", "时间格式错误"));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        settingComponent.executeDeleteUserLoginLogData(LocalDateTime.parse(beforeTime.trim()+":00", formatter));
    }

    /**
     * 获取节点参数界面信息
     */
    public Map<String,Object> getNodeParameterViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        SystemNode systemNode = new SystemNode();
        systemNode.setMaxMemory(settingComponent.maxMemory());//分配最大内存
        systemNode.setTotalMemory(settingComponent.totalMemory());//已分配内存
        systemNode.setFreeMemory(settingComponent.freeMemory());//已分配内存中的剩余空间
        systemNode.setUsableMemory(settingComponent.maxMemory() - settingComponent.totalMemory() + settingComponent.freeMemory());//空闲内存

        returnValue.put("systemNode",systemNode);

        returnValue.put("cacheName", selectCache);
        return returnValue;
    }
}