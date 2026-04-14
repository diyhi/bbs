package cms.service.frontendModule;

import cms.dto.frontendModule.DynamicRouteDTO;

import java.util.List;

/**
 * 前台文档服务
 */
public interface DocumentService {
    /**
     * 获取所有动态路由
     * @return
     */
    public List<DynamicRouteDTO> getAllDynamicRoute();
    /**
     * 获取文档内容
     * @param routeEnumMapper 动态路由枚举
     * @return
     */
    public String getDocumentContent(String routeEnumMapper);
}
