package cms.service.question;


import cms.config.BusinessException;
import cms.dto.question.AnswerReplyRequest;
import cms.dto.question.AnswerRequest;
import cms.model.payment.PaymentLog;
import cms.model.platformShare.QuestionRewardPlatformShare;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.model.staff.SysUsers;
import cms.model.user.PointLog;
import cms.model.user.User;
import cms.utils.FileUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.Strings;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 答案服务
 */
public interface AnswerService {

    /**
     * 添加答案
     * @param answerRequest 答案表单
     * @param request       请求信息
     */
    public void addAnswer(AnswerRequest answerRequest, HttpServletRequest request);

    /**
     * 获取修改答案界面信息
     * @param answerId 答案Id
     * @return
     */
    public Map<String, Object> getEditAnswerViewModel(Long answerId);
    /**
     * 修改答案
     * @param answerRequest 答案表单
     * @param request       请求信息
     */
    public void editAnswer(AnswerRequest answerRequest, HttpServletRequest request);
    /**
     * 上传文件
     * @param dir 上传类型，分别为image、media、file
     * @param questionId 问题Id
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @param fileName 文件名称 预签名时有值
     * @param fileServerAddress 文件服务器地址
     * @param file 文件
     * @return
     */
    public Map<String, Object> uploadFile(String dir,Long questionId, String userName,Boolean isStaff, String fileName, String fileServerAddress, MultipartFile file);
    /**
     * 删除答案
     * @param answerId 答案Id集合
     */
    public void deleteAnswer(Long[] answerId);
    /**
     * 审核答案
     * @param answerId 评论Id
     */
    public void moderateAnswer(Long answerId);
    /**
     * 获取添加答案回复界面信息
     * @return
     */
    public Map<String, Object> getAddAnswerReplyViewModel();

    /**
     * 添加答案回复
     * @param answerReplyRequest 答案回复表单
     * @param request        请求信息
     * @return
     */
    public void addAnswerReply(AnswerReplyRequest answerReplyRequest, HttpServletRequest request);

    /**
     * 获取修改答案回复界面信息
     * @param answerReplyId 答案回复Id
     * @return
     */
    public Map<String, Object> getEditAnswerReplyViewModel(Long answerReplyId);

    /**
     * 修改答案回复
     * @param answerReplyRequest 答案回复表单
     * @param request        请求信息
     * @return
     */
    public void editAnswerReply(AnswerReplyRequest answerReplyRequest, HttpServletRequest request);

    /**
     * 删除答案回复
     * @param answerReplyId 答案回复Id集合
     */
    public void deleteAnswerReply(Long[] answerReplyId);
    /**
     * 审核答案回复
     * @param answerReplyId 答案回复Id
     */
    public void moderateAnswerReply(Long answerReplyId);
    /**
     * 恢复答案回复
     * @param answerReplyId 答案回复Id
     */
    public void recoveryAnswerReply(Long answerReplyId);
    /**
     * 采纳答案
     * @param answerId 答案Id
     */
    public void adoptionAnswer(Long answerId);
    /**
     * 取消采纳答案
     * @param answerId 答案Id
     */
    public void cancelAdoptionAnswer(Long answerId);


}
