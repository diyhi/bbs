package cms.web.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import cms.utils.WebUtil;

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
	
	//后台URL
	private AntPathRequestMatcher[] backstage_filterMatchers = {
	    new AntPathRequestMatcher("/control/**"),
	    new AntPathRequestMatcher("/admin/**")
	};
	
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
		
		
		//这里可以做异常处理
		if(ex instanceof  SystemException) {
			SystemException se = ( SystemException)ex;
	
			//取出key值
			Map<String,Object> model = new HashMap<String,Object>();
		 	model.put("error", se.getMessage());
			model.put("key", se.getKey());
			model.put("value",se.getValues());
			return new ModelAndView("jsp/common/exception", model);
		}else{
			
			
			
			
			//输出日志
			if (logger.isWarnEnabled()) {
	            logger.warn("请求异常",ex);
	        }
			boolean backstage_flag = false;
			//后台URL
			for (AntPathRequestMatcher rm : backstage_filterMatchers) {
				if (rm.matches(request)) { 
					backstage_flag = true;
				}
			}
			boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
			
			if(backstage_flag){//如果是后台请求错误	
				
				
				if (isAjax) {//ajax请求
		    		response.setStatus(400);//设置状态码
		    		try {
						WebUtil.writeToWeb("", "json", response);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (logger.isErrorEnabled()) {
				            logger.error("后台AJAX请求异常",e);
				        }
					}
		    		return null;
		        }else {//普通请求
		        	Map<String,Object> model = new HashMap<String,Object>();
				 	model.put("error", "请求错误");
					return new ModelAndView("jsp/common/exception", model);
		        } 	
			}else{//前台
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
		    		return null;
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
			//	Map<String,Object> model = new HashMap<String,Object>();
			//	return new ModelAndView("templates/ttt/pc/message", model);

				
				
			}
			
		}
		
	//	if(ex instanceof PersistenceException){ 
	//		return new ModelAndView("jsp/common/exception");
    //    } 
	
	//	return null;
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
