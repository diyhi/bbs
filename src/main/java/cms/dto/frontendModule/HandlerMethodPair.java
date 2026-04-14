package cms.dto.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 封装 Controller 实例（Handler）和其目标方法（Method）
 */
@Getter
@Setter
public class HandlerMethodPair implements Serializable {
    @Serial
    private static final long serialVersionUID = 8281685456269386912L;

    /** 对应路由的 Controller Bean 实例。 */
    private Object handler;

    /** Controller 实例中带有 @DynamicRouteTarget 注解的 Java 方法。*/
    private Method method;

    public HandlerMethodPair() {}
    /**
     * 构造函数。
     * @param handler Controller Bean 实例。
     * @param method 目标 Java 方法。
     */
    public HandlerMethodPair(Object handler, Method method) {
        this.handler = handler;
        this.method = method;
    }
}
