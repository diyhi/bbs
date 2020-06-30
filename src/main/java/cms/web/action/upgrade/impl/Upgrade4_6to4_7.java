package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import cms.bean.template.Templates;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.template.TemplateService;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.ObjectConversion;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 4.6升级到4.7版本执行程序
 *
 */
public class Upgrade4_6to4_7 {
	
	
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
    			
    			
    			updateSQL_systemsetting(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改设置表(systemsetting)字段成功",1))+",");
    			
    			
    						
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
    	
       	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'修改答案','问答',1,'blank_15.html','02619989d8fe4ca6b60d0cd855cf57a9',4,'questionRelated_editAnswer_collection_default','修改答案',0,'questionRelated_editAnswer_1'),(NULL,'default','collection',NULL,'修改答案回复','问答',1,'blank_16.html','a86967d780014f3d850efcf9d5b0fcba',4,'questionRelated_editReply_collection_default','修改答案回复',0,'questionRelated_editReply_1'),(NULL,'default','collection',NULL,'修改答案','问答',1,NULL,'4f6397a608a548be8b09a6b06c788e50',4,'questionRelated_editAnswer_collection_default','修改答案',0,'questionRelated_editAnswer_2'),(NULL,'default','collection',NULL,'修改答案回复','问答',1,NULL,'4f99621be57642109a3040138ad3afca',4,'questionRelated_editReply_collection_default','修改答案回复',0,'questionRelated_editReply_2');";
       	upgradeService.insertNativeSQL(sql);
    }
   	
   /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('02619989d8fe4ca6b60d0cd855cf57a9','default',-1,'blank_15.html','修改答案','user/editAnswer',0,2180,4),('4f6397a608a548be8b09a6b06c788e50','default',-1,NULL,'修改答案(移动版)','queryEditAnswer',1,2200,4),('4f99621be57642109a3040138ad3afca','default',-1,NULL,'修改答案回复(移动版)','queryEditAnswerReply',1,2210,4),('a86967d780014f3d850efcf9d5b0fcba','default',-1,'blank_16.html','修改答案回复','user/editAnswerReply',0,2190,4);";
        upgradeService.insertNativeSQL(sql);
        
        
    }
	
	/**
	 * 修改systemsetting表字段
	 * @param upgradeService
	 */
    private static void updateSQL_systemsetting(UpgradeService upgradeService){
    	List<Object[]> objectList = upgradeService.queryNativeSQL("select allowRegister from systemsetting where id=1;");
		   if(objectList != null && objectList.size() >0){
			   for(int o = 0; o<objectList.size(); o++){
					Object object = objectList.get(o);
					Boolean allowRegister = ObjectConversion.conversion(object, ObjectConversion.BOOLEAN);//是否允许注册
					
					if(allowRegister != null && allowRegister.equals(true)){
				    	String allowRegisterAccount_str = "{\"local\":true,\"weChat\":true}";
				    	
						String updateSQL = "UPDATE systemsetting SET allowRegisterAccount = '"+allowRegisterAccount_str+"' WHERE id=1;";
				    	upgradeService.insertNativeSQL(updateSQL);
						
				    	
						
				    	//删除字段
				    	String deleteField = "alter table systemsetting drop allowRegister";
				    	upgradeService.insertNativeSQL(deleteField);
						
					}
					
					break;
					
			   }
		}
    }

}
