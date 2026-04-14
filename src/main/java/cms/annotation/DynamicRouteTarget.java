package cms.annotation;

import java.lang.annotation.*;

/**
 * 动态路由映射
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicRouteTarget {
    /**
     * 此 Controller 关联到的路由枚举项
     */
    DynamicRouteEnum route();
}
