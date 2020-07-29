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
import cms.web.action.redEnvelope.ReceiveRedEnvelopeConfig;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 4.7升级到4.8版本执行程序
 *
 */
public class Upgrade4_7to4_8 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
    	ReceiveRedEnvelopeConfig receiveRedEnvelopeConfig= (ReceiveRedEnvelopeConfig)SpringConfigTool.getContext().getBean("receiveRedEnvelopeConfig");
    	for(int i =0; i< 100; i++){
    		upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
    		
    		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
    		if(upgradeSystem == null || upgradeSystem.getRunningStatus().equals(9999)){
    			break;
    		}
    		if(upgradeSystem.getRunningStatus()>=100 && upgradeSystem.getRunningStatus()<200){
    			// 修改layout表字段长度
    		    modifySQL_layout(upgradeService);
    		    upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改layout表字段长度成功",1))+",");
				
    		    
    		   //修改paymentlog表字段 将parameterId列的值复制到sourceParameterId列
    		    updateSQL_paymentLog(upgradeService,receiveRedEnvelopeConfig);
    		    upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改paymentlog表，将parameterId列的值复制到sourceParameterId列成功",1))+",");
    			
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
   	 * 修改layout表字段长度
   	 * @param upgradeService
   	 */
    private static void modifySQL_layout(UpgradeService upgradeService){
    	// alter table 表名 modify column 列名 类型(要修改的长度);
    	String sql = "alter table layout modify column referenceCode varchar(100);";
    	upgradeService.insertNativeSQL(sql);
    }
    
    /**
	 * 修改paymentlog表字段 将parameterId列的值复制到sourceParameterId列
	 * @param upgradeService
	 */
    private static void updateSQL_paymentLog(UpgradeService upgradeService,ReceiveRedEnvelopeConfig receiveRedEnvelopeConfig){
    	
    	int tableQuantity = receiveRedEnvelopeConfig.getTableQuantity();
    	for(int i =0; i<tableQuantity; i++){
    		String sql = "UPDATE paymentlog_"+i+" SET sourceParameterId = parameterId";
	    	upgradeService.insertNativeSQL(sql);
	    	
	    	//删除字段
	    	String deleteField = "alter table paymentlog_"+i+" drop parameterId";
	    	upgradeService.insertNativeSQL(deleteField);
    	}
    	
    	
    }
    
    
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_forum(UpgradeService upgradeService){
    	
       	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','entityBean',NULL,'发红包内容','红包',1,'blank_1.html','7747c7fa3dd6451783f94d01f1678a0b',4,'redEnvelopeRelated_giveRedEnvelopeContent_entityBean_default','发红包内容',0,'redEnvelopeRelated_giveRedEnvelopeContent_1'),(NULL,'default','page','{\"receiveRedEnvelopeUser_id\":\"9440a764a0c04c8daaef6d08255abb36\",\"receiveRedEnvelopeUser_maxResult\":12,\"receiveRedEnvelopeUser_sort\":10}','领取红包用户列表','红包',1,NULL,'d627809654484be288ae30b32457a2d6',4,'redEnvelopeRelated_receiveRedEnvelopeUser_page_default','领取红包用户列表',0,'redEnvelopeRelated_receiveRedEnvelopeUser_1'),(NULL,'default','entityBean',NULL,'发红包内容','红包',1,NULL,'e6da1f7084864f91992a61f32fbaafc0',4,'redEnvelopeRelated_giveRedEnvelopeContent_entityBean_default','发红包',0,'redEnvelopeRelated_giveRedEnvelopeContent_2');";
       	upgradeService.insertNativeSQL(sql);
       	
       	
    }
   	
   /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('ae570158ef4848a3acffe14b870eda86','default',-1,'redEnvelopeAmountDistributionList.html','发红包金额分配列表','user/control/redEnvelopeAmountDistributionList',0,2300,1),('b16e8eee7474475cbc0fc9b81e2ea603','default',-1,'giveRedEnvelopeList.html','发红包列表','user/control/giveRedEnvelopeList',0,2200,1),('c7b55917354243199f3c354532957590','default',-1,'receiveRedEnvelopeList.html','收红包列表','user/control/receiveRedEnvelopeList',0,2400,1),('d627809654484be288ae30b32457a2d6','default',-1,NULL,'查询领取红包用户列表','queryReceiveRedEnvelopeUser',1,2410,4),('e6da1f7084864f91992a61f32fbaafc0','default',-1,NULL,'查询发红包(移动端)','queryGiveRedEnvelope',1,2420,4);";
        upgradeService.insertNativeSQL(sql);
        
    }  
	
    /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
	private static void insertSQL_syspermission(UpgradeService upgradeService){
		String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('0aa99e1b05ff46f48e4356db9c42a182','GET','AUTH_ab714c30b67345048aee5397c9f34ffe_GET_READ',1,'收红包'),('115cccb921fd423e9dd8fb31b2e6437b','GET','AUTH_43bfdc37ca5a4473af0d483500390fe9_GET_READ',1,'红包分配'),('1e480a95e4c14abf954953d813054a6d','GET','AUTH_dede44542c5644a69b72180e1abc368b_GET_READ',1,'红包分配'),('9ced5b62c30940c7ba9a7b4f33d46ad3','GET','AUTH_0667a23692b244178cda49b8c8aa66bb_GET_READ',1,'发红包');";
		upgradeService.insertNativeSQL(sql);
	}
		
	
	      
	/**
	 * 插入升级SQL
	 * @param upgradeService
	 */
	private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
		
		String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'1e480a95e4c14abf954953d813054a6d','dede44542c5644a69b72180e1abc368b'),(NULL,'9ced5b62c30940c7ba9a7b4f33d46ad3','0667a23692b244178cda49b8c8aa66bb'),(NULL,'115cccb921fd423e9dd8fb31b2e6437b','43bfdc37ca5a4473af0d483500390fe9'),(NULL,'0aa99e1b05ff46f48e4356db9c42a182','ab714c30b67345048aee5397c9f34ffe');";
		
	    upgradeService.insertNativeSQL(sql);
	}
	
	/**
		 * 插入升级SQL
		 * @param upgradeService
		 */
	private static void insertSQL_sysresources(UpgradeService upgradeService){
		  	
		String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('0667a23692b244178cda49b8c8aa66bb','会员管理','发红包',27580,'','/control/redEnvelope/giveRedEnvelope/list.htm*',NULL,NULL),('43bfdc37ca5a4473af0d483500390fe9','会员管理','红包分配',27585,'','/control/redEnvelope/redEnvelopeAmountDistribution/list.htm*',NULL,NULL),('ab714c30b67345048aee5397c9f34ffe','会员管理','收红包',27590,'','/control/redEnvelope/receiveRedEnvelope/list.htm*',NULL,NULL),('dede44542c5644a69b72180e1abc368b','话题管理','红包分配',12640,'','/control/redEnvelope/redEnvelopeAmountDistribution/list.htm*',NULL,NULL);";
		
		upgradeService.insertNativeSQL(sql);
		
		
		
	}
    
    
    
}
