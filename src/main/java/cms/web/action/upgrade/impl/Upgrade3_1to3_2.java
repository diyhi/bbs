package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import cms.bean.QueryResult;
import cms.bean.template.Templates;
import cms.bean.topic.Comment;
import cms.bean.topic.Reply;
import cms.bean.topic.Topic;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.bean.user.User;
import cms.bean.user.UserDynamic;
import cms.service.template.TemplateService;
import cms.service.topic.CommentService;
import cms.service.topic.TopicService;
import cms.service.upgrade.UpgradeService;
import cms.service.user.UserService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.topic.CommentManage;
import cms.web.action.upgrade.UpgradeManage;
import cms.web.action.user.UserDynamicManage;
import cms.web.action.user.UserManage;

/**
 * 3.1升级到3.2版本执行程序
 *
 */
public class Upgrade3_1to3_2 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
    	TopicService topicService = (TopicService)SpringConfigTool.getContext().getBean("topicServiceBean");
    	UserService userService = (UserService)SpringConfigTool.getContext().getBean("userServiceBean");
    	UserManage userManage = (UserManage)SpringConfigTool.getContext().getBean("userManage");
    	UserDynamicManage userDynamicManage = (UserDynamicManage)SpringConfigTool.getContext().getBean("userDynamicManage");
    	CommentService commentService = (CommentService)SpringConfigTool.getContext().getBean("commentServiceBean");
    	CommentManage commentManage = (CommentManage)SpringConfigTool.getContext().getBean("commentManage");
 
    	
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
    			updateSQL_user(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改用户表(user)字段成功",1))+",");
    			
    			//导入话题到用户动态
    			importTopicToUserDynamic(topicService,userService,userManage, userDynamicManage);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"导入话题到用户动态成功",1))+",");
    			
    			
    			// 导入评论到用户动态
    			importCommentToUserDynamic(topicService,userService,userManage,userDynamicManage,commentService,commentManage);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"导入评论到用户动态成功",1))+",");
    			
    			//导入回复到用户动态
    			importReplyToUserDynamic(topicService,userService,userManage,userDynamicManage,commentService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"导入回复到用户动态成功",1))+",");

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
	 * 导入话题到用户动态
	 */
	private static void importTopicToUserDynamic(TopicService topicService,UserService userService,UserManage userManage,UserDynamicManage userDynamicManage){
		
		int page = 1;//分页 当前页
		int maxResult = 200;// 每页显示记录数
		
		while(true){	
			//当前页
			int firstIndex = (page-1)*maxResult;
			
			//分页查询话题
			List<Topic> topicList = topicService.findTopicByPage(firstIndex, maxResult);
			if(topicList == null || topicList.size() == 0){
				break;
			}
			
			for(Topic topic : topicList){
				User user = userManage.query_cache_findUserByUserName(topic.getUserName());
				if(user != null && topic.getIsStaff() == false){
					//用户动态
					UserDynamic userDynamic = new UserDynamic();
					userDynamic.setId(userDynamicManage.createUserDynamicId(user.getId()));
					userDynamic.setUserName(user.getUserName());
					userDynamic.setModule(100);//模块 100.话题
					userDynamic.setTopicId(topic.getId());
					userDynamic.setPostTime(topic.getPostTime());
					userDynamic.setStatus(topic.getStatus());
					
					Object new_userDynamic = userDynamicManage.createUserDynamicObject(userDynamic);
					userService.saveUserDynamic(new_userDynamic);
				}
			}
			page++;
		}
		
	}
	
	/**
	 * 导入评论到用户动态
	 */
	private static void importCommentToUserDynamic(TopicService topicService,UserService userService,UserManage userManage,UserDynamicManage userDynamicManage,CommentService commentService,CommentManage commentManage){
		
		int page = 1;//分页 当前页
		int maxResult = 200;// 每页显示记录数
		
		while(true){	
			//当前页
			int firstIndex = (page-1)*maxResult;
			
			
			
			
			//分页查询评论
			QueryResult<Comment> qr = commentService.getScrollData(Comment.class, firstIndex, maxResult);

			if(qr == null || qr.getResultlist() == null || qr.getResultlist().size() ==0){
				break;
			}
			
			for(Comment comment :qr.getResultlist()){
				User user = userManage.query_cache_findUserByUserName(comment.getUserName());
				if(user != null && comment.getIsStaff() == false){
					Long quoteId = null;
					
					//引用Id组
					if(comment.getQuoteIdGroup() != null && !"".equals(comment.getQuoteIdGroup().trim())){
						String[] quoteIdArray = comment.getQuoteIdGroup().split(",");
						if(quoteIdArray.length >=1){
							String _quoteId = quoteIdArray[1];
							if(_quoteId != null && !"".equals(_quoteId.trim())){
								quoteId = Long.parseLong(_quoteId);
							}
						}
					}
					if(quoteId != null){
						
						Comment quoteComment = commentManage.query_cache_findByCommentId(quoteId);
						if(quoteComment != null){
							//用户动态
							UserDynamic userDynamic = new UserDynamic();
							userDynamic.setId(userDynamicManage.createUserDynamicId(user.getId()));
							userDynamic.setUserName(user.getUserName());
							userDynamic.setModule(300);//模块 300.引用评论
							userDynamic.setTopicId(comment.getTopicId());
							userDynamic.setCommentId(comment.getId());
							userDynamic.setQuoteCommentId(quoteId);
							userDynamic.setPostTime(comment.getPostTime());
							userDynamic.setStatus(comment.getStatus());
							
							Object new_userDynamic = userDynamicManage.createUserDynamicObject(userDynamic);
							userService.saveUserDynamic(new_userDynamic);
						}
					}else{
						
						//用户动态
						UserDynamic userDynamic = new UserDynamic();
						userDynamic.setId(userDynamicManage.createUserDynamicId(user.getId()));
						userDynamic.setUserName(user.getUserName());
						userDynamic.setModule(200);//模块 200.评论
						userDynamic.setTopicId(comment.getTopicId());
						userDynamic.setCommentId(comment.getId());
						userDynamic.setPostTime(comment.getPostTime());
						userDynamic.setStatus(comment.getStatus());
						
						Object new_userDynamic = userDynamicManage.createUserDynamicObject(userDynamic);
						userService.saveUserDynamic(new_userDynamic);
					}	
				}
    		}
			page++;
		}
		
	}
	
	
	/**
	 * 导入回复到用户动态
	 */
	private static void importReplyToUserDynamic(TopicService topicService,UserService userService,UserManage userManage,UserDynamicManage userDynamicManage,CommentService commentService){
		
		int page = 1;//分页 当前页
		int maxResult = 200;// 每页显示记录数
		
		while(true){	
			//当前页
			int firstIndex = (page-1)*maxResult;
			
			//分页查询回复
			QueryResult<Reply> qr = commentService.getScrollData(Reply.class, firstIndex, maxResult);

			if(qr == null || qr.getResultlist() == null || qr.getResultlist().size() ==0){
				break;
			}
			
			for(Reply reply :qr.getResultlist()){
				User user = userManage.query_cache_findUserByUserName(reply.getUserName());
				if(user != null && reply.getIsStaff() == false){
					//用户动态
					UserDynamic userDynamic = new UserDynamic();
					userDynamic.setId(userDynamicManage.createUserDynamicId(user.getId()));
					userDynamic.setUserName(user.getUserName());
					userDynamic.setModule(400);//模块 400.回复
					userDynamic.setTopicId(reply.getTopicId());
					userDynamic.setCommentId(reply.getCommentId());
					userDynamic.setReplyId(reply.getId());
					userDynamic.setPostTime(reply.getPostTime());
					userDynamic.setStatus(reply.getStatus());
					
					Object new_userDynamic = userDynamicManage.createUserDynamicObject(userDynamic);
					userService.saveUserDynamic(new_userDynamic);
					
				}
				
			}
			page++;
		}
		
	}
    
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_forum(UpgradeService upgradeService){
    	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'话题取消隐藏','话题',1,'userDynamicList.html','5c7624ced2c845b28741c724594b6b0c',1,'topicRelated_topicUnhide_collection_default','话题取消隐藏',0,'topicRelated_topicUnhide_2');";
    	upgradeService.insertNativeSQL(sql);
    }
	
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
       	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('5c7624ced2c845b28741c724594b6b0c','default',-1,'userDynamicList.html','用户动态列表','user/control/userDynamicList',0,900,1);";
       	
       	upgradeService.insertNativeSQL(sql);
    }
    /**
	 * 修改用户字段
	 * @param upgradeService
	 */
    private static void updateSQL_user(UpgradeService upgradeService){
    	String sql = "UPDATE user SET allowUserDynamic = true;";
    	upgradeService.insertNativeSQL(sql);
    }
}
