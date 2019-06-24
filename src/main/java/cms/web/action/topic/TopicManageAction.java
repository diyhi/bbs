package cms.web.action.topic;

import java.io.File;
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
import cms.bean.user.UserGrade;
import cms.service.setting.SettingService;
import cms.service.thumbnail.ThumbnailService;
import cms.service.topic.CommentService;
import cms.service.topic.TagService;
import cms.service.topic.TopicIndexService;
import cms.service.topic.TopicService;
import cms.service.user.UserGradeService;
import cms.utils.FileType;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.RedirectPath;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.FileManage;
import cms.web.action.SystemException;
import cms.web.action.TextFilterManage;
import cms.web.action.thumbnail.ThumbnailManage;


/**
 * 话题管理
 *
 */
@Controller
@RequestMapping("/control/topic/manage") 
public class TopicManageAction {
	 
	@Resource TopicService topicService; 
	@Resource TextFilterManage textFilterManage;
	@Resource FileManage fileManage;
	@Resource SettingService settingService;
	@Resource TagService tagService;
	@Resource CommentManage commentManage;
	@Resource CommentService commentService;
	@Resource TopicManage topicManage;
	
	@Resource TopicIndexService topicIndexService;
	
	@Resource UserGradeService userGradeService;
	
	@Resource ThumbnailService thumbnailService;
	@Resource ThumbnailManage thumbnailManage;
	/**
	 * 话题   查看
	 * @param topicId
	 * @param model
	 * @param pageForm
	 * @param origin 来源 1.话题列表  2.全部待审核话题 3.全部待审核评论 4.全部待审核回复
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=view",method=RequestMethod.GET)
	public String view(Long topicId,Long commentId,ModelMap model,Integer page,Integer origin,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if(topicId != null && topicId >0L){
			Topic topic = topicService.findById(topicId);
			if(topic == null){
				throw new SystemException("话题不存在");
			}else{
				if(topic.getIp() != null && !"".equals(topic.getIp().trim())){
					topic.setIpAddress(IpAddress.queryAddress(topic.getIp().trim()));
				}
				
				model.addAttribute("topic", topic);
				
				model.addAttribute("availableTag", commentManage.availableTag());
				
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
								new_quoteList.put(comment.getId(), textFilterManage.filterText(comment.getContent()));
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
							new_quoteList.put(comment.getId(), textFilterManage.filterText(comment.getContent()));
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
								if(comment.getId().equals(reply.getCommentId())){
									comment.addReply(reply);
								}
							}
							
						}
					}
				}
				
				
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
				model.addAttribute("pageView", pageView);
				
				String username = "";//用户名称
				
				Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
				if(obj instanceof UserDetails){
					username =((UserDetails)obj).getUsername();	
				}
				model.addAttribute("userName", username);
			}
		}
		return "jsp/topic/view_topic";
	}
	
	/**
	 * 话题   添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Topic topic,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String username = "";//用户名称
		
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof UserDetails){
			username =((UserDetails)obj).getUsername();	
		}
		model.addAttribute("userName", username);
		List<UserGrade> userGradeList = userGradeService.findAllGrade();
		model.addAttribute("userGradeList", JsonUtils.toJSONString(userGradeList));
		return "jsp/topic/add_topic";
	}
	
	/**
	 * 话题  添加
	 * isTopicList 上一链接是否来自话题列表
	 */
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Long tagId,String tagName, String title,Boolean allow,Integer status,
			String content,String sort,Boolean isTopicList,
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
				String text = textFilterManage.filterText(new_content);
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
						imageInfo.setName(fileManage.getName(other_imageNameList.get(i)));
						imageInfo.setPath(fileManage.getFullPath(other_imageNameList.get(i)));
						
						beforeImageList.add(imageInfo);
						
						if(i ==2){//只添加3张图片
							break;
						}
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
		
