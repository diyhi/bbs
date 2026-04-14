package cms.service.topic;

import cms.dto.topic.CommentRequest;
import cms.dto.topic.ReplyRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 评论服务
 */
public interface CommentService {

    /**
     * 添加评论
     * @param commentRequest 评论表单
     * @param request        请求信息
     */
    public void addComment(CommentRequest commentRequest, HttpServletRequest request);
    /**
     * 获取修改评论界面信息
     * @param commentId 评论Id
     * @return
     */
    public Map<String, Object> getEditCommentViewModel(Long commentId);
    /**
     * 修改评论
     * @param commentRequest 评论表单
     * @param request        请求信息
     */
    public void editComment(CommentRequest commentRequest, HttpServletRequest request);
    /**
     * 删除评论
     * @param commentId 评论Id集合
     */
    public void deleteComment(Long[] commentId);

    /**
     * 上传文件
     * @param dir 上传类型，分别为image、media、file
     * @param topicId 话题Id
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @param fileName 文件名称 预签名时有值
     * @param fileServerAddress 文件服务器地址
     * @param file 文件
     * @return
     */
    public Map<String, Object> uploadFile(String dir,Long topicId, String userName,Boolean isStaff, String fileName, String fileServerAddress, MultipartFile file);
    /**
     * 恢复评论
     * @param commentId 评论Id
     */
    public void recoveryComment(Long commentId);

    /**
     * 获取引用评论界面信息
     * @param commentId 评论Id
     * @return
     */
    public Map<String, Object> getQuoteCommentViewModel(Long commentId);

    /**
     * 添加引用评论
     * @param commentRequest 评论表单
     * @param request        请求信息
     * @return
     */
    public void addQuote(CommentRequest commentRequest, HttpServletRequest request);

    /**
     * 审核评论
     * @param commentId 评论Id
     */
    public void moderateComment(Long commentId);

    /**
     * 获取添加回复界面信息
     * @return
     */
    public Map<String, Object> getAddReplyViewModel();

    /**
     * 添加回复
     * @param replyRequest 回复表单
     * @param request        请求信息
     * @return
     */
    public void addReply(ReplyRequest replyRequest, HttpServletRequest request);

    /**
     * 获取修改回复界面信息
     * @param replyId 回复Id
     * @return
     */
    public Map<String, Object> getEditReplyViewModel(Long replyId);
    /**
     * 修改回复
     * @param replyRequest 回复表单
     * @param request        请求信息
     * @return
     */
    public void editReply(ReplyRequest replyRequest, HttpServletRequest request);
    /**
     * 删除回复
     * @param replyId 回复Id集合
     */
    public void deleteReply(Long[] replyId);

    /**
     * 恢复回复
     * @param replyId 回复Id
     */
    public void recoveryReply(Long replyId);
    /**
     * 审核回复
     * @param replyId 回复Id
     */
    public void moderateReply(Long replyId);
}
