package cms.web.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.queryString.util.MultiMap;
import org.queryString.util.UrlEncoded;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import cms.bean.user.AccessUser;
import cms.bean.user.RefreshUser;
import cms.bean.user.UserAuthorization;
import cms.bean.user.UserState;
import cms.service.user.UserService;
import cms.utils.Base64;
import cms.utils.CommentedProperties;
import cms.utils.SpringConfigTool;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.common.OAuthManage;
import cms.web.action.template.LayoutManage;
import cms.web.action.user.UserManage;
import cms.web.taglib.Configuration;

/**
 * 检查登录用户跳转
 *
 */
public class LoginFilter implements Filter {
	

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;

		//处理跨域请求
		String allowedOrigins = (String) CommentedProperties.readCrossOrigin().get("allowedOrigins");
    	if(allowedOrigins != null && !"".equals(allowedOrigins.trim())){
    		String[] origin = allowedOrigins.split(",");
    		
    		Set<String> allowedOriginSet= new HashSet<String>(Arrays.asList(origin));
    	    String originHeader = request.getHeader("Origin");
    	    if (allowedOriginSet.contains(originHeader)){
    	    	response.setHeader("Access-Control-Allow-Origin", originHeader);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE");
              //  response.setHeader("Access-Control-Max-Age", "86400");
                response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Cache-Control,Accept,Authorization,BBS-XSRF-TOKEN,Set-Cookie");//表示访问请求中允许携带哪些Header信息  Cookie,token

                //暴露哪些头部信息(因为跨域访问默认不能获取全部头部信息)
               // response.setHeader("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION);
             
                
        		// 如果是OPTIONS则结束请求
                if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
                    response.setStatus(HttpStatus.NO_CONTENT.value());
                    return;
                }
    	    	
    	    }
    	}
		
		boolean isJump = false;
		
		OAuthManage oAuthManage = (OAuthManage)SpringConfigTool.getContext().getBean("oAuthManage");
		UserManage userManage = (UserManage)SpringConfigTool.getContext().getBean("userManage");
		
		
		
		AccessUser accessUser = oAuthManage.getUserName(request);
		if(accessUser != null){
			
			UserState userState = userManage.query_userState(accessUser.getUserName().trim());//用户状态
			if(userState != null){
				if(!userState.getSecurityDigest().equals(accessUser.getSecurityDigest())){//如果安全摘要有改变
					isJump = true;
				}
				
				if(userState.getState() !=1){// 如果不是正常用户   1:正常用户
					isJump = true;
				}
			}else{
				isJump = true;
			}
			
			if(isJump == false){
				AccessUserThreadLocal.set(accessUser);
			}
		}else{
			String accessToken = WebUtil.getCookieByName(request, "cms_accessToken");
			String refreshToken = WebUtil.getCookieByName(request, "cms_refreshToken");
			
			//从Header获取
			UserAuthorization headerUserAuthorization = WebUtil.getAuthorization(request);
			if(headerUserAuthorization != null){
				accessToken = headerUserAuthorization.getAccessToken();
				refreshToken = headerUserAuthorization.getRefreshToken();
			}
			
			if(accessToken != null && !"".equals(accessToken.trim()) && refreshToken != null && !"".equals(refreshToken.trim())){

				RefreshUser refreshUser = oAuthManage.getRefreshUserByRefreshToken(refreshToken.trim());

				if(refreshUser != null){
					if("0".equals(refreshUser.getAccessToken())){//如果刷新令牌重复执行，则修改用户的安全摘要，让当前用户重新登录
						UserService userService = (UserService)SpringConfigTool.getContext().getBean("userServiceBean");
						
						userService.updateUserSecurityDigest(refreshUser.getUserName(),new Date().getTime());
						userManage.delete_userState(refreshUser.getUserName());
						isJump = true;
					}else if(accessToken.equals(refreshUser.getAccessToken())){
						//前后端一体的架构才执行令牌续期。前后端分离架构由浏览器访问cms.web.action.common.UserFormManageAction.java的refreshToken进行续期
						if(headerUserAuthorization == null){
							//令牌续期
							boolean flag = oAuthManage.tokenRenewal(refreshToken,refreshUser,UUIDUtil.getUUID32(),UUIDUtil.getUUID32(),request,response);
							
							if(!flag){//如果续期不成功
								isJump = true;
							}
						}else{
							isJump = true;
						}
					}else{
						isJump = true;
					}
					
				}else{
					isJump = true;
				}
			
			}else{
				isJump = true;
			}
		}
		
		LayoutManage layoutManage = (LayoutManage)SpringConfigTool.getContext().getBean("layoutManage");//查询需要登录验证的路径
		
		//查询需要登录验证的路径
		AntPathRequestMatcher[] filterMatchers = layoutManage.queryLoginValidationPath();
		
		
		//删除虚拟目录
		String requestURI = this.deleteContextPath(request.getContextPath(),request.getRequestURI());
		
		boolean isFilter = false;
		for (AntPathRequestMatcher rm : filterMatchers) {
			if (rm.matches(request)) { 
				isFilter = true;
				break;
			}
			if(rm.getPattern().equalsIgnoreCase("/index") 
					&& "".equals(requestURI)){//首页
				isFilter = true;
				break;
			}
		}
	
		if(isFilter){
			
			boolean isAjax = WebUtil.submitDataMode(request);
			if(isJump == true){
				
				String uri = request.getRequestURI();
				//获取URI后面的参数
				if(request.getQueryString() != null && !"".equals(request.getQueryString())){
					uri += "?"+request.getQueryString();
				}
				
				String jumpUrl = "";
				
				
				if(uri != null){
					
					if("POST".equals(request.getMethod())){
						String referer= request.getHeader("referer");
						if(referer != null && !"".equals(referer)){
							uri = StringUtils.removeStartIgnoreCase(referer,Configuration.getUrl(request));//移除开始部分的相同的字符,不区分大小写
						}
					}else{
						if(isAjax == true){//ajax方式提交
							String url_jumpUrl = request.getParameter("jumpUrl");
							
							if(url_jumpUrl != null && !"".equals(url_jumpUrl.trim())){//如果jumpUrl参数已经有值
								jumpUrl = url_jumpUrl;
								
							}else{
								String referer= request.getHeader("referer");  
								if(referer != null && !"".equals(referer)){
									uri = StringUtils.removeStartIgnoreCase(referer,Configuration.getUrl(request));//移除开始部分的相同的字符,不区分大小写
									if(uri != null && !"".equals(uri.trim())){
										//截取问号之后的字符
										String referer_queryString = StringUtils.substringAfter(uri, "?");//从左往右查到相等的字符开始，保留后边的，不包含等于的字符
										String referer_url_jumpUrl = this.getJumpUrl(referer_queryString);
										if(referer_url_jumpUrl != null && !"".equals(referer_url_jumpUrl.trim())){//如果jumpUrl参数已经有值
											jumpUrl = referer_url_jumpUrl;
											
										}
									}
								}
								
							}
							
							
						}else{
							//删除虚拟目录
							uri = this.deleteContextPath(request.getContextPath(),uri);
						}
						
					}
					
				}
				
				if("".equals(jumpUrl) && uri != null && !"".equals(uri.trim())){
					jumpUrl = Base64.encodeBase64URL(uri);//Base64安全编码
				}
				/**
				String contextPath = request.getContextPath();
				if(!(contextPath != null && !"".equals(contextPath.trim()) ? contextPath+"/login" : "/login").equals(request.getRequestURI())){//防止死循环
					if(isAjax == true){//ajax方式提交
						String new_jumpUrl = "login?jumpUrl="+jumpUrl;
						response.setHeader("jumpPath", new_jumpUrl);//设置登录页面响应http头。用来激活Ajax请求处理方式 Session超时后的跳转
							
						//如果在登录页面使用Ajax请求/user/开头的URL,出现死循环
						if(new_jumpUrl.equals(uri)){
							response.setStatus(508);//508服务器处理请求时检测到一个无限循环
							return;
						}
					}else{
						
						
						response.sendRedirect((contextPath != null && !"".equals(contextPath.trim()) ? contextPath+"/" : "/")+"login?jumpUrl="+jumpUrl);
					}
					
					return;
				}**/
				
				if(isAjax == true){//ajax方式提交
					String new_jumpUrl = "login?jumpUrl="+jumpUrl;
					response.setHeader("jumpPath", new_jumpUrl);//设置登录页面响应http头。用来激活Ajax请求处理方式 Session超时后的跳转
						
					//如果在登录页面使用Ajax请求/user/开头的URL,出现死循环
					if(new_jumpUrl.equals(uri)){
						response.setStatus(508);//508服务器处理请求时检测到一个无限循环
						return;
					}
					response.setStatus(401);//401前台访问无授权页面
					return;
				}else{
					String contextPath = request.getContextPath();
					
					response.sendRedirect((contextPath != null && !"".equals(contextPath.trim()) ? contextPath+"/" : "/")+"login?jumpUrl="+jumpUrl);
				}
				return;
			}
		}
		
		chain.doFilter(req, res);
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 删除虚拟目录
	 * @param contextPath request.getContextPath()
	 * @param uri
	 * @return
	 */
	private String deleteContextPath(String contextPath,String uri){
		// 删除虚拟目录
		if(contextPath != null && !"".equals(contextPath)){
			int old_uri_length = uri.length();
			
			uri = StringUtils.removeStartIgnoreCase(uri,contextPath+"/");//移除开始部分的相同的字符,不区分大小写 如shop/
			
			if(uri != null && old_uri_length != uri.length()){
				uri = StringUtils.removeStartIgnoreCase(uri,"/"+contextPath+"/");//移除开始部分的相同的字符,不区分大小写 如/shop/
			}
			
		}else{
			//删除开头的左斜杆
			uri = StringUtils.removeStartIgnoreCase(uri,"/");//移除开始部分的相同的字符,不区分大小写
		}
		return uri;
	}

	/**
	 * 获取跳转参数
	 * @param queryString
	 * @return
	 */
	private String getJumpUrl(String queryString){
		if(queryString != null && !"".equals(queryString)){
       		MultiMap<String> values = new MultiMap<String>();  
	       	UrlEncoded.decodeTo(queryString, values, "UTF-8");
	       	Iterator iter = values.entrySet().iterator();  
	       	while(iter.hasNext()){  
	       		Map.Entry e = (Map.Entry)iter.next();  
	       		if("jumpUrl".equals(e.getKey()) && e.getValue() != null){
	       			if(e.getValue() instanceof List){
	       				List<String> valueList = (List)e.getValue();
		       			if(valueList.size() >0){
		       				for(String value :valueList){
		       					if(value != null && !"".equals(value.trim())){
		       						return value.trim();
		       					}
		       				}
			       		}
		       		}
	       		}
	       	} 
       	}
		return null;
	}
}
