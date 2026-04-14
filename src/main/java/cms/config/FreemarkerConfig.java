package cms.config;

import java.io.IOException;
import java.util.Locale;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.core.TemplateClassResolver;
import freemarker.template.TemplateException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

/**
 * Freemarker配置
 *
 */
@Configuration
public class FreemarkerConfig{


	
	@Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() throws IOException, TemplateException {
		MyFreeMarkerConfigurer factory = new MyFreeMarkerConfigurer();

        factory.setTemplateLoaderPaths("classpath:data/install/");//模板路径
		
       // factory.setTemplateLoaderPaths("classpath:templates/","classpath:manage/");//模板路径 classpath:static/view/
		factory.setDefaultEncoding("UTF-8");//编码设置
        //factory.setPreferFileSystemAccess(false);//是否优先从从文件系统中获取模板，以支持热加载，默认为true。  要继承父模板，读取父模板内容，需要设置prefer-file-system-access=false，否则会报404无法找到视图。并且设置为false后，数据热加载测试依然可以正常运行

        freemarker.template.Configuration configuration = factory.createConfiguration();
        
        configuration.setTemplateUpdateDelayMilliseconds(5000); //刷新模板的周期，单位为毫秒;如果模板不经常更新，此属性设置更新延迟时间
        configuration.setDefaultEncoding("UTF-8");//模板的编码格式
        configuration.setLocale(Locale.CHINA);//本地化设置
        configuration.setClassicCompatible(true);//空值处理 模板解析引擎是否工作在“Classic Compatibile”模式下
        configuration.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");//时间格式化
        configuration.setTimeFormat("HH:mm:ss");//时间格式化 
        configuration.setNumberFormat("0.######");//设置数字格式 以免出现 000.00
        configuration.setBooleanFormat("true,false");//布尔值格式化输出的格式
        configuration.setWhitespaceStripping(true);//剥去空白区域
        configuration.setTagSyntax(freemarker.template.Configuration.AUTO_DETECT_TAG_SYNTAX);//tag_syntax = square_bracket||auto_detect 设置标签类型 两种：[] 和 <> 。[] 这种标记解析要快些
        configuration.setURLEscapingCharset("UTF-8");//URL编码的字符集
       
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);//禁止解析任何类

        configuration.setIncompatibleImprovements(freemarker.template.Configuration.VERSION_2_3_34);
        factory.setConfiguration(configuration);
        return factory;
    }

    @Bean
    public FreeMarkerViewResolver freemarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();

        //设置优先级：必须高于默认解析器
        //resolver.setOrder(0);

        //设置后缀：必须匹配你 data/install/ 目录下的文件名 (如 install.html)
        resolver.setSuffix(".html");

        //设置内容类型
        resolver.setContentType("text/html; charset=UTF-8");
        resolver.setViewClass(org.springframework.web.servlet.view.freemarker.FreeMarkerView.class);
        return resolver;
    }
}
