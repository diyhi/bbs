package cms.controller.frontendModule;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.frontendModule.DynamicRouteDTO;
import cms.service.frontendModule.DocumentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台文档控制器
 * @author Administrator
 *
 */
@RestController
public class DocumentController {
    @Resource
    DocumentService documentService;
    /**
     * 文档列表
     * @return
     */
    @RequestMapping("/control/frontendDocument/list")
    public RequestResult documentList(){
        List<DynamicRouteDTO> dynamicRouteList = documentService.getAllDynamicRoute();
        return new RequestResult(ResultCode.SUCCESS, dynamicRouteList);
    }

    /**
     * 文档内容
     * @param routeEnumMapper 动态路由枚举
     * @return
     */
    @RequestMapping("/control/frontendDocument/details")
    public RequestResult getDocumentContent(String routeEnumMapper){
        String content = documentService.getDocumentContent(routeEnumMapper);
        return new RequestResult(ResultCode.SUCCESS, content);
    }

}
