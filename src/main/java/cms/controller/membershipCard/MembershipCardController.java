package cms.controller.membershipCard;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.membershipCard.MembershipCard;
import cms.model.membershipCard.MembershipCardOrder;
import cms.service.membershipCard.MembershipCardService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员卡列表控制器
 *
 */
@RestController
public class MembershipCardController {
    @Resource
    MembershipCardService membershipCardService;
    @Resource
    FileComponent fileComponent;

    /**
     * 会员卡列表
     * @param pageForm 页码
     * @return
     */
    @RequestMapping("/control/membershipCard/list")
    public RequestResult membershipCardList(PageForm pageForm){
        PageView<MembershipCard> pageView = membershipCardService.getMembershipCardList(pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
    /**
     * 会员卡订单列表
     * @param pageForm 页码
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/membershipCardOrder/list")
    public RequestResult queryMembershipCardOrderList(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<MembershipCardOrder> pageView = membershipCardService.getMembershipCardOrderList(pageForm.getPage(),fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
