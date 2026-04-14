package cms.utils;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * 让普通的java类获取spring 的bean配置
 * 此类可以取得Spring的上下文
 * 
 * OrderService orderService = (OrderService)SpringConfigTool.getContext().getBean("orderServiceBean");
 *
 */
@Configuration
public class SpringConfigTool implements ApplicationContextAware{
	/* 第一种写法 */
	
	private static ApplicationContext context;//声明一个静态变量保存
	
	//这个方法在在Bean被初始后，将会被注入 ApplicationContext的实例
	public void setApplicationContext(ApplicationContext contex)throws BeansException {
		this.context=contex;

	}

	public static ApplicationContext getContext(){
		return context;
	}
	
	/* 第二种写法 *//**
	private static ApplicationContext context = null;
	private static SpringConfigTool stools = null;
	public synchronized static SpringConfigTool init(){
		if(stools == null){
			stools = new SpringConfigTool();
		}
		return stools;
	}
	 
	public void setApplicationContext(ApplicationContext applicationContext)throws BeansException {
		context = applicationContext;
	}

	public synchronized static Object getBean(String beanName) {
		return context.getBean(beanName);
	}

**/

}
