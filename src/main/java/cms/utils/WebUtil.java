package cms.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.queryString.util.MultiMap;
import org.queryString.util.UrlEncoded;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import cms.bean.user.UserAuthorization;
import cms.web.taglib.Configuration;


/**
 * 
 * @author Administrator
 *
 */
public class WebUtil {
	private static final Logger logger = LogManager.getLogger(WebUtil.class);
	
	//String regexp  = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)"; // 结束条件
	private static Pattern pattern = Pattern.compile("((http[s]{0,1})://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)", Pattern.CASE_INSENSITIVE);
	
	/**默认Cookie有效期30*24*60*60  单位/秒  **/
	public static int cookieMaxAge = 30*24*60*60;
	
	
	/**
     * 添加cookie
     * @param response
     * @param name cookie的名称
     * @param value cookie的值
     * @param maxAge cookie存放的时间(以秒为单位,假如存放三天,即3*24*60*60; 如果值为0,cookie将随浏览器关闭而清除)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
    	addCookie(response,name,value,maxAge,true);
    }
    /**
     * 添加cookie
     * @param response
     * @param name cookie的名称
     * @param value cookie的值
     * @param maxAge cookie存放的时间(以秒为单位,假如存放三天,即3*24*60*60; 如果值为0,cookie将随浏览器关闭而清除)
     * @param httpOnly 标记是否对Cookie使用 HttpOnly 标志。如果设置为true，客户端的 JavaScript 将无法访问Cookie
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge,boolean httpOnly) {
    	
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
        cookie.setHttpOnly(httpOnly);
        cookie.setPath("/");//根目录下的所有程序都可以访问cookie
        if (maxAge>0) cookie.setMaxAge(maxAge);
        
        response.addCookie(cookie); ; 
    }
    
    /**
     * 获取cookie的存活时间
     * @param request
     * @param name cookie的名称
     * @return
     */
    public static int getCookieMaxAge(HttpServletRequest request, String name) {
    	Map<String, Cookie> cookieMap = WebUtil.readCookieMap(request);
        if(cookieMap.containsKey(name)){//如果存在cookie键的名称
            Cookie cookie = (Cookie)cookieMap.get(name);//取得cookie
            return cookie.getMaxAge();
        }else{
            return 0;
        }
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
	 * 断点续传流式下载 适合大文件(以流的形式向页面输出数据)
	 * @param physicalPath 完整路径(路径+文件名称)
	 * @param fileName 文件名称
	 * @param request
	 * @return
	*/
	public static ResponseEntity<StreamingResponseBody> rangeDownloadResponse(String physicalPath, String fileName,String rangeHeader, HttpServletRequest request,HttpServletResponse response) {
		
		Path filePath = Paths.get(physicalPath);
		
        Long fileLength = 0L;
        try {
        	fileLength = FileChannel.open(filePath).size();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
		          logger.error("读取文件长度错误"+physicalPath,e);
		    }
			return ResponseEntity.badRequest().build();
		}
        
        long rangeStart = 0L;
        long rangeEnd = fileLength;
        
        
        if (rangeHeader != null) {
            //example: bytes=0-1023 表示从0取到1023，长度为1024
            String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
            rangeStart = Long.parseLong(ranges[0]);
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]) + 1;
            }
        }
        final long skip = rangeStart;
        final long contentLength = rangeEnd - rangeStart;
		
		
		StreamingResponseBody streamingResponseBody = new StreamingResponseBody() {
	        @Override
	        public void writeTo(OutputStream outputStream) throws IOException { 
	        	try( InputStream inputStream = Files.newInputStream(filePath);
            			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);	
            		){
            		
            		bufferedInputStream.skip(skip);
	                byte[] bytes = new byte[1024];
	                long readSum = 0;
	                int readCount = 0;
	                while ((readCount = bufferedInputStream.read(bytes)) != -1) {
	                    if (readSum + readCount > contentLength) {
	                    	if(contentLength - readSum >=0L){
	                    		outputStream.write(bytes, 0, (int) (contentLength - readSum));
	                    	}
	                    } else {
	                        outputStream.write(bytes, 0, readCount);
	                    }
	                    readSum = readSum + readCount;
	                }
	                outputStream.flush();
	                
            	}
			   
	        }
	    };
	   
	    String header = request.getHeader("User-Agent").toUpperCase();
	    try {
        	//一般来说下载文件是使用201状态码的，但是IE浏览器不支持    手机UC浏览器要设置status = HttpStatus.OK;下载m3u8文件才正常
            if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE") || header.contains("UCBROWSER")) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
                fileName = fileName.replace("+", "%20");    // IE下载文件名空格变+号问题
            }
        } catch (UnsupportedEncodingException e) {
        	if (logger.isErrorEnabled()) {
	            logger.error("转换IE系列浏览器方法错误",e);
	        }
        }
	    
 
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    headers.setContentDispositionFormData("attachment", fileName);
	    headers.setContentLength(fileLength);  
	    headers.add("Accept-Ranges","bytes");
	    headers.add("Content-Range", "bytes " + rangeStart + "-" + (rangeEnd - 1) + "/" + fileLength);
	    if(rangeHeader != null){
	    	 return new ResponseEntity<StreamingResponseBody>(streamingResponseBody, headers, HttpStatus.PARTIAL_CONTENT);// HttpStatus.PARTIAL_CONTENT: 206
	    }else{
	    	return new ResponseEntity<StreamingResponseBody>(streamingResponseBody, headers, HttpStatus.OK);
	    }
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

	/**
	 * 是否为微信浏览器
	 * @param request
	 * @return
	 */
	public static boolean isWeChatBrowser(HttpServletRequest request){
		String userAgent = request.getHeader("user-agent").toLowerCase();
		if(userAgent != null && !"".equals(userAgent.trim()) && userAgent.indexOf("micromessenger")>-1){
			return true;
		}
		return false;
	}
	
	/**
	 * URI参数编码
	 * @param uri
	 * @return
	 */
	public static String parameterEncoded(String uri){
		//截取到等于第二个参数的字符串为止,从左往右
		String uri_before = StringUtils.substringBefore(uri, "?");
		
		//从左往右查到相等的字符开始，保留后边的，不包含等于的字符
		String queryString = StringUtils.substringAfter(uri, "?");

		//组装URL参数
		StringBuffer url_parameter = new StringBuffer("");
		//参数进行URL编码
		if(queryString != null && !"".equals(queryString)){
       		MultiMap<String> values = new MultiMap<String>();  
	       	UrlEncoded.decodeTo(queryString, values, "UTF-8");
	       	Iterator iter = values.entrySet().iterator();  
	       	while(iter.hasNext()){  
	       		Map.Entry e = (Map.Entry)iter.next();  
	       		if(e.getValue() != null){
	       			if(e.getValue() instanceof List){
	       				List<String> valueList = (List)e.getValue();
		       			if(valueList.size() >0){	
		       				for(String value :valueList){
		       					if(value != null && !"".equals(value.trim())){  					
		       						try {
										url_parameter.append("&"+e.getKey()+"="+URLEncoder.encode(value,"utf-8"));
									} catch (UnsupportedEncodingException e1) {
										// TODO Auto-generated catch block
										if (logger.isErrorEnabled()) {
											logger.error("将Url参数编码错误",e1);
								        }
									}
		       					}
		       				}
			       			
			       		}
		       		}else{	
		       			if(e.getValue() instanceof String){      				
		       				String value = e.getValue().toString().trim();
	       					if(value != null && !"".equals(value)){
	       						try {
	       							url_parameter.append("&"+e.getKey()+"="+URLEncoder.encode(value,"utf-8"));
	       						} catch (UnsupportedEncodingException e1) {
									// TODO Auto-generated catch block
	       							if (logger.isErrorEnabled()) {
	       					            logger.error("将Url参数编码错误",e1);
	       					        }
								}
			       			}	
		       			}
		       		}		
	       		}
	         	
	       	   

	       	} 
       	}
		String new_url_parameter = "";
		if(url_parameter.length() >1){
			new_url_parameter = "?";
			//删除第一个&
			new_url_parameter += StringUtils.difference("&", url_parameter.toString());
		}
		return uri_before+new_url_parameter;
		
	}
	
	/**
	 * 获取Header中的访问令牌
	 */
	public static UserAuthorization getAuthorization(HttpServletRequest request){
		//从Header获取
		Enumeration<String> headers = request.getHeaders("Authorization");
		while (headers.hasMoreElements()) { // 通常只有一个（大多数服务器强制执行）
			String value = headers.nextElement();
			String mark = "Bearer";
			if ((value.toLowerCase().startsWith(mark.toLowerCase()))) {
				String authHeaderValue = value.substring(mark.length()).trim();
				if(authHeaderValue != null && !"".equals(authHeaderValue)){
					String tokenArray[] = authHeaderValue.split(",");
					if(tokenArray != null && tokenArray.length ==2){
						return new UserAuthorization(tokenArray[0].trim(),tokenArray[1].trim());	
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取Referer中的域名
	 */
	public static String getRefererDomain(HttpServletRequest request){
		String referer = request.getHeader("referer");
		String domain = "";
		if(referer != null && !"".equals(referer.trim())){
			UriComponents refererComponent = UriComponentsBuilder.fromUriString(referer).build();
			
			//域名 http://localhost:3000/
			UriComponents domainComponent = UriComponentsBuilder.newInstance()
			        .scheme(refererComponent.getScheme())
			        .host(refererComponent.getHost())
			        .port(refererComponent.getPort())
			        .path("/")
			        .build();
			domain = domainComponent.toUriString();
		}
		return domain;
	}
	
	/**
	 * 获取Origin中的域名
	 */
	public static String getOriginDomain(HttpServletRequest request){
		String origin = request.getHeader("Origin");
		String domain = "";
		if(origin != null && !"".equals(origin.trim())){
			UriComponents refererComponent = UriComponentsBuilder.fromUriString(origin).build();
			
			//域名 http://localhost:3000/
			UriComponents domainComponent = UriComponentsBuilder.newInstance()
			        .scheme(refererComponent.getScheme())
			        .host(refererComponent.getHost())
			        .port(refererComponent.getPort())
			        .path("/")
			        .build();
			domain = domainComponent.toUriString();
		}
		return domain;
	}
}
