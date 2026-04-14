package cms.dto.frontendModule;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serial;
import java.io.Serializable;

/**
 * API接口元数据
 */
@Getter
@Setter
public class ApiMetadata implements Serializable {
    @Serial
    private static final long serialVersionUID = -3839956555454772149L;
    /** Controller的@RequestMapping路径 例如 value="/column/{columnId}" **/
    private final String[] paths;
    /** Controller的@RequestMapping请求方式  例如 method = RequestMethod.GET **/
    private final RequestMethod[] methods;
    /** 映射路由枚举值 **/
    private final String routeEnumMapper;
    /** 映射路由枚举名称 **/
    private final String routeEnumName;

    public ApiMetadata(String[] paths, RequestMethod[] methods,String routeEnumMapper,String routeEnumName) {
        this.paths = paths;
        this.methods = methods;
        this.routeEnumMapper = routeEnumMapper;
        this.routeEnumName = routeEnumName;
    }
}
