package cms.web.action.staff;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import cms.utils.WebUtil;
import cms.web.taglib.Configuration;

/**
 * 未登录时切入点
 * 实现 org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint接口
 *
 */
public class MyLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint{
	
	public MyLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
	}
	/**
	 * 执行重定向（或转发）到登录URL
	 */
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
        if (isAjax) {//ajax请求
        	String url = Configuration.getUrl(request)+"admin/login"+Configuration.getSuffix();
    		response.setHeader("login", url);//设置登录页面响应http头。用来激活Ajax请求处理方式 Session超时后的跳转
    		response.sendError(403, "禁止访问");
        }else {//普通请求
        	super.commence(request,response,authException);
        } 
		
		
		
	}
}
