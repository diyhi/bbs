package cms.service.frontend;


import cms.dto.PageView;
import cms.dto.frontendModule.*;
import cms.model.user.User;
import cms.model.user.UserLoginLog;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 前台用户服务接口
 */
public interface UserClientService {
    /**
     * 获取会员注册界面信息
     * @return
     */
    public Map<String,Object> getRegisterViewModel();

    /**
     * 会员注册
     * @param registerDTO 注册用户表单
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> register(RegisterDTO registerDTO, HttpServletRequest request, HttpServletResponse response);
    /**
     * 获取会员登录界面信息
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getLoginViewModel(HttpServletRequest request);
    /**
     * 会员登录
     * @param loginDTO 注册登录表单
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> login(LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response);
    /**
     * 会话续期
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> refreshToken(HttpServletRequest request, HttpServletResponse response);
    /**
     * 恢复微信浏览器会话
     * 微信浏览器被清理缓存后公众号自动登录
     * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> recoverWeChatBrowserSession(String code, HttpServletRequest request, HttpServletResponse response);
    /**
     * 会员退出
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> logout(HttpServletRequest request, HttpServletResponse response);
    /**
     * 获取找回密码第一步界面信息
     * @return
     */
    public Map<String,Object> getForgotPasswordStep1ViewModel();
    /**
     * 获取找回密码第一步
     * @param forgotPasswordStep1DTO 找回密码第一步表单
     * @return
     */
    public Map<String,Object> forgotPasswordStep1(ForgotPasswordStep1DTO forgotPasswordStep1DTO);
    /**
     * 获取找回密码第二步界面信息
     * @param userName 用户名称
     * @return
     */
    public Map<String,Object> getForgotPasswordStep2ViewModel(String userName);
    /**
     * 获取找回密码第二步
     * @param forgotPasswordStep2DTO 找回密码第二步表单
     * @return
     */
    public Map<String,Object> forgotPasswordStep2(ForgotPasswordStep2DTO forgotPasswordStep2DTO);
    /**
     * 获取实名认证界面信息
     * @return
     */
    public Map<String,Object> getRealNameAuthenticationViewModel();
    /**
     * 获取手机绑定界面信息
     * @return
     */
    public Map<String,Object> getPhoneBindingViewModel();
    /**
     * 手机绑定
     * @param phoneBindingDTO 手机绑定表单
     * @return
     */
    public Map<String,Object> phoneBinding(PhoneBindingDTO phoneBindingDTO);
    /**
     * 获取修改手机绑定第一步界面信息
     * @return
     */
    public Map<String,Object> getUpdatePhoneBindingStep1ViewModel();
    /**
     * 修改手机绑定第一步
     * @param smsCode 短信验证码
     * @return
     */
    public Map<String,Object> updatePhoneBinding_1(String smsCode);
    /**
     * 获取修改手机绑定第二步界面信息
     * @return
     */
    public Map<String,Object> getUpdatePhoneBindingStep2ViewModel();
    /**
     * 修改手机绑定第二步
     * @param countryCode 区号
     * @param mobile 手机号
     * @param smsCode 短信验证码
     * @return
     */
    public Map<String,Object> updatePhoneBinding_2(String countryCode,String mobile,String smsCode);
    /**
     * 根据账号或呢称查询用户
     * @param keyword 关键字
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public User getUser(String keyword, String fileServerAddress);
    /**
     * 获取积分
     * @param page 页码
     * @return
     */
    public Map<String,Object> getPoint(int page);
    /**
     * 获取修改会员界面信息
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditUserViewModel(String fileServerAddress);
    /**
     * 修改会员
     * @param userDTO 会员表单
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> editUser(UserDTO userDTO, HttpServletRequest request,HttpServletResponse response);
    /**
     * 更新头像
     * @param imgFile 图片文件
     * @param width  图片宽
     * @param height 图片高
     * @param x      X轴
     * @param y      Y轴
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> updateAvatar(MultipartFile imgFile, Integer width, Integer height, Integer x, Integer y,HttpServletRequest request);
    /**
     * 获取用户登录日志
     * @param page 页码
     * @return
     */
    public PageView<UserLoginLog> getUserLoginLog(int page);
    /**
     * 获取账户余额
     * @param page 页码
     * @return
     */
    public Map<String,Object> getBalance(int page);

}
