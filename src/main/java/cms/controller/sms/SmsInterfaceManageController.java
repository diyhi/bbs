package cms.controller.sms;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.sms.SmsInterfaceRequest;
import cms.service.sms.SmsInterfaceService;
import cms.validator.sms.SmsInterfaceValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 短信接口管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/smsInterface/manage")
public class SmsInterfaceManageController {
    @Resource
    SmsInterfaceService smsInterfaceService;
    @Resource
    MessageSource messageSource;
    @Resource
    SmsInterfaceValidator smsInterfaceValidator;
    /**
     * 短信接口 添加界面显示
     */
    @RequestMapping(params="method=add",method= RequestMethod.GET)
    public RequestResult addUI(){
        Map<String,Object> returnValue = smsInterfaceService.getAddSmsInterfaceViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 短信接口 添加
     * @param smsInterfaceRequest 短信接口表单
     * @param result 存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute SmsInterfaceRequest smsInterfaceRequest, BindingResult result,
                             HttpServletRequest request) {
        Map<String, Object> errors = new HashMap<>();
        //数据校验
        smsInterfaceValidator.validate(smsInterfaceRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        smsInterfaceService.addSmsInterface(smsInterfaceRequest,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 短信接口 显示修改
     * @param smsInterfaceId 短信接口Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Integer smsInterfaceId) {
        Map<String,Object> returnValue = smsInterfaceService.getEditSmsInterfaceViewModel(smsInterfaceId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 短信接口 修改
     * @param smsInterfaceRequest 短信接口表单
     * @param result 存储校验信息
     * @param request 请求信息
     * @return
    */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute SmsInterfaceRequest smsInterfaceRequest, BindingResult result,
                              HttpServletRequest request) {
        Map<String, Object> errors = new HashMap<>();
        //数据校验
        smsInterfaceValidator.validate(smsInterfaceRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        smsInterfaceService.editSmsInterface(smsInterfaceRequest,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 短信接口 删除
     * @param smsInterfaceId 短信接口Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Integer smsInterfaceId) {
        smsInterfaceService.deleteSmsInterface(smsInterfaceId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }



}
