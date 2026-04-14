package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.annotation.RoleAnnotation;
import cms.dto.user.ResourceEnum;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台AI助手控制器
 */
@RestController
public class AiAssistantController {

    /**
     * AI助手表单
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6150400)
    public Map<String,Object> aiAssistantUI(){
        return null;
    }


    /**
     * AI助手
     * @param prompt 提示词
     * @param interfaceProduct 接口产品
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @return
     */
    @RoleAnnotation(resourceCode= ResourceEnum._7001000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1150300)
    @RequestMapping(value="/user/control/aiAssistant", method= RequestMethod.POST)
    public Map<String,Object> aiAssistant(String prompt, Integer interfaceProduct,
                              String captchaKey, String captchaValue){
        return null;
    }
}
