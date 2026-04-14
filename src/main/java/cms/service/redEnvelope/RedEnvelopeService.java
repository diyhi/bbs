package cms.service.redEnvelope;


import java.util.Map;

/**
 * 红包服务
 */
public interface RedEnvelopeService {

    /**
     * 获取发红包列表
     * @param page 页码
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getGiveRedEnvelopeList(int page,Long id, String fileServerAddress);
    /**
     * 获取发红包金额分配列表
     * @param page 页码
     * @param giveRedEnvelopeId 发红包Id
     * @param id 用户Id
     * @param topicId 话题Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getRedEnvelopeAmountDistributionList(int page,String giveRedEnvelopeId,Long id,Long topicId, String fileServerAddress);
    /**
     * 获取收红包列表
     * @param page 页码
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getReceiveRedEnvelopeList(int page,Long id, String fileServerAddress);
}
