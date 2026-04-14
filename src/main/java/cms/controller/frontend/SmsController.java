package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.dto.frontendModule.SmsCodeDTO;
import cms.service.frontend.SmsClientService;
import jakarta.annotation.Resource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台短信控制器
 */
@RestController
public class SmsController {
    @Resource
    SmsClientService smsClientService;

    /**
     * 获取短信验证码
     * @param smsCodeDTO 短信验证码表单
     * @param result 存储校验信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1150900)
    @RequestMapping(value="/smsCode", method= RequestMethod.POST)
    public Map<String,Object> smsCode(@ModelAttribute SmsCodeDTO smsCodeDTO, BindingResult result) {
        return smsClientService.getSmsCode(smsCodeDTO);
    }


    /**
     * 获取绑定短信验证码
     * @param smsCodeDTO 短信验证码表单
     * @param result 存储校验信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1151000)
    @RequestMapping(value="/user/control/getSmsCode",method=RequestMethod.POST)
    public Map<String,Object> getBindingSmsCode(@ModelAttribute SmsCodeDTO smsCodeDTO, BindingResult result){
        return smsClientService.getBindingSmsCode(smsCodeDTO);
    }
}
