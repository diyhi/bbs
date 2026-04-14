package cms.service.follow.impl;


import cms.component.JsonComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.follow.Follow;
import cms.model.follow.Follower;
import cms.model.user.User;
import cms.repository.follow.FollowRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.follow.FollowService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 关注服务
 */
@Service
public class FollowServiceImpl implements FollowService {

    @Resource
    FollowRepository followRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    UserRepository userRepository;

    /**
     * 获取关注列表
     * @param page 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getFollowList(int page,Long id,String userName,String fileServerAddress){
        if(id == null || id <=0){
            throw new BusinessException(Map.of("id", "用户Id不能为空"));
        }
        if(userName == null || userName.trim().isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //调用分页算法代码
        PageView<Follow> pageView = new PageView<Follow>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<Follow> qr = followRepository.findFollowByUserName(id,userName,firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Follow follow : qr.getResultlist()){
                User user = userRepository.findUserByUserName(follow.getFriendUserName());
                if(user != null){
                    follow.setFriendAccount(user.getAccount());
                    follow.setFriendNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        follow.setFriendAvatarPath(fileServerAddress+user.getAvatarPath());
                        follow.setFriendAvatarName(user.getAvatarName());
                    }
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

    /**
     * 获取粉丝列表
     * @param page 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getFollowerList(int page,Long id,String userName,String fileServerAddress){
        if(id == null || id <=0){
            throw new BusinessException(Map.of("id", "用户Id不能为空"));
        }
        if(userName == null || userName.trim().isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //调用分页算法代码
        PageView<Follower> pageView = new PageView<Follower>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<Follower> qr = followRepository.findFollowerByUserName(id,userName,firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Follower follower : qr.getResultlist()){
                User user = userRepository.findUserByUserName(follower.getFriendUserName());
                if(user != null){
                    follower.setFriendAccount(user.getAccount());
                    follower.setFriendNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        follower.setFriendAvatarPath(fileServerAddress+user.getAvatarPath());
                        follower.setFriendAvatarName(user.getAvatarName());
                    }
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
