package cms.config;

import cms.utils.AccessUserThreadLocal;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;


/**
 * 监听器清空ThreadLocal
 * @author Gao
 *
 */
@WebListener
public class ThreadLocalListener implements ServletRequestListener {

	/**
	 * 对销毁客户端进行监听，即当执行request.removeAttribute("XXX")时调用
	 */
	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		//删除当前线程副本
    	AccessUserThreadLocal.removeThreadLocal();
	}

	
	
	
	/**
	 * request初始化，对实现客户端的请求进行监听
	 */
	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		// TODO Auto-generated method stub
		
	}

}
