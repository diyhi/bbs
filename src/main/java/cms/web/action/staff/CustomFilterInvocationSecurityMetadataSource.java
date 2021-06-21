package cms.web.action.staff;

import java.util.Collection;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cms.bean.PermissionObject;
import cms.service.staff.ACLService;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * 资源权限数据源，即定义某一资源可以被哪些角色访问
 * @author Gao
 *
 */
@Component
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource, InitializingBean{
	@Resource
	private ACLService aclService;
	
	private Map<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();

	
	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();

        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }

        return allAttributes;
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
        	
        	if (entry.getKey().matches(request)) {
                return entry.getValue();
            }
        }
        return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

	/**
	 * 初始化
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		loadResourceDefine();
	}
	private void loadResourceDefine() {
		// 在Web服务器启动时，提取系统中的所有权限。
		//应当是资源为key， 权限为value。 资源通常为url， 权限就是那些以ROLE_为前缀的角色。 一个资源可以由多个权限来访问。
		List<PermissionObject> query = aclService.findModulePermission();

		if(query != null && query.size() >0){
			for (PermissionObject permissionObject : query) {	
				String methods = null;
				if(permissionObject.getMethods() != null && !"".equals(permissionObject.getMethods())){
					methods = permissionObject.getMethods();
				}
				RequestMatcher matcher = new MyAntPathRequestMatcher(permissionObject.getUrl(), methods,true);
				Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();//权限
				ConfigAttribute ca = new SecurityConfig(permissionObject.getPermissionName()); 
				atts.add(ca);
				if(requestMap.get(matcher) != null){//处理附加URL情况	
					requestMap.get(matcher).add(ca);
				}else{
					requestMap.put(matcher,atts);
				}
			}
		}
	}
}
