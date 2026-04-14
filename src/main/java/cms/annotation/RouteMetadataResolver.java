package cms.annotation;

import cms.dto.frontendModule.ApiMetadata;
import cms.dto.frontendModule.HandlerMethodPair;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 路由元数据解析
 */
@Service
public class RouteMetadataResolver {
    @Resource
    ListableBeanFactory beanFactory;
    /**
     * 根据 DynamicRouteEnum 查找并提取关联的 @RequestMapping 信息。
     * @param targetRouteEnum 目标路由枚举值，例如 DynamicRouteEnum.DEFAULT_1000200
     * @return 包含路径和HTTP方法的 ApiMetadata 对象，如果找不到则返回 null。
     */
    public ApiMetadata findMetadataByRouteEnum(DynamicRouteEnum targetRouteEnum) {
        // 扫描所有带有 @DynamicRouteTarget 注解的 Bean
        Map<String, Object> targetBeans = beanFactory.getBeansWithAnnotation(Controller.class);
        for (Object handler : targetBeans.values()) {
            Class<?> handlerClass = handler.getClass();
            // 遍历 Controller 中的所有方法
            for (Method method : handlerClass.getMethods()) {

                //检查方法上是否有 @DynamicRouteTarget 注解
                if (method.isAnnotationPresent(DynamicRouteTarget.class)) {
                    DynamicRouteTarget dynamicTarget = method.getAnnotation(DynamicRouteTarget.class);

                    // 检查注解中的枚举值是否匹配目标
                    if (dynamicTarget.route() == targetRouteEnum) {

                        // 找到匹配项！现在读取 @RequestMapping
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                        if (requestMapping != null) {
                            // 提取 value 和 method 属性
                            String[] paths = requestMapping.value().length > 0
                                    ? requestMapping.value()
                                    : new String[]{""}; // 如果 value 为空，使用空数组

                            RequestMethod[] methods = requestMapping.method().length > 0
                                    ? requestMapping.method()
                                    : new RequestMethod[]{};

                            // 返回封装好的元数据
                           // System.out.printf("元数据 %s: 路径=%s, 方法=%s\n",targetRouteEnum.name(),Arrays.toString(paths),Arrays.toString(methods));

                            return new ApiMetadata(paths, methods,targetRouteEnum.name(),targetRouteEnum.getName());
                        } else {
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }


    /**
     * 获取所有 @DynamicRouteEnum 注解类上关联的 @RequestMapping 信息。
     * @return 包含路径和HTTP方法的 ApiMetadata 对象集合
     */
    public List<ApiMetadata> findAllMetadata() {
        List<ApiMetadata> apiMetadataList = new ArrayList<ApiMetadata>();
        DynamicRouteEnum[] allRoutes = DynamicRouteEnum.values();
        // 扫描所有带有 @DynamicRouteTarget 注解的 Bean
        Map<String, Object> targetBeans = beanFactory.getBeansWithAnnotation(Controller.class);
        for (Object handler : targetBeans.values()) {
            Class<?> handlerClass = handler.getClass();
            // 遍历 Controller 中的所有方法
            A:for (Method method : handlerClass.getMethods()) {

                //检查方法上是否有 @DynamicRouteTarget 注解
                if (method.isAnnotationPresent(DynamicRouteTarget.class)) {
                    DynamicRouteTarget dynamicTarget = method.getAnnotation(DynamicRouteTarget.class);
                    for (DynamicRouteEnum route : allRoutes) {
                        // 检查注解中的枚举值是否匹配目标
                        if (dynamicTarget.route() == route) {

                            // 找到匹配项！现在读取 @RequestMapping
                            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                            if (requestMapping != null) {
                                // 提取 value 和 method 属性
                                String[] paths = requestMapping.value().length > 0
                                        ? requestMapping.value()
                                        : new String[]{""}; // 如果 value 为空，使用空数组

                                RequestMethod[] methods = requestMapping.method().length > 0
                                        ? requestMapping.method()
                                        : new RequestMethod[]{};

                                apiMetadataList.add(new ApiMetadata(paths, methods,route.name(),route.getName()));
                            }
                            continue A;
                        }

                    }

                }
            }
        }
        return apiMetadataList;
    }


    /**
     * 通过路由枚举值查找 Controller 实例（Handler）
     * @param targetRouteEnum 目标路由枚举值，例如 DynamicRouteEnum.DEFAULT_1000200
     * @return Controller 实例，如果找不到则返回 null。
     */
    public HandlerMethodPair findHandlerByRouteEnum(DynamicRouteEnum targetRouteEnum) {
        // 扫描所有带有 @DynamicRouteTarget 注解的 Bean
        Map<String, Object> targetBeans = beanFactory.getBeansWithAnnotation(Controller.class);
        for (Object handler : targetBeans.values()) {
            Class<?> handlerClass = handler.getClass();
            // 遍历 Controller 中的所有方法
            for (Method method : handlerClass.getMethods()) {

                //检查方法上是否有 @DynamicRouteTarget 注解
                if (method.isAnnotationPresent(DynamicRouteTarget.class)) {
                    DynamicRouteTarget dynamicTarget = method.getAnnotation(DynamicRouteTarget.class);

                    // 检查注解中的枚举值是否匹配目标
                    if (dynamicTarget.route() == targetRouteEnum) {
                        return new HandlerMethodPair(handler, method);
                    }
                }
            }
        }
        return null;
    }

}
