package cms.controller.setting;


import cms.cache.CacheApiManager;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.setting.SystemSetting;
import cms.service.setting.SystemSettingService;
import cms.validator.staff.SystemSettingValidator;
import jakarta.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统设置管理控制器
 *
 */
@RestController
@RequestMapping("/control/systemSetting/manage")
public class SystemSettingController {
    @Resource SystemSettingService systemSettingService;
    @Resource SystemSettingValidator systemSettingValidator;
    @Resource MessageSource messageSource;
    @Resource CacheApiManager cacheApiManager;

    /**
     * 系统设置 修改界面显示
     * @return
     */
    @RequestMapping(value="edit",method= RequestMethod.GET)
    public RequestResult editSystemSettingUI(){
        Map<String,Object> returnValue =  systemSettingService.getEditSystemSettingViewModel();

        return new RequestResult(ResultCode.SUCCESS,returnValue);
    }


    /**
     * 系统设置 修改
     * @param systemSettingForm 系统设置表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(value="edit",method=RequestMethod.POST)
    public RequestResult editSystemSetting(@ModelAttribute SystemSetting systemSettingForm, BindingResult result){

        //数据校验
        systemSettingValidator.validate(systemSettingForm, result);
        if (result.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }

        systemSettingService.editSystemSetting(systemSettingForm);

        return new RequestResult(ResultCode.SUCCESS,null);
    }

    /**
     * 维护数据 界面显示
     * @return
     */
    @RequestMapping(params="method=maintainData",method=RequestMethod.GET)
    public RequestResult maintainDataUI(){
        return new RequestResult(ResultCode.SUCCESS,null);
    }

    /**
     * 清空所有缓存
     * @return
     */
    @RequestMapping(params="method=clearAllCache",method=RequestMethod.POST)
    public RequestResult clearAllCache(){
        cacheApiManager.clearAllCache();
        return new RequestResult(ResultCode.SUCCESS,null);
    }

    /**
     * 重建话题全文索引
     * @return
     */
    @RequestMapping(params="method=rebuildTopicIndex",method=RequestMethod.POST)
    public RequestResult rebuildTopicIndex(){
        systemSettingService.rebuildTopicIndex();
        return new RequestResult(ResultCode.SUCCESS,null);
    }

    /**
     * 重建问题全文索引
     * @return
     */
    @RequestMapping(params="method=rebuildQuestionIndex",method=RequestMethod.POST)
    public RequestResult rebuildQuestionIndex(){
        systemSettingService.rebuildQuestionIndex();
        return new RequestResult(ResultCode.SUCCESS,null);
    }



    /**
     * 删除浏览量数据
     * @param deletePageViewData_beforeTime 删除指定时间之前的数据
     * @return
     */
    @RequestMapping(params="method=deletePageViewData",method=RequestMethod.POST)
    public RequestResult deletePageViewData(String deletePageViewData_beforeTime){
        systemSettingService.deletePageViewData(deletePageViewData_beforeTime);
        return new RequestResult(ResultCode.SUCCESS,null);
    }

    /**
     * 删除用户登录日志数据
     * @param deleteUserLoginLogData_beforeTime 删除指定时间之前的数据
     * @return
     */
    @RequestMapping(params="method=deleteUserLoginLogData",method=RequestMethod.POST)
    public RequestResult deleteUserLoginLogData(String deleteUserLoginLogData_beforeTime){
        systemSettingService.deleteUserLoginLogData(deleteUserLoginLogData_beforeTime);
        return new RequestResult(ResultCode.SUCCESS,null);
    }



    /**
     * 节点参数 界面显示
     * @return
     */
    @RequestMapping(params="method=nodeParameter",method=RequestMethod.GET)
    public RequestResult nodeParameterUI(){
        Map<String,Object> returnValue = systemSettingService.getNodeParameterViewModel();
        return new RequestResult(ResultCode.SUCCESS,returnValue);
    }
}
