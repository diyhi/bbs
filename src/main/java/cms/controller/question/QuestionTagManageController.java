package cms.controller.question;


import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.config.BusinessException;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.question.QuestionTag;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import cms.service.question.QuestionTagService;
import cms.validator.question.QuestionTagValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 问答标签管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/questionTag/manage")
public class QuestionTagManageController {
    @Resource SettingRepository settingRepository;
    @Resource MessageSource messageSource;
    @Resource QuestionTagService questionTagService;
    @Resource QuestionTagValidator questionTagValidator;
    @Resource TextFilterComponent textFilterComponent;
    @Resource JsonComponent jsonComponent;
    @Resource FileComponent fileComponent;


    /**
     * 标签   添加界面显示
     * @param parentId 父Id
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.GET)
    public RequestResult addTagUI(Long parentId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<Long,String> navigation = questionTagService.getTabNavigation(parentId);
        if(parentId != null && parentId >0L && navigation.isEmpty()){
            throw new BusinessException(Map.of("navigation", "父类不存在"));
        }

        returnValue.put("navigation", navigation);//标签导航

        QuestionTag parentTag = questionTagService.getParentTag(parentId);
        if(parentTag != null){
            returnValue.put("parentTag",parentTag);
        }

        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 标签  添加
     * @param questionTagForm 标签表单
     * @param result 存储校验信息
     * @param parentId 父Id
     * @param imagePath 图片路径
     * @param images 图片
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=add", method=RequestMethod.POST)
    public RequestResult addTag(@ModelAttribute QuestionTag questionTagForm, BindingResult result, Long parentId, String imagePath,
                                MultipartFile images, HttpServletRequest request){
        Map<String, Object> errors = new HashMap<>();

        Map<String,String> multiLanguageExtensionMap = getMultiLanguageExtensionMap(errors,request);
        //数据校验
        questionTagValidator.validate(questionTagForm, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        if(imagePath != null && !imagePath.trim().isEmpty()) {
            imagePath = textFilterComponent.deleteBindURL(request, imagePath);
        }

        questionTagService.addTag(questionTagForm, parentId, multiLanguageExtensionMap,imagePath, images);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 标签   修改界面显示
     * @param questionTagId 标签Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.GET)
    public RequestResult editTagUI(Long questionTagId,
                         HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);

        Map<String,Object> returnValue = questionTagService.getEditTagViewModel(questionTagId,fileServerAddress);

        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 标签  修改
     * @param questionTagForm 标签表单
     * @param result 存储校验信息
     * @param questionTagId 标签Id
     * @param imagePath 图片路径
     * @param images 图片
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.POST)
    public RequestResult editTag(@ModelAttribute QuestionTag questionTagForm, BindingResult result, Long questionTagId, String imagePath,
                                MultipartFile images, HttpServletRequest request){
        Map<String, Object> errors = new HashMap<>();

        Map<String,String> multiLanguageExtensionMap = getMultiLanguageExtensionMap(errors,request);
        //数据校验
        questionTagValidator.validate(questionTagForm, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        if(imagePath != null && !imagePath.trim().isEmpty()) {
            imagePath = textFilterComponent.deleteBindURL(request, imagePath);
        }
        questionTagForm.setId(questionTagId);
        questionTagService.editTag(questionTagForm, multiLanguageExtensionMap,imagePath, images);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 标签   删除
     * @param questionTagId 标签Id
     * @return
     */
    @RequestMapping(params="method=delete", method=RequestMethod.POST)
    public RequestResult deleteTag(Long questionTagId){
        questionTagService.deleteTag(questionTagId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 获取多语言扩展字段值
     * @param errors 错误信息
     * @param request 请求信息
     * @return
     */
    private Map<String,String> getMultiLanguageExtensionMap(Map<String, Object> errors,HttpServletRequest request){
        //多语言扩展  key:字段-语言（例如：name-en_US） value:内容
        Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();


        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
            List<String> languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
            if(languageFormExtensionCodeList != null && !languageFormExtensionCodeList.isEmpty()){
                for(String languageFormExtensionCode : languageFormExtensionCodeList){

                    String name = request.getParameter("name-"+languageFormExtensionCode);
                    if(name != null && !name.trim().isEmpty()){
                        if(name.length() >190){
                            errors.put("name-"+languageFormExtensionCode, "不能大于190个字符");
                        }else{
                            multiLanguageExtensionMap.put("name-"+languageFormExtensionCode, name);
                        }
                    }

                }
            }
        }
        return multiLanguageExtensionMap;
    }


    /**
     * 标签 查询所有标签
     * @return
     */
    @RequestMapping(params="method=allTag", method=RequestMethod.GET)
    public RequestResult queryAllTag(){
        List<QuestionTag> tagList = questionTagService.getAllTag();
        return new RequestResult(ResultCode.SUCCESS, tagList);
    }

    /**
     * 标签 查询标签分页
     * @param pageForm 页码
     * @param parentId 父Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=questionTagPage", method=RequestMethod.GET)
    public RequestResult queryQuestionTagPage(PageForm pageForm, Long parentId, HttpServletRequest request){
        Map<String,Object> returnValue = new LinkedHashMap<>();
        returnValue.put("navigation", questionTagService.getTabNavigation(parentId));
        String fileServerAddress = fileComponent.fileServerAddress(request);
        returnValue.put("pageView", questionTagService.getTagList(parentId,fileServerAddress,pageForm.getPage()));
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


}