package cms.web.action.staff;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;

import cms.bean.staff.SysUsers;

/**
 * 访问决策器，决定某个用户具有的角色，是否有足够的权限去访问某个资源
 * @author Gao
 *
 */
@Component
public class CustomAccessDecisionManager implements AccessDecisionManager{
	@Resource StaffManage staffManage;
	
	
	/**
	 * 决定是否有权限访问资源
	 * @param authentication 用户凭证
	 * @param resource 资源 URL
	 * @param configAttributes 资源 URL 所需要的权限
	 * @throws AccessDeniedException 资源拒绝访问
     * @throws InsufficientAuthenticationException 用户凭证不符
	 */
	@Override
	public void decide(Authentication authentication, Object resource, Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		if( configAttributes == null ) {
			return ;
		}
		
	
		if(authentication.getPrincipal() instanceof SysUsers){
			SysUsers sysUsers = (SysUsers)authentication.getPrincipal();
			String userAccount = sysUsers.getUserAccount();

			String securityDigest = staffManage.query_staffSecurityDigest(userAccount);
			if(securityDigest != null){
				if(!sysUsers.getSecurityDigest().equals(securityDigest)){
					throw new AccessDeniedException("没有权限访问 - 安全摘要校验失败");
				}
			}else{
				throw new AccessDeniedException("没有权限访问 - 员工安全摘要为空");
			}
		}
		
		//所请求的资源拥有的权限(一个资源对多个权限)   
		Iterator<ConfigAttribute> ite = configAttributes.iterator();
		
		while(ite.hasNext()){
			
			ConfigAttribute ca = ite.next();
			//访问所请求资源所需要的权限   
			String needRole = ((SecurityConfig)ca).getAttribute();
			//ga 为用户所被赋予的权限。 needRole 为访问相应的资源应该具有的权限。
			for( GrantedAuthority ga: authentication.getAuthorities()){
				if(needRole.trim().equals(ga.getAuthority().trim())){
					return;
				}
			}	
		}
		throw new AccessDeniedException("没有权限访问");
		
	}	
		

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	/**
	 * 是否支持 FilterInvocationSecurityMetadataSource 需要将这里的false改为true
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

}
