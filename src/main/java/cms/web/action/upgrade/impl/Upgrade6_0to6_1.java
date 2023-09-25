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
 * 6.0升级到6.1版本执行程序
 *
 */
public class Upgrade6_0to6_1 {
	private static final Logger logger = LogManager.getLogger(Upgrade6_0to6_1.class);
	
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
    			updateSQL_systemsetting(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改systemsetting表字段成功",1))+",");
    			
				
				
				updateSQL_topic(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改topic表字段成功",1))+",");
    			
				
				
				updateSQL_comment(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改comment表字段成功",1))+",");
    			
			    
				updateSQL_question(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改question表字段成功",1))+",");
    			
				updateSQL_answer(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改answer表字段成功",1))+",");
    			
				
				
				updateSQL_help(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改help表字段成功",1))+",");
    			
	
				
				insertSQL_forum(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表forum插入SQL成功",1))+",");
				
				insertSQL_layout(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表layout插入SQL成功",1))+",");
				
    			
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
	 * 修改systemsetting表字段
	 * @param upgradeService
	 */
    private static void updateSQL_systemsetting(UpgradeService upgradeService){
    	String updateSQL = "UPDATE systemsetting SET supportEditor=10,topicHeatFactor='评论=20|点赞=10|浏览量=1|重力因子=1.8';";
    	upgradeService.insertNativeSQL(updateSQL);
    }
       
    /**
   	 * 修改topic表字段
   	 * @param upgradeService
   	*/
    private static void updateSQL_topic(UpgradeService upgradeService){
       	String updateSQL = "UPDATE topic SET isMarkdown=false,weight=0;";
       	upgradeService.insertNativeSQL(updateSQL);
    }
       
    /**
   	 * 修改comment表字段
   	 * @param upgradeService
   	*/
    private static void updateSQL_comment(UpgradeService upgradeService){
       	String updateSQL = "UPDATE comment SET isMarkdown=false;";
       	upgradeService.insertNativeSQL(updateSQL);
    }
    /**
   	 * 修改question表字段
   	 * @param upgradeService
   	*/
    private static void updateSQL_question(UpgradeService upgradeService){
       	String updateSQL = "UPDATE question SET isMarkdown=false;";
       	upgradeService.insertNativeSQL(updateSQL);
    }
       
    /**
   	 * 修改answer表字段
   	 * @param upgradeService
   	 */
    private static void updateSQL_answer(UpgradeService upgradeService){
       	String updateSQL = "UPDATE answer SET isMarkdown=false;";
       	upgradeService.insertNativeSQL(updateSQL);
    }
       /**
   	 * 修改help表字段
   	 * @param upgradeService
   	 */
    private static void updateSQL_help(UpgradeService upgradeService){
       	String updateSQL = "UPDATE help SET isMarkdown=false;";
       	upgradeService.insertNativeSQL(updateSQL);
    }
      
    /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	*/
    private static void insertSQL_forum(UpgradeService upgradeService){
      	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'api','collection','{\"hotTopic_id\":\"a90bd6b46e1c4077b06e48b7262895bb\",\"hotTopic_maxResult\":8}','热门话题','话题',1,NULL,'d07b94ad410e43fdab473029f44c67c8',4,'topicRelated_hotTopic_collection_default','热门话题',0,'topicRelated_hotTopic_1');";
      	upgradeService.insertNativeSQL(sql);
    }
      	
    /**
 	 * 插入升级SQL
 	 * @param upgradeService
 	*/
    private static void insertSQL_layout(UpgradeService upgradeService){
       	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`,`accessRequireLogin`) VALUES ('d07b94ad410e43fdab473029f44c67c8','api',-1,NULL,'查询热门话题','queryHotTopic',1,2880,4,b'0');";
        upgradeService.insertNativeSQL(sql);
    }  
       
}
