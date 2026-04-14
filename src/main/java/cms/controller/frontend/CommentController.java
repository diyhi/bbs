package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.component.fileSystem.FileComponent;
import cms.dto.*;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.service.frontend.CommentClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 前台评论控制器
 */
@RestController("frontendCommentController")
public class CommentController {
    @Resource
    CommentClientService commentClientService;
    @Resource
    FileComponent fileComponent;

    /**
     * 评论分页
     * @param page 页码
     * @param topicId 话题Id
     * @param commentId 评论Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6010300)
    public PageView<Comment> commentPage(Integer page, Long topicId, Long commentId,HttpServletRequest request){
        return commentClientService.getCommentPage(page,topicId, commentId,request);
    }

    /**
     * 添加评论表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6010600)
    public Map<String,Object> addCommentUI(){
        return commentClientService.getAddCommentViewModel();
    }


    /**
     * 评论   添加
     * @param topicId 话题Id
     * @param content 评论内容
     * @param isMarkdown 是否使用Markdown
     * @param markdownContent Markdown内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1010500)
    @RequestMapping(value="/user/control/comment/add", method= RequestMethod.POST)
    public ClientRequestResult add(Long topicId, String content, Boolean isMarkdown, String markdownContent,
                      String captchaKey, String captchaValue,
                      HttpServletRequest request){
        commentClientService.addComment(topicId, content, isMarkdown, markdownContent,
                captchaKey, captchaValue,request);
        return ClientRequestResult.success();
    }

    /**
     * 引用评论表单
     * @param commentId 评论Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6010700)
    public Map<String,Object> quoteCommentUI(Long commentId){
        return commentClientService.getQuoteCommentViewModel(commentId);
    }


    /**
     * 引用  添加
     * @param commentId 评论Id
     * @param content 评论内容
     * @param isMarkdown 是否使用Markdown
     * @param markdownContent Markdown内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1010600)
    @RequestMapping(value="/user/control/comment/addQuote", method=RequestMethod.POST)
    public ClientRequestResult addQuote(Long commentId,String content,Boolean isMarkdown,String markdownContent,
                           String captchaKey,String captchaValue,
                           HttpServletRequest request) {
        commentClientService.addQuoteComment(commentId, content, isMarkdown, markdownContent,
                captchaKey, captchaValue,request);
        return ClientRequestResult.success();
    }

    /**
     * 修改评论表单
     * @param commentId 评论Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6011000)
    public Map<String,Object> editCommentUI(Long commentId){
        return commentClientService.getEditCommentViewModel(commentId);
    }

    /**
     * 评论  修改
     * @param commentId 评论Id
     * @param content 内容
     * @param markdownContent Markdown内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1010700)
    @RequestMapping(value="/user/control/comment/edit", method=RequestMethod.POST)
    public ClientRequestResult edit(Long commentId,String content,String markdownContent,
                       String captchaKey,String captchaValue,
                       HttpServletRequest request){
        commentClientService.editComment(commentId, content, markdownContent,
                captchaKey, captchaValue,request);
        return ClientRequestResult.success();
    }

    /**
     * 评论  删除
     * @param commentId 评论Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1010800)
    @RequestMapping(value="/user/control/comment/delete", method=RequestMethod.POST)
    public Map<String,Object> delete(Long commentId){
        return commentClientService.deleteComment(commentId);
    }

    /**
     * 评论  图片上传
     * @param topicId 话题Id
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1010900)
    @RequestMapping(value="/user/control/comment/uploadImage", method=RequestMethod.POST)
    public Map<String, Object> uploadImage(Long topicId, String fileName,
                              MultipartFile file, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return commentClientService.uploadImage(topicId,fileName,file,fileServerAddress);
    }

    /**
     * 添加评论回复表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6010800)
    public Map<String,Object> addCommentReplyUI(){
        return commentClientService.getAddCommentReplyViewModel();
    }


    /**
     * 评论回复  添加
     * @param commentId 评论Id
     * @param friendReplyId 对方回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1011000)
    @RequestMapping(value="/user/control/comment/addReply", method=RequestMethod.POST)
    public ClientRequestResult addReply(Long commentId,Long friendReplyId,String content,
                           String captchaKey,String captchaValue,
                           HttpServletRequest request){
        commentClientService.addReply(commentId, friendReplyId,content,
                captchaKey, captchaValue,request);
        return ClientRequestResult.success();
    }

    /**
     * 修改评论回复表单
     * @param replyId 回复Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6011100)
    public Map<String,Object> editCommentReplyUI(Long replyId){
        return commentClientService.getEditCommentReplyViewModel(replyId);
    }
    /**
     * 评论回复  修改
     * @param replyId 回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1011100)
    @RequestMapping(value="/user/control/comment/editReply", method=RequestMethod.POST)
    public ClientRequestResult editReply(Long replyId,String content,
                            String captchaKey,String captchaValue,
                            HttpServletRequest request) {
        commentClientService.editReply(replyId, content, captchaKey, captchaValue,request);
        return ClientRequestResult.success();
    }

    /**
     * 评论回复  删除
     * @param replyId 回复Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1011200)
    @RequestMapping(value="/user/control/comment/deleteReply", method=RequestMethod.POST)
    public Map<String,Object> deleteReply(Long replyId){
        return commentClientService.deleteReply(replyId);
    }

    /**
     * 我的评论
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1011400)
    @RequestMapping(value="/user/control/commentList",method=RequestMethod.GET)
    public PageView<Comment> commentListUI(PageForm pageForm){
        return commentClientService.getCommentList(pageForm.getPage());
    }

    /**
     * 我的评论回复
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1011500)
    @RequestMapping(value="/user/control/replyList",method=RequestMethod.GET)
    public PageView<Reply> replyListUI(PageForm pageForm){
        return commentClientService.getReplyList(pageForm.getPage());
    }
}
