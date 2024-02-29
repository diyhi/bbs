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
 * 6.1升级到6.2版本执行程序
 *
 */
public class Upgrade6_1to6_2 {
	private static final Logger logger = LogManager.getLogger(Upgrade6_1to6_2.class);
	
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
    			
    			updateSQL_sysresources(upgradeService);
    	    	upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改资源表URL",1))+",");
    			
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
		String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('46c8721b9f004ba395201bd86c49375c','POST','AUTH_43210759422e407da5f5ac5cc1f7fe3d_POST_UPDATE',2,'修改员工自身信息'),('829f7bb9b92a46aab434622e36d229f8','GET','AUTH_43210759422e407da5f5ac5cc1f7fe3d_GET_READ',1,'修改员工自身信息页'),('45cac3b5741644998511a4982e06acde','POST','AUTH_374f5b9bffd443b9b2b4a830112951cb_POST_UPDATE',1,'恢复答案回复'),('25ba6537e4ef4bc8ad0530534975e426','GET','AUTH_879f7ed98c6d4af5b07a7dd2c98833d5_GET_READ',1,'积分日志明细'),('2bb181ae95ef4fe39a78b5737616f448','POST','AUTH_04acd924ee1f4ef8b60efd03a35998a3_POST_UPDATE',1,'设置默认角色');";
		
		upgradeService.insertNativeSQL(sql);
		
	}
		
	
	      
	/**
	 * 插入升级SQL
	 * @param upgradeService
	 */
	private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
		
		String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'829f7bb9b92a46aab434622e36d229f8','43210759422e407da5f5ac5cc1f7fe3d'),(NULL,'46c8721b9f004ba395201bd86c49375c','43210759422e407da5f5ac5cc1f7fe3d'),(NULL,'45cac3b5741644998511a4982e06acde','374f5b9bffd443b9b2b4a830112951cb'),(NULL,'25ba6537e4ef4bc8ad0530534975e426','879f7ed98c6d4af5b07a7dd2c98833d5'),(NULL,'2bb181ae95ef4fe39a78b5737616f448','04acd924ee1f4ef8b60efd03a35998a3');";
		
		upgradeService.insertNativeSQL(sql);

	}
	
	/**
		 * 插入升级SQL
		 * @param upgradeService
		 */
	private static void insertSQL_sysresources(UpgradeService upgradeService){
		  	
		String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('43210759422e407da5f5ac5cc1f7fe3d','员工管理','修改员工自身信息',30100,'','/control/staff/manage?method=editSelfInfo*',NULL,NULL),('374f5b9bffd443b9b2b4a830112951cb','问答管理','恢复答案回复',21550,'','/control/answer/manage?method=recoveryReply*',NULL,NULL),('879f7ed98c6d4af5b07a7dd2c98833d5','会员管理','积分日志明细',27510,'','/control/pointLog/manage?method=show&*',NULL,NULL),('04acd924ee1f4ef8b60efd03a35998a3','会员管理','设置默认角色',27850,'','/control/userRole/manage?method=setAsDefault*',NULL,NULL);";
		
		
		upgradeService.insertNativeSQL(sql);

	}
	
	
	/**
   	 * 修改资源表URL
   	 * @param upgradeService
   	 */
    private static void updateSQL_sysresources(UpgradeService upgradeService){
    	String sql_1 = "UPDATE sysresources SET url = '/control/comment/manage?method=addComment*'  WHERE url = '/control/comment/manage?method=add*';";
       	upgradeService.insertNativeSQL(sql_1);
    	String sql_2 = "UPDATE sysresources SET url = '/control/comment/manage?method=editComment*'  WHERE url = '/control/comment/manage?method=edit*';";
       	upgradeService.insertNativeSQL(sql_2);
       	String sql_3 = "UPDATE sysresources SET url = '/control/comment/manage?method=deleteComment*'  WHERE url = '/control/comment/manage?method=delete*';";
       	upgradeService.insertNativeSQL(sql_3);
       	
       	
       	String sql_4 = "UPDATE sysresources SET url = '/control/answer/manage?method=add&*'  WHERE url = '/control/answer/manage?method=add*';";
       	upgradeService.insertNativeSQL(sql_4);
    	String sql_5 = "UPDATE sysresources SET url = '/control/answer/manage?method=edit&*'  WHERE url = '/control/answer/manage?method=edit*';";
       	upgradeService.insertNativeSQL(sql_5);
       	String sql_6 = "UPDATE sysresources SET url = '/control/answer/manage?method=delete&*'  WHERE url = '/control/answer/manage?method=delete*';";
       	upgradeService.insertNativeSQL(sql_6);
       	
    }
}
