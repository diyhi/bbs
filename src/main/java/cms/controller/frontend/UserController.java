package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.annotation.RoleAnnotation;
import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.frontendModule.*;
import cms.dto.user.ResourceEnum;
import cms.model.user.User;
import cms.model.user.UserLoginLog;
import cms.service.frontend.UserClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 前台用户控制器
 */
@RestController("frontendUserController")
public class UserController {

    @Resource
    UserClientService userClientService;
    @Resource
    FileComponent fileComponent;


    /**
     * 会员注册表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1180100)
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public Map<String,Object> registerUI() {
        return userClientService.getRegisterViewModel();
    }


    /**
     * 会员注册
     * @param registerDTO         会员注册表单
     * @param result      存储校验信息
     * @param request          请求信息
     * @param response         响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1180200)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Map<String,Object> register(@ModelAttribute RegisterDTO registerDTO, BindingResult result,
                                       HttpServletRequest request, HttpServletResponse response) {
        return userClientService.register(registerDTO,request,response);
    }

    /**
     * 会员登录表单
     * @param request      请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1180400)
    @RequestMapping(value="/login",method=RequestMethod.GET)
    public Map<String,Object> loginUI(HttpServletRequest request){
        return userClientService.getLoginViewModel(request);
    }

    /**
     * 会员登录
     * @param loginDTO 会员登录表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1180500)
    @RequestMapping(value="/login",method=RequestMethod.POST)
    public Map<String,Object> login(@ModelAttribute LoginDTO loginDTO, BindingResult result,
                        HttpServletRequest request, HttpServletResponse response){
        return userClientService.login(loginDTO,request,response);
    }

    /**
     * 会员退出
     * @param request 请求信息
     * @param response 响应信息
	 * @return
	 */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1180600)
    @RequestMapping(value="/logout",method=RequestMethod.POST)
    public Map<String,Object> logout(HttpServletRequest request, HttpServletResponse response){
        return userClientService.logout(request,response);
    }


    /**
     * 找回密码 第一步表单
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1180700)
    @RequestMapping(value="/forgotPassword/step1",method=RequestMethod.GET)
    public Map<String,Object> forgotPassword_step1_UI(HttpServletRequest request, HttpServletResponse response){
        return userClientService.getForgotPasswordStep1ViewModel();
    }

    /**
     * 找回密码 第一步
     * @param forgotPasswordStep1DTO 找回密码第一步表单
     * @param result      存储校验信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1180800)
    @RequestMapping(value="/forgotPassword/step1",method=RequestMethod.POST)
    public Map<String,Object> findPassWord_step1(@ModelAttribute ForgotPasswordStep1DTO forgotPasswordStep1DTO, BindingResult result){
        return userClientService.forgotPasswordStep1(forgotPasswordStep1DTO);
    }

    /**
     * 找回密码 第二步表单
     * @param userName 用户名称
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1180900)
    @RequestMapping(value="/forgotPassword/step2",method=RequestMethod.GET)
    public Map<String,Object> forgotPassword_step2_UI(String userName){
        return userClientService.getForgotPasswordStep2ViewModel(userName);
    }

    /**
     * 找回密码 第二步
     * @param forgotPasswordStep2DTO 找回密码第二步表单
     * @param result      存储校验信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1181000)
    @RequestMapping(value="/forgotPassword/step2",method=RequestMethod.POST)
    public Map<String,Object> forgotPassword_step2(@ModelAttribute ForgotPasswordStep2DTO forgotPasswordStep2DTO, BindingResult result){
        return userClientService.forgotPasswordStep2(forgotPasswordStep2DTO);
    }

    /**
     * 会话续期
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1181100)
    @RequestMapping(value="/refreshToken",method=RequestMethod.POST)
    public Map<String,Object> refreshToken(HttpServletRequest request, HttpServletResponse response){
        return userClientService.refreshToken(request,response);
    }

    /**
     * 恢复微信浏览器会话
     * 微信浏览器被清理缓存后公众号自动登录
     * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1181200)
    @RequestMapping(value="/recoverWeChatBrowserSession",method=RequestMethod.POST)
    public Map<String,Object> recoverWeChatBrowserSession(String code,
                                              HttpServletRequest request, HttpServletResponse response){
        return userClientService.recoverWeChatBrowserSession(code,request,response);
    }


    /**
     * 实名认证
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1181800)
    @RequestMapping(value="/user/control/realNameAuthentication",method=RequestMethod.GET)
    public Map<String,Object> realNameAuthenticationUI(){
        return userClientService.getRealNameAuthenticationViewModel();
    }

    /**
     * 手机绑定表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1181900)
    @RequestMapping(value="/user/control/phoneBinding",method=RequestMethod.GET)
    public Map<String,Object> phoneBindingUI(){
        return userClientService.getPhoneBindingViewModel();
    }

    /**
     * 手机绑定
     * @param phoneBindingDTO 手机绑定表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1182000)
    @RequestMapping(value="/user/control/phoneBinding",method=RequestMethod.POST)
    public Map<String,Object> phoneBinding(PhoneBindingDTO phoneBindingDTO){
        return userClientService.phoneBinding(phoneBindingDTO);
    }


    /**
     * 修改手机绑定第一步表单  验证旧手机界面
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1182100)
    @RequestMapping(value="/user/control/updatePhoneBinding/step1",method=RequestMethod.GET)
    public Map<String,Object> updatePhoneBinding_1UI(){
        return userClientService.getUpdatePhoneBindingStep1ViewModel();
    }

    /**
     * 修改手机绑定 第一步验证旧手机
     * @param smsCode 短信验证码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1182200)
    @RequestMapping(value="/user/control/updatePhoneBinding/step1",method=RequestMethod.POST)
    public Map<String,Object> updatePhoneBinding_1(String smsCode){
        return userClientService.updatePhoneBinding_1(smsCode);
    }

    /**
     * 修改手机绑定第二步表单  验证新手机界面
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1182300)
    @RequestMapping(value="/user/control/updatePhoneBinding/step2",method=RequestMethod.GET)
    public Map<String,Object> updatePhoneBinding_2UI(){
        return userClientService.getUpdatePhoneBindingStep2ViewModel();
    }

    /**
     * 修改手机绑定 第二步验证新手机
     * @param countryCode 区号
     * @param mobile 手机号
     * @param smsCode 短信验证码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1182400)
    @RequestMapping(value="/user/control/updatePhoneBinding/step2",method=RequestMethod.POST)
    public Map<String,Object> updatePhoneBinding_2(String countryCode,String mobile,String smsCode){
        return userClientService.updatePhoneBinding_2(countryCode, mobile,smsCode);
    }

    /**
     * 根据账号或呢称查询用户
     * @param keyword 关键字
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1181300)
    @RequestMapping(value="/user/control/queryUser",method=RequestMethod.GET)
    public User queryUser(String keyword,
                            HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return userClientService.getUser(keyword,fileServerAddress);
    }

    /**
     * 积分
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1181400)
    @RequestMapping(value="/user/control/point",method=RequestMethod.GET)
    public Map<String,Object> pointUI(PageForm pageForm){
        return userClientService.getPoint(pageForm.getPage());
    }

    /**
     * 会员修改表单
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1181500)
    @RequestMapping(value="/user/control/editUser",method=RequestMethod.GET)
    public Map<String,Object> editUserUI(HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return userClientService.getEditUserViewModel(fileServerAddress);
    }

    /**
     * 会员修改
     * @param userDTO         会员表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1181600)
    @RequestMapping(value="/user/control/editUser",method=RequestMethod.POST)
    public Map<String,Object> editUser(@ModelAttribute UserDTO userDTO, BindingResult result,
                           HttpServletRequest request, HttpServletResponse response){
        return userClientService.editUser(userDTO,request,response);
    }

    /**
     * 更新头像
     * @param imgFile 头像
     * @param request 请求信息
     * @param width          图片宽
     * @param height         图片高
     * @param x              X轴
     * @param y              Y轴
     * @return
     */
    @RoleAnnotation(resourceCode= ResourceEnum._2001000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1181700)
    @RequestMapping(value="/user/control/updateAvatar",method=RequestMethod.POST)
    public Map<String,Object> updateAvatar(MultipartFile imgFile,Integer width, Integer height, Integer x, Integer y,
                               HttpServletRequest request){
        return userClientService.updateAvatar(imgFile,width, height, x, y,request);
    }

    /**
     * 用户登录日志
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1182500)
    @RequestMapping(value="/user/control/userLoginLogList",method=RequestMethod.GET)
    public PageView<UserLoginLog> userLoginLog(PageForm pageForm){
        return userClientService.getUserLoginLog(pageForm.getPage());
    }

    /**
     * 账户余额
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1182600)
    @RequestMapping(value="/user/control/balance",method=RequestMethod.GET)
    public Map<String,Object> balanceUI(PageForm pageForm){
        return userClientService.getBalance(pageForm.getPage());
    }

}