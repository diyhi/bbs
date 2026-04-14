package cms.service.frontend;


import cms.dto.thirdParty.WeiXinOpenId;
import cms.model.thirdParty.SupportLoginInterface;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * 前台第三方登录服务接口
 */
public interface ThirdPartyClientService {

    /**
     * 获取第三方登录接口
     * @param request 请求信息
     * @return
     */
    public List<SupportLoginInterface> getThirdPartyLogin(HttpServletRequest request);


    /**
     * 获取微信openid
     * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     * @return
     */
    public WeiXinOpenId getWeiXinOpenId(String code);

    /**
     * 获取第三方登录链接
     * @param interfaceProduct 接口产品
     * @param jumpUrl 重定向参数
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getLoginLink(Integer interfaceProduct,String jumpUrl, HttpServletRequest request);
    /**
     * 获取第三方登录重定向
     * @param code 微信公众号code
     * @param state 自定义参数   微信公众号state 存放csrf令牌
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> getLoginRedirect(String code,String state,
                                               HttpServletRequest request, HttpServletResponse response);
}
