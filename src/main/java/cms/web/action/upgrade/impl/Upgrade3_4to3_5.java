package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;

import cms.bean.template.Column;
import cms.bean.template.Forum;
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
 * 3.4升级到3.5版本执行程序
 *
 */
public class Upgrade3_4to3_5 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
    	ColumnManage columnManage= (ColumnManage)SpringConfigTool.getContext().getBean("columnManage");
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
    			
    			
    		    deleteIndex_userrolegroup(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"删除userrolegroup表 userRoleGroup_2_idx索引成功",1))+",");
    			
    			updateSQL_user(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改用户表(user)字段成功",1))+",");
    			
    			updateSQL_systemsetting(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改设置表(systemsetting)字段成功",1))+",");
    			
    		
    			updateSQL_column(upgradeService,columnManage);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"添加栏目成功",1))+",");
    	    			
    			
    			updateSQL_forum(upgradeService,templateService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改版块(forum)表 forumChildType 和 name 字段值成功",1))+",");
    	    	
    			
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
    	
    	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'会员卡列表','会员卡',1,'blank_5.html','7f2d29e0c92d43b4844e0c9400b62ebd',4,'membershipCardRelated_membershipCard_collection_default','会员卡列表',0,'membershipCardRelated_membershipCard_1'),(NULL,'default','entityBean',NULL,'会员卡内容','会员卡',1,'blank_6.html','8bf2670aafb9429a92a2dab3c1f08d60',4,'membershipCardRelated_membershipCardContent_entityBean_default','会员卡内容',0,'membershipCardRelated_membershipCardContent_1'),(NULL,'default','collection',NULL,'购买会员卡','会员卡',1,'blank_6.html','8bf2670aafb9429a92a2dab3c1f08d60',4,'membershipCardRelated_buyMembershipCard_collection_default','购买会员卡',0,'membershipCardRelated_buyMembershipCard_1'),(NULL,'default','collection',NULL,'会员卡列表','会员卡',1,NULL,'d548ee0e9d84456c8f616671022d043b',4,'membershipCardRelated_membershipCard_collection_default','查询会员卡列表',0,'membershipCardRelated_membershipCard_2'),(NULL,'default','entityBean',NULL,'会员卡内容','会员卡',1,NULL,'5d6067bbc62646f7a22f8095cca01f98',4,'membershipCardRelated_membershipCardContent_entityBean_default','会员卡内容',0,'membershipCardRelated_membershipCardContent_2');";
    	upgradeService.insertNativeSQL(sql);

    }
	
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
       	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('10494f8de46746e0886518832a168d9c','default',-1,'balance.html','余额','user/control/balance',0,1400,1),('5d6067bbc62646f7a22f8095cca01f98','default',-1,NULL,'会员卡(移动版)','queryMembershipCard',1,1720,4),('7f2d29e0c92d43b4844e0c9400b62ebd','default',-1,'blank_5.html','会员卡列表','membershipCardList',0,1310,4),('8bf2670aafb9429a92a2dab3c1f08d60','default',-1,'blank_6.html','会员卡','membershipCard',0,1320,4),('a14b8228d72b4c72b306111be88fc1bb','default',-1,'membershipCardOrderList.html','会员卡订单列表','membershipCardOrderList',0,1700,1),('bc920d0af10443828316bbc8bcb2b4c3','default',-1,'payment.html','付款页面','user/control/payment',0,1500,1),('d548ee0e9d84456c8f616671022d043b','default',-1,NULL,'查询会员卡列表(移动版)','queryMembershipCardList',1,1710,4),('e165692880c2439ea24164a545477616','default',-1,'paymentCompleted.html','付款完成页面','paymentCompleted',0,1600,1);";
       	upgradeService.insertNativeSQL(sql);
	
    }
    
    

    
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_syspermission(UpgradeService upgradeService){
       	String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('04931b8ad2db4512bb8c49d48f600b18','POST','AUTH_8dd0e634ff4647e0b438b75fbde1148f_POST_UPDATE',1,'修改会员卡'),('1cc026a6afec4d2c823901413e192bee','GET','AUTH_d71d35ca222a43e2bec6ceb76ac8097f_GET_READ',1,'添加会员卡页'),('294be5897f7c4770b7b2d0190e0a6bdd','POST','AUTH_7d7842f34d6940738a767b3d7dd9a6ec_POST_ADD',1,'充值'),('2a881ff2307942a888e84ce2b7cb9a57','POST','AUTH_d71d35ca222a43e2bec6ceb76ac8097f_POST_ADD',2,'添加会员卡'),('3402b228fb604030a83eac3c0e94373f','GET','AUTH_d5290b702acf41f286dc3d66c659d081_GET_READ',1,'会员卡订单'),('381f2e01b83742149361cb116c106e12','POST','AUTH_da63bf83542e47bdacdada8a8abf8166_POST_DELETE',1,'删除在线支付接口'),('3944c3265a08408db9c8ac3a95862cfe','GET','AUTH_a15cab18081d4ec7b85b03646b873166_GET_READ',1,'查看支付日志'),('3c6e8be51c62407c9d6a9072866c728d','GET','AUTH_05568f678d8e43018b884f0bba9dd600_GET_READ',1,'在线支付接口列表'),('5357d79b9cad4dc4b6b3ee3434977281','POST','AUTH_97bc7db41f8743978c70a104e49dd1c5_POST_DELETE',1,'删除会员卡'),('57ceef23523041babd9cec2026f3fd7b','GET','AUTH_44406f79d290492aa113d528da21b317_GET_READ',1,'添加在线支付接口页'),('58429820b3764a56a8fda45833ec4737','GET','AUTH_453b956048b044cfb4f3df143624aa68_GET_READ',1,'解锁话题隐藏内容分成'),('7565855bdad4464a9b2ef86ae988b163','POST','AUTH_bc413423f03f4f1d8d37178ae73eb9ed_POST_UPDATE',2,'修改在线支付接口'),('9117004d87d84d68992d737742a7fc98','GET','AUTH_74e391ff7c0d41aeab64e9373f16776a_GET_READ',1,'会员卡列表'),('aff1310185ef404ba5fae9faff402468','GET','AUTH_8dd0e634ff4647e0b438b75fbde1148f_GET_READ',1,'修改会员卡页'),('bb27abfac38e466db47a10922057429c','GET','AUTH_5c9e1ad37d2241d6b5eebe4d591f5da5_GET_READ',1,'支付日志'),('c60995081b5a434eab404e350a181d66','GET','AUTH_9a797b54d630450cb3cfadb1f2d94243_GET_READ',1,'会员卡订单列表'),('cc2b89e39e374669980b67dabbd727eb','GET','AUTH_bc413423f03f4f1d8d37178ae73eb9ed_GET_READ',1,'修改在线支付接口页'),('fa3278919bba435e857a341de20f95c2','POST','AUTH_44406f79d290492aa113d528da21b317_POST_ADD',2,'添加在线支付接口');";

       	upgradeService.insertNativeSQL(sql);
    }
       
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
       	String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'3402b228fb604030a83eac3c0e94373f','d5290b702acf41f286dc3d66c659d081'),(NULL,'bb27abfac38e466db47a10922057429c','5c9e1ad37d2241d6b5eebe4d591f5da5'),(NULL,'3944c3265a08408db9c8ac3a95862cfe','a15cab18081d4ec7b85b03646b873166'),(NULL,'294be5897f7c4770b7b2d0190e0a6bdd','7d7842f34d6940738a767b3d7dd9a6ec'),(NULL,'9117004d87d84d68992d737742a7fc98','74e391ff7c0d41aeab64e9373f16776a'),(NULL,'1cc026a6afec4d2c823901413e192bee','d71d35ca222a43e2bec6ceb76ac8097f'),(NULL,'2a881ff2307942a888e84ce2b7cb9a57','d71d35ca222a43e2bec6ceb76ac8097f'),(NULL,'aff1310185ef404ba5fae9faff402468','8dd0e634ff4647e0b438b75fbde1148f'),(NULL,'04931b8ad2db4512bb8c49d48f600b18','8dd0e634ff4647e0b438b75fbde1148f'),(NULL,'5357d79b9cad4dc4b6b3ee3434977281','97bc7db41f8743978c70a104e49dd1c5'),(NULL,'c60995081b5a434eab404e350a181d66','9a797b54d630450cb3cfadb1f2d94243'),(NULL,'58429820b3764a56a8fda45833ec4737','453b956048b044cfb4f3df143624aa68'),(NULL,'3c6e8be51c62407c9d6a9072866c728d','05568f678d8e43018b884f0bba9dd600'),(NULL,'57ceef23523041babd9cec2026f3fd7b','44406f79d290492aa113d528da21b317'),(NULL,'fa3278919bba435e857a341de20f95c2','44406f79d290492aa113d528da21b317'),(NULL,'cc2b89e39e374669980b67dabbd727eb','bc413423f03f4f1d8d37178ae73eb9ed'),(NULL,'7565855bdad4464a9b2ef86ae988b163','bc413423f03f4f1d8d37178ae73eb9ed'),(NULL,'381f2e01b83742149361cb116c106e12','da63bf83542e47bdacdada8a8abf8166');";
       	
       	upgradeService.insertNativeSQL(sql);
    }
       
       
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_sysresources(UpgradeService upgradeService){
       	String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('05568f678d8e43018b884f0bba9dd600','支付管理','在线支付接口列表',52000,'','/control/onlinePaymentInterface/list.htm*',NULL,NULL),('44406f79d290492aa113d528da21b317','支付管理','添加在线支付接口',52100,'','/control/onlinePaymentInterface/manage.htm?method=add*',NULL,NULL),('453b956048b044cfb4f3df143624aa68','平台收益管理','解锁话题隐藏内容分成',46400,'','/control/topicUnhidePlatformShare/list.htm*',NULL,NULL),('5c9e1ad37d2241d6b5eebe4d591f5da5','会员管理','支付日志',27560,'','/control/paymentLog/list.htm*',NULL,NULL),('74e391ff7c0d41aeab64e9373f16776a','会员卡管理','会员卡列表',28780,'','/control/membershipCard/list.htm*',NULL,NULL),('7d7842f34d6940738a767b3d7dd9a6ec','会员管理','充值',27570,'','/control/user/manage.htm?method=payment&*',NULL,NULL),('8dd0e634ff4647e0b438b75fbde1148f','会员卡管理','修改会员卡',28790,'','/control/membershipCard/manage.htm?method=edit*',NULL,NULL),('97bc7db41f8743978c70a104e49dd1c5','会员卡管理','删除会员卡',28795,'','/control/membershipCard/manage.htm?method=delete*',NULL,NULL),('9a797b54d630450cb3cfadb1f2d94243','会员卡管理','会员卡订单列表',28797,'','/control/membershipCardOrder/list.htm*',NULL,NULL),('a15cab18081d4ec7b85b03646b873166','会员管理','查看支付日志',27565,'','/control/paymentLog/manage.htm?method=show&*',NULL,NULL),('bc413423f03f4f1d8d37178ae73eb9ed','支付管理','修改在线支付接口',52200,'','/control/onlinePaymentInterface/manage.htm?method=edit&*',NULL,NULL),('d5290b702acf41f286dc3d66c659d081','会员管理','会员卡订单',27550,'','/control/membershipCard/manage.htm?method=membershipCardOrderList*',NULL,NULL),('d71d35ca222a43e2bec6ceb76ac8097f','会员卡管理','添加会员卡',28785,'','/control/membershipCard/manage.htm?method=add*',NULL,NULL),('da63bf83542e47bdacdada8a8abf8166','支付管理','删除在线支付接口',52300,'','/control/onlinePaymentInterface/manage.htm?method=delete*',NULL,NULL);";
       	
       	
       	upgradeService.insertNativeSQL(sql);
    }

   
    /**
   	 * 删除userrolegroup表userRoleGroup_2_idx索引
   	 * @param upgradeService
   	 */
    private static void deleteIndex_userrolegroup(UpgradeService upgradeService){
    	
    	String sql = "ALTER TABLE userrolegroup DROP INDEX userRoleGroup_2_idx";
    	upgradeService.insertNativeSQL(sql);
    }
    
    /**
	 * 修改用户字段
	 * @param upgradeService
	 */
    private static void updateSQL_user(UpgradeService upgradeService){
    	String sql = "UPDATE user SET deposit = 0;";
    	upgradeService.insertNativeSQL(sql);
    }
    
    /**
	 * 修改systemsetting表字段
	 * @param upgradeService
	 */
    private static void updateSQL_systemsetting(UpgradeService upgradeService){
    	String sql = "UPDATE systemsetting SET topicUnhidePlatformShareProportion = 0;";
    	upgradeService.insertNativeSQL(sql);
    }
    
    /**
    * 添加栏目
    * @param upgradeService
    */
    private static void updateSQL_column(UpgradeService upgradeService,ColumnManage columnManage){
    	Column column = new Column();
    	column.setName("会员卡");
    	column.setLinkMode(3);
    	column.setUrl("membershipCardList");
    	columnManage.addColumn(column, "default");
    	
    }
    
    /**
     * 修改forum表
     * @param upgradeService
     * @param templateService
     */
    private static void updateSQL_forum(UpgradeService upgradeService,TemplateService templateService){
		Forum forum = templateService.findForum("default","topicRelated_likeTopic_1");
		if(forum != null){
			String sql = "UPDATE forum SET forumChildType = '相似话题',name='相似话题' WHERE id="+forum.getId()+";";
	    	upgradeService.insertNativeSQL(sql);
		}
	}
    
}
