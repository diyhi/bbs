package cms.utils;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * yml文件工具
 * @author Gao
 *
 */
public class YmlUtils {
	private static final Logger logger = LogManager.getLogger(YmlUtils.class);
	
	/**
	 * 读取yml文件
	 * @param fileName 文件名称 例如application.yml
	 * @param key yml属性key
	 * @return
	 */
    public static Object getYmlProperty(String fileName,Object key){
        Resource resource = new ClassPathResource(fileName);
        Properties properties = null;
        try {
            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(resource);
            properties =  yamlFactory.getObject();
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
	            logger.error("读取yml文件属性错误 "+fileName,e);
	        }
            return null;
        }
        return properties.get(key);
    }
    
    /**
	 * 读取yml文件
	 * @param fileName 文件名称 例如application.yml
	 * @return
	 */
    public static Properties getYml(String fileName){
        Resource resource = new ClassPathResource(fileName);
        Properties properties = null;
        try {
            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(resource);
            properties =  yamlFactory.getObject();
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
	            logger.error("读取yml文件错误 "+fileName,e);
	        }
            return null;
        }
        return properties;
    }
}
