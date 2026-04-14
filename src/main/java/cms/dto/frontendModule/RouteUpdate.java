package cms.dto.frontendModule;

import cms.annotation.DynamicRouteEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 路由更新
 */
@Getter
@Setter
public class RouteUpdate implements Serializable {
    @Serial
    private static final long serialVersionUID = -7844674973747253515L;

    /** 旧URL路径 **/
    private String oldUrl;
    /** 旧请求方法 **/
    private String oldHttpMethod;
    /** 新路由枚举 **/
    private DynamicRouteEnum newDynamicRouteEnum;
    /** 新URL路径 **/
    private String newUrl;
    /** 新请求方法 **/
    private String newHttpMethod;

    public RouteUpdate(){};
    public RouteUpdate(String oldUrl, String oldHttpMethod, DynamicRouteEnum newDynamicRouteEnum, String newUrl, String newHttpMethod) {
        this.oldUrl = oldUrl;
        this.oldHttpMethod = oldHttpMethod;
        this.newDynamicRouteEnum = newDynamicRouteEnum;
        this.newUrl = newUrl;
        this.newHttpMethod = newHttpMethod;
    }
}
