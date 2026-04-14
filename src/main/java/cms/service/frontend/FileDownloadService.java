package cms.service.frontend;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 文件下载服务接口
 */
public interface FileDownloadService {

    /**
     * 文件下载
     * @param jump 重定向参数
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Object fileDownload(String jump,
                                          HttpServletRequest request, HttpServletResponse response) throws IOException;
}
