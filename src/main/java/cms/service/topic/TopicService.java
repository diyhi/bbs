package cms.service.topic;

import cms.dto.PageView;
import cms.dto.topic.TopicRequest;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.model.topic.Topic;
import cms.model.topic.TopicUnhide;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 话题服务
 */
public interface TopicService {
    /**
     * 获取话题列表
     * @param page 页码
     * @param visible 是否可见
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Topic> getTopicList(int page, Boolean visible, String fileServerAddress);

    /**
     * 获取话题明细
     * @param topicId 话题Id
     * @param commentId 评论Id
     * @param page 页码
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getTopicDetail(Long topicId, Long commentId, Integer page, HttpServletRequest request);
    /**
     * 获取添加话题界面信息
     * @return
     */
    public Map<String,Object> getAddTopicViewModel();
    /**
     * 添加话题
     * @param topicRequest 话题表单
     * @param request 请求信息
     */
    public void addTopic(TopicRequest topicRequest,HttpServletRequest request);
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
     * 获取修改话题界面信息
     * @param topicId 话题Id
     * @return
     */
    public Map<String,Object> getEditTopicViewModel(Long topicId);
    /**
     * 修改话题
     * @param topicRequest 话题表单
     * @param request 请求信息
     */
    public void editTopic(TopicRequest topicRequest,HttpServletRequest request);
    /**
     * 删除话题
     * @param topicId 话题Id集合
     */
    public void deleteTopic(Long[] topicId);

    /**
     * 还原话题
     * @param topicId 话题Id集合
     */
    public void reductionTopic(Long[] topicId);
    /**
     * 审核话题
     * @param topicId 话题Id
     */
    public void auditTopic(Long topicId);
    /**
     * 获取话题搜索
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
    public PageView<Topic> getSearch(int page, Integer dataSource,String keyword,String tagId,String tagName,String account,
                                     String start_postTime,String end_postTime,String fileServerAddress);

    /**
     * 获取全部待审核话题
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Topic> getPendingTopics(int page, String fileServerAddress);
    /**
     * 获取全部待审核评论
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Comment> getAllPendingComments(int page,String fileServerAddress);
    /**
     * 获取全部待审核回复
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Reply> getAllPendingReplies(int page, String fileServerAddress);
    /**
     * 获取话题取消隐藏列表
     * @param page 页码
     * @param topicId 话题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getUnhiddenTopics(int page,Long topicId,String fileServerAddress);
}
