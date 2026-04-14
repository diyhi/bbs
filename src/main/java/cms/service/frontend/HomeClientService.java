package cms.service.frontend;


import cms.dto.PageView;
import cms.model.user.UserDynamic;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 前台用户中心服务接口
 */
public interface HomeClientService {
    /**
     * 获取用户信息
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getUserInfo(String userName, HttpServletRequest request);

    /**
     * 获取用户动态列表
     * @param userName 用户名称
     * @param page 页码
     * @param request 请求信息
     * @return
     */
    public PageView<UserDynamic> getUserDynamicList(String userName, int page,HttpServletRequest request);

}
