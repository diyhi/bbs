package cms.web.action.staff;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import cms.web.filter.CsrfSecurityRequestMatcher;

/**
 * 资源服务
 * @author Gao
 *
 */
@Configuration
@EnableResourceServer
//@Order(100)//默认order=3
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{
	@Autowired
	private TokenStore tokenStore;
	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;
	@Autowired
	private CsrfSecurityRequestMatcher csrfSecurityRequestMatcher;
	@Autowired
	private AuthenticationEntryPoint authenticationEntryPoint;
	@Autowired
	private AccessDeniedHandler accessDeniedHandler;
	
	@Autowired
	private CustomAccessDecisionManager customAccessDecisionManager;
	
	@Autowired
	private CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;
	
	
	/**
     * 这里设置需要token验证的url
     * 这些url可以在WebSecurityConfigurerAdapter中排除掉，
     * 对于相同的url，如果二者都配置了验证
     * 则优先进入ResourceServerConfigurerAdapter,进行token验证。而不会进入
     * WebSecurityConfigurerAdapter 的 basic auth或表单认证。
     */
	@Override
    public void configure(HttpSecurity http) throws Exception {
        //配置受保护的资源
        http.authorizeRequests()
			.antMatchers("/control/**").authenticated() //允许认证的用户进行访问
			.antMatchers("/admin/logout").authenticated(); //允许认证的用户进行访问

        http
	        // 关闭httpBasic
	        .httpBasic().disable();
        http
 		.logout().logoutUrl("/admin/logout").disable();//关闭默认退出入口(不能删除，删除会和前台/logout冲突)
	
      
        http
        	.headers().frameOptions().sameOrigin();//允许加载本站点内的页面
        
        http
        	.csrf().requireCsrfProtectionMatcher(csrfSecurityRequestMatcher)//要使用csrf保护的请求匹配器
        	.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());//将CSRF令牌存储在自定义Cookie中 CookieServerCsrfTokenRepository   不允许JS读取可以设置new CookieCsrfTokenRepository()

        http.addFilterAfter(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
        
    }
	@Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(tokenService());

        resources
        	.authenticationEntryPoint(authenticationEntryPoint)//匿名用户访问无权限资源时异常  认证异常流程处理返回
	        .accessDeniedHandler(accessDeniedHandler) //认证过的用户访问无权限资源时异常
	        .tokenExtractor(myBearerTokenExtractor());//token获取方式,默认BearerTokenExtractor 从header获取token为空则从request.getParameter("access_token")

        
    }
	
	/**
     * 资源服务令牌解析服务,调用远程服务解析
     
	@Bean
    public RemoteTokenServices tokenService() {
        RemoteTokenServices tokenServices = new RemoteTokenServices();
        //设置授权服务器check_token端点完整地址
        tokenServices.setCheckTokenEndpointUrl("http://127.0.0.1:8080/cms/admin/oauth/check_token");
        //设置客户端id与secret，注意：client_secret值不能使用passwordEncoder加密！
        tokenServices.setClientId("bbs");
        tokenServices.setClientSecret("secret");
        return tokenServices; 
    }*/
	
	/**
     * 资源服务器令牌解析服务，资源和认证在一起，不用调用远程服务解析
     *
     * @return 
     */
    @Bean
    @Primary
    public ResourceServerTokenServices tokenService() {
        DefaultTokenServices services = new DefaultTokenServices();

        services.setTokenStore(tokenStore);// 必须设置
        services.setAuthenticationManager(authenticationManager);
        return services;
    }
	
	/**
	 * 令牌提取器
	 * @return
	 */
	@Bean
    public MyBearerTokenExtractor myBearerTokenExtractor() {
		return new MyBearerTokenExtractor();
	}

	
	/**
     * API权限控制
     * 过滤器优先度在 FilterSecurityInterceptor 之后
     * spring-security 的默认过滤器列表见 https://docs.spring.io/spring-security/site/docs/5.0.0.M1/reference/htmlsingle/#ns-custom-filters
     *
     * @return
     */
    private CustomFilterSecurityInterceptor customFilterSecurityInterceptor() {
    	CustomFilterSecurityInterceptor interceptor = new CustomFilterSecurityInterceptor();
        interceptor.setAuthenticationManager(authenticationManager);
        interceptor.setAccessDecisionManager(customAccessDecisionManager);
        interceptor.setSecurityMetadataSource(customFilterInvocationSecurityMetadataSource);
        return interceptor;
    }
	
}
