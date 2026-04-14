package cms.service.question;

import cms.dto.PageView;
import cms.dto.question.AppendQuestionRequest;
import cms.dto.question.QuestionRequest;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 问题服务
 */
public interface QuestionService {

    /**
     * 获取问题列表
     * @param page 页码
     * @param visible 是否可见
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Question> getQuestionList(int page, Boolean visible, String fileServerAddress);

    /**
     * 获取问题明细
     * @param questionId 问题Id
     * @param answerId 答案Id
     * @param page 页码
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getQuestionDetail(Long questionId, Long answerId, Integer page, HttpServletRequest request);

    /**
     * 获取问题添加界面信息
     * @return
     */
    public Map<String, Object> getAddQuestionViewModel();
    /**
     * 添加问题
     * @param questionRequest 问题表单
     * @param request 请求信息
     */
    public void addQuestion(QuestionRequest questionRequest, HttpServletRequest request);
    /**
     * 上传文件
     * @param dir 上传类型，分别为image、media、file
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @param fileName 文件名称 预签名时有值
     * @param fileServerAddress 文件服务器地址
     * @param file 文件
     * @return
     */
    public Map<String, Object> uploadFile(String dir, String userName,Boolean isStaff, String fileName, String fileServerAddress, MultipartFile file);


    /**
     * 获取修改问题界面信息
     * @param questionId 问题Id
     * @return
     */
    public Map<String, Object> getEditQuestionViewModel(Long questionId);
    /**
     * 修改问题
     * @param questionRequest 问题表单
     * @param request 请求信息
     */
    public void editQuestion(QuestionRequest questionRequest, HttpServletRequest request);
    /**
     * 获取问题追加界面信息
     * @param questionId 问题Id
     * @return
     */
    public Map<String, Object> getAddAdditionalQuestionViewModel(Long questionId);
    /**
     * 追加问题
     * @param appendQuestionRequest 追加问题表单
     * @param request 请求信息
     */
    public void appendQuestion(AppendQuestionRequest appendQuestionRequest, HttpServletRequest request);
    /**
     * 获取修改追加问题界面信息
     * @param questionId 问题Id
     * @param appendQuestionItemId 追加问题Id
     * @return
     */
    public Map<String, Object> getEditAdditionalQuestionViewModel(Long questionId,String appendQuestionItemId);

    /**
     * 修改追加问题
     * @param appendQuestionRequest 追加问题表单
     * @param request 请求信息
     */
    public void editAppendQuestion(AppendQuestionRequest appendQuestionRequest, HttpServletRequest request);
    /**
     * 删除问题
     * @param questionId 问题Id集合
     */
    public void deleteQuestion(Long[] questionId);
    /**
     * 删除追加问题
     * @param questionId 问题Id集合
     */
    public void deleteAdditionalQuestion(Long questionId,String appendQuestionItemId);
    /**
     * 还原问题
     * @param questionId 问题Id集合
     */
    public void reductionQuestion(Long[] questionId);
    /**
     * 审核问题
     * @param questionId 问题Id
     */
    public void auditQuestion(Long questionId);
    /**
     * 获取问题搜索
     * @param page 页码
     * @param dataSource 数据源
     * @param keyword 关键词
     * @param tagId 标签Id
     * @param tagName 标签名称
     * @param account 账号
     * @param start_postTime 起始发贴时间
     * @param end_postTime 结束发贴时间
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Question> getSearch(int page, Integer dataSource,String keyword,String tagId,String tagName,String account,
                                        String start_postTime,String end_postTime,String fileServerAddress);
    /**
     * 获取全部待审核问题
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Question> getPendingQuestions(int page, String fileServerAddress);

    /**
     * 获取全部待审核答案
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Answer> getAllPendingAnswers(int page, String fileServerAddress);
    /**
     * 获取全部待审核回复
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<AnswerReply> getAllPendingReplies(int page, String fileServerAddress);
}
