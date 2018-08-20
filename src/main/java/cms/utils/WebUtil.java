package cms.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


/**
 * 
 * @author Administrator
 *
 */
public class WebUtil {
	private static final Logger logger = LogManager.getLogger(WebUtil.class);
	
	//String regexp  = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)"; // 结束条件
	private static Pattern pattern = Pattern.compile("((http[s]{0,1})://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)", Pattern.CASE_INSENSITIVE);
		
	
    /**
     * 添加cookie
     * @param response
     * @param name cookie的名称
     * @param value cookie的值
     * @param maxAge cookie存放的时间(以秒为单位,假如存放三天,即3*24*60*60; 如果值为0,cookie将随浏览器关闭而清除)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
    	if(value != null && !"".equals(value.trim())){
    		try {
    			value = URLEncoder.encode(value,"utf-8");
    		} catch (UnsupportedEncodingException e) {
    			// TODO Auto-generated catch block
    		//	e.printStackTrace();
    			if (logger.isErrorEnabled()) {
    	            logger.error("添加cookie错误",e);
    	        }
    		}
    	}
    	
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");//根目录下的所有程序都可以访问cookie
     //   cookie.setHttpOnly(true);
        if (maxAge>0) cookie.setMaxAge(maxAge);
        response.addCookie(cookie); ; 
    }
    
    /**
     * 获取cookie的值
     * @param request
     * @param name cookie的名称
     * @return
     */
    public static String getCookieByName(HttpServletRequest request, String name) {
    	Map<String, Cookie> cookieMap = WebUtil.readCookieMap(request);
        if(cookieMap.containsKey(name)){//如果存在cookie键的名称
            Cookie cookie = (Cookie)cookieMap.get(name);//取得cookie
            String value = cookie.getValue();//返回cookie的值
            if(value != null && !"".equals(value.trim())){
            	try {
            		return URLDecoder.decode(value,"utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("获取cookie的值错误",e);
			        }
				}
            }
            
            
            
            return value;
        }else{
            return null;
        }
    }
    /**
     * 获取cookie的值
     * @param cookies   request.getCookies()对象
     * @param name cookie的名称
     * @return
     */
    public static String getCookieByName(Cookie[] cookies, String name) {
    	if (null != cookies) {//如果Cookie不为空
            for (int i = 0; i < cookies.length; i++) {//循环所有Cookie
            	if(name.equals(cookies[i].getName())){
            		String value = cookies[i].getValue();
                    if(value != null && !"".equals(value.trim())){
                    	try {
                    		return  URLDecoder.decode(value,"utf-8");
        				} catch (UnsupportedEncodingException e) {
        					// TODO Auto-generated catch block
        				//	e.printStackTrace();
        					if (logger.isErrorEnabled()) {
        			            logger.error("获取cookie的值错误",e);
        			        }
        				}
                    }
                    return value;
            	//	return cookies[i].getValue();
            	}
            	
            }
        }
    	return null;
    }
    
    /**
	 * 删除Cookie
	 */
	public static void deleteCookie(HttpServletResponse response, String name){
		Cookie cookie = new Cookie(name, null); 
		//设置为0为立即删除该Cookie 
		cookie.setMaxAge(0); 
		//删除指定路径上的Cookie，不设置该路径，默认为删除当前路径Cookie 
		cookie.setPath("/"); 
		response.addCookie(cookie); 



	}
    
    protected static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        //从request范围读取所有Cookie
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {//如果Cookie不为空
            for (int i = 0; i < cookies.length; i++) {//循环所有Cookie
            	//将Cookie放进Map集合里(cookie名称,cookie)
                cookieMap.put(cookies[i].getName(), cookies[i]);
            }
        }
        return cookieMap;
    }
    
    
    
    
    /**
	 * 本方法封装了往前台设置的header,contentType等信息
	 * @param message	需要传给前台的数据
	 * @param type		指定传给前台的数据格式,如"html","json"等
	 * @param response	HttpServletResponse对象
	 * @throws IOException
	 */
	public static void writeToWeb(String message, String type, HttpServletResponse response) throws IOException{
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0); 
		response.setContentType("text/" + type +"; charset=utf-8");
		response.getWriter().write(message);
		response.getWriter().close();

		
	}
	
	/**
	 * 提交数据方式
	 * @param request HttpServletRequest对象
	 * @return true: Ajax方式  false: Form表单提交方式
	 */
	public static boolean submitDataMode(HttpServletRequest request){
		boolean isAjax = false;//是否以Ajax方式提交数据
		//读取报文
		String requestType = request.getHeader("X-Requested-With");  
	
		if(requestType != null && !"".equals(requestType.trim())){//重复添加时会有多个值
			String requestType_arr[] =  requestType.split(",");
			if(requestType_arr != null && requestType_arr.length >0){
				for(String value : requestType_arr){
					if(value != null && "XMLHttpRequest".equalsIgnoreCase(value.trim())){
						isAjax = true;
						break;
					}
				}
			}
		}
		
		
		//判断是否ajax方式提交
	//	if(requestType != null && "XMLHttpRequest".equals(requestType)){
	//		isAjax = true;
	//	}
		
		return isAjax;
	}
	
	
	public static ResponseEntity<byte[]> downloadResponse(byte[] body, String fileName, HttpServletRequest request) {

        String header = request.getHeader("User-Agent").toUpperCase();
        HttpStatus status = HttpStatus.CREATED;
        try {
        	//一般来说下载文件是使用201状态码的，但是IE浏览器不支持
            if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
                fileName = fileName.replace("+", "%20");    // IE下载文件名空格变+号问题
                status = HttpStatus.OK;
            }
        } catch (UnsupportedEncodingException e) {
        	if (logger.isErrorEnabled()) {
	            logger.error("转换IE系列浏览器方法错误",e);
	        }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(body.length);

        
        return new ResponseEntity<byte[]>(body, headers, status);
    }
	
	
	
	/**
	 * 文本中的URL转超链接
	 * @param text 文本
	 * @return
	 */
	public static String urlToHyperlink(String text){
		// url的正则表达式
        Matcher matcher = pattern.matcher(text);
        
        String resultText = "";// （临时变量，保存转换后的文本）
        int lastEnd = 0;// 保存每个链接最后一行的下标
        
        while(matcher.find()){
        	resultText += text.substring(lastEnd, matcher.start());
        	resultText += "<a target=\"_blank\" href=\"" + matcher.group() + "\">" + matcher.group() + "</a>";
        	lastEnd = matcher.end();
        }
        resultText += text.substring(lastEnd);
        return resultText;
	}

	
}
