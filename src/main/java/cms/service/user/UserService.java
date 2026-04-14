package cms.service.user;

import cms.dto.PageView;
import cms.dto.payment.PaymentRequest;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.model.topic.Topic;
import cms.model.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 获取用户列表
     *
     * @param page              页码
     * @param visible           null或true:正常页面  false:回收站
     * @param fileServerAddress 文件服务器随机地址
     */
    public PageView<User> getUserList(int page, Boolean visible, String fileServerAddress);

    /**
     * 搜索用户列表
     *
     * @param page                   页码
     * @param searchType             查询类型
     * @param userTypeCode           查询用户类型
     * @param keyword                关键词
     * @param start_point            起始积分
     * @param end_point              结束积分
     * @param start_registrationDate 起始注册日期
     * @param end_registrationDate   结束注册日期
     * @param fileServerAddress      文件服务器随机地址
     * @param request                请求信息
     * @return
     */
    public Map<String, Object> searchUset(int page, Integer searchType, Integer userTypeCode, String keyword,
                                          String start_point, String end_point,
                                          String start_registrationDate, String end_registrationDate, String fileServerAddress, HttpServletRequest request);

    /**
     * 查询用户
     * @param keyword 关键字
     * @param fileServerAddress 文件服务器随机地址
     */
    public User queryUser(String keyword,String fileServerAddress);

    /**
     * 获取用户明细
     * @param id              用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getUserDetails(Long id, String fileServerAddress);

    /**
     * 获取添加用户界面信息
     * @return
     */
    public Map<String, Object> getAddUserViewModel();
    /**
     * 添加用户
     * @param userForm 用户表单
     * @param userRolesId 角色Id
     * @param request 请求信息
     */
    public void addUser( User userForm, String[] userRolesId,HttpServletRequest request);

    /**
     * 获取修改用户界面信息
     * @param id 用户Id
     * @return
     */
    public Map<String, Object> getEditUserViewModel(Long id);
    /**
     * 修改用户
     * @param userForm    用户表单
     * @param userRolesId 角色Id
     * @param request     请求信息
     */
    public void editUser(User userForm, String[] userRolesId, HttpServletRequest request);

    /**
     * 删除用户
     * @param userId 用户Id集合
     */
    public void deleteUser(Long[] userId);
    /**
     * 还原用户
     * @param userId 用户Id集合
     */
    public void reductionUser(Long[] userId);

    /**
     * 更新头像
     * @param file 文件
     * @param id 用户Id
     * @param width 图片宽
     * @param height 图片高
     * @param x X轴
     * @param y Y轴
     */
    public  void updateAvatar(MultipartFile file,Integer width, Integer height, Integer x, Integer y, Long id);

    /**
     * 付款
     * @param paymentRequest 付款表单
     */
    public void payment(PaymentRequest paymentRequest);
    /**
     * 注销账号
     * @param id 用户Id
     */
    public void cancelAccount(Long id);

    /**
     * 获取用户发表的话题
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<Topic> getUserTopic(int page, String userName,String fileServerAddress);
    /**
     * 获取用户发表的评论
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<Comment> getUserComment(int page, String userName, String fileServerAddress);

    /**
     * 获取用户发表的评论回复
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<Reply> getUserCommentReply(int page, String userName, String fileServerAddress);

    /**
     * 获取用户发表的问题
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<Question> getUserQuestion(int page, String userName, String fileServerAddress);

    /**
     * 获取用户发表的答案
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<Answer> getUserAnswer(int page, String userName, String fileServerAddress);
    /**
     * 获取用户发表的答案回复
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<AnswerReply> getUserAnswerReply(int page, String userName, String fileServerAddress);

}