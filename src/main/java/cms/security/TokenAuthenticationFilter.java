package cms.security;

import cms.component.admin.AdminCacheManager;
import cms.component.staff.StaffCacheManager;
import cms.component.staff.StaffComponent;
import cms.dto.admin.AdminAccessToken;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * Token过滤器 拦截所有受保护的请求并验证 Access Token
 */
@Component
public class TokenAuthenticationFilter  extends OncePerRequestFilter {
    @Resource AdminCacheManager adminCacheManager;
    @Resource StaffCacheManager staffCacheManager;
    @Resource UserDetailsService userDetailsService;

    //指定的URL下工作
    private final PathPatternRequestMatcher[] filterMatchers = {
            PathPatternRequestMatcher.withDefaults().matcher("/control/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/admin/logout")
    };




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 只有当请求匹配指定的URL时，才执行认证逻辑
        for (RequestMatcher rm : filterMatchers) {
            if (rm.matches(request)) {
                String accessToken = request.getHeader("Authorization");

                if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                    // 验证失败，设置 HTTP 状态码为 401 Unauthorized（未授权）
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    sendErrorResponse(response, response.getStatus(),"请求头Authorization字段不能为空");
                    return;
                }

                String token = accessToken.substring(7);

                if(token.isEmpty()){
                    // 验证失败，设置 HTTP 状态码为 401 Unauthorized（未授权）
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    sendErrorResponse(response, response.getStatus(),"accessToken不能为空");
                    return;
                }


                AdminAccessToken adminAccessToken = adminCacheManager.getAdminAccessTokenByAccessToken(token);
                if(adminAccessToken == null){
                    // 验证失败，设置 HTTP 状态码为 401 Unauthorized（未授权）
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    sendErrorResponse(response, response.getStatus(),"accessToken已过期");
                    return;
                }

                String securityDigest = staffCacheManager.query_staffSecurityDigest(adminAccessToken.getUserAccount());


                if (!Objects.equals(adminAccessToken.getSecurityDigest(), securityDigest)) {
                    // 验证失败，设置 HTTP 状态码为 401 Unauthorized（未授权）
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    sendErrorResponse(response, response.getStatus(),"安全摘要已改变");
                    return;
                }


                UserDetails userDetails = userDetailsService.loadUserByUsername(adminAccessToken.getUserAccount());
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // 已经通过令牌认证，这里不需要凭据了
                        userDetails.getAuthorities()
                );
                //将 Authentication 对象设置到 SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }
        filterChain.doFilter(request, response);
    }


    // 封装一个独立的私有方法来处理错误响应
    private void sendErrorResponse(HttpServletResponse response, Integer status,String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("{\"status\": " + status + ",\"error\": \"" + message + "\"}");
        writer.flush();
        writer.close();

    }
}
