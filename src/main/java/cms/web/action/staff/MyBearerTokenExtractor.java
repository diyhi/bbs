package cms.web.action.staff;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
/**
 * 令牌提取器
 * 参考org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor.java
 * @author Gao
 *
 */
public class MyBearerTokenExtractor implements TokenExtractor{
	private final static Log logger = LogFactory.getLog(MyBearerTokenExtractor.class);
	
	//指定的URL下工作
	private AntPathRequestMatcher[] filterMatchers = {
		    new AntPathRequestMatcher("/control/**"),
		    new AntPathRequestMatcher("/admin/logout")
		};
	
	
	@Override
	public Authentication extract(HttpServletRequest request) {
		
		boolean flag = false;
		for (AntPathRequestMatcher rm : filterMatchers) {
			if (rm.matches(request)) { 
				flag = true;
			}
		}
		
		
		if(flag){
			String tokenValue = extractToken(request);
			if (tokenValue != null) {
				PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(tokenValue, "");
				return authentication;
			}
		}
		
		/**
		String tokenValue = extractToken(request);
		if (tokenValue != null) {
			PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(tokenValue, "");
			return authentication;
		}**/
		return null;
	}

	protected String extractToken(HttpServletRequest request) {
		// 检查header...
		String token = extractHeaderToken(request);
		
		// 如果标头为空也允许从请求参数中提取
		if (token == null) {
			logger.debug("标头header中找不到令牌。 尝试从请求参数中提取");
			token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
			if (token == null) {
				logger.debug("在请求参数中找不到令牌。 不是OAuth2请求");
			}
			else {
				request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, OAuth2AccessToken.BEARER_TYPE);
			}
		}

		return token;
	}

	/**
	 * 从标头header中提取OAuth令牌
	 * 
	 * @param request.
	 * @return 令牌；如果未提供OAuth授权标头，则为null。
	 */
	protected String extractHeaderToken(HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaders("Authorization");
		while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
			String value = headers.nextElement();
			if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
				String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
				// Add this here for the auth details later. Would be better to change the signature of this method.
				request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE,
						value.substring(0, OAuth2AccessToken.BEARER_TYPE.length()).trim());
				int commaIndex = authHeaderValue.indexOf(',');
				if (commaIndex > 0) {
					authHeaderValue = authHeaderValue.substring(0, commaIndex);
				}
				return authHeaderValue;
			}
		}

		return null;
	}

}
