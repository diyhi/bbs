package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.service.frontend.FeedbackClientService;
import cms.utils.IpAddress;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台在线留言控制器
 */
@RestController("frontendFeedbackController")
public class FeedbackController {
    @Resource
    FeedbackClientService feedbackClientService;
    /**
     * 在线留言  -- 添加页
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6080100)
    public Map<String,Object> addFeedbackUI(){
        return feedbackClientService.getAddFeedbackViewModel();
    }

    /**
     * 在线留言   添加
     * @param name 名称
     * @param contact 联系方式
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1080100)
    @RequestMapping(value="/feedback/control/add", method= RequestMethod.POST)
    public Map<String,Object> addFeedback(String name, String contact, String content,
                                          String captchaKey, String captchaValue, HttpServletRequest request) {
        String ip = IpAddress.getClientIpAddress(request);
        return feedbackClientService.addFeedback(name, contact, content,ip,
                captchaKey,captchaValue);
    }
}
