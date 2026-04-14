package cms.service.message;

import java.util.Map;

/**
 * 私信服务
 */
public interface PrivateMessageService {

    /**
     * 获取私信列表
     * @param page 页码
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getPrivateMessageList(int page,Long id,String fileServerAddress);
    /**
     * 获取私信列表
     * @param page 页码
     * @param id 用户Id
     * @param friendUserId 对方用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getPrivateMessageChatList(Integer page,Long id,Long friendUserId,String fileServerAddress);
    /**
     * 删除私信对话
     * @param userId 用户Id
     * @param friendUserId 对方用户Id
     */
    public void deletePrivateMessageChat(Long userId,Long friendUserId);
    /**
     * 还原私信
     * @param userId 用户Id
     * @param privateMessageId 私信Id
     */
    public void reductionPrivateMessage(Long userId,String privateMessageId);
}
