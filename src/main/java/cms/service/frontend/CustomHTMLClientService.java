package cms.service.frontend;

import jakarta.servlet.http.HttpServletRequest;


/**
 * 前台自定义HTML服务接口
 */
public interface CustomHTMLClientService {
    /**
     * 获取自定义HTML
     * @param request 请求信息
     * @return
     */
    public String getCustomHTML(HttpServletRequest request);
}
