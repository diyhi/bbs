package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;

import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.upgrade.UpgradeService;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.FileManage;
import cms.web.action.cache.CacheManage;
import cms.web.action.upgrade.UpgradeManage;

/**
 * 3.0升级到3.1版本执行程序
 *
 */
public class Upgrade3_0to3_1 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	FileManage fileManage = (FileManage)SpringConfigTool.getContext().getBean("fileManage");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	CacheManage cacheManage = (CacheManage)SpringConfigTool.getContext().getBean("cacheManage");
    	
    	for(int i =0; i< 100; i++){
    		upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
    		
    		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
    		if(upgradeSystem == null || upgradeSystem.getRunningStatus().equals(9999)){
    			break;
    		}
    		if(upgradeSystem.getRunningStatus()>=100 && upgradeSystem.getRunningStatus()<200){
    			updateSQL_systemSetting(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改系统设置(systemsetting)字段成功",1))+",");
    			//清空系统设置缓存
    			cacheManage.clearCache("settingServiceBean_cache");
    			
    			
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,200,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级流程结束",1))+",");

    		}
    		
    		
    		if(upgradeSystem.getRunningStatus()>=200 && upgradeSystem.getRunningStatus()<9999){
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,9999,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级完成",1))+",");
				//写入当前BBS版本
				fileManage.writeStringToFile("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt",upgradeSystem.getId(),"utf-8",false);
				
				//临时目录路径
				String temp_path = "WEB-INF"+File.separator+"data"+File.separator+"temp"+File.separator+"upgrade"+File.separator;
				//删除临时文件夹
				fileManage.removeDirectory(temp_path+upgradeSystem.getUpdatePackageFirstDirectory()+File.separator);
				
    		}
    		
    		
    		
    		
    	}
    	upgradeManage.taskRunMark_delete();
    }
    

    
    /**
	 * 修改系统设置字段
	 * @param upgradeService
	 */
    private static void updateSQL_systemSetting(UpgradeService upgradeService){
    	String userloginlog_sql = "UPDATE systemsetting SET topicEditorTag = '{\"fontname\":true,\"fontsize\":true,\"forecolor\":true,\"hilitecolor\":true,\"bold\":true,\"italic\":true,\"underline\":true,\"removeformat\":true,\"link\":true,\"unlink\":true,\"justifyleft\":true,\"justifycenter\":true,\"justifyright\":true,\"insertorderedlist\":true,\"insertunorderedlist\":true,\"emoticons\":true,\"image\":true,\"imageFormat\":[\"JPG\",\"JPEG\",\"BMP\",\"PNG\",\"GIF\"],\"imageSize\":\"5000\",\"hidePassword\":true,\"hideComment\":true,\"hideGrade\":true,\"hidePoint\":true,\"hideAmount\":true}' WHERE id=1;";
    	upgradeService.insertNativeSQL(userloginlog_sql);
    }
	
 
}
