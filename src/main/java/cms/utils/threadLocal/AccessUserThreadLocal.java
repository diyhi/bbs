package cms.utils.threadLocal;

import cms.bean.user.AccessUser;

/**
 * 访问用户参数传递
 *
 */
public class AccessUserThreadLocal {
	
	public static ThreadLocal<AccessUser> holder = new ThreadLocal<AccessUser>(); 

	/**
	 * 设置参数
	 * @param accessUser 用户
	 */
	public static void set(AccessUser accessUser){
		holder.set(accessUser);
	}

	public static AccessUser get() {
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
