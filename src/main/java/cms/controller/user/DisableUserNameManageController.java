package cms.controller.user;


import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.user.DisableUserNameRequest;
import cms.dto.user.UserCustomRequest;
import cms.service.user.DisableUserNameService;
import cms.service.user.PointLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * 禁止的用户名称管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/disableUserName/manage")
public class DisableUserNameManageController {

    @Resource
    DisableUserNameService disableUserNameService;
    @Resource FileComponent fileComponent;


    /**
     * 禁止的用户名称管理 添加界面显示
     */
    @RequestMapping(params="method=add",method=RequestMethod.GET)
    public RequestResult addUI() {
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 禁止的用户名称管理 添加
     * @param disableUserNameRequest 禁止的用户名称表单
     * @param result      存储校验信息
     * @return
     */
    @RequestMapping(params="method=add", method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute DisableUserNameRequest disableUserNameRequest, BindingResult result) {
        disableUserNameService.addDisableUserName(disableUserNameRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 禁止的用户名称管理 修改界面显示
     * @param disableUserNameId 禁止的用户名称Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Integer disableUserNameId) {
        Map<String,Object> returnValue = disableUserNameService.getEditDisableUserNameViewModel(disableUserNameId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 禁止的用户名称管理 修改
     * @param disableUserNameRequest 禁止的用户名称表单
     * @param result      存储校验信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute DisableUserNameRequest disableUserNameRequest, BindingResult result) {
        disableUserNameService.editDisableUserName(disableUserNameRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 禁止的用户名称管理 删除
     * @param disableUserNameId 禁止的用户名称Id
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Integer disableUserNameId) {
        disableUserNameService.deleteUDisableUserName(disableUserNameId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
}
