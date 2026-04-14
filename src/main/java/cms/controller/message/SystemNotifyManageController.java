package cms.controller.message;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.message.SystemNotifyRequest;
import cms.model.message.SystemNotify;
import cms.service.message.SystemNotifyService;
import cms.validator.message.SystemNotifyValidator;
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
 * 系统通知管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/systemNotify/manage")
public class SystemNotifyManageController {
    @Resource
    SystemNotifyService systemNotifyService;
    @Resource
    MessageSource messageSource;
    @Resource
    SystemNotifyValidator systemNotifyValidator;
    /**
     * 系统通知 添加界面显示
     */
    @RequestMapping(params="method=add",method= RequestMethod.GET)
    public RequestResult addUI(){
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 系统通知 添加
     * @param systemNotifyRequest 系统通知表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute SystemNotifyRequest systemNotifyRequest, BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        //数据校验
        systemNotifyValidator.validate(systemNotifyRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        systemNotifyService.addSystemNotify(systemNotifyRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 系统通知 显示修改
     * @param systemNotifyId 系统通知Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Long systemNotifyId) {
        SystemNotify systemNotify = systemNotifyService.getEditSystemNotifyViewModel(systemNotifyId);
        return new RequestResult(ResultCode.SUCCESS, systemNotify);
    }

    /**
     * 系统通知 修改
     * @param systemNotifyRequest 系统通知表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute SystemNotifyRequest systemNotifyRequest, BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        //数据校验
        systemNotifyValidator.validate(systemNotifyRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        systemNotifyService.editSystemNotify(systemNotifyRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 系统通知 删除
     * @param systemNotifyId 系统通知Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Long systemNotifyId) {
        systemNotifyService.deleteSystemNotify(systemNotifyId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 订阅系统通知列表
     * @param id 用户Id
     * @param request 请求信息
     */
    @RequestMapping(params="method=subscriptionSystemNotifyList",method=RequestMethod.GET)
    public RequestResult subscriptionSystemNotifyList(PageForm pageForm,Long id, HttpServletRequest request){
        Map<String,Object> returnValue= systemNotifyService.getSubscriptionSystemNotifyList(pageForm.getPage(),id,request);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 还原订阅系统通知
     * @param userId 用户Id
     * @param subscriptionSystemNotifyId 订阅系统通知Id
     * @return
     */
    @RequestMapping(params="method=reductionSubscriptionSystemNotify", method=RequestMethod.POST)
    public RequestResult reductionSubscriptionSystemNotify(Long userId,String subscriptionSystemNotifyId) {
        systemNotifyService.reductionSubscriptionSystemNotify(userId,subscriptionSystemNotifyId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

}
