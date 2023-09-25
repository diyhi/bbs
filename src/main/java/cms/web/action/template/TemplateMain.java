package cms.web.action.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cms.bean.DataView;
import cms.bean.PageView;
import cms.bean.help.Help;
import cms.bean.help.HelpType;
import cms.bean.links.Links;
import cms.bean.membershipCard.MembershipCard;
import cms.bean.question.Answer;
import cms.bean.question.Question;
import cms.bean.question.QuestionTag;
import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.redEnvelope.ReceiveRedEnvelope;
import cms.bean.template.Advert;
import cms.bean.template.Column;
import cms.bean.template.CustomHTML;
import cms.bean.template.Forum;
import cms.bean.template.Layout;
import cms.bean.thirdParty.SupportLoginInterface;
import cms.bean.topic.Comment;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.service.template.TemplateService;
import cms.utils.threadLocal.TemplateThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.template.impl.Advertising_TemplateManage;
import cms.web.action.template.impl.Column_TemplateManage;
import cms.web.action.template.impl.CustomForum_TemplateManage;
import cms.web.action.template.impl.Favorite_TemplateManage;
import cms.web.action.template.impl.Feedback_TemplateManage;
import cms.web.action.template.impl.Follow_TemplateManage;
import cms.web.action.template.impl.Help_TemplateManage;
import cms.web.action.template.impl.Like_TemplateManage;
import cms.web.action.template.impl.Links_TemplateManage;
import cms.web.action.template.impl.MembershipCard_TemplateManage;
import cms.web.action.template.impl.QuestionTag_TemplateManage;
import cms.web.action.template.impl.Question_TemplateManage;
import cms.web.action.template.impl.RedEnvelope_TemplateManage;
import cms.web.action.template.impl.Report_TemplateManage;
import cms.web.action.template.impl.System_TemplateManage;
import cms.web.action.template.impl.Tag_TemplateManage;
import cms.web.action.template.impl.Topic_TemplateManage;

import org.springframework.stereotype.Component;



/**
 * 模板管理入口
 * @author Administrator
 *
 */
@Component("templateMain")
public class TemplateMain {
	@Resource TemplateService templateService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	
	@Resource Tag_TemplateManage tag_TemplateManage;//标签 -- 模板方法实现
	@Resource Topic_TemplateManage topic_TemplateManage;//话题 -- 模板方法实现
	@Resource QuestionTag_TemplateManage questionTag_TemplateManage;// 问题标签 -- 模板方法实现
	@Resource Question_TemplateManage question_TemplateManage;// 问题 -- 模板方法实现
	@Resource Feedback_TemplateManage feedback_TemplateManage;
	@Resource Links_TemplateManage links_TemplateManage;// 友情链接 -- 模板方法实现
	
	@Resource MembershipCard_TemplateManage membershipCard_TemplateManage;// 会员卡 -- 模板方法实现
	
	@Resource Column_TemplateManage column_TemplateManage;// 站点栏目 -- 模板方法实现
	
	@Resource Help_TemplateManage help_TemplateManage;//在线帮助 -- 模板方法实现
	@Resource Advertising_TemplateManage advertising_TemplateManage;//广告 -- 模板方法实现
	
	@Resource Favorite_TemplateManage favorite_TemplateManage;//收藏夹 -- 模板方法实现
	@Resource Like_TemplateManage like_TemplateManage;//点赞 -- 模板方法实现
	@Resource Follow_TemplateManage follow_TemplateManage;//关注 -- 模板方法实现
	
	@Resource CustomForum_TemplateManage customForum_TemplateManage;//自定义版块 -- 模板方法实现
	@Resource System_TemplateManage system_TemplateManage;//系统部分 -- 模板方法实现
	
	@Resource RedEnvelope_TemplateManage redEnvelope_TemplateManage;//红包 -- 模板方法实现
	
	@Resource Report_TemplateManage report_TemplateManage;//举报 -- 模板方法实现
	
