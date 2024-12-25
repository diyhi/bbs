package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 6.4升级到6.5版本执行程序
 *
 */
public class Upgrade6_4to6_5 {
	private static final Logger logger = LogManager.getLogger(Upgrade6_4to6_5.class);
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
    	for(int i =0; i< 100; i++){
    		upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
    		
    		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
    		if(upgradeSystem == null || upgradeSystem.getRunningStatus().equals(9999)){
    			break;
    		}
    		if(upgradeSystem.getRunningStatus()>=100 && upgradeSystem.getRunningStatus()<200){
    			insertSQL_forum(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表forum插入SQL成功",1))+",");
				
				insertSQL_layout(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表layout插入SQL成功",1))+",");
				
    			
    			updateSQL_systemsetting(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表systemsetting修改SQL成功",1))+",");
    			
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,200,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级流程结束",1))+",");

    		}
    		
    		
    		if(upgradeSystem.getRunningStatus()>=200 && upgradeSystem.getRunningStatus()<9999){
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,9999,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级完成",1))+",");
				//写入当前BBS版本
				FileUtil.writeStringToFile("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt",upgradeSystem.getId(),"utf-8",false);
				
				//临时目录路径
				String temp_path = "WEB-INF"+File.separator+"data"+File.separator+"temp"+File.separator+"upgrade"+File.separator;
				//删除临时文件夹
				localFileManage.removeDirectory(temp_path+upgradeSystem.getUpdatePackageFirstDirectory()+File.separator);
				
    		}
    		
    		
    		
    		
    	}
    	upgradeManage.taskRunMark_delete();
    }

    
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_forum(UpgradeService upgradeService){
       	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'api','collection',NULL,'AI助手','系统',1,NULL,'6037f3c12d0d4f6ab5657714aeaa4344',4,'systemRelated_aiAssistant_collection_default','查询AI助手',0,'systemRelated_aiAssistant_1');";
       	upgradeService.insertNativeSQL(sql);
       	
       	
       	
    }
   	
   /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`,`accessRequireLogin`) VALUES ('6037f3c12d0d4f6ab5657714aeaa4344','api',-1,NULL,'查询AI助手表单','queryAiAssistant',1,2890,4,b'1');";
        upgradeService.insertNativeSQL(sql);
    }  
    
	
    
    /**
	 * 修改systemsetting表字段
	 * @param upgradeService
	 */
    private static void updateSQL_systemsetting(UpgradeService upgradeService){
    	String updateSQL = "UPDATE systemsetting SET aiAssistant_submitQuantity=5;";
    	upgradeService.insertNativeSQL(updateSQL);
    }
}
