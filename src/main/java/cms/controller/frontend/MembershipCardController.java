package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.dto.ClientRequestResult;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.model.membershipCard.MembershipCard;
import cms.model.membershipCard.MembershipCardOrder;
import cms.service.frontend.MembershipCardClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台会员卡控制器
 */
@RestController("frontendMembershipCardController")
public class MembershipCardController {
    @Resource
    MembershipCardClientService membershipCardClientService;
    /**
     * 会员卡列表
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6060100)
    public List<MembershipCard> membershipCardList() {
        return membershipCardClientService.getMembershipCardList();
    }

    /**
     * 会员卡明细
     * @param membershipCardId 会员卡Id
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6060200)
    public MembershipCard memberCardDetail(Long membershipCardId){
        return membershipCardClientService.getMemberCardDetail(membershipCardId);
    }


    /**
     * 购买会员卡   添加
     * @param specificationId 规格Id
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1060100)
    @RequestMapping(value="/user/control/membershipCard/add", method= RequestMethod.POST)
    public ClientRequestResult addMembershipCard(Long specificationId,
                                                 HttpServletRequest request, HttpServletResponse response) {
        membershipCardClientService.addMembershipCard(specificationId);
        return ClientRequestResult.success();
    }
    /**
     * 会员卡订单列表
     * @param pageForm 页码
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1060200)
    @RequestMapping(value="/user/control/membershipCardOrderList",method=RequestMethod.GET)
    public PageView<MembershipCardOrder> membershipCardOrderUI(PageForm pageForm,
                                                               HttpServletRequest request, HttpServletResponse response){
        return membershipCardClientService.getMembershipCardOrderList(pageForm.getPage());
    }
}
