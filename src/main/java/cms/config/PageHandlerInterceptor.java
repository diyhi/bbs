package cms.config;

import cms.annotation.RoleAnnotation;
import cms.component.user.UserRoleComponent;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import cms.utils.IpAddress;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import cms.utils.WebUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;


/**
 * 页面拦截器
 *
 */
@Component
public class PageHandlerInterceptor implements HandlerInterceptor{

    @Resource
    UserRoleComponent userRoleComponent;
    @Resource
    SettingRepository settingRepository;
	
	//?  匹配任何单字符
	//*  匹配0或者任意数量的字符
	//** 匹配0或者更多的目录
	//后台URL
    private final PathPatternRequestMatcher[] backstage_filterMatchers = {
            PathPatternRequestMatcher.withDefaults().matcher("/control/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/admin/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/captcha/**"),//生成验证码
            PathPatternRequestMatcher.withDefaults().matcher("/checkCaptcha"),//校验验证码
            PathPatternRequestMatcher.withDefaults().matcher("/error"),//spring默认的错误页
    };




    /**
	 * preHandle()方法在业务处理器处理请求之前被调用 
	 */
	  
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, 
			Object handler) throws Exception { 
		//System.out.println(request.getRequestURI()+" -- "+request.getQueryString()+" -- "+request.getMethod());



		//是否为后台URL
		boolean backstage_flag = false;
		//后台URL
		for (RequestMatcher rm : backstage_filterMatchers) {
			if (rm.matches(request)) { 
				backstage_flag = true;
			}
		}
		
		
		//拦截用户角色处理 注解参考： @RoleAnnotation(resourceCode=ResourceEnum._2001000)
		if(handler instanceof HandlerMethod){
			HandlerMethod  handlerMethod= (HandlerMethod) handler;
	        Method method=handlerMethod.getMethod();
	        RoleAnnotation roleAnnotation = method.getAnnotation(RoleAnnotation.class);
	        if(roleAnnotation != null){
	        	boolean flag = userRoleComponent.checkPermission(roleAnnotation.resourceCode(),null);
	        	if(!flag){
	        		 return false;
	        	}
	        }
		}


		SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
		
		if(systemSetting != null && systemSetting.getCloseSite().equals(3)){//3.全站关闭
			
			if(!backstage_flag){
				String baseURI = WebUtil.baseURI(request.getRequestURI(), request.getContextPath());
				//删除后缀
				baseURI = StringUtils.substringBeforeLast(baseURI, ".");
				if(!baseURI.equalsIgnoreCase("message")){
					response.sendRedirect(WebUtil.getUrl(request)+"message");
					return false;
				}
			}
		}
		return true;   
	} 
	 /**
	  * postHandle()方法在业务处理器处理请求之后被调用
	  */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, 
			 Object handler, ModelAndView modelAndView)throws Exception {  
		

		request.setAttribute("baseURL", WebUtil.getUrl(request));//系统路径
		

	}   
	/**
	 *  afterCompletion()方法在DispatcherServlet完全处理完请求后被调用
     */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse
			 response, Object handler, Exception ex)throws Exception {

	}
}
