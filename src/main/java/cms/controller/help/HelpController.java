package cms.controller.help;


import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.help.Help;
import cms.service.help.HelpService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帮助列表控制器
 * @author Administrator
 *
 */
@RestController
public class HelpController {
	private static final Logger logger = LogManager.getLogger(HelpController.class);

    @Resource HelpService helpService;
    @Resource FileComponent fileComponent;



    /**
     * 帮助列表
     * @param pageForm 页码
     * @param visible 是否显示
     * @return
     */
    @RequestMapping("/control/help/list")
    public RequestResult helpList(PageForm pageForm, Boolean visible, HttpServletRequest request){
        PageView<Help> pageView = helpService.getHelpList(visible,pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }



}
