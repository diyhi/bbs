package cms.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cms.dto.PageIndex;
import cms.dto.user.UserAuthorization;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;





/**
 * Web工具
 * @author Administrator
 *
 */
public class WebUtil {
	private static final Logger logger = LogManager.getLogger(WebUtil.class);
	
	//String regexp  = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)"; // 结束条件
	private static final Pattern pattern = Pattern.compile("((http[s]{0,1})://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)", Pattern.CASE_INSENSITIVE);
		
	/**默认Cookie有效期30*24*60*60  单位/秒  **/
	public static int cookieMaxAge = 30*24*60*60;


    //虚拟路径
    private static String path = "";


    /**
     * 获取网站URL
     * @param request 请求信息
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
     * /api
     * @param _path 虚拟路径
     */
    public static synchronized void setPath(String _path) {
        path = _path;
    }

    /**
     * 将文本用UTF8格式解码
     * @param text 文本
     * @return
     */
    public static String decode(String text) {
        return URLDecoder.decode(text, StandardCharsets.UTF_8 );
    }

    /**
     * 截取资源标识
     * @param uri 统一资源标识符
     * @param path 虚拟路径
     * @return
     */
    public static String baseURI(String uri,String path){
        if(uri != null && !uri.isEmpty()){
            if(path != null && !path.isEmpty()){
                if(uri.length() > path.length() && uri.startsWith(path)){
                    uri = uri.substring(path.length());
                }
            }
            //删除左斜杆
            if(uri.startsWith("/")){
                uri = uri.substring(1);
            }
            return uri;
        }
        return "";
    }




