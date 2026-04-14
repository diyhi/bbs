package cms.security;

import cms.dto.staff.PermissionObject;
import cms.repository.staff.ACLRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;

/**
 * 动态权限
 */
@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Resource private ACLRepository aclRepository;



    // 缓存权限数据，格式为：MyAntPathRequestMatcher -> 权限列表
    private final Map<MyAntPathRequestMatcher, List<String>> permissionCache = new LinkedHashMap<>();

    @PostConstruct
    public void loadResourceDefine() {
        List<PermissionObject> query = aclRepository.findModulePermission();
        if (query != null && !query.isEmpty()) {
            for (PermissionObject permissionObject : query) {
                // 将 URL 和 HTTP 方法包装成 MyAntPathRequestMatcher
                MyAntPathRequestMatcher matcher = new MyAntPathRequestMatcher(permissionObject.getUrl(), permissionObject.getMethods(),true);

                // 将权限名添加到对应URL的权限列表中
                permissionCache.computeIfAbsent(matcher, k -> new ArrayList<>())
                        .add(permissionObject.getPermissionName());
            }
        }
    }


    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authenticationSupplier, RequestAuthorizationContext context) {

        // 获取当前认证对象
        Authentication authentication = authenticationSupplier.get();

        // 匿名用户直接拒绝
        if (!authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // 返回被拒绝的 AuthorizationResult
            return new AuthorizationDecision(false);
        }

        // 获取请求的URL和HTTP方法
        String requestUrl = context.getRequest().getRequestURI();
        String requestMethod = context.getRequest().getMethod();

        // 权限规则匹配
        List<String> requiredPermissions = new ArrayList<>();

        for (Map.Entry<MyAntPathRequestMatcher, List<String>> entry : permissionCache.entrySet()) {
            // 使用MyAntPathRequestMatcher 进行匹配
            if (entry.getKey().matches(context.getRequest())) {
                requiredPermissions.addAll(entry.getValue());
            }
        }

        // 如果该 URL 不需要任何权限，则允许访问
        if (requiredPermissions.isEmpty()) {
            return new AuthorizationDecision(true);
        }

        //获取当前用户的权限
        Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();

        //检查用户是否拥有 URL 所需的任一权限
        boolean hasPermission = userAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requiredPermissions::contains);

        // 最终返回 AuthorizationDecision，Spring Security 7.x 会隐式将其封装成 AuthorizationResult
        return new AuthorizationDecision(hasPermission);
    }
}
