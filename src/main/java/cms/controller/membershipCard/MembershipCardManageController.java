package cms.controller.membershipCard;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.membershipCard.MembershipCardRequest;
import cms.service.membershipCard.MembershipCardService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 会员卡管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/membershipCard/manage")
public class MembershipCardManageController {
    @Resource
    MembershipCardService membershipCardService;
    @Resource
    MessageSource messageSource;
    @Resource
    FileComponent fileComponent;

    /**
     * 查询用户会员卡订单列表
     * @param pageForm 页码
     * @param userName 用户名称
     * @return
     */
    @RequestMapping(params="method=membershipCardOrderList",method=RequestMethod.GET)
    public RequestResult queryUserMembershipCardOrderList(PageForm pageForm,String userName){
        Map<String,Object> returnValue = membershipCardService.getUserMembershipCardOrderList(pageForm.getPage(),userName);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 会员卡 添加界面显示
     */
    @RequestMapping(params="method=add",method= RequestMethod.GET)
    public RequestResult addUI(){
        Map<String,Object> returnValue = membershipCardService.getAddMembershipCardViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /* 会员卡 添加
     * @param membershipCardRequest 会员卡表单
     * @param result 存储校验信息
     * @param request 请求信息
     * @return
    */
    @RequestMapping(params="method=add",method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute MembershipCardRequest membershipCardRequest, BindingResult result,
                             HttpServletRequest request) {
        membershipCardService.addMembershipCard(membershipCardRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 会员卡 显示修改
     * @param membershipCardId 会员卡Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Long membershipCardId) {
        Map<String,Object> returnValue = membershipCardService.getEditMembershipCardViewModel(membershipCardId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 会员卡 修改
     * @param membershipCardRequest 会员卡表单
     * @param result 存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute MembershipCardRequest membershipCardRequest, BindingResult result,
                              HttpServletRequest request) {
        membershipCardService.editMembershipCard(membershipCardRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 文件上传
     * @param dir 上传类型，分别为image、flash、media、file
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=upload",method=RequestMethod.POST)
    public Map<String,Object> upload(String dir, String fileName,
                                     MultipartFile file, HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return membershipCardService.uploadFile(dir, fileName, fileServerAddress, file);
    }

    /**
     * 会员卡 删除
     * @param membershipCardId 会员卡Id
     * @return
     */
    @RequestMapping(params="method=delete", method=RequestMethod.POST)
    public RequestResult delete(Long membershipCardId) {
        membershipCardService.deleteMembershipCard(membershipCardId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }



}
