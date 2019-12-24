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
 * 3.2升级到3.3版本执行程序
 *
 */
public class Upgrade3_2to3_3 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
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
    		
    			insertSQL_syspermission(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");
    			
    			insertSQL_syspermissionresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");
    				
    			insertSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");
    			
    			deleteIndex_topic(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表topic删除索引topic_2_idx成功",1))+",");
    			
    			
    			
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
    	
    	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'给话题点赞','点赞',1,'blank_1.html','7747c7fa3dd6451783f94d01f1678a0b',4,'likeRelated_addLike_collection_default','组话题点赞',0,'likeRelated_addLike_1'),(NULL,'default','entityBean',NULL,'话题点赞总数','点赞',1,NULL,'91fcfa258f41472096f57db6308856cf',4,'likeRelated_likeCount_entityBean_default','话题点赞总数',0,'likeRelated_likeCount_1'),(NULL,'default','entityBean',NULL,'用户是否已经点赞该话题','点赞',1,NULL,'402eabef91f246859d854838997e6eb6',4,'likeRelated_alreadyLiked_entityBean_default','用户是否已经点赞该话题',0,'likeRelated_alreadyLiked_1'),(NULL,'default','collection',NULL,'关注用户','关注',1,'home.html','440b1b2f202d4de38f450226083ca174',1,'followRelated_addFollow_collection_default','关注用户',0,'followRelated_addFollow_1'),(NULL,'default','entityBean',NULL,'粉丝总数','关注',1,NULL,'65735bf28feb47088a5102ff644a0d77',4,'followRelated_followerCount_entityBean_default','查询粉丝总数',0,'followRelated_followerCount_1'),(NULL,'default','entityBean',NULL,'是否已经关注该用户','关注',1,NULL,'6c6f77c044024be0a56486f0336df56c',4,'followRelated_following_entityBean_default','查询是否已经关注该用户',0,'followRelated_following_1');";
    	upgradeService.insertNativeSQL(sql);

    }
	
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
       	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('402eabef91f246859d854838997e6eb6','default',-1,NULL,'查询用户是否已经点赞该话题','queryAlreadyLiked',1,920,4),('725640fa18ce499e8c425689c8bdd134','default',-1,'likeList.html','点赞列表','user/control/likeList',0,1000,1),('91fcfa258f41472096f57db6308856cf','default',-1,NULL,'查询话题点赞总数','queryLikeCount',1,910,4),('ffbc4af71a024cbd861e5dd621bf45a1','default',-1,'topicLikeList.html','话题点赞列表','user/control/topicLikeList',0,1100,1),('444c325c26504de9913e844f8648ce1e','default',-1,'followerList.html','粉丝列表','user/control/followerList',0,1300,1),('65735bf28feb47088a5102ff644a0d77','default',-1,NULL,'查询粉丝总数','queryFollowerCount',1,1110,4),('6c6f77c044024be0a56486f0336df56c','default',-1,NULL,'查询是否已经关注该用户','queryFollowing',1,1120,4),('7f5b0839442446148b5f56f0f2648d45','default',-1,'followList.html','关注列表','user/control/followList',0,1200,1);";
       	upgradeService.insertNativeSQL(sql);

    }
    
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_syspermission(UpgradeService upgradeService){
       	String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('1474318f7e2240739d00a85ff21778d1','GET','AUTH_98e03673f0c847a2bc8f93008b1729ef_GET_READ',1,'点赞用户'),('50eb45ee72014011a3828b594988af72','GET','AUTH_088ec0225b114039aab46c17888125f2_GET_READ',1,'点赞列表'),('a97984e18c42486bae7b5b9709309981','GET','AUTH_a7f6e1dcc5e943f48f4270dcfa056557_GET_READ',1,'粉丝列表'),('e189c9ff166d48f19369852c9bee511a','GET','AUTH_58dc8b9d637a4ceda36b7083cd94bb76_GET_READ',1,'关注列表');";

       	upgradeService.insertNativeSQL(sql);
    }
       
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
       	String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (1433,'1474318f7e2240739d00a85ff21778d1','98e03673f0c847a2bc8f93008b1729ef'),(1434,'50eb45ee72014011a3828b594988af72','088ec0225b114039aab46c17888125f2'),(1435,'e189c9ff166d48f19369852c9bee511a','58dc8b9d637a4ceda36b7083cd94bb76'),(1436,'a97984e18c42486bae7b5b9709309981','a7f6e1dcc5e943f48f4270dcfa056557');";

       	upgradeService.insertNativeSQL(sql);
    }
       
       
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_sysresources(UpgradeService upgradeService){
       	String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('088ec0225b114039aab46c17888125f2','会员管理','点赞列表',27465,'','/control/like/list.htm*',NULL,NULL),('58dc8b9d637a4ceda36b7083cd94bb76','会员管理','关注列表',27470,'','/control/follow/list.htm*',NULL,NULL),('98e03673f0c847a2bc8f93008b1729ef','话题管理','点赞用户',12625,'','/control/topicLike/list.htm*',NULL,NULL),('a7f6e1dcc5e943f48f4270dcfa056557','会员管理','粉丝列表',27475,'','/control/follower/list.htm*',NULL,NULL);";
       	
       	
       	upgradeService.insertNativeSQL(sql);
    }

    /**
   	 * 删除topic表topic_2_idx索引
   	 * @param upgradeService
   	 */
    private static void deleteIndex_topic(UpgradeService upgradeService){
    	
    	String sql = "ALTER TABLE topic DROP INDEX topic_2_idx";
    	upgradeService.insertNativeSQL(sql);
    }
    
}
