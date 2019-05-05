package cms.web.action.staff;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cms.bean.setting.SystemSetting;
import cms.service.setting.SettingService;
import cms.utils.WebUtil;
import cms.web.action.common.CaptchaManage;
import cms.web.action.setting.SettingManage;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.TextEscapeUtils;
import org.springframework.util.Assert;


/**
 * 员工登录验证
 * @author Administrator
 *
 */
public class MyUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
 
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "j_username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "j_password";
    public static final String SPRING_SECURITY_FORM_TOKEN_KEY = "j_token";
    public static final String SPRING_SECURITY_FORM_CAPTCHAKEY_KEY = "j_captchaKey";
    public static final String SPRING_SECURITY_FORM_CAPTCHAVALUE_KEY = "j_captchaValue";
    public static final String SPRING_SECURITY_LAST_USERNAME_KEY = "SPRING_SECURITY_LAST_USERNAME";
   
    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
    private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
    private String tokenParameter = SPRING_SECURITY_FORM_TOKEN_KEY;
    private String captchaKeyParameter = SPRING_SECURITY_FORM_CAPTCHAKEY_KEY;
    private String captchaValueParameter = SPRING_SECURITY_FORM_CAPTCHAVALUE_KEY;
    private boolean postOnly = true;
    
   

    @Resource StaffManage staffManage;//通过接口引用代理返回的对象
    @Resource CaptchaManage captchaManage;
    @Resource SettingService settingService;
    @Resource SettingManage settingManage;
    

    // Constructors ===================================================================================================
    //登录时提交的地址
    public MyUsernamePasswordAuthenticationFilter() {
    	super("/admin/loginVerification");
    }

    // Methods ========================================================================================================

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String username = obtainUsername(request);
        String password = obtainPassword(request);
       
        String token = obtainToken(request);
        String captchaKey = obtainCaptchaKey(request);
        String captchaValue = obtainCaptchaValue(request);
        
        if (username == null || "".equals(username.trim())) {
        	
            throw new AuthenticationServiceException(messages.getMessage("LdapAuthenticationProvider.emptyUsername"));
            
        }
        
        if (password == null || "".equals(password.trim())) {
        	throw new AuthenticationServiceException(messages.getMessage("LdapAuthenticationProvider.emptyPassword"));
        }else{
        	if(password.trim().length() != 64){
        		throw new AuthenticationServiceException(messages.getMessage("LdapAuthenticationProvider.passwordLength"));
        	}
        }
  
        
        
     // Place the last username attempted into HttpSession for views
        HttpSession session = request.getSession(false);
       
        if (session != null || getAllowSessionCreation()) {
        	//用户名回显
            request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextEscapeUtils.escapeEntities(username.trim()));
        }
        
       
        
      //是否需要验证码  true:要  false:不要
  		boolean isCaptcha = false;
  		SystemSetting systemSetting = settingService.findSystemSetting_cache();
  		if(systemSetting.getLogin_submitQuantity() <=0){//每分钟连续登录密码错误N次时出现验证码
  			isCaptcha = true;
  		}else{

  			String staffName = WebUtil.getCookieByName(request.getCookies(),"staffName");
  			if(staffName != null && !"".equals(staffName.trim())){
  				Integer errorCount = staffManage.getLoginFailureCount(staffName.trim());//先查询错误次数
  	  			
  				if(errorCount != null && errorCount >= systemSetting.getLogin_submitQuantity()){
  					isCaptcha = true;
  				}
  			}
  			
  		}
  		
  		
  		if(isCaptcha){
  			if (captchaKey == null || "".equals(captchaKey.trim())) {
  	        	throw new AuthenticationServiceException(messages.getMessage("emptyCaptchaKey"));
  	        }
  	        if (captchaValue == null || "".equals(captchaValue.trim())) {
  	        	throw new AuthenticationServiceException(messages.getMessage("emptyCaptchaValue"));
  	        }
  			
  	        //增加验证码重试次数
			Integer original = settingManage.getSubmitQuantity("captcha", captchaKey.trim());
    		if(original != null){
    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),original+1);//刷新每分钟原来提交次数
    		}else{
    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),1);//刷新每分钟原来提交次数
    		}
  			
  			String _captcha = captchaManage.captcha_generate(captchaKey.trim(),"");
  	        if(_captcha != null && !"".equals(_captcha.trim())){
  	        	if(!_captcha.equalsIgnoreCase(captchaValue.trim())){
  	        		throw new AuthenticationServiceException(messages.getMessage("captchaError"));
  	        	}
  			}else{
  				throw new AuthenticationServiceException(messages.getMessage("captchaError"));
  			}
  			//删除验证码
  			captchaManage.captcha_delete(captchaKey);
  		}
  		
  		

		//判断令牌
		if(token != null && !"".equals(token)){	
			String cookie_token = WebUtil.getCookieByName(request, "XSRF-TOKEN");
			if(!token.equals(cookie_token)){
				throw new AuthenticationServiceException(messages.getMessage("tokenError"));//令牌错误
			}
		}else{
			throw new AuthenticationServiceException(messages.getMessage("emptyToken"));//缺少token参数
		}
        
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username.trim(), password.trim());

        
        

        // 允许子类设置详细属性   
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
    
   
	
    /**
     * Enables subclasses to override the composition of the password, such as by including additional values
     * and a separator.<p>This might be used for example if a postcode/zipcode was required in addition to the
     * password. A delimiter such as a pipe (|) should be used to separate the password and extended value(s). The
     * <code>AuthenticationDao</code> will need to generate the expected password in a corresponding manner.</p>
     *
     * @param request 请求对象
     *
     * @return the password that will be presented in the <code>Authentication</code> request token to the
     *         <code>AuthenticationManager</code>
     */
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(passwordParameter);
    }

    /**
     * 使子类可以重写用户名的组成，例如包括附加值和分隔符。
     * @param request 请求对象
     * @return the username that will be presented in the <code>Authentication</code> request token to the
     *         <code>AuthenticationManager</code>
     */
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }
    protected String obtainToken(HttpServletRequest request) {
        return request.getParameter(tokenParameter);
    }
    
    
    /**
     * 使子类可以重写验证码键
     * @param request 请求对象
     * @return the username that will be presented in the <code>Authentication</code> request token to the
     *         <code>AuthenticationManager</code>
     */
    protected String obtainCaptchaKey(HttpServletRequest request) {
        return request.getParameter(captchaKeyParameter);
    }
    /**
     * 使子类可以重写验证码值
     * @param request 请求对象
     * @return the username that will be presented in the <code>Authentication</code> request token to the
     *         <code>AuthenticationManager</code>
     */
    protected String obtainCaptchaValue(HttpServletRequest request) {
        return request.getParameter(captchaValueParameter);
    }

    /**
     * 所提供的子类可以放置将其配置为在认证请求的详细属性
     * @param request 正在创建一个身份验证请求
     * @param authRequest 详细设置的身份验证请求对象
     */
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * 设置从登录request中获得账号参数
     * @param usernameParameter 参数名称。默认为 "j_username".
     */
    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "账号不能为空");
        this.usernameParameter = usernameParameter;
    }

    /**
     * 设置从登录request中获得密码参数
     * @param passwordParameter 参数名称。默认为 "j_password".
     */
    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "密码不能为空");
        this.passwordParameter = passwordParameter;
    }
    public void setTokenParameter(String tokenParameter) {
        Assert.hasText(tokenParameter, "令牌不能为空");
        this.tokenParameter = tokenParameter;
    }
    /**
     * 设置从登录request中获得验证码键参数
     * @param captchaKeyParameter 参数名称。默认为 "j_captchaKey".
     */
    public void setCaptchaKeyParameter(String captchaKeyParameter) {
    	Assert.hasText(captchaValueParameter, "验证码键不能为空");
		this.captchaKeyParameter = captchaKeyParameter;
	}
    /**
     * 设置从登录request中获得验证码值参数
     * @param captchaValueParameter 参数名称。默认为 "j_captchaValue".
     */
    public void setCaptchaValueParameter(String captchaValueParameter) {
    	Assert.hasText(captchaValueParameter, "验证码值不能为空");
		this.captchaValueParameter = captchaValueParameter;
	}

    /**
     * 定义是否只有HTTP POST请求将由这个过滤器允许.
     * 如果设置为true，收到验证请求时，将抛出一个异常.并不会尝试验证
     * 如果处理身份验证失败。unsuccessfulAuthentication()方法将被调用
     * 默认值为true 但可由子类重写。
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return usernameParameter;
    }

    public final String getPasswordParameter() {
        return passwordParameter;
    }

	public String getCaptchaValueParameter() {
		return captchaValueParameter;
	}

	public String getCaptchaKeyParameter() {
		return captchaKeyParameter;
	}

	


}
