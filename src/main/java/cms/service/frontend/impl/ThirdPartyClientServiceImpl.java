package cms.service.frontend.impl;

import cms.component.*;
import cms.component.fileSystem.FileComponent;
import cms.component.membershipCard.MembershipCardGiftTaskComponent;
import cms.component.setting.SettingComponent;
import cms.component.thirdParty.ThirdPartyCacheManager;
import cms.component.thirdParty.ThirdPartyComponent;
import cms.component.user.UserCacheManager;
import cms.component.user.UserComponent;
import cms.component.user.UserLoginLogComponent;
import cms.component.user.UserLoginLogConfig;
import cms.config.BusinessException;
import cms.dto.thirdParty.*;
import cms.dto.user.AccessUser;
import cms.dto.user.RefreshUser;
import cms.dto.user.UserAuthorization;
import cms.model.setting.AllowLoginAccountType;
import cms.model.setting.AllowRegisterAccountType;
import cms.model.setting.SystemSetting;
import cms.model.thirdParty.SupportLoginInterface;
import cms.model.thirdParty.ThirdPartyLoginInterface;
import cms.model.user.User;
import cms.model.user.UserLoginLog;
import cms.repository.setting.SettingRepository;
import cms.repository.thirdParty.ThirdPartyLoginRepository;
import cms.repository.user.UserRepository;
import cms.service.frontend.ThirdPartyClientService;
import cms.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 前台第三方登录服务
 */
@Service
public class ThirdPartyClientServiceImpl implements ThirdPartyClientService {
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    SettingComponent settingComponent;
    @Resource
    UserRepository userRepository;
    @Resource
    UserComponent userComponent;
    @Resource
    AccessSourceDeviceComponent accessSourceDeviceComponent;
    @Resource
    ThirdPartyLoginRepository thirdPartyLoginRepository;
    @Resource
    ThirdPartyComponent thirdPartyComponent;
    @Resource ThirdPartyCacheManager thirdPartyCacheManager;
    @Resource OAuthCacheManager oAuthCacheManager;
    @Resource
    UserLoginLogComponent userLoginLogComponent;
    @Resource UserCacheManager userCacheManager;
    @Resource
    MembershipCardGiftTaskComponent membershipCardGiftTaskComponent;
    @Resource
    UserLoginLogConfig userLoginLogConfig;
    @Resource TextFilterComponent textFilterComponent;


