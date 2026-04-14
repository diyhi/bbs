package cms.validator.frontendModule;


import cms.component.JsonComponent;
import cms.dto.frontendModule.FrontendApiForm;
import cms.model.frontendModule.ConfigImageAd;
import cms.model.frontendModule.FrontendApi;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 前台API校验器
 * @author Gao
 */
@Component("frontendApiValidator")
public class FrontendApiValidator implements Validator{
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;

    private final List<String> methods = Arrays.asList("GET","POST");

    public boolean supports(Class<?> clazz) {//该校验器支持的目标类
		return clazz.equals(FrontendApiForm.class);
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
        FrontendApiForm frontendApiForm = (FrontendApiForm)obj;
        FrontendApi frontendApi = frontendApiForm.getFrontendApi();
        if(frontendApi == null){
            errors.rejectValue("frontendApi","errors.common", new String[]{"前台API不能为空"},"");
            return;
        }


        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        List<String> languageFormExtensionCodeList = null;
        if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()) {
            languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);

        }



        //名称
        if(frontendApi.getName() != null && !frontendApi.getName().trim().isEmpty()){
            if(frontendApi.getName().trim().length() >80){
                errors.rejectValue("frontendApi.name","errors.common", new String[]{"不能超过80个字符"},"");
            }
        }else{
            errors.rejectValue("frontendApi.name","errors.common", new String[]{"名称不能为空"},"");
        }
        //URL路径
        if(frontendApi.getUrl() != null && !frontendApi.getUrl().trim().isEmpty()){
            if(frontendApi.getUrl().trim().length() >80){
                errors.rejectValue("frontendApi.url","errors.common", new String[]{"不能超过80个字符"},"");
            }
        }
        //请求方法
        if(frontendApi.getHttpMethod() != null && !frontendApi.getHttpMethod().trim().isEmpty()){
            if(!methods.contains(frontendApi.getHttpMethod())){
                errors.rejectValue("frontendApi.httpMethod","errors.common", new String[]{"请求方法格式不正确"},"");
            }
        }
        //话题分页
        if(frontendApiForm.getConfigTopicPage() != null){
            if(frontendApiForm.getConfigTopicPage().getMaxResult() != null && frontendApiForm.getConfigTopicPage().getMaxResult() <=0){
                errors.rejectValue("configTopicPage.maxResult","errors.common", new String[]{"每页显示记录数必须大于0"},"");
            }
            if(frontendApiForm.getConfigTopicPage().getPageCount() != null && frontendApiForm.getConfigTopicPage().getPageCount() <=0){
                errors.rejectValue("configTopicPage.pageCount","errors.common", new String[]{"页码显示总数必须大于0"},"");
            }
        }
        //评论分页
        if(frontendApiForm.getConfigCommentPage() != null){
            if(frontendApiForm.getConfigCommentPage().getMaxResult() != null && frontendApiForm.getConfigCommentPage().getMaxResult() <=0){
                errors.rejectValue("configCommentPage.maxResult","errors.common", new String[]{"每页显示记录数必须大于0"},"");
            }
            if(frontendApiForm.getConfigCommentPage().getPageCount() != null && frontendApiForm.getConfigCommentPage().getPageCount() <=0){
                errors.rejectValue("configCommentPage.pageCount","errors.common", new String[]{"页码显示总数必须大于0"},"");
            }
        }
        //相似话题
        if(frontendApiForm.getConfigSimilarTopic() != null){
            if(frontendApiForm.getConfigSimilarTopic().getMaxResult() != null && frontendApiForm.getConfigSimilarTopic().getMaxResult() <=0){
                errors.rejectValue("configSimilarTopic.maxResult","errors.common", new String[]{"每页显示记录数必须大于0"},"");
            }
        }
        //热门话题
        if(frontendApiForm.getConfigHotTopic() != null){
            if(frontendApiForm.getConfigHotTopic().getMaxResult() != null && frontendApiForm.getConfigHotTopic().getMaxResult() <=0){
                errors.rejectValue("configHotTopic.maxResult","errors.common", new String[]{"每页显示记录数必须大于0"},"");
            }
        }
        //话题精华分页
        if(frontendApiForm.getConfigTopicFeaturedPage() != null){
            if(frontendApiForm.getConfigTopicFeaturedPage().getMaxResult() != null && frontendApiForm.getConfigTopicFeaturedPage().getMaxResult() <=0){
                errors.rejectValue("configTopicFeaturedPage.maxResult","errors.common", new String[]{"每页显示记录数必须大于0"},"");
            }
            if(frontendApiForm.getConfigTopicFeaturedPage().getPageCount() != null && frontendApiForm.getConfigTopicFeaturedPage().getPageCount() <=0){
                errors.rejectValue("configTopicFeaturedPage.pageCount","errors.common", new String[]{"页码显示总数必须大于0"},"");
            }
        }

        //问题分页
        if(frontendApiForm.getConfigQuestionPage() != null){
            if(frontendApiForm.getConfigQuestionPage().getMaxResult() != null && frontendApiForm.getConfigQuestionPage().getMaxResult() <=0){
                errors.rejectValue("configQuestionPage.maxResult","errors.common", new String[]{"每页显示记录数必须大于0"},"");
            }
            if(frontendApiForm.getConfigQuestionPage().getPageCount() != null && frontendApiForm.getConfigQuestionPage().getPageCount() <=0){
                errors.rejectValue("configQuestionPage.pageCount","errors.common", new String[]{"页码显示总数必须大于0"},"");
            }
        }
        //答案分页
        if(frontendApiForm.getConfigAnswerPage() != null){
            if(frontendApiForm.getConfigAnswerPage().getMaxResult() != null && frontendApiForm.getConfigAnswerPage().getMaxResult() <=0){
                errors.rejectValue("configAnswerPage.maxResult","errors.common", new String[]{"每页显示记录数必须大于0"},"");
            }
            if(frontendApiForm.getConfigAnswerPage().getPageCount() != null && frontendApiForm.getConfigAnswerPage().getPageCount() <=0){
                errors.rejectValue("configAnswerPage.pageCount","errors.common", new String[]{"页码显示总数必须大于0"},"");
            }
        }
        //相似问题
        if(frontendApiForm.getConfigSimilarQuestion() != null){
            if(frontendApiForm.getConfigSimilarQuestion().getMaxResult() != null && frontendApiForm.getConfigSimilarQuestion().getMaxResult() <=0){
                errors.rejectValue("configSimilarQuestion.maxResult","errors.common", new String[]{"每页显示记录数必须大于0"},"");
            }
        }



        //领取红包用户分页
        if(frontendApiForm.getConfigRedEnvelopeUserPage() != null){
            if(frontendApiForm.getConfigRedEnvelopeUserPage().getMaxResult() != null && frontendApiForm.getConfigRedEnvelopeUserPage().getMaxResult() <=0){
                errors.rejectValue("configRedEnvelopeUserPage.maxResult","errors.common", new String[]{"每页显示记录数必须大于0"},"");
            }
        }
        //图片广告
        if(frontendApiForm.getConfigImageAdList() != null && !frontendApiForm.getConfigImageAdList().isEmpty()){
            for (int i = 0; i < frontendApiForm.getConfigImageAdList().size(); i++) {
                ConfigImageAd configImageAd = frontendApiForm.getConfigImageAdList().get(i);
                if(configImageAd.getName() == null || configImageAd.getName().trim().isEmpty()){
                    //这里要使用configImageAdList正确映射，Controller类手动替换成configImageAd适配前端的属性
                    errors.rejectValue("configImageAdList["+i+"].name","errors.common", new String[]{"图片名称不能为空"},"");
                }

                if(languageFormExtensionCodeList != null && !languageFormExtensionCodeList.isEmpty()) {

                    Set<String> validKeySet = languageFormExtensionCodeList.stream()
                            .map(code -> "name-" + code)
                            .collect(Collectors.toSet());

                    Map<String, String> multiLanguageMap = configImageAd.getMultiLanguageExtensionField();

                    if (multiLanguageMap != null && !multiLanguageMap.isEmpty()) {
                        for (Map.Entry<String, String> entry : multiLanguageMap.entrySet()) {

                            if (!validKeySet.contains(entry.getKey())) {
                                errors.rejectValue("configImageAdList[" + i + "].multiLanguageExtensionField[" + entry.getKey()+"]",
                                        "errors.common", new String[]{"当前语言不存在"},"");return;
                            }

                            if (entry.getValue() != null && entry.getValue().strip().length() > 10) {
                                errors.rejectValue("configImageAdList[" + i + "].multiLanguageExtensionField[" + entry.getKey()+"]",
                                        "errors.common", new String[]{"不能大于190个字符"},"");
                            }
                        }
                    }
                }
            }
        }
        //在线帮助分页
        if(frontendApiForm.getConfigHelpPage() != null){
            if(frontendApiForm.getConfigHelpPage().getMaxResult() != null && frontendApiForm.getConfigHelpPage().getMaxResult() <=0){
                errors.rejectValue("configHelpPage.maxResult","errors.common", new String[]{"每页显示记录数必须大于0"},"");
            }
            if(frontendApiForm.getConfigHelpPage().getPageCount() != null && frontendApiForm.getConfigHelpPage().getPageCount() <=0){
                errors.rejectValue("configHelpPage.pageCount","errors.common", new String[]{"页码显示总数必须大于0"},"");
            }
        }
	}
}
