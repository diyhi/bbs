package cms.controller.membershipCard;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.membershipCard.MembershipCardGiftTask;
import cms.service.membershipCard.MembershipCardGiftTaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员卡赠送任务列表控制器
 *
 */
@RestController
public class MembershipCardGiftTaskController {
    @Resource
    MembershipCardGiftTaskService membershipCardGiftTaskService;
    /**
     * 会员卡赠送任务列表
     * @param pageForm 页码
     * @return
     */
    @RequestMapping("/control/membershipCardGiftTask/list")
    public RequestResult membershipCardGiftTaskList(PageForm pageForm){
        PageView<MembershipCardGiftTask> pageView = membershipCardGiftTaskService.getMembershipCardGiftTaskList(pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
