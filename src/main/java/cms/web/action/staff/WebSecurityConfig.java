package cms.web.action.staff;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.util.UriUtils;

import cms.web.filter.CsrfSecurityRequestMatcher;

/**
 * 认证服务
 * @author Gao
 *
 */
@Configuration
@EnableWebSecurity
//@Order(1)//默认order=100 order的值越小，类的优先级越高，IOC容器就会优先加载，默认的优先级 资源服务器配置ResourceServerConfig（3） >  WebSecurityConfig（100）   如果配置优先级是：认证服务器配置WebSecurityConfig（1）> 资源服务器配置ResourceServerConfig（100） 这样，WebSecurityConfig中的configure就会执行，而ResourceServerConfig中的configure不生效。当然，其他的一些重写方法也是可以在两者中都会生效的
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired 
	private CustomUserDetailsService customUserDetailsService;

	/**
	 * 密码加密使用bcrypt方式
	 * @return
	 */
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	/**
     * 设置不需要拦截的静态资源
     * @param web
     * @throws Exception
     
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/backstage/**","/common/**","/file/**");
    }*/
	/**
     * 设置不需要拦截的静态资源
     * web.ignoring()完全绕过spring security的所有filter
     * permitAll，会给没有登录的用户适配一个AnonymousAuthenticationToken，设置到SecurityContextHolder，方便后面的filter可以统一处理authentication
     * 不配置此项前端某些js文件会请求两次，图片延迟加载插件的图片会请求两次
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) {
     //   web.ignoring().antMatchers("/**/*.jpg","/**/*.jpeg","/**/*.png","/**/*.gif","/**/*.bmp","/**/*.css","/**/*.js");
    
    	 web.ignoring().antMatchers("/backstage/**","/common/**","/file/**");
    	/**web.ignoring()
    	 .antMatchers("/backstage/**")
         .antMatchers("/common/**")
         .antMatchers("/file/**");**/
    }
    

	
	/**
     * 配置权限(ResourceServerConfig.java的优先级(@Order(3))比本配置高，它存在时本配置(@Order(100))不会生效)
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	
    	//access(String) 如果给定的SpEL表达式计算结果为true，就允许访问
    	//anonymous() 允许匿名用户访问
    	//authenticated() 允许认证的用户进行访问
    	//denyAll() 无条件拒绝所有访问
    	//fullyAuthenticated() 如果用户是完整认证的话（不是通过Remember-me功能认证的），就允许访问
    	//hasAuthority(String) 如果用户具备给定权限的话就允许访问
    	//hasAnyAuthority(String…)如果用户具备给定权限中的某一个的话，就允许访问
    	//hasRole(String) 如果用户具备给定角色(用户组)的话,就允许访问/
    	//hasAnyRole(String…) 如果用户具有给定角色(用户组)中的一个的话,允许访问.
    	//hasIpAddress(String 如果请求来自给定ip地址的话,就允许访问.
    	//not() 对其他访问结果求反.
    	//permitAll() 无条件允许访问
    	//rememberMe() 如果用户是通过Remember-me功能认证的，就允许访问
    	
  
    	//配置受保护的资源
    	http.authorizeRequests()//资源服务器和认证服务器分离情况下需要配置
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
    		.csrf().requireCsrfProtectionMatcher(csrfSecurityRequestMatcher())//要使用csrf保护的请求匹配器
    		.csrfTokenRepository(new CookieCsrfTokenRepository());//将CSRF令牌存储在自定义Cookie中 CookieServerCsrfTokenRepository
    	
    	//http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//让 Spring Security 不创建和使用 session
    	

    	
    	//添加自定义异常入口
    	http.exceptionHandling()
        	.authenticationEntryPoint(authenticationEntryPoint())//匿名用户访问无权限资源时异常
        	.accessDeniedHandler(accessDeniedHandler()); //认证过的用户访问无权限资源时异常

    }
    

    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }

    
    /**
     * 认证管理 配置支持password模式
     * @return
     * @throws Exception
     */
    //@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    	
    }
    
    /**
     * 过滤掉不需要CSRF保护的URL
     * @return
     */
    @Bean
    public CsrfSecurityRequestMatcher csrfSecurityRequestMatcher(){
    	return new CsrfSecurityRequestMatcher();
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
  					if(StringUtils.startsWith(authException.getMessage(), "Invalid access token")){//判断开始部分是否与二参数相同
  						message = "无效的访问令牌"+StringUtils.removeStart(authException.getMessage(), "Invalid access token");
  					}
  			
  					//向Header头写入信息
  					response.setHeader("message", UriUtils.encode(message,"UTF-8"));//不支持中文参数   用UriUtils.encode代替URLEncoder.encode,解决空格转换成+号的问题
  					response.sendError(401,//返回401错误
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
}
