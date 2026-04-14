package cms.controller.filterWord;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.repository.setting.SettingRepository;
import cms.service.filterWord.FilterWordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 文过滤词管理控制器
 */
@RestController
@RequestMapping("/control/filterWord/manage")
public class FilterWordManageController {

    @Resource
    FilterWordService filterWordService;



    /**
     * 过滤词展示
     */
    @RequestMapping(params="method=view",method= RequestMethod.GET)
    public RequestResult view() {
        Map<String,Object> returnValue = filterWordService.getFilterWordView();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 上传词库
     * @param file 文件
     * @return
     */
    @RequestMapping(params="method=uploadFilterWord",method=RequestMethod.POST)
    public RequestResult uploadFilterWord(MultipartFile file) {
        filterWordService.uploadFilterWord(file);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 删除词库
     */
    @RequestMapping(params="method=deleteFilterWord",method=RequestMethod.POST)
    public RequestResult deleteFilterWord() {
        filterWordService.deleteFilterWord();
        return new RequestResult(ResultCode.SUCCESS, null);
    }


}
