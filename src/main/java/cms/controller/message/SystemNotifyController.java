package cms.controller.message;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.message.SystemNotify;
import cms.service.message.SystemNotifyService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统通知列表控制器
 *
 */
@RestController
public class SystemNotifyController {
    @Resource
    SystemNotifyService systemNotifyService;
    /**
     * 系统通知列表
     * @param pageForm 页码
     * @return
     */
    @RequestMapping("/control/systemNotify/list")
    public RequestResult systemNotifyList(PageForm pageForm){
        PageView<SystemNotify> pageView = systemNotifyService.getSystemNotifyList(pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