    /**
     * 获取第三方登录接口
     * @param request 请求信息
     * @return
     */
    public List<SupportLoginInterface> getThirdPartyLogin(HttpServletRequest request){
        List<SupportLoginInterface> supportLoginInterfaceList = new ArrayList<SupportLoginInterface>();

        String accessPath = accessSourceDeviceComponent.accessDevices(request);

        //获取允许登录账号类型
        List<Integer> accountTypeList = settingComponent.getAllowLoginAccountType();

        //显示所有有效的第三方登录接口
        List<ThirdPartyLoginInterface> thirdPartyLoginInterfaceList = thirdPartyLoginRepository.findAllValidThirdPartyLoginInterface_cache();
        //设置第三方登录接口
        if(thirdPartyLoginInterfaceList != null && thirdPartyLoginInterfaceList.size() >0){
            for(ThirdPartyLoginInterface thirdPartyLoginInterface : thirdPartyLoginInterfaceList){

                //接口产品  10.微信 50.其他开放平台
                if(thirdPartyLoginInterface.getInterfaceProduct().equals(10) && !accountTypeList.contains(40)){//如果微信登录入口关闭
                    continue;
                }else if(thirdPartyLoginInterface.getInterfaceProduct().equals(50) && !accountTypeList.contains(80)){//如果其他开放平台登录入口关闭
                    continue;
                }


                if(accessPath.equals("pc")){
                    if(thirdPartyComponent.isSupportEquipment(thirdPartyLoginInterface.getSupportEquipment(), 1)){
                        SupportLoginInterface supportLoginInterface = new SupportLoginInterface();
                        supportLoginInterface.setName(thirdPartyLoginInterface.getName());
                        supportLoginInterface.setInterfaceProduct(thirdPartyLoginInterface.getInterfaceProduct());
                        supportLoginInterface.setThirdPartyLoginInterfaceId(thirdPartyLoginInterface.getId());
                        supportLoginInterfaceList.add(supportLoginInterface);
                    }
                }else if(accessPath.equals("wap")){
                    if(WebUtil.isWeChatBrowser(request)){//微信浏览器
                        if(thirdPartyComponent.isSupportEquipment(thirdPartyLoginInterface.getSupportEquipment(), 4)){
                            SupportLoginInterface supportLoginInterface = new SupportLoginInterface();
                            supportLoginInterface.setName(thirdPartyLoginInterface.getName());
                            supportLoginInterface.setInterfaceProduct(thirdPartyLoginInterface.getInterfaceProduct());
                            supportLoginInterface.setThirdPartyLoginInterfaceId(thirdPartyLoginInterface.getId());
                            supportLoginInterfaceList.add(supportLoginInterface);
                        }
                    }else{
                        if(thirdPartyComponent.isSupportEquipment(thirdPartyLoginInterface.getSupportEquipment(), 2)){
                            SupportLoginInterface supportLoginInterface = new SupportLoginInterface();
                            supportLoginInterface.setName(thirdPartyLoginInterface.getName());
                            supportLoginInterface.setInterfaceProduct(thirdPartyLoginInterface.getInterfaceProduct());
                            supportLoginInterface.setThirdPartyLoginInterfaceId(thirdPartyLoginInterface.getId());
                            supportLoginInterfaceList.add(supportLoginInterface);
                        }

                    }
                }

            }

            //排序
            if(accountTypeList != null && accountTypeList.size() >1 && supportLoginInterfaceList.size() >1){
                for(int i = accountTypeList.size()-1; i>=0;i--){
                    Integer accountType = accountTypeList.get(i);
                    for(int j=0; j<supportLoginInterfaceList.size(); j++){
                        SupportLoginInterface supportLoginInterface =  supportLoginInterfaceList.get(j);
                        //接口产品  10.微信 50.其他开放平台
                        if(supportLoginInterface.getInterfaceProduct().equals(10) && accountType.equals(40)){//如果是微信登录
                            //移动元素到首位
                            supportLoginInterfaceList.remove(j);
                            supportLoginInterfaceList.add(0,supportLoginInterface);
                            break;
                        }else if(supportLoginInterface.getInterfaceProduct().equals(50) && accountType.equals(80)){//如果是其他开放平台登录
                            //移动元素到首位
                            supportLoginInterfaceList.remove(j);
                            supportLoginInterfaceList.add(0,supportLoginInterface);
                            break;
                        }
                    }
                }
            }
        }

        return supportLoginInterfaceList;
    }

    /**
     * 获取微信openid
     * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     * @return
     */
    public WeiXinOpenId getWeiXinOpenId(String code){
        if(code == null || code.trim().isEmpty()){
            return null;
        }
        WeiXinOpenId oldWeiXinOpenId = thirdPartyCacheManager.getWeiXinOpenId(code.trim());
        if(oldWeiXinOpenId != null){
            //只允许查一次
            thirdPartyCacheManager.deleteWeiXinOpenId(code.trim());
            return oldWeiXinOpenId;
        }else{
            return thirdPartyComponent.queryWeiXinOpenId(code.trim());
        }
    }


