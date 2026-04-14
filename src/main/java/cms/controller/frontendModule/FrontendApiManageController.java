package cms.controller.frontendModule;


import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.frontendModule.DynamicRouteGroupDTO;
import cms.dto.frontendModule.FrontendApiForm;
import cms.model.frontendModule.FrontendApi;
import cms.repository.setting.SettingRepository;
import cms.service.frontendModule.FrontendApiService;
import cms.validator.frontendModule.FrontendApiValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


/**
 * 前台API管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/frontendApi/manage")
public class FrontendApiManageController {
    @Resource FrontendApiValidator frontendApiValidator;
    @Resource MessageSource messageSource;
    @Resource FrontendApiService frontendApiService;
    @Resource FileComponent fileComponent;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;

    /**
     * 前台API 添加界面显示
     */
    @RequestMapping(params = "method=addFrontendApi", method = RequestMethod.GET)
    public RequestResult addFrontendApiUI(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        List<DynamicRouteGroupDTO> allRouteGroupDTOs = frontendApiService.getAllCustomRouteGroup();
        returnValue.put("customDynamicRouteGroupList",allRouteGroupDTOs);

        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 前台API 添加
     * @param frontendApiForm 前台API功能表单
     * @param result 存储校验信息
     * @param imageAdFile 图片广告文件
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params = "method=addFrontendApi", method = RequestMethod.POST)
    public RequestResult addFrontendApi(@ModelAttribute FrontendApiForm frontendApiForm, BindingResult result,
                                        MultipartFile[] imageAdFile, HttpServletRequest request){

        //数据校验
        frontendApiValidator.validate(frontendApiForm, result);
        if (result.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                // 替换字段名
                String newField = fieldError.getField().replace("configImageAdList", "configImageAd");

                errors.put(newField,  messageSource.getMessage(fieldError, null));

            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }

        frontendApiService.addFrontendApi(frontendApiForm,imageAdFile,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 前台API 修改界面显示
     * @param frontendApiId 前台ApiId
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editFrontendApi",method=RequestMethod.GET)
    public RequestResult editFrontendApiUI(Integer frontendApiId, HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<>();
        FrontendApi frontendApi = frontendApiService.getEditFrontendApiViewModel(frontendApiId,request);
        returnValue.put("frontendApi",frontendApi);
        List<DynamicRouteGroupDTO> allRouteGroupDTOs = frontendApiService.getAllCustomRouteGroup();
        returnValue.put("customDynamicRouteGroupList",allRouteGroupDTOs);

        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 前台API 修改
     * @param frontendApiForm 前台API表单
     * @param result 存储校验信息
     * @param frontendApiId 前台ApiId
     * @param imageAdFile 图片广告文件
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editFrontendApi", method=RequestMethod.POST)
    public RequestResult editFrontendApi(@ModelAttribute FrontendApiForm frontendApiForm,BindingResult result,
                             Integer frontendApiId,MultipartFile[] imageAdFile, HttpServletRequest request){
        //数据校验
        frontendApiValidator.validate(frontendApiForm, result);
        if (result.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                // 替换字段名
                String newField = fieldError.getField().replace("configImageAdList", "configImageAd");

                errors.put(newField,  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }

        frontendApiForm.getFrontendApi().setId(frontendApiId);
        frontendApiService.editFrontendApi(frontendApiForm,imageAdFile,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 前台API 删除
     * @param frontendApiId 前台ApiId
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=deleteFrontendApi", method=RequestMethod.POST)
    public RequestResult deleteFrontendApi(Integer frontendApiId, HttpServletRequest request){
        frontendApiService.deleteFrontendApi(frontendApiId,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 前台API 文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=upload", method=RequestMethod.POST)
    public Map<String,Object> upload(String dir,String fileName,MultipartFile file, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return frontendApiService.upload(dir,fileServerAddress,fileName,file);
    }



    /**
     * 校验URL路径
     * @param frontendApiId 前台ApiId
     * @param url URL路径
     * @param httpMethod 请求方法
     * @return
     */
    @RequestMapping(params="method=checkUrl", method=RequestMethod.GET)
    public RequestResult checkUrl(Integer frontendApiId,String url,String httpMethod){
        frontendApiService.checkUrl(frontendApiId,url,httpMethod);
        return new RequestResult(ResultCode.SUCCESS, null);
    }



}