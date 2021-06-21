package cms.web.action.staff;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import cms.utils.CommentedProperties;

/**
 * 授权服务
 * @author Gao
 * 当前Spring Security OAuth2 的API已显示过时，是因为Spring官方在停止旧的oauth2支持，并在开发新的oauth2授权服务器。
 * 但新的oauth2授权服务器目前为止还在实验性阶段,到本功能开发时只更新到0.1.0版本，所以并没有升级到spring-authorization-server
 * 
 * 过时通知 https://spring.io/blog/2019/11/14/spring-security-oauth-2-0-roadmap-update
 * 新的oauth2授权服务器 https://github.com/spring-projects-experimental/spring-authorization-server
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
	@Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
	
	@Autowired
    public UserDetailsService customUserDetailsService;

    @Autowired
    private TokenStore tokenStore;
	

	@Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()//使用内存存储
        		.withClient("bbs")//客户端Id client_id 
                    .secret(passwordEncoder.encode((String)CommentedProperties.readOAuth2().get("secret")))//客户端密钥  client_secret 字段不能直接是 secret 的原始值，需要经过加密。因为是用的 BCryptPasswordEncoder，所以最终插入的值应该是经过 BCryptPasswordEncoder.encode()之后的值。
                    .accessTokenValiditySeconds((Integer)CommentedProperties.readOAuth2().get("accessTokenValiditySeconds"))//access_token 的有效时长 (秒), 默认 12 小时  
                    .refreshTokenValiditySeconds((Integer)CommentedProperties.readOAuth2().get("refreshTokenValiditySeconds"))//刷新token的有效时长 (秒), 默认 30 天
                    .scopes("all")// .scopes("read", "write")//客户端访问范围，默认为空则拥有全部范围  "all", "read", "write"  用来限制客户端访问的权限，在换取的 token 的时候会带上 scope 参数，只有在 scopes 定义内的，才可以正常换取 token。
                    .authorizedGrantTypes("password", "refresh_token")//授权模式为密码模式   authorization_code：授权码类型。implicit：隐式授权类型。password：资源所有者（即用户）密码类型。client_credentials：客户端凭据（客户端ID以及Key）类型。refresh_token：通过以上授权获得的刷新令牌来获取新的令牌。
        .and()
        .build();
    }

	
	
	
	
	
	@Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		String urlPrefix = "/admin";
		
		endpoints 
		.authenticationManager(authenticationManager)//指定认证管理器。调用此方法才能支持 password 模式
        .userDetailsService(customUserDetailsService)//用户账号密码认证
        .tokenStore(tokenStore)//指定token存储位置

        //.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)//请求方式
        //.tokenEnhancer(tokenEnhancer)// 自定义生成令牌
        //.reuseRefreshTokens(false)// 是否重复使用 refresh_token 默认为true  1.重复使用：access_token过期刷新时， refresh token过期时间未改变，仍以初次生成的时间为准  2.非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新而无需失效再次登录
        //.exceptionTranslator(new CustomWebResponseExceptionTranslator())// 自定义异常处理
        
        //pathMapping用来配置端点URL链接，有两个参数，都将以 "/" 字符为开始的字符串。 第一个参数：这个端点URL的默认链接 第二个参数：你要进行替代的URL链接
		.pathMapping("/oauth/authorize", urlPrefix+"/oauth/authorize")//POST 授权码模式认证授权接口  授权端点。----对应的类：AuthorizationEndpoint.java
        .pathMapping("/oauth/token", urlPrefix+"/oauth/token")//GET/POST  获取 token 的接口  对应的类：TokenEndpoint.java
        .pathMapping("/oauth/confirm_access", urlPrefix+"/oauth/confirm_access") //用户确认授权提交端点。----对应的类：WhitelabelApprovalEndpoint.java
        .pathMapping("/oauth/error", urlPrefix+"/oauth/error") //授权服务错误信息端点。
        .pathMapping("/oauth/check_token", urlPrefix+"/oauth/check_token")//POST 检查 token 合法性接口
        .pathMapping("/oauth/token_key", urlPrefix+"/oauth/token_key");//提供公有密匙的端点，如果你使用JWT令牌的话。

		
		/**
		// 增加自定义授权方式，这里可以增加新的认证方式，只要自定义TokenGranter即可
        endpoints.tokenGranter(
            new CompositeTokenGranter(
                Arrays.asList(endpoints.getTokenGranter(), customTokenGranter(endpoints))
            )
        );**/
		/**
		MyResourceOwnerPasswordTokenGranter myResourceOwnerPasswordTokenGranter = new MyResourceOwnerPasswordTokenGranter(authenticationManager,endpoints.getTokenServices(), endpoints.getClientDetailsService(),
	    endpoints.getOAuth2RequestFactory());
	    endpoints.tokenGranter(myResourceOwnerPasswordTokenGranter);
		**/
		
	//	endpoints.exceptionTranslator(webResponseExceptionTranslator());
		
		
		
		/**
		DefaultTokenServices tokenServices  = new DefaultTokenServices();
		tokenServices .setClientDetailsService(clientDetailsService());
		//tokenServices .setAccessTokenValiditySeconds(1);
		//tokenServices .setRefreshTokenValiditySeconds(1);//ClientDetails中设置了过期时间，这里的无效
		tokenServices .setSupportRefreshToken(true);
		tokenServices .setTokenStore(endpoints.getTokenStore());
		tokenServices .setTokenEnhancer(endpoints.getTokenEnhancer());
		endpoints.tokenServices(tokenServices );
		**/
	
	}


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
      //  security.tokenKeyAccess("permitAll()")
      //          .checkTokenAccess("isAuthenticated()");

         
        security.allowFormAuthenticationForClients();//允许客户端访问 OAuth2 授权接口，否则请求 token 会返回 401。
        security.checkTokenAccess("isAuthenticated()");//允许已授权用户访问 checkToken 接口。
        security.tokenKeyAccess("isAuthenticated()");//允许已授权用户获取 token 接口。
   //     security.tokenKeyAccess("permitAll()");
        //POST /oauth/authorize  授权码模式认证授权接口
        //GET/POST /oauth/token  获取 token 的接口
        //POST  /oauth/check_token  检查 token 合法性接口
        
        
        //http://localhost:8080/admin/oauth/token?username=user&password=123&grant_type=password&client_id=client_password&client_secret=secret
        //refresh_token示例：http://localhost:8080/admin/oauth/token?grant_type=refresh_token&refresh_token=xxx&client_id=client_password&client_secret=secret
            
   
        
    }

    /**
     * 指定token存储位置
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }
}
