package cms.web.action.template;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cms.bean.forumCode.ForumCodeNode;
import cms.bean.template.Layout;
import cms.utils.UUIDUtil;
import cms.web.action.FileManage;
import cms.web.action.forumCode.ForumCodeManage;

/**
 * 模板管理
 *
 */
@Component("templateManage")
public class TemplateManage {
	@Resource ForumCodeManage forumCodeManage;
	@Resource FileManage fileManage;
	
	
	/**
	 * 新建布局
	 * @param dirName 模板目录名称
	 */
	public List<Layout> newLayout(String dirName){
		//生成布局默认页
		List<Layout> layoutList = new ArrayList<Layout>();
		//首页
		Layout layout_index = new Layout();
		layout_index.setId(UUIDUtil.getUUID32());
		layout_index.setName("首页");
		layout_index.setDirName(dirName);
		layout_index.setLayoutFile("index.html");
		layout_index.setType(1);//默认页
		layout_index.setSort(10);//排序
		layout_index.setReferenceCode("index");
		layoutList.add(layout_index);
		
		
		//话题搜索
		Layout search_page = new Layout();
		search_page.setId(UUIDUtil.getUUID32());
		search_page.setName("话题搜索页");
		search_page.setDirName(dirName);
		search_page.setLayoutFile("search.html");
		search_page.setType(1);//默认页
		search_page.setSort(30);//排序
		search_page.setReferenceCode("search");
		layoutList.add(search_page);
		
		
		//在线帮助'更多'
		Layout layout_Help_more = new Layout();
		layout_Help_more.setId(UUIDUtil.getUUID32());
		layout_Help_more.setName("在线帮助'更多'");
		layout_Help_more.setDirName(dirName);
		layout_Help_more.setLayoutFile("more_help.html");
		layout_Help_more.setType(1);//默认页
		layout_Help_more.setSort(60);//排序
		layout_Help_more.setReferenceCode("more");
		layout_Help_more.setForumData(3);//在线帮助
		layoutList.add(layout_Help_more);
		
		
		//注册页
		Layout layout_register = new Layout();
		layout_register.setId(UUIDUtil.getUUID32());
		layout_register.setName("注册页");
		layout_register.setDirName(dirName);
		layout_register.setLayoutFile("register.html");
		layout_register.setType(1);//默认页
		layout_register.setSort(130);//排序
		layout_register.setReferenceCode("register");
		layoutList.add(layout_register);
		
		//用户协议
		Layout layout_agreement = new Layout();
		layout_agreement.setId(UUIDUtil.getUUID32());
		layout_agreement.setName("用户协议");
		layout_agreement.setDirName(dirName);
		layout_agreement.setLayoutFile("agreement.html");
		layout_agreement.setType(1);//默认页
		layout_agreement.setSort(135);//排序
		layout_agreement.setReferenceCode("agreement");
		layoutList.add(layout_agreement);
		
		Layout layout_findPassWord_step1 = new Layout();
		layout_findPassWord_step1.setId(UUIDUtil.getUUID32());
		layout_findPassWord_step1.setName("找回密码第一步");
		layout_findPassWord_step1.setDirName(dirName);
		layout_findPassWord_step1.setLayoutFile("findPassWord_step1.html");
		layout_findPassWord_step1.setType(1);//默认页
		layout_findPassWord_step1.setSort(136);//排序
		layout_findPassWord_step1.setReferenceCode("findPassWord/step1");
		layoutList.add(layout_findPassWord_step1);
		
		Layout layout_findPassWord_step2 = new Layout();
		layout_findPassWord_step2.setId(UUIDUtil.getUUID32());
		layout_findPassWord_step2.setName("找回密码第二步");
		layout_findPassWord_step2.setDirName(dirName);
		layout_findPassWord_step2.setLayoutFile("findPassWord_step2.html");
		layout_findPassWord_step2.setType(1);//默认页
		layout_findPassWord_step2.setSort(137);//排序
		layout_findPassWord_step2.setReferenceCode("findPassWord/step2");
		layoutList.add(layout_findPassWord_step2);
		
		
		
		
		//默认跳转页面
		Layout layout_jump = new Layout();
		layout_jump.setId(UUIDUtil.getUUID32());
		layout_jump.setName("默认跳转页");
		layout_jump.setDirName(dirName);
		layout_jump.setLayoutFile("jump.html");
		layout_jump.setType(1);//默认页
		layout_jump.setSort(140);//排序
		layout_jump.setReferenceCode("jump");
		layoutList.add(layout_jump);
		
		//默认消息页面
		Layout layout_message = new Layout();
		layout_message.setId(UUIDUtil.getUUID32());
		layout_message.setName("默认消息页");
		layout_message.setDirName(dirName);
		layout_message.setLayoutFile("message.html");
		layout_message.setType(1);//默认页
		layout_message.setSort(150);//排序
		layout_message.setReferenceCode("message");
		layoutList.add(layout_message);
		
		
		//登录页
		Layout layout_login = new Layout();
		layout_login.setId(UUIDUtil.getUUID32());
		layout_login.setName("登录页");
		layout_login.setDirName(dirName);
		layout_login.setLayoutFile("login.html");
		layout_login.setType(1);//默认页
		layout_login.setSort(160);//排序
		layout_login.setReferenceCode("login");
		layoutList.add(layout_login);
		
		
		
		
		//用户中心页
		Layout layout_home = new Layout();
		layout_home.setId(UUIDUtil.getUUID32());
		layout_home.setName("用户中心页");
		layout_home.setDirName(dirName);
		layout_home.setLayoutFile("home.html");
		layout_home.setType(1);//默认页
		layout_home.setSort(170);//排序
		layout_home.setReferenceCode("user/control/home");
		layoutList.add(layout_home);
		
		//我的话题
		Layout layout_topic = new Layout();
		layout_topic.setId(UUIDUtil.getUUID32());
		layout_topic.setName("我的话题");
		layout_topic.setDirName(dirName);
		layout_topic.setLayoutFile("topicList.html");
		layout_topic.setType(1);//默认页
		layout_topic.setSort(180);//排序
		layout_topic.setReferenceCode("user/control/topicList");
		layoutList.add(layout_topic);
		
		//我的评论
		Layout layout_comment = new Layout();
		layout_comment.setId(UUIDUtil.getUUID32());
		layout_comment.setName("我的评论");
		layout_comment.setDirName(dirName);
		layout_comment.setLayoutFile("commentList.html");
		layout_comment.setType(1);//默认页
		layout_comment.setSort(180);//排序
		layout_comment.setReferenceCode("user/control/commentList");
		layoutList.add(layout_comment);
		
		//我的回复
		Layout layout_reply = new Layout();
		layout_reply.setId(UUIDUtil.getUUID32());
		layout_reply.setName("我的回复");
		layout_reply.setDirName(dirName);
		layout_reply.setLayoutFile("replyList.html");
		layout_reply.setType(1);//默认页
		layout_reply.setSort(180);//排序
		layout_reply.setReferenceCode("user/control/replyList");
		layoutList.add(layout_reply);
		
		
		//积分
		Layout layout_point = new Layout();
		layout_point.setId(UUIDUtil.getUUID32());
		layout_point.setName("积分");
		layout_point.setDirName(dirName);
		layout_point.setLayoutFile("point.html");
		layout_point.setType(1);//默认页
		layout_point.setSort(220);//排序
		layout_point.setReferenceCode("user/control/point");
		layoutList.add(layout_point);
		
		
		//修改会员
		Layout layout_editUser = new Layout();
		layout_editUser.setId(UUIDUtil.getUUID32());
		layout_editUser.setName("修改会员");
		layout_editUser.setDirName(dirName);
		layout_editUser.setLayoutFile("editUser.html");
		layout_editUser.setType(1);//默认页
		layout_editUser.setSort(270);//排序
		layout_editUser.setReferenceCode("user/control/editUser");
		layoutList.add(layout_editUser);
		
		//实名认证
		Layout layout_realNameAuthentication = new Layout();
		layout_realNameAuthentication.setId(UUIDUtil.getUUID32());
		layout_realNameAuthentication.setName("实名认证");
		layout_realNameAuthentication.setDirName(dirName);
		layout_realNameAuthentication.setLayoutFile("realNameAuthentication.html");
		layout_realNameAuthentication.setType(1);//默认页
		layout_realNameAuthentication.setSort(274);//排序
		layout_realNameAuthentication.setReferenceCode("user/control/realNameAuthentication");
		layoutList.add(layout_realNameAuthentication);
		
		
		//绑定手机
		Layout layout_phoneBinding = new Layout();
		layout_phoneBinding.setId(UUIDUtil.getUUID32());
		layout_phoneBinding.setName("绑定手机");
		layout_phoneBinding.setDirName(dirName);
		layout_phoneBinding.setLayoutFile("phoneBinding.html");
		layout_phoneBinding.setType(1);//默认页
		layout_phoneBinding.setSort(275);//排序
		layout_phoneBinding.setReferenceCode("user/control/phoneBinding");
		layoutList.add(layout_phoneBinding);
		
		
		//更换绑定手机第一步
		Layout layout_updatePhoneBinding_step1 = new Layout();
		layout_updatePhoneBinding_step1.setId(UUIDUtil.getUUID32());
		layout_updatePhoneBinding_step1.setName("更换绑定手机第一步");
		layout_updatePhoneBinding_step1.setDirName(dirName);
		layout_updatePhoneBinding_step1.setLayoutFile("updatePhoneBinding_step1.html");
		layout_updatePhoneBinding_step1.setType(1);//默认页
		layout_updatePhoneBinding_step1.setSort(276);//排序
		layout_updatePhoneBinding_step1.setReferenceCode("user/control/updatePhoneBinding/step1");
		layoutList.add(layout_updatePhoneBinding_step1);
		
		//更换绑定手机第二步
		Layout layout_updatePhoneBinding_step2 = new Layout();
		layout_updatePhoneBinding_step2.setId(UUIDUtil.getUUID32());
		layout_updatePhoneBinding_step2.setName("更换绑定手机第二步");
		layout_updatePhoneBinding_step2.setDirName(dirName);
		layout_updatePhoneBinding_step2.setLayoutFile("updatePhoneBinding_step2.html");
		layout_updatePhoneBinding_step2.setType(1);//默认页
		layout_updatePhoneBinding_step2.setSort(277);//排序
		layout_updatePhoneBinding_step2.setReferenceCode("user/control/updatePhoneBinding/step2");
		layoutList.add(layout_updatePhoneBinding_step2);
		
		
		
		//用户登录日志列表
		Layout layout_userLoginLogList = new Layout();
		layout_userLoginLogList.setId(UUIDUtil.getUUID32());
		layout_userLoginLogList.setName("用户登录日志列表");
		layout_userLoginLogList.setDirName(dirName);
		layout_userLoginLogList.setLayoutFile("userLoginLogList.html");
		layout_userLoginLogList.setType(1);//默认页
		layout_userLoginLogList.setSort(310);//排序
		layout_userLoginLogList.setReferenceCode("user/control/userLoginLogList");
		layoutList.add(layout_userLoginLogList);
		
		
		//私信列表
		Layout layout_privateMessageList = new Layout();
		layout_privateMessageList.setId(UUIDUtil.getUUID32());
		layout_privateMessageList.setName("私信列表");
		layout_privateMessageList.setDirName(dirName);
		layout_privateMessageList.setLayoutFile("privateMessageList.html");
		layout_privateMessageList.setType(1);//默认页
		layout_privateMessageList.setSort(350);//排序
		layout_privateMessageList.setReferenceCode("user/control/privateMessageList");
		layoutList.add(layout_privateMessageList);
		
		//私信对话列表
		Layout layout_privateMessageChatList = new Layout();
		layout_privateMessageChatList.setId(UUIDUtil.getUUID32());
		layout_privateMessageChatList.setName("私信对话列表");
		layout_privateMessageChatList.setDirName(dirName);
		layout_privateMessageChatList.setLayoutFile("privateMessageChatList.html");
		layout_privateMessageChatList.setType(1);//默认页
		layout_privateMessageChatList.setSort(400);//排序
		layout_privateMessageChatList.setReferenceCode("user/control/privateMessageChatList");
		layoutList.add(layout_privateMessageChatList);
		
		//添加私信
		Layout layout_addPrivateMessage = new Layout();
		layout_addPrivateMessage.setId(UUIDUtil.getUUID32());
		layout_addPrivateMessage.setName("添加私信");
		layout_addPrivateMessage.setDirName(dirName);
		layout_addPrivateMessage.setLayoutFile("addPrivateMessage.html");
		layout_addPrivateMessage.setType(1);//默认页
		layout_addPrivateMessage.setSort(450);//排序
		layout_addPrivateMessage.setReferenceCode("user/control/addPrivateMessage");
		layoutList.add(layout_addPrivateMessage);
		
		//系统通知列表
		Layout layout_systemNotifyList = new Layout();
		layout_systemNotifyList.setId(UUIDUtil.getUUID32());
		layout_systemNotifyList.setName("系统通知列表");
		layout_systemNotifyList.setDirName(dirName);
		layout_systemNotifyList.setLayoutFile("systemNotifyList.html");
		layout_systemNotifyList.setType(1);//默认页
		layout_systemNotifyList.setSort(500);//排序
		layout_systemNotifyList.setReferenceCode("user/control/systemNotifyList");
		layoutList.add(layout_systemNotifyList);
		
		//提醒列表
		Layout layout_remindList = new Layout();
		layout_remindList.setId(UUIDUtil.getUUID32());
		layout_remindList.setName("提醒列表");
		layout_remindList.setDirName(dirName);
		layout_remindList.setLayoutFile("remindList.html");
		layout_remindList.setType(1);//默认页
		layout_remindList.setSort(550);//排序
		layout_remindList.setReferenceCode("user/control/remindList");
		layoutList.add(layout_remindList);

		//收藏夹列表
		Layout layout_favoriteList = new Layout();
		layout_favoriteList.setId(UUIDUtil.getUUID32());
		layout_favoriteList.setName("收藏夹列表");
		layout_favoriteList.setDirName(dirName);
		layout_favoriteList.setLayoutFile("favoriteList.html");
		layout_favoriteList.setType(1);//默认页
		layout_favoriteList.setSort(600);//排序
		layout_favoriteList.setReferenceCode("user/control/favoriteList");
		layoutList.add(layout_favoriteList);
		
		//话题收藏列表
		Layout layout_dynamicImageFavoriteList = new Layout();
		layout_dynamicImageFavoriteList.setId(UUIDUtil.getUUID32());
		layout_dynamicImageFavoriteList.setName("话题收藏列表");
		layout_dynamicImageFavoriteList.setDirName(dirName);
		layout_dynamicImageFavoriteList.setLayoutFile("topicFavoriteList.html");
		layout_dynamicImageFavoriteList.setType(1);//默认页
		layout_dynamicImageFavoriteList.setSort(700);//排序
		layout_dynamicImageFavoriteList.setReferenceCode("user/control/topicFavoriteList");
		layoutList.add(layout_dynamicImageFavoriteList);
		
		//话题取消隐藏用户列表
		Layout layout_topicUnhideList = new Layout();
		layout_topicUnhideList.setId(UUIDUtil.getUUID32());
		layout_topicUnhideList.setName("话题取消隐藏用户列表");
		layout_topicUnhideList.setDirName(dirName);
		layout_topicUnhideList.setLayoutFile("topicUnhideList.html");
		layout_topicUnhideList.setType(1);//默认页
		layout_topicUnhideList.setSort(800);//排序
		layout_topicUnhideList.setReferenceCode("user/control/topicUnhideList");
		layoutList.add(layout_topicUnhideList);
		
		//用户动态列表
		Layout layout_userDynamicList = new Layout();
		layout_userDynamicList.setId(UUIDUtil.getUUID32());
		layout_userDynamicList.setName("用户动态列表");
		layout_userDynamicList.setDirName(dirName);
		layout_userDynamicList.setLayoutFile("userDynamicList.html");
		layout_userDynamicList.setType(1);//默认页
		layout_userDynamicList.setSort(900);//排序
		layout_userDynamicList.setReferenceCode("user/control/userDynamicList");
		layoutList.add(layout_userDynamicList);
		
		return layoutList;
	}
	
