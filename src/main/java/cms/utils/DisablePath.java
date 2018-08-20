package cms.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 禁止路径
 *
 */
public class DisablePath {
	private static final Logger logger = LogManager.getLogger(DisablePath.class);
	 
	private static Set<String> disablePathSet = new HashSet<String>();//URL禁止路径
	
	/**
	 * 禁止路径初始化
	 */
	static{
		Properties props = new Properties();   
		//取得当前系统使用的目录
        try {
			props.load(DisablePath.class.getClassLoader()   
			        .getResourceAsStream("disablePath.properties"));//得到当前类的类加载器,以流的方式读取配置文件   
			
			Iterator itr = props.entrySet().iterator();
	        while (itr.hasNext()){
	            Entry e = (Entry)itr.next();
	            
	            if(e.getKey().toString().startsWith("path")){//path为禁止路径前缀
	            	disablePathSet.add(e.getValue().toString().trim().toLowerCase());
	            }
	        }

		} catch (IOException e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("禁止路径初始化",e);
	        }
		}
	}

	public static Set<String> getPath(){
		return disablePathSet;
	}
}
