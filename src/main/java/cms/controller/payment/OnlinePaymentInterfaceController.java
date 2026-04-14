package cms.controller.payment;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.payment.OnlinePaymentInterface;
import cms.service.payment.OnlinePaymentInterfaceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在线支付接口列表控制器
 *
 */
@RestController
public class OnlinePaymentInterfaceController {
    @Resource
    OnlinePaymentInterfaceService onlinePaymentInterfaceService;
    /**
     * 在线支付接口列表
     * @param pageForm 页码
     * @return
     */
    @RequestMapping("/control/onlinePaymentInterface/list")
    public RequestResult onlinePaymentInterfaceList(PageForm pageForm){
        PageView<OnlinePaymentInterface> pageView = onlinePaymentInterfaceService.getOnlinePaymentInterfaceList(pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
