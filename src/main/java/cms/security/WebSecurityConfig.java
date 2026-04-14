package cms.security;

import cms.repository.staff.ACLRepository;
import jakarta.annotation.Resource;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.util.*;

/**
 * 认证服务
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private static final Logger logger = LogManager.getLogger(WebSecurityConfig.class);

    @Resource ACLRepository aclRepository;
    @Resource TokenAuthenticationFilter tokenAuthenticationFilter;
    @Resource Environment environment;

    @Resource CustomAuthorizationManager customAuthorizationManager;


    private final PathPatternRequestMatcher[] filterMatchers = {
            //PathPatternRequestMatcher.withDefaults().matcher("/backstage/**"),
            //PathPatternRequestMatcher.withDefaults().matcher("/common/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/file/**")
    };




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationEntryPoint authenticationEntryPoint,AccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 关闭 CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 禁用 Session
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // 允许同源iframe加载
                )
                /**
                .authorizeHttpRequests(authorize -> authorize
                        .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                            @Override
                            public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                                // 注入自定义的 AccessDecisionManager
                                //fsi.setAccessDecisionManager(customAccessDecisionManager());
                                // 注入自定义的 SecurityMetadataSource
                                fsi.setSecurityMetadataSource(customSecurityMetadataSource());
                                return fsi;
                            }
                        })
                        //配置受保护的资源
                        .requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/control/**")).authenticated()// 管理后台接口
                        .requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/admin/logout")).authenticated()// // 管理员退出接口
                        // 其他所有路径都允许访问
                        .anyRequest().permitAll()
                )**/
                /**
                .authorizeHttpRequests(authorize -> authorize
                        // 使用自定义的 AuthorizationManager 替代 .anyRequest().authenticated()
                        .anyRequest(customAuthorizationManager)
                )**/
                .authorizeHttpRequests(authorize -> authorize
                                // 确保由 StreamingResponseBody 触发的 ASYNC 调度跳过第二次授权检查。
                                .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                                //配置受保护的资源
                                .requestMatchers("/control/**").access(customAuthorizationManager)
                                .requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/admin/logout")).authenticated()// // 管理员退出接口

                                // 其他所有路径都允许访问
                                .anyRequest().permitAll()

                        )

                //禁用页面缓存标头 Cache-Control: no-cache
                //spring security 默认会有禁止缓存标头Cache-Control: no-cache，不配置此项前端某些图片延迟加载插件的图片会重复请求两次
                .headers(headers -> headers.addHeaderWriter(new HeaderWriter() {
                    private final CacheControlHeadersWriter originalWriter = new CacheControlHeadersWriter();

                    @Override
                    public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
                        for (RequestMatcher rm : filterMatchers) {
                            if (rm.matches(request)) {
                                //默认
                                //Cache-Control: no-cache, no-store, max-age=0, must-revalidate
                                //Pragma: no-cache
                                //Expires: 0
                                // 清除页面缓存头
                                response.setHeader("Cache-Control", "");
                                response.setHeader("Pragma", "");
                                response.setHeader("Expires", "");
                                return;
                            }
                        }
                        originalWriter.writeHeaders(request, response);
                    }
                }))

                .logout(logout -> logout
                        .logoutUrl("/admin/logout") // 自定义退出 URL
                        .disable()//关闭默认退出入口(不能删除，删除会和前台/logout冲突)
                )

                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 在UsernamePasswordAuthenticationFilter之前添加token过滤器

                //添加自定义异常入口
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint) // 匿名用户访问无权限资源时异常
                        .accessDeniedHandler(accessDeniedHandler) // 认证过的用户访问无权限资源时异常
                );

        String allowedOrigins = environment.getProperty("bbs.allowedOrigins", String.class, "");

        if(!allowedOrigins.trim().isEmpty()){
            http.cors(cors -> cors.configurationSource(corsConfigurationSource())); // 启用 CORS 并配置源
        }

        return http.build();
    }

    /**
     * 密码加密使用bcrypt方式
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 处理认证请求
     * @param authenticationConfiguration 认证配置
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 匿名用户访问无权限资源时异常
     * @return
     */
    @Bean
    @Primary
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                if (!response.isCommitted()) {// 如果响应未已经提交；提交的响应未写入状态码和报头
                    String message = authException.getMessage();
                    if("Full authentication is required to access this resource".equals(authException.getMessage())){
                        message = "需要登录用户才能访问此资源";
                    }
                    if(Strings.CS.startsWith(authException.getMessage(), "Invalid access token")){//判断开始部分是否与二参数相同
                        message = "无效的访问令牌"+Strings.CS.removeStart(authException.getMessage(), "Invalid access token");
                    }

                    //向Header头写入信息
                    response.setHeader("message", UriUtils.encode(message,"UTF-8"));//不支持中文参数   用UriUtils.encode代替URLEncoder.encode,解决空格转换成+号的问题

                    response.sendError(401,//返回401错误  HttpServletResponse.SC_UNAUTHORIZED
                            authException.getMessage());//错误信息

                }
            }

        };
    }




    /**
     * 认证过的用户访问无权限资源时异常
     * 参考org.springframework.security.web.access.AccessDeniedHandlerImpl.java
     * @return 返回403状态
     */
    @Bean
    @Primary
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler(){

            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
                    throws IOException, ServletException {
                if (!response.isCommitted()) {
                    //向Header头写入信息
                    response.setHeader("message", UriUtils.encode(accessDeniedException.getMessage(),"UTF-8"));//不支持中文参数   用UriUtils.encode代替URLEncoder.encode,解决空格转换成+号的问题
                    response.sendError(HttpStatus.FORBIDDEN.value(),//返回403错误
                            HttpStatus.FORBIDDEN.getReasonPhrase());//错误信息

                }

            }

        };
    }

    /**
     * 跨域配置
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        String allowedOrigins = environment.getProperty("bbs.allowedOrigins", String.class, "");

        if(!allowedOrigins.trim().isEmpty()){
            String[] origin = allowedOrigins.split(",");
            CorsConfiguration configuration = new CorsConfiguration();
            //开放哪些ip、端口、域名的访问权限，星号表示开放所有域
            configuration.setAllowedOrigins(Arrays.asList(origin));
            //是否允许发送Cookie信息
            configuration.setAllowCredentials(true);
            //开放哪些Http方法，允许跨域访问
            configuration.setAllowedMethods(Arrays.asList("GET","HEAD","POST","PUT","PATCH","DELETE"));
            //允许HTTP请求中的携带哪些Header信息
            configuration.setAllowedHeaders(Arrays.asList("Origin","X-Requested-With","Content-Type","Cache-Control","Accept","Authorization","BBS-XSRF-TOKEN","Set-Cookie"));
            //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
            //configuration.addExposedHeader("*");
            configuration.addExposedHeader("Message");//Message为cms.web.action.staff.WebSecurityConfig.java的accessDeniedHandler方法返回的消息头。 response.addHeader("Access-Control-Expose-Headers", "Message");

            //预检请求的缓存时间（秒），即在这段间内对于相同的跨域请求不会再次预检了
            configuration.setMaxAge(18000L);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**",configuration);//添加映射路径
            return source;
        }

        return null;
    }

}
