package cms.web.action;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriUtils;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.utils.JsonUtils;
import cms.utils.WebUtil;
import cms.web.taglib.Configuration;

/**
 * 统一异常异常和日志处理
 * @author Administrator
 * 下面两个类一起用
 * cms.web.action.SystemException
 * cms.web.action.SystemExceptionHandler
 */
public class SystemExceptionHandler implements HandlerExceptionResolver,Ordered{
	//定义日志
	private static final Logger logger = LogManager.getLogger(SystemExceptionHandler.class);
	
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	

	
	public ModelAndView resolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
		
		
		/**
		暂时屏蔽掉该异常吧。如下：catch (IOException ex) {
		    String name = ex.getClass().getName();
		    if(name==null || name.indexOf("ClientAbortException")<0){
		         // write log....
		    }
		}**/
		

		
		/**
		if(ex instanceof  java.net.SocketException) {
			//ClientAbortException:  java.net.SocketException: Software caused connection abort: socket write error
			String name = ex.getClass().getName();
		    if(name==null || name.indexOf("ClientAbortException")<0){
		         return null;
		    }
			
		}**/
		
		//捕获oauth2抛出的异常
		if(ex instanceof  InvalidGrantException || ex instanceof  InvalidTokenException) {	
			Map<String,Object> error = new HashMap<String,Object>();
			error.put("userInfo", ex.getMessage());
			try {
				WebUtil.writeToWeb(JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error)), "json", response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("统一异常处理转换JSON",e);
		        }
			}
			
			//return null;//返回null将不会拦截异常，其它处理器可以继续处理该异常
			return new ModelAndView();// 返回空的ModelAndView以阻止异常继续被其它处理器捕获
		}
		
		
		
		
		//这里可以做异常处理
		if(ex instanceof  SystemException) {
			SystemException se = ( SystemException)ex;
    		try {
    			Map<String,Object> error = new HashMap<String,Object>();
    			error.put("exception", se.getMessage());
    			//向Header头写入信息   方法返回ResponseEntity<byte[]>类型抛出SystemException错误时供前端使用
				response.setHeader("exception", UriUtils.encode(se.getMessage(),"UTF-8"));//不支持中文参数   用UriUtils.encode代替URLEncoder.encode,解决空格转换成+号的问题      
					
				WebUtil.writeToWeb(JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error)), "json", response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("统一异常处理转换JSON",e);
		        }
			}
			//return null;//返回null将不会拦截异常，其它处理器可以继续处理该异常
			return new ModelAndView();// 返回空的ModelAndView以阻止异常继续被其它处理器捕获
		}
			
		//输出日志
		if (logger.isWarnEnabled()) {
			String url = Configuration.getUrl(request)+Configuration.baseURI(request.getRequestURI(), request.getContextPath())+(request.getQueryString() != null && !"".equals(request.getQueryString().trim()) ? "?"+request.getQueryString() :"");
			
            LocalDateTime currentTime  = LocalDateTime.now();
            logger.warn(dateTimeFormatter.format(currentTime)+" 请求异常 "+url,ex);
        }
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		if (isAjax) {//ajax请求
    		response.setStatus(400);//设置状态码
    		try {
				WebUtil.writeToWeb("", "json", response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("前台AJAX请求异常",e);
		        }
			}
    		
			//return null;//返回null将不会拦截异常，其它处理器可以继续处理该异常
			return new ModelAndView();// 返回空的ModelAndView以阻止异常继续被其它处理器捕获
        }else {//普通请求
        	response.setContentType("text/html;charset=UTF-8");
        	StringBuffer html = new StringBuffer("");
        	html.append("<html>");
        	html.append("<head>");
        	html.append("<title>请求错误</title>");
        	html.append("</head>");
        	html.append("<body>");
        	html.append("</body>");
        	html.append("</html>");
        
    		byte[] bytes = html.toString().getBytes();
    		try {
				response.getOutputStream().write(bytes);//字节流和字符流同时使用  getOutputStream和getWriter这两个方法互相排斥，调用了其中的任何一个方法后，就不能再调用另一方法。会抛异常。
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("前台普通请求异常",e);
		        }
			}
        	
        	
        	
        	/**
        	response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = null;
			try {
				out = response.getWriter();
				out.println("<html>");
                out.println("<head>");
                out.println("<title>请求错误</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("</body>");
                out.println("</html>");
			} catch (IOException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("前台普通请求异常",e);
		        }
			}finally {
				if(out != null){
					out.close();
				}
                
            }**/
        	
        	
        	return new ModelAndView();
        } 	
		
	}

	/**
	 * 异常处理优先级
	 * 
	 * 在使用Spring MVC时会遇到需要自己捕获异常并处理的情况。一般可以使用HandlerExceptionResolver去处理。
	 * 但是默认情况下，Spring MVC或自己注入3个HandlerExceptionResolver如下：
	 * 	org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver,
	 *	org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver,
	 *	org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
	 * 这时会按顺序调用HandlerExceptionResolver处理能够处理的异常，如果没有处理就返回null，这时会继续调用下一个，如果其中一个返回了ModelAndView则后面的将不会再调用
	 * 我们通过bean配置的HandlerExceptionResolver将会在最后一个执行，如果前面某个HandlerExceptionResolver已经返回ModelAndView了，就调用不到了.
	 * 比如参数格式不正确时的400的错误就被DefaultHandlerExceptionResolver处理了。解决办法，调整HandlerExceptionResolver执行顺序，
	 * 可以通过实现接口org.springframework.core.Ordered 来解决。默认的三个Order为Integer.MAX_VALUE，所以我们只有比它小就可以了如Integer.MIN_VALUE。
	 */
	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	} 
} 
