package cms.service.follow;

import java.util.Map;

/**
 * 关注服务
 */
public interface FollowService {

    /**
     * 获取关注列表
     * @param page 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getFollowList(int page,Long id,String userName,String fileServerAddress);
    /**
     * 获取粉丝列表
     * @param page 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getFollowerList(int page,Long id,String userName,String fileServerAddress);
}
