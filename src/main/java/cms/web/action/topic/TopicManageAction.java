package cms.web.action.topic;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.mediaProcess.MediaProcessQueue;
import cms.bean.payment.PaymentLog;
import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.setting.SystemSetting;
import cms.bean.staff.SysUsers;
import cms.bean.thumbnail.Thumbnail;
import cms.bean.topic.Comment;
import cms.bean.topic.HideTagType;
import cms.bean.topic.ImageInfo;
import cms.bean.topic.Quote;
import cms.bean.topic.Reply;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.bean.topic.TopicIndex;
import cms.bean.user.User;
import cms.bean.user.UserGrade;
import cms.service.mediaProcess.MediaProcessService;
import cms.service.redEnvelope.RedEnvelopeService;
import cms.service.setting.SettingService;
import cms.service.thumbnail.ThumbnailService;
import cms.service.topic.CommentService;
import cms.service.topic.TagService;
import cms.service.topic.TopicIndexService;
import cms.service.topic.TopicService;
import cms.service.user.UserGradeService;
import cms.service.user.UserService;
import cms.utils.CommentedProperties;
import cms.utils.FileType;
import cms.utils.FileUtil;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.SecureLink;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.mediaProcess.MediaProcessQueueManage;
import cms.web.action.payment.PaymentManage;
import cms.web.action.redEnvelope.RedEnvelopeManage;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;


/**
 * 话题管理
 *
 */
@Controller
@RequestMapping("/control/topic/manage") 
public class TopicManageAction {
	 
	@Resource TopicService topicService; 
	@Resource TextFilterManage textFilterManage;
	@Resource SettingService settingService;
	@Resource TagService tagService;
	@Resource CommentManage commentManage;
	@Resource CommentService commentService;
	@Resource TopicManage topicManage;
	@Resource FileManage fileManage;
	@Resource TopicIndexService topicIndexService;
	
	@Resource UserGradeService userGradeService;
	
	@Resource ThumbnailService thumbnailService;
	
	@Resource UserManage userManage;
	@Resource UserService userService;
	@Resource UserRoleManage userRoleManage;
	
