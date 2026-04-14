package cms.service.frontend.impl;

import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.follow.*;
import cms.component.frontendModule.FrontendApiComponent;
import cms.model.frontendModule.ConfigCustomHtml;
import cms.model.frontendModule.FrontendApi;
import cms.service.frontend.CustomHTMLClientService;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;


/**
 * 前台自定义HTML服务
 */
@Service
public class CustomHTMLClientServiceImpl implements CustomHTMLClientService {
    @Resource
    FrontendApiComponent frontendApiComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;

    /**
     * 获取自定义HTML
     * @param request 请求信息
     * @return
     */
    public String getCustomHTML(HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigCustomHtml configCustomHtml) {
            String fileServerAddress = fileComponent.fileServerAddress(request);
            return textFilterComponent.processFilePath(configCustomHtml.getContent(),"frontendApi", fileServerAddress,false);

        }
        return null;
    }


}
