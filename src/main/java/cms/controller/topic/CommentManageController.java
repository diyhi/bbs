package cms.controller.topic;

import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.topic.CommentRequest;
import cms.dto.topic.ReplyRequest;
import cms.service.topic.CommentService;
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
 * 评论管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/comment/manage")
public class CommentManageController {
    @Resource
    CommentService commentService;
    @Resource
    FileComponent fileComponent;


    /**
     * 评论  添加
     * @param commentRequest 评论表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=addComment",method= RequestMethod.POST)
    public RequestResult addComment(@ModelAttribute CommentRequest commentRequest, BindingResult result,
                                    HttpServletRequest request){
        commentService.addComment(commentRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 评论  修改页面显示
     * @param commentId 评论Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editComment",method=RequestMethod.GET)
    public RequestResult editCommentUI(Long commentId,HttpServletRequest request) {
        Map<String,Object> returnValue = commentService.getEditCommentViewModel(commentId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 评论  修改
     * @param commentRequest 评论表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editComment",method=RequestMethod.POST)
    public RequestResult editComment(@ModelAttribute CommentRequest commentRequest, BindingResult result,
                              HttpServletRequest request) {
        commentService.editComment(commentRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 评论  删除
     * @param commentId 评论Id
     * @return
     */
    @RequestMapping(params="method=deleteComment",method=RequestMethod.POST)
    public RequestResult deleteComment(Long[] commentId) {
        commentService.deleteComment(commentId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 评论  恢复
     * @param commentId 评论Id
     * @return
     */
    @RequestMapping(params="method=recoveryComment",method=RequestMethod.POST)
    public RequestResult recoveryComment(Long commentId) {
        commentService.recoveryComment(commentId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 评论  图片上传
     * 员工发话题 上传文件名为UUID + a + 员工Id
     * 用户发话题 上传文件名为UUID + b + 用户Id
     * @param topicId 话题Id
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=uploadImage",method=RequestMethod.POST)
    public Map<String,Object> uploadImage(Long topicId, String userName, Boolean isStaff, String fileName,
                              MultipartFile file, HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return commentService.uploadFile("image", topicId,userName, isStaff,fileName, fileServerAddress, file);
    }


    /**
     * 引用  添加页面显示
     * @param commentId 评论Id
     * @return
     */
    @RequestMapping(params="method=addQuote",method=RequestMethod.GET)
    public RequestResult addQuoteUI(Long commentId) {
        Map<String,Object> returnValue = commentService.getQuoteCommentViewModel(commentId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 引用  添加
     * @param commentRequest 评论表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=addQuote",method=RequestMethod.POST)
    public RequestResult addQuote(@ModelAttribute CommentRequest commentRequest, BindingResult result,
                           HttpServletRequest request) {
        commentService.addQuote(commentRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 审核评论
     * @param commentId 评论Id
     * @return
     */
    @RequestMapping(params="method=auditComment",method=RequestMethod.POST)
    public RequestResult auditComment(Long commentId){
        commentService.moderateComment(commentId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 回复  添加页面显示
     * @return
     */
    @RequestMapping(params="method=addReply",method=RequestMethod.GET)
    public RequestResult addReplyUI(){
        Map<String,Object> returnValue = commentService.getAddReplyViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 回复  添加
     * @param replyRequest 回复表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=addReply",method=RequestMethod.POST)
    public RequestResult addReply(@ModelAttribute  ReplyRequest replyRequest, BindingResult result,
                           HttpServletRequest request) {
        commentService.addReply(replyRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 回复  修改页面显示
     * @param replyId 回复Id
     * @return
     */
    @RequestMapping(params="method=editReply",method=RequestMethod.GET)
    public RequestResult editReplyUI(Long replyId) {
        Map<String,Object> returnValue = commentService.getEditReplyViewModel(replyId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 回复  修改
     * @param replyRequest 回复表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editReply",method=RequestMethod.POST)
    public RequestResult editReply(@ModelAttribute  ReplyRequest replyRequest, BindingResult result,
                            HttpServletRequest request) {
        commentService.editReply(replyRequest, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 回复  删除
     * @param replyId 回复Id
     * @return
     */
    @RequestMapping(params="method=deleteReply",method=RequestMethod.POST)
    public RequestResult deleteReply(Long[] replyId) {
        commentService.deleteReply(replyId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 回复  恢复
     * @param replyId 回复Id
     * @return
     */
    @RequestMapping(params="method=recoveryReply",method=RequestMethod.POST)
    public RequestResult recoveryReply(Long replyId) {
        commentService.recoveryReply(replyId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 审核回复
     * @param replyId 回复Id
     * @return
     */
    @RequestMapping(params="method=auditReply",method=RequestMethod.POST)
    public RequestResult auditReply(Long replyId) {
        commentService.moderateReply(replyId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

}
