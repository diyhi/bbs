package cms.controller.message;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.sms.SendSmsLog;
import cms.service.message.RemindService;
import cms.service.sms.SmsInterfaceService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 提醒控制器
 *
 */
@RestController
@RequestMapping("/control/remind/manage")
public class RemindManageController {
    @Resource
    RemindService remindService;
    @Resource
    FileComponent fileComponent;
    /**
     * 提醒列表
     * @param pageForm 页码
     * @param id 用户Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=remindList",method= RequestMethod.GET)
    public RequestResult remindList(PageForm pageForm,Long id, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = remindService.getRemindList(pageForm.getPage(),id,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 还原提醒
     * @param userId 用户Id
     * @param remindId 提醒Id
     * @return
     */
    @RequestMapping(params="method=reductionRemind", method=RequestMethod.POST)
    public RequestResult reductionRemind(Long userId,String remindId) {
        remindService.reductionRemind(userId, remindId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
}
