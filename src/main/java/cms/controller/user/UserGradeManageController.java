package cms.controller.user;


import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.user.UserGrade;
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
 * 用户等级管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/userGrade/manage")
public class UserGradeManageController {

    @Resource
    UserGradeService userGradeService;
    @Resource FileComponent fileComponent;
    @Resource
    UserGradeValidator userGradeValidator;
    @Resource
    MessageSource messageSource;

    /**
     * 用户等级管理 添加界面显示
     */
    @RequestMapping(params="method=add",method=RequestMethod.GET)
    public RequestResult addUI() {
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 用户等级管理 添加
     * @param userGradeForm 用户等级表单
     * @param result      存储校验信息
     * @return
     */
    @RequestMapping(params="method=add", method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute UserGrade userGradeForm, BindingResult result) {
        //数据校验
        userGradeValidator.validate(userGradeForm, result);
        if (result.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        userGradeService.addUserGrade(userGradeForm);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户等级 修改界面显示
     * @param id 用户等级Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Integer id) {
        UserGrade userGrade = userGradeService.getEditUserGradeViewModel(id);
        return new RequestResult(ResultCode.SUCCESS, userGrade);
    }

    /**
     * 用户等级管理 修改
     * @param userGradeForm 用户等级表单
     * @param result      存储校验信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute UserGrade userGradeForm, BindingResult result) {
        //数据校验
        userGradeValidator.validate(userGradeForm, result);
        if (result.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        userGradeService.editUserGrade(userGradeForm);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户等级管理 删除
     * @param id 用户等级Id
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Integer id) {
        userGradeService.deleteUserGrade(id);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
}
