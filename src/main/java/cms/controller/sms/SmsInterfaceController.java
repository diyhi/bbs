package cms.controller.sms;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.sms.SmsInterface;
import cms.service.sms.SmsInterfaceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信接口列表控制器
 *
 */
@RestController
public class SmsInterfaceController {
    @Resource
    SmsInterfaceService smsInterfaceService;
    /**
     * 短信接口列表
     * @param pageForm 页码
     * @return
     */
    @RequestMapping("/control/smsInterface/list")
    public RequestResult smsInterfaceList(PageForm pageForm){
        PageView<SmsInterface> pageView = smsInterfaceService.getSmsInterfaceList(pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
