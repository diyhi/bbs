package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;

import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 5.4升级到5.5版本执行程序
 *
 */
public class Upgrade5_4to5_5 {
	
	
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
	private static void insertSQL_syspermission(UpgradeService upgradeService){
		String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('1b7916b970e142888fe14821c935f579','GET','AUTH_3e88a83fff2a476392eccffb64d25b1e_GET_READ',1,'查询会员卡赠送任务'),('2a3de832f5d44914a4b568991aa282dc','POST','AUTH_f14133ca94274ff197996e059a26ad49_POST_UPDATE',2,'修改会员卡赠送任务'),('4f4d36de8c024d8b8befca963b969f2e','GET','AUTH_adb6277a6b4f404faa45d94216ba30ff_GET_READ',1,'会员卡赠送任务列表'),('59e5dcf269e54f09a85a4cd474d2bf14','POST','AUTH_c51b715175a54b968e7fdfa0883cb08e_POST_DELETE',1,'删除会员卡赠送任务'),('7f4f7787832a4455aaa125a41ffad75b','GET','AUTH_95bbc26180f3435f89803a95c630a39e_GET_READ',1,'添加会员卡赠送任务页'),('b3db3d9edfc54405b520c044d805c496','GET','AUTH_db27cdfec51a4c30a066319ff6f08bb9_GET_READ',1,'会员卡赠送项列表(获赠用户)'),('cb3b881e83474c4cb19990a573d3b0da','POST','AUTH_95bbc26180f3435f89803a95c630a39e_POST_ADD',2,'添加会员卡赠送任务'),('e76cfef15b7846b9a5e76f735e7f6026','GET','AUTH_f14133ca94274ff197996e059a26ad49_GET_READ',1,'修改会员卡赠送任务页');";
		
		upgradeService.insertNativeSQL(sql);
	}
		
	
	      
	/**
	 * 插入升级SQL
	 * @param upgradeService
	 */
	private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
		
		String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'4f4d36de8c024d8b8befca963b969f2e','adb6277a6b4f404faa45d94216ba30ff'),(NULL,'7f4f7787832a4455aaa125a41ffad75b','95bbc26180f3435f89803a95c630a39e'),(NULL,'cb3b881e83474c4cb19990a573d3b0da','95bbc26180f3435f89803a95c630a39e'),(NULL,'e76cfef15b7846b9a5e76f735e7f6026','f14133ca94274ff197996e059a26ad49'),(NULL,'2a3de832f5d44914a4b568991aa282dc','f14133ca94274ff197996e059a26ad49'),(NULL,'59e5dcf269e54f09a85a4cd474d2bf14','c51b715175a54b968e7fdfa0883cb08e'),(NULL,'1b7916b970e142888fe14821c935f579','3e88a83fff2a476392eccffb64d25b1e'),(NULL,'b3db3d9edfc54405b520c044d805c496','db27cdfec51a4c30a066319ff6f08bb9');";
		
		upgradeService.insertNativeSQL(sql);
	
	}
	
	/**
		 * 插入升级SQL
		 * @param upgradeService
		 */
	private static void insertSQL_sysresources(UpgradeService upgradeService){
		  	
		String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('3e88a83fff2a476392eccffb64d25b1e','会员卡管理','查看会员卡赠送任务',28778,'','/control/membershipCardGiftTask/manage?method=view*',NULL,NULL),('95bbc26180f3435f89803a95c630a39e','会员卡管理','添加会员卡赠送任务',28772,'','/control/membershipCardGiftTask/manage?method=add*',NULL,NULL),('adb6277a6b4f404faa45d94216ba30ff','会员卡管理','会员卡赠送任务列表',28770,'','/control/membershipCardGiftTask/list*',NULL,NULL),('c51b715175a54b968e7fdfa0883cb08e','会员卡管理','删除会员卡赠送任务',28776,'','/control/membershipCardGiftTask/manage?method=delete*',NULL,NULL),('db27cdfec51a4c30a066319ff6f08bb9','会员卡管理','会员卡赠送项列表(获赠用户)',28779,'','/control/membershipCardGiftTask/manage?method=membershipCardGiftItemList*',NULL,NULL),('f14133ca94274ff197996e059a26ad49','会员卡管理','修改会员卡赠送任务',28774,'','/control/membershipCardGiftTask/manage?method=edit*',NULL,NULL);";
		
		
		upgradeService.insertNativeSQL(sql);
	}
    
}
