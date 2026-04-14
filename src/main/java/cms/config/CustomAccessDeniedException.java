package cms.config;

import java.io.Serial;

/**
 * 拒绝访问403异常
 * throw new CustomAccessDeniedException("");
 *
 * 和cms.config.GlobalExceptionHandler一起使用
 */
public class CustomAccessDeniedException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -4737347901881616255L;


    /**
     * 构造函数：处理单个错误信息
     * @param message 错误信息
     */
    public CustomAccessDeniedException(String message) {
        // 调用父类构造函数，并传入 null 作为 message 和 cause，并设置 enableSuppression 和 writableStackTrace 为 false
        super(message, null, false, false);
    }

    /**
     * 重写此方法，阻止堆栈信息被填充
     * 可以避免在控制台打印大量的业务异常堆栈，保持日志整洁
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}