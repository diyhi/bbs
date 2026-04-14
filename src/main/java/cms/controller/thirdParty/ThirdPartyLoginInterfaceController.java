package cms.controller.thirdParty;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.thirdParty.ThirdPartyLoginInterface;
import cms.service.thirdParty.ThirdPartyLoginInterfaceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第三方登录接口控制器
 *
 */
@RestController
public class ThirdPartyLoginInterfaceController {
    @Resource
    ThirdPartyLoginInterfaceService thirdPartyLoginInterfaceService;
    /**
     * 第三方登录列表
     * @param pageForm 页码
     * @return
     */
    @RequestMapping("/control/thirdPartyLoginInterface/list")
    public RequestResult thirdPartyLoginInterfaceList(PageForm pageForm){
        PageView<ThirdPartyLoginInterface> pageView = thirdPartyLoginInterfaceService.getThirdPartyLoginInterfaceList(pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
