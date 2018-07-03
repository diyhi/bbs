package cms.web.action;


import javax.servlet.ServletContext;
import javax.servlet.jsp.JspFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.wltea.analyzer.cfg.DefaultConfig;

import com.github.pukkaone.jsp.EscapeXmlELResolver;


/**
 * 系统初始化管理
 *
 */
@Component
public class SystemInit implements InitializingBean,ServletContextAware{
	
	public void afterPropertiesSet() throws Exception {

		
		
		//系统启动时加载IKAnalyzer词典
		org.wltea.analyzer.dic.Dictionary.initial(DefaultConfig.getInstance());
		
	
	}


	@Override
	public void setServletContext(ServletContext context) {
	//	context.getSessionCookieConfig().setHttpOnly(true);
     
		//JSTL EL表达式XSS过滤
		JspFactory.getDefaultFactory()
        .getJspApplicationContext(context)
        .addELResolver(new EscapeXmlELResolver());
	}
}
