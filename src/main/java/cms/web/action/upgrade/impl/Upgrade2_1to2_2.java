package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;

import cms.bean.template.Templates;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.template.TemplateService;
import cms.service.upgrade.UpgradeService;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.FileManage;
import cms.web.action.upgrade.UpgradeManage;

/**
 * 2.1升级到2.2版本执行程序
 *
 */
public class Upgrade2_1to2_2 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	FileManage fileManage = (FileManage)SpringConfigTool.getContext().getBean("fileManage");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
    	
    	for(int i =0; i< 100; i++){
    		upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
    		
    		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
    		if(upgradeSystem == null || upgradeSystem.getRunningStatus().equals(9999)){
    			break;
    		}
    		if(upgradeSystem.getRunningStatus()>=100 && upgradeSystem.getRunningStatus()<200){
    			//插入升级SQL
    			Templates templates =templateService.findTemplatebyDirName("default");//查询模板
    			if(templates != null){
    				insertSQL_layout(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表layout插入SQL成功",1))+",");
    			}
    			
    			
    			insertSQL_syspermission(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");
    			
    			insertSQL_syspermissionresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");
    				
    			insertSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");
    			
    			
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,200,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级流程结束",1))+",");

    		}
    		
    		
    		if(upgradeSystem.getRunningStatus()>=200 && upgradeSystem.getRunningStatus()<9999){
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,9999,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级完成，需要再次重启服务器新增的权限功能才能正常使用",1))+",");
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
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	String layout_sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('50f0a08d6cf34fa699f549a95aa5bc47','default',-1,'remindList.html','提醒列表','user/control/remindList',0,550,1);";
    	
    	upgradeService.insertNativeSQL(layout_sql);
		
    	
    }

    
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_syspermission(UpgradeService upgradeService){
    	String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('27464511268442c39980b0a8a25c2663','GET','AUTH_f2ddeb0bb4c94342854c6a2461869fb4_GET_READ',1,'提醒列表'),('3107b49684c54d51b54c45a7307228e0','POST','AUTH_3338673059d44af39b27bf4cddc15bcc_POST_UPDATE',1,'还原提醒');";
    	
    	upgradeService.insertNativeSQL(sql);
    }
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
    	String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (1428,'27464511268442c39980b0a8a25c2663','f2ddeb0bb4c94342854c6a2461869fb4'),(1429,'3107b49684c54d51b54c45a7307228e0','3338673059d44af39b27bf4cddc15bcc');";
    	
    	upgradeService.insertNativeSQL(sql);
    }
    
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_sysresources(UpgradeService upgradeService){
    	String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('f2ddeb0bb4c94342854c6a2461869fb4','会员管理','提醒列表',27400,'','/control/remind/manage.htm?method=remindList&*',NULL,NULL),('3338673059d44af39b27bf4cddc15bcc','会员管理','还原提醒',27450,'','/control/remind/manage.htm?method=reductionRemind&*',NULL,NULL);";
 	
    	upgradeService.insertNativeSQL(sql);
    }
	

	

    
	
	
}
