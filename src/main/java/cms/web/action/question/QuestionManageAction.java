package cms.web.action.question;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.question.AppendQuestionItem;
import cms.bean.question.Question;
import cms.bean.question.QuestionIndex;
import cms.bean.question.QuestionTag;
import cms.bean.question.QuestionTagAssociation;
import cms.bean.staff.SysUsers;
import cms.bean.user.User;
import cms.service.question.AnswerService;
import cms.service.question.QuestionIndexService;
import cms.service.question.QuestionService;
import cms.service.question.QuestionTagService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.FileType;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.RedirectPath;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.FileManage;
import cms.web.action.SystemException;
import cms.web.action.TextFilterManage;
import cms.web.action.user.UserManage;


/**
 * 问题管理
 *
 */
@Controller
@RequestMapping("/control/question/manage") 
public class QuestionManageAction {
	 
	@Resource QuestionService questionService; 
	@Resource SettingService settingService;
	@Resource AnswerManage answerManage;
	@Resource AnswerService answerService;
	@Resource TextFilterManage textFilterManage;
	@Resource FileManage fileManage;
	@Resource QuestionManage questionManage;
	@Resource QuestionTagService questionTagService;
	@Resource QuestionTagManage questionTagManage;
	@Resource UserManage userManage;
	@Resource UserService userService;
	@Resource QuestionIndexService questionIndexService;
	
	/**
	 * 问题   查看
	 * @param questionId
	 * @param answerId
	 * @param model
	 * @param pageForm
	 * @param origin 来源 1.问题列表  2.全部待审核问题 3.全部待审核答案 4.全部待审核回复
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=view",method=RequestMethod.GET)
	public String view(Long questionId,Long answerId,ModelMap model,Integer page,Integer origin,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if(questionId != null && questionId >0L){
			Question question = questionService.findById(questionId);
			if(question == null){
				throw new SystemException("问题不存在");
			}else{
				if(question.getIp() != null && !"".equals(question.getIp().trim())){
					question.setIpAddress(IpAddress.queryAddress(question.getIp().trim()));
				}

				//删除最后一个逗号
				String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

				List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
				question.setAppendQuestionItemList(appendQuestionItemList);
				
				
				model.addAttribute("question", question);
				
				model.addAttribute("availableTag", answerManage.availableTag());
				
				PageForm pageForm = new PageForm();
				pageForm.setPage(page);
				
				if(answerId != null && answerId >0L && page == null){
					Long row = answerService.findRowByAnswerId(answerId,questionId);
					if(row != null && row >0L){
							
						Integer _page = Integer.parseInt(String.valueOf(row))/settingService.findSystemSetting_cache().getBackstagePageNumber();
						if(Integer.parseInt(String.valueOf(row))%settingService.findSystemSetting_cache().getBackstagePageNumber() >0){//余数大于0要加1
							
							_page = _page+1;
						}
						pageForm.setPage(_page);
						
					}
				}
				
				
				
				
				
				//答案
				StringBuffer jpql = new StringBuffer("");
				//存放参数值
				List<Object> params = new ArrayList<Object>();
				PageView<Answer> pageView = new PageView<Answer>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
				
				
				
				
				//当前页
				int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
				//排序
				LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

				if(questionId != null && questionId >0L){
					jpql.append(" o.questionId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
					params.add(questionId);//设置o.parentId=?2参数
				}

				orderby.put("postTime", "asc");//根据sort字段降序排序
				QueryResult<Answer> qr = answerService.getScrollData(Answer.class,firstindex, pageView.getMaxresult(),jpql.toString(),params.toArray(),orderby);
				
				
				List<Long> answerIdList = new ArrayList<Long>();
				List<Answer> answerList = qr.getResultlist();
				
				
				if(answerList != null && answerList.size() >0){
					for(Answer answer : answerList){
						answerIdList.add(answer.getId());
					}
				}
				
				
				if(answerIdList != null && answerIdList.size() >0){
					List<AnswerReply> answerReplyList = answerService.findReplyByAnswerId(answerIdList);
					if(answerReplyList != null && answerReplyList.size() >0){
						for(Answer answer : answerList){
							for(AnswerReply answerReply : answerReplyList){
								if(answer.getId().equals(answerReply.getAnswerId())){
									answer.addAnswerReply(answerReply);
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
		return "jsp/question/view_question";
	}
	
	/**
	 * 问题   添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Question question,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String username = "";//用户名称
		
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof UserDetails){
			username =((UserDetails)obj).getUsername();	
		}
		model.addAttribute("userName", username);
		return "jsp/question/add_question";
	}
	
	/**
	 * 问题  添加
	 * isQuestionList 上一链接是否来自问题列表
	 */
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Long[] tagId,String tagName, String title,Boolean allow,Integer status,
			String content,String sort,Boolean isQuestionList,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Question question = new Question();
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
		
