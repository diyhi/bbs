package cms.controller.sms;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.sms.SendSmsLog;
import cms.model.sms.SmsInterface;
import cms.service.sms.SmsInterfaceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信发送错误日志控制器
 *
 */
@RestController
public class SendSmsLogController {
    @Resource
    SmsInterfaceService smsInterfaceService;
    /**
     * 短信发送错误日志列表
     * @param pageForm 页码
     * @return
     */
    @RequestMapping("/control/sendSmsLog/list")
    public RequestResult smsInterfaceList(PageForm pageForm){
        PageView<SendSmsLog> pageView = smsInterfaceService.getSendSmsLogList(pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
