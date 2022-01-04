package cms.web.action.template.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.MediaInfo;
import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.setting.SystemSetting;
import cms.bean.template.Forum;
import cms.bean.template.Forum_CommentRelated_Comment;
import cms.bean.template.Forum_TopicRelated_LikeTopic;
import cms.bean.template.Forum_TopicRelated_Topic;
import cms.bean.topic.Comment;
import cms.bean.topic.HideTagType;
import cms.bean.topic.ImageInfo;
import cms.bean.topic.Quote;
import cms.bean.topic.Reply;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.bean.topic.TopicUnhide;
import cms.bean.user.AccessUser;
import cms.bean.user.ResourceEnum;
import cms.bean.user.User;
import cms.bean.user.UserGrade;
import cms.service.setting.SettingService;
import cms.service.topic.CommentService;
import cms.service.topic.TagService;
import cms.service.topic.TopicService;
import cms.service.user.UserGradeService;
import cms.utils.JsonUtils;
import cms.utils.SecureLink;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.common.CaptchaManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.lucene.TopicLuceneManage;
import cms.web.action.mediaProcess.MediaProcessQueueManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.topic.CommentManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;

/**
 * 话题 -- 模板方法实现
 *
 */
@Component("topic_TemplateManage")
public class Topic_TemplateManage {
	@Resource TopicService topicService; 
	@Resource TagService tagService;
	@Resource CommentService commentService; 

	@Resource TextFilterManage textFilterManage;
	@Resource CommentManage commentManage;
	@Resource CaptchaManage captchaManage;
	
	@Resource SettingService settingService;
	@Resource SettingManage settingManage;
	@Resource TopicManage topicManage;
	
	@Resource TopicLuceneManage topicLuceneManage;
	
	@Resource UserManage userManage;
	@Resource UserGradeService userGradeService;
	@Resource UserRoleManage userRoleManage;
	@Resource FileManage fileManage;
	@Resource MediaProcessQueueManage mediaProcessQueueManage;
	
