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
 * 4.0升级到4.1版本执行程序
 *
 */
public class Upgrade4_0to4_1 {
	
	
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
    				
    				insertSQL_forum(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表forum插入SQL成功",1))+",");
    				
    				insertSQL_layout(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表layout插入SQL成功",1))+",");
    			}
    			
    			
    			
    			updateSQL_question(upgradeService);
    	    	upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改question表appendContent字段",1))+",");
    	    	
    	
    	       	updateSQL_sysresources(upgradeService);
    	    	upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改资源表问题URL",1))+",");
     	    	
    	    	
    	    	
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
   	 * 修改问题表追加内容字段
   	 * @param upgradeService
   	 */
    private static void updateSQL_question(UpgradeService upgradeService){
    	String sql = "UPDATE question SET appendContent = '[' ";
       	upgradeService.insertNativeSQL(sql);
    }

    
    /**
   	 * 修改资源表问题URL
   	 * @param upgradeService
   	 */
    private static void updateSQL_sysresources(UpgradeService upgradeService){
    	String sql_1 = "UPDATE sysresources SET url = '/control/question/manage.htm?method=editQuestion*'  WHERE id = '9f27719619c244879c74ef032bf67684';";
       	upgradeService.insertNativeSQL(sql_1);
    	String sql_2 = "UPDATE sysresources SET url = '/control/question/manage.htm?method=deleteQuestion*'  WHERE id = '938b9dff2a27416abf6ae6ac21eaaaad';";
       	upgradeService.insertNativeSQL(sql_2);
    }
    
    

    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_forum(UpgradeService upgradeService){
       	
       	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'关注用户','关注',1,'blank_1.html','7747c7fa3dd6451783f94d01f1678a0b',4,'followRelated_addFollow_collection_default','关注用户',0,'followRelated_addFollow_3'),(NULL,'default','collection',NULL,'追加问题','问答',1,'blank_12.html','53349d99e80c4667883ffffdd9ff9e8e',4,'questionRelated_appendQuestion_collection_default','追加提问',0,'questionRelated_appendQuestion_1'),(NULL,'default','collection',NULL,'追加问题','问答',1,NULL,'eee858f7902c49e5b7adf4e23fc41960',4,'questionRelated_appendQuestion_collection_default','追加提问',0,'questionRelated_appendQuestion_2');";
       	upgradeService.insertNativeSQL(sql);

    }
   	
   /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
  
    	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('53349d99e80c4667883ffffdd9ff9e8e','default',-1,'blank_12.html','追加提问','user/appendQuestion',0,2090,4),('eee858f7902c49e5b7adf4e23fc41960','default',-1,NULL,'追加提问(移动版)','queryAppendQuestion',1,2100,4);";
        upgradeService.insertNativeSQL(sql);
   	
        
        
    }
    
  
       /**
      	 * 插入升级SQL
      	 * @param upgradeService
      	 */
    private static void insertSQL_syspermission(UpgradeService upgradeService){
    	
    	String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('12d63c0e375a47728d4c182372529b8e','POST','AUTH_3b87a0950675425d9b46b0a9526acb46_POST_ADD',2,'追加问题'),('139ded1d586348f78e3751c9dc8dec13','GET','AUTH_7e6b62b229a84c0fa739373313f381c0_GET_READ',1,'修改追加问题页'),('8fc70392b4fd405aabdb48c7a3bae7ed','POST','AUTH_7e6b62b229a84c0fa739373313f381c0_POST_UPDATE',2,'修改追加问题'),('c095eeeee5064a739c7856ffaf8a7e60','GET','AUTH_3b87a0950675425d9b46b0a9526acb46_GET_READ',1,'追加问题页'),('f1939cc01644444bba162f2a8325b91a','POST','AUTH_3aa16023dbcd43cb8ccba4352f9b7e58_POST_DELETE',1,'删除追加问题');";

        upgradeService.insertNativeSQL(sql);
    }
          
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
    	
    	
    	String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'c095eeeee5064a739c7856ffaf8a7e60','3b87a0950675425d9b46b0a9526acb46'),(NULL,'12d63c0e375a47728d4c182372529b8e','3b87a0950675425d9b46b0a9526acb46'),(NULL,'12d63c0e375a47728d4c182372529b8e','44b819a086634eb894a353009c49fc9d'),(NULL,'139ded1d586348f78e3751c9dc8dec13','7e6b62b229a84c0fa739373313f381c0'),(NULL,'8fc70392b4fd405aabdb48c7a3bae7ed','7e6b62b229a84c0fa739373313f381c0'),(NULL,'8fc70392b4fd405aabdb48c7a3bae7ed','005a590d129146aeb8a1989690235b30'),(NULL,'f1939cc01644444bba162f2a8325b91a','3aa16023dbcd43cb8ccba4352f9b7e58');";
          	
        upgradeService.insertNativeSQL(sql);
    }
    
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_sysresources(UpgradeService upgradeService){
    	
    	
    	String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('005a590d129146aeb8a1989690235b30','问答管理','文件上传',0,NULL,'/control/question/manage.htm?method=upload&*','7e6b62b229a84c0fa739373313f381c0',2),('3aa16023dbcd43cb8ccba4352f9b7e58','问答管理','删除追加问题',20670,'','/control/question/manage.htm?method=deleteAppendQuestion*',NULL,NULL),('3b87a0950675425d9b46b0a9526acb46','问答管理','追加问题',20650,'','/control/question/manage.htm?method=appendQuestion*',NULL,NULL),('44b819a086634eb894a353009c49fc9d','问答管理','文件上传',0,NULL,'/control/question/manage.htm?method=upload&*','3b87a0950675425d9b46b0a9526acb46',2),('7e6b62b229a84c0fa739373313f381c0','问答管理','修改追加问题',20660,'','/control/question/manage.htm?method=editAppendQuestion*',NULL,NULL);";
       	
       	
       	upgradeService.insertNativeSQL(sql);
    }
    
    
    
    
    
    
    
}
