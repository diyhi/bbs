package cms.controller.admin;


import java.util.*;

import cms.component.JsonComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.admin.AdminAuthorization;
import cms.service.admin.AdminService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import cms.utils.IpAddress;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;


/**
 * 管理员控制器
 * @author Administrator
 *
 */
@RestController
public class AdminController {
	private static final Logger logger = LogManager.getLogger(AdminController.class);

    @Resource AdminService adminService;
    @Resource JsonComponent jsonComponent;

	/**
	 * 员工登录页面显示
	 * @param username 员工账号
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/admin/login",method=RequestMethod.GET) 
	public RequestResult loginUI(String username){
        Map<String, Object> returnValue = adminService.getLoginViewModel(username);

        return new RequestResult(ResultCode.SUCCESS, returnValue);
	}
	
	/**
	 * 员工登录
	 * @param username 账号
	 * @param password 密码
	 * @param captchaKey 验证码编号
	 * @param captchaValue 验证码值
     * @param request 请求信息
	 * @return
	 */
	@RequestMapping(value="/admin/login",method=RequestMethod.POST)
	public RequestResult login(String username,String password,String captchaKey,String captchaValue,
			HttpServletRequest request){
        String ip = IpAddress.getClientIpAddress(request);
        AdminAuthorization adminAuthorization = adminService.login(username,password, captchaKey, captchaValue, ip);

        return new RequestResult(ResultCode.SUCCESS, adminAuthorization);
	}
	
	
	
	/**
	 * 刷新令牌
	 * @param refresh_token 刷新令牌
	 * @return
     */
	@RequestMapping(value="/admin/refreshToken",method=RequestMethod.POST)
	public RequestResult refreshToken(String refresh_token){

        AdminAuthorization authorization = adminService.refreshAccessToken(refresh_token);
        return new RequestResult(ResultCode.SUCCESS,authorization);
	}


    /**
     * 员工退出
     * @param authorization 授权令牌
     * @return
     */
	@RequestMapping(value="/admin/logout",method=RequestMethod.POST)
	public RequestResult logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return new RequestResult(ResultCode.FAILURE, Map.of("logout", "Authorization 请求头必须以 Bearer 开头"));
        }
        String accessToken = authorization.substring(7);
        adminService.revokeToken(accessToken);
        return new RequestResult(ResultCode.SUCCESS,null);
	}

	/**
	 * 当前时间
	 * @return
	 */
	@RequestMapping(value="/admin/currentTime",method=RequestMethod.GET)
	public TimeData currentTime(){
		TimeData time = new TimeData();
		time.setCurrentTime(new Date().getTime());
		
		TimeZone tz = TimeZone.getDefault();
	//	TimeZone tz = TimeZone.getTimeZone("America/New_York");//-5
	
		//获取当前时区的标准时间到GMT的偏移量。相对于“本初子午线”的偏移，单位/毫秒。
		int offset = tz.getRawOffset();
		//时区偏移量 “时间偏移”对应的小时
		int gmt = offset/(3600*1000);
	
		time.setTimezoneOffset(gmt);
		
		return time;
	}

    @Getter
    @Setter
    public static class TimeData {
		//系统当前时间戳
        private Long currentTime;
        //系统当前时区偏移
        private Integer timezoneOffset;
    }

}
