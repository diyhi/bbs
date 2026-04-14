package cms.service.user.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.user.PointLogConfig;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.user.PointLog;
import cms.model.user.User;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.user.PointLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 积分日志服务
 */
@Service
public class PointLogServiceImpl implements PointLogService {


    @Resource
    UserRepository userRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    PointLogConfig pointLogConfig;
    @Resource UserCacheManager userCacheManager;


    /**
     * 获取积分日志列表
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器地址
     */
    public Map<String, Object> getPointLogList(int page, String userName,String fileServerAddress){
        if(userName == null || userName.trim().isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }
        User user = userRepository.findUserByUserName(userName);
        if(user == null){
            throw new BusinessException(Map.of("user", "用户不存在"));
        }

        Map<String,Object> returnValue = new HashMap<String,Object>();
        //调用分页算法代码
        PageView<PointLog> pageView = new PageView<PointLog>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<PointLog> qr =  userRepository.findPointLogPage(user.getId(),user.getUserName(),firstIndex, pageView.getMaxresult());

        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(PointLog pointLog : qr.getResultlist()){
                if(pointLog.getOperationUserType().equals(1)){//员工
                    pointLog.setOperationAccount(pointLog.getOperationUserName());//员工用户名和账号是同一个
                }else if(pointLog.getOperationUserType().equals(2)){//会员
                    User _user = userCacheManager.query_cache_findUserByUserName(pointLog.getOperationUserName());
                    if(_user != null){
                        pointLog.setOperationAccount(_user.getAccount());
                    }
                }
            }
        }

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);

        User currentUser = new User();
        currentUser.setId(user.getId());
        currentUser.setAccount(user.getAccount());
        currentUser.setNickname(user.getNickname());
        if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
            currentUser.setAvatarPath(fileServerAddress+user.getAvatarPath());
            currentUser.setAvatarName(user.getAvatarName());
        }
        returnValue.put("currentUser", currentUser);
        returnValue.put("pageView", pageView);
        return returnValue;
    }

    /**
     * 积分日志明细
     * @param pointLogId 积分日志Id
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String, Object> getPointLogDetail(String pointLogId,String userName,String fileServerAddress){
        if(pointLogId == null || pointLogId.trim().isEmpty()){
            throw new BusinessException(Map.of("pointLogId", "积分Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        if(pointLogId.trim().length() == 36 && pointLogConfig.verificationPointLogId(pointLogId)){
            PointLog pointLog = userRepository.findPointLogById(pointLogId);
            User user = userRepository.findUserByUserName(userName);
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

            if(pointLog.getOperationUserType().equals(1)){//员工
                pointLog.setOperationAccount(pointLog.getOperationUserName());//员工用户名和账号是同一个
            }else if(pointLog.getOperationUserType().equals(2)){//会员
                User _user = userCacheManager.query_cache_findUserByUserName(pointLog.getOperationUserName());
                if(_user != null){
                    pointLog.setOperationAccount(_user.getAccount());
                }
            }

            returnValue.put("pointLog", pointLog);
        }

        return returnValue;
    }
}