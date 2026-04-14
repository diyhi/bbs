package cms.model.frontendModule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 前台API
 */
@Getter
@Setter
@Entity
public class FrontendApi implements Serializable {
    @Serial
    private static final long serialVersionUID = 7272922109327766513L;

    /** Id **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /** 名称 **/
    @Column(length=100)
    private String name;
    /** URL路径 **/
    @Column(length=100)
    private String url;
    /** 请求方法 GET POST 空值为任意类型 **/
    @Column(length=30)
    private String httpMethod;

    /** 访问需要登录 **/
    private Boolean requiresLogin = false;

    /** 类型 1.默认  2.自定义    由Controller类上注解指定映射路径的为默认；由数据库配置映射路径的为自定义
    private Integer type = 1; **/
    /** 映射路由枚举值 **/
    @Column(length=100)
    private String routeEnumMapper;
    /** 映射路由枚举名称 **/
    @Transient
    private String routeEnumName;

    /** 功能参数配置对象 例如： ConfigCustomHtml、ConfigImageAd等**/
    @Transient
    private Object configObject;
    /** 存储 configData 的JSON格式内容 **/
    @Lob
    private String configData;


    /**
     * 获取功能参数配置对象
     * 例如 ConfigCustomHtml configCustomHtml = frontendApi.getConfigObject(ConfigCustomHtml.class);
     * @param type 类的类型
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfigObject(Class<T> type) {
        if (this.configObject == null) {
            return null; // 没有实例时返回 null
        }
        // 运行时检查，防止 ClassCastException
        if (!type.isInstance(this.configObject)) {
            throw new ClassCastException("预期类型为：" + type.getName() +
                    "，但实际找到的类型为：" + this.configObject.getClass().getName() +
                    "。请检查您的 API 配置。");
        }
        return (T) this.configObject;
    }
}
