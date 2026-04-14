package cms.service.frontend;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * 前台基本信息服务接口
 */
public interface BaseInfoClientService {
    /**
     * 获取基本信息
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getBaseInfo(HttpServletRequest request);
    /**
     * 获取默认消息
     * @param response 响应信息
     * @return
     */
    public String getMessage(HttpServletResponse response);

}
