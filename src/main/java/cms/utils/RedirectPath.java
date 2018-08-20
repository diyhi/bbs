package cms.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 读取URL重定向路径(用于页面导航,如添加完成后的转向页跳向其它页的路径)
 *
 */
public class RedirectPath {
	
	private static final Logger logger = LogManager.getLogger(RedirectPath.class);
	
	//改成单例模式让只初始化一次节省性能
	private static Properties properties = new Properties();
	static{
		try {
			properties.load(RedirectPath.class.getClassLoader().getResourceAsStream("redirectPath.properties"));
		} catch (IOException e) {
			
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("读取URL重定向路径",e);
	        }
		}
	}
	public static String readUrl(String key){
		return (String)properties.get(key);
	}
}
