package cms.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;


/**
 *  获取IP地址
 *
 */
public class IpAddress {
	private static final Logger logger = LogManager.getLogger(IpAddress.class);

    private static Searcher searcher;

	static{

        ClassPathResource classPathResource = new ClassPathResource("data/ip/ip2region.xdb");

        try (InputStream inputStream = classPathResource.getInputStream()){

            searcher = Searcher.newWithBuffer(IOUtils.toByteArray(inputStream));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            //	e.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error("IP地址库文件不存在错误",e);
            }
        }catch (IOException e) {
            // TODO Auto-generated catch block
            //e1.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error("IP地址库初始化IO错误",e);
            }
        }
	}

	
	/**
	 * 获取IP地址
	 * @param request 请求信息
	 * @return
	 */
	public static String getClientIpAddress(HttpServletRequest request){
	    String ip=request.getHeader("X-Forwarded-For");
	    if(ip==null || ip.isEmpty() ||"unknown".equalsIgnoreCase(ip)){
	        ip=request.getHeader("Proxy-Client-IP");
	    }
	    if(ip==null || ip.isEmpty() ||"unknown".equalsIgnoreCase(ip)){
	        ip=request.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip==null || ip.isEmpty() ||"unknown".equalsIgnoreCase(ip)){
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
	 * @param ip IP地址
	 * @return
	 */
	public static String queryAddress(String ip){
        if(searcher != null){
            try {

                return format(searcher.search(ip));
            } catch (Exception e) {
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
	 * 查询省份IP归属地
	 * @param ip ip地址
	 * @return
     */
	public static String queryProvinceAddress(String ip){
        if(searcher != null){
            try {
                String region = searcher.search(ip);
                if(region != null && !region.trim().isEmpty()){
                    String [] regionGroup = region.split("\\|");
                    if(regionGroup.length >=5){

                        if(regionGroup[2] != null && !regionGroup[2].equals("0")){
                            return regionGroup[2];
                        }

                        if(regionGroup[0] != null && !regionGroup[0].equals("0")){
                            return regionGroup[0];
                        }
                        if(regionGroup[4] != null && !regionGroup[4].equals("0")){
                            return regionGroup[4];
                        }
                    }

                }

            } catch (Exception e) {
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
	 * 查询是否为内网IP
	 * @param ip ip地址
	 * @return
	 */
	public static boolean isLocalIP(String ip){
	    try {
			InetAddress address = InetAddress.getByName(ip);
			if(address.isSiteLocalAddress() || address.isLoopbackAddress()){//如果为内网IP
				return true;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("查询是否为内网IP错误",e);
	        }
		}
	    return false;
	}
	
	
	
	/**
	 * 查询是否为中国地区(内网IP也显示为中国地区)
	 * @param ip ip地址
	 * @return
	 */
	public static boolean isChinaRegion(String ip){
		if(isLocalIP(ip)){
			return true;
		}

        if(searcher != null){
            try {
                String region = searcher.search(ip);
                if(region != null && !region.trim().isEmpty()){
                    if(Strings.CI.startsWith(region, "中国|")){//判断开始部分是否与二参数相同。不区分大小写
                        return true;
                    }

                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                //	e.printStackTrace();
                if (logger.isErrorEnabled()) {
                    logger.error("IP地址查询错误",e);
                }
            }
        }
        return false;
	}
	
	
	/**
	 * IP归属地格式化
	 * @param ipAddress IP归属地
	 * @return
	 */
	private static String format(String ipAddress){
        if(ipAddress != null && !ipAddress.trim().isEmpty()){
            if(!Strings.CI.startsWith(ipAddress, "中国|0|0|0|")){//判断开始部分是否与二参数相同。不区分大小写
                ipAddress = Strings.CS.replace(ipAddress, "中国|", "");//中国替换成空串
            }


            ipAddress = Strings.CS.replace(ipAddress, "|0", "");//0替换成空串
            LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
            String [] regionGroup = ipAddress.split("\\|");

            for(String info: regionGroup){
                if(info !=null && !info.equals("0")){
                    hashSet.add(info);
                }
            }
            if(hashSet.size() >0){
                return StringUtils.join(hashSet.toArray(), " ");
            }

            //ipAddress = StringUtils.replace(ipAddress, "|0", " ");//0替换成空串
            //ipAddress = StringUtils.replace(ipAddress, "|", " ");//竖线替换成空格
        }
        return "";
	}
}
