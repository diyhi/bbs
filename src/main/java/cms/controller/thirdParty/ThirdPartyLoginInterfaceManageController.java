package cms.controller.thirdParty;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.sms.SmsInterfaceRequest;
import cms.dto.thirdParty.ThirdPartyLoginInterfaceRequest;
import cms.service.sms.SmsInterfaceService;
import cms.service.thirdParty.ThirdPartyLoginInterfaceService;
import cms.validator.sms.SmsInterfaceValidator;
import cms.validator.thirdParty.ThirdPartyLoginInterfaceValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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
 * 第三方登录接口管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/thirdPartyLoginInterface/manage")
public class ThirdPartyLoginInterfaceManageController {
    @Resource
    ThirdPartyLoginInterfaceService thirdPartyLoginInterfaceService;
    @Resource
    MessageSource messageSource;
    @Resource
    ThirdPartyLoginInterfaceValidator thirdPartyLoginInterfaceValidator;
    /**
     * 第三方登录接口 添加界面显示
     */
    @RequestMapping(params="method=add",method= RequestMethod.GET)
    public RequestResult addUI(){
        Map<String,Object> returnValue = thirdPartyLoginInterfaceService.getAddThirdPartyLoginInterfaceViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 第三方登录接口 添加
     * @param thirdPartyLoginInterfaceRequest 第三方登录接口表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute ThirdPartyLoginInterfaceRequest thirdPartyLoginInterfaceRequest, BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        //数据校验
        thirdPartyLoginInterfaceValidator.validate(thirdPartyLoginInterfaceRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        thirdPartyLoginInterfaceService.addThirdPartyLoginInterface(thirdPartyLoginInterfaceRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 第三方登录接口 显示修改
     * @param thirdPartyLoginInterfaceId 第三方登录接口Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Integer thirdPartyLoginInterfaceId) {
        Map<String,Object> returnValue = thirdPartyLoginInterfaceService.getEditThirdPartyLoginInterfaceViewModel(thirdPartyLoginInterfaceId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 第三方登录接口 修改
     * @param thirdPartyLoginInterfaceRequest 第三方登录接口表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute ThirdPartyLoginInterfaceRequest thirdPartyLoginInterfaceRequest, BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        //数据校验
        thirdPartyLoginInterfaceValidator.validate(thirdPartyLoginInterfaceRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        thirdPartyLoginInterfaceService.editThirdPartyLoginInterface(thirdPartyLoginInterfaceRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 第三方登录接口 删除
     * @param thirdPartyLoginInterfaceId 第三方登录接口Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Integer thirdPartyLoginInterfaceId) {
        thirdPartyLoginInterfaceService.deleteThirdPartyLoginInterface(thirdPartyLoginInterfaceId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }



}
