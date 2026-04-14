package cms.controller.user;


import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.user.UserCustomRequest;
import cms.model.user.UserGrade;
import cms.service.user.UserCustomService;
import cms.service.user.UserGradeService;
import cms.validator.user.UserGradeValidator;
import jakarta.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * 用户自定义注册功能项控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/userCustom/manage")
public class UserCustomManageController {

    @Resource
    UserCustomService userCustomService;
    @Resource FileComponent fileComponent;
    @Resource MessageSource messageSource;


    /**
     * 用户自定义注册功能项管理 添加界面显示
     */
    @RequestMapping(params="method=add",method=RequestMethod.GET)
    public RequestResult addUI() {
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 用户自定义注册功能项管理 添加
     * @param userCustomRequest 用户自定义注册功能项表单
     * @param result      存储校验信息
     * @return
     */
    @RequestMapping(params="method=add", method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute UserCustomRequest userCustomRequest, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        userCustomService.addUserCustom(userCustomRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户自定义注册功能项管理 修改界面显示
     * @param id 用户等级Id
     * @return
    */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Integer id) {
        Map<String,Object> returnValue = userCustomService.getEditUserCustomViewModel(id);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 用户自定义注册功能项管理 修改
     * @param userCustomRequest 用户自定义注册功能项表单
     * @param result      存储校验信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute UserCustomRequest userCustomRequest, BindingResult result) {
        //数据校验
        if (result.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        userCustomService.editUserCustom(userCustomRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户自定义注册功能项管理 删除
     * @param id 用户等级Id
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Integer id) {
        userCustomService.deleteUserCustom(id);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
}
