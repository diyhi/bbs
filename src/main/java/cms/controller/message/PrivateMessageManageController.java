package cms.controller.message;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.message.PrivateMessageService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 私信管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/privateMessage/manage")
public class PrivateMessageManageController {
    @Resource
    PrivateMessageService privateMessageService;
    @Resource
    MessageSource messageSource;
    @Resource
    FileComponent fileComponent;

    /**
     * 私信列表
     * @param pageForm 页码
     * @param id 用户Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=privateMessageList",method=RequestMethod.GET)
    public RequestResult privateMessageList(PageForm pageForm, Long id, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = privateMessageService.getPrivateMessageList(pageForm.getPage(),id,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 私信列表
     * @param page 页码
     * @param id 用户Id
     * @param friendUserId 对方用户Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=privateMessageChatList",method=RequestMethod.GET)
    public RequestResult privateMessageChatList(Integer page,Long id,Long friendUserId, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = privateMessageService.getPrivateMessageChatList(page,id,friendUserId,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 私信删除
     * @param userId 用户Id
     * @param friendUserId 对方用户Id
     * @return
     */
    @RequestMapping(params="method=deletePrivateMessageChat", method=RequestMethod.POST)
    public RequestResult delete(Long userId,Long friendUserId) {
        privateMessageService.deletePrivateMessageChat(userId, friendUserId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 还原私信
     * @param userId 用户Id
     * @param privateMessageId 私信Id
     * @return
     */
    @RequestMapping(params="method=reductionPrivateMessage", method=RequestMethod.POST)
    public RequestResult reductionPrivateMessage(Long userId,String privateMessageId) {
        privateMessageService.reductionPrivateMessage(userId, privateMessageId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

}
