package cms.controller.help;


import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.help.Help;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import cms.service.help.HelpService;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 帮助管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/help/manage")
public class HelpManageController {
    @Resource
    SettingRepository settingRepository;
    @Resource
    HelpService helpService;
    @Resource
    FileComponent fileComponent;


    /**
     * 帮助   查看
     *
     * @param helpId  帮助Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params = "method=view", method = RequestMethod.GET)
    public RequestResult view(Long helpId,
                              HttpServletRequest request) {
        List<String> serverAddressList = fileComponent.fileServerAllAddress(request);
        Help help = helpService.getHelp(helpId, WebUtil.getUrl(request), serverAddressList);
        return new RequestResult(ResultCode.SUCCESS, help);
    }

    /**
     * 帮助   添加界面显示
     *
     * @return
     */
    @RequestMapping(params = "method=add", method = RequestMethod.GET)
    public RequestResult addHelpUI() {
        Map<String, Object> returnValue = new LinkedHashMap<String, Object>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 帮助  添加
     *
     * @param helpTypeId      帮助分类Id
     * @param name            名称
     * @param content         内容
     * @param isMarkdown      是否为Markdown格式数据
     * @param markdownContent Markdown内容
     * @param request         请求信息
     * @return
     */
    @RequestMapping(params = "method=add", method = RequestMethod.POST)
    public RequestResult addHelp(Long helpTypeId, String name, String content, Boolean isMarkdown, String markdownContent, HttpServletRequest request) {

        helpService.addHelp(helpTypeId, name, content, isMarkdown, markdownContent, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 文件上传
     *
     * @param helpTypeId 帮助分类Id
     * @param dir        上传类型，分别为image、flash、media、file
     * @param fileName   文件名称 预签名时有值
     * @param file       文件
     * @param request    请求信息
     * @return
     */
    @RequestMapping(params = "method=upload", method = RequestMethod.POST)
    public Map<String, Object> upload(Long helpTypeId, String dir, String fileName,
                                      MultipartFile file, HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return helpService.uploadFile(helpTypeId, dir, fileName, fileServerAddress, file);
    }

    /**
     * 帮助   修改界面显示
     *
     * @param helpId 帮助Id
     * @return
     */
    @ResponseBody
    @RequestMapping(params = "method=edit", method = RequestMethod.GET)
    public RequestResult editHelpUI(Long helpId) {
        Help help = helpService.getEditHelpViewModel(helpId);
        return new RequestResult(ResultCode.SUCCESS, help);
    }


    /**
     * 帮助  修改
     *
     * @param helpId          帮助Id
     * @param helpTypeId      帮助分类Id
     * @param name            名称
     * @param content         内容
     * @param isMarkdown      是否为Markdown格式数据
     * @param markdownContent Markdown内容
     * @param request         请求信息
     * @return
     */
    @RequestMapping(params = "method=edit", method = RequestMethod.POST)
    public RequestResult editHelp(Long helpId, Long helpTypeId, String name, String content, Boolean isMarkdown, String markdownContent,
                                  HttpServletRequest request) {
        helpService.editHelp(helpId, helpTypeId, name, content, isMarkdown, markdownContent, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 帮助   删除
     *
     * @param helpId 帮助Id组
     * @return
     */
    @RequestMapping(params = "method=delete", method = RequestMethod.POST)
    public RequestResult deleteHelp(Long[] helpId) {
        helpService.deleteHelp(helpId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 帮助   还原
     *
     * @param helpId 帮助Id组
     * @return
     */
    @RequestMapping(params = "method=reduction", method = RequestMethod.POST)
    public RequestResult reductionHelp(Long[] helpId) {
        helpService.reductionHelp(helpId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 移动
     *
     * @param helpId     帮助Id集合
     * @param helpTypeId 分类Id
     * @return
     */
    @ResponseBody
    @RequestMapping(params = "method=move", method = RequestMethod.POST)
    public RequestResult moveHelp(Long[] helpId, Long helpTypeId) {
        helpService.moveHelp(helpId, helpTypeId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 搜索帮助分页
     *
     * @param keyword 关键词
     * @return
     */
    @ResponseBody
    @RequestMapping(params = "method=ajax_searchHelpPage", method = RequestMethod.GET)
    public RequestResult searchHelpPage(PageForm pageForm, String keyword) {
        PageView<Help> pageView = helpService.searchHelp(keyword, pageForm.getPage());

        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}