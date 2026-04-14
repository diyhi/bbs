package cms.dto.frontendModule;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteGroupEnum;
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
 * 动态路由组
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DynamicRouteGroupDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8286209638670057674L;

    /** 组枚举值 （例如："TOPIC"）**/
    private String groupEnum;
    /** 资源组名称 **/
    private String name;
    /** 编号 **/
    private Integer code;
    /** 动态路由集合 **/
    private List<DynamicRouteDTO> dynamicRouteList;

    /**
     * 将单个 DynamicRouteEnum 对象转换为 DTO。
     * @param routeGroupEnum 要转换的枚举对象
     * @return 对应的 DTO 对象
     */
    public static DynamicRouteGroupDTO fromEnum(DynamicRouteGroupEnum routeGroupEnum) {
        DynamicRouteGroupDTO dto = new DynamicRouteGroupDTO();
        dto.setGroupEnum(routeGroupEnum.name());
        dto.setName(routeGroupEnum.getName());
        dto.setCode(routeGroupEnum.getCode());
        return dto;
    }

    /**
     * 将 DynamicRouteEnum 的所有枚举常量转换为一个 DTO 列表。
     *
     * @return 包含所有路由数据的 DTO 列表
     */
    public static List<DynamicRouteGroupDTO> fromEnumList() {
        return Arrays.stream(DynamicRouteGroupEnum.values())
                .map(DynamicRouteGroupDTO::fromEnum)
                .collect(Collectors.toList());
    }
}
