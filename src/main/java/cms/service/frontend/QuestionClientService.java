package cms.service.frontend;


import cms.dto.PageView;
import cms.dto.frontendModule.AppendQuestionDTO;
import cms.dto.frontendModule.QuestionDTO;
import cms.model.question.Question;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * 前台问题服务接口
 */
public interface QuestionClientService {

    /**
     * 获取问题分页
     * @param page 页码
     * @param questionTagId 标签Id
     * @param filterCondition 过滤条件
     * @param request 请求信息
     * @return
     */
    public PageView<Question> getQuestionPage(int page, Long questionTagId, Integer filterCondition, HttpServletRequest request);
    /**
     * 获取相似问题
     * @param questionId 问题Id
     * @return
     */
    public List<Question> getSimilarQuestion(Long questionId,HttpServletRequest request);
    /**
     * 获取问题明细
     * @param questionId 问题Id
     * @param request 请求信息
     */
    public Question getQuestionDetail(Long questionId, HttpServletRequest request);
    /**
     * 获取添加问题界面信息
     * @return
     */
    public Map<String,Object> getAddQuestionViewModel();
    /**
     * 添加问题
     * @param questionDTO 问题表单
     * @param request 请求信息
     */
    public void addQuestion(QuestionDTO questionDTO,HttpServletRequest request);
    /**
     * 获取追加问题界面信息
     * @return
     */
    public Map<String,Object> getAppendQuestionViewModel();
    /**
     * 追加问题
     * @param appendQuestionDTO 追加问题表单
     * @param request 请求信息
     */
    public void appendQuestion(AppendQuestionDTO appendQuestionDTO, HttpServletRequest request);
    /**
     * 文件上传
     * 员工发话题 上传文件名为UUID + a + 员工Id
     * 用户发话题 上传文件名为UUID + b + 用户Id
     * @param dir: 上传类型，分别为image、file、media
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> uploadFile(String dir, String fileName, MultipartFile file, String fileServerAddress);

    /**
     * 获取我的问题
     * @param page 页码
     * @return
     */
    public PageView<Question> getQuestionList(int page);
}