	/**
	 * 新建模板
	 * @param dirName 新模板目录名称
	 */
	public List<Layout> newTemplate(String newDirName){
		List<Layout> layoutList = this.newLayout(newDirName);

		//生成资源目录
		String resource_path = "common"+File.separator+newDirName+File.separator;
		fileManage.createFolder(resource_path+"pc"+File.separator);
		fileManage.createFolder(resource_path+"wap"+File.separator);
		
		//生成模板目录
		String path_dir = "WEB-INF"+File.separator+"templates"+File.separator+newDirName;
		fileManage.createFolder(path_dir);
		
		//生成模板目录内文件夹
		List<String> folder = new ArrayList<String>();//文件夹名称
		folder.add("forum");//版块
		folder.add("public");//公共页面
		for(String s: folder){
			String pc_path_folder = "WEB-INF"+File.separator+"templates"+File.separator+newDirName+File.separator+"pc"+File.separator+s;
			fileManage.createFolder(pc_path_folder);
			
			String wap_path_folder = "WEB-INF"+File.separator+"templates"+File.separator+newDirName+File.separator+"wap"+File.separator+s;
			fileManage.createFolder(wap_path_folder);
		}
		
		
		//生成布局默认页文件
		for(Layout layout :layoutList){
			String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+newDirName+File.separator+"pc"+File.separator+layout.getLayoutFile();
			//创建文件并将注释写入模板文件
			fileManage.writeStringToFile(pc_path,"<#-- "+layout.getName()+" -->","utf-8",false);
			
			String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+newDirName+File.separator+"wap"+File.separator+layout.getLayoutFile();
			//创建文件并将注释写入模板文件
			fileManage.writeStringToFile(wap_path,"<#-- "+layout.getName()+" -->","utf-8",false);
		}
		//生成版块默认文件
		List<ForumCodeNode> forumCodeNodeList = forumCodeManage.forumCodeNodeList(newDirName);
		for(ForumCodeNode forumCodeNode : forumCodeNodeList){
			List<ForumCodeNode> childNodeList = forumCodeNode.getChildNode();
			for(ForumCodeNode childNode :childNodeList){
				String prefix = childNode.getPrefix();//版块代码文件名称前缀 
				if(childNode.getDisplayType() != null && childNode.getDisplayType().size() >0){
					for(String s :childNode.getDisplayType()){
						String displayType = "";
						if("单层".equals(s)){
							displayType = "monolayer";
						}else if("多层".equals(s)){
							displayType = "multilayer";
						}else if("分页".equals(s)){
							displayType = "page";
						}else if("实体对象".equals(s)){
							displayType = "entityBean";
						}else if("集合".equals(s)){
							displayType = "collection";
						}
						String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+newDirName+File.separator+"pc"+File.separator+"forum"+File.separator+prefix+displayType+"_default.html";

						//创建文件并将注释写入模板文件
						fileManage.writeStringToFile(pc_path,"<#-- "+childNode.getNodeName()+"  "+s+" -->","utf-8",false);
						String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+newDirName+File.separator+"wap"+File.separator+"forum"+File.separator+prefix+displayType+"_default.html";

						//创建文件并将注释写入模板文件
						fileManage.writeStringToFile(wap_path,"<#-- "+childNode.getNodeName()+"  "+s+" -->","utf-8",false);
					}
				}else{
					String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+newDirName+File.separator+"pc"+File.separator+"forum"+File.separator+prefix+"default.html";
					//创建文件并将注释写入模板文件
					fileManage.writeStringToFile(pc_path,"<#-- "+childNode.getNodeName()+" -->","utf-8",false);
					
					String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+newDirName+File.separator+"wap"+File.separator+"forum"+File.separator+prefix+"default.html";
					//创建文件并将注释写入模板文件
					fileManage.writeStringToFile(wap_path,"<#-- "+childNode.getNodeName()+" -->","utf-8",false);
				}
			}
		}
		return layoutList;
	}
	
	
	/**
	 * 读取示例程序
	 * @param referenceCodeOrModule 版块输入版块模板  布局输入引用代码
	 * @return
	 */
	public String readExample(String referenceCodeOrModule){
		String example = "";

		if(referenceCodeOrModule != null && !"".equals(referenceCodeOrModule.trim())){
			
			String reference_start = null;
			List<Layout> layoutList = this.newLayout(null);
			if(layoutList != null && layoutList.size() >0){
				for(Layout layout : layoutList){
					if(layout.getLayoutFile().equals(referenceCodeOrModule+".html")){
						reference_start = referenceCodeOrModule;
						break;
					}
				}
			}
			
			if(reference_start == null){
				//从右往左截取到相等的字符
				reference_start = StringUtils.substringBeforeLast(referenceCodeOrModule, "_");
			}
			
			String path = "WEB-INF"+ File.separator+ "example" + File.separator+reference_start+".html";
			
			example = fileManage.readFileToString(path,"utf-8");
			
		}
		
		return example;
	}
}
