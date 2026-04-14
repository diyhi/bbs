package cms.controller.help;


import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.help.HelpType;
import cms.service.help.HelpTypeService;
import cms.validator.help.HelpTypeValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


/**
 * 帮助分类管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/helpType/manage")
public class HelpTypeManageController {
    @Resource MessageSource messageSource;
    @Resource HelpTypeService helpTypeService;
    @Resource HelpTypeValidator helpTypeValidator;
    @Resource TextFilterComponent textFilterComponent;
    @Resource FileComponent fileComponent;


    /**
     * 帮助分类   添加界面显示
     * @param parentId 父Id
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.GET)
    public RequestResult addHelpTypeUI(Long parentId){
        Map<String,Object> returnValue = helpTypeService.getAddHelpTypeViewModel(parentId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 帮助分类  添加
     * @param helpTypeForm 帮助分类表单
     * @param result 存储校验信息
     * @param parentId 父Id
     * @param imagePath 图片路径
     * @param images 图片
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=add", method=RequestMethod.POST)
    public RequestResult addHelpType(@ModelAttribute HelpType helpTypeForm, BindingResult result, Long parentId, String imagePath,
                                MultipartFile images, HttpServletRequest request){
        Map<String, Object> errors = new HashMap<>();

        //数据校验
        helpTypeValidator.validate(helpTypeForm, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        if(imagePath != null && !imagePath.trim().isEmpty()) {
            imagePath = textFilterComponent.deleteBindURL(request, imagePath);
        }

        helpTypeService.addHelpType(helpTypeForm, parentId, imagePath, images);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 帮助分类  修改界面显示
     * @param typeId 帮助分类Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.GET)
    public RequestResult editHelpTypeUI(Long typeId,
                         HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);

        Map<String,Object> returnValue = helpTypeService.getEditHelpTypeViewModel(typeId,fileServerAddress);


        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 帮助分类  修改
     * @param helpTypeForm 分类表单
     * @param result 存储校验信息
     * @param typeId 分类Id
     * @param imagePath 图片路径
     * @param images 图片
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.POST)
    public RequestResult editHelpType(@ModelAttribute HelpType helpTypeForm, BindingResult result, Long typeId, String imagePath,
                                MultipartFile images, HttpServletRequest request){
        Map<String, Object> errors = new HashMap<>();

        //数据校验
        helpTypeValidator.validate(helpTypeForm, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        if(imagePath != null && !imagePath.trim().isEmpty()) {
            imagePath = textFilterComponent.deleteBindURL(request, imagePath);
        }
        helpTypeForm.setId(typeId);
        helpTypeService.editHelpType(helpTypeForm, imagePath, images);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 帮助分类   删除
     * @param typeId 帮助分类Id
     * @return
     */
    @RequestMapping(params="method=delete", method=RequestMethod.POST)
    public RequestResult deleteHelpType(Long typeId){
        helpTypeService.deleteHelpType(typeId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 帮助分类   合并界面显示
     * @param typeId 帮助分类Id
     * @return
     */
    @RequestMapping(params="method=merger", method=RequestMethod.GET)
    public RequestResult mergerHelpTypeUI(Long typeId){
        Map<String,Object> returnValue = helpTypeService.getMergerHelpTypeViewModel(typeId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 帮助分类   合并
     * @param typeId 主分类Id
     * @param mergerTypeId 合并分类Id
     */
    @RequestMapping(params="method=merger", method=RequestMethod.POST)
    public RequestResult mergerHelpType(Long typeId,Long mergerTypeId){
        helpTypeService.mergerHelpType(typeId,mergerTypeId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 帮助分类管理 分类选择分页显示
     * @param pageForm 页码
     * @param parentId 父Id
     */
    @RequestMapping(params="method=helpTypePageSelect", method=RequestMethod.GET)
    public RequestResult helpTypePageSelect(PageForm pageForm,Long parentId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        PageView<HelpType> pageView = helpTypeService.getHelpTypePage(parentId,pageForm.getPage());
        returnValue.put("pageView",pageView);

        Map<Long,String> navigation = helpTypeService.getHelpTypeNavigation(parentId);
        returnValue.put("navigation",navigation);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 帮助分类管理 分类选择分页显示(移动功能使用)
     * @param pageForm 页码
     * @param parentId 父Id
     */
    @RequestMapping(params="method=helpTypePageSelect_move", method=RequestMethod.GET)
    public RequestResult productTypePageSelect_move(PageForm pageForm,Long parentId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        PageView<HelpType> pageView = helpTypeService.getHelpTypePage(parentId,pageForm.getPage());
        returnValue.put("pageView",pageView);

        Map<Long,String> navigation = helpTypeService.getHelpTypeNavigation(parentId);
        returnValue.put("navigation",navigation);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }
}