	/**
	 * 话题列表  -- 分页
	 * @param forum
	 */
	public PageView<Topic> topic_page(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_TopicRelated_Topic forum_TopicRelated_Topic = JsonUtils.toObject(formValueJSON,Forum_TopicRelated_Topic.class);
			if(forum_TopicRelated_Topic != null){
				int maxResult = settingService.findSystemSetting_cache().getForestagePageNumber();
				//每页显示记录数
				if(forum_TopicRelated_Topic.getTopic_maxResult() != null && forum_TopicRelated_Topic.getTopic_maxResult() >0){
					maxResult = forum_TopicRelated_Topic.getTopic_maxResult();
				}
				
				return this.topic_SQL_Page(forum_TopicRelated_Topic,parameter,runtimeParameter,maxResult);
			}
			
		}
		return null;
	}
	
	
	/**
	 * 话题SQL分页
	 * @param maxResult 每页显示记录数
	 */
	private PageView<Topic> topic_SQL_Page(Forum_TopicRelated_Topic forum_TopicRelated_Topic,Map<String,Object> parameter,Map<String,Object> runtimeParameter,int maxResult){
		int page = 1;//分页 当前页
		int pageCount=10;// 页码显示总数
		int sort = 1;//排序
		Long tagId = null;//标签Id
		
		String requestURI = "";
		String queryString = "";
		//标签Id
		if(forum_TopicRelated_Topic.getTopic_tagId() != null && forum_TopicRelated_Topic.getTopic_tagId() >0){
			tagId = forum_TopicRelated_Topic.getTopic_tagId();
		}
		//排序
		if(forum_TopicRelated_Topic.getTopic_sort() != null && forum_TopicRelated_Topic.getTopic_sort() >0){
			sort = forum_TopicRelated_Topic.getTopic_sort();
		}
		
		
		
		//页码显示总数
		if(forum_TopicRelated_Topic.getTopic_pageCount() != null && forum_TopicRelated_Topic.getTopic_pageCount() >0){
			pageCount = forum_TopicRelated_Topic.getTopic_pageCount();
		}
		//获取提交参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				
				if("page".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=9){
							page = Integer.parseInt(paramIter.getValue().toString());
						}
					}
				}else if("tagId".equals(paramIter.getKey())){
					if(forum_TopicRelated_Topic.isTopic_tag_transferPrameter()){
						if(Verification.isNumeric(paramIter.getValue().toString())){
							if(paramIter.getValue().toString().length() <=18){
								tagId = Long.parseLong(paramIter.getValue().toString());	
							}
						}
					}
				}
			}
		}
	
		AccessUser accessUser = null;

		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("requestURI".equals(paramIter.getKey())){
					requestURI = (String)paramIter.getValue();
				}else if("queryString".equals(paramIter.getKey())){
					queryString = (String)paramIter.getValue();
				}else if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		PageForm pageForm = new PageForm();
		pageForm.setPage(page);
		
		//调用分页算法代码
		PageView<Topic> pageView = new PageView<Topic>(maxResult,pageForm.getPage(),pageCount,requestURI,queryString);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

		//执行查询
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		
		
		
		
		//标签Id
		if(tagId != null){
			jpql.append(" and o.tagId=?"+ (params.size()+1));
			params.add(tagId);//加上查询参数
		}

		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(20);//设置o.visible=?1是否可见
		
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("sort", "desc");//优先级   大-->小
		//排行依据
		if(sort == 1){
			orderby.put("id", "desc");//发布时间排序   新-->旧
		}else if(sort == 2){
			orderby.put("id", "asc");//发布时间排序  旧-->新
		}else if(sort == 3){
			orderby.put("lastReplyTime", "desc");//回复时间排序   新-->旧
		}else if(sort == 4){
			orderby.put("lastReplyTime", "asc");//回复时间排序  旧-->新
		}
		
		
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
		QueryResult<Topic> qr = topicService.getScrollData(Topic.class,firstIndex, maxResult, jpql_str, params.toArray(),orderby);

		
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			SystemSetting systemSetting = settingService.findSystemSetting_cache();
			
			
			for(Topic topic : qr.getResultlist()){
				//处理视频播放器标签
				if(topic.getContent() != null && !"".equals(topic.getContent().trim())){
					//处理富文本路径
					topic.setContent(fileManage.processRichTextFilePath(topic.getContent(),"topic"));
					
					Integer topicContentUpdateMark = topicManage.query_cache_markUpdateTopicStatus(topic.getId(), Integer.parseInt(RandomStringUtils.randomNumeric(8)));
					
					//生成处理'视频播放器'Id
					String processVideoPlayerId = mediaProcessQueueManage.createProcessVideoPlayerId(topic.getId(),topicContentUpdateMark);
					
					//处理视频信息
					List<MediaInfo> mediaInfoList = mediaProcessQueueManage.query_cache_processVideoInfo(topic.getContent(),processVideoPlayerId,topic.getTagId(),systemSetting.getFileSecureLinkSecret());
					
					topic.setMediaInfoList(mediaInfoList);
					
				}
			}
			
			
			//查询标签名称
			List<Tag> tagList = tagService.findAllTag_cache();
			Map<Long,List<String>> tagRoleNameMap = new HashMap<Long,List<String>>();//标签角色名称 key:标签Id value:角色名称集合
			Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id value:角色名称集合
			Map<Long,Boolean> userViewPermissionMap = new HashMap<Long,Boolean>();//用户如果对话题项是否有查看权限  key:标签Id value:是否有查看权限
			
			if(tagList != null && tagList.size() >0){
				for(Topic topic : qr.getResultlist()){
					
					//解析隐藏标签
					Map<Integer,Object> analysisHiddenTagMap = topicManage.query_cache_analysisHiddenTag(topic.getContent(),topic.getId());
					if(analysisHiddenTagMap != null && analysisHiddenTagMap.size() >0){
						
						//内容含有隐藏标签类型
						LinkedHashMap<Integer,Boolean> hideTagTypeMap = new LinkedHashMap<Integer,Boolean>();//key:内容含有隐藏标签类型  10.输入密码可见  20.评论话题可见  30.达到等级可见 40.积分购买可见 50.余额购买可见  value:当前用户是否已对隐藏内容解锁
						
						//允许可见的隐藏标签
						List<Integer> visibleTagList = this.getVisibleTagList(accessUser,topic);
						
						
						for (HideTagType hideTagType : HideTagType.values()) {//按枚举类的顺序排序
				            for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
								if(entry.getKey().equals(hideTagType.getName())){
									if(visibleTagList.contains(entry.getKey())){
										hideTagTypeMap.put(entry.getKey(), true);
										
									}else{
										hideTagTypeMap.put(entry.getKey(), false);
									}
									break;
								}
							}
				        }
						topic.setHideTagTypeMap(hideTagTypeMap);
						
					}
					
					topic.setIpAddress(null);//IP地址不显示
					topic.setContent(null);//话题内容不显示
					if(topic.getPostTime().equals(topic.getLastReplyTime())){//如果发贴时间等于回复时间，则不显示回复时间
						topic.setLastReplyTime(null);
					}
					
					if(topic.getIsStaff() == false){//会员
						userRoleNameMap.put(topic.getUserName(), null);
					}
					for(Tag tag: tagList){
						if(topic.getTagId().equals(tag.getId())){
							topic.setTagName(tag.getName());
							tagRoleNameMap.put(tag.getId(), null);
							userViewPermissionMap.put(tag.getId(), null);
							
							break;
						}
					}
					
					if(topic.getImage() != null && !"".equals(topic.getImage().trim())){
						List<ImageInfo> imageInfoList = JsonUtils.toGenericObject(topic.getImage().trim(),new TypeReference< List<ImageInfo> >(){});
						if(imageInfoList != null && imageInfoList.size() >0){
							for(ImageInfo imageInfo : imageInfoList){
								imageInfo.setPath(fileManage.fileServerAddress()+imageInfo.getPath());
							}
							topic.setImageInfoList(imageInfoList);
						}
					}
					
				}
			}
			
			if(tagRoleNameMap != null && tagRoleNameMap.size() >0){
				for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
					List<String> roleNameList = userRoleManage.queryAllowViewTopicRoleName(entry.getKey());
					entry.setValue(roleNameList);
				}
			}
			
			if(userRoleNameMap != null && userRoleNameMap.size() >0){
				for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
					List<String> roleNameList = userRoleManage.queryUserRoleName(entry.getKey());
					entry.setValue(roleNameList);
				}
			}
			if(userViewPermissionMap != null && userViewPermissionMap.size()>0){
				for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
					//是否有当前功能操作权限
					boolean flag = userRoleManage.isPermission(ResourceEnum._1001000,entry.getKey());
					entry.setValue(flag);
				}
			}
			
			for(Topic topic : qr.getResultlist()){
				if(topic.getIsStaff() == false){//会员
					User user = userManage.query_cache_findUserByUserName(topic.getUserName());
					if(user != null){
						topic.setAccount(user.getAccount());
						topic.setNickname(user.getNickname());
						topic.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
						topic.setAvatarName(user.getAvatarName());
						
						if(user.getCancelAccountTime() != -1L){//账号已注销
							topic.setUserInfoStatus(-30);
						}
					}
					
				}else{
					topic.setAccount(topic.getUserName());//员工用户名和账号是同一个
				}
				//话题允许查看的角色名称集合
				for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
					if(entry.getKey().equals(topic.getTagId())){
						List<String> roleNameList = entry.getValue();
						if(roleNameList != null && roleNameList.size() >0){
							topic.setAllowRoleViewList(roleNameList);
						}
						break;
					}
					
				}
				//用户角色名称集合
				for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
					if(entry.getKey().equals(topic.getUserName())){
						List<String> roleNameList = entry.getValue();
						if(roleNameList != null && roleNameList.size() >0){
							topic.setUserRoleNameList(roleNameList);
						}
						break;
					}
				}
				
				//用户如果对话题项无查看权限，则不显示摘要、图片、视频
				for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
					if(entry.getKey().equals(topic.getTagId())){
						if(entry.getValue() != null && entry.getValue() == false){
							topic.setImage(null);
							topic.setImageInfoList(new ArrayList<ImageInfo>());
							topic.setSummary("");
							topic.setMediaInfoList(new ArrayList<MediaInfo>());
						}
						break;
					}
					
				}
				
				
			}
			
			
			//非正常状态用户清除显示数据
			for(Topic topic : qr.getResultlist()){
				if(topic.getUserInfoStatus() <0){
					topic.setUserName(null);
					topic.setAccount(null);
					topic.setNickname(null);
					topic.setAvatarPath(null);
					topic.setAvatarName(null);
					topic.setUserRoleNameList(new ArrayList<String>());
					topic.setTitle("");
					topic.setContent("");
					topic.setSummary("");
				}
			}
			
		}
		
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		return pageView;
	}
	
	
	/**
	 * 话题-- 相似话题-- 集合
	 * @param forum
	 */
	public List<Topic> topic_like_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_TopicRelated_LikeTopic forum_TopicRelated_LikeTopic = JsonUtils.toObject(formValueJSON,Forum_TopicRelated_LikeTopic.class);
			if(forum_TopicRelated_LikeTopic != null){
				Integer  likeTopic_maxResult = 10;
				if(forum_TopicRelated_LikeTopic.getLikeTopic_maxResult() != null && forum_TopicRelated_LikeTopic.getLikeTopic_maxResult() >0){
					likeTopic_maxResult = forum_TopicRelated_LikeTopic.getLikeTopic_maxResult();
				}
				Long topicId = null;
				
				if(parameter != null && parameter.size() >0){
					for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
						if("topicId".equals(paramIter.getKey())){
							if(Verification.isNumeric(paramIter.getValue().toString())){
								if(paramIter.getValue().toString().length() <=18){
									topicId = Long.parseLong(paramIter.getValue().toString());	
								}
							}
						}
					}
				}
				
				
				
				if(topicId != null && topicId > 0){
					return topicLuceneManage.findLikeTopic(likeTopic_maxResult,topicId,20);
				}
			}
		}
		return null;
	}
	
	
	
	/**
	 * 话题-- 话题内容 -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Topic content_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		
		Long topicId = null;
		String ip = null;
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(parameter != null && parameter.size() >0){
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("topicId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							topicId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		
		if(runtimeParameter != null && runtimeParameter.size() >0){
			for(Map.Entry<String,Object> runtimeParameIter : runtimeParameter.entrySet()) {
				if("ip".equals(runtimeParameIter.getKey())){
					if(runtimeParameIter.getValue() != null && !"".equals(runtimeParameIter.getValue().toString().trim())){
						ip = runtimeParameIter.getValue().toString().trim();
					}
				}
			}
		}
		if(topicId != null && topicId > 0L){
			Topic topic = topicManage.queryTopicCache(topicId);//查询缓存
			
			if(topic != null){
				//检查权限
				userRoleManage.checkPermission(ResourceEnum._1001000,topic.getTagId());

				
				if(ip != null){
					topicManage.addView(topicId, ip);
				}
				
				if(topic.getStatus() >20){//20:已发布
					return null;
					
				}else{
					if(topic.getStatus().equals(10) ){
						if(accessUser == null){
							return null;
						}else{
							if(!accessUser.getUserName().equals(topic.getUserName())){
								return null;
							}
						}
					}	
				}
				topic.setIpAddress(null);//IP地址不显示
				List<Tag> tagList = tagService.findAllTag_cache();
				if(tagList != null && tagList.size() >0){
					for(Tag tag :tagList){
						if(topic.getTagId().equals(tag.getId())){
							topic.setTagName(tag.getName());
							break;
						}
						
					}
				}
				User user = null;
				if(topic.getIsStaff() == false){//会员
					user = userManage.query_cache_findUserByUserName(topic.getUserName());
					if(user != null){
						topic.setAccount(user.getAccount());
						topic.setNickname(user.getNickname());
						topic.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
						topic.setAvatarName(user.getAvatarName());
						
						List<String> userRoleNameList = userRoleManage.queryUserRoleName(user.getUserName());
						if(userRoleNameList != null && userRoleNameList.size() >0){
							topic.setUserRoleNameList(userRoleNameList);//用户角色名称集合
						}
						if(user.getCancelAccountTime() != -1L){//账号已注销
							topic.setUserInfoStatus(-30);
						}
					}
					
				}else{
					topic.setAccount(topic.getUserName());//员工用户名和账号是同一个
				}
				
				List<String> topicRoleNameList = userRoleManage.queryAllowViewTopicRoleName(topic.getTagId());
				if(topicRoleNameList != null && topicRoleNameList.size() >0){
					topic.setAllowRoleViewList(topicRoleNameList);//话题允许查看的角色名称集合
				}
				
				
				SystemSetting systemSetting = settingService.findSystemSetting_cache();
				
				//话题内容摘要MD5
				String topicContentDigest_link = "";
				String topicContentDigest_video = "";
				if(topic.getContent() != null && !"".equals(topic.getContent().trim())){
					//处理富文本路径
					topic.setContent(fileManage.processRichTextFilePath(topic.getContent(),"topic"));
				}
				
				//处理文件防盗链
				if(topic.getContent() != null && !"".equals(topic.getContent().trim()) && systemSetting.getFileSecureLinkSecret() != null && !"".equals(systemSetting.getFileSecureLinkSecret().trim())){
					List<String> serverAddressList = fileManage.fileServerAllAddress();
					
					
					//解析上传的文件完整路径名称
					Map<String,String> analysisFullFileNameMap = topicManage.query_cache_analysisFullFileName(topic.getContent(),topic.getId(),serverAddressList);
					if(analysisFullFileNameMap != null && analysisFullFileNameMap.size() >0){
						
						
						Map<String,String> newFullFileNameMap = new HashMap<String,String>();//新的完整路径名称 key: 完整路径名称 value: 重定向接口
						for (Map.Entry<String,String> entry : analysisFullFileNameMap.entrySet()) {
							
							newFullFileNameMap.put(entry.getKey(), SecureLink.createDownloadRedirectLink(entry.getKey(),entry.getValue(),topic.getTagId(),systemSetting.getFileSecureLinkSecret()));
						}
						
						Integer topicContentUpdateMark = topicManage.query_cache_markUpdateTopicStatus(topicId, Integer.parseInt(RandomStringUtils.randomNumeric(8)));
						//生成处理'上传的文件完整路径名称'Id
						String processFullFileNameId = topicManage.createProcessFullFileNameId(topicId,topicContentUpdateMark,newFullFileNameMap);
						
						topic.setContent(topicManage.query_cache_processFullFileName(topic.getContent(),"topic",newFullFileNameMap,processFullFileNameId,serverAddressList));
						
						topicContentDigest_link = cms.utils.MD5.getMD5(processFullFileNameId);
					}
					
					
				}
			
				//处理视频播放器标签
				if(topic.getContent() != null && !"".equals(topic.getContent().trim())){
					Integer topicContentUpdateMark = topicManage.query_cache_markUpdateTopicStatus(topicId, Integer.parseInt(RandomStringUtils.randomNumeric(8)));
					
					//生成处理'视频播放器'Id
					String processVideoPlayerId = mediaProcessQueueManage.createProcessVideoPlayerId(topicId,topicContentUpdateMark);
					
					//处理视频播放器标签
					String content = mediaProcessQueueManage.query_cache_processVideoPlayer(topic.getContent(),processVideoPlayerId+"|"+topicContentDigest_link,topic.getTagId(),systemSetting.getFileSecureLinkSecret());
					topic.setContent(content);
					
					topicContentDigest_video = cms.utils.MD5.getMD5(processVideoPlayerId);
				}
				
				//处理隐藏标签
				if(topic.getContent() != null && !"".equals(topic.getContent().trim())){
					List<Integer> visibleTagList = this.getVisibleTagList(accessUser,topic);
					
					
					Integer topicContentUpdateMark = topicManage.query_cache_markUpdateTopicStatus(topicId, Integer.parseInt(RandomStringUtils.randomNumeric(8)));
					
					//生成处理'隐藏标签'Id
					String processHideTagId = topicManage.createProcessHideTagId(topicId,topicContentUpdateMark, visibleTagList);
					
					//处理隐藏标签
					String content = topicManage.query_cache_processHiddenTag(topic.getContent(),visibleTagList,processHideTagId+"|"+topicContentDigest_link+"|"+ topicContentDigest_video);
					
					//String content = textFilterManage.processHiddenTag(topic.getContent(),visibleTagList);
					topic.setContent(content);
					
					
				}
				
				//非正常状态用户清除显示数据
				if(topic.getUserInfoStatus() <0){
					topic.setUserName(null);
					topic.setAccount(null);
					topic.setNickname(null);
					topic.setAvatarPath(null);
					topic.setAvatarName(null);
					topic.setUserRoleNameList(new ArrayList<String>());
					topic.setTitle(null);
					topic.setContent(null);
					topic.setSummary(null);
				}
				
				return topic;
			}
			
		}
		return null;
	}
	
	/**
	 * 允许可见的隐藏标签
	 */
	private List<Integer> getVisibleTagList(AccessUser accessUser,Topic topic){
		//允许可见的隐藏标签
		List<Integer> visibleTagList = new ArrayList<Integer>();
		if(accessUser != null){
			//如果话题由当前用户发表，则显示全部隐藏内容
			if(topic.getIsStaff() == false && topic.getUserName().equals(accessUser.getUserName())){
				visibleTagList.add(HideTagType.PASSWORD.getName());
				visibleTagList.add(HideTagType.COMMENT.getName());
				visibleTagList.add(HideTagType.GRADE.getName());
				visibleTagList.add(HideTagType.POINT.getName());
				visibleTagList.add(HideTagType.AMOUNT.getName());
			}else{
				//解析隐藏标签
				Map<Integer,Object> analysisHiddenTagMap = topicManage.query_cache_analysisHiddenTag(topic.getContent(),topic.getId());
				for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
					
					if(entry.getKey().equals(HideTagType.PASSWORD.getName())){//输入密码可见
						//话题取消隐藏Id
					  	String topicUnhideId = topicManage.createTopicUnhideId(accessUser.getUserId(), HideTagType.PASSWORD.getName(), topic.getId());
					  
						TopicUnhide topicUnhide = topicManage.query_cache_findTopicUnhideById(topicUnhideId);
				  		
				  		if(topicUnhide != null){
				  			visibleTagList.add(HideTagType.PASSWORD.getName());//当前话题已经取消隐藏
					  	}
					}else if(entry.getKey().equals(HideTagType.COMMENT.getName())){//评论话题可见
						Boolean isUnhide = topicManage.query_cache_findWhetherCommentTopic(topic.getId(),accessUser.getUserName());
						if(isUnhide){
							visibleTagList.add(HideTagType.COMMENT.getName());//当前话题已经取消隐藏
						}
					}else if(entry.getKey().equals(HideTagType.GRADE.getName())){//超过等级可见
						User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
						if(_user != null){
							List<UserGrade> userGradeList = userGradeService.findAllGrade_cache();//取得用户所有等级
							if(userGradeList != null && userGradeList.size() >0){
								for(UserGrade userGrade : userGradeList){
									if(_user.getPoint() >= userGrade.getNeedPoint() && (Long)entry.getValue() <=userGrade.getNeedPoint()){
										visibleTagList.add(HideTagType.GRADE.getName());//当前话题已经取消隐藏
										
										break;
									}
								} 
									
								
							}
						}
						
					}else if(entry.getKey().equals(HideTagType.POINT.getName())){//积分购买可见
						//话题取消隐藏Id
					  	String topicUnhideId = topicManage.createTopicUnhideId(accessUser.getUserId(), HideTagType.POINT.getName(), topic.getId());
					  
						TopicUnhide topicUnhide = topicManage.query_cache_findTopicUnhideById(topicUnhideId);
				  		
				  		if(topicUnhide != null){
				  			visibleTagList.add(HideTagType.POINT.getName());//当前话题已经取消隐藏
					  	}
					}else if(entry.getKey().equals(HideTagType.AMOUNT.getName())){//余额购买可见
						//话题取消隐藏Id
					  	String topicUnhideId = topicManage.createTopicUnhideId(accessUser.getUserId(), HideTagType.AMOUNT.getName(), topic.getId());
					  	TopicUnhide topicUnhide = topicManage.query_cache_findTopicUnhideById(topicUnhideId);
				  		
				  		if(topicUnhide != null){
				  			visibleTagList.add(HideTagType.AMOUNT.getName());//当前话题已经取消隐藏
					  	}
					}
					
				}
			}
		}
		return visibleTagList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 话题  -- 取消隐藏
	 * @param forum
	 */
	public Map<String,Object> topicUnhide_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		

		return value;
	}
	
	/**
	 * 话题  -- 添加
	 * @param forum
	 */
	public Map<String,Object> addTopic_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null){
			boolean captchaKey = captchaManage.topic_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
			
			User user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
			if(user != null){
				value.put("deposit",user.getDeposit());//用户共有预存款
			}
		}
		
		
		
		
		
		
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果全局不允许提交话题
		if(systemSetting.isAllowTopic() == false){
			value.put("allowTopic",false);//不允许提交话题
		}else{
			value.put("allowTopic",true);//允许提交话题
		}
		
		value.put("giveRedEnvelopeAmountMin",systemSetting.getGiveRedEnvelopeAmountMin());
		value.put("giveRedEnvelopeAmountMax",systemSetting.getGiveRedEnvelopeAmountMax());
		
		value.put("availableTag", topicManage.availableTag());//话题编辑器允许使用标签
		List<UserGrade> userGradeList = userGradeService.findAllGrade_cache();
		value.put("userGradeList", JsonUtils.toJSONString(userGradeList));
		
		value.put("fileSystem", fileManage.getFileSystem());
		return value;
	}
	
	/**
	 * 话题  -- 修改
	 * @param forum
	 */
	public Map<String,Object> editTopic_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		Long topicId = null;
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null){
			boolean captchaKey = captchaManage.topic_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("topicId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							topicId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		
		if(accessUser != null && topicId != null && topicId >0L){
			Topic topic = topicManage.queryTopicCache(topicId);//查询缓存
			if(topic != null && topic.getStatus() <100 && topic.getUserName().equals(accessUser.getUserName())){
				topic.setIpAddress(null);//IP地址不显示
				
				if(topic.getContent() != null && !"".equals(topic.getContent().trim())){
					//处理富文本路径
					topic.setContent(fileManage.processRichTextFilePath(topic.getContent(),"topic"));
				}
				
				List<Tag> tagList = tagService.findAllTag_cache();
				if(tagList != null && tagList.size() >0){
					for(Tag tag :tagList){
						if(topic.getTagId().equals(tag.getId())){
							topic.setTagName(tag.getName());
							break;
						}
						
					}
				}
				
				List<String> topicRoleNameList = userRoleManage.queryAllowViewTopicRoleName(topic.getTagId());
				if(topicRoleNameList != null && topicRoleNameList.size() >0){
					topic.setAllowRoleViewList(topicRoleNameList);//话题允许查看的角色名称集合
				}
				
				
				value.put("topic",topic);
				
				
			}
		}
		

		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果全局不允许提交话题
		if(systemSetting.isAllowTopic() == false){
			value.put("allowTopic",false);//不允许提交话题
		}else{
			value.put("allowTopic",true);//允许提交话题
		}
		value.put("availableTag", topicManage.availableTag());//话题编辑器允许使用标签
		List<UserGrade> userGradeList = userGradeService.findAllGrade_cache();
		value.put("userGradeList", JsonUtils.toJSONString(userGradeList));
		value.put("fileSystem", fileManage.getFileSystem());
		return value;
	}
	
	/**
	 * 评论列表  -- 集合
	 * @param forum
	 */
	public PageView<Comment> comment_page(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_CommentRelated_Comment forum_CommentRelated_Comment = JsonUtils.toObject(formValueJSON,Forum_CommentRelated_Comment.class);
			if(forum_CommentRelated_Comment != null){
				int maxResult = settingService.findSystemSetting_cache().getForestagePageNumber();
				//每页显示记录数
				if(forum_CommentRelated_Comment.getComment_maxResult() != null && forum_CommentRelated_Comment.getComment_maxResult() >0){
					maxResult = forum_CommentRelated_Comment.getComment_maxResult();
				}
				
				return this.comment_SQL_Page(forum_CommentRelated_Comment,parameter,runtimeParameter,maxResult);

			}
			
		}
		return null;
	}
	
	
	/**
	 * 评论SQL分页
	 * @param maxResult 每页显示记录数
	 */
	private PageView<Comment> comment_SQL_Page(Forum_CommentRelated_Comment forum_CommentRelated_Comment,Map<String,Object> parameter,Map<String,Object> runtimeParameter,int maxResult){
		Integer page = null;//分页 当前页
		int pageCount=10;// 页码显示总数
		int sort = 1;//排序
		Long topicId = null;//话题Id
		Long commentId = null;//评论Id
		
		//排序
		if(forum_CommentRelated_Comment.getComment_sort() != null && forum_CommentRelated_Comment.getComment_sort() >0){
			sort = forum_CommentRelated_Comment.getComment_sort();
		}
		
		//页码显示总数
		if(forum_CommentRelated_Comment.getComment_pageCount() != null && forum_CommentRelated_Comment.getComment_pageCount() >0){
			pageCount = forum_CommentRelated_Comment.getComment_pageCount();
		}
		
		
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("page".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=9){
							page = Integer.parseInt(paramIter.getValue().toString());
						}
					}
				}else if("topicId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							topicId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
					
				}else if("commentId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							commentId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
					
				}
			}
		}
		String requestURI = "";
		String queryString = "";
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("requestURI".equals(paramIter.getKey())){
					requestURI = (String)paramIter.getValue();
				}else if("queryString".equals(paramIter.getKey())){
					queryString = (String)paramIter.getValue();
				}
			}
		}
		
		
		
		PageForm pageForm = new PageForm();
		pageForm.setPage(page);
		
		if(commentId != null && commentId >0L && page == null){
			Integer row_sort = 1;
			if(sort == 1){
				row_sort = 1;
			}else if(sort == 2){
				row_sort = 2;
			}
			
			
			Long row = commentService.findRowByCommentId(commentId,topicId,20,row_sort);
			if(row != null && row >0L){
				
				
				Integer _page = Integer.parseInt(String.valueOf(row))/maxResult;
				if(Integer.parseInt(String.valueOf(row))%maxResult >0){//余数大于0要加1
					
					_page = _page+1;
				}
				
				
				pageForm.setPage(_page);
				
			}
		}
		
		
		
		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		PageView<Comment> pageView = new PageView<Comment>(maxResult,pageForm.getPage(),pageCount,requestURI,queryString);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

		if(topicId != null && topicId >0L){
			jpql.append(" o.topicId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
			params.add(topicId);//设置o.parentId=?2参数
		}
		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(20);
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
				
		
		//排行依据
		if(sort == 1){
			orderby.put("postTime", "desc");//发布时间排序   新-->旧
		}else if(sort == 2){
			orderby.put("postTime", "asc");//发布时间排序  旧-->新
		}
		//根据sort字段降序排序
		QueryResult<Comment> qr = commentService.getScrollData(Comment.class,firstIndex, pageView.getMaxresult(),jpql_str,params.toArray(),orderby);
		
		
		List<Long> commentIdList = new ArrayList<Long>();
		List<Comment> commentList = qr.getResultlist();
		
		//引用修改Id集合
		List<Long> quoteUpdateIdList = new ArrayList<Long>();
		//重新查询Id
		List<Long> query_quoteUpdateIdList = new ArrayList<Long>();
		//新引用集合
		Map<Long,String> new_quoteList = new HashMap<Long,String>();//key:自定义评论Id value:自定义评论内容(文本)
		
		Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id 角色名称集合
		if(commentList != null && commentList.size() >0){
			for(Comment comment : commentList){
				if(comment.getContent() != null && !"".equals(comment.getContent().trim())){
					//处理富文本路径
					comment.setContent(fileManage.processRichTextFilePath(comment.getContent(),"comment"));
				}
				comment.setIpAddress(null);//IP地址不显示
				if(comment.getIsStaff() == false){//会员
					User user = userManage.query_cache_findUserByUserName(comment.getUserName());
					if(user != null){
						comment.setAccount(user.getAccount());
						comment.setNickname(user.getNickname());
						comment.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
						comment.setAvatarName(user.getAvatarName());
						userRoleNameMap.put(comment.getUserName(), null);
						
						if(user.getCancelAccountTime() != -1L){//账号已注销
							comment.setUserInfoStatus(-30);
						}
					}
					
				}else{
					comment.setAccount(comment.getUserName());//员工用户名和账号是同一个
				}
				
				if(comment.getQuoteUpdateId() != null && comment.getQuoteUpdateId().length() >1){
					String[] quoteUpdateId_arr = comment.getQuoteUpdateId().split(",");
					if(quoteUpdateId_arr != null && quoteUpdateId_arr.length >0){
						for(String quoteUpdateId : quoteUpdateId_arr){
							if(quoteUpdateId != null && !"".equals(quoteUpdateId.trim())){
								Long l = Long.parseLong(quoteUpdateId);
								if(!quoteUpdateIdList.contains(l)){
									quoteUpdateIdList.add(l);
								}
							}
						}
					}
				}
			}
			
			A:for(Long quoteUpdateId : quoteUpdateIdList){
				for(Comment comment : commentList){
					if(comment.getId().equals(quoteUpdateId)){
						new_quoteList.put(comment.getId(), textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(comment.getContent())));
						continue A;
					}
				}
				query_quoteUpdateIdList.add(quoteUpdateId);
			}
		}
		
		if(userRoleNameMap != null && userRoleNameMap.size() >0){
			for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
				List<String> roleNameList = userRoleManage.queryUserRoleName(entry.getKey());
				entry.setValue(roleNameList);
			}
		}
		
		
		if(query_quoteUpdateIdList != null && query_quoteUpdateIdList.size() >0){
			List<Comment> quote_commentList = commentService.findByCommentIdList(query_quoteUpdateIdList);
			if(quote_commentList != null && quote_commentList.size() >0){
				for(Comment comment : quote_commentList){
					new_quoteList.put(comment.getId(), textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(comment.getContent())));
				}
			}
		}
		
		
		
		
		if(commentList != null && commentList.size() >0){
			for(Comment comment : commentList){
				commentIdList.add(comment.getId());
				if(comment.getQuote() != null && !"".equals(comment.getQuote().trim())){
					//旧引用
					List<Quote> quoteList = JsonUtils.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
					if(quoteList != null && quoteList.size() >0){
						for(Quote quote :quoteList){
							if(new_quoteList.containsKey(quote.getCommentId())){
								quote.setContent(new_quoteList.get(quote.getCommentId()));
							}
							if(quote.getIsStaff() == false){//会员
								User user = userManage.query_cache_findUserByUserName(quote.getUserName());
								quote.setAccount(user.getAccount());
								quote.setNickname(user.getNickname());
								quote.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
								quote.setAvatarName(user.getAvatarName());

								if(user.getCancelAccountTime() != -1L){//账号已注销
									quote.setUserInfoStatus(-30);
								}
							}else{
								quote.setAccount(quote.getUserName());//员工用户名和账号是同一个
							}
							
							//非正常状态用户清除显示数据
							if(quote.getUserInfoStatus() <0){
								quote.setUserName(null);
								quote.setAccount(null);
								quote.setNickname(null);
								quote.setAvatarPath(null);
								quote.setAvatarName(null);
								quote.setUserRoleNameList(new ArrayList<String>());
								quote.setContent("");
							}
						}
					}
					comment.setQuoteList(quoteList);
				}
				
				//用户角色名称集合
				for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
					if(entry.getKey().equals(comment.getUserName())){
						List<String> roleNameList = entry.getValue();
						if(roleNameList != null && roleNameList.size() >0){
							comment.setUserRoleNameList(roleNameList);
						}
						break;
					}
				}
				
				//非正常状态用户清除显示数据
				if(comment.getUserInfoStatus() <0){
					comment.setUserName(null);
					comment.setAccount(null);
					comment.setNickname(null);
					comment.setAvatarPath(null);
					comment.setAvatarName(null);
					comment.setUserRoleNameList(new ArrayList<String>());
					comment.setContent("");
				}
			}
		}
		
		if(commentIdList != null && commentIdList.size() >0){
			List<Reply> replyList = commentService.findReplyByCommentId(commentIdList,20);
			if(replyList != null && replyList.size() >0){
				for(Comment comment : commentList){
					for(Reply reply : replyList){
						if(comment.getId().equals(reply.getCommentId())){
							reply.setIpAddress(null);//IP地址不显示
							if(reply.getIsStaff() == false){//会员
								User user = userManage.query_cache_findUserByUserName(reply.getUserName());
								if(user != null){
									reply.setAccount(user.getAccount());
									reply.setNickname(user.getNickname());
									reply.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
									reply.setAvatarName(user.getAvatarName());
									
									List<String> roleNameList = userRoleManage.queryUserRoleName(reply.getUserName());
									reply.setUserRoleNameList(roleNameList);
									
									if(user.getCancelAccountTime() != -1L){//账号已注销
										reply.setUserInfoStatus(-30);
									}
								}
								
							}else{
								reply.setAccount(reply.getUserName());//员工用户名和账号是同一个
							}
							
							//非正常状态用户清除显示数据
							if(reply.getUserInfoStatus() <0){
								reply.setUserName(null);
								reply.setAccount(null);
								reply.setNickname(null);
								reply.setAvatarPath(null);
								reply.setAvatarName(null);
								reply.setUserRoleNameList(new ArrayList<String>());
								reply.setContent(null);
							}
							
							comment.addReply(reply);
						}
					}
					
				}
			}
		}
		
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		return pageView;
	}
	
	/**
	 * 评论  -- 添加
	 * @param forum
	 */
	public Map<String,Object> addComment_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null){
			boolean captchaKey = captchaManage.comment_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		//如果全局不允许提交评论
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.isAllowComment()){
			value.put("allowComment",true);//允许提交评论
		}else{
			value.put("allowComment",false);//不允许提交评论
		}
		
		value.put("availableTag", commentManage.availableTag());//评论编辑器允许使用标签
		value.put("fileSystem", fileManage.getFileSystem());
		return value;
	}
	
	
	/**
	 * 评论  -- 引用添加
	 * @param forum
	 */
	public Map<String,Object> quoteComment_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		Long commentId = null;
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null){
			boolean captchaKey = captchaManage.comment_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey == true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("commentId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							commentId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		
		if(commentId != null && commentId >0L){
			Comment comment = commentService.findByCommentId(commentId);
			if(comment != null && comment.getStatus() <100){
				value.put("quoteContent", textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(comment.getContent())));//引用内容
			}
		}
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		
		if(systemSetting.isAllowComment()){
			value.put("allowComment",true);//允许提交评论
		}else{
			value.put("allowComment",false);//不允许提交评论
		}
		value.put("availableTag", commentManage.availableTag());//评论编辑器允许使用标签
		value.put("fileSystem", fileManage.getFileSystem());
		return value;
	}
	
	/**
	 * 评论  -- 修改
	 * @param forum
	 */
	public Map<String,Object> editComment_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		Long commentId = null;
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null){
			boolean captchaKey = captchaManage.comment_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("commentId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							commentId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		
		if(accessUser != null && commentId != null && commentId >0L){
			Comment comment = commentManage.query_cache_findByCommentId(commentId);//查询缓存
			if(comment != null && comment.getStatus() <100 && comment.getUserName().equals(accessUser.getUserName())){
				comment.setIpAddress(null);//IP地址不显示
				if(comment.getContent() != null && !"".equals(comment.getContent().trim())){
					//处理富文本路径
					comment.setContent(fileManage.processRichTextFilePath(comment.getContent(),"comment"));
				}
				
				value.put("comment",comment);
				
				
			}
		}
		

		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果全局不允许提交评论
		if(systemSetting.isAllowComment() == false){
			value.put("allowComment",false);//不允许提交评论
		}else{
			value.put("allowComment",true);//允许提交评论
		}
		value.put("availableTag", commentManage.availableTag());//评论编辑器允许使用标签
		value.put("fileSystem", fileManage.getFileSystem());
		return value;
	}
	
	
	/**
	 * 评论  -- 回复添加
	 * @param forum
	 */
	public Map<String,Object> replyComment_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		
		if(accessUser != null){
			boolean captchaKey = captchaManage.comment_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//验证码key
			}
		}
		//如果全局不允许提交评论
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.isAllowComment()){
			value.put("allowReply",true);//允许提交回复
		}else{
			value.put("allowReply",false);//不允许提交回复
		}
		return value;
	}
	
	/**
	 * 评论  -- 回复修改
	 * @param forum
	 */
	public Map<String,Object> editCommentReply_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		Long replyId = null;
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		
		if(accessUser != null){
			boolean captchaKey = captchaManage.comment_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//验证码key
			}
		}
		
		
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("replyId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							replyId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		
		if(accessUser != null && replyId != null && replyId >0L){
			Reply reply = commentManage.query_cache_findReplyByReplyId(replyId);//查询缓存
			if(reply != null && reply.getStatus() <100 && reply.getUserName().equals(accessUser.getUserName())){
				reply.setIpAddress(null);//IP地址不显示
	
				value.put("reply",reply);
				
				
			}
		}
				
		
		
		//如果全局不允许提交评论
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.isAllowComment()){
			value.put("allowReply",true);//允许提交回复
		}else{
			value.put("allowReply",false);//不允许提交回复
		}
		return value;
	}
}
