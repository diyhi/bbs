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
 * 4.8升级到4.9版本执行程序
 *
 */
public class Upgrade4_8to4_9 {
	
	
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
		String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('2ebdff3458fb4d7c9ca31a218afc90af','POST','AUTH_59a96171493346c0b463150a25dd6213_POST_UPDATE',2,'修改文件系统接口'),('35f952bab9d443008088e702edb13dd3','POST','AUTH_b6a21b6a8d4f42aeaf2c5e7deeed85b3_POST_DELETE',1,'删除文件系统接口'),('36107be91fc241a282eb7de274b0fc69','POST','AUTH_7867d6ca24c94edcbd52fdb8cfe4a8de_POST_UPDATE',1,'恢复评论'),('6ed84f80a1e843e3ae66c022496bf712','POST','AUTH_fe96c07df5364cdcbc86088748af49db_POST_ADD',2,'添加文件系统接口'),('afb57aa600074540a550b5762f7aa6db','POST','AUTH_b697a7d318d34693b5e1f50988bcb325_POST_UPDATE',1,'恢复评论回复'),('be9f292427a9491ea0acbc4f09391178','GET','AUTH_59a96171493346c0b463150a25dd6213_GET_READ',1,'修改文件系统接口页'),('d81d8535e2074c65b8adf5032334a6f9','GET','AUTH_fe96c07df5364cdcbc86088748af49db_GET_READ',1,'添加文件系统接口页'),('de711e4912964310aeb6ce9054f00c67','GET','AUTH_663ca2f809ad45c083eccee9f803edd4_GET_READ',1,'文件系统接口列表');";
		
		upgradeService.insertNativeSQL(sql);
	}
		
	
	      
	/**
	 * 插入升级SQL
	 * @param upgradeService
	 */
	private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
		
		String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'36107be91fc241a282eb7de274b0fc69','7867d6ca24c94edcbd52fdb8cfe4a8de'),(NULL,'afb57aa600074540a550b5762f7aa6db','b697a7d318d34693b5e1f50988bcb325'),(NULL,'de711e4912964310aeb6ce9054f00c67','663ca2f809ad45c083eccee9f803edd4'),(NULL,'d81d8535e2074c65b8adf5032334a6f9','fe96c07df5364cdcbc86088748af49db'),(NULL,'6ed84f80a1e843e3ae66c022496bf712','fe96c07df5364cdcbc86088748af49db'),(NULL,'be9f292427a9491ea0acbc4f09391178','59a96171493346c0b463150a25dd6213'),(NULL,'2ebdff3458fb4d7c9ca31a218afc90af','59a96171493346c0b463150a25dd6213'),(NULL,'35f952bab9d443008088e702edb13dd3','b6a21b6a8d4f42aeaf2c5e7deeed85b3');";
		
	    upgradeService.insertNativeSQL(sql);
	
	}
	
	/**
		 * 插入升级SQL
		 * @param upgradeService
		 */
	private static void insertSQL_sysresources(UpgradeService upgradeService){
		  	
		String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('59a96171493346c0b463150a25dd6213','文件系统接口管理','修改文件系统接口',53200,'','/control/fileSystemInterface/manage.htm?method=edit&*',NULL,NULL),('663ca2f809ad45c083eccee9f803edd4','文件系统接口管理','文件系统接口列表',53000,'','/control/fileSystemInterface/list*',NULL,NULL),('7867d6ca24c94edcbd52fdb8cfe4a8de','话题管理','恢复评论',13550,'','/control/comment/manage.htm?method=recoveryComment*',NULL,NULL),('b697a7d318d34693b5e1f50988bcb325','话题管理','恢复评论回复',14550,'','/control/comment/manage.htm?method=recoveryReply*',NULL,NULL),('b6a21b6a8d4f42aeaf2c5e7deeed85b3','文件系统接口管理','删除文件系统接口',53300,'','/control/fileSystemInterface/manage.htm?method=delete*',NULL,NULL),('fe96c07df5364cdcbc86088748af49db','文件系统接口管理','添加文件系统接口',53100,'','/control/fileSystemInterface/manage.htm?method=add*',NULL,NULL);";
		
		upgradeService.insertNativeSQL(sql);
	}
    
    
    
}
