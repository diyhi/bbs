package cms.component;

import cms.component.fileSystem.FileComponent;
import cms.component.membershipCard.MembershipCardGiftTaskComponent;
import cms.component.user.UserCacheManager;
import cms.component.user.UserComponent;
import cms.component.user.UserLoginLogComponent;
import cms.component.user.UserLoginLogConfig;
import cms.dto.user.AccessUser;
import cms.dto.user.RefreshUser;
import cms.dto.user.UserAuthorization;
import cms.dto.user.UserState;
import cms.model.user.User;
import cms.model.user.UserLoginLog;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.utils.AccessUserThreadLocal;
import cms.utils.IpAddress;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 开放授权组件
 *
 */
@Component("oAuthComponent")
public class OAuthComponent {

    @Resource
    OAuthCacheManager oAuthCacheManager;
    @Resource UserCacheManager userCacheManager;
    @Resource
    UserComponent userComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    UserLoginLogComponent userLoginLogComponent;
    @Resource
    MembershipCardGiftTaskComponent membershipCardGiftTaskComponent;
    @Resource
    UserRepository userRepository;
    @Resource
    UserLoginLogConfig userLoginLogConfig;



    /**
     * 获取登录用户
     * @param request 请求信息
     * @return
     */
    public AccessUser getUserName(HttpServletRequest request){

        //从Cookie获取
        String accessToken = WebUtil.getCookieByName(request, "cms_accessToken");

        //从Header获取
        UserAuthorization headerUserAuthorization = WebUtil.getAuthorization(request);
        if(headerUserAuthorization != null){
            accessToken = headerUserAuthorization.getAccessToken();
        }

        if(accessToken != null && !accessToken.trim().isEmpty()){
            AccessUser accessUser = oAuthCacheManager.getAccessUserByAccessToken(accessToken.trim());
            return accessUser;
        }
        return null;
    }


    /**
     * 令牌续期
     * @param oldRefreshToken 旧刷新令牌号
     * @param refreshUser 刷新用户
     * @param newAccessToken 新访问令牌  值为UUIDUtil.getUUID32()
     * @param newRefreshToken 新刷新令牌  值为UUIDUtil.getUUID32()
     * @param request 请求信息
     * @param response 响应信息
     * @return 返回true表示续期成功
     * @return
     */
    public boolean tokenRenewal(String oldRefreshToken, RefreshUser refreshUser, String newAccessToken, String newRefreshToken,
                                HttpServletRequest request, HttpServletResponse response){

        UserState userState = userCacheManager.query_userState(refreshUser.getUserName().trim());//用户状态
        if(userState == null || !userState.getSecurityDigest().equals(refreshUser.getSecurityDigest())){
            return false;
        }

        /**
         //第三方登录续期
         if(refreshUser.getLoginInterface().equals(50)){//如果是其他开放平台登录
         OtherAccessToken otherAccessToken = thirdPartyManage.otherRefreshUserToken(oldRefreshToken);
         if(otherAccessToken != null){
         new_accessToken = otherAccessToken.getAccess_token();
         new_refreshToken = otherAccessToken.getRefresh_token();
         }else{
         return false;
         }
         }**/



        User user = userCacheManager.query_cache_findUserById(refreshUser.getUserId());
        if(user != null){
            //账号
            String account = user.getAccount();
            //呢称
            String nickname = user.getNickname();
            //头像路径
            String avatarPath = fileComponent.fileServerAddress(request)+user.getAvatarPath();
            //头像名称
            String avatarName = user.getAvatarName();

            oAuthCacheManager.addAccessToken(newAccessToken, new AccessUser(refreshUser.getUserId(),refreshUser.getUserName(),account,nickname,avatarPath,avatarName, refreshUser.getSecurityDigest(),refreshUser.isRememberMe(),refreshUser.getOpenId(),refreshUser.getLoginInterface()));
            refreshUser.setAccessToken(newAccessToken);
            oAuthCacheManager.addRefreshToken(newRefreshToken, refreshUser);

            if(refreshUser.getOpenId() != null && !"".equals(refreshUser.getOpenId().trim())){
                //第三方openId
                oAuthCacheManager.addOpenId(refreshUser.getOpenId(),newRefreshToken);
            }


            //将旧的刷新令牌的accessToken设为0
            oAuthCacheManager.addRefreshToken(oldRefreshToken, new RefreshUser("0",refreshUser.getUserId(),refreshUser.getUserName(),account,nickname,avatarPath,avatarName,refreshUser.getSecurityDigest(),refreshUser.isRememberMe(),refreshUser.getOpenId(),refreshUser.getLoginInterface()));
            AccessUserThreadLocal.set(new AccessUser(refreshUser.getUserId(),refreshUser.getUserName(),account,nickname,avatarPath,avatarName,refreshUser.getSecurityDigest(),refreshUser.isRememberMe(),refreshUser.getOpenId(),refreshUser.getLoginInterface()));





            //存放时间 单位/秒
            int maxAge = 0;
            if(refreshUser.isRememberMe()){
                maxAge = WebUtil.cookieMaxAge;//默认Cookie有效期
            }

            //将访问令牌添加到Cookie
            WebUtil.addCookie(request,response, "cms_accessToken", newAccessToken, maxAge);
            //将刷新令牌添加到Cookie
            WebUtil.addCookie(request,response, "cms_refreshToken", newRefreshToken, maxAge);

            //写入登录日志
            UserLoginLog userLoginLog = new UserLoginLog();
            userLoginLog.setId(userLoginLogConfig.createUserLoginLogId(user.getId()));
            userLoginLog.setIp(IpAddress.getClientIpAddress(request));
            userLoginLog.setTypeNumber(20);//续期
            userLoginLog.setUserId(user.getId());
            userLoginLog.setLogonTime(LocalDateTime.now());
            Object new_userLoginLog = userLoginLogComponent.createUserLoginLogObject(userLoginLog);
            userRepository.saveUserLoginLog(new_userLoginLog);

            //异步执行会员卡赠送任务(长期任务类型)
            membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(user.getUserName());

            return true;
        }




        return false;
    }
}
