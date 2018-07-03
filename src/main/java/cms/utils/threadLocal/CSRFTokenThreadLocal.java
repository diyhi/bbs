package cms.utils.threadLocal;


/**
 * CSRF令牌参数传递
 *
 */
public class CSRFTokenThreadLocal {
	
	public static ThreadLocal<String> holder = new ThreadLocal<String>(); 

	/**
	 * 设置参数
	 * @param token 令牌
	 */
	public static void set(String token){
		holder.set(token);
	}

	public static String get() {
		return holder.get();
	}
	/**
	 * cms.web.filter.TempletesInterceptor中删除
	 */
	public static void removeThreadLocal() {
		//一定要清理，否则就是线程不安全的，因为服务器都是有线程池的。 
		set(null); 
		holder.remove();
	}
}
