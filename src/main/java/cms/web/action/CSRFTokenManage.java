package cms.web.action;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import cms.bean.ErrorView;
import cms.bean.template.Templates;
import cms.service.template.TemplateService;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import cms.utils.threadLocal.CSRFTokenThreadLocal;


/**
 * CSRF令牌管理
 *
 */
@Component("csrfTokenManage")
public class CSRFTokenManage {
	@Resource TemplateService templateService;
	
	/**
	 * 设置令牌
	 * @param request
	 * @param response
	 */
	public void setToken(HttpServletRequest request,HttpServletResponse response){
		String token = this.getToken(request);
		if(token == null || "".equals(token.trim())){
			String new_token = UUIDUtil.getUUID32();
			//将令牌添加到Cookie
			//将 CSRF cookie 指定为 HttpOnly 并不能提供任何实际的保护，因为 CSRF 只是为了防止跨域攻击。如果攻击者可以通过 JavaScript 读取 cookie，则浏览器认为他们已经在同一个域上了
			WebUtil.addCookie(response, "cms_token",new_token , 0,false);
			CSRFTokenThreadLocal.set(new_token);
		}
	}
	
	/**
	 * 获取令牌
	 * @param request
	 * @return
	 */
	public String getToken(HttpServletRequest request){
		//获取token
		return WebUtil.getCookieByName(request, "cms_token");
	}
	/**
	 * 删除令牌
	 * @param response
	 * @return
	 */
	public void deleteToken(HttpServletResponse response){
		WebUtil.deleteCookie(response, "cms_token");
	}
	
	/**
	 * 处理CSRF令牌
	 * @param request
	 * @param token
	 * @param error
	 */
    public void processCsrfToken(HttpServletRequest request, String token,Map<String,String> error) {
    	Templates template = templateService.findTemplatebyDirName_cache(templateService.findTemplateDir_cache());
    	//是否验证CSRF
    	boolean verifyCSRF = template.getVerifyCSRF();
    	
    	
    	if(verifyCSRF){
    		String xsrf_token = request.getHeader("BBS-XSRF-TOKEN");
    		if(xsrf_token != null && !"".equals(xsrf_token.trim())){
    			token = xsrf_token.trim();
    		}
        	
        	//判断令牌
    		if(token != null && !"".equals(token.trim())){	
    			String token_sessionid = this.getToken(request);//获取令牌
    			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
    				if(!token_sessionid.equals(token)){
    					error.put("token", ErrorView._13.name());//令牌错误
    				}
    			}else{
    				error.put("token", ErrorView._12.name());//令牌过期
    			}
    		}else{
    			error.put("token", ErrorView._11.name());//令牌为空
    		}
    	}else{
    		if(!request.getMethod().equals("GET")){
    			//判断令牌
        		if(token != null && !"".equals(token.trim())){	
        			error.put("token", ErrorView._24.name());//令牌检测未开启
        		}
    		}
    	}
    }
}
