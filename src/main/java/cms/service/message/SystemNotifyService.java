package cms.service.message;

import cms.dto.PageView;
import cms.dto.message.SystemNotifyRequest;
import cms.model.message.SystemNotify;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 系统通知服务
 */
public interface SystemNotifyService {

    /**
     * 获取系统通知列表
     * @param page 页码
     */
    public PageView<SystemNotify> getSystemNotifyList(int page);
    /**
     * 添加系统通知
     * @param systemNotifyRequest 系统通知表单
     */
    public void addSystemNotify(SystemNotifyRequest systemNotifyRequest);
    /**
     * 获取修改系统通知界面信息
     * @param systemNotifyId 系统通知Id
     * @return
     */
    public SystemNotify getEditSystemNotifyViewModel(Long systemNotifyId);
    /**
     * 修改系统通知
     * @param systemNotifyRequest 系统通知表单
     */
    public void editSystemNotify(SystemNotifyRequest systemNotifyRequest);
    /**
     * 删除系统通知
     * @param systemNotifyId 系统通知Id
     */
    public void deleteSystemNotify(Long systemNotifyId);
    /**
     * 查询订阅系统通知列表
     * @param page 页码
     * @param id 用户Id
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getSubscriptionSystemNotifyList(int page, Long id, HttpServletRequest request);
    /**
     * 还原订阅系统通知
     * @param userId 用户Id
     * @param subscriptionSystemNotifyId 订阅系统通知Id
     */
    public void reductionSubscriptionSystemNotify(Long userId,String subscriptionSystemNotifyId);
}
