package cms.controller.frontendModule;


import cms.component.frontendModule.FrontendApiComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 前台API列表控制器
 * @author Administrator
 *
 */
@RestController
public class FrontendApiController {
	private static final Logger logger = LogManager.getLogger(FrontendApiController.class);

    @Resource
    FrontendApiComponent frontendApiComponent;

    /**
     * 前台API列表
     * @return
     */
    @RequestMapping("/control/frontendApi/list")
    public RequestResult frontendApiList(){

        Map<String,Object> returnValue = new LinkedHashMap<>();
        returnValue.put("frontendCustomApi", frontendApiComponent.getFrontendCustomApiList());
        returnValue.put("frontendDefaultApi", frontendApiComponent.getFrontendDefaultApiList());

        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }



}
