package cms.web.filter;

import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 过滤掉不需要CSRF保护的URL
 *
 */
public class CsrfSecurityRequestMatcher implements RequestMatcher {
	private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
	
	//过滤URL
	private AntPathRequestMatcher[] filterMatchers = {
		    new AntPathRequestMatcher("/control/**"),
		    new AntPathRequestMatcher("/admin/logout")
		};
	
	//排除URL
	private Set<AntPathRequestMatcher> requestMatchers;
	
	public Set<AntPathRequestMatcher> getRequestMatchers() {
		return requestMatchers;
	}
	public void setRequestMatchers(Set<AntPathRequestMatcher> requestMatchers) {
		this.requestMatchers = requestMatchers;
	}


	@Override
	public boolean matches(HttpServletRequest request) {
		boolean flag = false;
		//session过期则不保护
		HttpSession session = request.getSession(false);
        if (session == null) {
        	return false; 
        }
        
		
		//过滤URL
		for (AntPathRequestMatcher rm : filterMatchers) {
			if (rm.matches(request)) { 
				flag = true;
			}
		}
		
		
		if(flag == true){
			if (allowedMethods.matcher(request.getMethod()).matches()) {
				return false;
		    }   
		    // 排除URL
			if(requestMatchers != null && requestMatchers.size() >0){
				for (AntPathRequestMatcher rm : requestMatchers) {
					if (rm.matches(request)) { 
						return false; 
					}
				}
			}
		    return true;
		}
		return false; 
	}
}