		question.setAllow(allow);
		question.setStatus(status);
		Date d = new Date();
		question.setPostTime(d);
		question.setLastAnswerTime(d);
		
		
		LinkedHashSet <QuestionTagAssociation> questionTagAssociationList = new LinkedHashSet<QuestionTagAssociation>();
		if(tagId != null && tagId.length >0){
			List<QuestionTag> questionTagList =  questionTagService.findAllQuestionTag();
			for(Long id :tagId){
				if(id != null && id >0L){
					QuestionTagAssociation questionTagAssociation = new QuestionTagAssociation();
					questionTagAssociation.setQuestionTagId(id);
					for(QuestionTag questionTag : questionTagList){
						if(questionTag.getId().equals(id) && questionTag.getChildNodeNumber().equals(0)){
							questionTagAssociation.setQuestionTagName(questionTag.getName());
							questionTagAssociation.setUserName(username);
							questionTagAssociationList.add(questionTagAssociation);
							break;
						}
						
					}	
				}
			}
		}else{
			error.put("tagId", "标签不能为空");
		}
		
		
		if(title != null && !"".equals(title.trim())){
			question.setTitle(title);
			if(title.length() >150){
				error.put("title", "不能大于150个字符");
			}
		}else{
			error.put("title", "标题不能为空");
		}
		if(content != null && !"".equals(content.trim())){
			//过滤标签
			content = textFilterManage.filterTag(request,content);
			Object[] object = textFilterManage.filterHtml(request,content,"question",null);
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
		
			
			
			//不含标签内容
			String text = textFilterManage.filterText(value);
			//清除空格&nbsp;
			String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
			//摘要
			if(trimSpace != null && !"".equals(trimSpace)){
				if(trimSpace.length() >180){
					question.setSummary(trimSpace.substring(0, 180)+"..");
				}else{
					question.setSummary(trimSpace+"..");
				}
			}
			
			//不含标签内容
			String source_text = textFilterManage.filterText(content);
			//清除空格&nbsp;
			String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();
			
			if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(source_text.trim()) && !"".equals(source_trimSpace))){
				
				question.setIp(IpAddress.getClientIpAddress(request));
				question.setUserName(username);
				question.setIsStaff(true);
				question.setContent(value);
			}else{
				error.put("content", "问题内容不能为空");
			}	
		}else{
			error.put("content", "问题内容不能为空");
		}
		
		
		
		
		if(sort != null){
			if(Verification.isNumeric(sort.toString())){
				if(sort.toString().length() <=8){
					question.setSort(Integer.parseInt(sort.toString()));
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
			model.addAttribute("questionTagAssociationList", questionTagAssociationList);
			
			
			
			
			
		}else{
			questionService.saveQuestion(question,new ArrayList<QuestionTagAssociation>(questionTagAssociationList));
			
			//更新索引
			questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),1));
			
			
			
			//上传文件编号
			String fileNumber = "a"+userId;
			
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
			
					 if(imageName != null && !"".equals(imageName.trim())){
		 
						 //如果验证不是当前用户上传的文件，则不删除锁
						 if(!questionManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
							 continue;
						 }
						 
						 fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
					
					 }
				}
			}
			//falsh
			if(flashNameList != null && flashNameList.size() >0){
				for(String flashName :flashNameList){
					 if(flashName != null && !"".equals(flashName.trim())){
						 //如果验证不是当前用户上传的文件，则不删除锁
						 if(!questionManage.getFileNumber(fileManage.getBaseName(flashName.trim())).equals(fileNumber)){
							 continue;
						 }
						 
						 fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
					
					 }
				}
			}
			//音视频
			if(mediaNameList != null && mediaNameList.size() >0){
				for(String mediaName :mediaNameList){
					if(mediaName != null && !"".equals(mediaName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!questionManage.getFileNumber(fileManage.getBaseName(mediaName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
					
					}
				}
			}
			//文件
			if(fileNameList != null && fileNameList.size() >0){
				for(String fileName :fileNameList){
					if(fileName != null && !"".equals(fileName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!questionManage.getFileNumber(fileManage.getBaseName(fileName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
					
					}
				}
			}
			
			
			model.addAttribute("message","添加问题成功");//返回消息
			if(isQuestionList != null && isQuestionList == true){
				model.addAttribute("urladdress", RedirectPath.readUrl("control.question.list")+"?visible=true");
			}else{
				model.addAttribute("urladdress", RedirectPath.readUrl("control.question.manage")+"?method=list&visible=true&tagId="+tagId);
			}
			
			return "jsp/common/message";
			
		}
		question.setTitle(title);
		question.setContent(content);
		model.addAttribute("question",question);
		model.addAttribute("userName", username);
		return "jsp/question/add_question";
	}
	
	
	/**
	 * 问题   追加界面显示
	 */
	@RequestMapping(params="method=appendQuestion",method=RequestMethod.GET)
	public String appendQuestionUI(Long questionId,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(questionId != null && questionId >0L){
			Question question = questionService.findById(questionId);
			if(question != null){
				model.addAttribute("question", question);
			}else{
				throw new SystemException("问题不存在");
			}	
			
		}
		return "jsp/question/add_appendQuestion";
	}
	
	
	
	/**
	 * 问题  追加
	 * @param model
	 * @param questionId 问题Id
	 * @param content 追加内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody//方式来做ajax,直接返回字符串
	@RequestMapping(params="method=appendQuestion", method=RequestMethod.POST)
	public String appendQuestion(ModelMap model,Long questionId,String content,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

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
	
		String appendContent = "";
		Question question = null;
		if(questionId != null && questionId >0L){
			question = questionService.findById(questionId);
			if(question != null){
				if(content != null && !"".equals(content.trim())){
					//过滤标签
					content = textFilterManage.filterTag(request,content);
					Object[] object = textFilterManage.filterHtml(request,content,"question",null);
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
				
					
					
					//不含标签内容
					String source_text = textFilterManage.filterText(content);
					//清除空格&nbsp;
					String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();
					
					if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(source_text.trim()) && !"".equals(source_trimSpace))){
						
						appendContent = value;
					}else{
						error.put("content", "问题内容不能为空");
					}	
				}else{
					error.put("content", "问题内容不能为空");
				}
				
			}else{
				error.put("content", "问题不存在");
			}
		}else{
			error.put("content", "问题Id不能为空");
		}
		
		String appendContent_json = "";
		if(appendContent != null && !"".equals(appendContent.trim())){
			AppendQuestionItem appendQuestionItem = new AppendQuestionItem();
			appendQuestionItem.setId(UUIDUtil.getUUID32());
			appendQuestionItem.setContent(appendContent.trim());
			appendQuestionItem.setPostTime(new Date());
			appendContent_json = JsonUtils.toJSONString(appendQuestionItem);
		}else{
			error.put("content", "追加内容不能为空");
		}
		
		
		if(error != null && error.size() >0){
			model.addAttribute("error", error);	
			
		}else{

			//追加问题
			questionService.saveAppendQuestion(questionId, appendContent_json+",");
			//更新索引
			questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));

			//删除缓存
			questionManage.delete_cache_findById(questionId);//删除问题缓存
			
			//上传文件编号
			String fileNumber = questionManage.generateFileNumber(question.getUserName(), question.getIsStaff());
			
			
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
			
					 if(imageName != null && !"".equals(imageName.trim())){
		 
						 //如果验证不是当前用户上传的文件，则不删除锁
						 if(!questionManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
							 continue;
						 }
						 
						 fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
					
					 }
				}
			}
			//falsh
			if(flashNameList != null && flashNameList.size() >0){
				for(String flashName :flashNameList){
					 if(flashName != null && !"".equals(flashName.trim())){
						 //如果验证不是当前用户上传的文件，则不删除锁
						 if(!questionManage.getFileNumber(fileManage.getBaseName(flashName.trim())).equals(fileNumber)){
							 continue;
						 }
						 
						 fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
					
					 }
				}
			}
			//音视频
			if(mediaNameList != null && mediaNameList.size() >0){
				for(String mediaName :mediaNameList){
					if(mediaName != null && !"".equals(mediaName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!questionManage.getFileNumber(fileManage.getBaseName(mediaName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
					
					}
				}
			}
			//文件
			if(fileNameList != null && fileNameList.size() >0){
				for(String fileName :fileNameList){
					if(fileName != null && !"".equals(fileName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!questionManage.getFileNumber(fileManage.getBaseName(fileName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
					
					}
				}
			}
			
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
	 *  文件上传
	 * 
	 * 员工发问题 上传文件名为UUID + a + 员工Id
	 * 用户发问题 上传文件名为UUID + b + 用户Id
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
		
		String number = questionManage.generateFileNumber(userName, isStaff);
		
		
		
		
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
						String pathDir = "file"+File.separator+"question"+File.separator + date +File.separator +"image"+ File.separator;
						//文件锁目录
						String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;
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
						returnJson.put("url", "file/question/"+date+"/image/"+newFileName);
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
						String pathDir = "file"+File.separator+"question"+File.separator + date+ File.separator +"flash"+ File.separator;
						//文件锁目录
						String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;
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
						returnJson.put("url", "file/question/"+date+"/flash/"+newFileName);
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
						String pathDir = "file"+File.separator+"question"+File.separator + date+ File.separator +"media"+ File.separator;
						//文件锁目录
						String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;
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
						returnJson.put("url", "file/question/"+date+"/media/"+newFileName);
						return JsonUtils.toJSONString(returnJson);
					}
				}else if(dir.equals("file")){
					//允许上传文件格式
					List<String> formatList = fileManage.readRichTextAllowFileUploadFormat();
					
					//验证文件后缀
					boolean authentication = fileManage.validateFileSuffix(imgFile.getOriginalFilename(),formatList);
					if(authentication){
						
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"question"+File.separator + date+ File.separator +"file"+ File.separator;
						//文件锁目录
						String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;
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
						returnJson.put("url", "file/question/"+date+"/file/"+newFileName);
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
	 * 问题   修改界面显示
	 * 
	 */
	@RequestMapping(params="method=editQuestion", method=RequestMethod.GET)
	public String editQuestionUI(ModelMap model,Long questionId,Boolean visible,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(questionId != null && questionId >0L){
			Question question = questionService.findById(questionId);
			if(question != null){
				List<QuestionTag> questionTagList = questionTagService.findAllQuestionTag();
				if(questionTagList != null && questionTagList.size() >0){
					
					List<QuestionTagAssociation> questionTagAssociationList = questionService.findQuestionTagAssociationByQuestionId(question.getId());
					if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
						for(QuestionTag questionTag : questionTagList){
							for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
								if(questionTagAssociation.getQuestionTagId().equals(questionTag.getId())){
									questionTagAssociation.setQuestionTagName(questionTag.getName());
									question.addQuestionTagAssociation(questionTagAssociation);
									break;
								}
							}
						}
					}
					
				}
			}else{
				throw new SystemException("问题不存在");
			}	
			model.addAttribute("question", question);
		}
		return "jsp/question/edit_question";
	}
	/**
	 * 问题   修改
	 * @param model
	 * @param pageForm
	 * @param tagId
	 * @param title
	 * @param content
	 * @param sort 
	 * @param visible
	 * @param isQuestionList 上一链接是否来自问题列表
	 */
	@ResponseBody//方式来做ajax,直接返回字符串
	@RequestMapping(params="method=editQuestion", method=RequestMethod.POST)
	public String editQuestion(ModelMap model,Long questionId,Long[] tagId,
			String title,Boolean allow,Integer status,
			String content,String sort,Boolean visible,Boolean isQuestionList,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Question question = null;
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
		
		List<String> oldPathFileList = new ArrayList<String>();//旧路径文件
		//旧状态
		Integer old_status = -1;
		
		String old_content = "";
		if(questionId != null && questionId >0L){
			question = questionService.findById(questionId);
			if(question != null){
				old_status = question.getStatus();
				question.setAllow(allow);
				if(question.getStatus() < 100){
					question.setStatus(status);
				}

				old_content = question.getContent();
				if(title != null && !"".equals(title.trim())){
					question.setTitle(title);
					if(title.length() >150){
						error.put("title", "不能大于150个字符");
					}
				}else{
					error.put("title", "标题不能为空");
				}
				
				LinkedHashSet <QuestionTagAssociation> questionTagAssociationList = new LinkedHashSet<QuestionTagAssociation>();
				if(tagId != null && tagId.length >0){
					List<QuestionTag> questionTagList =  questionTagService.findAllQuestionTag();
					for(Long id :tagId){
						if(id != null && id >0L){
							QuestionTagAssociation questionTagAssociation = new QuestionTagAssociation();
							questionTagAssociation.setQuestionTagId(id);
							for(QuestionTag questionTag : questionTagList){
								if(questionTag.getId().equals(id) && questionTag.getChildNodeNumber().equals(0)){
									questionTagAssociation.setQuestionTagName(questionTag.getName());
									questionTagAssociation.setUserName(question.getUserName());
									questionTagAssociationList.add(questionTagAssociation);
									break;
								}
								
							}	
						}
					}
				}else{
					error.put("tagId", "标签不能为空");
				}
				
				if(content != null && !"".equals(content.trim())){
					
					//过滤标签
					content = textFilterManage.filterTag(request,content);
					Object[] object = textFilterManage.filterHtml(request,content,"question",null);
			
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

					
					//不含标签内容
					String text = textFilterManage.filterText(value);
					
					
					//清除空格&nbsp;
					String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
					
					
					//摘要
					if(trimSpace != null && !"".equals(trimSpace)){
						if(trimSpace.length() >180){
							question.setSummary(trimSpace.substring(0, 180)+"..");
						}else{
							question.setSummary(trimSpace+"..");
						}
					}
					
					//不含标签内容
					String source_text = textFilterManage.filterText(content);
					//清除空格&nbsp;
					String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();
					if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(source_text.trim()) && !"".equals(source_trimSpace))){
						question.setContent(value);
					}else{
						error.put("content", "问题内容不能为空");
					}	
				}else{
					error.put("content", "问题内容不能为空");
				}
				
				if(sort != null){
					if(Verification.isNumeric(sort.toString())){
						if(sort.toString().length() <=8){
							question.setSort(Integer.parseInt(sort.toString()));
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
					question.setLastUpdateTime(new Date());//最后修改时间
					int i = questionService.updateQuestion(question,new ArrayList<QuestionTagAssociation>(questionTagAssociationList));
					//更新索引
					questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));
					
					if(i >0 && question.getStatus() < 100 && !old_status.equals(status)){
						User user = userManage.query_cache_findUserByUserName(question.getUserName());
						if(user != null){
							//修改用户动态问题状态
							userService.updateUserDynamicQuestionStatus(user.getId(),question.getUserName(),question.getId(),question.getStatus());
						}
						
					}
					 
					
					//删除缓存
					questionManage.delete_cache_findById(question.getId());//删除问题缓存
					questionManage.delete_cache_findQuestionTagAssociationByQuestionId(question.getId());//删除'根据问题Id查询问题标签关联'缓存
					
					Object[] obj = textFilterManage.readPathName(old_content,"question");
					if(obj != null && obj.length >0){
						//旧图片
						List<String> old_imageNameList = (List<String>)obj[0];
						
						if(old_imageNameList != null && old_imageNameList.size() >0){
							
					        Iterator<String> iter = old_imageNameList.iterator();
					        while (iter.hasNext()) {
					        	String imageName = iter.next();  
					        	
								for(String new_imageName : imageNameList){
									if(imageName.equals("file/question/"+new_imageName)){
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
									if(flashName.equals("file/question/"+new_flashName)){
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
									if(mediaName.equals("file/question/"+new_mediaName)){
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
									if(fileName.equals("file/question/"+new_fileName)){
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
					
					
					//上传文件编号
					String fileNumber = questionManage.generateFileNumber(question.getUserName(), question.getIsStaff());
					
					//删除图片锁
					if(imageNameList != null && imageNameList.size() >0){
						for(String imageName :imageNameList){
					
							 if(imageName != null && !"".equals(imageName.trim())){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!questionManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
										continue;
								 }
								 fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
			
							 }
						}
					}
					//删除Falsh锁
					if(flashNameList != null && flashNameList.size() >0){
						for(String flashName :flashNameList){
							 
							 if(flashName != null && !"".equals(flashName.trim())){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!questionManage.getFileNumber(fileManage.getBaseName(flashName.trim())).equals(fileNumber)){
										continue;
								 }
								 fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
				
							 }
						}
					}
					//删除音视频锁
					if(mediaNameList != null && mediaNameList.size() >0){
						for(String mediaName :mediaNameList){
							if(mediaName != null && !"".equals(mediaName.trim())){
								//如果验证不是当前用户上传的文件，则不删除
								if(!questionManage.getFileNumber(fileManage.getBaseName(mediaName.trim())).equals(fileNumber)){
									continue;
								}
								fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
							
							}
						}
					}
					//删除文件锁
					if(fileNameList != null && fileNameList.size() >0){
						for(String fileName :fileNameList){
							if(fileName != null && !"".equals(fileName.trim())){
								//如果验证不是当前用户上传的文件，则不删除
								if(!questionManage.getFileNumber(fileManage.getBaseName(fileName.trim())).equals(fileNumber)){
									continue;
								}
								fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
							
							}
						}
					}
					
					//删除旧路径文件
					if(oldPathFileList != null && oldPathFileList.size() >0){
						for(String oldPathFile :oldPathFileList){
							//如果验证不是当前用户上传的文件，则不删除
							if(!questionManage.getFileNumber(fileManage.getBaseName(oldPathFile.trim())).equals(fileNumber)){
								continue;
							}
							
							
							//替换路径中的..号
							oldPathFile = fileManage.toRelativePath(oldPathFile);
							
							//删除旧路径文件
							Boolean state = fileManage.deleteFile(oldPathFile);
							if(state != null && state == false){

								//替换指定的字符，只替换第一次出现的
								oldPathFile = StringUtils.replaceOnce(oldPathFile, "file"+File.separator+"question"+File.separator, "");
								oldPathFile = StringUtils.replace(oldPathFile, File.separator, "_");//替换所有出现过的字符
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+oldPathFile);
							}
						}
					}
					
					
					
					
				}
				
			}else{
				error.put("question", "问题不存在");
			}
		}else{
			error.put("question", "Id不存在");
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
	 * 问题   修改追加界面显示
	 */
	@RequestMapping(params="method=editAppendQuestion",method=RequestMethod.GET)
	public String editAppendQuestionUI(Long questionId,String appendQuestionItemId,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(questionId != null && questionId >0L){
			Question question = questionService.findById(questionId);
			if(question != null){
				//删除最后一个逗号
				String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

				List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
				if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
					for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
						if(appendQuestionItem.getId().equals(appendQuestionItemId)){
							model.addAttribute("appendQuestionItem", appendQuestionItem);
							break;
						}
					}
				}

				model.addAttribute("question", question);
			}else{
				throw new SystemException("问题不存在");
			}	
			
		}
		return "jsp/question/edit_appendQuestion";
	}
	
	
	/**
	 * 问题   追加修改
	 * @param model
	 * @param questionId
	 * @param appendQuestionItemId
	 * @param content
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody//方式来做ajax,直接返回字符串
	@RequestMapping(params="method=editAppendQuestion", method=RequestMethod.POST)
	public String editAppendQuestion(ModelMap model,Long questionId,String appendQuestionItemId, String content,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Question question = null;
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
		
		List<String> oldPathFileList = new ArrayList<String>();//旧路径文件
		
		String old_content = "";
		String appendContent = "";
		if(questionId != null && questionId >0L){
			question = questionService.findById(questionId);
			if(question != null){
				//删除最后一个逗号
				String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

				List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
				
				boolean flag = false;
				if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
					for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
						if(appendQuestionItem.getId().equals(appendQuestionItemId)){
							old_content = appendQuestionItem.getContent();
							flag = true;
							break;
						}
					}
				}
				if(flag == false){
					error.put("question", "追加问题不存在");
				}
				
				
				if(content != null && !"".equals(content.trim())){
					
					//过滤标签
					content = textFilterManage.filterTag(request,content);
					Object[] object = textFilterManage.filterHtml(request,content,"question",null);
			
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

					//不含标签内容
					String source_text = textFilterManage.filterText(content);
					//清除空格&nbsp;
					String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();
					if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(source_text.trim()) && !"".equals(source_trimSpace))){
						
						appendContent = value;
					}else{
						error.put("content", "问题内容不能为空");
					}	
				}else{
					error.put("content", "问题内容不能为空");
				}

				if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
					for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
						if(appendQuestionItem.getId().equals(appendQuestionItemId)){
							appendQuestionItem.setContent(appendContent);
						}
					}
				}
				
				
				String appendContent_json = JsonUtils.toJSONString(appendQuestionItemList);
				//删除最后一个中括号
				appendContent_json = StringUtils.substringBeforeLast(appendContent_json, "]");//从右往左截取到相等的字符,保留左边的

					
				
				
				if(error.size() ==0){
					
					
					int i = questionService.updateAppendQuestion(questionId,appendContent_json+",");
					//更新索引
					questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));

					//删除缓存
					questionManage.delete_cache_findById(question.getId());//删除问题缓存
					
					Object[] obj = textFilterManage.readPathName(old_content,"question");
					if(obj != null && obj.length >0){
						//旧图片
						List<String> old_imageNameList = (List<String>)obj[0];
						
						if(old_imageNameList != null && old_imageNameList.size() >0){
							
					        Iterator<String> iter = old_imageNameList.iterator();
					        while (iter.hasNext()) {
					        	String imageName = iter.next();  
					        	
								for(String new_imageName : imageNameList){
									if(imageName.equals("file/question/"+new_imageName)){
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
									if(flashName.equals("file/question/"+new_flashName)){
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
									if(mediaName.equals("file/question/"+new_mediaName)){
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
									if(fileName.equals("file/question/"+new_fileName)){
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
					
					
					//上传文件编号
					String fileNumber = questionManage.generateFileNumber(question.getUserName(), question.getIsStaff());
					
					//删除图片锁
					if(imageNameList != null && imageNameList.size() >0){
						for(String imageName :imageNameList){
					
							 if(imageName != null && !"".equals(imageName.trim())){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!questionManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
										continue;
								 }
								 fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
			
							 }
						}
					}
					//删除Falsh锁
					if(flashNameList != null && flashNameList.size() >0){
						for(String flashName :flashNameList){
							 
							 if(flashName != null && !"".equals(flashName.trim())){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!questionManage.getFileNumber(fileManage.getBaseName(flashName.trim())).equals(fileNumber)){
										continue;
								 }
								 fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
				
							 }
						}
					}
					//删除音视频锁
					if(mediaNameList != null && mediaNameList.size() >0){
						for(String mediaName :mediaNameList){
							if(mediaName != null && !"".equals(mediaName.trim())){
								//如果验证不是当前用户上传的文件，则不删除
								if(!questionManage.getFileNumber(fileManage.getBaseName(mediaName.trim())).equals(fileNumber)){
									continue;
								}
								fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
							
							}
						}
					}
					//删除文件锁
					if(fileNameList != null && fileNameList.size() >0){
						for(String fileName :fileNameList){
							if(fileName != null && !"".equals(fileName.trim())){
								//如果验证不是当前用户上传的文件，则不删除
								if(!questionManage.getFileNumber(fileManage.getBaseName(fileName.trim())).equals(fileNumber)){
									continue;
								}
								fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
							
							}
						}
					}
					
					//删除旧路径文件
					if(oldPathFileList != null && oldPathFileList.size() >0){
						for(String oldPathFile :oldPathFileList){
							//如果验证不是当前用户上传的文件，则不删除
							if(!questionManage.getFileNumber(fileManage.getBaseName(oldPathFile.trim())).equals(fileNumber)){
								continue;
							}
							
							
							//替换路径中的..号
							oldPathFile = fileManage.toRelativePath(oldPathFile);
							
							//删除旧路径文件
							Boolean state = fileManage.deleteFile(oldPathFile);
							if(state != null && state == false){

								//替换指定的字符，只替换第一次出现的
								oldPathFile = StringUtils.replaceOnce(oldPathFile, "file"+File.separator+"question"+File.separator, "");
								oldPathFile = StringUtils.replace(oldPathFile, File.separator, "_");//替换所有出现过的字符
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+oldPathFile);
							}
						}
					}
					
					
					
					
				}
				
			}else{
				error.put("question", "问题不存在");
			}
		}else{
			error.put("question", "Id不存在");
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
	 * 问题   删除
	 * @param model
	 * @param questionId
	*/
	@RequestMapping(params="method=deleteQuestion", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String deleteQuestion(ModelMap model,Long[] questionId,
			Boolean isQuestionList,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if(questionId != null && questionId.length >0){
			List<Long> questionIdList = new ArrayList<Long>();
			for(Long l :questionId){
				if(l != null && l >0L){
					questionIdList .add(l);
				}
			}
			if(questionIdList != null && questionIdList.size() >0){

				List<Question> questionList = questionService.findByIdList(questionIdList);
				if(questionList != null && questionList.size() >0){
					for(Question question : questionList){
						if(question.getStatus() < 100){//标记删除
							int i = questionService.markDelete(question.getId());

							User user = userManage.query_cache_findUserByUserName(question.getUserName());
							if(i >0 && user != null){
								//修改问题状态
								userService.softDeleteUserDynamicByQuestionId(user.getId(),question.getUserName(),question.getId());
							}
							//更新索引
							questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));
							questionManage.delete_cache_findById(question.getId());//删除缓存
							questionManage.delete_cache_findQuestionTagAssociationByQuestionId(question.getId());//删除'根据问题Id查询问题标签关联'缓存
						}else{//物理删除
							
							String fileNumber = questionManage.generateFileNumber(question.getUserName(), question.getIsStaff());
							
							int i = questionService.deleteQuestion(question.getId());
							
							if(i>0){
								//根据问题Id删除用户动态(问题下的评论和回复也同时删除)
								userService.deleteUserDynamicByQuestionId(question.getId());
							}
							
							questionManage.delete_cache_findById(question.getId());//删除缓存
							questionManage.delete_cache_findQuestionTagAssociationByQuestionId(question.getId());//删除'根据问题Id查询问题标签关联'缓存
							//更新索引
							questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),3));
							
							
							
							
							
							String questionContent = question.getContent();
							
							//删除最后一个逗号
							String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

							List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
							if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
								for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
									questionContent += appendQuestionItem.getContent();
								}
							}
							
							
							Object[] obj = textFilterManage.readPathName(questionContent,"question");
							if(obj != null && obj.length >0){
								List<String> filePathList = new ArrayList<String>();
								
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
									 if(!questionManage.getFileNumber(fileManage.getBaseName(filePath.trim())).equals(fileNumber)){
										 continue;
									 }
									
									//替换路径中的..号
									filePath = fileManage.toRelativePath(filePath);
									
									//删除旧路径文件
									Boolean state = fileManage.deleteFile(filePath);
									if(state != null && state == false){
										 //替换指定的字符，只替换第一次出现的
										filePath = StringUtils.replaceOnce(filePath, "file/question/", "");
										//创建删除失败文件
										fileManage.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+filePath.replaceAll("/","_"));
									}
								}
								
								//清空目录
								Boolean state_ = fileManage.removeDirectory("file"+File.separator+"answer"+File.separator+question.getId()+File.separator);
								if(state_ != null && state_ == false){
									//创建删除失败目录文件
									fileManage.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+"#"+question.getId());
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
	 * 追加问题   删除
	 * @param model
	 * @param questionId
	 * @param appendQuestionItemId
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=deleteAppendQuestion", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String deleteAppendQuestion(ModelMap model,Long questionId,String appendQuestionItemId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if(questionId != null && questionId >0 && appendQuestionItemId != null && !"".equals(appendQuestionItemId.trim())){
			
		
			Question question = questionService.findById(questionId);
			if(question != null){
				boolean flag = false;
				String old_content = "";
				//删除最后一个逗号
				String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的
	
				List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
			
				if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
					
					Iterator<AppendQuestionItem> iter = appendQuestionItemList.iterator();  
					while (iter.hasNext()) {  
						AppendQuestionItem appendQuestionItem = iter.next();  
						if(appendQuestionItem.getId().equals(appendQuestionItemId)){
							old_content = appendQuestionItem.getContent();
							flag = true;
							iter.remove();
							break;
						}
					}  
				}
				
				
				
				
				String appendContent_json = JsonUtils.toJSONString(appendQuestionItemList);
				//删除最后一个中括号
				appendContent_json = StringUtils.substringBeforeLast(appendContent_json, "]");//从右往左截取到相等的字符,保留左边的
	
				if(appendQuestionItemList.size() >0){
					appendContent_json += ",";
				}
				if(flag){
					String fileNumber = questionManage.generateFileNumber(question.getUserName(), question.getIsStaff());
					
					int i = questionService.updateAppendQuestion(questionId, appendContent_json);

					
					questionManage.delete_cache_findById(question.getId());//删除缓存
					//更新索引
					questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));
					Object[] obj = textFilterManage.readPathName(old_content,"question");
					if(obj != null && obj.length >0){
						List<String> filePathList = new ArrayList<String>();
						
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
							 if(!questionManage.getFileNumber(fileManage.getBaseName(filePath.trim())).equals(fileNumber)){
								 continue;
							 }
							
							//替换路径中的..号
							filePath = fileManage.toRelativePath(filePath);
							
							//删除旧路径文件
							Boolean state = fileManage.deleteFile(filePath);
							if(state != null && state == false){
								 //替换指定的字符，只替换第一次出现的
								filePath = StringUtils.replaceOnce(filePath, "file/question/", "");
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+filePath.replaceAll("/","_"));
							}
						}
						
						//清空目录
						Boolean state_ = fileManage.removeDirectory("file"+File.separator+"answer"+File.separator+question.getId()+File.separator);
						if(state_ != null && state_ == false){
							//创建删除失败目录文件
							fileManage.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+"#"+question.getId());
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
	 * @param questionId 问题Id集合
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=reduction",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String reduction(ModelMap model,Long[] questionId,
			HttpServletResponse response) throws Exception {
		if(questionId != null && questionId.length>0){
			
			List<Question> questionList = questionService.findByIdList(Arrays.asList(questionId));
			if(questionList != null && questionList.size() >0){
				int i = questionService.reductionQuestion(questionList);
				
				for(Question question :questionList){
					
					User user = userManage.query_cache_findUserByUserName(question.getUserName());
					if(i >0 && user != null){
						//修改问题状态
						userService.reductionUserDynamicByQuestionId(user.getId(),question.getUserName(),question.getId());
					}
					
					//更新索引
					questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));
					questionManage.delete_cache_findById(question.getId());//删除缓存
				}
		
				return "1";
			}	
		}
		return "0";
	}
	
	/**
	 * 审核问题
	 * @param model
	 * @param questionId 问题Id
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=auditQuestion",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String auditQuestion(ModelMap model,Long questionId,
			HttpServletResponse response) throws Exception {
		if(questionId != null && questionId>0L){
			int i = questionService.updateQuestionStatus(questionId, 20);

			Question question = questionManage.query_cache_findById(questionId);
			if(i >0 && question != null){
				User user = userManage.query_cache_findUserByUserName(question.getUserName());
				if(user != null){
					//修改问题状态
					userService.updateUserDynamicQuestionStatus(user.getId(),question.getUserName(),question.getId(),20);
				}
			}

			//更新索引
			questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(questionId),2));
			questionManage.delete_cache_findById(questionId);//删除缓存
			return "1";
		}
		return "0";
	}
	
	

}
