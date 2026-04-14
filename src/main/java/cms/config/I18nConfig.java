package cms.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import cms.utils.FileUtil;

/**
 * 国际化文件
 * @author Gao
 *
 */
@Configuration
public class I18nConfig implements LocaleResolver, InitializingBean{
	private static final Logger logger = LogManager.getLogger(I18nConfig.class);
	
	
	//i18n目录下的国际化文件
	private final List<String> i18nList = new ArrayList<String>();
	
	/**
	 * 初始化数据
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		org.springframework.core.io.Resource[] resources;
		try {
			resources = new PathMatchingResourcePatternResolver().
			        getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "i18n/**");
			
			// 遍历文件内容
	        for(org.springframework.core.io.Resource resource : resources) {
	        	if("jar".equals(resource.getURL().getProtocol())){//jar:开头
	        		//jar:file:/D:/test2/test.jar!/BOOT-INF/classes!/i18n/
	        		i18nList.add(FileUtil.getBaseName(resource.getURL().getPath()));
	        	}else{//file:开头
	        		//file:/F:/JAVA/cms-pro/target/classes/i18n/
	        		i18nList.add(FileUtil.getBaseName(resource.getURL().getPath()));
	        	}
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("读取国际化语言文件错误",e);
	        }
		}
	}
	
	
	@Primary
    @Bean(name = "messageSource")
    public MessageSource messageSource(){
		ReloadableResourceBundleMessageSource messageBundle = new ReloadableResourceBundleMessageSource();
        messageBundle.setBasenames("classpath:i18n/errorMessages","classpath:i18n/springSecurityMessages","classpath:i18n/messages");//即配置文件所在目录为 i18n，文件前缀为 errorMessages
        messageBundle.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageBundle.setFallbackToSystemLocale(false);//在未找到特定 Locale 的文件时是否返回系统 Locale  默认为true
        return messageBundle;
    }
	
	/**
	 * 根据请求中的Accept-Language头部解析出语言设置。
	 * 如果没有指定语言，则默认为中文;如果语言设置不完整或设置错误，则为美国英语。
	 * @param request HTTP请求对象
	 * @return 解析后的语言设置Locale对象
	 */
	
	@Override
	public Locale resolveLocale(HttpServletRequest request) {

        // 从请求的头部获取Accept-Language标头，该标头指定了客户端期望的语言
        //String language = request.getHeader("Accept-Language");
		 
        String language = request.getLocale().getLanguage();
        
        if(language == null || language.equalsIgnoreCase("zh")){//汉字   zh-CN简体中文(中国)	 zh-TW繁体中文(中国台湾)  zh-HK繁体中文(中国香港)   zh-SG新加坡
        	//locale = Locale.CHINA;// 默认语言设置为中文
        	return Locale.of("zh", "CN");
        }
        
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        //localeResolver.setDefaultLocale(Locale.ENGLISH);//English – en
        localeResolver.setDefaultLocale(Locale.US);//US – en_US
        Locale locale = localeResolver.resolveLocale(request);
        if(i18nList.contains("messages_"+locale)){
        	return locale;
        }

        return Locale.of("en", "US");//默认语言设置为美国英语
	}

	 /**
     * 将当前语言设置为指定语言
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param locale 语言设置的Locale对象
     **/
	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		// TODO Auto-generated method stub
		
	}
	
	/**
     * 创建LocaleResolver用于处理语言设置。
     **/
	@Bean(name="localeResolver")
    public LocaleResolver localeResolver() {
        // 返回当前类作为LocaleResolver的实例
        return new I18nConfig();
    }
	
}
