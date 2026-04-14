package cms.service.admin;

import cms.dto.admin.AdminAuthorization;

import java.util.Map;

/**
 * 管理员服务
 */
public interface AdminService {

    /**
     * 登录界面信息
     * @param username 员工账号
     * @return
     */
    public Map<String, Object> getLoginViewModel(String username);


    /**
     * 登录
     * @param username 员工账号
     * @param password 密码
     * @param captchaKey 验证码key
     * @param captchaValue 验证码值
     * @param ip IP地址
     * @return
     */
    public AdminAuthorization login(String username, String password, String captchaKey, String captchaValue, String ip);


        /**
         * 管理员刷新访问令牌
         * @param refreshToken 刷新令牌
         * @return
         */
    public AdminAuthorization refreshAccessToken(String refreshToken);
    /**
     * 撤销令牌
     * @param accessToken 访问令牌
     */
    public void revokeToken(String accessToken);

    /**
     * 查询管理员信息
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> queryAdminInfo(String fileServerAddress);

    /**
     * 查询管理员概览数据
     * @return
     */
    public Map<String,Object> adminOverview();
}
