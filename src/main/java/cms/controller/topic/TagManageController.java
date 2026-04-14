package cms.controller.topic;


import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.config.BusinessException;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.setting.SystemSetting;
import cms.model.topic.Tag;
import cms.repository.setting.SettingRepository;
import cms.service.topic.TagService;
import cms.validator.topic.TagValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


/**
 * 话题标签管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/tag/manage")
public class TagManageController {
    @Resource SettingRepository settingRepository;
    @Resource MessageSource messageSource;
    @Resource TagService tagService;
    @Resource TagValidator tagValidator;
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
        Map<Long,String> navigation = tagService.getTabNavigation(parentId);
        if(parentId != null && parentId >0L && navigation.isEmpty()){
            throw new BusinessException(Map.of("navigation", "父类不存在"));
        }
        returnValue.put("parentTag",tagService.getParentTag(parentId));

        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 标签  添加
     * @param tagForm 标签表单
     * @param result 存储校验信息
     * @param parentId 父Id
     * @param imagePath 图片路径
     * @param images 图片
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=add", method=RequestMethod.POST)
    public RequestResult addTag(@ModelAttribute Tag tagForm, BindingResult result, Long parentId, String imagePath,
                                MultipartFile images, HttpServletRequest request){
        Map<String, Object> errors = new HashMap<>();

        Map<String,String> multiLanguageExtensionMap = getMultiLanguageExtensionMap(errors,request);
        //数据校验
        tagValidator.validate(tagForm, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        if(imagePath != null && !imagePath.trim().isEmpty()) {
            imagePath = textFilterComponent.deleteBindURL(request, imagePath);
        }

        tagService.addTag(tagForm, parentId, multiLanguageExtensionMap,imagePath, images);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 标签   修改界面显示
     * @param tagId 标签Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.GET)
    public RequestResult editTagUI(Long tagId,
                         HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);

        Map<String,Object> returnValue = tagService.getEditTagViewModel(tagId,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 标签  修改
     * @param tagForm 标签表单
     * @param result 存储校验信息
     * @param tagId 标签Id
     * @param imagePath 图片路径
     * @param images 图片
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.POST)
    public RequestResult editTag(@ModelAttribute Tag tagForm, BindingResult result, Long tagId, String imagePath,
                                MultipartFile images, HttpServletRequest request){
        Map<String, Object> errors = new HashMap<>();

        Map<String,String> multiLanguageExtensionMap = getMultiLanguageExtensionMap(errors,request);
        //数据校验
        tagValidator.validate(tagForm, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        if(imagePath != null && !imagePath.trim().isEmpty()) {
            imagePath = textFilterComponent.deleteBindURL(request, imagePath);
        }
        tagForm.setId(tagId);
        tagService.editTag(tagForm, multiLanguageExtensionMap,imagePath, images);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 标签   删除
     * @param tagId 标签Id
     * @return
     */
    @RequestMapping(params="method=delete", method=RequestMethod.POST)
    public RequestResult deleteTag(Long tagId){
        tagService.deleteTag(tagId);
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
        List<Tag> tagList = tagService.getAllTag();
        return new RequestResult(ResultCode.SUCCESS, tagList);
    }
}