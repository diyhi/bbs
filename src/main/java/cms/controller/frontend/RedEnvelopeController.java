package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.model.redEnvelope.GiveRedEnvelope;
import cms.model.redEnvelope.ReceiveRedEnvelope;
import cms.service.frontend.RedEnvelopeClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台红包控制器
 */
@RestController("frontendRedEnvelopeController")
public class RedEnvelopeController {
    @Resource
    RedEnvelopeClientService redEnvelopeClientService;
    @Resource
    FileComponent fileComponent;

    /**
     * 发红包明细
     * @param giveRedEnvelopeId 发红包Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6070100)
    public GiveRedEnvelope sentRedEnvelopeDetail(String giveRedEnvelopeId, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return redEnvelopeClientService.getSentRedEnvelopeDetail(giveRedEnvelopeId, fileServerAddress);
    }

    /**
     * 领取红包用户分页
     * @param pageForm 页码
     * @param giveRedEnvelopeId 发红包Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6070200)
    public PageView<ReceiveRedEnvelope> receivedRedEnvelopePage(PageForm pageForm, String giveRedEnvelopeId, HttpServletRequest request) {
        return redEnvelopeClientService.getReceivedRedEnvelopePage(pageForm.getPage(), giveRedEnvelopeId,request);
    }

    /**
     * 收红包
     * @param giveRedEnvelopeId 发红包Id
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1070100)
    @RequestMapping(value="/user/control/redEnvelope/addReceiveRedEnvelope", method= RequestMethod.POST)
    public Map<String,Object> addReceiveRedEnvelope(String giveRedEnvelopeId,
                                                    HttpServletRequest request, HttpServletResponse response){
        return redEnvelopeClientService.addReceiveRedEnvelope(giveRedEnvelopeId);
    }
    /**
     * 发红包列表
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1070200)
    @RequestMapping(value="/user/control/giveRedEnvelopeList",method=RequestMethod.GET)
    public PageView<GiveRedEnvelope> giveRedEnvelopeListUI(PageForm pageForm){
        return redEnvelopeClientService.getGiveRedEnvelopeList(pageForm.getPage());
    }

    /**
     * 发红包金额分配列表
     * @param pageForm 页码
     * @param giveRedEnvelopeId 发红包Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1070300)
    @RequestMapping(value="/user/control/redEnvelopeAmountDistributionList",method=RequestMethod.GET)
    public Map<String,Object> redEnvelopeAmountDistributionUI(PageForm pageForm, String giveRedEnvelopeId){
        return redEnvelopeClientService.getRedEnvelopeAmountDistributionList(pageForm.getPage(),giveRedEnvelopeId);
    }

    /**
     * 收红包列表
     * @param pageForm 页码
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1070400)
    @RequestMapping(value="/user/control/receiveRedEnvelopeList",method=RequestMethod.GET)
    public PageView<ReceiveRedEnvelope> receiveRedEnvelopeUI(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return redEnvelopeClientService.getReceiveRedEnvelopeList(pageForm.getPage(),fileServerAddress);
    }
}