    /**
     * 添加cookie
     * @param request 请求信息
     * @param response 响应信息
     * @param name cookie的名称
     * @param value cookie的值
     * @param maxAge cookie存放的时间(以秒为单位,假如存放三天,即3*24*60*60; 如果值为0,cookie将随浏览器关闭而清除)
     */
    public static void addCookie(HttpServletRequest request,HttpServletResponse response, String name, String value, int maxAge) {
    	if(value != null && !value.trim().isEmpty()){

            value = URLEncoder.encode(value,StandardCharsets.UTF_8 );
            Cookie cookie = new Cookie(name, value);
            //  cookie.setHttpOnly(httpOnly);
            cookie.setPath("/");//根目录下的所有程序都可以访问cookie
            if (maxAge>0) cookie.setMaxAge(maxAge);

            cookie.setSecure(Strings.CI.startsWith(request.getScheme(), "https") || request.isSecure());


            response.addCookie(cookie);
    	}
    	

    }
    /**
     * 添加cookie
     * @param request 请求信息
     * @param response 响应信息
     * @param name cookie的名称
     * @param value cookie的值
     * @param maxAge cookie存放的时间(以秒为单位,假如存放三天,即3*24*60*60; 如果值为0,cookie将随浏览器关闭而清除)
     * @param httpOnly 标记是否对Cookie使用 HttpOnly 标志。如果设置为true，客户端的 JavaScript 将无法访问Cookie
     */
    public static void addCookie(HttpServletRequest request,HttpServletResponse response, String name, String value, int maxAge,boolean httpOnly) {
 
    	//https://web.dev/same-site-same-origin/
    	//同站不设置sameSite参数仍然可以跨域。例如 test.com:3000和test.com:8080
    	
    	final ResponseCookie responseCookie = ResponseCookie
    	        .from(name, value)
    	      //  .secure(true)//是否只在 HTTPS协议发送的请求才允许Cookie传输给服务端
    	        .httpOnly(httpOnly)
    	        .path("/")
    	        .maxAge(maxAge>0 ? maxAge : -1)//负值表示没有“Max-Age”属性
    	        .secure(Strings.CI.startsWith(request.getScheme(), "https") || request.isSecure())
    	     //   .sameSite("None")//Strict：严格模式，必须同站请求才能发送cookie。 Lax：(relax缩写)宽松模式，安全的跨站请求可以发送cookie。 None：显示地禁止SameSite限制，必须配合Secure一起使用
    	        .build();
    	
    	response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }
    /**
     * 获取cookie的存活时间
     * @param request 请求信息
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
     * @param request 请求信息
     * @param name cookie的名称
     * @return
     */
    public static String getCookieByName(HttpServletRequest request, String name) {
    	Map<String, Cookie> cookieMap = WebUtil.readCookieMap(request);
        if(cookieMap.containsKey(name)){//如果存在cookie键的名称
            Cookie cookie = (Cookie)cookieMap.get(name);//取得cookie
            String value = cookie.getValue();//返回cookie的值
            if(value != null && !value.trim().isEmpty()){

                return URLDecoder.decode(value,StandardCharsets.UTF_8);

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
                    if(value != null && !value.trim().isEmpty()){
                        return  URLDecoder.decode(value,StandardCharsets.UTF_8);
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
	 * @param response	响应信息
	 * @throws IOException
	 */
	public static void writeToWeb(String message, String type, HttpServletResponse response) throws IOException{
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0); 
		response.setContentType("application/" + type +"; charset=utf-8");
		response.getWriter().write(message);
		response.getWriter().close();

		
	}

	
	/**
	 * 提交数据方式
	 * @param request 请求信息
	 * @return true: Ajax方式  false: Form表单提交方式
	 */
	public static boolean submitDataMode(HttpServletRequest request){
		boolean isAjax = false;//是否以Ajax方式提交数据
		//读取报文
		String requestType = request.getHeader("X-Requested-With");  
	
		if(requestType != null && !requestType.trim().isEmpty()){//重复添加时会有多个值
			String[] requestType_arr =  requestType.split(",");
            for(String value : requestType_arr){
                if(value != null && "XMLHttpRequest".equalsIgnoreCase(value.trim())){
                    return true;
                }
            }
		}
		return isAjax;
	}
	
	/**
	 * 向页面输出数据
	 * @param body 字节
	 * @param fileName 文件名称
	 * @param request 响应信息
	 * @return
	 */
	public static ResponseEntity<byte[]> downloadResponse(byte[] body, String fileName, HttpServletRequest request) {

        String header = request.getHeader("User-Agent").toUpperCase();
        HttpStatus status = HttpStatus.CREATED;
        

        //一般来说下载文件是使用201状态码的，但是IE浏览器不支持    手机UC浏览器要设置status = HttpStatus.OK;下载m3u8文件才正常
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE") || header.contains("UCBROWSER")) {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8 );
            fileName = fileName.replace("+", "%20");    // IE下载文件名空格变+号问题
            status = HttpStatus.OK;
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
	 * @param request 响应信息
	 * @return
	*/
	public static ResponseEntity<StreamingResponseBody> rangeDownloadResponse(String physicalPath, String fileName,String rangeHeader, HttpServletRequest request,HttpServletResponse response) {
		
		Path filePath = Paths.get(physicalPath);
		
        long fileLength = 0L;
        try (FileChannel fileChannel = FileChannel.open(filePath)) {
            fileLength = fileChannel.size();
        } catch (IOException e) {
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
	                        outputStream.write(bytes, 0, (int) (contentLength - readSum));
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

        //一般来说下载文件是使用201状态码的，但是IE浏览器不支持    手机UC浏览器要设置status = HttpStatus.OK;下载m3u8文件才正常
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE") || header.contains("UCBROWSER")) {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            fileName = fileName.replace("+", "%20");    // IE下载文件名空格变+号问题
        }

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(contentLength);
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
	 * @param request 请求信息
	 * @return
	 */
	public static boolean isWeChatBrowser(HttpServletRequest request){
		String userAgent = request.getHeader("user-agent").toLowerCase();
		if(!userAgent.trim().isEmpty() && userAgent.indexOf("micromessenger")>-1){
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
        if(queryString != null && !queryString.trim().isEmpty()){
            MultiMap<String> values = new MultiMap<String>();
            UrlEncoded.decodeTo(queryString, values, StandardCharsets.UTF_8);
            Iterator iter = values.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry e = (Map.Entry)iter.next();
                if(e.getValue() != null){
                    if(e.getValue() instanceof List){
                        List<String> valueList = (List)e.getValue();
                        if(valueList.size() >0){
                            for(String value :valueList){
                                if(value != null && !value.trim().isEmpty()){

                                    url_parameter.append("&"+e.getKey()+"="+URLEncoder.encode(value, StandardCharsets.UTF_8));

                                }
                            }

                        }
                    }else{
                        if(e.getValue() instanceof String){
                            String value = e.getValue().toString().trim();
                            if(!value.isEmpty()){
                                url_parameter.append("&"+e.getKey()+"="+URLEncoder.encode(value, StandardCharsets.UTF_8));

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
     * @param request 请求信息
     * @return
     */
	public static UserAuthorization getAuthorization(HttpServletRequest request){
		//从Header获取
		Enumeration<String> headers = request.getHeaders("Authorization");
		while (headers.hasMoreElements()) { // 通常只有一个（大多数服务器强制执行）
			String value = headers.nextElement();
			String mark = "Bearer";
			if ((value.toLowerCase().startsWith(mark.toLowerCase()))) {
				String authHeaderValue = value.substring(mark.length()).trim();
				if(!authHeaderValue.isEmpty()){
					String[] tokenArray = authHeaderValue.split(",");
					if(tokenArray.length ==2){
						return new UserAuthorization(tokenArray[0].trim(),tokenArray[1].trim());
					}
				}
			}
		}
		return null;
	}

    /**
     * 获取Referer中的域名
     * @param request 请求信息
     * @return
     */
	public static String getRefererDomain(HttpServletRequest request){
		String referer = request.getHeader("referer");
		if(referer != null && !referer.trim().isEmpty()){
			UriComponents refererComponent = UriComponentsBuilder.fromUriString(referer).build();
			
			//域名 http://localhost:3000/
			UriComponents domainComponent = UriComponentsBuilder.newInstance()
			        .scheme(refererComponent.getScheme())
			        .host(refererComponent.getHost())
			        .port(refererComponent.getPort())
			        .path("/")
			        .build();
            return domainComponent.toUriString();
		}
		return "";
	}

    /**
     * 获取Origin中的域名
     * @param request 请求信息
     * @return
     */
	public static String getOriginDomain(HttpServletRequest request){
		String origin = request.getHeader("Origin");
		String domain = "";
		if(origin != null && !origin.trim().isEmpty()){
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
	
	/**
	 * 获取前端地址
	 * @param request 请求信息
	 * @return
	 */
	public static String domain(HttpServletRequest request){  
		String domain = getOriginDomain(request);
		if(domain == null || domain.trim().isEmpty()){
			domain = getUrl(request);
		}
		
		return domain;
	}

    /**
     *
     * @param viewpagecount 页码显示总数
     * @param currenPage 当前页
     * @param totalpage 总页数
     * @return
     */
    public static PageIndex getPageIndex(long viewpagecount, int currenPage, long totalpage){
        long startpage = currenPage-(viewpagecount%2==0? viewpagecount/2-1 : viewpagecount/2);
        long endpage = currenPage+viewpagecount/2;
        if(startpage<1){
            startpage = 1;
            if(totalpage>=viewpagecount) endpage = viewpagecount;
            else endpage = totalpage;
        }
        if(endpage>totalpage){
            endpage = totalpage;
            if((endpage-viewpagecount)>0) startpage = endpage-viewpagecount+1;
            else startpage = 1;
        }
        return new PageIndex(startpage, endpage);
    }
}