	/**
	 * 公共模板处理
	 * @param type 布局类型
	 * @param layoutFile 布局文件
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> list(Integer type,String layoutFile,HttpServletRequest request)throws Exception{
		Map<String,Object> root = new HashMap<String,Object>();
		String dirName = templateService.findTemplateDir_cache();//当前模板使用的目录

		//如果引用代码相同，只运行一次
		List<String> referenceCode = new ArrayList<String>();
		
		
		List<Forum> list = templateService.findForum_cache(dirName, type,layoutFile);
		for(Forum forum : list){
			if(!referenceCode.contains(forum.getReferenceCode())){
				//结构   由|线作分割符 " 引用名称 | 引用路径"组成
				root.put(forum.getReferenceCode(), forum.getReferenceCode()+"|templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/forum/"+forum.getModule()+".html");
				
			}
		
			referenceCode.add(forum.getModule());
		}
		return root;
	}
	/**
	 * 版块对应的模板对象
	 * @param forum
	 * @param submitParameter 提交参数
	 * @param runtimeParameter 运行时参数
	 * @return
	 */
	public Object templateObject(Forum forum,Map<String,Object> submitParameter,Map<String,Object> runtimeParameter){
		if(forum.getForumChildType().equals("标签列表")){
			if(forum.getDisplayType().equals("collection")){//集合
				
				List<Tag> value = tag_TemplateManage.tag_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("话题列表")){
			if(forum.getDisplayType().equals("page")){//分页
				
				PageView<Topic> value = topic_TemplateManage.topic_page(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("相似话题")){
			if(forum.getDisplayType().equals("collection")){//集合
				
				List<Topic> value = topic_TemplateManage.topic_like_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("热门话题")){
			if(forum.getDisplayType().equals("collection")){//集合
				List<Topic> value = topic_TemplateManage.topic_hot_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("话题取消隐藏")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = topic_TemplateManage.topicUnhide_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("话题内容")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Topic value = topic_TemplateManage.content_entityBean(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("添加话题")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = topic_TemplateManage.addTopic_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("修改话题")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = topic_TemplateManage.editTopic_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("评论列表")){
			if(forum.getDisplayType().equals("page")){//分页
				PageView<Comment> value = topic_TemplateManage.comment_page(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("添加评论")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = topic_TemplateManage.addComment_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("引用评论")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = topic_TemplateManage.quoteComment_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("修改评论")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = topic_TemplateManage.editComment_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("回复评论")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = topic_TemplateManage.replyComment_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("修改评论回复")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = topic_TemplateManage.editCommentReply_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("问题标签列表")){
			if(forum.getDisplayType().equals("collection")){//集合
				List<QuestionTag> value = questionTag_TemplateManage.questionTag_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("问题列表")){
			if(forum.getDisplayType().equals("page")){//分页
				PageView<Question> value = question_TemplateManage.question_page(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("问题内容")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Question value = question_TemplateManage.content_entityBean(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("答案列表")){
			if(forum.getDisplayType().equals("page")){//分页
				PageView<Answer> value = question_TemplateManage.answer_page(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("添加问题")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = question_TemplateManage.addQuestion_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("追加问题")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = question_TemplateManage.appendQuestion_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("添加答案")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = question_TemplateManage.addAnswer_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("修改答案")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = question_TemplateManage.editAnswer_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("回复答案")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = question_TemplateManage.replyAnswer_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("修改答案回复")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = question_TemplateManage.editAnswerReply_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("采纳答案")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = question_TemplateManage.adoptionAnswer_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("回答总数")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Long value = question_TemplateManage.answerCount_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("相似问题")){
			if(forum.getDisplayType().equals("collection")){//集合
				
				List<Question> value = question_TemplateManage.question_like_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}
		
		
		
		else if(forum.getForumChildType().equals("加入收藏夹")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = favorite_TemplateManage.addFavorite_collection(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("话题会员收藏总数")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Long value = favorite_TemplateManage.favoriteCount_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("用户是否已经收藏话题")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Boolean value = favorite_TemplateManage.alreadyCollected_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("问题会员收藏总数")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Long value = favorite_TemplateManage.questionFavoriteCount_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("用户是否已经收藏问题")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Boolean value = favorite_TemplateManage.alreadyFavoriteQuestion_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("给话题点赞")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = like_TemplateManage.addLike_collection(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("话题点赞总数")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Long value = like_TemplateManage.likeCount_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("用户是否已经点赞该话题")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Boolean value = like_TemplateManage.alreadyLiked_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("关注用户")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = follow_TemplateManage.addFollow_collection(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("关注总数")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Long value = follow_TemplateManage.followCount_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("粉丝总数")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Long value = follow_TemplateManage.followerCount_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("是否已经关注该用户")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Boolean value = follow_TemplateManage.following_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
			
		}else if(forum.getForumChildType().equals("会员卡列表")){
			if(forum.getDisplayType().equals("collection")){//集合
				List<MembershipCard> value = membershipCard_TemplateManage.membershipCard_collection(forum,submitParameter, runtimeParameter);
				return value;
			}	
		}else if(forum.getForumChildType().equals("会员卡内容")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				MembershipCard value =  membershipCard_TemplateManage.membershipCardContent_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}	
		}else if(forum.getForumChildType().equals("购买会员卡")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = membershipCard_TemplateManage.buyMembershipCard_collection(forum, submitParameter, runtimeParameter);
				return value;
			}	
		}
		
		else if(forum.getForumChildType().equals("发红包内容")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				GiveRedEnvelope value =  redEnvelope_TemplateManage.content_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}	
		}else if(forum.getForumChildType().equals("领取红包用户列表")){
			if(forum.getDisplayType().equals("page")){//分页
				PageView<ReceiveRedEnvelope> value = redEnvelope_TemplateManage.receiveRedEnvelopeUser_page(forum, submitParameter,runtimeParameter);
				return value;
			}	
		}else if(forum.getForumChildType().equals("抢红包")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = redEnvelope_TemplateManage.addReceiveRedEnvelope_collection(forum, submitParameter, runtimeParameter);
				return value;
			}	
		}
		
		else if(forum.getForumChildType().equals("添加在线留言")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = feedback_TemplateManage.addFeedback_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("友情链接列表")){
			if(forum.getDisplayType().equals("collection")){//集合
				List<Links> value = links_TemplateManage.links_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("图片广告")){
			if(forum.getDisplayType().equals("collection")){//集合
				List<Advert> value = advertising_TemplateManage.recommend_collection_image(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("站点栏目列表")){
			if(forum.getDisplayType().equals("collection")){//集合
				List<Column> value = column_TemplateManage.column_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("在线帮助列表")){
			if(forum.getDisplayType().equals("monolayer")){//单层
				DataView<Help> value = help_TemplateManage.help_monolayer(forum, submitParameter,runtimeParameter);
				return value;
			}else if(forum.getDisplayType().equals("page")){//分页
				PageView<Help> value = help_TemplateManage.help_page(forum, submitParameter,runtimeParameter);
				return value;
			}else if(forum.getDisplayType().equals("collection")){//集合
				List<Help> value = help_TemplateManage.help_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("推荐在线帮助")){
			if(forum.getDisplayType().equals("collection")){//集合
				List<Help> value = help_TemplateManage.recommend_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("在线帮助分类")){
			if(forum.getDisplayType().equals("collection")){//集合
				List<HelpType> value = help_TemplateManage.type_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("在线帮助导航")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<Long,String> value = help_TemplateManage.navigation_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("在线帮助内容")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				Help value = help_TemplateManage.content_entityBean(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("添加举报")){
			if(forum.getDisplayType().equals("collection")){//集合
				Map<String,Object> value = report_TemplateManage.addReport_collection(forum, submitParameter,runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("用户自定义HTML")){
			if(forum.getDisplayType().equals("entityBean")){//实体对象
				CustomHTML value = customForum_TemplateManage.customHTML_entityBean(forum, submitParameter, runtimeParameter);
				return value;
			}
		}else if(forum.getForumChildType().equals("热门搜索词")){
			if(forum.getDisplayType().equals("collection")){//集合
				List<String> value = system_TemplateManage.searchWord_collection(forum, submitParameter,runtimeParameter);
				return value;
			}	
		}else if(forum.getForumChildType().equals("第三方登录")){
			if(forum.getDisplayType().equals("collection")){//集合
				List<SupportLoginInterface> value = system_TemplateManage.thirdPartyLogin_collection(forum, submitParameter,runtimeParameter);
				return value;
			}	
		}
		
		
		
		return null;	
	}
	
	
	/**
	 * 公共页(引用版块值)[根据页面是否有标签智能调用]
	 * @param quoteTemplate 显示在某个页面上
	 * @throws Exception
	 */
	public Map<String,Object> publicQuoteCall(String quoteTemplate,HttpServletRequest request)throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		
		//当前模板使用的目录
		String dirName = templateService.findTemplateDir_cache();
		List<Layout> layoutList = templateService.findLayout_cache(dirName, 6);//公共页(引用版块值)
	
		for(Layout layout : layoutList){
			//每个引用代码只含有一个版块
			Forum forum = templateService.findForum_cache(dirName, layout.getReferenceCode());
			if(forum != null){
				//结构   由|线作分割符 " 引用名称 | 引用路径"组成
				map.put(forum.getReferenceCode(), forum.getReferenceCode()+"|templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/forum/"+forum.getModule()+".html");  
			}
		}
		return map;
	}

	/**
	 * 公共页(生成新引用页)[根据页面是否有标签智能调用]
	 * @param quoteTemplate 当前引用模板
	 * @throws Exception
	 */
	public Map<String,Object> newPublic(String quoteTemplate,HttpServletRequest request)throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
	
		
		//当前模板使用的目录
		String dirName = templateService.findTemplateDir_cache();
		//检查所有的公共页(生成新引用页)
		List<Layout> publics = templateService.findLayout_cache(dirName, 5);//公共页(生成新引用页)
		for(Layout layout : publics){
			map.put(layout.getReferenceCode(), layout.getReferenceCode()+"|templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/public/"+layout.getLayoutFile()); 
			List<Forum> forumList = templateService.findForumByLayoutId_cache(dirName,layout.getId());
			for(Forum forum : forumList){
				map.put(forum.getReferenceCode(), forum.getReferenceCode()+"|templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/forum/"+forum.getModule()+".html");  
			}
			//添加到模板参数线程变量
			TemplateThreadLocal.setLayoutFile(layout.getLayoutFile());
		}
		return map;
	}
}