		if(error != null && error.size() >0){
			model.addAttribute("error", error);
		}else{
			topicService.saveTopic(topic);
			
			//更新索引
			topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),1));
			
			//上传文件编号
			String fileNumber = "a"+userId;
			
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
			
					 if(imageName != null && !"".equals(imageName.trim())){
		 
						 //如果验证不是当前用户上传的文件，则不删除锁
						 if(!topicManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
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
						 if(!topicManage.getFileNumber(fileManage.getBaseName(flashName.trim())).equals(fileNumber)){
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
						if(!topicManage.getFileNumber(fileManage.getBaseName(mediaName.trim())).equals(fileNumber)){
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
						if(!topicManage.getFileNumber(fileManage.getBaseName(fileName.trim())).equals(fileNumber)){
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
					thumbnailManage.addThumbnail(thumbnailList,beforeImageList);
				}
			}
			
			
			
			model.addAttribute("message","添加话题成功");//返回消息
			if(isTopicList != null && isTopicList == true){
				model.addAttribute("urladdress", RedirectPath.readUrl("control.topic.list")+"?visible=true");
			}else{
				model.addAttribute("urladdress", RedirectPath.readUrl("control.topic.manage")+"?method=list&visible=true&tagId="+tagId);
			}
			
			return "jsp/common/message";
			
		}
		topic.setTitle(title);
		topic.setContent(content);
		model.addAttribute("topic",topic);
		model.addAttribute("userName", username);
		model.addAttribute("userGradeList", JsonUtils.toJSONString(userGradeList));
		return "jsp/topic/add_topic";
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
	 * @param imgFile
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=upload",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String upload(ModelMap model,String dir,String userName, Boolean isStaff,
			MultipartFile imgFile, HttpServletResponse response) throws Exception {
		
		String number = topicManage.generateFileNumber(userName, isStaff);
		
		
		
		
		Map<String,Object> returnJson = new HashMap<String,Object>();
		if(dir != null && number != null && !"".equals(number.trim())){
			DateTime dateTime = new DateTime();     
		     
			String date = dateTime.toString("yyyy-MM-dd");
			
			if(imgFile != null && !imgFile.isEmpty()){
				
				
				//当前文件名称
				String fileName = imgFile.getOriginalFilename();
				
				//文件大小
				Long size = imgFile.getSize();
				//取得文件后缀
				String suffix = fileManage.getExtension(fileName).toLowerCase();

				if(dir.equals("image")){
					//允许上传图片格式
					List<String> formatList = new ArrayList<String>();
					formatList.add("gif");
					formatList.add("jpg");
					formatList.add("jpeg");
					formatList.add("bmp");
					formatList.add("png");
					
					//允许上传图片大小
					long imageSize = 200000L;

					//验证文件类型
					boolean authentication = fileManage.validateFileSuffix(imgFile.getOriginalFilename(),formatList);
					
					//如果用flash控件上传
					if(imgFile.getContentType().equalsIgnoreCase("application/octet-stream")){
						String fileType = FileType.getType(imgFile.getInputStream());
						for (String format :formatList) {
							if(format.equalsIgnoreCase(fileType)){
								authentication = true;
								break;
							}
						}
					}
					
					if(authentication && size/1024 <= imageSize){
						
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
						fileManage.newFile(lockPathDir+date +"_image_"+newFileName);
						//保存文件
						fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());
						
						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/topic/"+date+"/image/"+newFileName);
						return JsonUtils.toJSONString(returnJson);
						
					}
				}else if(dir.equals("flash")){
					//允许上传flash格式
					List<String> flashFormatList = new ArrayList<String>();
					flashFormatList.add("swf");
					
					//验证文件后缀
					boolean authentication = fileManage.validateFileSuffix(imgFile.getOriginalFilename(),flashFormatList);
					
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
						fileManage.newFile(lockPathDir+date +"_flash_"+newFileName);
						//保存文件
						fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());
						
						
						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/topic/"+date+"/flash/"+newFileName);
						return JsonUtils.toJSONString(returnJson);
					}
					
					
					
				}else if(dir.equals("media")){
					//允许上传视音频格式
					List<String> formatList = new ArrayList<String>();
					formatList.add("flv");
					formatList.add("mp4");
					formatList.add("avi");
					formatList.add("mkv");
					formatList.add("wmv");
					formatList.add("wav");
					formatList.add("rm/rmvb");
					formatList.add("mp3");
					formatList.add("flac");
					formatList.add("ape");
					
					
					//验证文件后缀
					boolean authentication = fileManage.validateFileSuffix(imgFile.getOriginalFilename(),formatList);
					
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
						fileManage.newFile(lockPathDir+date +"_media_"+newFileName);
						//保存文件
						fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());

						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/topic/"+date+"/media/"+newFileName);
						return JsonUtils.toJSONString(returnJson);
					}
				}else if(dir.equals("file")){
					//允许上传文件格式
					List<String> formatList = new ArrayList<String>();
					formatList.add("mp4");
					formatList.add("avi");
					formatList.add("mkv");
					formatList.add("wmv");
					formatList.add("wav");
					formatList.add("rm/rmvb");
					formatList.add("mp3");
					formatList.add("flac");
					formatList.add("ape");
					formatList.add("zip");
					formatList.add("rar");
					formatList.add("7z");
					
					//验证文件后缀
					boolean authentication = fileManage.validateFileSuffix(imgFile.getOriginalFilename(),formatList);
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
						fileManage.newFile(lockPathDir+date +"_file_"+newFileName);
						//保存文件
						fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());

						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/topic/"+date+"/file/"+newFileName);
						returnJson.put("title", imgFile.getOriginalFilename());//旧文件名称
						return JsonUtils.toJSONString(returnJson);
					}
				}
			}

		}
		
		//上传失败
		returnJson.put("error", 1);
		returnJson.put("message", "上传失败");
		return JsonUtils.toJSONString(returnJson);
	}
	/**
	 * 话题   修改界面显示
	 * 
	 */
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Long topicId,Boolean visible,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(topicId != null && topicId >0L){
			Topic topic = topicService.findById(topicId);
			if(topic != null){
				List<Tag> tagList = tagService.findAllTag();
				if(tagList != null && tagList.size() >0){
					for(Tag tag : tagList){
						if(topic.getTagId().equals(tag.getId())){
							topic.setTagName(tag.getName());
							break;
						}
					}
				}	
			}else{
				throw new SystemException("话题不存在");
			}	
			model.addAttribute("topic", topic);
			
			List<UserGrade> userGradeList = userGradeService.findAllGrade();
			model.addAttribute("userGradeList", JsonUtils.toJSONString(userGradeList));
		}
		return "jsp/topic/edit_topic";
	}
	/**
	 * 话题   修改
	 * @param model
	 * @param pageForm
	 * @param tagId
	 * @param title
	 * @param content
	 * @param sort 
	 * @param visible
	 * @param isTopicList 上一链接是否来自话题列表
	 */
	@ResponseBody//方式来做ajax,直接返回字符串
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Long topicId,Long tagId,
			String tagName,String title,Boolean allow,Integer status,
			String content,String sort,Boolean visible,Boolean isTopicList,
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

		
		String old_content = "";
		if(topicId != null && topicId >0L){
			topic = topicService.findById(topicId);
			if(topic != null){
				topic.setTagId(tagId);
				topic.setTagName(tagName);
				topic.setAllow(allow);
				if(topic.getStatus() < 100){
					topic.setStatus(status);
				}
				
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
					String text = textFilterManage.filterText(new_content);
					
					
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
							imageInfo.setName(fileManage.getName(other_imageNameList.get(i)));
							imageInfo.setPath(fileManage.getFullPath(other_imageNameList.get(i)));
							
							beforeImageList.add(imageInfo);
							
							if(i ==2){//只添加3张图片
								break;
							}
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
					topicService.updateTopic(topic);
					//更新索引
					topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),2));
					
					//删除缓存
					topicManage.deleteTopicCache(topic.getId());//删除话题缓存
					topicManage.delete_cache_analysisHiddenTag(topic.getId());//删除解析隐藏标签缓存
					
					
					Object[] obj = textFilterManage.readPathName(old_content,"topic");
					if(obj != null && obj.length >0){
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
									
									oldPathFileList.add(fileManage.toSystemPath(imageName));
			
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
									oldPathFileList.add(fileManage.toSystemPath(flashName));
									
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
									oldPathFileList.add(fileManage.toSystemPath(mediaName));
									
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
									oldPathFileList.add(fileManage.toSystemPath(fileName));
									
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
								thumbnailManage.deleteThumbnail(thumbnailList,oldBeforeImageList);
							}
						}
						
						//异步增加缩略图
						if(beforeImageList != null && beforeImageList.size() >0){
							thumbnailManage.addThumbnail(thumbnailList,beforeImageList);
						}
					}
					
					//上传文件编号
					String fileNumber = topicManage.generateFileNumber(topic.getUserName(), topic.getIsStaff());
					
					//删除图片锁
					if(imageNameList != null && imageNameList.size() >0){
						for(String imageName :imageNameList){
					
							 if(imageName != null && !"".equals(imageName.trim())){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!topicManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
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
								 if(!topicManage.getFileNumber(fileManage.getBaseName(flashName.trim())).equals(fileNumber)){
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
								if(!topicManage.getFileNumber(fileManage.getBaseName(mediaName.trim())).equals(fileNumber)){
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
								if(!topicManage.getFileNumber(fileManage.getBaseName(fileName.trim())).equals(fileNumber)){
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
							if(!topicManage.getFileNumber(fileManage.getBaseName(oldPathFile.trim())).equals(fileNumber)){
								continue;
							}
							
							
							//替换路径中的..号
							oldPathFile = fileManage.toRelativePath(oldPathFile);
							
							//删除旧路径文件
							Boolean state = fileManage.deleteFile(oldPathFile);
							if(state != null && state == false){

								//替换指定的字符，只替换第一次出现的
								oldPathFile = StringUtils.replaceOnce(oldPathFile, "file"+File.separator+"topic"+File.separator, "");
								oldPathFile = StringUtils.replace(oldPathFile, File.separator, "_");//替换所有出现过的字符
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"topic"+File.separator+"lock"+File.separator+oldPathFile);
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
		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", error);
		}else{
			returnValue.put("success", "true");
			
		}
		return JsonUtils.toJSONString(returnValue);
	}
	/**
	 * 话题   删除
	 * @param model
	 * @param informationId
	 * @param visible
	*/
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long[] topicId,
			Boolean isTopicList,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
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
							//更新索引
							topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),2));
							topicManage.deleteTopicCache(topic.getId());//删除缓存
						}else{//物理删除
							
							String fileNumber = topicManage.generateFileNumber(topic.getUserName(), topic.getIsStaff());
							
							int i = topicService.deleteTopic(topic.getId());
							topicManage.deleteTopicCache(topic.getId());//删除缓存
							//更新索引
							topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),3));
							Object[] obj = textFilterManage.readPathName(topic.getContent(),"topic");
							if(obj != null && obj.length >0){
								List<String> filePathList = new ArrayList<String>();
								
								List<Thumbnail> thumbnailList = thumbnailService.findAllThumbnail_cache();
								if(thumbnailList != null && thumbnailList.size() >0){
									List<ImageInfo> beforeImageList = null;
									if(topic.getImage() != null && !"".equals(topic.getImage().trim())){
										beforeImageList = JsonUtils.toGenericObject(topic.getImage().trim(),new TypeReference< List<ImageInfo> >(){});
									}
									if(beforeImageList != null && beforeImageList.size() >0){
										//删除旧缩略图
										thumbnailManage.deleteThumbnail(thumbnailList,beforeImageList);
									}
									
								}
								
								//删除图片
								List<String> imageNameList = (List<String>)obj[0];		
								for(String imageName :imageNameList){
									filePathList.add(imageName);
									
								}
								//删除Flash
								List<String> flashNameList = (List<String>)obj[1];		
								for(String flashName :flashNameList){
									filePathList.add(flashName);
								}
								//删除影音
								List<String> mediaNameList = (List<String>)obj[2];		
								for(String mediaName :mediaNameList){
									filePathList.add(mediaName);
								}
								//删除文件
								List<String> fileNameList = (List<String>)obj[3];		
								for(String fileName :fileNameList){
									filePathList.add(fileName);
								}
	
								
								for(String filePath :filePathList){
									
									
									 //如果验证不是当前用户上传的文件，则不删除
									 if(!topicManage.getFileNumber(fileManage.getBaseName(filePath.trim())).equals(fileNumber)){
										 continue;
									 }
									
									//替换路径中的..号
									filePath = fileManage.toRelativePath(filePath);
									
									//删除旧路径文件
									Boolean state = fileManage.deleteFile(filePath);
									if(state != null && state == false){
										 //替换指定的字符，只替换第一次出现的
										filePath = StringUtils.replaceOnce(filePath, "file/topic/", "");
										//创建删除失败文件
										fileManage.failedStateFile("file"+File.separator+"topic"+File.separator+"lock"+File.separator+filePath.replaceAll("/","_"));
									}
								}
								
								//清空目录
								Boolean state_ = fileManage.removeDirectory("file"+File.separator+"comment"+File.separator+topic.getId()+File.separator);
								if(state_ != null && state_ == false){
									//创建删除失败目录文件
									fileManage.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+"#"+topic.getId());
								}
								
							}
						}
						
						
					}	
					return"1";	
				}
			}
		}
		return"0";
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
		if(topicId != null && topicId.length>0){
			
			List<Topic> topicList = topicService.findByIdList(Arrays.asList(topicId));
			if(topicList != null && topicList.size() >0){
				topicService.reductionTopic(topicList);
				
				for(Topic topic :topicList){
					//更新索引
					topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),2));
					topicManage.deleteTopicCache(topic.getId());//删除缓存
				}
		
				return "1";
			}	
		}
		return "0";
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
		if(topicId != null && topicId>0L){
			topicService.updateTopicStatus(topicId, 20);
			//更新索引
			topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topicId),2));
			topicManage.deleteTopicCache(topicId);//删除缓存
			return "1";
		}
		return "0";
	}
	
	
	
	/**
	 * 搜索话题分页
	 * @param searchName 搜索名称
	 * @param tableName div表名
	 * @return
	 
	@RequestMapping(params="method=ajax_searchInformationPage", method=RequestMethod.GET)
	public String searchInformationPage(ModelMap model,PageForm pageForm,
			String searchName,String tableName) {
		
			
		StringBuffer jpql = new StringBuffer("");
		String sql = "";
		List<Object> params = new ArrayList<Object>();
		//调用分页算法代码
		PageView<Information> pageView = new PageView<Information>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		if(searchName != null && !"".equals(searchName.trim())){
			String searchName_utf8 = "";
			try {
				searchName_utf8 = URLDecoder.decode(searchName.trim(),"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("搜索资讯名称编码错误",e);
		        }
			}
			jpql.append(" and o.name like ?").append((params.size()+1)).append(" escape '/' ");
			params.add("%/"+ searchName_utf8.trim()+"%" );//加上查询参数

			model.addAttribute("searchName", searchName_utf8);
		}
	
		jpql.append(" and o.visible=?").append((params.size()+1));//and o.code=?1
		params.add(true);//设置o.visible=?1是否可见
		//删除第一个and
		sql = StringUtils.difference(" and", jpql.toString());
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
	//	orderby.put("sell", "desc");//先按是否在售排序
	//	orderby.put("id", "desc");//根据code字段降序排序
		QueryResult<Information> qr = informationService.getScrollData(Information.class,firstindex, pageView.getMaxresult(), sql, params.toArray(),orderby);
		
		
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		model.addAttribute("pageView", pageView);
		
		model.addAttribute("tableName", tableName);
		
		return "jsp/information/ajax_searchInformationPage";
		
		
	}*/
}
