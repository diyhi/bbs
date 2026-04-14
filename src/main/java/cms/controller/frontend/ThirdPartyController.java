package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.dto.thirdParty.WeiXinOpenId;
import cms.model.thirdParty.SupportLoginInterface;
import cms.service.frontend.ThirdPartyClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 前台第三方服务控制器
 */
@RestController
public class ThirdPartyController {
    @Resource
    ThirdPartyClientService thirdPartyClientService;

    /**
     * 第三方登录
     * @param request 请求信息
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6150300)
    public List<SupportLoginInterface> thirdPartyLoginUI(HttpServletRequest request) {
        return thirdPartyClientService.getThirdPartyLogin(request);
    }

    /**
     * 查询微信openid
     * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1170100)
    @RequestMapping(value="/thirdParty/queryWeiXinOpenId",method = RequestMethod.POST)
    public WeiXinOpenId queryWeiXinOpenId(String code){
        return thirdPartyClientService.getWeiXinOpenId(code);
    }

    /**
     * 第三方登录链接
     * @param interfaceProduct 接口产品
     * @param jumpUrl 重定向参数
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1170200)
    @RequestMapping(value="/thirdParty/loginLink",method = RequestMethod.GET)
    public Map<String,Object> loginLink(Integer interfaceProduct, String jumpUrl,
                                        HttpServletRequest request){
        return thirdPartyClientService.getLoginLink(interfaceProduct,jumpUrl, request);
    }

    /**
     * 第三方登录重定向
     * @param code 微信公众号code
     * @param state 自定义参数   微信公众号state 存放csrf令牌
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1170300)
    @RequestMapping("/thirdParty/loginRedirect")
    public Map<String,Object> loginRedirect(String code,String state,
                                HttpServletRequest request, HttpServletResponse response) {
        return thirdPartyClientService.getLoginRedirect(code,state,request, response);
    }
}
