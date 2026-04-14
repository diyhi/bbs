package cms.controller.redEnvelope;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.redEnvelope.RedEnvelopeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 红包列表控制器
 *
 */
@RestController
public class RedEnvelopeController {
    @Resource
    RedEnvelopeService redEnvelopeService;
    @Resource
    FileComponent fileComponent;

    /**
     * 发红包列表
     * @param pageForm 页码
     * @param id 用户Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/redEnvelope/giveRedEnvelope/list")
    public RequestResult giveRedEnvelopeList(PageForm pageForm, Long id, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = redEnvelopeService.getGiveRedEnvelopeList(pageForm.getPage(),id,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 发红包金额分配
     * @param pageForm 页码
     * @param giveRedEnvelopeId 发红包Id
     * @param id 用户Id
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/redEnvelope/redEnvelopeAmountDistribution/list")
    public RequestResult redEnvelopeAmountDistributionList(PageForm pageForm,String giveRedEnvelopeId,Long id,Long topicId, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = redEnvelopeService.getRedEnvelopeAmountDistributionList(pageForm.getPage(),giveRedEnvelopeId,id,topicId,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 发红包列表
     * @param pageForm 页码
     * @param id 用户Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/redEnvelope/receiveRedEnvelope/list")
    public RequestResult receiveRedEnvelopePage(PageForm pageForm, Long id, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = redEnvelopeService.getReceiveRedEnvelopeList(pageForm.getPage(),id,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }
}
