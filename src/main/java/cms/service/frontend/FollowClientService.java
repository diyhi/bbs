package cms.service.frontend;

import cms.dto.PageView;
import cms.dto.frontendModule.FollowStatusDTO;
import cms.dto.frontendModule.FollowerCountDTO;
import cms.dto.frontendModule.FollowingCountDTO;
import cms.model.follow.Follow;
import cms.model.follow.Follower;

import java.util.Map;

/**
 * 前台关注服务接口
 */
public interface  FollowClientService {
    /**
     * 获取关注总数
     * @param userName 用户名称
     * @return
     */
    public FollowingCountDTO getFollowingCount(String userName);
    /**
     * 获取粉丝总数
     * @param userName 用户名称
     * @return
     */
    public FollowerCountDTO getFollowerCount(String userName);
    /**
     * 是否已经关注该用户
     * @param userName 用户名称
     * @return
     */
    public FollowStatusDTO isFollowing(String userName);
    /**
     * 添加关注
     * @param userName 用户名称
     * @return
     */
    public Map<String,Object> addFollow(String userName);
    /**
     * 删除关注
     * @param followId 关注Id
     * @return
     */
    public Map<String,Object> deleteFollow(String followId);

    /**
     * 获取关注列表
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Follow> getFollowList(int page, String fileServerAddress);
    /**
     * 获取粉丝列表
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Follower> getFollowerList(int page, String fileServerAddress);
}
