package cms.service.frontend;


import cms.dto.PageView;
import cms.dto.message.UnreadMessage;
import cms.model.message.PrivateMessage;
import cms.model.message.Remind;
import cms.model.message.SubscriptionSystemNotify;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;


/**
 * 前台消息服务接口
 */
public interface MessageClientService {

    /**
     * 获取私信列表
     * @param page 页码
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<PrivateMessage> getPrivateMessageList(int page, String fileServerAddress);
    /**
     * 获取私信对话列表
     * @param page 页码
     * @param friendUserName 对方用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getPrivateMessageChatList(Integer page, String friendUserName,String fileServerAddress);
    /**
     * 获取添加私信界面信息
     * @param friendUserName 对方用户名称
     * @return
     */
    public Map<String,Object> getAddPrivateMessageViewModel(String friendUserName);
    /**
     * 添加私信
     * @param friendUserName 对方用户名称
     * @param messageContent 消息内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     */
    public void addPrivateMessage(String friendUserName, String messageContent,String captchaKey, String captchaValue);
    /**
     * 删除私信
     * @param friendUserName 对方用户名称
     */
    public void deletePrivateMessage(String friendUserName);
    /**
     * 获取系统通知列表
     * @param page 页码
     * @return
     */
    public PageView<SubscriptionSystemNotify> getSystemNotifyList(int page);
    /**
     * 删除系统通知
     * @param subscriptionSystemNotifyId 订阅系统通知Id
     */
    public void deleteSystemNotify(String subscriptionSystemNotifyId);
    /**
     * 全部系统通知标记为已读
     */
    public void markAllSystemNotifyAsRead();
    /**
     * 获取未读消息数量
     * @return
     */
    public UnreadMessage getUnreadMessageCount();
    /**
     * 获取提醒列表
     * @param page 页码
     * @param request 请求信息
     * @return
     */
    public PageView<Remind> getRemindList(int page, HttpServletRequest request);
    /**
     * 全部提醒状态标记为已读
     */
    public void markAllRemindersAsRead();

    /**
     * 删除提醒
     * @param remindId 提醒Id
     */
    public void deleteRemind(String remindId);


}
