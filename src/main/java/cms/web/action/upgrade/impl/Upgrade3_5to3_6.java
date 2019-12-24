package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;

import org.joda.time.DateTime;

import cms.bean.QueryResult;
import cms.bean.template.Templates;
import cms.bean.topic.Topic;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.template.TemplateService;
import cms.service.topic.TopicService;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 3.5升级到3.6版本执行程序
 *
 */
public class Upgrade3_5to3_6 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
    	TopicService topicService = (TopicService)SpringConfigTool.getContext().getBean("topicServiceBean");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
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
    			
    			setLastReplyTime(upgradeService,topicService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"设置话题默认最后回复时间成功",1))+",");
    			
    		  
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
    	
    	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'修改话题','话题',1,'blank_7.html','0f667ec7b1884604a164a22c0fe037e0',4,'topicRelated_editTopic_collection_default','修改话题',0,'topicRelated_editTopic_1'),(NULL,'default','collection',NULL,'修改话题','话题',1,NULL,'0f61e4a78bba476faa52040b15c25597',4,'topicRelated_editTopic_collection_default','修改话题',0,'topicRelated_editTopic_2');";
    	upgradeService.insertNativeSQL(sql);

    }
	
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	
       	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('0f667ec7b1884604a164a22c0fe037e0','default',-1,'blank_7.html','修改话题','user/editTopic',0,1730,4),('0f61e4a78bba476faa52040b15c25597','default',-1,NULL,'修改话题(移动版)','queryEditTopic',1,1740,4);";
       	upgradeService.insertNativeSQL(sql);
	
    }
    
 
    /**
	 * 修改systemsetting表字段
	 * @param upgradeService
	 */
    private static void updateSQL_systemsetting(UpgradeService upgradeService){
    	String sql = "UPDATE systemsetting SET fileSecureLinkExpire = 300;";
    	upgradeService.insertNativeSQL(sql);
    }
   
    
    /**
	 * 设置话题默认最后回复时间
	 */
	private static void setLastReplyTime(UpgradeService upgradeService,TopicService topicService){
		
		int page = 1;//分页 当前页
		int maxResult = 200;// 每页显示记录数
		
		while(true){	
			//当前页
			int firstIndex = (page-1)*maxResult;
			
			//话题查询回复
			QueryResult<Topic> qr = topicService.getScrollData(Topic.class, firstIndex, maxResult);

			if(qr == null || qr.getResultlist() == null || qr.getResultlist().size() ==0){
				break;
			}
			
			for(Topic topic :qr.getResultlist()){
				if(topic.getLastReplyTime() == null){
					DateTime dateTime = new DateTime(topic.getPostTime());
					String lastReplyTime = dateTime.toString("yyyy-MM-dd HH:mm:ss");
					
					String sql = "UPDATE topic SET lastReplyTime = STR_TO_DATE('"+lastReplyTime+"', '%Y-%m-%d %H:%i:%s')  WHERE id="+topic.getId()+";";
			 
					upgradeService.insertNativeSQL(sql);
				}
				
				
			}
			page++;
		}
		
	}
    
}
