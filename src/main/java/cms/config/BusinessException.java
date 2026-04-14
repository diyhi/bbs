package cms.config;

import java.io.Serial;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 业务异常
 * throw new BusinessException("刷新令牌不能为空");
 * throw new BusinessException(errors).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
 * if (!errors.isEmpty()) {
 *     //创建异常实例
 *     BusinessException ex = new BusinessException(errors);
 *     //根据业务逻辑判断是否添加验证码 Key
 *     if (isCaptcha) {
 *         ex.addData("captchaKey", UUIDUtil.getUUID32());
 *     }
 *     //抛出异常
 *     throw ex;
 * }
 * 和cms.config.GlobalExceptionHandler一起使用
 */
public class BusinessException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 3908019285144313092L;


    // 存储多个验证错误  key:字段名，value错误信息
    private final Map<String, String> errors;
    // 扩展动态字段 例如captchaKey 或其他动态字段
    private final Map<String, Object> extraData = new HashMap<>();

    /**
     * 构造函数：处理单个错误信息
     * @param message 错误信息
     */
    public BusinessException(String message) {
        // 调用父类构造函数，并传入 null 作为 message 和 cause，并设置 enableSuppression 和 writableStackTrace 为 false
        super(message, null, false, false);
        this.errors = Collections.singletonMap("exception", message);
    }

    /**
     * 构造函数：处理多个验证错误
     * @param errors 包含所有错误信息的Map
     */
    public BusinessException(Map<String, String> errors) {
        super("业务多个验证错误", null, false, false);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public BusinessException addData(String key, Object value) {
        this.extraData.put(key, value);
        return this; // 返回对象自身，支持链式写法
    }
    public BusinessException addDataIf(boolean condition, String key, Object value) {
        if (condition) {
            this.extraData.put(key, value);
        }
        return this;
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
