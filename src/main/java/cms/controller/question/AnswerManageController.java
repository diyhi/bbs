package cms.controller.question;

import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.question.AnswerReplyRequest;
import cms.dto.question.AnswerRequest;
import cms.dto.topic.CommentRequest;
import cms.dto.topic.ReplyRequest;
import cms.service.question.AnswerService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 答案管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/answer/manage")
public class AnswerManageController {
    @Resource
    AnswerService answerService;
    @Resource
    FileComponent fileComponent;
    /**
     * 答案  添加
     * @param answerRequest 评论表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=add",method= RequestMethod.POST)
    public RequestResult add(@ModelAttribute AnswerRequest answerRequest, BindingResult result,
                             HttpServletRequest request) {
        answerService.addAnswer(answerRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 答案  修改页面显示
     * @param answerId 答案Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Long answerId,
                         HttpServletRequest request) {
        Map<String,Object> returnValue = answerService.getEditAnswerViewModel(answerId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 答案  修改
     * @param answerRequest 评论表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute AnswerRequest answerRequest, BindingResult result,
                       HttpServletRequest request) {
        answerService.editAnswer(answerRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 答案  删除
     * @param answerId 答案Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Long[] answerId) {
        answerService.deleteAnswer(answerId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 答案  图片上传
     * 员工发话题 上传文件名为UUID + a + 员工Id
     * 用户发话题 上传文件名为UUID + b + 用户Id
     * @param questionId 问题Id
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=uploadImage",method=RequestMethod.POST)
    public Map<String, Object> uploadImage(Long questionId, String userName, Boolean isStaff, String fileName,
                              MultipartFile file, HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return answerService.uploadFile("image", questionId,userName, isStaff,fileName, fileServerAddress, file);
    }

    /**
     * 审核答案
     * @param answerId 答案Id
     * @return
     */
    @RequestMapping(params="method=auditAnswer",method=RequestMethod.POST)
    public RequestResult auditAnswer(Long answerId) {
        answerService.moderateAnswer(answerId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 答案回复  添加页面显示
     * @return
     */
    @RequestMapping(params="method=addAnswerReply",method=RequestMethod.GET)
    public RequestResult addAnswerReplyUI() {
        Map<String,Object> returnValue = answerService.getAddAnswerReplyViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 答案回复  添加
     * @param answerReplyRequest 答案回复表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=addAnswerReply",method=RequestMethod.POST)
    public RequestResult addAnswerReply(@ModelAttribute AnswerReplyRequest answerReplyRequest, BindingResult result,
                                        HttpServletRequest request) {
        answerService.addAnswerReply(answerReplyRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 答案回复  修改页面显示
     * @param answerReplyId 答案回复Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editAnswerReply",method=RequestMethod.GET)
    public RequestResult editAnswerReplyUI(Long answerReplyId,
                                    HttpServletRequest request) {
        Map<String,Object> returnValue = answerService.getEditAnswerReplyViewModel(answerReplyId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 答案回复  修改
     * @param answerReplyRequest 答案回复表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editAnswerReply",method=RequestMethod.POST)
    public RequestResult editAnswerReply(@ModelAttribute AnswerReplyRequest answerReplyRequest, BindingResult result,
                                  HttpServletRequest request) {
        answerService.editAnswerReply(answerReplyRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 回复  删除
     * @param answerReplyId 回复Id
     */
    @RequestMapping(params="method=deleteAnswerReply",method=RequestMethod.POST)
    public RequestResult deleteAnswerReply(Long[] answerReplyId) {
        answerService.deleteAnswerReply(answerReplyId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 审核回复
     * @param answerReplyId 回复Id
     * @return
     */
    @RequestMapping(params="method=auditAnswerReply",method=RequestMethod.POST)
    public RequestResult auditAnswerReply(Long answerReplyId) {
        answerService.moderateAnswerReply(answerReplyId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 回复  恢复
     * @param replyId 回复Id
     * @return
     */
    @RequestMapping(params="method=recoveryReply",method=RequestMethod.POST)
    public RequestResult recoveryReply(Long replyId) {
        answerService.recoveryAnswerReply(replyId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 采纳答案
     * @param answerId 答案Id
     * @return
     */
    @RequestMapping(params="method=adoptionAnswer",method=RequestMethod.POST)
    public RequestResult adoptionAnswer(Long answerId) {
        answerService.adoptionAnswer(answerId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 取消采纳答案
     * @param answerId 答案Id
     * @return
     */
    @RequestMapping(params="method=cancelAdoptionAnswer",method=RequestMethod.POST)
    public RequestResult cancelAdoptionAnswer(Long answerId) {
        answerService.cancelAdoptionAnswer(answerId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


}
