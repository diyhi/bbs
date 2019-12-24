package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;
import java.util.List;


import cms.bean.template.Column;
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
import cms.web.action.message.RemindConfig;
import cms.web.action.template.ColumnManage;
import cms.web.action.upgrade.UpgradeManage;
import cms.web.action.user.UserDynamicConfig;
import cms.web.action.user.UserLoginLogConfig;
/**
 * 3.6升级到4.0版本执行程序
 *
 */
public class Upgrade3_6to4_0 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
    	UserDynamicConfig userDynamicConfig = (UserDynamicConfig)SpringConfigTool.getContext().getBean("userDynamicConfig");
    	ColumnManage columnManage = (ColumnManage)SpringConfigTool.getContext().getBean("columnManage");
    	RemindConfig remindConfig = (RemindConfig)SpringConfigTool.getContext().getBean("remindConfig");
    	UserLoginLogConfig userLoginLogConfig = (UserLoginLogConfig)SpringConfigTool.getContext().getBean("userLoginLogConfig");
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
    			
    			updateSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改资源路径成功",1))+",");
    			
    			
    			
    			insertSQL_syspermission(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");
    			
    			insertSQL_syspermissionresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");
    				
    			insertSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");
    			
    			
    			
    		    deleteIndex_userrolegroup(upgradeService,userDynamicConfig);
    	    	upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"删除userdynamic表userDynamic_2_idx,userDynamic_3_idx,userDynamic_4_idx索引",1))+",");
    	    	
    			
    	    	updateSQL_systemSetting(upgradeService);
    	    	upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改系统设置字段",1))+",");
    	    	
    			
    		    updateSQL_userDynamic(upgradeService,userLoginLogConfig);
    	    	upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"设置UserDynamic表的默认值",1))+",");
    	    	    	
    		    
    		    updateSQL_remind(upgradeService,remindConfig);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"设置Remind表的questionId默认值",1))+",");
    	    	
    			
    			
    			updateSQL_column(upgradeService,columnManage);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"添加栏目成功",1))+",");
    	    	
    			
    		
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
	 * 修改SQL
	 * @param upgradeService
	 */
    private static void updateSQL_sysresources(UpgradeService upgradeService){
    	//('53662c3fbcd145068ba4760a6d1d39a9','模板管理','查询自定义项目',11,NULL,'/control/customItem/manage.htm?method=ajax_searchCustomItemPage&*','ef3e2b9c32634f4088f4066880c70677',1)
    	String sql_1 = "UPDATE sysresources SET url = '/control/questionTag/manage.htm?method=questionTagPage&*',name='查询问题标签分页' WHERE id = '53662c3fbcd145068ba4760a6d1d39a9';";
    	upgradeService.insertNativeSQL(sql_1);
    	
    	
    	//('c930706b667749d1b5784c37c6a4ffd4','模板管理','查询自定义项目',12,NULL,'/control/customItem/manage.htm?method=ajax_searchCustomItemPage&*','922f6908c5a1434aba4b0f6f8f008c1c',1)
    	String sql_2 = "UPDATE sysresources SET url = '/control/questionTag/manage.htm?method=questionTagPage&*',name='查询问题标签分页' WHERE id = 'c930706b667749d1b5784c37c6a4ffd4';";
    	upgradeService.insertNativeSQL(sql_2);

   	
    }
    
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_forum(UpgradeService upgradeService){
       	
       	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','page','{\"question_id\":\"94cbe208feb5483a82b98cc12f1bcf4f\",\"question_quantity\":null,\"question_sort\":1,\"question_more\":null,\"question_moreValue\":null,\"question_maxResult\":30,\"question_pageCount\":null,\"question_tagId\":null,\"question_tagName\":null,\"question_tag_transferPrameter\":true,\"question_filterCondition\":null,\"question_filterCondition_transferPrameter\":true,\"question_recommendQuestionList\":{}}','问题列表','问答',1,'blank_8.html','197fb4524ba0483d8406ee25fdc21487',4,'questionRelated_question_page_default','问题列表',0,'questionRelated_question_1'),(NULL,'default','entityBean',NULL,'问题内容','问答',2,'blank_9.html','d7e5fcb22e1b463cb2616616e42d3b63',4,'questionRelated_questionContent_entityBean_default','问题内容',0,'questionRelated_questionContent_1'),(NULL,'default','collection',NULL,'关注用户','关注',1,'blank_9.html','d7e5fcb22e1b463cb2616616e42d3b63',4,'followRelated_addFollow_collection_default','关注用户',0,'followRelated_addFollow_2'),(NULL,'default','page','{\"answer_id\":\"6cbe1e2efad64c85a245da5ff9d1a4c8\",\"answer_maxResult\":15,\"answer_pageCount\":null,\"answer_sort\":2}','答案列表','问答',1,'blank_9.html','d7e5fcb22e1b463cb2616616e42d3b63',4,'questionRelated_answer_page_default','答案列表',0,'questionRelated_answer_1'),(NULL,'default','collection',NULL,'问题标签列表','问答',1,'blank_8.html','197fb4524ba0483d8406ee25fdc21487',4,'questionRelated_questionTag_collection_default','问题标签',0,'questionRelated_questionTag_1'),(NULL,'default','collection',NULL,'添加问题','问答',1,'blank_10.html','9e9cafbe93314ca585aedbe98f3b4e11',4,'questionRelated_addQuestion_collection_default','添加问题',0,'questionRelated_addQuestion_1'),(NULL,'default','collection',NULL,'问题标签列表','问答',1,NULL,'7c79820f306143378169167088f17cc2',4,'questionRelated_questionTag_collection_default','问题标签',0,'questionRelated_questionTag_2'),(NULL,'default','collection',NULL,'添加答案','问答',1,'blank_9.html','d7e5fcb22e1b463cb2616616e42d3b63',4,'questionRelated_addAnswer_collection_default','添加答案',0,'questionRelated_addAnswer_1'),(NULL,'default','collection',NULL,'回复答案','问答',1,'blank_11.html','894c1ee85cb34067a0f2c82364178a66',4,'questionRelated_replyAnswer_collection_default','回复答案',0,'questionRelated_replyAnswer_1'),(NULL,'default','collection',NULL,'采纳答案','问答',1,'blank_9.html','d7e5fcb22e1b463cb2616616e42d3b63',4,'questionRelated_adoptionAnswer_collection_default','采纳答案',0,'questionRelated_adoptionAnswer_1'),(NULL,'default','entityBean',NULL,'关注总数','关注',1,NULL,'4605cd9bb304408883ecc648ba911e37',4,'followRelated_followCount_entityBean_default','关注总数',0,'followRelated_followCount_1'),(NULL,'default','entityBean',NULL,'回答总数','问答',1,NULL,'b9d3684fd5004247b4820ca5b9a5bff3',4,'questionRelated_answerCount_entityBean_default','回答总数',0,'questionRelated_answerCount_1'),(NULL,'default','collection','{\"likeQuestion_id\":\"1e2c10a7a96241bb9e7572eea9830b7e\",\"likeQuestion_maxResult\":10}','相似问题','问答',1,'blank_9.html','d7e5fcb22e1b463cb2616616e42d3b63',4,'questionRelated_likeQuestion_collection_default','相似问题',0,'questionRelated_likeQuestion_1'),(NULL,'default','page','{\"question_id\":\"9075b80be5aa4b719bc2d38eab3ab017\",\"question_quantity\":null,\"question_sort\":1,\"question_more\":null,\"question_moreValue\":null,\"question_maxResult\":30,\"question_pageCount\":null,\"question_tagId\":null,\"question_tagName\":null,\"question_tag_transferPrameter\":true,\"question_filterCondition\":null,\"question_filterCondition_transferPrameter\":true,\"question_recommendQuestionList\":{}}','问题列表','问答',1,NULL,'4f98b0ca689c4fb495c0a12cafe5a650',4,'questionRelated_question_page_default','问题列表',0,'questionRelated_question_2'),(NULL,'default','entityBean',NULL,'问题内容','问答',1,NULL,'1d95af33c5ff4eff96c56a79d3ec68dd',4,'questionRelated_questionContent_entityBean_default','问题内容',0,'questionRelated_questionContent_2'),(NULL,'default','page','{\"answer_id\":\"44936843f7b24741a92ffabf8f41e2a7\",\"answer_maxResult\":15,\"answer_pageCount\":null,\"answer_sort\":2}','答案列表','问答',1,NULL,'69b1d61a5afa47a0a05760c5ac7ac282',4,'questionRelated_answer_page_default','答案列表',0,'questionRelated_answer_2'),(NULL,'default','collection',NULL,'添加问题','问答',1,NULL,'31f359bb910044749c8073b861fa2909',4,'questionRelated_addQuestion_collection_default','添加问题',0,'questionRelated_addQuestion_2'),(NULL,'default','collection',NULL,'添加答案','问答',1,NULL,'d734da046f784ff2a80dc79515b93646',4,'questionRelated_addAnswer_collection_default','添加答案',0,'questionRelated_addAnswer_2'),(NULL,'default','collection',NULL,'回复答案','问答',1,NULL,'62077b6844e2413d9f5e73d1996c7ee9',4,'questionRelated_replyAnswer_collection_default','回复答案',0,'questionRelated_replyAnswer_2');";
       	upgradeService.insertNativeSQL(sql);

    }
   	
   /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
  
    	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('197fb4524ba0483d8406ee25fdc21487','default',-1,'blank_8.html','问题列表','askList',0,1750,4),('1d95af33c5ff4eff96c56a79d3ec68dd','default',-1,NULL,'问题内容(移动版)','queryQuestionContent',1,2040,4),('31f359bb910044749c8073b861fa2909','default',-1,NULL,'发表问题(移动版)','queryAddQuestion',1,2060,4),('4605cd9bb304408883ecc648ba911e37','default',-1,NULL,'查询关注总数','queryFollowCount',1,2010,4),('4f98b0ca689c4fb495c0a12cafe5a650','default',-1,NULL,'问题列表(移动版)','queryQuestionList',1,2030,4),('62077b6844e2413d9f5e73d1996c7ee9','default',-1,NULL,'发表答案回复(移动版)','queryAddAnswerReply',1,2080,4),('69b1d61a5afa47a0a05760c5ac7ac282','default',-1,NULL,'答案列表(移动版)','queryAnswerList',1,2050,4),('6c93b3c854804de983ccd807ade0da3c','default',-1,'questionList.html','我的问题','user/control/questionList',0,1800,1),('7c79820f306143378169167088f17cc2','default',-1,NULL,'查询全部问题标签(json)','queryAllQuestionTag',1,1780,4),('894c1ee85cb34067a0f2c82364178a66','default',-1,'blank_11.html','答案回复','answer_reply',0,1790,4),('9e9cafbe93314ca585aedbe98f3b4e11','default',-1,'blank_10.html','发表问题','user/addQuestion',0,1770,4),('9fdd67aa7e3d4dd7aabbe1ab0fd324e5','default',-1,'answerReplyList.html','我的答案回复','user/control/answerReplyList',0,2000,1),('b9d3684fd5004247b4820ca5b9a5bff3','default',-1,NULL,'查询回答总数','queryAnswerCount',1,2020,4),('d734da046f784ff2a80dc79515b93646','default',-1,NULL,'发表答案(移动版)','queryAddAnswer',1,2070,4),('d7e5fcb22e1b463cb2616616e42d3b63','default',-1,'blank_9.html','问题内容','question',0,1760,4),('f24e26d8f5fb4f408f32fdff4089d083','default',-1,'answerList.html','我的答案','user/control/answerList',0,1900,1);";
        upgradeService.insertNativeSQL(sql);
   	
    }
    
  
       /**
      	 * 插入升级SQL
      	 * @param upgradeService
      	 */
    private static void insertSQL_syspermission(UpgradeService upgradeService){
    	
    	
    	String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('0cb6e21b9d254dd881c201b577fa46f6','POST','AUTH_71e587d45b384855831dcfa5db98f8fe_POST_UPDATE',2,'修改答案回复'),('1931aeb8dd194f8282ebbd4085166f52','POST','AUTH_62c2facd01254ffe99ce7692def6815f_POST_DELETE',1,'删除答案回复'),('1d34b3ce43ae4d8f962a8b27381a3dec','POST','AUTH_670eb3b034d34a82a6053de088e27d0a_POST_UPDATE',1,'还原问题'),('211fa9879cdc41b7b7e8ed3fe308278e','POST','AUTH_decb896d7fee40f09a958647cf14b849_POST_UPDATE',1,'审核问题'),('260db4f1552c4f09a5f6f999e896cdac','GET','AUTH_9be9600772e3420a91ee019acc3ba379_GET_READ',1,'添加答案回复页'),('2f44f50ef9184640b7578bd1eeb9888c','GET','AUTH_4ab032ea2c53438799d6b32b0eebb60b_GET_READ',1,'问题标签列表'),('3093827bb1314f9ab1ba5068fa94bdf6','GET','AUTH_37f5d0f7095b4093a772593dd4e28a39_GET_READ',1,'提交的问题'),('36aaf8e88b324c3b8ce50276cb83e901','GET','AUTH_ad17b7a816ea45b09be8084be966ba4b_GET_READ',1,'问题搜索'),('3a459f7f04b64379bea2ae724cf3f78a','GET','AUTH_ca0e7a1a86384639a025a3fab07bd963_GET_READ',1,'问题列表'),('4e150361440b4132baf9d65563ccb6c7','GET','AUTH_5a8bd389628a4faaa96194435a14b384_GET_READ',1,'全部待审核答案回复'),('621036c4c1e94863a8600c3f771ed176','POST','AUTH_a046d8a39d3b44d694d8c7720cd6a7d2_POST_DELETE',1,'删除答案'),('6271f4f215a349538099bd5ac40a5ddc','POST','AUTH_9be9600772e3420a91ee019acc3ba379_POST_ADD',2,'添加答案回复'),('64e00e15204f4b9e8a87a03e161d2cd6','GET','AUTH_95a1e6ab0ea64972ab36f85f2940d8e9_GET_READ',1,'提交的答案'),('7058d03b34d749f78d942940581cdbca','POST','AUTH_c128236bfefd4cf3b241357733581002_POST_UPDATE',1,'审核答案'),('75e4692e5c614f5abc7d7b5810cf59d9','GET','AUTH_1a93fe93805849d68e478bb91c28480b_GET_READ',1,'添加问题页'),('774d0cdd7c8c443e868c8192d787ff3b','POST','AUTH_9f27719619c244879c74ef032bf67684_POST_UPDATE',2,'修改问题'),('8987621aa88a4479b62d665b09e309e6','GET','AUTH_71b0cd5212874f21a2eb46d87278bc25_GET_READ',1,'添加问题标签页'),('8acdceb3d436410b95c33ab0943df4bb','POST','AUTH_3a3f2b720eb74733b2e661de78decea0_POST_UPDATE',1,'审核答案回复'),('8faaec7f982d4d3b9847b955fc8d973c','POST','AUTH_1a93fe93805849d68e478bb91c28480b_POST_ADD',2,'添加问题'),('967733f255964ca3ac38a5cfdd4b3cd0','GET','AUTH_84b8ba7c751a488885fbfdeb25a0eca3_GET_READ',1,'全部待审核问题'),('99d76e6d914d42cf9765ff1b6927d1f7','POST','AUTH_8dab213a3c11412795f51a834ba6bd05_POST_UPDATE',1,'采纳答案'),('a3358191e3084c8a906fdd3fcd23cf65','GET','AUTH_71e587d45b384855831dcfa5db98f8fe_GET_READ',1,'修改答案回复页'),('a75786d463494a21b2d8e0664141e92a','GET','AUTH_76ffb8fb053647c5b5905b0f605104fe_GET_READ',1,'全部待审核答案'),('b0087f154eaf40be87fa3675768fd414','GET','AUTH_336407134b204deebb6d335ffe080612_GET_READ',1,'修改问题标签页'),('b01cdaa1a9b14ba6970a31506f56ff23','POST','AUTH_eadf2a9785af45b08c5c42d6b1263133_POST_UPDATE',1,'重建问题索引'),('b6d19989f45c4c918a5588d5d339893a','POST','AUTH_c74888ede5e74dce8a656d722836d8b1_POST_UPDATE',2,'修改答案'),('b6e330a0c462407590a51a6e1063677a','POST','AUTH_b0857fe7e73e487798150bd09b2063f6_POST_UPDATE',1,'取消采纳答案'),('bc3cdb8d556f48098fdbb1bbb06bd273','POST','AUTH_71b0cd5212874f21a2eb46d87278bc25_POST_ADD',2,'添加问题标签'),('c2442a047cc940cb862f97ae1fb4a21f','GET','AUTH_2a34da73c3ca436e9fb658f8a1639895_GET_READ',1,'提交的答案回复'),('c706d0be75314aefadedad63df77b056','POST','AUTH_336407134b204deebb6d335ffe080612_POST_UPDATE',2,'修改问题标签'),('ceb28608464548d58f191f29e987c858','GET','AUTH_607959c1d66c4900835c30532ebf656e_GET_READ',1,'查看问题'),('cfe10d0e303b4eb1acfb725110af3570','GET','AUTH_c74888ede5e74dce8a656d722836d8b1_GET_READ',1,'修改答案页'),('dca0b9b3bf664e9795c63bcf78633946','POST','AUTH_938b9dff2a27416abf6ae6ac21eaaaad_POST_DELETE',1,'删除问题'),('de2ae43b2077478ead903fa969172e1c','POST','AUTH_c0b306017906415abce45445b5606bd6_POST_ADD',2,'添加答案'),('de484d1d48ce4b42b086f51b62af403f','GET','AUTH_9f27719619c244879c74ef032bf67684_GET_READ',1,'修改问题页'),('fdde776dc2f44554871b0706b5fe89fc','POST','AUTH_7710a45b7cfa484183ee104ffe20f6c4_POST_DELETE',1,'删除问题标签');";

        upgradeService.insertNativeSQL(sql);
    }
          
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
    	
    	
    	String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'3a459f7f04b64379bea2ae724cf3f78a','ca0e7a1a86384639a025a3fab07bd963'),(NULL,'ceb28608464548d58f191f29e987c858','607959c1d66c4900835c30532ebf656e'),(NULL,'75e4692e5c614f5abc7d7b5810cf59d9','1a93fe93805849d68e478bb91c28480b'),(NULL,'8faaec7f982d4d3b9847b955fc8d973c','1a93fe93805849d68e478bb91c28480b'),(NULL,'75e4692e5c614f5abc7d7b5810cf59d9','b4f51bedcc8d45448e980aec0f32a79f'),(NULL,'8faaec7f982d4d3b9847b955fc8d973c','680e74218d4e4703b6f672cb7d51b7b9'),(NULL,'de484d1d48ce4b42b086f51b62af403f','9f27719619c244879c74ef032bf67684'),(NULL,'774d0cdd7c8c443e868c8192d787ff3b','9f27719619c244879c74ef032bf67684'),(NULL,'de484d1d48ce4b42b086f51b62af403f','669021ae338942df96d09b1ffe0729b9'),(NULL,'774d0cdd7c8c443e868c8192d787ff3b','8f22d9489e3b488d8ee59a76377ee96b'),(NULL,'dca0b9b3bf664e9795c63bcf78633946','938b9dff2a27416abf6ae6ac21eaaaad'),(NULL,'1d34b3ce43ae4d8f962a8b27381a3dec','670eb3b034d34a82a6053de088e27d0a'),(NULL,'211fa9879cdc41b7b7e8ed3fe308278e','decb896d7fee40f09a958647cf14b849'),(NULL,'99d76e6d914d42cf9765ff1b6927d1f7','8dab213a3c11412795f51a834ba6bd05'),(NULL,'b6e330a0c462407590a51a6e1063677a','b0857fe7e73e487798150bd09b2063f6'),(NULL,'de2ae43b2077478ead903fa969172e1c','c0b306017906415abce45445b5606bd6'),(NULL,'de2ae43b2077478ead903fa969172e1c','ef1afb617a754220a0b27ec0cb01efa8'),(NULL,'cfe10d0e303b4eb1acfb725110af3570','c74888ede5e74dce8a656d722836d8b1'),(NULL,'b6d19989f45c4c918a5588d5d339893a','c74888ede5e74dce8a656d722836d8b1'),(NULL,'b6d19989f45c4c918a5588d5d339893a','cf8e8bd2ea2549ae9a4cfe7356c04be4'),(NULL,'621036c4c1e94863a8600c3f771ed176','a046d8a39d3b44d694d8c7720cd6a7d2'),(NULL,'a3358191e3084c8a906fdd3fcd23cf65','71e587d45b384855831dcfa5db98f8fe'),(NULL,'0cb6e21b9d254dd881c201b577fa46f6','71e587d45b384855831dcfa5db98f8fe'),(NULL,'260db4f1552c4f09a5f6f999e896cdac','9be9600772e3420a91ee019acc3ba379'),(NULL,'6271f4f215a349538099bd5ac40a5ddc','9be9600772e3420a91ee019acc3ba379'),(NULL,'1931aeb8dd194f8282ebbd4085166f52','62c2facd01254ffe99ce7692def6815f'),(NULL,'7058d03b34d749f78d942940581cdbca','c128236bfefd4cf3b241357733581002'),(NULL,'8acdceb3d436410b95c33ab0943df4bb','3a3f2b720eb74733b2e661de78decea0'),(NULL,'2f44f50ef9184640b7578bd1eeb9888c','4ab032ea2c53438799d6b32b0eebb60b'),(NULL,'8987621aa88a4479b62d665b09e309e6','71b0cd5212874f21a2eb46d87278bc25'),(NULL,'bc3cdb8d556f48098fdbb1bbb06bd273','71b0cd5212874f21a2eb46d87278bc25'),(NULL,'b0087f154eaf40be87fa3675768fd414','336407134b204deebb6d335ffe080612'),(NULL,'c706d0be75314aefadedad63df77b056','336407134b204deebb6d335ffe080612'),(NULL,'fdde776dc2f44554871b0706b5fe89fc','7710a45b7cfa484183ee104ffe20f6c4'),(NULL,'967733f255964ca3ac38a5cfdd4b3cd0','84b8ba7c751a488885fbfdeb25a0eca3'),(NULL,'a75786d463494a21b2d8e0664141e92a','76ffb8fb053647c5b5905b0f605104fe'),(NULL,'4e150361440b4132baf9d65563ccb6c7','5a8bd389628a4faaa96194435a14b384'),(NULL,'36aaf8e88b324c3b8ce50276cb83e901','ad17b7a816ea45b09be8084be966ba4b'),(NULL,'36aaf8e88b324c3b8ce50276cb83e901','6c6b8190a0864463b68f9f514b1ad4d0'),(NULL,'3093827bb1314f9ab1ba5068fa94bdf6','37f5d0f7095b4093a772593dd4e28a39'),(NULL,'64e00e15204f4b9e8a87a03e161d2cd6','95a1e6ab0ea64972ab36f85f2940d8e9'),(NULL,'c2442a047cc940cb862f97ae1fb4a21f','2a34da73c3ca436e9fb658f8a1639895'),(NULL,'b01cdaa1a9b14ba6970a31506f56ff23','eadf2a9785af45b08c5c42d6b1263133');";
          	
        upgradeService.insertNativeSQL(sql);
    }
    
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_sysresources(UpgradeService upgradeService){
    	
    	String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('1a93fe93805849d68e478bb91c28480b','问答管理','添加问题',20200,'','/control/question/manage.htm?method=add*',NULL,NULL),('2a34da73c3ca436e9fb658f8a1639895','会员管理','提交的答案回复',26970,'','/control/user/manage.htm?method=allAnswerReply*',NULL,NULL),('336407134b204deebb6d335ffe080612','问答管理','修改问题标签',22200,'','/control/questionTag/manage.htm?method=edit*',NULL,NULL),('37f5d0f7095b4093a772593dd4e28a39','会员管理','提交的问题',26950,'','/control/user/manage.htm?method=allQuestion*',NULL,NULL),('3a3f2b720eb74733b2e661de78decea0','问答管理','审核答案回复',21600,'','/control/answer/manage.htm?method=auditAnswerReply&*',NULL,NULL),('4ab032ea2c53438799d6b32b0eebb60b','问答管理','问题标签列表',22000,'','/control/questionTag/list*',NULL,NULL),('5a8bd389628a4faaa96194435a14b384','问答管理','全部待审核答案回复',22600,'','/control/question/allAuditAnswerReply.htm*',NULL,NULL),('607959c1d66c4900835c30532ebf656e','问答管理','查看问题',20100,'','/control/question/manage.htm?method=view*',NULL,NULL),('62c2facd01254ffe99ce7692def6815f','问答管理','删除答案回复',21500,'','/control/answer/manage.htm?method=deleteAnswerReply*',NULL,NULL),('669021ae338942df96d09b1ffe0729b9','问答管理','查询标签',0,NULL,'/control/questionTag/manage.htm?method=allTag&*','9f27719619c244879c74ef032bf67684',1),('670eb3b034d34a82a6053de088e27d0a','问答管理','还原问题',20500,'','/control/question/manage.htm?method=reduction*',NULL,NULL),('680e74218d4e4703b6f672cb7d51b7b9','问答管理','文件上传',0,NULL,'/control/question/manage.htm?method=upload&*','1a93fe93805849d68e478bb91c28480b',2),('6c6b8190a0864463b68f9f514b1ad4d0','问答管理','查询标签',0,NULL,'/control/questionTag/manage.htm?method=questionTagPage&*','ad17b7a816ea45b09be8084be966ba4b',1),('71b0cd5212874f21a2eb46d87278bc25','问答管理','添加问题标签',22100,'','/control/questionTag/manage.htm?method=add*',NULL,NULL),('71e587d45b384855831dcfa5db98f8fe','问答管理','修改答案回复',21400,'','/control/answer/manage.htm?method=editAnswerReply*',NULL,NULL),('76ffb8fb053647c5b5905b0f605104fe','问答管理','全部待审核答案',22500,'','/control/question/allAuditAnswer.htm*',NULL,NULL),('7710a45b7cfa484183ee104ffe20f6c4','问答管理','删除问题标签',22300,'','/control/questionTag/manage.htm?method=delete*',NULL,NULL),('84b8ba7c751a488885fbfdeb25a0eca3','问答管理','全部待审核问题',22400,'','/control/question/allAuditQuestion.htm*',NULL,NULL),('8dab213a3c11412795f51a834ba6bd05','问答管理','采纳答案',20700,'','/control/answer/manage.htm?method=adoptionAnswer*',NULL,NULL),('8f22d9489e3b488d8ee59a76377ee96b','问答管理','文件上传',0,NULL,'/control/question/manage.htm?method=upload&*','9f27719619c244879c74ef032bf67684',2),('938b9dff2a27416abf6ae6ac21eaaaad','问答管理','删除问题',20400,'','/control/question/manage.htm?method=delete*',NULL,NULL),('95a1e6ab0ea64972ab36f85f2940d8e9','会员管理','提交的答案',26960,'','/control/user/manage.htm?method=allAnswer*',NULL,NULL),('9be9600772e3420a91ee019acc3ba379','问答管理','添加答案回复',21300,'','/control/answer/manage.htm?method=addAnswerReply*',NULL,NULL),('9f27719619c244879c74ef032bf67684','问答管理','修改问题',20300,'','/control/question/manage.htm?method=edit*',NULL,NULL),('a046d8a39d3b44d694d8c7720cd6a7d2','问答管理','删除答案',21100,'','/control/answer/manage.htm?method=delete*',NULL,NULL),('ad17b7a816ea45b09be8084be966ba4b','问答管理','问题搜索',22700,'','/control/question/search*',NULL,NULL),('b0857fe7e73e487798150bd09b2063f6','问答管理','取消采纳答案',20800,'','/control/answer/manage.htm?method=cancelAdoptionAnswer*',NULL,NULL),('b4f51bedcc8d45448e980aec0f32a79f','问答管理','查询标签',0,NULL,'/control/questionTag/manage.htm?method=allTag&*','1a93fe93805849d68e478bb91c28480b',1),('c0b306017906415abce45445b5606bd6','问答管理','添加答案',20900,'','/control/answer/manage.htm?method=add*',NULL,NULL),('c128236bfefd4cf3b241357733581002','问答管理','审核答案',21200,'','/control/answer/manage.htm?method=auditAnswer&*',NULL,NULL),('c74888ede5e74dce8a656d722836d8b1','问答管理','修改答案',21000,'','/control/answer/manage.htm?method=edit*',NULL,NULL),('ca0e7a1a86384639a025a3fab07bd963','问答管理','问题列表',20000,'','/control/question/list*',NULL,NULL),('cf8e8bd2ea2549ae9a4cfe7356c04be4','问答管理','图片上传',0,NULL,'/control/answer/manage.htm?method=uploadImage&*','c74888ede5e74dce8a656d722836d8b1',2),('decb896d7fee40f09a958647cf14b849','问答管理','审核问题',20600,'','/control/question/manage.htm?method=auditQuestion*',NULL,NULL),('eadf2a9785af45b08c5c42d6b1263133','全站设置','重建问题索引',49210,'','/control/systemSetting/manage.htm?method=rebuildQuestionIndex&*',NULL,NULL),('ef1afb617a754220a0b27ec0cb01efa8','问答管理','图片上传',0,NULL,'/cms/control/answer/manage.htm?method=uploadImage&*','c0b306017906415abce45445b5606bd6',2);";
       	
       	
       	upgradeService.insertNativeSQL(sql);
    }
    
    /**
   	 * 删除userdynamic表userDynamic_2_idx,userDynamic_3_idx,userDynamic_4_idx索引
   	 * @param upgradeService
   	 */
    private static void deleteIndex_userrolegroup(UpgradeService upgradeService,UserDynamicConfig userDynamicConfig){
    	
    	int tableQuantity = userDynamicConfig.getTableQuantity();
    	for(int i =0; i<tableQuantity; i++){
    		String sql_1 = "ALTER TABLE userdynamic_"+i+" DROP INDEX userDynamic_2_idx";
        	upgradeService.insertNativeSQL(sql_1);
        	
        	String sql_2 = "ALTER TABLE userdynamic_"+i+" DROP INDEX userDynamic_3_idx";
        	upgradeService.insertNativeSQL(sql_2);
        	
        	String sql_3 = "ALTER TABLE userdynamic_"+i+" DROP INDEX userDynamic_4_idx";
        	upgradeService.insertNativeSQL(sql_3);
    		
    	}
    }

    /**
   	 * 修改系统设置字段
   	 * @param upgradeService
   	 */
    private static void updateSQL_systemSetting(UpgradeService upgradeService){
    	String userloginlog_sql = "UPDATE systemsetting SET "
    			+ " questionEditorTag = '{\"fontname\":true,\"fontsize\":true,\"forecolor\":true,\"hilitecolor\":true,\"bold\":true,\"italic\":true,\"underline\":true,\"removeformat\":true,\"link\":true,\"unlink\":true,\"justifyleft\":true,\"justifycenter\":true,\"justifyright\":true,\"insertorderedlist\":true,\"insertunorderedlist\":true,\"emoticons\":true,\"image\":true,\"imageFormat\":[\"JPG\",\"JPEG\",\"BMP\",\"PNG\",\"GIF\"],\"imageSize\":\"5000\",\"file\":false,\"fileFormat\":[],\"fileSize\":null,\"hidePassword\":false,\"hideComment\":false,\"hideGrade\":false,\"hidePoint\":false,\"hideAmount\":false}', "
    			+ " answerEditorTag = '{\"fontname\":true,\"fontsize\":true,\"forecolor\":true,\"hilitecolor\":true,\"bold\":true,\"italic\":true,\"underline\":true,\"removeformat\":true,\"link\":true,\"unlink\":true,\"justifyleft\":true,\"justifycenter\":true,\"justifyright\":true,\"insertorderedlist\":true,\"insertunorderedlist\":true,\"emoticons\":true,\"image\":true,\"imageFormat\":[\"JPG\",\"JPEG\",\"BMP\",\"PNG\",\"GIF\"],\"imageSize\":\"5000\",\"file\":false,\"fileFormat\":[],\"fileSize\":null,\"hidePassword\":false,\"hideComment\":false,\"hideGrade\":false,\"hidePoint\":false,\"hideAmount\":false}', "
    			
    			+ " maxQuestionTagQuantity = 5, "
    			+ " question_submitQuantity = 10, "
    			+ " answer_submitQuantity = 10, "
    			+ " question_rewardPoint = 10,"
    			+ " answer_rewardPoint = 5,"
    			+ " answerReply_rewardPoint = 5,"
    			+ " question_review = 50,"
    			+ " answer_review = 50,"
    			+ " answerReply_review = 50,"
    			+ " allowQuestion = b'1', "
    			+ " allowAnswer = b'1', "
    			+ " realNameUserAllowQuestion = b'0', "
    			+ " realNameUserAllowAnswer = b'0' "
    			+ "WHERE id=1;";
       	upgradeService.insertNativeSQL(userloginlog_sql);
    }
    
    /**
     * 设置UserDynamic表的默认值
     * @param upgradeService
     * @param userLoginLogConfig
     */
    private static void updateSQL_userDynamic(UpgradeService upgradeService,UserLoginLogConfig userLoginLogConfig){
		//表数量
		int tableQuantity = userLoginLogConfig.getTableQuantity();
		for(int i =0; i<tableQuantity; i++){
			if(i == 0){//默认对象
				int page = 1;//分页 当前页
				int maxResult = 200;// 每页显示记录数
				
				while(true){	
					//当前页
					int firstIndex = (page-1)*maxResult;
					
					
					List<Object[]> objectList = upgradeService.queryNativeSQL("select id,module,topicId,commentId,replyId from userdynamic_0 limit "+firstIndex+","+maxResult+";");
					if(objectList == null || objectList.size() ==0){
						break;
					}
					
					for(int o = 0; o<objectList.size(); o++){
						Object[] object = objectList.get(o);
						String id = ObjectConversion.conversion(object[0], ObjectConversion.STRING);//Id
						Integer module = ObjectConversion.conversion(object[1], ObjectConversion.INTEGER);//模块
						Long topicId = ObjectConversion.conversion(object[2], ObjectConversion.LONG);//话题Id
						Long commentId = ObjectConversion.conversion(object[3], ObjectConversion.LONG);//评论Id
						Long replyId = ObjectConversion.conversion(object[4], ObjectConversion.LONG);//回复Id
	
						/** 功能Id组 格式：,话题Id,评论Id,回复Id,  或者 ,问题Id,答案Id,答案回复Id   **/
						String functionIdGroup = null;
						
						if(module.equals(100)){//模块 100.话题 
							functionIdGroup = ","+topicId+",";
						}else if(module.equals(200)){//模块 200.评论
							functionIdGroup = ","+topicId+","+commentId+",";
						}else if(module.equals(300)){//模块 300.引用评论
							functionIdGroup = ","+topicId+","+commentId+",";
						}else if(module.equals(400)){//模块 400.评论回复
							functionIdGroup = ","+topicId+","+commentId+","+replyId+",";
						}
						if(functionIdGroup != null){
							String sql = "UPDATE userdynamic_0 SET functionIdGroup = '"+functionIdGroup+"',questionId=-1,answerId=-1,answerReplyId=-1  WHERE id='"+id+"';";
							 
							upgradeService.insertNativeSQL(sql);
						}
					}
					
					page++;
				}
			}else{
				int page = 1;//分页 当前页
				int maxResult = 200;// 每页显示记录数
				
				while(true){	
					//当前页
					int firstIndex = (page-1)*maxResult;
					
					
					List<Object[]> objectList = upgradeService.queryNativeSQL("select id,module,topicId,commentId,replyId from userdynamic_"+i+" limit "+firstIndex+","+maxResult+";");
					if(objectList == null || objectList.size() ==0){
						break;
					}
					
					for(int o = 0; o<objectList.size(); o++){
						Object[] object = objectList.get(o);
						String id = ObjectConversion.conversion(object[0], ObjectConversion.STRING);//Id
						Integer module = ObjectConversion.conversion(object[1], ObjectConversion.INTEGER);//模块
						Long topicId = ObjectConversion.conversion(object[2], ObjectConversion.LONG);//话题Id
						Long commentId = ObjectConversion.conversion(object[3], ObjectConversion.LONG);//评论Id
						Long replyId = ObjectConversion.conversion(object[4], ObjectConversion.LONG);//回复Id
						
						/** 功能Id组 格式：,话题Id,评论Id,回复Id,  或者 ,问题Id,答案Id,答案回复Id   **/
						String functionIdGroup = null;
						
						if(module.equals(100)){//模块 100.话题 
							functionIdGroup = ","+topicId+",";
						}else if(module.equals(200)){//模块 200.评论
							functionIdGroup = ","+topicId+","+commentId+",";
						}else if(module.equals(300)){//模块 300.引用评论
							functionIdGroup = ","+topicId+","+commentId+",";
						}else if(module.equals(400)){//模块 400.评论回复
							functionIdGroup = ","+topicId+","+commentId+","+replyId+",";
						}
						if(functionIdGroup != null){
							String sql = "UPDATE userdynamic_"+i+" SET functionIdGroup = '"+functionIdGroup+"',questionId=-1,answerId=-1,answerReplyId=-1  WHERE id='"+id+"';";
							 
							upgradeService.insertNativeSQL(sql);
						}
					}
					page++;
				}
			}
		}
	}
    
    
    /**
     * 设置Remind表的默认值
     * @param upgradeService
     * @param remindConfig
     */
    private static void updateSQL_remind(UpgradeService upgradeService,RemindConfig remindConfig){
    	
    	//表编号
		int tableNumber = remindConfig.getTableQuantity();
		for(int i = 0; i<tableNumber; i++){
			
			String sql = "UPDATE remind_"+i+" SET questionId = -1;";
	    	upgradeService.insertNativeSQL(sql);

			
		}
    }
    
    /**
     * 添加栏目
     * @param upgradeService
     */
     private static void updateSQL_column(UpgradeService upgradeService,ColumnManage columnManage){
     	Column column = new Column();
     	column.setName("问答");
     	column.setLinkMode(3);
     	column.setUrl("askList");
     	column.setSort(75);
     	columnManage.addColumn(column, "default");
     	
     }
}
