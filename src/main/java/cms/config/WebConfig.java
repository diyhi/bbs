package cms.config;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


import jakarta.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import cms.utils.PathUtil;
import tools.jackson.databind.json.JsonMapper;

/**
 * 拦截器
 * @author Gao
 *
 */
@Configuration
//@EnableWebMvc//本注解会关闭默认配置
public class WebConfig implements WebMvcConfigurer{

    @Resource
    PageHandlerInterceptor pageHandlerInterceptor;
	
	@Resource TaskExecutor taskExecutor;//多线程

	
    /**
     * 增加处理静态资源的Handler
     * 源码 org.springframework.boot.autoconfigure.web.ResourceProperties CLASSPATH_RESOURCE_LOCATIONS
     * 自定义静态资源目录  越靠前的配置优先级越高
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry.setOrder(100).addResourceHandler("/file/**")
        .addResourceLocations("file:"+PathUtil.defaultExternalDirectory()+File.separator+"file"+File.separator);
    	
    	registry.setOrder(200).addResourceHandler("/robots.txt")
        .addResourceLocations("classpath:/static/");

    	/**
    	registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/");
               // .addResourceLocations("file:E:/myimgs/");
    //	super.addResourceHandlers(registry);**/
    }



    /**
     * 【JSON 转换器】手动创建 JacksonJsonHttpMessageConverter 并注入定制 JsonMapper
     */
    @Bean
    public JacksonJsonHttpMessageConverter customJsonMessageConverter(JsonMapper customJsonMapper) {
        // 使用构造函数注入定制的 ObjectMapper
        JacksonJsonHttpMessageConverter converter =
                new JacksonJsonHttpMessageConverter(customJsonMapper);

        // 确保它支持 JSON 类型
        converter.setSupportedMediaTypes(List.of(
                new MediaType("application", "json", StandardCharsets.UTF_8),
                new MediaType("application", "*+json", StandardCharsets.UTF_8)
        ));

        return converter;
    }

    /**
     * 【String 转换器】注册定制的 StringHttpMessageConverter
     */
    @Bean
    public StringHttpMessageConverter customStringHttpMessageConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        // 设置支持的 MIME 类型和编码
        converter.setSupportedMediaTypes(Arrays.asList(
                new MediaType("text", "html", StandardCharsets.UTF_8),
                MediaType.TEXT_PLAIN
        ));
        return converter;
    }

    /**
     * 配置异步请求处理选项
     * 
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        long timeout = 24 * 60 * 60 * 1000;//24小时
        WebMvcConfigurer.super.configureAsyncSupport(configurer);
        configurer.setDefaultTimeout(timeout);//超时时间，单位/毫秒;  -1表示无限制;  86400000表示24小时; 如果未设置此值，则使用基础实现的默认超时，例如，在带有Servlet 3的Tomcat上为10秒。
        configurer.setTaskExecutor((ThreadPoolTaskExecutor)taskExecutor);//指定自定义线程池
        
       // configurer.registerDeferredResultInterceptors(this.timeoutDeferredTimeoutInterceptor());//注册异步拦截器
    }


    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pageHandlerInterceptor);
    }




}
