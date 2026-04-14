package cms.service.user.impl;


import cms.component.JsonComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.user.User;
import cms.model.user.UserLoginLog;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.user.UserLoginLogService;
import cms.utils.IpAddress;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录日志服务
 */
@Service
public class UserLoginLogServiceImpl implements UserLoginLogService {

    @Resource
    UserRepository userRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;


    /**
     * 获取用户登录日志列表
     * @param page 页码
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getUserLoginLogList(int page,Long id, String fileServerAddress){
        if(id == null || id <=0){
            throw new BusinessException(Map.of("id", "用户Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //调用分页算法代码
        PageView<UserLoginLog> pageView = new PageView<UserLoginLog>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();;
        QueryResult<UserLoginLog> qr = userRepository.findUserLoginLogPage(id, firstIndex, pageView.getMaxresult());

        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(UserLoginLog userLoginLog : qr.getResultlist()){
                if(userLoginLog.getIp() != null && !userLoginLog.getIp().trim().isEmpty()){
                    userLoginLog.setIpAddress(IpAddress.queryAddress(userLoginLog.getIp()));
                }
            }
        }

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        User user = userRepository.findUserById(id);
        if(user != null){
            User currentUser = new User();
            currentUser.setId(user.getId());
            currentUser.setAccount(user.getAccount());
            currentUser.setNickname(user.getNickname());
            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                currentUser.setAvatarPath(fileServerAddress+user.getAvatarPath());
                currentUser.setAvatarName(user.getAvatarName());
            }
            returnValue.put("currentUser", currentUser);
        }
        returnValue.put("pageView", pageView);
        return returnValue;
    }


}
