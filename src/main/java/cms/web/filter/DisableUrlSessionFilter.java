package cms.web.filter;

import javax.servlet.Filter;   
import javax.servlet.FilterChain;   
import javax.servlet.FilterConfig;   
import javax.servlet.ServletException;   
import javax.servlet.ServletRequest;   
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;   
import javax.servlet.http.HttpServletResponse;   
import javax.servlet.http.HttpServletResponseWrapper;   
import javax.servlet.http.HttpSession;   
import java.io.IOException;

/**
 * 去掉URL带的jsessionid  并且设置HttpOnly=true
 * 用户首次登录系统时，会在地址后面加个jsessionid=xxx
 *
 */
public class DisableUrlSessionFilter implements Filter {   
	  
    /**  
     * Filters requests to disable URL-based session identifiers.  
     */  
    public void doFilter(ServletRequest request, ServletResponse response,   
            FilterChain chain) throws IOException, ServletException {   
        // skip non-http requests   
        if (!(request instanceof HttpServletRequest)) {   
            chain.doFilter(request, response);   
            return;   
        }   
  
        HttpServletRequest httpRequest = (HttpServletRequest) request;   
        HttpServletResponse httpResponse = (HttpServletResponse) response;   
  
         
        // clear session if session id in URL   
        if (httpRequest.isRequestedSessionIdFromURL()) {   
            HttpSession session = httpRequest.getSession();   
            if (session != null)   
                session.invalidate();   
        }  
        // wrap response to remove URL encoding   
        HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(   
                httpResponse) {   
            @Override  
            public String encodeRedirectUrl(String url) {   
                return url;   
            }   
  
            @Override  
            public String encodeRedirectURL(String url) {   
                return url;   
            }   
  
            @Override  
            public String encodeUrl(String url) {   
                return url;   
            }   
  
            @Override  
            public String encodeURL(String url) {   
                return url;   
            }   
        };   
  
        // process next request in chain   
        chain.doFilter(request, wrappedResponse);   
    }   
  
    /**  
     * Unused.  
     */  
    public void init(FilterConfig config) throws ServletException {   
    }   
  
    /**  
     * Unused.  
     */  
    public void destroy() {   
    }   
}  

