package cms.service.frontend;


import cms.dto.PageView;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * 前台评论服务接口
 */
public interface CommentClientService {
    /**
     * 获取评论分页
     * @param page 页码
     * @param topicId 话题Id
     * @param commentId 评论Id
     * @param request 请求信息
     * @return
     */
    public PageView<Comment> getCommentPage(Integer page, Long topicId, Long commentId, HttpServletRequest request);

    /**
     * 获取添加评论界面信息
     * @return
     */
    public Map<String,Object> getAddCommentViewModel();
    /**
     * 添加评论
     * @param topicId 话题Id
     * @param content 评论内容
     * @param isMarkdown 是否使用Markdown
     * @param markdownContent Markdown内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void addComment(Long topicId, String content, Boolean isMarkdown, String markdownContent,
                                  String captchaKey, String captchaValue, HttpServletRequest request);

    /**
     * 获取引用评论界面信息
     * @param commentId 评论Id
     */
    public Map<String,Object> getQuoteCommentViewModel(Long commentId);
    /**
     * 添加引用评论
     * @param commentId 评论Id
     * @param content 评论内容
     * @param isMarkdown 是否使用Markdown
     * @param markdownContent Markdown内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void addQuoteComment(Long commentId, String content, Boolean isMarkdown, String markdownContent,
                                        String captchaKey, String captchaValue,
                                        HttpServletRequest request);

    /**
     * 获取修改评论界面信息
     * @param commentId 评论Id
     */
    public Map<String,Object> getEditCommentViewModel(Long commentId);
    /**
     * 修改评论
     * @param commentId 评论Id
     * @param content 内容
     * @param markdownContent Markdown内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void editComment(Long commentId,String content,String markdownContent,
                                    String captchaKey,String captchaValue, HttpServletRequest request);

    /**
     * 删除评论
     * @param commentId 评论Id
     * @return
     */
    public Map<String,Object> deleteComment(Long commentId);

    /**
     * 评论  图片上传
     * @param topicId 话题Id
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String, Object> uploadImage(Long topicId, String fileName,
                                           MultipartFile file, String fileServerAddress);

    /**
     * 获取添加评论回复界面信息
     * @return
     */
    public Map<String,Object> getAddCommentReplyViewModel();
    /**
     * 添加评论回复
     * @param commentId 评论Id
     * @param friendReplyId 对方回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void addReply(Long commentId,Long friendReplyId,String content,
                                        String captchaKey,String captchaValue,
                                        HttpServletRequest request);
    /**
     * 修改评论回复界面信息
     * @param replyId 回复Id
     */
    public Map<String,Object> getEditCommentReplyViewModel(Long replyId);
    /**
     * 修改评论回复
     * @param replyId 回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void editReply(Long replyId,String content,String captchaKey,String captchaValue,
                                         HttpServletRequest request);
    /**
     * 删除评论回复
     * @param replyId 评论回复Id
     * @return
     */
    public Map<String,Object> deleteReply(Long replyId);
    /**
     * 获取我的评论
     * @param page 页码
     * @return
     */
    public PageView<Comment> getCommentList(int page);
    /**
     * 获取我的评论回复
     * @param page 页码
     * @return
     */
    public PageView<Reply> getReplyList(int page);
}
