package cms.service.frontend;

import cms.dto.PageView;
import cms.dto.frontendModule.TopicDTO;
import cms.model.topic.Topic;
import cms.model.topic.TopicUnhide;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 前台话题服务接口
 */
public interface TopicClientService {
    /**
     * 获取话题分页
     * @param page 页码
     * @param tagId 标签Id
     * @param request 请求信息
     * @return
     */
    public PageView<Topic> getTopicPage(int page, Long tagId, HttpServletRequest request);
    /**
     * 获取精华话题分页
     * @param page 页码
     * @param tagId 标签Id
     * @param request 请求信息
     * @return
     */
    public PageView<Topic> getFeaturedTopicPage(int page, Long tagId, HttpServletRequest request);

    /**
     * 获取相似话题
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    public List<Topic> getSimilarTopic(Long topicId, HttpServletRequest request);
    /**
     * 获取热门话题
     * @param request 请求信息
     * @return
     */
    public List<Topic> getHotTopic(HttpServletRequest request);
    /**
     * 获取话题明细
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    public Topic getTopicDetail(Long topicId, HttpServletRequest request);

    /**
     * 话题解锁
     * @param topicId 话题Id
     * @param hideType 隐藏类型
     * @param password 密码
     * @return
     */
    public Map<String,Object> topicUnhide(Long topicId, Integer hideType, String password);

    /**
     * 获取添加话题界面信息
     * @return
     */
    public Map<String,Object> getAddTopicViewModel();
    /**
     * 添加话题
     * @param topicDTO 话题表单
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> addTopic(TopicDTO topicDTO, HttpServletRequest request);
    /**
     * 获取修改话题界面信息
     * @param topicId 话题Id
     * @return
     */
    public Map<String,Object> getEditTopicViewModel(Long topicId);
    /**
     * 修改话题
     * @param topicDTO 话题表单
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> editTopic(TopicDTO topicDTO, HttpServletRequest request);
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
     * 获取我的话题
     * @param page 页码
     * @return
     */
    public PageView<Topic> getTopicList(int page);
    /**
     * 获取话题解锁用户列表
     * @param page 页码
     * @param topicId 话题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<TopicUnhide> getTopicUnhideList(int page,Long topicId,String fileServerAddress);
}
