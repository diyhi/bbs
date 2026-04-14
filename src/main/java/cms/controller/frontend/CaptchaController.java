package cms.controller.frontend;


import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.dto.CaptchaVerification;
import cms.service.frontend.CaptchaService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码
 */
@RestController
public class CaptchaController {

    @Resource
    CaptchaService captchaService;

    /**
     * 生成验证码
     * @param captchaKey 验证码Key
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1150100)
    @RequestMapping(value="/captcha/{captchaKey}", method=RequestMethod.GET)
    public ResponseEntity<byte[]> createCaptcha(@PathVariable String captchaKey){

        return captchaService.generateCaptchaImage(captchaKey);
    }

    /**
     * 校验验证码
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1150200)
    @RequestMapping(value="/checkCaptcha",method=RequestMethod.POST)
    public CaptchaVerification verifyCaptcha(String captchaKey,String captchaValue){
        return captchaService.verifyCaptcha(captchaKey, captchaValue);
    }
}
