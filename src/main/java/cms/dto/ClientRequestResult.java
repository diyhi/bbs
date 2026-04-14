package cms.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 前台返回请求结果
 *
 * ClientRequestResult.success()
 *
 * Map<String,String> errors = new HashMap<String,String>();
 * ClientRequestResult.fail(errors)
 *
 * Map<String, Object> returnValue = new HashMap<>();
 * new ClientRequestResult(true).addAll(returnValue);
 *
 *
 * @author Gao
 *
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY) // 核心：null 或空集合都不序列化
public class ClientRequestResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -5680448710249308794L;

    private boolean success;
    private Map<String, String> error;
    // 存放扩展字段（如 captchaKey, token, userId 等）
    private Map<String, Object> extraData = new HashMap<>();

    // 静态构造方法：成功
    public static ClientRequestResult success() {
        ClientRequestResult result = new ClientRequestResult();
        result.setSuccess(true);
        return result;
    }

    // 静态构造方法：失败
    public static ClientRequestResult fail(Map<String, String> error) {
        ClientRequestResult result = new ClientRequestResult();
        result.setSuccess(false);
        result.setError(error);
        return result;
    }

    // 链式调用：添加扩展数据
    public ClientRequestResult add(String key, Object value) {
        this.extraData.put(key, value);
        return this;
    }
    public ClientRequestResult addIf(boolean condition, String key, Object value) {
        if (condition) {
            this.extraData.put(key, value);
        }
        return this;
    }
    /**
     * 链式调用：添加Map扩展数据
     * 将传入的 Map 全部平铺到当前对象的根部
     */
    public ClientRequestResult addAll(Map<String, ?> data) {
        if (data != null) {
            this.extraData.putAll(data);
        }
        return this;
    }

    @JsonAnyGetter // 将 extraData 里的 key-value 平铺到 JSON 根部
    public Map<String, Object> getExtraData() {
        return extraData;
    }

    // 私有化构造，强制使用静态方法
    private ClientRequestResult() {}
}
