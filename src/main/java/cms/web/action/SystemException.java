package cms.web.action;

/**
 * 统一异常处理(接收异常参数)
 * @author Administrator
 * 实现父类的构造器 
 * 
 * 使用方法throw new SystemException("错误");
 * 
 * 下面两个类一起用
 * cms.web.action.SystemException
 * cms.web.action.SystemExceptionHandler
 */
public class SystemException extends RuntimeException {
	private static final long serialVersionUID = 581404505964190127L;

	//定义异常代码
	private String key;
	
	private Object[] values;
	
	public SystemException() {
//		super();
	}

	public SystemException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public SystemException(String message) {
		super(message);
	}

	public SystemException(Throwable throwable) {
		super(throwable);
	}

	public SystemException(String message, String key) {
		super(message);
		this.key = key;
	}
	
	public SystemException(String message, String key, Object value) {
		super(message);
		this.key = key;
		this.values = new Object[]{value};
	}
	
	public SystemException(String message, String key, Object[] values ) {
		super(message);
		this.key = key;
		this.values = values;
	}
	
	public String getKey() {
		return key;
	}

	public Object[] getValues() {
		return values;
	}

}
