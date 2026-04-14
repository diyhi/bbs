package cms.controller.payment;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.payment.OnlinePaymentInterfaceRequest;
import cms.model.payment.OnlinePaymentInterface;
import cms.service.payment.OnlinePaymentInterfaceService;
import cms.validator.payment.OnlinePaymentInterfaceValidator;
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
 * 在线支付接口管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/onlinePaymentInterface/manage")
public class OnlinePaymentInterfaceManageController {
    @Resource
    OnlinePaymentInterfaceService onlinePaymentInterfaceService;
    @Resource
    MessageSource messageSource;
    @Resource
    OnlinePaymentInterfaceValidator onlinePaymentInterfaceValidator;
    /**
     * 在线支付接口 添加界面显示
     */
    @RequestMapping(params="method=add",method= RequestMethod.GET)
    public RequestResult addUI(){
        Map<String,Object> returnValue = onlinePaymentInterfaceService.getAddOnlinePaymentInterfaceViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 在线支付接口 添加
     * @param onlinePaymentInterfaceRequest 在线支付接口表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute OnlinePaymentInterfaceRequest onlinePaymentInterfaceRequest, BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        //数据校验
        onlinePaymentInterfaceValidator.validate(onlinePaymentInterfaceRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        onlinePaymentInterfaceService.addOnlinePaymentInterface(onlinePaymentInterfaceRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 在线支付接口 显示修改
     * @param onlinePaymentInterfaceId 在线支付接口Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Integer onlinePaymentInterfaceId) {
        Map<String,Object> returnValue = onlinePaymentInterfaceService.getEditOnlinePaymentInterfaceViewModel(onlinePaymentInterfaceId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 在线支付接口 修改
     * @param onlinePaymentInterfaceRequest 在线支付接口表单
     * @param onlinePaymentInterfaceId 在线支付接口Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute OnlinePaymentInterfaceRequest onlinePaymentInterfaceRequest, BindingResult result,Integer onlinePaymentInterfaceId) {
        Map<String, Object> errors = new HashMap<>();
        onlinePaymentInterfaceRequest.setOnlinePaymentInterfaceId(onlinePaymentInterfaceId);
        //数据校验
        onlinePaymentInterfaceValidator.validate(onlinePaymentInterfaceRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        onlinePaymentInterfaceService.editOnlinePaymentInterface(onlinePaymentInterfaceRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 在线支付接口 删除
     * @param onlinePaymentInterfaceId 在线支付接口Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Integer onlinePaymentInterfaceId) {
        onlinePaymentInterfaceService.deleteOnlinePaymentInterface(onlinePaymentInterfaceId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
}
