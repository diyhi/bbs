package cms.web.action.staff;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.service.staff.StaffService;
import cms.utils.WebUtil;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * 登录失败后处理
 */
public class MySimpleUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	@Resource StaffService staffService;//通过接口引用代理返回的对象
    @Resource StaffManage staffManage;

	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
		
    	String name= (String)request.getSession().getAttribute("SPRING_SECURITY_LAST_USERNAME");
    	if(name != null && !"".equals(name.trim())){
    		WebUtil.addCookie(response, "cms_staffName", name, 120);
    		Integer original = staffManage.getLoginFailureCount(name);//原来总次数
    		if(original != null){
    			staffManage.addLoginFailureCount(name,original+1);
    		}else{
    			staffManage.addLoginFailureCount(name,1);
    		}
    	}
    	super.onAuthenticationFailure(request, response, exception);
    }
	
	
}
