package cms.dto.frontendModule;

import cms.annotation.DynamicRouteEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 动态路由
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DynamicRouteDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -301391366446131594L;

    /** 映射路由枚举值 （例如："DEFAULT_1000100"）**/
    private String routeEnumMapper;
    /**
     * 路由名称
     **/
    private String name;

    /**
     * 路由类型 1：默认  2.自定义
     **/
    private Integer type;

    /**
     * 将单个 DynamicRouteEnum 对象转换为 DTO。
     *
     * @param routeEnum 要转换的枚举对象
     * @return 对应的 DTO 对象
     */
    public static DynamicRouteDTO fromEnum(DynamicRouteEnum routeEnum) {
        DynamicRouteDTO dto = new DynamicRouteDTO();
        dto.setRouteEnumMapper(routeEnum.name());
        dto.setName(routeEnum.getName());
        dto.setType(routeEnum.getType());
        return dto;
    }

    /**
     * 将 DynamicRouteEnum 的所有枚举常量转换为一个 DTO 列表。
     *
     * @return 包含所有路由数据的 DTO 列表
     */
    public static List<DynamicRouteDTO> fromEnumList() {
        return Arrays.stream(DynamicRouteEnum.values())
                .map(DynamicRouteDTO::fromEnum)
                .collect(Collectors.toList());
    }
}
