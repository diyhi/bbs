package cms.service.frontend.impl;

import cms.component.follow.*;
import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.frontendModule.FollowStatusDTO;
import cms.dto.frontendModule.FollowerCountDTO;
import cms.dto.frontendModule.FollowingCountDTO;
import cms.dto.riskControl.BlockedFeaturesEnum;
import cms.dto.user.AccessUser;
import cms.model.follow.Follow;
import cms.model.follow.Follower;
import cms.model.message.Remind;
import cms.model.setting.SystemSetting;
import cms.model.user.User;
import cms.repository.follow.FollowRepository;
import cms.repository.message.RemindRepository;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.FollowClientService;
import cms.utils.AccessUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * 前台关注服务
 */
@Service
public class FollowClientServiceImpl implements FollowClientService {
    @Resource
    UserCacheManager userCacheManager;
    @Resource
    FollowerCacheManager followerCacheManager;
    @Resource
    FollowCacheManager followCacheManager;
    @Resource
    FollowConfig followConfig;
    @Resource
    SettingRepository settingRepository;
    @Resource
    FollowComponent followComponent;
    @Resource
    FollowerConfig followerConfig;
    @Resource
    RemindComponent remindComponent;
    @Resource
    RemindConfig remindConfig;
    @Resource
    FollowRepository followRepository;
    @Resource RemindCacheManager remindCacheManager;
    @Resource
    RemindRepository remindRepository;
    @Resource FollowerComponent followerComponent;

    /**
     * 获取关注总数
     * @param userName 用户名称
     * @return
     */
    public FollowingCountDTO getFollowingCount(String userName){
        if(userName == null || userName.trim().isEmpty()) {
            return new FollowingCountDTO(0L);
        }
        User user = userCacheManager.query_cache_findUserByUserName(userName.trim());
        if(user == null){
            return new FollowingCountDTO(0L);
        }
        Long count = followCacheManager.query_cache_followCount(user.getId(), user.getUserName());
        if(count == null){
            return new FollowingCountDTO(0L);
        }
        return new FollowingCountDTO(count);
    }

