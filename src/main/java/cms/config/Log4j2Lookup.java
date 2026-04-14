package cms.config;

import java.io.File;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

import cms.utils.PathUtil;

/**
 * Log4j2自定义变量
 * @author Gao
 *
 */
@Plugin(name = "custom", category = StrLookup.CATEGORY)
public class Log4j2Lookup implements StrLookup{

	@Override
	public String lookup(String key) {

		//日志完整路径
		if ("fullPath".equals(key)) {
			return PathUtil.defaultExternalDirectory()+File.separator+"data"+File.separator+"log"+File.separator;
			
		}
		return null;
	}

	@Override
	public String lookup(LogEvent event, String key) {
		return null;
	}

}
