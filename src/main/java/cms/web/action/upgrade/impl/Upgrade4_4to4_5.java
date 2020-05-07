package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;
import cms.bean.template.Templates;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.template.TemplateService;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 4.4升级到4.5版本执行程序
 *
 */
public class Upgrade4_4to4_5 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
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
    				
    				insertSQL_forum(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表forum插入SQL成功",1))+",");
    				
    				insertSQL_layout(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表layout插入SQL成功",1))+",");
    			}
    			
    			
    			updateSQL_user_type(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表User设置字段type初始值成功",1))+",");
    			
    			
    			updateSQL_user_platformUserId(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表User将userName列的值复制到platformUserId列成功",1))+",");
    			
    		
    			
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
    private static void insertSQL_forum(UpgradeService upgradeService){
       	
       	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'第三方登录','系统',1,'login.html','0133f7f7319441138e4a70248c33829b',1,'systemRelated_thirdPartyLogin_collection_default','第三方登录',0,'systemRelated_thirdPartyLogin_1'),(NULL,'default','collection',NULL,'第三方登录','系统',1,NULL,'a808d04033b74c288cd7e5226d222176',4,'systemRelated_thirdPartyLogin_collection_default','第三方登录',0,'systemRelated_thirdPartyLogin_2');";
       	upgradeService.insertNativeSQL(sql);
    }
   	
   /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('a808d04033b74c288cd7e5226d222176','default',-1,NULL,'查询第三方登录','queryThirdPartyLogin',1,2170,4);";
        upgradeService.insertNativeSQL(sql);
        
        
    }
    
   
    
    
    
    /**
   	 * 修改用户表字段 初始type值
   	 * @param upgradeService
   	 */
    private static void updateSQL_user_type(UpgradeService upgradeService){
    	String sql = "UPDATE user SET type=10";
       	upgradeService.insertNativeSQL(sql);
    }
    
    /**
   	 * 修改用户表字段,将userName列的值复制到platformUserId列
   	 * @param upgradeService
   	 */
    private static void updateSQL_user_platformUserId(UpgradeService upgradeService){
    	String sql = "UPDATE user SET platformUserId=userName";
       	upgradeService.insertNativeSQL(sql);
    }
  
    
    
    
    /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
	private static void insertSQL_syspermission(UpgradeService upgradeService){
		String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('44b76b0928494c6d90886568188c849a','POST','AUTH_a10f02c6ecc1470fbf6d449d5b84181e_POST_DELETE',1,'删除第三方登录接口'),('5774f59a2f3e46388dffe8f67986c9e3','GET','AUTH_403e8ffa7ac9419a8ad7a2b4bdb27899_GET_READ',1,'第三方登录接口列表'),('8e87baf20e6b4926979c6296e79a38ec','POST','AUTH_bf7e0ea48b264555a194ccedddf25de5_POST_UPDATE',2,'修改第三方登录接口'),('99175b2b7be34a6fb29d4a7fc953a56f','GET','AUTH_bf7e0ea48b264555a194ccedddf25de5_GET_READ',1,'修改第三方登录接口页'),('c767b5f6098c46deb20d0d0dd943e5a5','GET','AUTH_b0e69631d5a34c12acdf7125e5598d84_GET_READ',1,'添加第三方登录接口页'),('cb64a997e4c3494e861899acc09c6a28','POST','AUTH_b0e69631d5a34c12acdf7125e5598d84_POST_ADD',2,'添加第三方登录接口');";
		upgradeService.insertNativeSQL(sql);
		
		
	}
	      
	/**
	 * 插入升级SQL
	 * @param upgradeService
	 */
	private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
		
		String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'5774f59a2f3e46388dffe8f67986c9e3','403e8ffa7ac9419a8ad7a2b4bdb27899'),(NULL,'c767b5f6098c46deb20d0d0dd943e5a5','b0e69631d5a34c12acdf7125e5598d84'),(NULL,'cb64a997e4c3494e861899acc09c6a28','b0e69631d5a34c12acdf7125e5598d84'),(NULL,'99175b2b7be34a6fb29d4a7fc953a56f','bf7e0ea48b264555a194ccedddf25de5'),(NULL,'8e87baf20e6b4926979c6296e79a38ec','bf7e0ea48b264555a194ccedddf25de5'),(NULL,'44b76b0928494c6d90886568188c849a','a10f02c6ecc1470fbf6d449d5b84181e');";
	      	
	    upgradeService.insertNativeSQL(sql);
	}
	
	/**
		 * 插入升级SQL
		 * @param upgradeService
		 */
	private static void insertSQL_sysresources(UpgradeService upgradeService){
		  	
		String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('403e8ffa7ac9419a8ad7a2b4bdb27899','第三方服务管理','第三方登录接口列表',54000,'','/control/thirdPartyLoginInterface/list.htm*',NULL,NULL),('a10f02c6ecc1470fbf6d449d5b84181e','第三方服务管理','删除第三方登录接口',54300,'','/control/thirdPartyLoginInterface/manage.htm?method=delete*',NULL,NULL),('b0e69631d5a34c12acdf7125e5598d84','第三方服务管理','添加第三方登录接口',54100,'','/control/thirdPartyLoginInterface/manage.htm?method=add*',NULL,NULL),('bf7e0ea48b264555a194ccedddf25de5','第三方服务管理','修改第三方登录接口',54200,'','/control/thirdPartyLoginInterface/manage.htm?method=edit&*',NULL,NULL);";
		
		upgradeService.insertNativeSQL(sql);
	}

}
