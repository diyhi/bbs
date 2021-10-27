package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;

import cms.bean.template.Column;
import cms.bean.template.Templates;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.template.TemplateService;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.template.ColumnManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 5.3升级到5.4版本执行程序
 *
 */
public class Upgrade5_3to5_4 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
    	ColumnManage columnManage= (ColumnManage)SpringConfigTool.getContext().getBean("columnManage");
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
    				
    				updateSQL_column(upgradeService,columnManage);
        			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"添加栏目成功",1))+",");
        	    	
    			}
    				
    			updateSQL_topic_essence(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改话题表字段(topic)字段成功",1))+",");
    			
    					
    			
    			
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
    	
       	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'在线帮助分类','在线帮助',1,'blank_17.html','339ad4fabe7c4362ace884fa0f56feea',4,'helpRelated_helpType_collection_default','在线帮助分类',0,'helpRelated_helpType_1'),(NULL,'default','entityBean',NULL,'在线帮助内容','在线帮助',2,'blank_18.html','96ada50db91c4afbb6c1074300939b22',4,'helpRelated_helpContent_entityBean_default','在线帮助内容',0,'helpRelated_helpContent_1'),(NULL,'default','collection',NULL,'在线帮助列表','在线帮助',2,'blank_18.html','96ada50db91c4afbb6c1074300939b22',4,'helpRelated_help_collection_default','在线帮助列表',0,'helpRelated_help_1'),(NULL,'default','collection',NULL,'在线帮助导航','在线帮助',2,'blank_18.html','96ada50db91c4afbb6c1074300939b22',4,'helpRelated_helpNavigation_collection_default','在线帮助导航',0,'helpRelated_helpNavigation_1'),(NULL,'default','collection',NULL,'在线帮助分类','在线帮助',1,NULL,'66a96e7ee8304694ba86dd75836afdcb',4,'helpRelated_helpType_collection_default','在线帮助分类',0,'helpRelated_helpType_2'),(NULL,'default','entityBean',NULL,'在线帮助内容','在线帮助',1,NULL,'13f0e445ce6d416fb33a63558ce70b4b',4,'helpRelated_helpContent_entityBean_default','在线帮助内容',0,'helpRelated_helpContent_2'),(NULL,'default','collection',NULL,'在线帮助列表','在线帮助',1,NULL,'419e6748fc534966a67ad4e8443dc216',4,'helpRelated_help_collection_default','在线帮助列表',0,'helpRelated_help_2'),(NULL,'default','collection',NULL,'在线帮助导航','在线帮助',1,NULL,'c8ad33d6f9a24809ae348ce44de1b4c1',4,'helpRelated_helpNavigation_collection_default','在线帮助导航',0,'helpRelated_helpNavigation_2');";
       	upgradeService.insertNativeSQL(sql);
       	
       	
    
    }
   	
   /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`,`accessRequireLogin`) VALUES ('13f0e445ce6d416fb33a63558ce70b4b','default',-1,NULL,'查询帮助内容(移动端)','queryHelpContent',1,2460,4,b'0'),('339ad4fabe7c4362ace884fa0f56feea','default',-1,'blank_17.html','帮助中心','help',0,2430,4,b'0'),('419e6748fc534966a67ad4e8443dc216','default',-1,NULL,'查询帮助列表(移动端)','queryHelpList',1,2470,4,b'0'),('66a96e7ee8304694ba86dd75836afdcb','default',-1,NULL,'查询帮助分类(移动端)','queryHelpTypeList',1,2450,4,b'0'),('96ada50db91c4afbb6c1074300939b22','default',-1,'blank_18.html','帮助内容','helpDetail',0,2440,4,b'0'),('c8ad33d6f9a24809ae348ce44de1b4c1','default',-1,NULL,'查询帮助导航(移动端)','queryHelpNavigation',1,2480,4,b'0');";
        upgradeService.insertNativeSQL(sql);
        
        
        
    }  
    
    /**
   	 * 修改话题表字段,设置essence字段默认值
   	 * @param upgradeService
   	 */
    private static void updateSQL_topic_essence(UpgradeService upgradeService){
    	String sql = "UPDATE topic SET essence=false;";
       	upgradeService.insertNativeSQL(sql);
    }
    
    /**
     * 添加栏目
     * @param upgradeService
     */
     private static void updateSQL_column(UpgradeService upgradeService,ColumnManage columnManage){
     	Column column = new Column();
     	column.setName("帮助中心");
     	column.setLinkMode(3);
     	column.setUrl("help");
     	columnManage.addColumn(column, "default");
     	
     }
}
