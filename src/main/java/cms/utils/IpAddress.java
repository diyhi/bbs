package cms.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbMakerConfigException;
import org.lionsoul.ip2region.DbSearcher;



/**
 *  获取IP地址
 *
 */
public class IpAddress {
	private static final Logger logger = LogManager.getLogger(IpAddress.class); 
	
	private static DbSearcher searcher = null;
	static{
		String path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator+"ip"+File.separator;
		try {
			searcher = new DbSearcher(new DbConfig(), path+"ip2region.db");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("IP地址库文件不存在错误",e);
	        }
		} catch (DbMakerConfigException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("IP地址库初始化配置错误",e);
	        }
		}
	}
	
	/**
	 * 获取IP地址
	 * @param request
	 * @return
	 */
	public static String getClientIpAddress(HttpServletRequest request){
	    String ip=request.getHeader("X-Forwarded-For");
	    if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)){
	        ip=request.getHeader("Proxy-Client-IP");
	    }
	    if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)){
	        ip=request.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)){
	        ip=request.getRemoteAddr();
	    }
	    //对于通过多个代理的情况， 第一个IP为客户端真实IP,多个IP按照','分割 "***.***.***.***".length()
	    if (ip != null && ip.length() > 15) { // "***.***.***.***".length()  
            // = 15  
			if (ip.indexOf(",") > 0) {  
				ip = ip.substring(0, ip.indexOf(","));  
			}  
	    } 
	    
	    return ip;
	}
	
	/**
	private static final String[] HEADERS_TO_TRY = {   
        "X-Forwarded-For",  
        "Proxy-Client-IP",  
        "WL-Proxy-Client-IP",  
        "HTTP_X_FORWARDED_FOR",  
        "HTTP_X_FORWARDED",  
        "HTTP_X_CLUSTER_CLIENT_IP",  
        "HTTP_CLIENT_IP",  
        "HTTP_FORWARDED_FOR",  
        "HTTP_FORWARDED",  
        "HTTP_VIA",  
        "REMOTE_ADDR",  
        "X-Real-IP"};  
  
     
     * 获取客户端ip地址(可以穿透代理) 
     * @param request 
     * 
     * 有可能获取到两个IP  117.136.79.200, 106.39.190.190	2017-10-01 14:25:31
     * @return 
     
    public static String getClientIpAddress(HttpServletRequest request) {  
        for (String header : HEADERS_TO_TRY) {  
            String ip = request.getHeader(header);  
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {  
                return ip;  
            }  
        }  
        return request.getRemoteAddr();  
    }  **/
	
	/**
	 * 查询IP归属地
	 * @param ip
	 * @return
	 */
	public static String queryAddress(String ip){
		if(searcher != null){
			try {

				return format(searcher.memorySearch(ip).getRegion());
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("IP地址查询错误",e);
		        }
			}
		}
		return "";
	}
	
	
	/**
	 * IP归属地格式化
	 * @param ipAddress
	 * @return
	 */
	private static String format(String ipAddress){
		if(ipAddress != null && !"".equals(ipAddress.trim())){
			ipAddress = StringUtils.replace(ipAddress, "|0", " ");//0替换成空串
			ipAddress = StringUtils.replace(ipAddress, "|", " ");//竖线替换成空格
		}
		return ipAddress;
	}

}
