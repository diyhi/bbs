package cms.web.taglib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 自定义标签，对应tld文件为WEB-INF/tld/config.tld
 *
 */
public class Configuration{
	private static final Logger logger = LogManager.getLogger(Configuration.class);
	
	//虚拟路径
	private static String path = "";
	

	
	/**
	 * 获取网站URL
	 * @return
	 */
	public static String getUrl(HttpServletRequest request) {
		String port = "";
		if(request.getServerPort() != 80 && request.getServerPort() !=443){
			port = ":"+request.getServerPort();
			
		}
		
		
		return request.getScheme()+"://"+request.getServerName()+port+request.getContextPath()+"/";
	}
	
	/**
	 * 获取网站虚拟路径
	 * @return
	 */
	public static String getPath() {
		return path;
	}
	/**
	 * 由cms.web.filter.TempletesInterceptor方法设置参数
	 * /shop
	 * @param _path
	 */
	public static synchronized void setPath(String _path) {
		path = _path;
	}
	
	
	/**
	 * 取得网站后缀名
	 * @return .htm
	 */
	public static String getSuffix() {
	    return ".htm";
	}
	/**
	 * 取得WEB-INF目录的绝对路径
	 * @return
	 
	public static String paths() {
		if(paths == null || "".equals(paths)){
			
			HttpServletRequest request = RequestThreadLocal.get();
			//当前目录绝对路径
			paths = request.getSession().getServletContext().getRealPath("/WEB-INF")+File.separator;
		}
	    return paths;
	}*/
	/**
	 * 将文本用UTF8格式解码
	 * @param text
	 * @return
	 */
	public static String decode(String text) {
		String text_utf8 ="";
		try {
			
			text_utf8 = URLDecoder.decode(text,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("将文本用UTF8格式解码",e);
	        }
		}
		
	    return text_utf8;
	}
	
	/**
	 * 截取资源标识
	 * @param uri
	 * @param path 虚拟路径
	 * @return
	 */
	public static String baseURI(String uri,String path){
		if(uri != null && !"".equals(uri)){
			if(path != null && !"".equals(path)){
				if(uri.length() > path.length() && uri.startsWith(path)){
					uri = uri.substring(path.length(),uri.length());
				}
			}
			//删除左斜杆
			if(uri != null && !"".equals(uri)){
				if(uri.startsWith("/")){
					uri = uri.substring(1,uri.length());
				}
			}
			return uri;
		}
		return "";
	}
	
	
}