    /**
     * 生成微信用户信息
     * @param unionId 第三方用户信息唯一凭证
     * @param openId 用户的唯一标识
     * @param error 错误信息集合
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    private UserAuthorization createWeiXinUserInfo(String unionId,String openId,Map<String,String> error,
                                                   HttpServletRequest request, HttpServletResponse response){

        String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(unionId,40);
        User user = userRepository.findUserByPlatformUserId(platformUserId);


        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            error.put("weiXinUserInfo", "只读模式不允许提交数据");
        }else{
            if(user == null){//用户不存在，执行注册

                //读取允许注册账号类型
                AllowRegisterAccountType allowRegisterAccountType =  settingComponent.readAllowRegisterAccountType();

                if(allowRegisterAccountType != null && allowRegisterAccountType.isWeChat()){
                    user = new User();
                    String id = UUIDUtil.getUUID22();
                    user.setUserName(id);//会员用户名
                    user.setAccount(userComponent.queryUserIdentifier(40)+"-"+id);//账号
                    user.setSalt(UUIDUtil.getUUID32());//盐值
                    user.setSecurityDigest(new Date().getTime());//安全摘要
                    user.setAllowUserDynamic(true);//是否允许显示用户动态
                    user.setRealNameAuthentication(false);//是否实名认证
                    user.setRegistrationDate(LocalDateTime.now());//注册日期
                    user.setPoint(0L);//当前积分
                    user.setDeposit(new BigDecimal("0"));//当前预存款
                    user.setType(40);//用户类型 40:微信用户
                    user.setPlatformUserId(platformUserId);//平台用户Id
                    user.setState(1);//用户状态    1:正常用户
                    user.setUserVersion(0);//版本号


                    try {
                        userRepository.saveUser(user,null,null);
                    } catch (Exception e) {
                        error.put("register", "注册会员出错");
                        // TODO Auto-generated catch block
                        //e.printStackTrace();
                    }
                }else{//如果不允许注册
                    error.put("register","不允许注册");


                }


            }
        }


        //读取允许登录账号类型
        AllowLoginAccountType allowLoginAccountType =  settingComponent.readAllowLoginAccountType();
        if(!allowLoginAccountType.isWeChat()){
            error.put("register", "登录入口已关闭");
        }

        //自动登录
        if(error.size() == 0 && user != null){

            if(user.getState().equals(1)){
                //写入登录日志
                UserLoginLog userLoginLog = new UserLoginLog();
                userLoginLog.setId(userLoginLogConfig.createUserLoginLogId(user.getId()));
                userLoginLog.setIp(IpAddress.getClientIpAddress(request));
                userLoginLog.setUserId(user.getId());
                userLoginLog.setTypeNumber(10);//登录
                userLoginLog.setLogonTime(LocalDateTime.now());
                Object new_userLoginLog = userLoginLogComponent.createUserLoginLogObject(userLoginLog);
                userRepository.saveUserLoginLog(new_userLoginLog);


                //访问令牌
                String accessToken = UUIDUtil.getUUID32();
                //刷新令牌
                String refreshToken = UUIDUtil.getUUID32();

                if(openId != null && !openId.trim().isEmpty()){
                    oAuthCacheManager.addOpenId(openId,refreshToken);
                }

                AccessUser accessUser = new AccessUser(user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(), user.getSecurityDigest(),false,openId,10);
                oAuthCacheManager.addAccessToken(accessToken, accessUser);
                oAuthCacheManager.addRefreshToken(refreshToken, new RefreshUser(accessToken,user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),false,openId,10));

                //将访问令牌添加到Cookie
                WebUtil.addCookie(request,response, "cms_accessToken", accessToken, 0);
                //将刷新令牌添加到Cookie
                WebUtil.addCookie(request,response, "cms_refreshToken", refreshToken, 0);
                AccessUserThreadLocal.set(new AccessUser(user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),false,openId,10));

                //删除缓存
                userCacheManager.delete_cache_findUserById(user.getId());
                userCacheManager.delete_cache_findUserByUserName(user.getUserName());

                //异步执行会员卡赠送任务(长期任务类型)
                membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(user.getUserName());

                return new UserAuthorization(accessToken,refreshToken, accessUser);
            }else{
                error.put("register", "禁止账号");
            }


        }
        return null;
    }



    /**
     * 获取第三方登录链接
     * @param interfaceProduct 接口产品
     * @param jumpUrl 重定向参数
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getLoginLink(Integer interfaceProduct,String jumpUrl, HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        String domain = textFilterComponent.domain(request);

        if(domain != null && !domain.trim().isEmpty()){
            if(interfaceProduct != null){
                if(interfaceProduct.equals(10)){//微信
                    if(accessSourceDeviceComponent.accessDevices(request).equals("pc")){//电脑端
                        WeChatConfig weChatConfig = thirdPartyComponent.queryWeChatConfig();
                        if(weChatConfig != null){
                            String appid = weChatConfig.getOp_appID();//开放平台唯一标识
                            String redirect_uri = domain+"thirdParty/loginRedirect";

                            if(jumpUrl != null && !jumpUrl.trim().isEmpty()){
                                redirect_uri = domain+"thirdParty/loginRedirect?jumpUrl="+jumpUrl.trim();//Base64安全编码;
                            }
                            redirect_uri = java.net.URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8);

                            //授权接口
                            returnValue.put("redirectUrl", "https://open.weixin.qq.com/connect/qrconnect?appid="+appid+"&redirect_uri="+redirect_uri+"&response_type=code&scope=snsapi_login&state="+interfaceProduct+"#wechat_redirect");

                        }
                    }else if(accessSourceDeviceComponent.accessDevices(request).equals("wap")){//手机端
                        WeChatConfig weChatConfig = thirdPartyComponent.queryWeChatConfig();
                        if(weChatConfig != null){
                            String appid = weChatConfig.getOa_appID();//公众号唯一标识

                            String redirect_uri = domain+"thirdParty/loginRedirect";

                            if(jumpUrl != null && !jumpUrl.trim().isEmpty()){
                                redirect_uri = domain+"thirdParty/loginRedirect?jumpUrl="+jumpUrl.trim();//Base64安全编码;
                            }
                            redirect_uri = java.net.URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8);

                            //授权接口
                            returnValue.put("redirectUrl", "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+redirect_uri+"&response_type=code&scope=snsapi_userinfo&state="+interfaceProduct+"&connect_redirect=1#wechat_redirect");

                        }
                    }
                }
            }
        }

        return returnValue;
    }

    /**
     * 获取第三方登录重定向
     * @param code 微信公众号code
     * @param state 自定义参数   微信公众号state 存放csrf令牌
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> getLoginRedirect(String code,String state,
                                               HttpServletRequest request, HttpServletResponse response){
        Map<String,String> errors = new HashMap<String,String>();

        Integer interfaceProduct = -1;//接口产品
        UserAuthorization userAuthorization = null;
        //判断令牌
        if(state == null || state.trim().isEmpty()) {
            throw new BusinessException(Map.of("state", "自定义参数不能为空"));
        }
        String[] param_arr = state.trim().split("_");
        interfaceProduct = Integer.parseInt(param_arr[0]);

        if(interfaceProduct.equals(10)){//微信
            WeChatConfig weChatConfig = thirdPartyComponent.queryWeChatConfig();
            if(weChatConfig != null){
                String appid = "";//应用唯一标识
                String secret = "";//应用密钥

                if(accessSourceDeviceComponent.accessDevices(request).equals("pc")){//电脑端
                    appid = weChatConfig.getOp_appID();
                    secret = weChatConfig.getOp_appSecret();
                }else if(accessSourceDeviceComponent.accessDevices(request).equals("wap")){//手机端
                    //微信浏览器端
                    appid = weChatConfig.getOa_appID();
                    secret = weChatConfig.getOa_appSecret();
                }

                if(appid != null && !appid.trim().isEmpty() && secret != null && !secret.trim().isEmpty()){
                    WeiXinUserInfo weiXinUserInfo = thirdPartyComponent.queryWeiXinUserInfo(code,appid,secret);
                    if(weiXinUserInfo != null){
                        if(weiXinUserInfo.getErrorCode() == null || weiXinUserInfo.getErrorCode().isEmpty()){


                            if(weiXinUserInfo.getUnionId() != null && !weiXinUserInfo.getUnionId().isEmpty()){
                                userAuthorization = this.createWeiXinUserInfo(weiXinUserInfo.getUnionId(),weiXinUserInfo.getOpenId(), errors,request, response);

                            }else{
                                errors.put("weiXinUserInfo", "微信unionid为空，请将公众号绑定到微信开放平台");
                            }
                        }else{
                            errors.put("weiXinUserInfo", weiXinUserInfo.getErrorCode()+" -- "+weiXinUserInfo.getErrorMessage());
                        }
                    }else{

                        errors.put("weiXinUserInfo", "查询微信用户基本信息为空");
                    }
                }


            }else{
                errors.put("weChatConfig", "微信配置信息不存在");
            }
        }

        Map<String,Object> returnValue = new HashMap<String,Object>();

        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);

        }else{
            returnValue.put("success", true);
            returnValue.put("userAuthorization", userAuthorization);
        }
        return returnValue;
    }
}
