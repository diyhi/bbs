package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.annotation.RoleAnnotation;
import cms.component.fileSystem.FileComponent;
import cms.dto.ClientRequestResult;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.message.UnreadMessage;
import cms.dto.user.ResourceEnum;
import cms.model.message.PrivateMessage;
import cms.model.message.Remind;
import cms.model.message.SubscriptionSystemNotify;
import cms.repository.topic.TagRepository;
import cms.service.frontend.MessageClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台消息控制器
 */
@RestController
public class MessageController {

    @Resource
    MessageClientService messageClientService;
    @Resource
    FileComponent fileComponent;


    /**
     * 私信列表
     * @param pageForm 页码
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1190100)
    @RequestMapping(value="/user/control/privateMessageList",method= RequestMethod.GET)
    public PageView<PrivateMessage> privateMessageList(PageForm pageForm,HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return messageClientService.getPrivateMessageList(pageForm.getPage(),fileServerAddress);
    }

    /**
     * 私信对话列表
     * @param page 页码
     * @param friendUserName 对方用户名称
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1190200)
    @RequestMapping(value="/user/control/privateMessageChatList",method=RequestMethod.GET)
    public Map<String,Object> privateMessageChatList(Integer page, String friendUserName,
                                                     HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return messageClientService.getPrivateMessageChatList(page,friendUserName,fileServerAddress);
    }

    /**
     * 添加私信表单
     * @param friendUserName 对方用户名称
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1190300)
    @RequestMapping(value="/user/control/addPrivateMessage",method=RequestMethod.GET)
    public Map<String,Object> addPrivateMessageUI(String friendUserName) {
        return messageClientService.getAddPrivateMessageViewModel(friendUserName);
    }


    /**
     * 添加私信
     * @param friendUserName 对方用户名称
     * @param messageContent 消息内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @return
     */
    @RoleAnnotation(resourceCode= ResourceEnum._6001000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1190400)
    @RequestMapping(value="/user/control/addPrivateMessage", method=RequestMethod.POST)
    public ClientRequestResult addPrivateMessage(String friendUserName, String messageContent,
                                                 String captchaKey, String captchaValue){
        messageClientService.addPrivateMessage(friendUserName, messageContent, captchaKey, captchaValue);
        return ClientRequestResult.success();
    }

    /**
     * 删除私信
     * @param friendUserName 对方用户名称
     * @return
     */
    @RoleAnnotation(resourceCode=ResourceEnum._6002000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1190500)
    @RequestMapping(value="/user/control/deletePrivateMessage", method=RequestMethod.POST)
    public ClientRequestResult deletePrivateMessage(String friendUserName){
        messageClientService.deletePrivateMessage(friendUserName);
        return ClientRequestResult.success();
    }

    /**
     * 系统通知列表
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1190600)
    @RequestMapping(value="/user/control/systemNotifyList",method=RequestMethod.GET)
    public PageView<SubscriptionSystemNotify> systemNotifyList(PageForm pageForm){
        return messageClientService.getSystemNotifyList(pageForm.getPage());
    }

    /**
     * 全部系统通知标记为已读
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1190700)
    @RequestMapping(value="/user/control/markAllSystemNotifyAsRead", method=RequestMethod.POST)
    public ClientRequestResult markAllSystemNotifyAsRead(){
        messageClientService.markAllSystemNotifyAsRead();
        return ClientRequestResult.success();
    }

    /**
     * 删除系统通知
     * @param subscriptionSystemNotifyId 订阅系统通知Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1190800)
    @RequestMapping(value="/user/control/deleteSystemNotify", method=RequestMethod.POST)
    public ClientRequestResult deleteSystemNotify(String subscriptionSystemNotifyId){
        messageClientService.deleteSystemNotify(subscriptionSystemNotifyId);
        return ClientRequestResult.success();
    }

    /**
     * 未读消息数量
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1190900)
    @RequestMapping(value="/user/control/unreadMessageCount",method=RequestMethod.GET)
    public UnreadMessage unreadMessageCount(){
        return messageClientService.getUnreadMessageCount();
    }


    /**
     * 提醒列表
     * @param pageForm 页码
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1191000)
    @RequestMapping(value="/user/control/remindList",method=RequestMethod.GET)
    public PageView<Remind> remindList(PageForm pageForm,HttpServletRequest request){
        return messageClientService.getRemindList(pageForm.getPage(),request);
    }

    /**
     * 全部提醒状态标记为已读
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1191100)
    @RequestMapping(value="/user/control/markAllRemindersAsRead", method=RequestMethod.POST)
    public ClientRequestResult markAllRemindersAsRead(){
        messageClientService.markAllRemindersAsRead();
        return ClientRequestResult.success();
    }

    /**
     * 删除提醒
     * @param remindId 提醒Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1191200)
    @RequestMapping(value="/user/control/deleteRemind", method=RequestMethod.POST)
    public ClientRequestResult deleteRemind(String remindId){
        messageClientService.deleteRemind(remindId);
        return ClientRequestResult.success();
    }

}
