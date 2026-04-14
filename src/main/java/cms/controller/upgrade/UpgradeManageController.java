package cms.controller.upgrade;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.frontendModule.DynamicRouteDTO;
import cms.model.upgrade.UpgradeSystem;
import cms.service.frontendModule.DocumentService;
import cms.service.upgrade.UpgradeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 升级管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/upgrade/manage")
public class UpgradeManageController {
    @Resource
    UpgradeService upgradeService;
    /**
     * 升级列表
     * @return
     */
    @RequestMapping(params="method=upgradeSystemList",method= RequestMethod.GET)
    public RequestResult upgradeSystemList(){
        Map<String,Object> returnValue = upgradeService.getUpgradeSystemList();
        return new RequestResult(ResultCode.SUCCESS,returnValue);
    }

    /**
     * 根据Id查询升级
     * @param upgradeSystemId 升级Id
     * @return
     */
    @RequestMapping(params="method=queryUpgrade",method=RequestMethod.GET)
    public RequestResult queryUpgrade(String upgradeSystemId) {
        UpgradeSystem upgradeSystem = upgradeService.getUpgradeDetails(upgradeSystemId);
        return new RequestResult(ResultCode.SUCCESS,upgradeSystem);
    }

    /**
     * 立即升级
     * @param updatePackageFirstDirectory 升级包目录
     * @return
     */
    @RequestMapping(params="method=upgradeNow",method=RequestMethod.POST)
    public RequestResult upgradeNow(String updatePackageFirstDirectory) {
        upgradeService.upgradeNow(updatePackageFirstDirectory);
        return new RequestResult(ResultCode.SUCCESS,null);
    }

    /**
     * 继续升级
     * @param upgradeId 升级Id
     * @return
     */
    @RequestMapping(params="method=continueUpgrade",method=RequestMethod.POST)
    public RequestResult continueUpgrade(String upgradeId) {
        upgradeService.continueUpgrade(upgradeId);
        return new RequestResult(ResultCode.SUCCESS,null);
    }
}
