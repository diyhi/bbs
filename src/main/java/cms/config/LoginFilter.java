package cms.config;

import cms.component.OAuthComponent;
import cms.component.frontendModule.FrontendApiComponent;
import cms.component.user.UserCacheManager;
import cms.dto.user.AccessUser;
import cms.dto.user.UserState;
import cms.utils.AccessUserThreadLocal;
import cms.utils.Base64;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;

/**
 * 检查登录用户跳转
 */
@WebFilter(urlPatterns ="/*", filterName ="loginFilter",asyncSupported = true)
public class LoginFilter extends OncePerRequestFilter {
    @Resource Environment environment;
    @Resource
    OAuthComponent oAuthComponent;
    @Resource
    UserCacheManager userCacheManager;
    @Resource
    FrontendApiComponent frontendApiComponent;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        //处理跨域请求
        String allowedOrigins = environment.getProperty("bbs.allowedOrigins", String.class, "");


        if(!allowedOrigins.trim().isEmpty()){
            String[] origin = allowedOrigins.split(",");

            Set<String> allowedOriginSet= new HashSet<String>(Arrays.asList(origin));
            String originHeader = request.getHeader("Origin");
            if (allowedOriginSet.contains(originHeader)){
                response.setHeader("Access-Control-Allow-Origin", originHeader);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE");
                //  response.setHeader("Access-Control-Max-Age", "86400");
                response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Cache-Control,Accept,Authorization,BBS-XSRF-TOKEN,Set-Cookie");//表示访问请求中允许携带哪些Header信息  Cookie,token

                //暴露哪些头部信息(因为跨域访问默认不能获取全部头部信息)
                // response.setHeader("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION);


                // 如果是OPTIONS则结束请求
                if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
                    response.setStatus(HttpStatus.NO_CONTENT.value());
                    return;
                }

            }
        }


        //身份校验逻辑
        boolean isJump = checkAuthentication(request);

        //判断是否需要拦截
        boolean isAuthRequired = frontendApiComponent.isAuthRequired(request);

        if (isAuthRequired && isJump) {
            handleUnauthorizedRedirect(request, response);
            return;
        }

        filterChain.doFilter(request, response);

    }


    /**
     * 校验身份：返回 true 表示需要跳转（验证失败），返回 false 表示验证通过
     * @param request 请求信息
     * @return
     */
    private boolean checkAuthentication(HttpServletRequest request) {
        AccessUser accessUser = oAuthComponent.getUserName(request);
        if (accessUser != null) {

            UserState userState = userCacheManager.query_userState(accessUser.getUserName().trim());
            if (userState != null && userState.getState() == 1  // 如果是正常用户   1:正常用户
                    && userState.getSecurityDigest().equals(accessUser.getSecurityDigest())) {//如果安全摘要没有改变
                AccessUserThreadLocal.set(accessUser);
                return false;
            }
        }
        return true;
    }

    /**
     * 处理无授权跳转
     */
    private void handleUnauthorizedRedirect(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        //获取URI后面的参数
        if (StringUtils.isNotBlank(request.getQueryString())) {
            uri += "?" + request.getQueryString();
        }

        String jumpUrl = "";
        if ("POST".equals(request.getMethod())) {
            String referer = request.getHeader("referer");
            if (StringUtils.isNotBlank(referer)) {
                uri = Strings.CI.removeStart(referer, WebUtil.getUrl(request));//移除开始部分的相同的字符,不区分大小写
            }
        } else {
            jumpUrl = request.getParameter("jumpUrl");
            if (StringUtils.isBlank(jumpUrl)) {
                String referer = request.getHeader("referer");
                if (StringUtils.isNotBlank(referer)) {
                    String refUri = Strings.CI.removeStart(referer, WebUtil.getUrl(request));//移除开始部分的相同的字符,不区分大小写
                    jumpUrl = extractJumpUrlFromQuery(refUri);
                }
            }
        }

        if (StringUtils.isBlank(jumpUrl) && StringUtils.isNotBlank(uri)) {
            jumpUrl = Base64.encodeBase64URL(uri);//Base64安全编码
        }

        String loginRedirectPath = "login?jumpUrl=" + jumpUrl;

        //设置登录页面响应http头。用来激活Ajax请求处理方式 Session超时后的跳转
        response.setHeader("jumpPath", loginRedirectPath);

        //如果在登录页面使用Ajax请求/user/开头的URL,出现死循环
        if (loginRedirectPath.equals(uri)) {
            response.setStatus(508);
            return;
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value()); //401前台访问无授权页面
    }


    /**
     * 获取跳转参数
     */
    private String extractJumpUrlFromQuery(String uri) {
        if (StringUtils.isBlank(uri) || !uri.contains("?")) return null;
        return UriComponentsBuilder.fromUriString(uri)
                .build()
                .getQueryParams()
                .getFirst("jumpUrl");
    }

}