	@Resource MediaProcessService mediaProcessService;
	@Resource MediaProcessQueueManage mediaProcessQueueManage;
	@Resource RedEnvelopeService redEnvelopeService;
	@Resource PaymentManage paymentManage;
	@Resource RedEnvelopeManage redEnvelopeManage;
	/**
	 * 话题   查看
	 * @param topicId
	 * @param model
	 * @param pageForm
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=view",method=RequestMethod.GET)
	public String view(Long topicId,Long commentId,ModelMap model,Integer page,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
		
		
		
		if(topicId != null && topicId >0L){
			Topic topic = topicService.findById(topicId);
			if(topic != null){
				if(topic.getIp() != null && !"".equals(topic.getIp().trim())){
					topic.setIpAddress(IpAddress.queryAddress(topic.getIp().trim()));
				}
				if(topic.getContent() != null && !"".equals(topic.getContent().trim())){
					//处理富文本路径
					topic.setContent(fileManage.processRichTextFilePath(topic.getContent(),"topic"));
				}
				
				SystemSetting systemSetting = settingService.findSystemSetting_cache();
				if(topic.getContent() != null && !"".equals(topic.getContent().trim()) && systemSetting.getFileSecureLinkSecret() != null && !"".equals(systemSetting.getFileSecureLinkSecret().trim())){
					List<String> serverAddressList = fileManage.fileServerAllAddress();
					//解析上传的文件完整路径名称
					Map<String,String> analysisFullFileNameMap = textFilterManage.analysisFullFileName(topic.getContent(),"topic",serverAddressList);
					if(analysisFullFileNameMap != null && analysisFullFileNameMap.size() >0){
						
						
						Map<String,String> newFullFileNameMap = new HashMap<String,String>();//新的完整路径名称 key: 完整路径名称 value: 重定向接口
						for (Map.Entry<String,String> entry : analysisFullFileNameMap.entrySet()) {

							newFullFileNameMap.put(entry.getKey(), SecureLink.createDownloadRedirectLink(entry.getKey(),entry.getValue(),-1L,systemSetting.getFileSecureLinkSecret()));
						}
						
						topic.setContent(textFilterManage.processFullFileName(topic.getContent(),"topic",newFullFileNameMap,serverAddressList));
						
					}
					
					
					
						
						
				}
				if(topic.getContent() != null && !"".equals(topic.getContent().trim())){
					//处理视频播放器标签
					String content = textFilterManage.processVideoPlayer(topic.getContent(),-1L,systemSetting.getFileSecureLinkSecret());
					topic.setContent(content);
				}
				
				if(topic.getIsStaff() == false){//会员
					User user = userManage.query_cache_findUserByUserName(topic.getUserName());
					if(user != null){
						topic.setNickname(user.getNickname());
						topic.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
						topic.setAvatarName(user.getAvatarName());
						
						List<String> userRoleNameList = userRoleManage.queryUserRoleName(user.getUserName());
						if(userRoleNameList != null && userRoleNameList.size() >0){
							topic.setUserRoleNameList(userRoleNameList);//用户角色名称集合
						}
					}
					
				}
				
				List<String> topic_roleNameList = userRoleManage.queryAllowViewTopicRoleName(topic.getTagId());
				if(topic_roleNameList != null && topic_roleNameList.size() >0){
					topic.setAllowRoleViewList(topic_roleNameList);
				}
				
				Tag tag = tagService.findById(topic.getTagId());
				if(tag != null){
					topic.setTagName(tag.getName());
				}
				
				if(topic.getGiveRedEnvelopeId() != null && !"".equals(topic.getGiveRedEnvelopeId())){//红包
					GiveRedEnvelope giveRedEnvelope = redEnvelopeService.findById(topic.getGiveRedEnvelopeId());
					returnValue.put("giveRedEnvelope", giveRedEnvelope);
					
				}
				
				
				returnValue.put("topic", topic);
				
				returnValue.put("availableTag", commentManage.availableTag());
				
				PageForm pageForm = new PageForm();
				pageForm.setPage(page);
				
				if(commentId != null && commentId >0L && page == null){
					Long row = commentService.findRowByCommentId(commentId,topicId);
					if(row != null && row >0L){
							
						Integer _page = Integer.parseInt(String.valueOf(row))/settingService.findSystemSetting_cache().getBackstagePageNumber();
						if(Integer.parseInt(String.valueOf(row))%settingService.findSystemSetting_cache().getBackstagePageNumber() >0){//余数大于0要加1
							_page = _page+1;
						}
						pageForm.setPage(_page);
					}
				}
				
				
				
				
				
				//评论
				StringBuffer jpql = new StringBuffer("");
				//存放参数值
				List<Object> params = new ArrayList<Object>();
				PageView<Comment> pageView = new PageView<Comment>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
				
				
				
				
				//当前页
				int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
				//排序
				LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

				if(topicId != null && topicId >0L){
					jpql.append(" o.topicId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
					params.add(topicId);//设置o.parentId=?2参数
				}

				orderby.put("postTime", "asc");//根据sort字段降序排序
				QueryResult<Comment> qr = commentService.getScrollData(Comment.class,firstindex, pageView.getMaxresult(),jpql.toString(),params.toArray(),orderby);
				
				
				List<Long> commentIdList = new ArrayList<Long>();
				List<Comment> commentList = qr.getResultlist();
				
				//引用修改Id集合
				List<Long> quoteUpdateIdList = new ArrayList<Long>();
				//重新查询Id
				List<Long> query_quoteUpdateIdList = new ArrayList<Long>();
				//新引用集合
				Map<Long,String> new_quoteList = new HashMap<Long,String>();//key:自定义评论Id value:自定义评论内容(文本)
				
				if(commentList != null && commentList.size() >0){
					for(Comment comment : commentList){
						if(comment.getContent() != null && !"".equals(comment.getContent().trim())){
							//处理富文本路径
							comment.setContent(fileManage.processRichTextFilePath(comment.getContent(),"comment"));
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
						
						
						if(comment.getIp() != null && !"".equals(comment.getIp().trim())){
							comment.setIpAddress(IpAddress.queryAddress(comment.getIp()));
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
				
				
				
				
				if(query_quoteUpdateIdList != null && query_quoteUpdateIdList.size() >0){
					List<Comment> quote_commentList = commentService.findByCommentIdList(query_quoteUpdateIdList);
					if(quote_commentList != null && quote_commentList.size() >0){
						for(Comment comment : quote_commentList){
							new_quoteList.put(comment.getId(), textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(comment.getContent())));
						}
					}
				}

				Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id 角色名称集合
				if(commentList != null && commentList.size() >0){
					for(Comment comment : commentList){
						if(comment.getIsStaff() == false){//会员
							User user = userManage.query_cache_findUserByUserName(comment.getUserName());
							if(user != null){
								comment.setNickname(user.getNickname());
								comment.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
								comment.setAvatarName(user.getAvatarName());
								userRoleNameMap.put(comment.getUserName(), null);
							}
							
						}
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
										if(user != null){
											quote.setNickname(user.getNickname());
											quote.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
											quote.setAvatarName(user.getAvatarName());
											userRoleNameMap.put(quote.getUserName(), null);
										}
										
									}
									
									
								}
							}
							comment.setQuoteList(quoteList);
						}
						
						
					}
				}
				
				
				
				
				if(commentIdList != null && commentIdList.size() >0){
					List<Reply> replyList = commentService.findReplyByCommentId(commentIdList);
					if(replyList != null && replyList.size() >0){
						for(Comment comment : commentList){
							for(Reply reply : replyList){
								if(reply.getIsStaff() == false){//会员
									User user = userManage.query_cache_findUserByUserName(reply.getUserName());
									if(user != null){
										reply.setNickname(user.getNickname());
										reply.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
										reply.setAvatarName(user.getAvatarName());
										userRoleNameMap.put(reply.getUserName(), null);
									}
									
								}
								
								if(comment.getId().equals(reply.getCommentId())){
									comment.addReply(reply);
								}
							}
							
						}
					}
				}
				
				if(userRoleNameMap != null && userRoleNameMap.size() >0){
					for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
						List<String> roleNameList = userRoleManage.queryUserRoleName(entry.getKey());
						entry.setValue(roleNameList);
					}
				}
				
				if(commentList != null && commentList.size() >0){
					for(Comment comment : commentList){
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
						if(comment.getReplyList() != null && comment.getReplyList().size() >0){
							for(Reply reply : comment.getReplyList()){
								for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
									if(entry.getKey().equals(reply.getUserName())){
										List<String> roleNameList = entry.getValue();
										if(roleNameList != null && roleNameList.size() >0){
											reply.setUserRoleNameList(roleNameList);
										}
										break;
									}
								}
							}
						}
						if(comment.getQuoteList() != null && comment.getQuoteList().size() >0){
							for(Quote quote : comment.getQuoteList()){
								//用户角色名称集合
								for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
									if(entry.getKey().equals(quote.getUserName())){
										List<String> roleNameList = entry.getValue();
										if(roleNameList != null && roleNameList.size() >0){
											quote.setUserRoleNameList(roleNameList);
										}
										break;
									}
								}
								
								
							}
							
						}
					}
				}
				
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
				returnValue.put("pageView", pageView);
				
				
				String username = "";//用户名称
				
				Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
				if(obj instanceof UserDetails){
					username =((UserDetails)obj).getUsername();	
				}
				returnValue.put("userName", username);
				
				
				
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
			}else{
				error.put("topicId", "话题不存在");
			}
		}else{
			error.put("topicId", "话题Id参数不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 话题   添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Topic topic,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		String username = "";//用户名称
		
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof UserDetails){
			username =((UserDetails)obj).getUsername();	
		}
		returnValue.put("userName", username);
		List<UserGrade> userGradeList = userGradeService.findAllGrade();
		returnValue.put("userGradeList", userGradeList);

		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	
	/**
	 * 话题  添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Long tagId,String tagName, String title,Boolean allow,Integer status,
			String content,String sort,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		Topic topic = new Topic();
		List<String> imageNameList = null;
		boolean isImage = false;//是否含有图片
		List<String> flashNameList = null;
		boolean isFlash = false;//是否含有Flash
		List<String> mediaNameList = null;
		boolean isMedia = false;//是否含有音视频
		List<String> fileNameList = null;
		boolean isFile = false;//是否含有文件
		boolean isMap = false;//是否含有地图
		Map<String,String> error = new HashMap<String,String>();
		
		String username = "";//用户名称
		String userId = "";//用户Id
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof SysUsers){
			userId =((SysUsers)obj).getUserId();
			username =((SysUsers)obj).getUserAccount();
		}
		
		List<UserGrade> userGradeList = userGradeService.findAllGrade();
		
		//前3张图片地址
		List<ImageInfo> beforeImageList = new ArrayList<ImageInfo>();
		
		topic.setTagId(tagId);
		topic.setTagName(tagName);
		topic.setAllow(allow);
		topic.setStatus(status);
		Date d = new Date();
		topic.setPostTime(d);
		topic.setLastReplyTime(d);
		if(tagId == null || tagId <=0L){
			error.put("tagId", "标签不能为空");
		}else{
			
			if(title != null && !"".equals(title.trim())){
				topic.setTitle(title);
				if(title.length() >150){
					error.put("title", "不能大于150个字符");
				}
			}else{
				error.put("title", "标题不能为空");
			}
			
			if(content != null && !"".equals(content.trim())){
				//过滤标签
				content = textFilterManage.filterTag(request,content);
				Object[] object = textFilterManage.filterHtml(request,content,"topic",null);
				String value = (String)object[0];
				imageNameList = (List<String>)object[1];
				isImage = (Boolean)object[2];//是否含有图片
				flashNameList = (List<String>)object[3];
				isFlash = (Boolean)object[4];//是否含有Flash
				mediaNameList = (List<String>)object[5];
				isMedia = (Boolean)object[6];//是否含有音视频
				fileNameList = (List<String>)object[7];
				isFile = (Boolean)object[8];//是否含有文件
				isMap = (Boolean)object[9];//是否含有地图
				
				
				//校正隐藏标签
				String validValue =  textFilterManage.correctionHiddenTag(value,userGradeList);
				
				//解析隐藏标签
				Map<Integer,Object> analysisHiddenTagMap = textFilterManage.analysisHiddenTag(validValue);
				for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
					//管理员账户不能发表'余额购买'话题
					if(entry.getKey().equals(HideTagType.AMOUNT.getName())){
						error.put("content", "管理员账户不能发表'余额购买'话题");
						break;
					}
				}
				
				
				
				//删除隐藏标签
				String new_content = textFilterManage.deleteHiddenTag(value);

				//不含标签内容
				String text = textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(new_content));
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				//摘要
				if(trimSpace != null && !"".equals(trimSpace)){
					if(trimSpace.length() >180){
						topic.setSummary(trimSpace.substring(0, 180)+"..");
					}else{
						topic.setSummary(trimSpace+"..");
					}
				}
				
				//不含标签内容
				String source_text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();
				
				if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(source_text.trim()) && !"".equals(source_trimSpace))){
					
					topic.setIp(IpAddress.getClientIpAddress(request));
					topic.setUserName(username);
					topic.setIsStaff(true);
					topic.setContent(validValue);
				}else{
					error.put("content", "话题内容不能为空");
				}	
				
						
				
				//非隐藏标签内图片
				List<String> other_imageNameList = textFilterManage.readImageName(new_content,"topic");
				
				if(other_imageNameList != null && other_imageNameList.size() >0){
					for(int i=0; i<other_imageNameList.size(); i++){
						ImageInfo imageInfo = new ImageInfo();
						imageInfo.setName(FileUtil.getName(other_imageNameList.get(i)));
						imageInfo.setPath(FileUtil.getFullPath(other_imageNameList.get(i)));
						
						beforeImageList.add(imageInfo);
						
					}
					topic.setImage(JsonUtils.toJSONString(beforeImageList));
					
				}
				
			}else{
				error.put("content", "话题内容不能为空");
			}
		}
		if(sort != null){
			if(Verification.isNumeric(sort.toString())){
				if(sort.toString().length() <=8){
					topic.setSort(Integer.parseInt(sort.toString()));
				}else{
					error.put("sort", "不能超过8位数字");
				}
			}else{
				error.put("sort", "请填写整数");
			}
		}else{
			error.put("sort", "排序不能为空");
		}
		
		if(error.size() ==0){
			topicService.saveTopic(topic,null,null,null,null);
			topicManage.delete_cache_markUpdateTopicStatus(topic.getId());//删除 标记修改话题状态
			//更新索引
			topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),1));
			
			if(isMedia){
				List<MediaProcessQueue> mediaProcessQueueList = new ArrayList<MediaProcessQueue>();
				for(String fullPathName :mediaNameList){
					//取得路径名称
					String pathName = FileUtil.getFullPath(fullPathName);
					//文件名称
					String fileName = FileUtil.getName(fullPathName);
					
					MediaProcessQueue mediaProcessQueue = new MediaProcessQueue();
					mediaProcessQueue.setModule(10);//10:话题
					mediaProcessQueue.setType(10);//10:视频
					mediaProcessQueue.setParameter(String.valueOf(topic.getId()));
					mediaProcessQueue.setPostTime(topic.getPostTime());
					mediaProcessQueue.setFilePath("file/topic/"+pathName);
					mediaProcessQueue.setFileName(fileName);
					mediaProcessQueueList.add(mediaProcessQueue);
				}
				mediaProcessService.saveMediaProcessQueueList(mediaProcessQueueList);
			}
			
			
			//上传文件编号
			String fileNumber = "a"+userId;
			
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
			
					 if(imageName != null && !"".equals(imageName.trim())){
		 
						 //如果验证不是当前用户上传的文件，则不删除锁
						 if(!topicManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
							 continue;
						 }
						 
						 fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
					
					 }
				}
			}
			//falsh
			if(flashNameList != null && flashNameList.size() >0){
				for(String flashName :flashNameList){
					 if(flashName != null && !"".equals(flashName.trim())){
						 //如果验证不是当前用户上传的文件，则不删除锁
						 if(!topicManage.getFileNumber(FileUtil.getBaseName(flashName.trim())).equals(fileNumber)){
							 continue;
						 }
						 
						 fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
					
					 }
				}
			}
			//音视频
			if(mediaNameList != null && mediaNameList.size() >0){
				for(String mediaName :mediaNameList){
					if(mediaName != null && !"".equals(mediaName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!topicManage.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
					
					}
				}
			}
			//文件
			if(fileNameList != null && fileNameList.size() >0){
				for(String fileName :fileNameList){
					if(fileName != null && !"".equals(fileName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!topicManage.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
					
					}
				}
			}
			
			List<Thumbnail> thumbnailList = thumbnailService.findAllThumbnail_cache();
			if(thumbnailList != null && thumbnailList.size() >0){
				//异步增加缩略图
				if(beforeImageList != null && beforeImageList.size() >0){
					fileManage.addThumbnail(thumbnailList,beforeImageList);
				}
			}
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 *  文件上传
	 * 
	 * 员工发话题 上传文件名为UUID + a + 员工Id
	 * 用户发话题 上传文件名为UUID + b + 用户Id
	 * @param model
	 * @param dir 上传类型，分别为image、flash、media、file 
	 * @param userName 用户名称
	 * @param isStaff 是否是员工   true:员工   false:会员
	 * @param fileName 文件名称 预签名时有值
	 * @param imgFile
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=upload",method=RequestMethod.POST)
	public String upload(ModelMap model,String dir,String userName, Boolean isStaff,String fileName,
			MultipartFile file, HttpServletResponse response) throws Exception {
		
		String number = topicManage.generateFileNumber(userName, isStaff);
		
		String errorMessage  = "";
		
		Map<String,Object> returnJson = new HashMap<String,Object>();
		if(dir != null && number != null && !"".equals(number.trim())){
			DateTime dateTime = new DateTime();     
		     
			String date = dateTime.toString("yyyy-MM-dd");
			
			int fileSystem = fileManage.getFileSystem();
			if(fileSystem ==10 || fileSystem == 20 || fileSystem == 30){//10.SeaweedFS 20.MinIO 30.阿里云OSS
				if(fileName != null && !"".equals(fileName.trim())){
					//取得文件后缀
					String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
					
					if(dir.equals("image")){
						//允许上传格式
						List<String> formatList = CommentedProperties.readRichTextAllowImageUploadFormat();
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							//文件锁目录
							String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_image_"+newFileName);
							String presigne = fileManage.createPresigned("file/topic/"+date+"/image/"+newFileName,null);//创建预签名
							
							
							//返回预签名URL
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);
							
						}else{
							errorMessage = "文件格式不允许上传";
						}
						
						
					}else if(dir.equals("flash")){
						//允许上传格式
						List<String> formatList = new ArrayList<String>();
						formatList.add("swf");
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							//文件锁目录
							String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_flash_"+newFileName);
							String presigne = fileManage.createPresigned("file/topic/"+date+"/flash/"+newFileName,null);//创建预签名
							
							
							//返回预签名URL
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);
							
						}else{
							errorMessage = "文件格式不允许上传";
						}
						
					}else if(dir.equals("media")){
						//允许上传格式
						List<String> formatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							//文件锁目录
							String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_media_"+newFileName);
							String presigne = fileManage.createPresigned("file/topic/"+date+"/media/"+newFileName,null);//创建预签名
							
							
							//返回预签名URL
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);
							
						}else{
							errorMessage = "文件格式不允许上传";
						}
					}else if(dir.equals("file")){
						//允许上传格式
						List<String> formatList = CommentedProperties.readRichTextAllowFileUploadFormat();
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							//文件锁目录
							String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_file_"+newFileName);
							String presigne = fileManage.createPresigned("file/topic/"+date+"/file/"+newFileName,null);//创建预签名
							
							
							//返回预签名URL
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);
							
						}else{
							errorMessage = "文件格式不允许上传";
						}
					}else{
						errorMessage = "缺少dir参数";
					}
				}else{
					errorMessage = "文件名称不能为空";
				}
				
				
				
			
			}else{//0.本地系统
				
				if(file != null && !file.isEmpty()){
					//当前文件名称
					String sourceFileName = file.getOriginalFilename();
				
					//取得文件后缀
					String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();

					if(dir.equals("image")){
						//允许上传图片格式
						List<String> formatList = CommentedProperties.readRichTextAllowImageUploadFormat();

						//验证文件类型
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
						
						//如果用flash控件上传
						if(file.getContentType().equalsIgnoreCase("application/octet-stream")){
							String fileType = FileType.getType(file.getInputStream());
							for (String format :formatList) {
								if(format.equalsIgnoreCase(fileType)){
									authentication = true;
									break;
								}
							}
						}
						
						if(authentication ){
							//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
							String pathDir = "file"+File.separator+"topic"+File.separator + date +File.separator +"image"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							//生成文件保存目录
							fileManage.createFolder(pathDir);
							//生成锁文件保存目录
							fileManage.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_image_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/topic/"+date+"/image/"+newFileName);
							returnJson.put("title", file.getOriginalFilename());//旧文件名称
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else if(dir.equals("flash")){
						//允许上传flash格式
						List<String> flashFormatList = new ArrayList<String>();
						flashFormatList.add("swf");
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),flashFormatList);
						
						if(authentication){
							
							//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
							String pathDir = "file"+File.separator+"topic"+File.separator + date+ File.separator +"flash"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							//生成文件保存目录
							fileManage.createFolder(pathDir);
							//生成锁文件保存目录
							fileManage.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_flash_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
							
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/topic/"+date+"/flash/"+newFileName);
							returnJson.put("title", file.getOriginalFilename());//旧文件名称
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else if(dir.equals("media")){
						//允许上传视音频格式
						List<String> formatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
						
						if(authentication){
							
							//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
							String pathDir = "file"+File.separator+"topic"+File.separator + date+ File.separator +"media"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							//生成文件保存目录
							fileManage.createFolder(pathDir);
							//生成锁文件保存目录
							fileManage.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_media_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());

							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/topic/"+date+"/media/"+newFileName);
							returnJson.put("title", file.getOriginalFilename());//旧文件名称
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else if(dir.equals("file")){
						//允许上传文件格式
						List<String> formatList = CommentedProperties.readRichTextAllowFileUploadFormat();
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
						if(authentication){
							
							//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
							String pathDir = "file"+File.separator+"topic"+File.separator + date+ File.separator +"file"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							//生成文件保存目录
							fileManage.createFolder(pathDir);
							//生成锁文件保存目录
							fileManage.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_file_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());

							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/topic/"+date+"/file/"+newFileName);
							returnJson.put("title", file.getOriginalFilename());//旧文件名称
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else{
						errorMessage = "缺少dir参数";
					}
				}else{
					errorMessage = "文件不能为空";
				}
				
				
			}
		}else{
			errorMessage = "参数不能为空";
		}
		
		//上传失败
		returnJson.put("error", 1);
		returnJson.put("message", errorMessage);
		return JsonUtils.toJSONString(returnJson);
	}
	/**
	 * 话题   修改界面显示
	 * 
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Long topicId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();

		if(topicId != null && topicId >0L){
			Topic topic = topicService.findById(topicId);
			if(topic != null){
				if(topic.getContent() != null && !"".equals(topic.getContent().trim())){
					//处理富文本路径
					topic.setContent(fileManage.processRichTextFilePath(topic.getContent(),"topic"));
				}
				List<Tag> tagList = tagService.findAllTag();
				if(tagList != null && tagList.size() >0){
					for(Tag tag : tagList){
						if(topic.getTagId().equals(tag.getId())){
							topic.setTagName(tag.getName());
							break;
						}
					}
				}	
				
				returnValue.put("topic", topic);
				
				List<UserGrade> userGradeList = userGradeService.findAllGrade();
				returnValue.put("userGradeList", userGradeList);
				
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
			}else{
				error.put("topicId", "话题不存在");
			}	
			
			
		}else{
			error.put("topicId", "话题Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 话题   修改
	 * @param model
	 * @param pageForm
	 * @param tagId
	 * @param title
	 * @param content
	 * @param sort 
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Long topicId,Long tagId,
			String tagName,String title,Boolean allow,Integer status,
			String content,String sort,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Topic topic = null;
		List<String> imageNameList = null;
		boolean isImage = false;//是否含有图片
		List<String> flashNameList = null;
		boolean isFlash = false;//是否含有Flash
		List<String> mediaNameList = null;
		boolean isMedia = false;//是否含有音视频
		List<String> fileNameList = null;
		boolean isFile = false;//是否含有文件
		boolean isMap = false;//是否含有地图
		Map<String,String> error = new HashMap<String,String>();
		
		//前3张图片地址
		List<ImageInfo> beforeImageList = new ArrayList<ImageInfo>();
		//旧前3张图片地址
		List<ImageInfo> oldBeforeImageList = new ArrayList<ImageInfo>();
		
		List<String> oldPathFileList = new ArrayList<String>();//旧路径文件
		//旧状态
		Integer old_status = -1;
		
		
		if(status == null){
			error.put("status", "话题状态不能为空");
		}
		
		
		String old_content = "";
		if(topicId != null && topicId >0L){
			topic = topicService.findById(topicId);
			if(topic != null && error.size() ==0){
				old_status = topic.getStatus();
				topic.setTagId(tagId);
				topic.setTagName(tagName);
				topic.setAllow(allow);
				topic.setStatus(status);
				
				if(topic.getImage() != null && !"".equals(topic.getImage().trim())){
					oldBeforeImageList = JsonUtils.toGenericObject(topic.getImage().trim(),new TypeReference< List<ImageInfo> >(){});
				}
				
				old_content = topic.getContent();
				
				if(title != null && !"".equals(title.trim())){
					topic.setTitle(title);
					if(title.length() >150){
						error.put("title", "不能大于150个字符");
					}
				}else{
					error.put("title", "标题不能为空");
				}
				if(content != null && !"".equals(content.trim())){
					
					//过滤标签
					content = textFilterManage.filterTag(request,content);
					Object[] object = textFilterManage.filterHtml(request,content,"topic",null);
			
					String value = (String)object[0];
					imageNameList = (List<String>)object[1];
					isImage = (Boolean)object[2];//是否含有图片
					flashNameList = (List<String>)object[3];
					isFlash = (Boolean)object[4];//是否含有Flash
					mediaNameList = (List<String>)object[5];
					isMedia = (Boolean)object[6];//是否含有音视频
					fileNameList = (List<String>)object[7];
					isFile = (Boolean)object[8];//是否含有文件
					isMap = (Boolean)object[9];//是否含有地图
					
					List<UserGrade> userGradeList = userGradeService.findAllGrade();
					//校正隐藏标签
					String validValue =  textFilterManage.correctionHiddenTag(value,userGradeList);
					
					if(topic.getIsStaff()){//如果是员工
						//解析隐藏标签
						Map<Integer,Object> analysisHiddenTagMap = textFilterManage.analysisHiddenTag(validValue);
						for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
							//管理员账户不能发表'余额购买'话题
							if(entry.getKey().equals(HideTagType.AMOUNT.getName())){
								error.put("content", "管理员账户不能发表'余额购买'话题");
								break;
							}
						}
					}

					//删除隐藏标签
					String new_content = textFilterManage.deleteHiddenTag(value);
					
					
					//不含标签内容
					String text = textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(new_content));
					
					
					//清除空格&nbsp;
					String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
					
					
					//摘要
					if(trimSpace != null && !"".equals(trimSpace)){
						if(trimSpace.length() >180){
							topic.setSummary(trimSpace.substring(0, 180)+"..");
						}else{
							topic.setSummary(trimSpace+"..");
						}
					}
					
					//不含标签内容
					String source_text = textFilterManage.filterText(content);
					//清除空格&nbsp;
					String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();
					if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(source_text.trim()) && !"".equals(source_trimSpace))){
						topic.setContent(validValue);
					}else{
						error.put("content", "话题内容不能为空");
					}	
					
					//非隐藏标签内图片
					List<String> other_imageNameList = textFilterManage.readImageName(new_content,"topic");
					
					
					if(other_imageNameList != null && other_imageNameList.size() >0){
						for(int i=0; i<other_imageNameList.size(); i++){
							ImageInfo imageInfo = new ImageInfo();
							imageInfo.setName(FileUtil.getName(other_imageNameList.get(i)));
							imageInfo.setPath(FileUtil.getFullPath(other_imageNameList.get(i)));
							
							beforeImageList.add(imageInfo);
					
						}
						topic.setImage(JsonUtils.toJSONString(beforeImageList));
						
					}else{
						topic.setImage(null);
					}
					
					
					
				}else{
					error.put("content", "话题内容不能为空");
				}
				
				if(sort != null){
					if(Verification.isNumeric(sort.toString())){
						if(sort.toString().length() <=8){
							topic.setSort(Integer.parseInt(sort.toString()));
						}else{
							error.put("sort", "不能超过8位数字");
						}
					}else{
						error.put("sort", "请填写整数");
					}
				}else{
					error.put("sort", "排序不能为空");
				}
				
				
				if(error.size() ==0){
					topic.setLastUpdateTime(new Date());//最后修改时间
					int i = topicService.updateTopic(topic);
					//更新索引
					topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),2));
					
					if(i >0 && !old_status.equals(status)){
						User user = userManage.query_cache_findUserByUserName(topic.getUserName());
						if(user != null){
							//修改用户动态话题状态
							userService.updateUserDynamicTopicStatus(user.getId(),topic.getUserName(),topic.getId(),topic.getStatus());
						}
						
					}
					
					
					//删除缓存
					topicManage.deleteTopicCache(topic.getId());//删除话题缓存
					topicManage.delete_cache_analysisHiddenTag(topic.getId());//删除解析隐藏标签缓存
					topicManage.delete_cache_analysisFullFileName(topic.getId());//删除 解析上传的文件完整路径名称缓存
					topicManage.delete_cache_markUpdateTopicStatus(topic.getId());//删除 标记修改话题状态
					
					Object[] obj = textFilterManage.readPathName(old_content,"topic");
					if(obj != null && obj.length >0){
						//删除旧媒体处理任务
						List<String> delete_mediaProcessFileNameList = new ArrayList<String>();//文件名称
						//删除旧媒体切片文件夹
						List<String> delete_mediaProcessDirectoryList = new ArrayList<String>();//文件夹
						
						//新增媒体处理任务
						if(isMedia){
							List<MediaProcessQueue> mediaProcessQueueList = new ArrayList<MediaProcessQueue>();
							//旧影音
							List<String> old_mediaNameList = (List<String>)obj[2];	
							A:for(String fullPathName :mediaNameList){
								for(String old_fullPathName : old_mediaNameList){
									if(old_fullPathName.equals("file/topic/"+fullPathName)){
										continue A;
									}
									
								}
								//取得路径名称
								String pathName = FileUtil.getFullPath(fullPathName);
								//文件名称
								String fileName = FileUtil.getName(fullPathName);
								
								MediaProcessQueue mediaProcessQueue = new MediaProcessQueue();
								mediaProcessQueue.setModule(10);//10:话题
								mediaProcessQueue.setType(10);//10:视频
								mediaProcessQueue.setParameter(String.valueOf(topic.getId()));
								mediaProcessQueue.setPostTime(topic.getLastUpdateTime());
								mediaProcessQueue.setFilePath("file/topic/"+pathName);
								mediaProcessQueue.setFileName(fileName);
								mediaProcessQueueList.add(mediaProcessQueue);
							}
							mediaProcessService.saveMediaProcessQueueList(mediaProcessQueueList);

						}
						
						
						
						//旧图片
						List<String> old_imageNameList = (List<String>)obj[0];
						
						if(old_imageNameList != null && old_imageNameList.size() >0){
							
					        Iterator<String> iter = old_imageNameList.iterator();
					        while (iter.hasNext()) {
					        	String imageName = iter.next();  
					        	
								for(String new_imageName : imageNameList){
									if(imageName.equals("file/topic/"+new_imageName)){
										iter.remove();
										break;
									}
								}
							}
							if(old_imageNameList != null && old_imageNameList.size() >0){
								for(String imageName : old_imageNameList){
									
									oldPathFileList.add(FileUtil.toSystemPath(imageName));
			
								}
								
							}
						}
						
						//旧Flash
						List<String> old_flashNameList = (List<String>)obj[1];		
						if(old_flashNameList != null && old_flashNameList.size() >0){		
					        Iterator<String> iter = old_flashNameList.iterator();
					        while (iter.hasNext()) {
					        	String flashName = iter.next();  
								for(String new_flashName : flashNameList){
									if(flashName.equals("file/topic/"+new_flashName)){
										iter.remove();
										break;
									}
								}
							}
							if(old_flashNameList != null && old_flashNameList.size() >0){
								for(String flashName : old_flashNameList){
									oldPathFileList.add(FileUtil.toSystemPath(flashName));
									
								}
								
							}
						}
		
						//旧影音
						List<String> old_mediaNameList = (List<String>)obj[2];	
						if(old_mediaNameList != null && old_mediaNameList.size() >0){		
					        Iterator<String> iter = old_mediaNameList.iterator();
					        while (iter.hasNext()) {
					        	String mediaName = iter.next();  
								for(String new_mediaName : mediaNameList){
									if(mediaName.equals("file/topic/"+new_mediaName)){
										iter.remove();
										break;
									}
								}
							}
							if(old_mediaNameList != null && old_mediaNameList.size() >0){
								for(String mediaName : old_mediaNameList){
									oldPathFileList.add(FileUtil.toSystemPath(mediaName));
									
									delete_mediaProcessFileNameList.add(FileUtil.getName(mediaName));
							
									
									delete_mediaProcessDirectoryList.add(FileUtil.toSystemPath(FileUtil.getFullPath(mediaName))+FileUtil.getBaseName(mediaName)+File.separator);
								}
								
							}
						}
						
						//旧文件
						List<String> old_fileNameList = (List<String>)obj[3];		
						if(old_fileNameList != null && old_fileNameList.size() >0){		
					        Iterator<String> iter = old_fileNameList.iterator();
					        while (iter.hasNext()) {
					        	String fileName = iter.next();  
								for(String new_fileName : fileNameList){
									if(fileName.equals("file/topic/"+new_fileName)){
										iter.remove();
										break;
									}
								}
							}
							if(old_fileNameList != null && old_fileNameList.size() >0){
								for(String fileName : old_fileNameList){
									oldPathFileList.add(FileUtil.toSystemPath(fileName));
									
								}
								
							}
						}
						
						//删除旧媒体处理任务
						if(delete_mediaProcessFileNameList != null && delete_mediaProcessFileNameList.size() >0){
							mediaProcessService.deleteMediaProcessQueue(delete_mediaProcessFileNameList);
							//删除缓存
							for(String delete_mediaProcessFileName : delete_mediaProcessFileNameList){
								mediaProcessQueueManage.delete_cache_findMediaProcessQueueByFileName(delete_mediaProcessFileName);
							}
						}
						//删除旧媒体切片文件夹
						if(delete_mediaProcessDirectoryList != null && delete_mediaProcessDirectoryList.size() >0){
							for(String mediaProcessDirectory :delete_mediaProcessDirectoryList){
								if(mediaProcessDirectory != null && !"".equals(mediaProcessDirectory.trim())){
									fileManage.removeDirectory(mediaProcessDirectory);
								}
							}
						}
						
					}
					
					
					List<Thumbnail> thumbnailList = thumbnailService.findAllThumbnail_cache();
					if(thumbnailList != null && thumbnailList.size() >0){
						
						
						if(oldBeforeImageList != null && oldBeforeImageList.size() >0){
							List<ImageInfo> deleteBeforeImageList = new ArrayList<ImageInfo>();
							A:for(ImageInfo oldImageInfo : oldBeforeImageList){
								if(beforeImageList != null && beforeImageList.size() >0){
									for(ImageInfo imageInfo : beforeImageList){
										if(oldImageInfo.getName().equals(imageInfo.getName())){
											continue A;
										}
									}
								}
								deleteBeforeImageList.add(oldImageInfo);
							}
							if(deleteBeforeImageList != null && deleteBeforeImageList.size() >0){
								//删除旧缩略图
								fileManage.deleteThumbnail(thumbnailList,oldBeforeImageList);
							}
						}
						
						//异步增加缩略图
						if(beforeImageList != null && beforeImageList.size() >0){
							fileManage.addThumbnail(thumbnailList,beforeImageList);
						}
					}
					
					//上传文件编号
					String fileNumber = topicManage.generateFileNumber(topic.getUserName(), topic.getIsStaff());
					
					//删除图片锁
					if(imageNameList != null && imageNameList.size() >0){
						for(String imageName :imageNameList){
					
							 if(imageName != null && !"".equals(imageName.trim())){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!topicManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
										continue;
								 }
								 fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
			
							 }
						}
					}
					//删除Falsh锁
					if(flashNameList != null && flashNameList.size() >0){
						for(String flashName :flashNameList){
							 
							 if(flashName != null && !"".equals(flashName.trim())){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!topicManage.getFileNumber(FileUtil.getBaseName(flashName.trim())).equals(fileNumber)){
										continue;
								 }
								 fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
				
							 }
						}
					}
					//删除音视频锁
					if(mediaNameList != null && mediaNameList.size() >0){
						for(String mediaName :mediaNameList){
							if(mediaName != null && !"".equals(mediaName.trim())){
								//如果验证不是当前用户上传的文件，则不删除
								if(!topicManage.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
									continue;
								}
								fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
							
							}
						}
					}
					//删除文件锁
					if(fileNameList != null && fileNameList.size() >0){
						for(String fileName :fileNameList){
							if(fileName != null && !"".equals(fileName.trim())){
								//如果验证不是当前用户上传的文件，则不删除
								if(!topicManage.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
									continue;
								}
								fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
							
							}
						}
					}
					
					//删除旧路径文件
					if(oldPathFileList != null && oldPathFileList.size() >0){
						for(String oldPathFile :oldPathFileList){
							//如果验证不是当前用户上传的文件，则不删除
							if(!topicManage.getFileNumber(FileUtil.getBaseName(oldPathFile.trim())).equals(fileNumber)){
								continue;
							}
							
							
							//替换路径中的..号
							oldPathFile = FileUtil.toRelativePath(oldPathFile);
							
							//删除旧路径文件
							Boolean state = fileManage.deleteFile(oldPathFile);
							if(state != null && state == false){

								//替换指定的字符，只替换第一次出现的
								oldPathFile = StringUtils.replaceOnce(oldPathFile, "file"+File.separator+"topic"+File.separator, "");
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"topic"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));
							}
						}
					}
					
					
					
					
				}
				
			}else{
				error.put("topic", "话题不存在");
			}
		}else{
			error.put("topic", "Id不存在");
		}
		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 话题   删除
	 * @param model
	 * @param topicId 话题Id集合
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long[] topicId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		String username = "";//用户名称
		String userId = "";//用户Id
		Object principal  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(principal instanceof SysUsers){
			userId =((SysUsers)principal).getUserId();
			username =((SysUsers)principal).getUserAccount();
		}
		if(topicId != null && topicId.length >0){
			List<Long> topicIdList = new ArrayList<Long>();
			for(Long l :topicId){
				if(l != null && l >0L){
					topicIdList .add(l);
				}
			}
			if(topicIdList != null && topicIdList.size() >0){

				List<Topic> topicList = topicService.findByIdList(topicIdList);
				if(topicList != null && topicList.size() >0){
					for(Topic topic : topicList){
						if(topic.getStatus() < 100){//标记删除
							int i = topicService.markDelete(topic.getId());

							User user = userManage.query_cache_findUserByUserName(topic.getUserName());
							if(i >0 && user != null){
								//修改话题状态
								userService.softDeleteUserDynamicByTopicId(user.getId(),topic.getUserName(),topic.getId());
							}
							//更新索引
							topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),2));
							topicManage.deleteTopicCache(topic.getId());//删除缓存
						}else{//物理删除
							GiveRedEnvelope giveRedEnvelope = null;
							String userName = null;
							BigDecimal amount = null;
							Object paymentLogObject = null;
							
							if(topic.getGiveRedEnvelopeId() != null && !"".equals(topic.getGiveRedEnvelopeId())){
								giveRedEnvelope = redEnvelopeService.findById(topic.getGiveRedEnvelopeId());
								if(giveRedEnvelope != null && (giveRedEnvelope.getRefundAmount() == null || giveRedEnvelope.getRefundAmount().compareTo(new BigDecimal("0"))==0)){
									User user = userManage.query_cache_findUserById(giveRedEnvelope.getUserId());
									BigDecimal refundAmount = redEnvelopeManage.refundAmount(giveRedEnvelope);
									
									if(user != null && refundAmount.compareTo(new BigDecimal("0")) >0){
										
										PaymentLog paymentLog = new PaymentLog();
										paymentLog.setPaymentRunningNumber(paymentManage.createRunningNumber(user.getId()));//支付流水号
										paymentLog.setPaymentModule(140);//支付模块 140.话题返还红包
										paymentLog.setSourceParameterId(topic.getGiveRedEnvelopeId());//参数Id 
										paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
										paymentLog.setOperationUserName(username);//操作用户名称
										paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出 
										paymentLog.setAmount(refundAmount);//金额
										paymentLog.setInterfaceProduct(0);//接口产品
										paymentLog.setUserName(user.getUserName());//用户名称
										paymentLog.setTimes(new Date());
										paymentLog.setSourceParameterId(giveRedEnvelope.getId());
										//金额日志
										paymentLogObject = paymentManage.createPaymentLogObject(paymentLog);
										
										
										userName = user.getUserName();
										amount = refundAmount;
									}
									
								}
								
								
							}
							
							
							String fileNumber = topicManage.generateFileNumber(topic.getUserName(), topic.getIsStaff());
							
							int i = topicService.deleteTopic(topic.getId(),giveRedEnvelope,userName,amount,paymentLogObject);
							
							if(i>0){
								//根据话题Id删除用户动态(话题下的评论和回复也同时删除)
								userService.deleteUserDynamicByTopicId(topic.getId());
							}
							
							topicManage.deleteTopicCache(topic.getId());//删除缓存
							
							topicManage.delete_cache_markUpdateTopicStatus(topic.getId());//删除 标记修改话题状态
							if(topic.getGiveRedEnvelopeId() != null && !"".equals(topic.getGiveRedEnvelopeId())){
								//删除缓存
						    	redEnvelopeManage.delete_cache_findById(topic.getGiveRedEnvelopeId());
							}
							
							//更新索引
							topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),3));
							Object[] obj = textFilterManage.readPathName(topic.getContent(),"topic");
							if(obj != null && obj.length >0){
								//删除旧媒体处理任务
								List<String> delete_mediaProcessFileNameList = new ArrayList<String>();//文件名称
								//删除旧媒体切片文件夹
								List<String> delete_mediaProcessDirectoryList = new ArrayList<String>();//文件夹
								
								
								List<String> filePathList = new ArrayList<String>();
								
								List<Thumbnail> thumbnailList = thumbnailService.findAllThumbnail_cache();
								if(thumbnailList != null && thumbnailList.size() >0){
									List<ImageInfo> beforeImageList = null;
									if(topic.getImage() != null && !"".equals(topic.getImage().trim())){
										beforeImageList = JsonUtils.toGenericObject(topic.getImage().trim(),new TypeReference< List<ImageInfo> >(){});
									}
									if(beforeImageList != null && beforeImageList.size() >0){
										//删除旧缩略图
										fileManage.deleteThumbnail(thumbnailList,beforeImageList);
									}
									
								}
								
								//删除图片
								List<String> imageNameList = (List<String>)obj[0];		
								for(String imageName :imageNameList){
									filePathList.add(FileUtil.toSystemPath(imageName));
									
								}
								//删除Flash
								List<String> flashNameList = (List<String>)obj[1];		
								for(String flashName :flashNameList){
									filePathList.add(FileUtil.toSystemPath(flashName));
								}
								//删除影音
								List<String> mediaNameList = (List<String>)obj[2];		
								for(String mediaName :mediaNameList){
									filePathList.add(FileUtil.toSystemPath(mediaName));
									
									delete_mediaProcessFileNameList.add(FileUtil.getName(mediaName));
									
									delete_mediaProcessDirectoryList.add(FileUtil.toSystemPath(FileUtil.getFullPath(mediaName))+FileUtil.getBaseName(mediaName)+File.separator);
								}
								//删除文件
								List<String> fileNameList = (List<String>)obj[3];		
								for(String fileName :fileNameList){
									filePathList.add(FileUtil.toSystemPath(fileName));
								}
								
								for(String filePath :filePathList){
									
									
									 //如果验证不是当前用户上传的文件，则不删除
									 if(!topicManage.getFileNumber(FileUtil.getBaseName(filePath.trim())).equals(fileNumber)){
										 continue;
									 }
									
									//替换路径中的..号
									filePath = FileUtil.toRelativePath(filePath);
									//删除旧路径文件
									Boolean state = fileManage.deleteFile(filePath);
									if(state != null && state == false){
										 //替换指定的字符，只替换第一次出现的
										filePath = StringUtils.replaceOnce(filePath, "file"+File.separator+"topic"+File.separator, "");
										//创建删除失败文件
										fileManage.failedStateFile("file"+File.separator+"topic"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
									}
								}
								
								//清空目录
								Boolean state_ = fileManage.removeDirectory("file"+File.separator+"comment"+File.separator+topic.getId()+File.separator);
								if(state_ != null && state_ == false){
									//创建删除失败目录文件
									fileManage.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+"#"+topic.getId());
								}
								
								//删除旧媒体处理任务
								if(delete_mediaProcessFileNameList != null && delete_mediaProcessFileNameList.size() >0){
									mediaProcessService.deleteMediaProcessQueue(delete_mediaProcessFileNameList);
									
									//删除缓存
									for(String delete_mediaProcessFileName : delete_mediaProcessFileNameList){
										mediaProcessQueueManage.delete_cache_findMediaProcessQueueByFileName(delete_mediaProcessFileName);
									}
									
								}
								//删除旧媒体切片文件夹
								if(delete_mediaProcessDirectoryList != null && delete_mediaProcessDirectoryList.size() >0){
									for(String mediaProcessDirectory :delete_mediaProcessDirectoryList){
										if(mediaProcessDirectory != null && !"".equals(mediaProcessDirectory.trim())){
											fileManage.removeDirectory(mediaProcessDirectory);
										}
									}
								}
							}
						}
						
						
					}	
					return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
				}
			}else{
				error.put("topicId", "话题Id不能为空");
			}
		}else{
			error.put("topicId", "话题Id不存在");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 还原
	 * @param model
	 * @param topicId 话题Id集合
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=reduction",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String reduction(ModelMap model,Long[] topicId,
			HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		if(topicId != null && topicId.length>0){
			
			List<Topic> topicList = topicService.findByIdList(Arrays.asList(topicId));
			if(topicList != null && topicList.size() >0){
				int i = topicService.reductionTopic(topicList);
				
				for(Topic topic :topicList){
					
					User user = userManage.query_cache_findUserByUserName(topic.getUserName());
					if(i >0 && user != null){
						//修改话题状态
						userService.reductionUserDynamicByTopicId(user.getId(),topic.getUserName(),topic.getId());
					}
					
					//更新索引
					topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),2));
					topicManage.deleteTopicCache(topic.getId());//删除缓存
				}
		
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}else{
				error.put("topic", "话题不能为空");
			}
		}else{
			error.put("topic", "话题Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 审核话题
	 * @param model
	 * @param topicId 话题Id
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=auditTopic",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String auditTopic(ModelMap model,Long topicId,
			HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(topicId != null && topicId>0L){
			int i = topicService.updateTopicStatus(topicId, 20);

			Topic topic = topicManage.queryTopicCache(topicId);
			if(i >0 && topic != null){
				User user = userManage.query_cache_findUserByUserName(topic.getUserName());
				if(user != null){
					//修改话题状态
					userService.updateUserDynamicTopicStatus(user.getId(),topic.getUserName(),topic.getId(),20);
				}
			}

			//更新索引
			topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topicId),2));
			topicManage.deleteTopicCache(topicId);//删除缓存
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("topicId", "话题Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	

}
