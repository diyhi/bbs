package cms.service.frontend;


import cms.dto.PageView;
import cms.dto.frontendModule.AnswerDTO;
import cms.dto.frontendModule.UserAnswerCountDTO;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * 前台答案服务接口
 */
public interface AnswerClientService {

    /**
     * 获取答案分页
     * @param page 页码
     * @param questionId 问题Id
     * @param answerId 答案Id
     * @param request 请求信息
     * @return
     */
    public PageView<Answer> getAnswerPage(Integer page, Long questionId, Long answerId, HttpServletRequest request);
    /**
     * 获取添加答案界面信息
     * @return
     */
    public Map<String,Object> getAddAnswerViewModel();
    /**
     * 添加答案
     * @param answerDTO 答案表单
     * @param request 请求信息
     */
    public void addAnswer(AnswerDTO answerDTO, HttpServletRequest request);
    /**
     * 获取修改答案界面信息
     * @param answerId 答案Id
     * @return
     */
    public Map<String,Object> getEditAnswerViewModel(Long answerId);
    /**
     * 修改答案
     * @param answerDTO 答案表单
     * @param request 请求信息
     */
    public void editAnswer(AnswerDTO answerDTO, HttpServletRequest request);
    /**
     * 删除答案
     * @param answerId 答案Id
     */
    public void deleteAnswer(Long answerId);
    /**
     * 采纳答案
     * @param answerId 答案Id
     */
    public void adoptionAnswer(Long answerId);
    /**
     * 获取添加答案回复界面信息
     * @return
     */
    public Map<String,Object> getAddReplyToAnswerViewModel();
    /**
     * 添加答案回复
     * @param answerId 答案回复Id
     * @param friendReplyId 对方回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void addAnswerReply(Long answerId,Long friendReplyId,String content,
                                              String captchaKey,String captchaValue,
                                              HttpServletRequest request);
    /**
     * 获取修改答案回复界面信息
     * @param replyId 答案回复Id
     * @return
     */
    public Map<String,Object> getEditReplyToAnswerViewModel(Long replyId);
    /**
     * 答案回复  修改
     * @param replyId 回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void editAnswerReply(Long replyId,String content,
                                         String captchaKey,String captchaValue,
                                         HttpServletRequest request);
    /**
     * 删除答案回复
     * @param replyId 回复Id
     */
    public void deleteAnswerReply(Long replyId);
    /**
     * 获取用户回答总数
     * @param userName 用户名称
     * @return
     */
    public UserAnswerCountDTO getUserAnswerCount(String userName);
    /**
     * 答案图片上传
     * @param questionId 问题Id
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String, Object> uploadImage(Long questionId, String fileName,
                              MultipartFile file, String fileServerAddress);
    /**
     * 获取我的答案
     * @param page 页码
     * @return
     */
    public PageView<Answer> getAnswerList(int page);
    /**
     * 获取我的答案回复
     *  @param page 页码
     * @return
     */
    public PageView<AnswerReply> getAnswerReplyList(int page);
}
