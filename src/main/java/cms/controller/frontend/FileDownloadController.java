package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.service.frontend.FileDownloadService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


/**
 * 文件下载控制器
 */
@Controller
public class FileDownloadController {

    @Resource
    FileDownloadService fileDownloadService;

    /**
     * 文件下载
     * @param jump 重定向参数
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1150600)
    @RequestMapping("/fileDownload")
    @ResponseBody
    public Object fileDownload(String jump,
                          HttpServletRequest request, HttpServletResponse response) throws IOException{
        return fileDownloadService.fileDownload(jump,request,response);
    }


}
