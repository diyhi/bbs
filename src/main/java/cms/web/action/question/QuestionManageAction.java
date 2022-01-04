package cms.web.action.question;

import java.io.File;
import java.math.BigDecimal;
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
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.payment.PaymentLog;
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.question.AppendQuestionItem;
import cms.bean.question.Question;
import cms.bean.question.QuestionIndex;
import cms.bean.question.QuestionTag;
import cms.bean.question.QuestionTagAssociation;
import cms.bean.staff.SysUsers;
import cms.bean.user.PointLog;
import cms.bean.user.User;
import cms.service.question.AnswerService;
import cms.service.question.QuestionIndexService;
import cms.service.question.QuestionService;
import cms.service.question.QuestionTagService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.CommentedProperties;
import cms.utils.FileType;
import cms.utils.FileUtil;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.SystemException;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.membershipCard.MembershipCardGiftTaskManage;
import cms.web.action.payment.PaymentManage;
import cms.web.action.user.PointManage;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;


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
	@Resource QuestionManage questionManage;
	@Resource QuestionTagService questionTagService;
	@Resource QuestionTagManage questionTagManage;
	@Resource UserManage userManage;
	@Resource UserService userService;
	@Resource QuestionIndexService questionIndexService;
	@Resource FileManage fileManage;
	@Resource PointManage pointManage;
	@Resource PaymentManage paymentManage;
	@Resource UserRoleManage userRoleManage;
	@Resource MembershipCardGiftTaskManage membershipCardGiftTaskManage;
	
	/**
	 * 问题   查看
	 * @param model
	 * @param questionId
	 * @param answerId
	 * @param page
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=view",method=RequestMethod.GET)
	public String view(ModelMap model,Long questionId,Long answerId,Integer page,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
		
		if(questionId != null && questionId >0L){
			Question question = questionService.findById(questionId);
			if(question != null){
				if(question.getIp() != null && !"".equals(question.getIp().trim())){
					question.setIpAddress(IpAddress.queryAddress(question.getIp().trim()));
				}
				if(question.getContent() != null && !"".equals(question.getContent().trim())){
					//处理富文本路径
					question.setContent(fileManage.processRichTextFilePath(question.getContent(),"question"));
				}

				//删除最后一个逗号
				String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

				List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
				if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
					for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
						if(appendQuestionItem.getContent() != null && !"".equals(appendQuestionItem.getContent().trim())){
							//处理富文本路径
							appendQuestionItem.setContent(fileManage.processRichTextFilePath(appendQuestionItem.getContent(),"question"));
						}
					}
					
				}
				
				question.setAppendQuestionItemList(appendQuestionItemList);
				
			
				if(question.getIsStaff() == false){//会员
					User user = userManage.query_cache_findUserByUserName(question.getUserName());
					if(user != null){
						question.setAccount(user.getAccount());
						question.setNickname(user.getNickname());
						question.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
						question.setAvatarName(user.getAvatarName());
						
						List<String> userRoleNameList = userRoleManage.queryUserRoleName(user.getUserName());
						if(userRoleNameList != null && userRoleNameList.size() >0){
							question.setUserRoleNameList(userRoleNameList);//用户角色名称集合
						}
					}
					
				}else{
					question.setAccount(question.getUserName());//员工用户名和账号是同一个
					
				}
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
					question.setQuestionTagAssociationList(questionTagAssociationList);
				}
				
				returnValue.put("question", question);
				returnValue.put("availableTag", answerManage.availableTag());
				
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
						if(answer.getContent() != null && !"".equals(answer.getContent().trim())){
							//处理富文本路径
							answer.setContent(fileManage.processRichTextFilePath(answer.getContent(),"answer"));
						}
						
					}
				}
				
				Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id 角色名称集合
				if(answerList != null && answerList.size() >0){
					for(Answer answer : answerList){
						if(answer.getIsStaff() == false){//会员
							User user = userManage.query_cache_findUserByUserName(answer.getUserName());
							if(user != null){
								answer.setAccount(user.getAccount());
								answer.setNickname(user.getNickname());
								answer.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
								answer.setAvatarName(user.getAvatarName());
								userRoleNameMap.put(answer.getUserName(), null);
							}
						}else{
							answer.setAccount(answer.getUserName());//员工用户名和账号是同一个
							
						}
						
						if(answer.getIp() != null && !"".equals(answer.getIp().trim())){
							answer.setIpAddress(IpAddress.queryAddress(answer.getIp()));
						}
					}
					
				}
				if(answerIdList != null && answerIdList.size() >0){
					List<AnswerReply> answerReplyList = answerService.findReplyByAnswerId(answerIdList);
					if(answerReplyList != null && answerReplyList.size() >0){
						for(Answer answer : answerList){
							for(AnswerReply answerReply : answerReplyList){
								if(answerReply.getIsStaff() == false){//会员
									User user = userManage.query_cache_findUserByUserName(answerReply.getUserName());
									if(user != null){
										answerReply.setAccount(user.getAccount());
										answerReply.setNickname(user.getNickname());
										answerReply.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
										answerReply.setAvatarName(user.getAvatarName());
										userRoleNameMap.put(answerReply.getUserName(), null);
									}
									
								}else{
									answerReply.setAccount(answerReply.getUserName());//员工用户名和账号是同一个
									
								}
								if(answer.getId().equals(answerReply.getAnswerId())){
									answer.addAnswerReply(answerReply);
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
				if(answerList != null && answerList.size() >0){
					for(Answer answer : answerList){	
						//用户角色名称集合
						for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
							if(entry.getKey().equals(answer.getUserName())){
								List<String> roleNameList = entry.getValue();
								if(roleNameList != null && roleNameList.size() >0){
									answer.setUserRoleNameList(roleNameList);
								}
								break;
							}
						}
						
						if(answer.getAnswerReplyList() != null && answer.getAnswerReplyList().size() >0){
							for(AnswerReply reply : answer.getAnswerReplyList()){
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
				error.put("questionId", "问题不存在");
			}
		}else{
			error.put("questionId", "问题Id参数不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 问题   添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Question question,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		String username = "";//用户名称
		
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof UserDetails){
			username =((UserDetails)obj).getUsername();	
		}
		returnValue.put("userName", username);
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	
	/**
	 * 问题  添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Long[] tagId,String tagName, String title,Boolean allow,Integer status,String point,
			String content,String sort,
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
			String text = textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(value));
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
		
		if(point != null && !"".equals(point.trim())){
			if(point.trim().length()>8){
				error.put("point", "不能超过8位数字");
			}else{
				boolean pointVerification = Verification.isPositiveIntegerZero(point.trim());//正整数+0
				if(pointVerification){
					Long _rewardPoint = Long.parseLong(point.trim());
					if(_rewardPoint < 0L){
						error.put("point","不能小于0");
						
					}
					if(error.size() ==0){
						question.setPoint(_rewardPoint);
					}
				}else{
					error.put("point","请填写正整数");
				}
			}	
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
			questionService.saveQuestion(question,new ArrayList<QuestionTagAssociation>(questionTagAssociationList),null,null,null,null);
			
			//更新索引
			questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),1));
			
			
			
			//上传文件编号
			String fileNumber = "a"+userId;
			
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
			
					 if(imageName != null && !"".equals(imageName.trim())){
		 
						 //如果验证不是当前用户上传的文件，则不删除锁
						 if(!questionManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
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
						 if(!questionManage.getFileNumber(FileUtil.getBaseName(flashName.trim())).equals(fileNumber)){
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
						if(!questionManage.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
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
						if(!questionManage.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
					
					}
				}
			}
			
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 问题   追加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=appendQuestion",method=RequestMethod.GET)
	public String appendQuestionUI(Long questionId,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
		if(questionId != null && questionId >0L){
			Question question = questionService.findById(questionId);
			if(question != null){
				returnValue.put("question", question);
			}else{
				error.put("questionId", "问题不存在");
			}
		}else{
			error.put("questionId", "问题Id参数不能为空");
		}
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
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
	@ResponseBody
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
						 if(!questionManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
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
						 if(!questionManage.getFileNumber(FileUtil.getBaseName(flashName.trim())).equals(fileNumber)){
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
						if(!questionManage.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
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
						if(!questionManage.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
					
					}
				}
			}
			
		}
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
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
		
		String number = questionManage.generateFileNumber(userName, isStaff);
		
		
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
							String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_image_"+newFileName);
							String presigne = fileManage.createPresigned("file/question/"+date+"/image/"+newFileName,null);//创建预签名
							
							
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
							String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_flash_"+newFileName);
							String presigne = fileManage.createPresigned("file/question/"+date+"/flash/"+newFileName,null);//创建预签名
							
							
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
							String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_media_"+newFileName);
							String presigne = fileManage.createPresigned("file/question/"+date+"/media/"+newFileName,null);//创建预签名
							
							
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
							String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_file_"+newFileName);
							String presigne = fileManage.createPresigned("file/question/"+date+"/file/"+newFileName,null);//创建预签名
							
							
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
					
						
						if(authentication){
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
							fileManage.addLock(lockPathDir,date +"_image_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/question/"+date+"/image/"+newFileName);
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
							fileManage.addLock(lockPathDir,date +"_flash_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
							
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/question/"+date+"/flash/"+newFileName);
							return JsonUtils.toJSONString(returnJson);
						}
						
						
						
					}else if(dir.equals("media")){
						//允许上传视音频格式
						List<String> formatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
						
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
						
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
							fileManage.addLock(lockPathDir,date +"_media_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());

							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/question/"+date+"/media/"+newFileName);
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
							fileManage.addLock(lockPathDir,date +"_file_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());

							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/question/"+date+"/file/"+newFileName);
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
	 * 问题   修改界面显示
	 * 
	 */
	@ResponseBody
	@RequestMapping(params="method=editQuestion", method=RequestMethod.GET)
	public String editQuestionUI(ModelMap model,Long questionId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(questionId != null && questionId >0L){
			Question question = questionService.findById(questionId);
			if(question != null){
				if(question.getContent() != null && !"".equals(question.getContent().trim())){
					//处理富文本路径
					question.setContent(fileManage.processRichTextFilePath(question.getContent(),"question"));
				}
				
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
				
				User user = userManage.query_cache_findUserByUserName(question.getUserName());
				if(user != null){
					returnValue.put("maxDeposit",user.getDeposit());//允许使用的预存款
					returnValue.put("maxPoint",user.getPoint());//允许使用的积分
				}
				returnValue.put("question", question);
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
			}else{
				error.put("questionId", "问题不存在");
			}
		}else{
			error.put("questionId", "问题Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
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
	 */
	@ResponseBody
	@RequestMapping(params="method=editQuestion", method=RequestMethod.POST)
	public String editQuestion(ModelMap model,Long questionId,Long[] tagId,
			String title,Boolean allow,Integer status,String point,String amount,
			String content,String sort,Boolean visible,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String username = "";//用户名称
		String userId = "";//用户Id
		Object principal  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(principal instanceof SysUsers){
			userId =((SysUsers)principal).getUserId();
			username =((SysUsers)principal).getUserAccount();
		}
		
		
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
				question.setStatus(status);
				

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
				
				//悬赏积分
				Long rewardPoint = null;
				//悬赏金额
				BigDecimal rewardAmount = null;

				User user = userService.findUserByUserName(question.getUserName());//查询用户数据
				if(question.getAdoptionAnswerId().equals(0L)){//未采纳答案的问题才允许修改赏金
					if(point != null && !"".equals(point.trim())){
						if(point.trim().length()>8){
							error.put("point", "不能超过8位数字");
						}else{
							boolean pointVerification = Verification.isPositiveIntegerZero(point.trim());//正整数+0
							if(pointVerification){
								Long _rewardPoint = Long.parseLong(point.trim());
								if(_rewardPoint < 0L){
									error.put("point","不能小于0");
									
								}
								
								if(question.getIsStaff()==false && _rewardPoint > user.getPoint()){
									error.put("point","不能大于账户积分");
								}
								
								if(error.size() ==0){
									rewardPoint = _rewardPoint;
								}
							}else{
								error.put("point","请填写正整数或0");
							}
						}	
					}
					
					if(user != null && question.getIsStaff()==false && amount != null && !"".equals(amount.trim())){
						if(amount.trim().length()>12){
							error.put("amount", "不能超过12位数字");
						}else{
							boolean amountVerification = amount.trim().matches("(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");//金额
							if(amountVerification){
								BigDecimal _rewardAmount = new BigDecimal(amount.trim());
								if(_rewardAmount.compareTo(user.getDeposit()) >0){
									error.put("amount", "不能大于账户预存款");
									
								}
								if(_rewardAmount.compareTo(new BigDecimal("0")) <0){
									error.put("amount","不能小于0");
								}
								if(error.size() ==0){
									rewardAmount = _rewardAmount;
								}
							}else{
								error.put("amount", "请填写金额");
							}
						}	
					}
				}
				
				//变更积分符号
				boolean changePointSymbol = true;//true：加号  false：减号
				//变更积分
				Long changePoint = 0L;
				//变更金额符号
				boolean changeAmountSymbol = true;//true：加号  false：减号
				//变更金额
				BigDecimal changeAmount = new BigDecimal("0");

				if(rewardPoint != null && rewardPoint > question.getPoint()){
					changePointSymbol = true;
					changePoint = rewardPoint - question.getPoint();
				}
				if(rewardPoint != null && rewardPoint < question.getPoint()){
					changePointSymbol = false;
					changePoint = question.getPoint() - rewardPoint;
				}
				
				if(rewardAmount != null && rewardAmount.compareTo(question.getAmount()) > 0){
					changeAmountSymbol = true;
					changeAmount = rewardAmount.subtract(question.getAmount());
				}
				if(rewardAmount != null && rewardAmount.compareTo(question.getAmount()) < 0){
					changeAmountSymbol = false;
					changeAmount = question.getAmount().subtract(rewardAmount);
				}
				
				//用户悬赏积分日志
				Object pointLogObject = null;
				//用户悬赏金额日志
				Object paymentLogObject = null;
				
				Date time = new Date();
				if(changePoint != null && changePoint>0L){//如果有变更积分
					question.setPoint(rewardPoint);
					
					if(question.getIsStaff()==false){
						PointLog reward_pointLog = new PointLog();
						reward_pointLog.setId(pointManage.createPointLogId(user.getId()));
						reward_pointLog.setModule(1200);//1200.调整赏金
						reward_pointLog.setParameterId(question.getId());//参数Id 
						reward_pointLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
						reward_pointLog.setOperationUserName(username);//操作用户名称
						reward_pointLog.setPointState(changePointSymbol==true ? 2 :1);//2:账户支出
						reward_pointLog.setPoint(changePoint);//积分
						reward_pointLog.setUserName(user.getUserName());//用户名称
						reward_pointLog.setRemark("");
						reward_pointLog.setTimes(time);
						pointLogObject = pointManage.createPointLogObject(reward_pointLog);
					}
				}
				if(changeAmount != null && changeAmount.compareTo(new BigDecimal("0")) >0){//如果有变更金额
					question.setAmount(rewardAmount);
					
					PaymentLog reward_paymentLog = new PaymentLog();
					reward_paymentLog.setPaymentRunningNumber(paymentManage.createRunningNumber(user.getId()));//支付流水号
					reward_paymentLog.setPaymentModule(110);//支付模块 110.调整赏金
					reward_paymentLog.setSourceParameterId(String.valueOf(question.getId()));//参数Id 
					reward_paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
					reward_paymentLog.setOperationUserName(username);//操作用户名称  0:系统  1: 员工  2:会员
					reward_paymentLog.setAmountState(changeAmountSymbol==true ? 2 :1);//金额状态  1:账户存入  2:账户支出 
					reward_paymentLog.setAmount(changeAmount);//金额
					reward_paymentLog.setInterfaceProduct(0);//接口产品
					reward_paymentLog.setUserName(user.getUserName());//用户名称
					reward_paymentLog.setTimes(time);
					paymentLogObject = paymentManage.createPaymentLogObject(reward_paymentLog);
					
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
					String text = textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(value));
					
					
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
					
					try {
						question.setLastUpdateTime(new Date());//最后修改时间
						int i = questionService.updateQuestion(question,new ArrayList<QuestionTagAssociation>(questionTagAssociationList),
								changePointSymbol,changePoint,changeAmountSymbol, changeAmount,pointLogObject,paymentLogObject);
						//更新索引
						questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));
						
						if(i >0 && !old_status.equals(status)){
							if(user != null){
								//修改用户动态问题状态
								userService.updateUserDynamicQuestionStatus(user.getId(),question.getUserName(),question.getId(),question.getStatus());
							}
							
						}
						
						
						//删除缓存
						questionManage.delete_cache_findById(question.getId());//删除问题缓存
						questionManage.delete_cache_findQuestionTagAssociationByQuestionId(question.getId());//删除'根据问题Id查询问题标签关联'缓存
						if(user != null){
							userManage.delete_cache_findUserById(user.getId());
							userManage.delete_cache_findUserByUserName(user.getUserName());
							//异步执行会员卡赠送任务(长期任务类型)
							membershipCardGiftTaskManage.async_triggerMembershipCardGiftTask(user.getUserName());
							
						}
						
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
										if(flashName.equals("file/question/"+new_flashName)){
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
										if(mediaName.equals("file/question/"+new_mediaName)){
											iter.remove();
											break;
										}
									}
								}
								if(old_mediaNameList != null && old_mediaNameList.size() >0){
									for(String mediaName : old_mediaNameList){
										oldPathFileList.add(FileUtil.toSystemPath(mediaName));
										
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
										oldPathFileList.add(FileUtil.toSystemPath(fileName));
										
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
									 if(!questionManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
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
									 if(!questionManage.getFileNumber(FileUtil.getBaseName(flashName.trim())).equals(fileNumber)){
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
									if(!questionManage.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
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
									if(!questionManage.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
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
								if(!questionManage.getFileNumber(FileUtil.getBaseName(oldPathFile.trim())).equals(fileNumber)){
									continue;
								}
								
								
								//替换路径中的..号
								oldPathFile = FileUtil.toRelativePath(oldPathFile);
								
								//删除旧路径文件
								Boolean state = fileManage.deleteFile(oldPathFile);
								if(state != null && state == false){

									//替换指定的字符，只替换第一次出现的
									oldPathFile = StringUtils.replaceOnce(oldPathFile, "file"+File.separator+"question"+File.separator, "");
									
									//创建删除失败文件
									fileManage.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));
								}
							}
						}
					} catch (SystemException e) {
						error.put("question", e.getMessage());//提交问题错误
					}
				}
				
			}else{
				error.put("question", "问题不存在");
			}
		}else{
			error.put("question", "Id不存在");
		}
		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 问题   修改追加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=editAppendQuestion",method=RequestMethod.GET)
	public String editAppendQuestionUI(Long questionId,String appendQuestionItemId,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
		
		if(questionId != null && questionId >0L){
			Question question = questionService.findById(questionId);
			if(question != null){
				//删除最后一个逗号
				String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

				List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
				if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
					for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
						if(appendQuestionItem.getId().equals(appendQuestionItemId)){
							
							if(appendQuestionItem.getContent() != null && !"".equals(appendQuestionItem.getContent().trim())){
								//处理富文本路径
								appendQuestionItem.setContent(fileManage.processRichTextFilePath(appendQuestionItem.getContent(),"question"));
							}
							
							returnValue.put("appendQuestionItem", appendQuestionItem);
							break;
						}
					}
				}
				
				returnValue.put("question", question);
			}else{
				error.put("questionId", "问题不存在");
			}
		}else{
			error.put("questionId", "问题Id参数不能为空");
		}
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		
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
	@ResponseBody
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
									if(flashName.equals("file/question/"+new_flashName)){
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
									if(mediaName.equals("file/question/"+new_mediaName)){
										iter.remove();
										break;
									}
								}
							}
							if(old_mediaNameList != null && old_mediaNameList.size() >0){
								for(String mediaName : old_mediaNameList){
									oldPathFileList.add(FileUtil.toSystemPath(mediaName));
									
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
									oldPathFileList.add(FileUtil.toSystemPath(fileName));
									
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
								 if(!questionManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
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
								 if(!questionManage.getFileNumber(FileUtil.getBaseName(flashName.trim())).equals(fileNumber)){
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
								if(!questionManage.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
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
								if(!questionManage.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
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
							if(!questionManage.getFileNumber(FileUtil.getBaseName(oldPathFile.trim())).equals(fileNumber)){
								continue;
							}
							
							
							//替换路径中的..号
							oldPathFile = FileUtil.toRelativePath(oldPathFile);
							
							//删除旧路径文件
							Boolean state = fileManage.deleteFile(oldPathFile);
							if(state != null && state == false){

								//替换指定的字符，只替换第一次出现的
								oldPathFile = StringUtils.replaceOnce(oldPathFile, "file"+File.separator+"question"+File.separator, "");
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));
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
		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 问题   删除
	 * @param model
	 * @param questionId
	*/
	@ResponseBody
	@RequestMapping(params="method=deleteQuestion", method=RequestMethod.POST)
	public String deleteQuestion(ModelMap model,Long[] questionId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String username = "";//用户名称
		String userId = "";//用户Id
		Object principal  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(principal instanceof SysUsers){
			userId =((SysUsers)principal).getUserId();
			username =((SysUsers)principal).getUserAccount();
		}
		Map<String,String> error = new HashMap<String,String>();//错误
		
		
		
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
						User user = userManage.query_cache_findUserByUserName(question.getUserName());
						//悬赏金额
						BigDecimal rewardAmount = new BigDecimal("0.00");
						//悬赏积分
						Long rewardPoint = 0L;
						//用户悬赏积分日志
						Object pointLogObject = null;
						//用户悬赏金额日志
						Object paymentLogObject = null;
						
						if(question.getStatus() < 100){//标记删除
							int i = questionService.markDelete(question.getId());

							
							if(i >0 && user != null){
								//修改问题状态
								userService.softDeleteUserDynamicByQuestionId(user.getId(),question.getUserName(),question.getId());
							}
							//更新索引
							questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));
							questionManage.delete_cache_findById(question.getId());//删除缓存
							questionManage.delete_cache_findQuestionTagAssociationByQuestionId(question.getId());//删除'根据问题Id查询问题标签关联'缓存
						}else{//物理删除
							if(question.getAdoptionAnswerId().equals(0L) && !question.getIsStaff()){//如果悬赏未采纳答案，则将赏金退还给提问用户
								
								Date time = new Date();
								
								
								
								if(user != null && question.getPoint() != null && question.getPoint() >0L){
									rewardPoint = question.getPoint();
									PointLog reward_pointLog = new PointLog();
									reward_pointLog.setId(pointManage.createPointLogId(user.getId()));
									reward_pointLog.setModule(1000);//1000.悬赏积分
									reward_pointLog.setParameterId(question.getId());//参数Id 
									reward_pointLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
									reward_pointLog.setOperationUserName(username);//操作用户名称
									reward_pointLog.setPointState(1);//2:账户支出
									reward_pointLog.setPoint(question.getPoint());//积分
									reward_pointLog.setUserName(user.getUserName());//用户名称
									reward_pointLog.setRemark("");
									reward_pointLog.setTimes(time);
									pointLogObject = pointManage.createPointLogObject(reward_pointLog);
								}
								
								
								
								if(user != null && question.getAmount() != null && question.getAmount().compareTo(new BigDecimal("0")) >0){
									rewardAmount = question.getAmount();
									PaymentLog reward_paymentLog = new PaymentLog();
									reward_paymentLog.setPaymentRunningNumber(paymentManage.createRunningNumber(user.getId()));//支付流水号
									reward_paymentLog.setPaymentModule(90);//支付模块 90.悬赏金额
									reward_paymentLog.setSourceParameterId(String.valueOf(question.getId()));//参数Id 
									reward_paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
									reward_paymentLog.setOperationUserName(username);//操作用户名称  0:系统  1: 员工  2:会员
									reward_paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出 
									reward_paymentLog.setAmount(question.getAmount());//金额
									reward_paymentLog.setInterfaceProduct(0);//接口产品
									reward_paymentLog.setUserName(user.getUserName());//用户名称
									reward_paymentLog.setTimes(time);
									paymentLogObject = paymentManage.createPaymentLogObject(reward_paymentLog);
								}
								
								
							}
							
							String fileNumber = questionManage.generateFileNumber(question.getUserName(), question.getIsStaff());
							try {
								int i = questionService.deleteQuestion(question.getId(),question.getUserName(),rewardPoint,pointLogObject,rewardAmount,paymentLogObject);
							
								if(i>0){
									//根据问题Id删除用户动态(问题下的评论和回复也同时删除)
									userService.deleteUserDynamicByQuestionId(question.getId());
								}
								
								questionManage.delete_cache_findById(question.getId());//删除缓存
								questionManage.delete_cache_findQuestionTagAssociationByQuestionId(question.getId());//删除'根据问题Id查询问题标签关联'缓存
								if(user != null){
									userManage.delete_cache_findUserById(user.getId());
									userManage.delete_cache_findUserByUserName(user.getUserName());
									//异步执行会员卡赠送任务(长期任务类型)
									membershipCardGiftTaskManage.async_triggerMembershipCardGiftTask(user.getUserName());
									
								}
								
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
									}
									//删除文件
									List<String> fileNameList = (List<String>)obj[3];		
									for(String fileName :fileNameList){
										filePathList.add(FileUtil.toSystemPath(fileName));
									}
		
									
									for(String filePath :filePathList){
										
										
										 //如果验证不是当前用户上传的文件，则不删除
										 if(!questionManage.getFileNumber(FileUtil.getBaseName(filePath.trim())).equals(fileNumber)){
											 continue;
										 }
										
										//替换路径中的..号
										filePath = FileUtil.toRelativePath(filePath);
										
										//删除旧路径文件
										Boolean state = fileManage.deleteFile(filePath);
										if(state != null && state == false){
											 //替换指定的字符，只替换第一次出现的
											filePath = StringUtils.replaceOnce(filePath, "file"+File.separator+"question"+File.separator, "");
											//创建删除失败文件
											fileManage.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
										}
									}
									
									//清空目录
									Boolean state_ = fileManage.removeDirectory("file"+File.separator+"answer"+File.separator+question.getId()+File.separator);
									if(state_ != null && state_ == false){
										//创建删除失败目录文件
										fileManage.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+"#"+question.getId());
									}
									
								}
							} catch (SystemException e) {
								error.put("answer", e.getMessage());
							}
							
						}
						
						
					}	
					
				}else{
					error.put("questionId", "问题不存在");
				}
			}else{
				error.put("questionId", "问题Id组不能为空");
			}
		}else{
			error.put("questionId", "问题Id不能为空");
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
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
	@ResponseBody
	@RequestMapping(params="method=deleteAppendQuestion", method=RequestMethod.POST)
	public String deleteAppendQuestion(ModelMap model,Long questionId,String appendQuestionItemId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
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
						}
						//删除文件
						List<String> fileNameList = (List<String>)obj[3];		
						for(String fileName :fileNameList){
							filePathList.add(FileUtil.toSystemPath(fileName));
						}

						
						for(String filePath :filePathList){
							
							
							 //如果验证不是当前用户上传的文件，则不删除
							 if(!questionManage.getFileNumber(FileUtil.getBaseName(filePath.trim())).equals(fileNumber)){
								 continue;
							 }
							
							//替换路径中的..号
							filePath = FileUtil.toRelativePath(filePath);
							
							//删除旧路径文件
							Boolean state = fileManage.deleteFile(filePath);
							if(state != null && state == false){
								 //替换指定的字符，只替换第一次出现的
								filePath = StringUtils.replaceOnce(filePath, "file"+File.separator+"question"+File.separator, "");
								
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
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
			}else{
				error.put("questionId", "问题不存在");
			}
		}else{
			error.put("questionId", "问题Id或追加问题项Id不能为空");
		}
		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 还原
	 * @param model
	 * @param questionId 问题Id集合
	 * @return
	 * @throws Exception
	*/
	@ResponseBody
	@RequestMapping(params="method=reduction",method=RequestMethod.POST)
	public String reduction(ModelMap model,Long[] questionId,
			HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
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
		
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}else{
				error.put("question", "问题不能为空");
			}
		}else{
			error.put("question", "问题Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 审核问题
	 * @param model
	 * @param questionId 问题Id
	 * @return
	 * @throws Exception
	*/
	@ResponseBody
	@RequestMapping(params="method=auditQuestion",method=RequestMethod.POST)
	public String auditQuestion(ModelMap model,Long questionId,
			HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
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
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("questionId", "问题Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	

}