    /**
     * 获取粉丝总数
     * @param userName 用户名称
     * @return
     */
    public FollowerCountDTO getFollowerCount(String userName){
        if(userName == null || userName.trim().isEmpty()) {
            return new FollowerCountDTO(0L);
        }
        User user = userCacheManager.query_cache_findUserByUserName(userName.trim());
        if(user == null){
            return new FollowerCountDTO(0L);
        }

        Long count = followerCacheManager.query_cache_followerCount(user.getId(), user.getUserName());
        if(count == null){
            return new FollowerCountDTO(0L);
        }
        return new FollowerCountDTO(count);
    }
    /**
     * 是否已经关注该用户
     * @param userName 用户名称
     * @return
     */
    public FollowStatusDTO isFollowing(String userName){
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null && userName != null && !userName.trim().isEmpty()){
            User user = userCacheManager.query_cache_findUserByUserName(userName.trim());
            if(user != null){
                //生成关注Id
                String followId = followConfig.createFollowId(accessUser.getUserId(),user.getId());
                Follow follow = followCacheManager.query_cache_findById(followId);
                if(follow != null){
                    return new FollowStatusDTO(true);
                }
            }
        }
        return new FollowStatusDTO(false);
    }

    /**
     * 添加关注
     * @param userName 用户名称
     * @return
     */
    public Map<String,Object> addFollow(String userName){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("follow", "只读模式不允许提交数据"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();



        User user = null;
        if(userName != null && !"".equals(userName.trim())){
            user = userCacheManager.query_cache_findUserByUserName(userName.trim());//查询缓存
        }else{
            errors.put("follow", "用户名称不能为空");
        }

        if(user != null){
            //关注Id
            String followId = followConfig.createFollowId(accessUser.getUserId(), user.getId());

            Follow follow = followCacheManager.query_cache_findById(followId);

            if(follow != null){
                errors.put("follow", "当前用户已关注对方");
            }

            if(user.getId().equals(accessUser.getUserId())){
                errors.put("follow", "不能关注自身");
            }

        }else{
            errors.put("follow", "用户不存在");
        }

        if(errors.size() == 0){
            LocalDateTime time = LocalDateTime.now();
            Follow follow = new Follow();
            follow.setId(followConfig.createFollowId(accessUser.getUserId(), user.getId()));
            follow.setAddtime(time);
            follow.setUserName(accessUser.getUserName());
            follow.setFriendUserName(user.getUserName());

            Follower follower = new Follower();
            follower.setId(followerConfig.createFollowerId(user.getId(), accessUser.getUserId()));
            follower.setAddtime(time);
            follower.setUserName(user.getUserName());
            follower.setFriendUserName(accessUser.getUserName());
            try {
                //保存关注
                followRepository.saveFollow(followComponent.createFollowObject(follow),followerComponent.createFollowerObject(follower));

                //删除关注缓存
                followCacheManager.delete_cache_findById(follow.getId());
                followerCacheManager.delete_cache_followerCount(user.getUserName());
                followCacheManager.delete_cache_findAllFollow(accessUser.getUserName());
                followCacheManager.delete_cache_followCount(accessUser.getUserName());

                if(!user.getId().equals(accessUser.getUserId())){//会员关注自已不发提醒给自己

                    //提醒对方
                    Remind remind = new Remind();
                    remind.setId(remindConfig.createRemindId(user.getId()));
                    remind.setReceiverUserId(user.getId());//接收提醒用户Id
                    remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                    remind.setTypeCode(80);//80.别人关注了我
                    remind.setSendTimeFormat(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() );//发送时间格式化


                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(user.getId());
                }

            } catch (org.springframework.orm.jpa.JpaSystemException e) {
                errors.put("follow", "重复关注");

            }

        }

        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
        }else{
            returnValue.put("success", true);
        }
        return returnValue;
    }
    /**
     * 删除关注
     * @param followId 关注Id
     * @return
     */
    public Map<String,Object> deleteFollow(String followId){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("follow", "只读模式不允许提交数据"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        Follow follow = null;



        if(followId == null || followId.trim().isEmpty()){
            errors.put("follow", "关注Id不存在");
        }else{
            follow  = followRepository.findById(followId.trim());
            if(follow != null){
                if(!follow.getUserName().equals(accessUser.getUserName())){
                    errors.put("follow", "本关注不属于当前用户");
                }
            }else{
                errors.put("follow", "关注不存在");
            }
        }

        if(errors.size() == 0){
            String[] idGroup = followId.trim().split("-");
            //粉丝Id
            String followerId = idGroup[1]+"-"+idGroup[0];

            int i = followRepository.deleteFollow(followId.trim(),followerId);
            if(i == 0){
                errors.put("follow", "删除关注失败");
            }
            //删除关注缓存
            followCacheManager.delete_cache_findById(followId.trim());
            followerCacheManager.delete_cache_followerCount(follow.getFriendUserName());

            followCacheManager.delete_cache_findAllFollow(accessUser.getUserName());
            followCacheManager.delete_cache_followCount(accessUser.getUserName());
        }

        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
        }else{
            returnValue.put("success", true);

        }
        return returnValue;
    }
    /**
     * 获取关注列表
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Follow> getFollowList(int page, String fileServerAddress){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        //调用分页算法代码
        PageView<Follow> pageView = new PageView<Follow>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<Follow> qr = followRepository.findFollowByUserName(accessUser.getUserId(),accessUser.getUserName(),firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Follow follow : qr.getResultlist()){
                User user = userCacheManager.query_cache_findUserByUserName(follow.getFriendUserName());//查询缓存
                if(user != null && user.getCancelAccountTime().equals(-1L)){
                    follow.setFriendAccount(user.getAccount());
                    follow.setFriendNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        follow.setFriendAvatarPath(fileServerAddress+user.getAvatarPath());
                        follow.setFriendAvatarName(user.getAvatarName());
                    }
                }else{
                    follow.setFriendUserName(null);
                }
            }
        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return  pageView;
    }

    /**
     * 获取粉丝列表
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Follower> getFollowerList(int page, String fileServerAddress){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        //调用分页算法代码
        PageView<Follower> pageView = new PageView<Follower>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<Follower> qr = followRepository.findFollowerByUserName(accessUser.getUserId(),accessUser.getUserName(),firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Follower follower : qr.getResultlist()){
                User user = userCacheManager.query_cache_findUserByUserName(follower.getFriendUserName());//查询缓存
                if(user != null && user.getCancelAccountTime().equals(-1L)){
                    follower.setFriendAccount(user.getAccount());
                    follower.setFriendNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        follower.setFriendAvatarPath(fileServerAddress+user.getAvatarPath());
                        follower.setFriendAvatarName(user.getAvatarName());
                    }
                }else{
                    follower.setFriendUserName(null);
                }
            }
        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return  pageView;
    }
}
