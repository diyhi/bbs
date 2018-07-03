package cms.web.filter;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




/**
 * 系统启动或关闭执行
 *
 */
public class BasicConfig implements ServletContextListener{
	private static final Logger logger = LogManager.getLogger(BasicConfig.class);
	
	
	/**
	 * 初始化执行
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		
				
	}
	
	/**
	 * 关闭系统时执行
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
		//解决关闭Tomcat时报错 registered the JDBC driver [com.mysql.cj.jdbc.Driver] but failed to unregister it when the web application was stopped. To prevent a memory leak, the JDBC Driver has been forcibly unregistered.
		com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();


		
		//解决关闭Tomcat时报错 registered the JDBC driver [com.alibaba.druid.proxy.DruidDriver] but failed to unregister it when the web application was stopped. To prevent a memory leak, the JDBC Driver has been forcibly unregistered.
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		Enumeration<java.sql.Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			java.sql.Driver driver = drivers.nextElement();

		    if (driver.getClass().getClassLoader() == cl) {

		        try {
		            DriverManager.deregisterDriver(driver);

		        } catch (SQLException ex) {
		        	if (logger.isErrorEnabled()) {
					    logger.error("关闭连接池错误",ex);
			    	}
		        }

		    }
		} 
		
		
	}

	

}
