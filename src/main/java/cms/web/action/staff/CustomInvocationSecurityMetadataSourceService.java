package cms.web.action.staff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import cms.bean.PermissionObject;
import cms.service.staff.ACLService;

import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.RequestMatcher;



/**
 * 最核心的地方，就是提供某个资源对应的权限定义，即getAttributes方法返回的结果。 此类在初始化时，应该取到所有资源及其对应角色的定义。
 * 
 */
public class CustomInvocationSecurityMetadataSourceService implements
		FilterInvocationSecurityMetadataSource {

	
	
	private ACLService aclService;

	public ACLService getAclService() {
		return aclService;
	}
	public void setAclService(ACLService aclService) {
		this.aclService = aclService;
	}
    
	protected final Logger logger = LogManager.getLogger(getClass());

    private Map<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();

    //~ Constructors ===================================================================================================

    /**
     * Sets the internal request map from the supplied map. The key elements should be of type {@link RequestMatcher},
     * which. The path stored in the key will depend on
     * the type of the supplied UrlMatcher.
     *
     * @param requestMap order-preserving map of request definitions to attribute lists
     */
    public CustomInvocationSecurityMetadataSourceService(
    		ACLService aclService) {
    	this.aclService = aclService;
    	loadResourceDefine();
    }
 /**
    public CustomInvocationSecurityMetadataSourceService(
            LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap) {
        this.requestMap = requestMap;
   //     loadResourceDefine();
    }**/

    //~ Methods ========================================================================================================

    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();

        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }

        return allAttributes;
    }

    public Collection<ConfigAttribute> getAttributes(Object object) {
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
        	
        	if (entry.getKey().matches(request)) {
                return entry.getValue();
            }
        }
        return null;

        
        
    }


    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
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
