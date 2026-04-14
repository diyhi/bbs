package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.dto.frontendModule.EmailCodeDTO;
import jakarta.annotation.Resource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台邮箱控制器(未开源)
 */
@RestController
public class EmailController {

    /**
     * 获取邮箱验证码
     * @param emailCodeDTO 邮箱验证码表单
     * @param result 存储校验信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1150500)
    @RequestMapping(value="/emailCode", method= RequestMethod.POST)
    public Map<String,Object> emailCode(@ModelAttribute EmailCodeDTO emailCodeDTO, BindingResult result) {
        return null;
    }
}
