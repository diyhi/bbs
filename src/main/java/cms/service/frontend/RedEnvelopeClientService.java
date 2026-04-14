package cms.service.frontend;

import cms.dto.PageView;
import cms.model.redEnvelope.GiveRedEnvelope;
import cms.model.redEnvelope.ReceiveRedEnvelope;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;


/**
 * 前台红包服务接口
 */
public interface RedEnvelopeClientService {
    /**
     * 获取发红包明细
     * @param giveRedEnvelopeId 发红包Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public GiveRedEnvelope getSentRedEnvelopeDetail(String giveRedEnvelopeId, String fileServerAddress);

    /**
     * 领取红包用户分页
     * @param page 页码
     * @param giveRedEnvelopeId 发红包Id
     * @param request 请求信息
     * @return
     */
    public PageView<ReceiveRedEnvelope> getReceivedRedEnvelopePage(int page, String giveRedEnvelopeId,HttpServletRequest request);

    /**
     * 收红包
     * @param giveRedEnvelopeId 发红包Id
     * @return
     */
    public Map<String,Object> addReceiveRedEnvelope(String giveRedEnvelopeId);
    /**
     * 获取发红包列表
     * @param page 页码
     * @return
     */
    public PageView<GiveRedEnvelope> getGiveRedEnvelopeList(int page);
    /**
     * 获取发红包金额分配列表
     * @param page 页码
     * @param giveRedEnvelopeId 发红包Id
     * @return
     */
    public Map<String,Object> getRedEnvelopeAmountDistributionList(int page, String giveRedEnvelopeId);
    /**
     * 获取收红包列表
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<ReceiveRedEnvelope> getReceiveRedEnvelopeList(int page, String fileServerAddress);
}
