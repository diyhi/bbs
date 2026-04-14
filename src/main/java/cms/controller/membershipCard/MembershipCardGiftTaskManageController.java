package cms.controller.membershipCard;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.membershipCard.MembershipCardGiftTaskRequest;
import cms.dto.sms.SmsInterfaceRequest;
import cms.model.membershipCard.MembershipCardGiftItem;
import cms.model.membershipCard.MembershipCardGiftTask;
import cms.service.membershipCard.MembershipCardGiftTaskService;
import cms.service.sms.SmsInterfaceService;
import cms.validator.sms.SmsInterfaceValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 会员卡赠送任务管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/membershipCardGiftTask/manage")
public class MembershipCardGiftTaskManageController {
    @Resource
    MembershipCardGiftTaskService membershipCardGiftTaskService;
    @Resource
    MessageSource messageSource;
    @Resource
    FileComponent fileComponent;

    /**
     * 查询会员卡赠送项(获赠用户)
     * @param pageForm 页码
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=membershipCardGiftItemList",method=RequestMethod.GET)
    public RequestResult queryMembershipCardGiftItem(PageForm pageForm, Long membershipCardGiftTaskId,
                                                     HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<MembershipCardGiftItem> pageView = membershipCardGiftTaskService.getMembershipCardGiftItem(pageForm.getPage(),membershipCardGiftTaskId,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
    /**
     * 查询会员卡赠送任务
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @return
     */
    @RequestMapping(params="method=view",method=RequestMethod.GET)
    public RequestResult queryMembershipCardGiftTask(Long membershipCardGiftTaskId){
        MembershipCardGiftTask task = membershipCardGiftTaskService.getMembershipCardGiftTask(membershipCardGiftTaskId);
        return new RequestResult(ResultCode.SUCCESS, task);
    }

    /**
     * 会员卡赠送任务 添加界面显示
     */
    @RequestMapping(params="method=add",method= RequestMethod.GET)
    public RequestResult addUI(){
        Map<String,Object> returnValue = membershipCardGiftTaskService.getAddMembershipCardGiftTaskViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 会员卡赠送任务 添加
     * @param membershipCardGiftTaskRequest 会员卡赠送任务表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute MembershipCardGiftTaskRequest membershipCardGiftTaskRequest, BindingResult result) {
        membershipCardGiftTaskService.addMembershipCardGiftTask(membershipCardGiftTaskRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 会员卡赠送任务 显示修改
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Long membershipCardGiftTaskId) {
        Map<String,Object> returnValue = membershipCardGiftTaskService.getEditMembershipCardGiftTaskViewModel(membershipCardGiftTaskId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 会员卡赠送任务 修改
     * @param membershipCardGiftTaskRequest 会员卡赠送任务表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute MembershipCardGiftTaskRequest membershipCardGiftTaskRequest, BindingResult result) {
        membershipCardGiftTaskService.editMembershipCardGiftTask(membershipCardGiftTaskRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 会员卡赠送任务 删除
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Long membershipCardGiftTaskId) {
        membershipCardGiftTaskService.deleteMembershipCardGiftTask(membershipCardGiftTaskId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }



}
