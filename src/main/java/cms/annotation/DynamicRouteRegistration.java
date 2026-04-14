package cms.annotation;

import cms.component.frontendModule.FrontendApiComponent;
import cms.model.frontendModule.FrontendApi;
import cms.repository.frontendModule.FrontendApiRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;


import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 启动时动态路由注册
 */
@Configuration
public class DynamicRouteRegistration implements ApplicationListener<ApplicationReadyEvent> {
    @Resource
    ListableBeanFactory beanFactory;
    @Resource
    RequestMappingHandlerMapping handlerMapping;
    @Resource
    FrontendApiRepository frontendApiRepository;

    @Resource
    FrontendApiComponent frontendApiComponent;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 扫描所有带有 @DynamicRouteTarget 的 Controller
        Map<String, Object> targetBeans = beanFactory.getBeansWithAnnotation(Controller.class);

        for (Object handler : targetBeans.values()) {
            Class<?> handlerClass = handler.getClass();

            // 遍历Controller中的所有方法
            for (Method method : handlerClass.getMethods()) {
                if (method.isAnnotationPresent(DynamicRouteTarget.class)) {

                    // 检查该方法是否已经被 Spring MVC 注解映射（如 @RequestMapping, @GetMapping, etc.）
                    if (isAlreadyMapped(method)) {
                        continue;
                    }

                    DynamicRouteTarget annotation = method.getAnnotation(DynamicRouteTarget.class);
                    registerMapping(handler, annotation.route(), method);
                }
            }
        }
    }

    /**
     * 根据数据库中配置的 路由信息 注册路由
     * @param handler Controller对象
     * @param routeEnum 动态路由枚举
     * @param method 请求方法
     */
    private void registerMapping(Object handler, DynamicRouteEnum routeEnum, Method method) {

        List<FrontendApi> frontendApiList = frontendApiRepository.findFrontendApiByMapper(routeEnum.name());
        if(frontendApiList != null && !frontendApiList.isEmpty()) {
            for (FrontendApi frontendApi : frontendApiList) {
                RequestMappingInfo mappingInfo = frontendApiComponent.createRequestMappingInfo(frontendApi.getUrl(), frontendApi.getHttpMethod());

                // 注册路由
                handlerMapping.registerMapping(mappingInfo, handler, method);

            }
        }
    }


    /**
     * 检查目标方法是否已经通过 Spring MVC 的注解（如 @RequestMapping, @GetMapping 等）配置了 URL。
     */
    private boolean isAlreadyMapped(Method method) {
        // AnnotatedElementUtils.has	Annotation 可以检查元注解（如 @GetMapping 包含 @RequestMapping）
        return AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class);
    }
}
