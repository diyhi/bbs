package cms.web.action.template;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.forumCode.ForumCodeNode;
import cms.bean.help.Help;
import cms.bean.help.HelpType;
import cms.bean.question.QuestionTag;
import cms.bean.template.Forum;
import cms.bean.template.Forum_AdvertisingRelated_Image;
import cms.bean.template.Forum_AnswerRelated_Answer;
import cms.bean.template.Forum_CommentRelated_Comment;
import cms.bean.template.Forum_CustomForumRelated_CustomHTML;
import cms.bean.template.Forum_HelpRelated_Help;
import cms.bean.template.Forum_QuestionRelated_LikeQuestion;
import cms.bean.template.Forum_QuestionRelated_Question;
import cms.bean.template.Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser;
import cms.bean.template.Forum_SystemRelated_SearchWord;
import cms.bean.template.Forum_TopicRelated_LikeTopic;
import cms.bean.template.Forum_TopicRelated_Topic;
import cms.bean.template.Layout;
import cms.bean.template.Templates;
import cms.bean.topic.Tag;
import cms.service.help.HelpService;
import cms.service.help.HelpTypeService;
import cms.service.question.QuestionTagService;
import cms.service.template.TemplateService;
import cms.service.topic.TagService;
import cms.service.topic.TopicService;
import cms.utils.CommentedProperties;
import cms.utils.FileType;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.forumCode.ForumCodeManage;

/**
 * 模板管理 版块管理
 *
 */
@Controller
@RequestMapping("/control/forum/manage") 
public class ForumManageAction{
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	@Resource HelpTypeService helpTypeService;
	@Resource HelpService helpService;
	@Resource LocalFileManage localFileManage;
	@Resource TagService tagService;
	@Resource TopicService topicService;
	@Resource QuestionTagService questionTagService;
	@Resource LayoutManage layoutManage;
	@Resource ForumCodeManage forumCodeManage;
	
	@Resource TextFilterManage textFilterManage;
	@Resource(name = "forumValidator") 
	private Validator validator; 
	@Resource MessageSource messageSource;

	
	/**
	 * 版块管理 版块模板文件名显示
	 * @param dirName 目录名称
	 * @param childNodeName 版块类型子节点名称
	 */
	@ResponseBody
	@RequestMapping(params="method=forumTemplateFileNameUI", method=RequestMethod.GET)
	public String forumTemplateFileNameUI(String dirName, String childNodeName,ModelMap model,
			HttpServletRequest request) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		//根据模板目录名称查询模板
		if(dirName != null && !"".equals(dirName.trim())){
			if(childNodeName != null && !"".equals(childNodeName.trim())){
				List<ForumCodeNode> forumCodeNodeList = forumCodeManage.getForumCodeNode(dirName,childNodeName);
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,forumCodeNodeList));
			}else{
				error.put("childNodeName", "版块类型子节点名称不能为空");
			}
		}else{
			error.put("dirName", "目录名称不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 模板管理 添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Forum forum,String layoutId,String dirName,
			ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
		if(layoutId != null && !"".equals(layoutId)){
			Layout layout = templateService.findLayoutByLayoutId(layoutId);
			returnValue.put("layout", layout);
		}else {
			error.put("layoutId", "布局Id不能为空");
		}
		
		//根据模板目录名称查询模板
		if(dirName != null && !"".equals(dirName.trim())){
			Templates templates = templateService.findTemplatebyDirName(dirName);
			returnValue.put("templates", templates);
			
			List<ForumCodeNode> forumCodeNodeList = forumCodeManage.forumCodeNodeList(dirName);
			returnValue.put("forumCodeNodeList", forumCodeNodeList);
			
		}else {
			error.put("dirName", "目录名称不能为空");
		}	
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
		
	}
	/**
	 * 版块管理 添加版块
	 * @param formbean
	 * @param result
	 * @param layoutId 布局ID
	 * @param advertisingRelated_Image_Count 广告部分--图片广告 -- 集合  行号集合
	 * @param templeteName 模板名称
	 * @param layoutName 布局名称
	 * @param returnData 空白页返回数据
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(Forum formbean,BindingResult result,String layoutId,String dirName,
			Integer[] advertisingRelated_Image_Count,
			MultipartHttpServletRequest request,HttpServletResponse response,
			ModelMap model,RedirectAttributes redirectAttrs) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Layout layout = null;
		if(layoutId != null && !"".equals(layoutId)){
			layout = templateService.findLayoutByLayoutId(layoutId);
			if(layout == null){
				error.put("layoutId", "布局不存在");
			}
		}else {
			error.put("layoutId", "布局Id不能为空");
		}
		
		//根据模板目录名称查询模板
		if(dirName == null || "".equals(dirName.trim())){
			error.put("dirName", "目录名称不能为空");
		}

		
		//公共页(引用版块值)只允许添加一个版块
		boolean b = templateService.getForumThere(formbean.getDirName(),layoutId,6);//6:公共页(引用版块值)
		if(b == true){
			error.put("forum", "公共页(引用版块值)只允许添加一个版块");
		}
		
		Forum forum = new Forum();
		String module = formbean.getModule();
		if(error.size() ==0){
			
			
			forum.setLayoutId(layoutId);
			forum.setName(formbean.getName());
			forum.setModule(formbean.getModule());
			forum.setForumType(formbean.getForumType());
			forum.setForumChildType(formbean.getForumChildType());
			forum.setDirName(formbean.getDirName());
			forum.setLayoutType(layout.getType());
			forum.setLayoutFile(layout.getLayoutFile());
			forum.setInvokeMethod(formbean.getInvokeMethod());
			
			if((layout.getType().equals(4) && layout.getReturnData().equals(1)) || layout.getType().equals(6)){//空白页json方式返回数据或公共页(引用版块值) 这两种布局方式不能选择'调用对象' 
				if(formbean.getInvokeMethod().equals(2)){//2.调用对象
					error.put("forum", "不允许选择调用对象");
				}
			}
			
		
			//空白页(json)只允许添加一个版块
			List<Forum> forumList_Blank = templateService.findForumByLayoutId(dirName, layoutId);
			if(forumList_Blank != null && forumList_Blank.size() >0){
				for(Forum forum_Blank : forumList_Blank){
					if(forum_Blank.getLayoutType().equals(4) && layout.getReturnData().equals(1)){
						error.put("forum", "空白页(json)只允许添加一个版块");
					}
				}
			}
			
			

			String displayType = "monolayer";//模板显示类型
			if(module != null && !"".equals(module.trim())){
				String[] moduleArray = module.split("_");
				if(moduleArray[2].equals("monolayer")){//单层
					displayType = "monolayer";
				}else if(moduleArray[2].equals("multilayer")){
					displayType = "multilayer";//多层
				}else if(moduleArray[2].equals("page")){
					displayType = "page";//分页
				}else if(moduleArray[2].equals("entityBean")){
					displayType = "entityBean";//实体对象
				}else if(moduleArray[2].equals("collection")){
					displayType = "collection";//集合
				}
				forum.setDisplayType(displayType);
			}else{
				error.put("module", "请选择版块模板");
			}
			
			//版块扩展
			this.forumExtend(forum,formbean,displayType, advertisingRelated_Image_Count,dirName,error,request);
			
			//数据校验
			this.validator.validate(formbean, result); 
			if (result.hasErrors()) {  
				List<FieldError> fieldErrorList = result.getFieldErrors();
				if(fieldErrorList != null && fieldErrorList.size() >0){
					for(FieldError fieldError : fieldErrorList){
						error.put(fieldError.getField(), messageSource.getMessage(fieldError, null));
					}
				}
			}
			
			
		}
		if(error.size() ==0){
			//生成模块引用代码
			if(layout.getType().equals(6)){//6.公共页(引用版块值)
				forum.setReferenceCode(layout.getReferenceCode());
			}else{
				String[] moduleArray = module.split("_");
				String newModule = moduleArray[0]+"_"+moduleArray[1]+"_"; //组成查询字符Product_Show_
				
				List<Forum> forumList = templateService.findForumByReferenceCodePrefix(dirName,newModule);
				
				
				List<Integer> number = new ArrayList<Integer>();
				if(forumList != null && forumList.size()>0){
					
					for(Forum f : forumList){
						
						String rc = f.getReferenceCode();
						if(rc != null && !"".equals(rc)){
							String[] rcArray = rc.split("_");
							int newNumber = Integer.parseInt(rcArray[2]); //组成查询字符Product_Show_
							number.add(newNumber);
						}
					}
					//初始化数组   ,冒泡排序法从低到高选择排序 
					Integer[] lastArray = (Integer []) number.toArray(new Integer[0]);//li.toArray返回的是Object类型数组,这句是将Object数组转为Integer数组
					//外层循环lastArray.length-1次循环   
			        for(int i=lastArray.length-1; i>0; i--) {   
			            //外层循环i次循环   
			            for(int j=0; j<i; j++) {   
			                //当前面的数据大于后面的数据，把两个数据进行交换   
			                if(lastArray[j] > lastArray[j+1]) {   
			                    int tempInt = lastArray[j];   
			                    lastArray[j] = lastArray[j+1];   
			                    lastArray[j+1] = tempInt;   
			                }   
			            }   
			        }   
			        
			        Integer max = Integer.parseInt(lastArray[lastArray.length-1].toString())+1;//最大数字
			        forum.setReferenceCode(newModule + max.toString());
				}else{
					forum.setReferenceCode(newModule + "1");
				}
				
			}
			
			templateService.saveForum(forum);
			
			
			//广告 - 图片广告   删除图片锁
			if(forum.getForumChildType().equals("图片广告")){
				if("collection".equals(forum.getDisplayType())){//集合
					if(forum.getFormValue() != null && !"".equals(forum.getFormValue().trim())){
						List<Forum_AdvertisingRelated_Image> collection_Forum_AdvertisingRelated_imageList = JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference< List<Forum_AdvertisingRelated_Image> >(){});
						if(collection_Forum_AdvertisingRelated_imageList != null && collection_Forum_AdvertisingRelated_imageList.size() >0){
							for(Forum_AdvertisingRelated_Image forum_AdvertisingRelated_Image :collection_Forum_AdvertisingRelated_imageList){
								if(forum_AdvertisingRelated_Image.getImage_fileName() != null && !"".equals(forum_AdvertisingRelated_Image.getImage_fileName().trim())){	
									localFileManage.deleteLock("file"+File.separator+"template"+File.separator+"lock"+File.separator,dirName +"_"+FileUtil.toRelativePath(forum_AdvertisingRelated_Image.getImage_fileName()));
								}
							}
						}
					
					}
					
				}
			}

			//自定义版块 用户自定义HTML 文件锁
			if(forum.getForumChildType().equals("用户自定义HTML")){
				if("entityBean".equals(forum.getDisplayType())){//集合
					if(forum.getFormValue() != null && !"".equals(forum.getFormValue().trim())){
						Forum_CustomForumRelated_CustomHTML entityBean_Forum_CustomForumRelated_CustomHTML = JsonUtils.toObject(forum.getFormValue(), Forum_CustomForumRelated_CustomHTML.class);
						
						if(entityBean_Forum_CustomForumRelated_CustomHTML != null){
							if(entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content() != null && !"".equals(entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content())){
								Object[] htmlContent_obj = textFilterManage.readPathName(entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content(), "template");
								List<String> pathFileList = new ArrayList<String>();//路径文件
								if(htmlContent_obj != null && htmlContent_obj.length >0){
									//图片
									List<String> imageNameList = (List<String>)htmlContent_obj[0];		
									if(imageNameList != null && imageNameList.size() >0){
										
										for(String imageName : imageNameList){
											//路径文件
											pathFileList.add(imageName);
									
										}	
									}
									
									//Flash
									List<String> flashNameList = (List<String>)htmlContent_obj[1];		
									if(flashNameList != null && flashNameList.size() >0){
										for(String flashName : flashNameList){
											//路径文件
											pathFileList.add(flashName);
											
										}
										
									}
									//影音
									List<String> mediaNameList = (List<String>)htmlContent_obj[2];	
									if(mediaNameList != null && mediaNameList.size() >0){
										for(String mediaName : mediaNameList){
											//路径文件
											pathFileList.add(mediaName);
										
										}
										
									}	
									//文件
									List<String> fileNameList = (List<String>)htmlContent_obj[3];		
									if(fileNameList != null && fileNameList.size() >0){
										for(String fileName : fileNameList){
											//路径文件
											pathFileList.add(fileName);
										
										}
									}
								}
								//删除路径文件
								if(pathFileList != null && pathFileList.size() >0){
									for(String oldPathFile :pathFileList){
										 //替换指定的字符，只替换第一次出现的
										oldPathFile = StringUtils.replaceOnce(oldPathFile, "file/template/", "");
										
										if(oldPathFile != null && !"".equals(oldPathFile.trim())){
											localFileManage.deleteLock("file"+File.separator+"template"+File.separator+"lock"+File.separator,oldPathFile.replaceAll("/","_"));
										 }
									}
								}
							}
						}
						
					}
				}
			}
		}
		

		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	/**
	 * 模板管理 修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(Integer forumId,String layoutId,String dirName,
			ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
		if(layoutId != null && !"".equals(layoutId)){
			Layout layout = templateService.findLayoutByLayoutId(layoutId);
			returnValue.put("layout", layout);
		}else {
			error.put("layoutId", "布局Id不能为空");
		}
		

		
		if(forumId != null && forumId >0){
			Forum forum = templateService.findForumById(forumId);
			if(forum != null){
				
				List<ForumCodeNode> forumCodeNodeList = forumCodeManage.getForumCodeNode(dirName,forum.getForumChildType());
				if(forumCodeNodeList != null && forumCodeNodeList.size() >0){
					for(ForumCodeNode forumCodeNode : forumCodeNodeList){
						if(forumCodeNode.getNodeName().equals(forum.getModule())){
							returnValue.put("forumCodeNode_remark", forumCodeNode.getRemark());
						}
					}
				}
				returnValue.put("forum", forum);
				
				//查询‘更多’
				Map<String,String> more = layoutManage.queryMore(forum.getDirName(),forum.getForumChildType());
				returnValue.put("more",JsonUtils.toJSONString(more));

				
				if(forum.getFormValue() != null && !"".equals(forum.getFormValue().trim())){
					 
					if(forum.getForumChildType().equals("图片广告")){
						if("collection".equals(forum.getDisplayType())){//集合
							returnValue.put("collection_Forum_AdvertisingRelated_imageList",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<List<Forum_AdvertisingRelated_Image>>(){}));	
						}
					}

					if(forum.getForumChildType().equals("话题列表")){
						if("page".equals(forum.getDisplayType())){//分页
							returnValue.put("page_Forum_TopicRelated_Topic",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_TopicRelated_Topic>(){}));	
						}
					}
					if(forum.getForumChildType().equals("相似话题")){
						if("collection".equals(forum.getDisplayType())){//集合
							returnValue.put("collection_Forum_TopicRelated_LikeTopic",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_TopicRelated_LikeTopic>(){}));	
						}
					}
					if(forum.getForumChildType().equals("评论列表")){
						if("page".equals(forum.getDisplayType())){//分页
							returnValue.put("page_Forum_CommentRelated_Comment",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_CommentRelated_Comment>(){}));	
						}
					}
					
					if(forum.getForumChildType().equals("问题列表")){
						if("page".equals(forum.getDisplayType())){//分页
							returnValue.put("page_Forum_QuestionRelated_Question",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_QuestionRelated_Question>(){}));	
						}
					}
					if(forum.getForumChildType().equals("答案列表")){
						if("page".equals(forum.getDisplayType())){//分页
							returnValue.put("page_Forum_AnswerRelated_Answer",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_AnswerRelated_Answer>(){}));	
						}
					}
					if(forum.getForumChildType().equals("相似问题")){
						if("collection".equals(forum.getDisplayType())){//集合
							returnValue.put("collection_Forum_QuestionRelated_LikeQuestion",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_QuestionRelated_LikeQuestion>(){}));	
						}
					}
					if(forum.getForumChildType().equals("领取红包用户列表")){
						if("page".equals(forum.getDisplayType())){//分页
							returnValue.put("page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser>(){}));	
						}
					}
					if(forum.getForumChildType().equals("在线帮助列表")){
						if("monolayer".equals(forum.getDisplayType())){//单层
							returnValue.put("monolayer_Forum_HelpRelated_Help",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_HelpRelated_Help>(){}));	
						}
						if("page".equals(forum.getDisplayType())){//分页
							returnValue.put("page_Forum_HelpRelated_Help",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_HelpRelated_Help>(){}));	
						}
					}
					
					if(forum.getForumChildType().equals("推荐在线帮助")){
						if("collection".equals(forum.getDisplayType())){//集合
							returnValue.put("collection_Forum_HelpRelated_RecommendHelp",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_HelpRelated_Help>(){}));	
						}
					}
					
					//用户自定义HTML
					if(forum.getForumChildType().equals("用户自定义HTML")){
						if("entityBean".equals(forum.getDisplayType())){//集合
							returnValue.put("entityBean_Forum_CustomForumRelated_CustomHTML",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference<Forum_CustomForumRelated_CustomHTML>(){}));	
						}
					}
					//热门搜索词
					if(forum.getForumChildType().equals("热门搜索词")){
						if("collection".equals(forum.getDisplayType())){//集合
							returnValue.put("collection_Forum_SystemRelated_SearchWord",JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference< Forum_SystemRelated_SearchWord>(){}));	
						}
					}
				}
			}
		}else{
			error.put("forumId", "版块Id不能为空");
		}
		
		
		//根据模板目录名称查询模板
		if(dirName != null && !"".equals(dirName.trim())){
			Templates templates = templateService.findTemplatebyDirName(dirName);
			returnValue.put("templates", templates);
		}else {
			error.put("dirName", "目录名称不能为空");
		}
	
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	
	/**
	 * 版块管理 修改版块
	 * @param formbean
	 * @param result
	 * @param layoutId 布局ID
	 * @param commodityRelated_Product_Count 商品部分--商品 -- 多层  行号集合
	 * @param advertisingRelated_Image_Count 广告部分--图片广告 -- 集合  行号集合
	 * @param templeteName 模板名称
	 * @param layoutName 布局名称
	 * @param returnData 空白页返回数据
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(Forum formbean,BindingResult result,Integer forumId, String layoutId,String dirName,
			Integer[] advertisingRelated_Image_Count,
			MultipartHttpServletRequest request,HttpServletResponse response,
			ModelMap model,RedirectAttributes redirectAttrs) throws Exception {
		
		
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Layout layout = null;
		if(layoutId != null && !"".equals(layoutId)){
			layout = templateService.findLayoutByLayoutId(layoutId);
			if(layout == null){
				error.put("layoutId", "布局不存在");
			}
		}else {
			error.put("layoutId", "布局Id不能为空");
		}
		Forum forum = null;
		if(forumId != null && forumId >0){
			forum = templateService.findForumById(forumId);
			if(forum == null){
				error.put("forumId", "版块不存在");
			}
		}else {
			error.put("forumId", "版块Id不能为空");
		}
		
		
		//根据模板目录名称查询模板
		if(dirName == null || "".equals(dirName.trim())){
			error.put("dirName", "目录名称不能为空");
		}
		
		
		Forum new_forum = new Forum();
		if(error.size() ==0){
			new_forum.setId(forumId);
			new_forum.setLayoutId(layoutId);
			new_forum.setName(formbean.getName());
			new_forum.setModule(forum.getModule());
			new_forum.setDisplayType(forum.getDisplayType());
			new_forum.setForumType(forum.getForumType());
			new_forum.setForumChildType(forum.getForumChildType());
			new_forum.setDirName(forum.getDirName());
			new_forum.setLayoutType(layout.getType());
			new_forum.setLayoutFile(layout.getLayoutFile());
			new_forum.setInvokeMethod(formbean.getInvokeMethod());
			
			if((layout.getType().equals(4) && layout.getReturnData().equals(1)) || layout.getType().equals(6)){//空白页json方式返回数据或公共页(引用版块值) 这两种布局方式不能选择'调用对象' 
				if(formbean.getInvokeMethod().equals(2)){//2.调用对象
					error.put("invokeMethod", "不能选择调用对象");
				}
			}
			

			//版块扩展
			this.forumExtend(new_forum,formbean,new_forum.getDisplayType(), advertisingRelated_Image_Count,dirName,error,request);
				
			//数据校验
			this.validator.validate(formbean, result); 
			if (result.hasErrors()) {  
				List<FieldError> fieldErrorList = result.getFieldErrors();
				if(fieldErrorList != null && fieldErrorList.size() >0){
					for(FieldError fieldError : fieldErrorList){
						error.put(fieldError.getField(), messageSource.getMessage(fieldError, null));
					}
				}
			}
			
		}
		
		if(error.size() ==0){
			templateService.updateForum(new_forum);
			
			
			//广告 - 图片广告   删除图片锁
			if(forum.getForumChildType().equals("图片广告")){
				if("collection".equals(forum.getDisplayType())){//集合
					if(new_forum.getFormValue() != null && !"".equals(new_forum.getFormValue().trim())){
						List<Forum_AdvertisingRelated_Image> collection_Forum_AdvertisingRelated_imageList = JsonUtils.toGenericObject(new_forum.getFormValue(), new TypeReference< List<Forum_AdvertisingRelated_Image> >(){});
						if(collection_Forum_AdvertisingRelated_imageList != null && collection_Forum_AdvertisingRelated_imageList.size() >0){
							
							for(Forum_AdvertisingRelated_Image forum_AdvertisingRelated_Image :collection_Forum_AdvertisingRelated_imageList){
								if(forum_AdvertisingRelated_Image.getImage_fileName() != null && !"".equals(forum_AdvertisingRelated_Image.getImage_fileName().trim())){
									
									
									localFileManage.deleteLock("file"+File.separator+"template"+File.separator+"lock"+File.separator,dirName +"_"+FileUtil.toRelativePath(forum_AdvertisingRelated_Image.getImage_fileName()));
								}
							}
						}
					
					}
					
				}
			}
			
			//自定义版块 用户自定义HTML 文件锁
			if(forum.getForumChildType().equals("用户自定义HTML")){
				if("entityBean".equals(forum.getDisplayType())){//集合
					if(new_forum.getFormValue() != null && !"".equals(new_forum.getFormValue().trim())){
						Forum_CustomForumRelated_CustomHTML entityBean_Forum_CustomForumRelated_CustomHTML = JsonUtils.toObject(new_forum.getFormValue(), Forum_CustomForumRelated_CustomHTML.class);
						
						if(entityBean_Forum_CustomForumRelated_CustomHTML != null){
							if(entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content() != null && !"".equals(entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content())){
								Object[] htmlContent_obj = textFilterManage.readPathName(entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content(), "template");
								List<String> pathFileList = new ArrayList<String>();//路径文件
								if(htmlContent_obj != null && htmlContent_obj.length >0){
									//图片
									List<String> imageNameList = (List<String>)htmlContent_obj[0];		
									if(imageNameList != null && imageNameList.size() >0){
										
										for(String imageName : imageNameList){
											//路径文件
											pathFileList.add(imageName);
									
										}	
									}
									
									//Flash
									List<String> flashNameList = (List<String>)htmlContent_obj[1];		
									if(flashNameList != null && flashNameList.size() >0){
										for(String flashName : flashNameList){
											//路径文件
											pathFileList.add(flashName);
											
										}
										
									}
									//影音
									List<String> mediaNameList = (List<String>)htmlContent_obj[2];	
									if(mediaNameList != null && mediaNameList.size() >0){
										for(String mediaName : mediaNameList){
											//路径文件
											pathFileList.add(mediaName);
										
										}
										
									}	
									//文件
									List<String> fileNameList = (List<String>)htmlContent_obj[3];		
									if(fileNameList != null && fileNameList.size() >0){
										for(String fileName : fileNameList){
											//路径文件
											pathFileList.add(fileName);
										
										}
									}
								}
								
								//删除路径文件
								if(pathFileList != null && pathFileList.size() >0){
									for(String oldPathFile :pathFileList){
										 //替换指定的字符，只替换第一次出现的
										oldPathFile = StringUtils.replaceOnce(oldPathFile, "file/template/", "");
										if(oldPathFile != null && !"".equals(oldPathFile.trim())){
											localFileManage.deleteLock("file"+File.separator+"template"+File.separator+"lock"+File.separator,oldPathFile.replaceAll("/","_"));
										 }
									}
								}
							}
						}
						
					}
				}
			}
			

			//广告 - 图片广告   删除旧图片
			if(forum.getForumChildType().equals("图片广告")){
				if("collection".equals(forum.getDisplayType())){//集合
					
					//旧图片
					List<String> old_imageList = new ArrayList<String>();
					//新图片
					List<String> new_imageList = new ArrayList<String>();
					if(forum.getFormValue() != null && !"".equals(forum.getFormValue().trim())){
						List<Forum_AdvertisingRelated_Image> collection_Forum_AdvertisingRelated_imageList = JsonUtils.toGenericObject(forum.getFormValue(), new TypeReference< List<Forum_AdvertisingRelated_Image> >(){});
						if(collection_Forum_AdvertisingRelated_imageList != null && collection_Forum_AdvertisingRelated_imageList.size() >0){
							for(Forum_AdvertisingRelated_Image forum_AdvertisingRelated_Image :collection_Forum_AdvertisingRelated_imageList){
								if(forum_AdvertisingRelated_Image.getImage_fileName() != null && !"".equals(forum_AdvertisingRelated_Image.getImage_fileName().trim())){
									
									old_imageList.add(forum_AdvertisingRelated_Image.getImage_filePath()+dirName+"/"+forum_AdvertisingRelated_Image.getImage_fileName());	
								}
							}	
						}
						if(new_forum.getFormValue() != null && !"".equals(new_forum.getFormValue().trim())){
							List<Forum_AdvertisingRelated_Image> new_collection_Forum_AdvertisingRelated_imageList = JsonUtils.toGenericObject(new_forum.getFormValue(), new TypeReference< List<Forum_AdvertisingRelated_Image> >(){});;
							if(new_collection_Forum_AdvertisingRelated_imageList != null && new_collection_Forum_AdvertisingRelated_imageList.size() >0){
								for(Forum_AdvertisingRelated_Image new_forum_AdvertisingRelated_Image :new_collection_Forum_AdvertisingRelated_imageList){
									
									new_imageList.add(new_forum_AdvertisingRelated_Image.getImage_filePath()+dirName+"/"+new_forum_AdvertisingRelated_Image.getImage_fileName());
								}	
							}
						}
						
						Iterator<String> iter = old_imageList.iterator();
				        while (iter.hasNext()) {
				        	String old_image_path = iter.next();
							for(String new_image_path : new_imageList){
								if(old_image_path.equals(new_image_path)){
									iter.remove();
									break;
								}
							}
						}
					
				        //删除旧图片
				        for(String old_image :old_imageList){
							//替换路径中的..号
				        	old_image = FileUtil.toRelativePath(old_image);
							Boolean state = localFileManage.deleteFile(old_image);
							if(state != null && state == false){
								//替换指定的字符，只替换第一次出现的
								old_image = StringUtils.replaceOnce(old_image, "file/template/", "");
								old_image = StringUtils.replace(old_image, "/", "_");//替换所有出现过的字符
								//创建删除失败文件
								localFileManage.failedStateFile("file"+File.separator+"template"+File.separator+"lock"+File.separator+old_image);
							}
						}
					}
					
				}
			}
			
			//自定义版块 用户自定义HTML 删除上传文件
			if(forum.getForumChildType().equals("用户自定义HTML")){
				if("entityBean".equals(forum.getDisplayType())){//集合
					List<String> old_pathFileList = new ArrayList<String>();//旧路径文件
					
					List<String> new_pathFileList = new ArrayList<String>();// 新路径文件
					
					if(forum.getFormValue() != null && !"".equals(forum.getFormValue().trim())){
						Forum_CustomForumRelated_CustomHTML old_entityBean_Forum_CustomForumRelated_CustomHTML = JsonUtils.toObject(forum.getFormValue(), Forum_CustomForumRelated_CustomHTML.class);
						
						if(old_entityBean_Forum_CustomForumRelated_CustomHTML != null){
							if(old_entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content() != null && !"".equals(old_entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content())){
								Object[] htmlContent_obj = textFilterManage.readPathName(old_entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content(), "template");
								
								if(htmlContent_obj != null && htmlContent_obj.length >0){
									//图片
									List<String> imageNameList = (List<String>)htmlContent_obj[0];		
									if(imageNameList != null && imageNameList.size() >0){
										
										for(String imageName : imageNameList){
											//路径文件
											old_pathFileList.add(imageName);
									
										}	
									}
									
									//Flash
									List<String> flashNameList = (List<String>)htmlContent_obj[1];		
									if(flashNameList != null && flashNameList.size() >0){
										for(String flashName : flashNameList){
											//路径文件
											old_pathFileList.add(flashName);
											
										}
										
									}
									//影音
									List<String> mediaNameList = (List<String>)htmlContent_obj[2];	
									if(mediaNameList != null && mediaNameList.size() >0){
										for(String mediaName : mediaNameList){
											//路径文件
											old_pathFileList.add(mediaName);
										
										}
										
									}	
									//文件
									List<String> fileNameList = (List<String>)htmlContent_obj[3];		
									if(fileNameList != null && fileNameList.size() >0){
										for(String fileName : fileNameList){
											//路径文件
											old_pathFileList.add(fileName);
										
										}
									}
								}
								
							}
						}
						
						if(new_forum.getFormValue() != null && !"".equals(new_forum.getFormValue().trim())){
							Forum_CustomForumRelated_CustomHTML new_entityBean_Forum_CustomForumRelated_CustomHTML = JsonUtils.toObject(new_forum.getFormValue(), Forum_CustomForumRelated_CustomHTML.class);
							if(new_entityBean_Forum_CustomForumRelated_CustomHTML != null){
								if(new_entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content() != null && !"".equals(new_entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content())){
									Object[] htmlContent_obj = textFilterManage.readPathName(new_entityBean_Forum_CustomForumRelated_CustomHTML.getHtml_content(), "template");
									if(htmlContent_obj != null && htmlContent_obj.length >0){
										//图片
										List<String> imageNameList = (List<String>)htmlContent_obj[0];		
										if(imageNameList != null && imageNameList.size() >0){
											
											for(String imageName : imageNameList){
												//路径文件
												new_pathFileList.add(imageName);
										
											}	
										}
										
										//Flash
										List<String> flashNameList = (List<String>)htmlContent_obj[1];		
										if(flashNameList != null && flashNameList.size() >0){
											for(String flashName : flashNameList){
												//路径文件
												new_pathFileList.add(flashName);
												
											}
											
										}
										//影音
										List<String> mediaNameList = (List<String>)htmlContent_obj[2];	
										if(mediaNameList != null && mediaNameList.size() >0){
											for(String mediaName : mediaNameList){
												//路径文件
												new_pathFileList.add(mediaName);
											
											}
											
										}	
										//文件
										List<String> fileNameList = (List<String>)htmlContent_obj[3];		
										if(fileNameList != null && fileNameList.size() >0){
											for(String fileName : fileNameList){
												//路径文件
												new_pathFileList.add(fileName);
											
											}
										}
									}
								}
							}
							Iterator<String> iter = old_pathFileList.iterator();
					        while (iter.hasNext()) {
					        	String old_pathFile = iter.next();
								for(String new_pathFile : new_pathFileList){
									
									if(old_pathFile.equals(new_pathFile)){
										iter.remove();
										break;
									}
								}
							}
							if(old_pathFileList != null && old_pathFileList.size() >0){
								
								for(String old_pathFile : old_pathFileList){
									//替换路径中的..号
									old_pathFile = FileUtil.toRelativePath(old_pathFile);
									old_pathFile = FileUtil.toSystemPath(old_pathFile);
									Boolean state = localFileManage.deleteFile(old_pathFile);
									
									if(state != null && state == false){
										//替换指定的字符，只替换第一次出现的
										old_pathFile = StringUtils.replaceOnce(old_pathFile, "file"+File.separator+"template"+File.separator, "");
										old_pathFile = FileUtil.toUnderline(old_pathFile);//替换所有出现过的字符
										//创建删除失败文件
										localFileManage.failedStateFile("file"+File.separator+"template"+File.separator+"lock"+File.separator+old_pathFile);
									}
								}
								
							}
							
							
						}
					}
					
					
				}
			}
		}
		

		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	/**
	 * 版块扩展
	 * @return
	 */
	private Forum forumExtend(Forum forum,Forum formbean,String displayType,Integer[] advertisingRelated_Image_Count,
			String dirName,Map<String,String> forumError,MultipartHttpServletRequest request)throws Exception{	
		
		//广告
		List<Forum_AdvertisingRelated_Image> collection_Forum_AdvertisingRelated_imageList = new ArrayList<Forum_AdvertisingRelated_Image>();//版块---广告部分--轮播广告--单层
		if(formbean.getForumChildType().equals("图片广告")){
			if("collection".equals(displayType)){//集合
				if(advertisingRelated_Image_Count != null && advertisingRelated_Image_Count.length>0){
					
					
					String[] collection_image_optionName = request.getParameterValues("collection_image_name");//选项名称
					List<MultipartFile> collection_image_imageFile = request.getFiles("collection_image_uploadImage"); 
					String[] collection_image_imagePath = request.getParameterValues("collection_image_path");//接收图片路径
					String[] collection_image_imageLink = request.getParameterValues("collection_image_link");//图片链接
					
					for (int i = 0; i < advertisingRelated_Image_Count.length; i++) {//循环全部提交的条件
		
						Forum_AdvertisingRelated_Image forum_AdvertisingRelated_Image = new Forum_AdvertisingRelated_Image();
			
						String image_name = ""; //图片名称
						String image_filePath= "";//图片文件路径 储存时不包括模板目录路径
						String image_fileName= "";//图片文件名称
						String image_link = "";//图片链接
						if(collection_image_optionName[i] != null && !"".equals(collection_image_optionName[i].trim())){
							image_name = collection_image_optionName[i];
						}else{
							forumError.put("collection_image_name_"+i, "请填写图片名称！");
						}
						
						if(collection_image_imageLink[i] != null && !"".equals(collection_image_imageLink[i].trim())){
							image_link = collection_image_imageLink[i];
						}
						
						MultipartFile file = collection_image_imageFile.get(i);
						
						if(file != null && !file.isEmpty()){
							//验证文件类型
							List<String> formatList = new ArrayList<String>();
							formatList.add("gif");
							formatList.add("jpg");
							formatList.add("jpeg");
							formatList.add("bmp");
							formatList.add("png");
							boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
							if(authentication){
								//取得文件后缀		
								String ext = FileUtil.getExtension(file.getOriginalFilename());
								//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
								String pathDir = "file"+File.separator+"template"+File.separator + dirName +File.separator;
								//构建文件名称
								String fileName = UUIDUtil.getUUID32()+ "." + ext;
								image_filePath = "file/template/";
								image_fileName = fileName;
								
								//生成文件保存目录
								FileUtil.createFolder(pathDir);
								//生成锁文件名称
								String lockFileName = dirName +"_"+fileName;
								//添加文件锁
								localFileManage.addLock("file"+File.separator+"template"+File.separator+"lock"+File.separator,lockFileName);
								//保存文件
								localFileManage.writeFile(pathDir, fileName,file.getBytes());
							   
								
							}else{
								forumError.put("collection_image_imagePath_"+i, "图片格式错误！");
							}
							
						}else{//如果图片已上传
							
							if(collection_image_imagePath[i] != null && collection_image_imagePath[i].trim().length() >14){
								//取得文件名称
								String fileName = FileUtil.getName(collection_image_imagePath[i].trim());
								//旧路径必须为file/template/开头
								if(!collection_image_imagePath[i].trim().substring(0, 14).equals("file/template/")){
									continue;
								}
								image_filePath = "file/template/";
								image_fileName = fileName;
							}
						}
						
						forum_AdvertisingRelated_Image.setImage_id(UUIDUtil.getUUID32());
						forum_AdvertisingRelated_Image.setImage_name(image_name);
						forum_AdvertisingRelated_Image.setImage_link(image_link);
						forum_AdvertisingRelated_Image.setImage_filePath(image_filePath);
						forum_AdvertisingRelated_Image.setImage_fileName(image_fileName);
						collection_Forum_AdvertisingRelated_imageList.add(forum_AdvertisingRelated_Image);	
					}
					
				}
				
				forum.setFormValue(JsonUtils.toJSONString(collection_Forum_AdvertisingRelated_imageList));//加入表单值
			}
		}
		
		Forum_TopicRelated_Topic page_Forum_TopicRelated_Topic = new Forum_TopicRelated_Topic();
		
		if(formbean.getForumChildType().equals("话题列表")){
			if("page".equals(displayType)){//分页
				String page_topic_sort = request.getParameter("page_topic_sort");//排序
				String page_topic_maxResult = request.getParameter("page_topic_maxResult");//每页显示记录数
				String page_topic_pageCount = request.getParameter("page_topic_pageCount");//页码显示总数
				String page_topic_tagId = request.getParameter("page_topic_tagId");//标签Id
				String page_topic_tag_transferPrameter = request.getParameter("page_topic_tag_transferPrameter");//是否传递标签参数
	
				Integer topic_maxResult = null;//每页显示记录数
				Integer topic_pageCount = null;//页码显示总数
				Long topic_tagId = null;//标签Id

				boolean topic_tag_transferPrameter = false;//是否传递标签参数
				
				
				//是否传递标签参数
				if(page_topic_tag_transferPrameter != null && "true".equals(page_topic_tag_transferPrameter)){
					topic_tag_transferPrameter = true;
				}
				//每页显示记录数
				if(page_topic_maxResult != null && !"".equals(page_topic_maxResult.trim())){	
					boolean page_topic_maxResult_Verification = Verification.isPositiveIntegerZero(page_topic_maxResult.trim());//非负整数（正整数+ 0）
					if(!page_topic_maxResult_Verification){
						forumError.put("page_topic_maxResult", "请填写数字！");
					}else{
						topic_maxResult = Integer.parseInt(page_topic_maxResult.trim());
					}
				}
				//页码显示总数
				if(page_topic_pageCount != null && !"".equals(page_topic_pageCount.trim())){
					boolean page_topic_pageCount_Verification = Verification.isPositiveIntegerZero(page_topic_pageCount.trim());//非负整数（正整数+ 0）
					if(!page_topic_pageCount_Verification){
						forumError.put("page_topic_pageCount", "请填写数字！");
					}else{
						topic_pageCount = Integer.parseInt(page_topic_pageCount.trim());
					}
				}
				if(page_topic_tagId != null && !"".equals(page_topic_tagId.trim())){
					topic_tagId = Long.parseLong(page_topic_tagId);
				}
				
				
				//标签
				if(topic_tagId != null && topic_tagId >0L && topic_tag_transferPrameter == false){
					Tag tag = tagService.findById(topic_tagId);
					if(tag != null){
						page_Forum_TopicRelated_Topic.setTopic_tagId(tag.getId());
						
						page_Forum_TopicRelated_Topic.setTopic_tagName(tag.getName());
					}	
				}
				
				
				page_Forum_TopicRelated_Topic.setTopic_id(UUIDUtil.getUUID32());
				page_Forum_TopicRelated_Topic.setTopic_sort(Integer.parseInt(page_topic_sort));
				page_Forum_TopicRelated_Topic.setTopic_maxResult(topic_maxResult);//每页显示记录数
				page_Forum_TopicRelated_Topic.setTopic_pageCount(topic_pageCount);//页码显示总数
				page_Forum_TopicRelated_Topic.setTopic_tag_transferPrameter(topic_tag_transferPrameter);//是否传递标签参数
				forum.setFormValue(JsonUtils.toJSONString(page_Forum_TopicRelated_Topic));//加入表单值
				
			}
		}
		
		Forum_CommentRelated_Comment page_Forum_CommentRelated_Comment = new Forum_CommentRelated_Comment();
		//评论列表
		if(formbean.getForumChildType().equals("评论列表")){
			if("page".equals(displayType)){//分页
				String page_comment_sort = request.getParameter("page_comment_sort");//排序
				String page_comment_maxResult = request.getParameter("page_comment_maxResult");//每页显示记录数
				String page_comment_pageCount = request.getParameter("page_comment_pageCount");//页码显示总数
			
				Integer comment_maxResult = null;//每页显示记录数
				Integer comment_pageCount = null;//页码显示总数
				
				//每页显示记录数
				if(page_comment_maxResult != null && !"".equals(page_comment_maxResult.trim())){	
					boolean page_comment_maxResult_Verification = Verification.isPositiveIntegerZero(page_comment_maxResult.trim());//非负整数（正整数+ 0）
					if(!page_comment_maxResult_Verification){
						forumError.put("page_comment_maxResult", "请填写数字！");
					}else{
						comment_maxResult = Integer.parseInt(page_comment_maxResult.trim());
					}
				}
				//页码显示总数
				if(page_comment_pageCount != null && !"".equals(page_comment_pageCount.trim())){
					boolean page_comment_pageCount_Verification = Verification.isPositiveIntegerZero(page_comment_pageCount.trim());//非负整数（正整数+ 0）
					if(!page_comment_pageCount_Verification){
						forumError.put("page_comment_pageCount", "请填写数字！");
					}else{
						comment_pageCount = Integer.parseInt(page_comment_pageCount.trim());
					}
				}
				
				page_Forum_CommentRelated_Comment.setComment_id(UUIDUtil.getUUID32());
				page_Forum_CommentRelated_Comment.setComment_sort(Integer.parseInt(page_comment_sort));
				page_Forum_CommentRelated_Comment.setComment_maxResult(comment_maxResult);//每页显示记录数
				page_Forum_CommentRelated_Comment.setComment_pageCount(comment_pageCount);//页码显示总数
				forum.setFormValue(JsonUtils.toJSONString(page_Forum_CommentRelated_Comment));//加入表单值
				
				
				
			}
		}
		
		Forum_TopicRelated_LikeTopic collection_Forum_TopicRelated_LikeTopic = new Forum_TopicRelated_LikeTopic();
		if(formbean.getForumChildType().equals("相似话题")){
			if("collection".equals(displayType)){//集合
				String collection_likeTopic_maxResult = request.getParameter("collection_likeTopic_maxResult");//显示记录数
				
				Integer likeTopic_maxResult = null;//显示记录数
				
				//显示记录数
				if(collection_likeTopic_maxResult != null && !"".equals(collection_likeTopic_maxResult.trim())){	
					boolean collection_likeTopic_maxResult_Verification = Verification.isPositiveIntegerZero(collection_likeTopic_maxResult.trim());//非负整数（正整数+ 0）
					if(!collection_likeTopic_maxResult_Verification){
						forumError.put("collection_likeTopic_maxResult", "请填写数字！");
					}else{
						likeTopic_maxResult = Integer.parseInt(collection_likeTopic_maxResult.trim());
					}
				}
				collection_Forum_TopicRelated_LikeTopic.setLikeTopic_id(UUIDUtil.getUUID32());
				collection_Forum_TopicRelated_LikeTopic.setLikeTopic_maxResult(likeTopic_maxResult);//显示记录数
				forum.setFormValue(JsonUtils.toJSONString(collection_Forum_TopicRelated_LikeTopic));//加入表单值
				
			}
		}
		
		
		Forum_QuestionRelated_Question page_Forum_QuestionRelated_Question = new Forum_QuestionRelated_Question();
		
		if(formbean.getForumChildType().equals("问题列表")){
			if("page".equals(displayType)){//分页
				String page_question_sort = request.getParameter("page_question_sort");//排序
				String page_question_maxResult = request.getParameter("page_question_maxResult");//每页显示记录数
				String page_question_pageCount = request.getParameter("page_question_pageCount");//页码显示总数
				String page_question_tagId = request.getParameter("page_question_tagId");//标签Id
				String page_question_tag_transferPrameter = request.getParameter("page_question_tag_transferPrameter");//是否传递标签参数
				
				String page_question_filterCondition = request.getParameter("page_question_filterCondition");//过滤条件
				String page_question_filterCondition_transferPrameter = request.getParameter("page_question_filterCondition_transferPrameter");//是否传递过滤条件参数
	
				Integer question_maxResult = null;//每页显示记录数
				Integer question_pageCount = null;//页码显示总数
				Long question_tagId = null;//标签Id
				
				Integer question_filterCondition = null;//过滤条件

				boolean question_tag_transferPrameter = false;//是否传递标签参数
				
				boolean question_filterCondition_transferPrameter = false;//是否传递过滤条件参数
				
				
				//是否传递标签参数
				if(page_question_tag_transferPrameter != null && "true".equals(page_question_tag_transferPrameter)){
					question_tag_transferPrameter = true;
				}
				//是否传递过滤条件参数
				if(page_question_filterCondition_transferPrameter != null && "true".equals(page_question_filterCondition_transferPrameter)){
					question_filterCondition_transferPrameter = true;
				}
				
				//每页显示记录数
				if(page_question_maxResult != null && !"".equals(page_question_maxResult.trim())){	
					boolean page_question_maxResult_Verification = Verification.isPositiveIntegerZero(page_question_maxResult.trim());//非负整数（正整数+ 0）
					if(!page_question_maxResult_Verification){
						forumError.put("page_question_maxResult", "请填写数字！");
					}else{
						question_maxResult = Integer.parseInt(page_question_maxResult.trim());
					}
				}
				//页码显示总数
				if(page_question_pageCount != null && !"".equals(page_question_pageCount.trim())){
					boolean page_question_pageCount_Verification = Verification.isPositiveIntegerZero(page_question_pageCount.trim());//非负整数（正整数+ 0）
					if(!page_question_pageCount_Verification){
						forumError.put("page_question_pageCount", "请填写数字！");
					}else{
						question_pageCount = Integer.parseInt(page_question_pageCount.trim());
					}
				}
				if(page_question_tagId != null && !"".equals(page_question_tagId.trim())){
					question_tagId = Long.parseLong(page_question_tagId);
				}
				if(page_question_filterCondition != null && !"".equals(page_question_filterCondition.trim())){
					question_filterCondition = Integer.parseInt(page_question_filterCondition);
				}
				
				//标签
				if(question_tagId != null && question_tagId >0L && question_tag_transferPrameter == false){
					QuestionTag questionTag = questionTagService.findById(question_tagId);
					if(questionTag != null){
						page_Forum_QuestionRelated_Question.setQuestion_tagId(questionTag.getId());
						
						page_Forum_QuestionRelated_Question.setQuestion_tagName(questionTag.getName());
					}	
				}
				//过滤条件
				if(question_filterCondition != null && question_filterCondition >0 && question_filterCondition_transferPrameter == false){
					page_Forum_QuestionRelated_Question.setQuestion_filterCondition(question_filterCondition);
				}
				
				page_Forum_QuestionRelated_Question.setQuestion_id(UUIDUtil.getUUID32());
				page_Forum_QuestionRelated_Question.setQuestion_sort(Integer.parseInt(page_question_sort));
				page_Forum_QuestionRelated_Question.setQuestion_maxResult(question_maxResult);//每页显示记录数
				page_Forum_QuestionRelated_Question.setQuestion_pageCount(question_pageCount);//页码显示总数
				page_Forum_QuestionRelated_Question.setQuestion_tag_transferPrameter(question_tag_transferPrameter);//是否传递标签参数
				page_Forum_QuestionRelated_Question.setQuestion_filterCondition_transferPrameter(question_filterCondition_transferPrameter);//是否传递过滤条件参数
				forum.setFormValue(JsonUtils.toJSONString(page_Forum_QuestionRelated_Question));//加入表单值
				
			}
		}
		
		Forum_AnswerRelated_Answer collection_Forum_AnswerRelated_Answer = new Forum_AnswerRelated_Answer();
		//评论列表
		if(formbean.getForumChildType().equals("答案列表")){
			if("page".equals(displayType)){//集合
				String page_answer_sort = request.getParameter("page_answer_sort");//排序
				String page_answer_maxResult = request.getParameter("page_answer_maxResult");//每页显示记录数
				String page_answer_pageCount = request.getParameter("page_answer_pageCount");//页码显示总数
			
				Integer answer_maxResult = null;//每页显示记录数
				Integer answer_pageCount = null;//页码显示总数
				
				//每页显示记录数
				if(page_answer_maxResult != null && !"".equals(page_answer_maxResult.trim())){	
					boolean page_answer_maxResult_Verification = Verification.isPositiveIntegerZero(page_answer_maxResult.trim());//非负整数（正整数+ 0）
					if(!page_answer_maxResult_Verification){
						forumError.put("page_answer_maxResult", "请填写数字！");
					}else{
						answer_maxResult = Integer.parseInt(page_answer_maxResult.trim());
					}
				}
				//页码显示总数
				if(page_answer_pageCount != null && !"".equals(page_answer_pageCount.trim())){
					boolean page_answer_pageCount_Verification = Verification.isPositiveIntegerZero(page_answer_pageCount.trim());//非负整数（正整数+ 0）
					if(!page_answer_pageCount_Verification){
						forumError.put("page_answer_pageCount", "请填写数字！");
					}else{
						answer_pageCount = Integer.parseInt(page_answer_pageCount.trim());
					}
				}
				
				collection_Forum_AnswerRelated_Answer.setAnswer_id(UUIDUtil.getUUID32());
				collection_Forum_AnswerRelated_Answer.setAnswer_sort(Integer.parseInt(page_answer_sort));
				collection_Forum_AnswerRelated_Answer.setAnswer_maxResult(answer_maxResult);//每页显示记录数
				collection_Forum_AnswerRelated_Answer.setAnswer_pageCount(answer_pageCount);//页码显示总数
				forum.setFormValue(JsonUtils.toJSONString(collection_Forum_AnswerRelated_Answer));//加入表单值
				
				
				
			}
		}
		Forum_QuestionRelated_LikeQuestion collection_Forum_QuestionRelated_LikeQuestion = new Forum_QuestionRelated_LikeQuestion();
		if(formbean.getForumChildType().equals("相似问题")){
			if("collection".equals(displayType)){//集合
				String collection_likeQuestion_maxResult = request.getParameter("collection_likeQuestion_maxResult");//显示记录数
				
				Integer likeQuestion_maxResult = null;//显示记录数
				
				//显示记录数
				if(collection_likeQuestion_maxResult != null && !"".equals(collection_likeQuestion_maxResult.trim())){	
					boolean collection_likeQuestion_maxResult_Verification = Verification.isPositiveIntegerZero(collection_likeQuestion_maxResult.trim());//非负整数（正整数+ 0）
					if(!collection_likeQuestion_maxResult_Verification){
						forumError.put("collection_likeQuestion_maxResult", "请填写数字！");
					}else{
						likeQuestion_maxResult = Integer.parseInt(collection_likeQuestion_maxResult.trim());
					}
				}
				collection_Forum_QuestionRelated_LikeQuestion.setLikeQuestion_id(UUIDUtil.getUUID32());
				collection_Forum_QuestionRelated_LikeQuestion.setLikeQuestion_maxResult(likeQuestion_maxResult);//显示记录数
				forum.setFormValue(JsonUtils.toJSONString(collection_Forum_QuestionRelated_LikeQuestion));//加入表单值
				
			}
		}
		
		Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser = new Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser();
		if(formbean.getForumChildType().equals("领取红包用户列表")){
			if("page".equals(displayType)){//分页
				String page_receiveRedEnvelopeUser_maxResult = request.getParameter("page_receiveRedEnvelopeUser_maxResult");//显示记录数
				String page_receiveRedEnvelopeUser_sort = request.getParameter("page_receiveRedEnvelopeUser_sort");//排序
				
				
				Integer receiveRedEnvelopeUser_maxResult = null;//显示记录数
				
				//显示记录数
				if(page_receiveRedEnvelopeUser_maxResult != null && !"".equals(page_receiveRedEnvelopeUser_maxResult.trim())){	
					boolean page_receiveRedEnvelopeUser_maxResult_Verification = Verification.isPositiveIntegerZero(page_receiveRedEnvelopeUser_maxResult.trim());//非负整数（正整数+ 0）
					if(!page_receiveRedEnvelopeUser_maxResult_Verification){
						forumError.put("page_receiveRedEnvelopeUser_maxResult", "请填写数字！");
					}else{
						receiveRedEnvelopeUser_maxResult = Integer.parseInt(page_receiveRedEnvelopeUser_maxResult.trim());
					}
				}
			
				
				page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.setReceiveRedEnvelopeUser_id(UUIDUtil.getUUID32());
				page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.setReceiveRedEnvelopeUser_maxResult(receiveRedEnvelopeUser_maxResult);//显示记录数
				page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.setReceiveRedEnvelopeUser_sort(Integer.parseInt(page_receiveRedEnvelopeUser_sort));
				forum.setFormValue(JsonUtils.toJSONString(page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser));//加入表单值
				
			}
		}
		
		//在线帮助
		Forum_HelpRelated_Help monolayer_Forum_HelpRelated_Help = new Forum_HelpRelated_Help();
		Forum_HelpRelated_Help page_Forum_HelpRelated_Help = new Forum_HelpRelated_Help();
		
		if(formbean.getForumChildType().equals("在线帮助列表")){
			if("monolayer".equals(displayType)){//单层
				String monolayer_help_quantity = request.getParameter("monolayer_help_quantity");//商品数量
				String monolayer_help_sort = request.getParameter("monolayer_help_sort");//排序
				String monolayer_help_more = request.getParameter("monolayer_help_more");//更多
			//	String monolayer_product_moreValue = request.getParameter("monolayer_product_moreValue");//更多选中文本
				String monolayer_help_maxResult = request.getParameter("monolayer_help_maxResult");//每页显示记录数
				String monolayer_help_pageCount = request.getParameter("monolayer_help_pageCount");//页码显示总数
				String monolayer_help_helpTypeId = request.getParameter("monolayer_help_helpTypeId");//在线帮助分类Id
				String monolayer_help_helpType_transferPrameter = request.getParameter("monolayer_help_helpType_transferPrameter");//是否传递在线帮助分类参数
	
				Integer help_quantity = null;//商品展示数量
				Integer help_maxResult = null;//每页显示记录数
				Integer help_pageCount = null;//页码显示总数
				Long help_helpTypeId = null;//在线帮助分类Id

				boolean help_helpType_transferPrameter = false;//是否在线帮助分类参数
				
				
				//是否传递在线帮助分类参数
				if(monolayer_help_helpType_transferPrameter != null && "true".equals(monolayer_help_helpType_transferPrameter)){
					help_helpType_transferPrameter = true;
				}
				//商品数量
				if(monolayer_help_quantity != null && !"".equals(monolayer_help_quantity.trim())){
					boolean monolayer_help_quantity_Verification = Verification.isPositiveIntegerZero(monolayer_help_quantity.trim());//非负整数（正整数+ 0）
					if(!monolayer_help_quantity_Verification){
						forumError.put("monolayer_help_quantity", "请填写数字！");
					}else{
						help_quantity = Integer.parseInt(monolayer_help_quantity.trim());
					}
				}
				//每页显示记录数
				if(monolayer_help_maxResult != null && !"".equals(monolayer_help_maxResult.trim())){	
					boolean monolayer_help_maxResult_Verification = Verification.isPositiveIntegerZero(monolayer_help_maxResult.trim());//非负整数（正整数+ 0）
					if(!monolayer_help_maxResult_Verification){
						forumError.put("monolayer_help_maxResult", "请填写数字！");
					}else{
						help_maxResult = Integer.parseInt(monolayer_help_maxResult.trim());
					}
				}
				//页码显示总数
				if(monolayer_help_pageCount != null && !"".equals(monolayer_help_pageCount.trim())){
					boolean monolayer_help_pageCount_Verification = Verification.isPositiveIntegerZero(monolayer_help_pageCount.trim());//非负整数（正整数+ 0）
					if(!monolayer_help_pageCount_Verification){
						forumError.put("monolayer_help_pageCount", "请填写数字！");
					}else{
						help_pageCount = Integer.parseInt(monolayer_help_pageCount.trim());
					}
				}
				if(monolayer_help_helpTypeId != null && !"".equals(monolayer_help_helpTypeId.trim())){
					help_helpTypeId = Long.parseLong(monolayer_help_helpTypeId);
				}
				
				
				//在线帮助分类
				if(help_helpTypeId != null && help_helpTypeId >0L && help_helpType_transferPrameter == false){
					HelpType helpType = helpTypeService.findById(help_helpTypeId);
					if(helpType != null){
						monolayer_Forum_HelpRelated_Help.setHelp_helpTypeId(helpType.getId());
						
						monolayer_Forum_HelpRelated_Help.setHelp_helpTypeName(helpType.getName());
					}	
				}
				//更多选中文本
				String moreValue = layoutManage.getMoreName(formbean.getDirName(),forum.getForumChildType(),monolayer_help_more);

				
				monolayer_Forum_HelpRelated_Help.setHelp_id(UUIDUtil.getUUID32());
				monolayer_Forum_HelpRelated_Help.setHelp_quantity(help_quantity);
				monolayer_Forum_HelpRelated_Help.setHelp_sort(Integer.parseInt(monolayer_help_sort));
				monolayer_Forum_HelpRelated_Help.setHelp_more(monolayer_help_more);
				monolayer_Forum_HelpRelated_Help.setHelp_moreValue(moreValue);
				monolayer_Forum_HelpRelated_Help.setHelp_maxResult(help_maxResult);//每页显示记录数
				monolayer_Forum_HelpRelated_Help.setHelp_pageCount(help_pageCount);//页码显示总数
				monolayer_Forum_HelpRelated_Help.setHelp_helpType_transferPrameter(help_helpType_transferPrameter);//是否传递在线帮助参数
				forum.setFormValue(JsonUtils.toJSONString(monolayer_Forum_HelpRelated_Help));//加入表单值
				
			}else if("page".equals(displayType)){//分页
				String page_help_sort = request.getParameter("page_help_sort");//排序
				String page_help_maxResult = request.getParameter("page_help_maxResult");//每页显示记录数
				String page_help_pageCount = request.getParameter("page_help_pageCount");//页码显示总数
				String page_help_helpTypeId = request.getParameter("page_help_helpTypeId");//在线帮助分类Id
				String page_help_helpType_transferPrameter = request.getParameter("page_help_helpType_transferPrameter");//是否传递在线帮助分类参数
	
				Integer help_maxResult = null;//每页显示记录数
				Integer help_pageCount = null;//页码显示总数
				Long help_helpTypeId = null;//在线帮助分类Id

				boolean help_helpType_transferPrameter = false;//是否在线帮助分类参数
				
				
				//是否传递在线帮助分类参数
				if(page_help_helpType_transferPrameter != null && "true".equals(page_help_helpType_transferPrameter)){
					help_helpType_transferPrameter = true;
				}
				//每页显示记录数
				if(page_help_maxResult != null && !"".equals(page_help_maxResult.trim())){	
					boolean page_help_maxResult_Verification = Verification.isPositiveIntegerZero(page_help_maxResult.trim());//非负整数（正整数+ 0）
					if(!page_help_maxResult_Verification){
						forumError.put("page_help_maxResult", "请填写数字！");
					}else{
						help_maxResult = Integer.parseInt(page_help_maxResult.trim());
					}
				}
				//页码显示总数
				if(page_help_pageCount != null && !"".equals(page_help_pageCount.trim())){
					boolean page_help_pageCount_Verification = Verification.isPositiveIntegerZero(page_help_pageCount.trim());//非负整数（正整数+ 0）
					if(!page_help_pageCount_Verification){
						forumError.put("page_help_pageCount", "请填写数字！");
					}else{
						help_pageCount = Integer.parseInt(page_help_pageCount.trim());
					}
				}
				if(page_help_helpTypeId != null && !"".equals(page_help_helpTypeId.trim())){
					help_helpTypeId = Long.parseLong(page_help_helpTypeId);
				}
				
				
				//在线帮助分类
				if(help_helpTypeId != null && help_helpTypeId >0L && help_helpType_transferPrameter == false){
					HelpType helpType = helpTypeService.findById(help_helpTypeId);
					if(helpType != null){
						page_Forum_HelpRelated_Help.setHelp_helpTypeId(helpType.getId());
						
						page_Forum_HelpRelated_Help.setHelp_helpTypeName(helpType.getName());
					}	
				}
				
				
				page_Forum_HelpRelated_Help.setHelp_id(UUIDUtil.getUUID32());
				page_Forum_HelpRelated_Help.setHelp_sort(Integer.parseInt(page_help_sort));
				page_Forum_HelpRelated_Help.setHelp_maxResult(help_maxResult);//每页显示记录数
				page_Forum_HelpRelated_Help.setHelp_pageCount(help_pageCount);//页码显示总数
				page_Forum_HelpRelated_Help.setHelp_helpType_transferPrameter(help_helpType_transferPrameter);//是否传递在线帮助参数
				forum.setFormValue(JsonUtils.toJSONString(page_Forum_HelpRelated_Help));//加入表单值
				
			}
		}	
		
		Forum_HelpRelated_Help collection_Forum_HelpRelated_RecommendHelp = new Forum_HelpRelated_Help();
		if(formbean.getForumChildType().equals("推荐在线帮助")){

			String[] collection_recommendHelp_helpId = request.getParameterValues("collection_recommendHelp_helpId");//在线帮助Id

			//在线帮助Id
			List<Help> help_recommendHelpList = new ArrayList<Help>();//id:在线帮助Id name:在线帮助名称
			if(collection_recommendHelp_helpId != null && collection_recommendHelp_helpId.length >0){
				List<Long> helpIdList = new ArrayList<Long>();
				for(int i = 0; i<collection_recommendHelp_helpId.length; i++){
					String helpId = collection_recommendHelp_helpId[i];
					if(helpId != null && !"".equals(helpId.trim())){
						Long _helpId = Long.parseLong(helpId);
						helpIdList.add(_helpId);
					}
				}
				if(helpIdList != null && helpIdList.size() >0){
					List<Help> _helpList = helpService.findByIdList(helpIdList);
					if( _helpList != null &&  _helpList.size() >0){
						for(Long id :helpIdList){//按添加的顺序
							for(Help help : _helpList){
								if(id.equals(help.getId())){
									help_recommendHelpList.add(new Help(help.getId(), help.getName()));
									break;
								}
							}						
						}		
					}
				}
			}	
			
			
			collection_Forum_HelpRelated_RecommendHelp.setHelp_id(UUIDUtil.getUUID32());

			collection_Forum_HelpRelated_RecommendHelp.setHelp_recommendHelpList(help_recommendHelpList);
			forum.setFormValue(JsonUtils.toJSONString(collection_Forum_HelpRelated_RecommendHelp));//加入表单值
			
		}
		//在线帮助分类
		if(formbean.getForumChildType().equals("在线帮助分类")){
			if("monolayer".equals(displayType)){//单层
				//无内容
			}
		}
		//在线帮助导航
		if(formbean.getForumChildType().equals("在线帮助导航")){
			if("monolayer".equals(displayType)){//单层
				//无内容
			}
		}
		
		
		
		//用户自定义HTML
		Forum_CustomForumRelated_CustomHTML entityBean_Forum_CustomForumRelated_CustomHTML = new Forum_CustomForumRelated_CustomHTML();
		if(formbean.getForumChildType().equals("用户自定义HTML")){
			if("entityBean".equals(displayType)){//实体对象
				
				String entityBean_customForum_htmlContent = request.getParameter("entityBean_customForum_htmlContent");//用户自定义HTML内容
				
				
				
				entityBean_Forum_CustomForumRelated_CustomHTML.setHtml_id(UUIDUtil.getUUID32());
				entityBean_Forum_CustomForumRelated_CustomHTML.setHtml_content(entityBean_customForum_htmlContent);
				forum.setFormValue(JsonUtils.toJSONString(entityBean_Forum_CustomForumRelated_CustomHTML));//加入表单值
			}
		}
		
		//热门搜索词
		Forum_SystemRelated_SearchWord collection_Forum_SystemRelated_SearchWord = new Forum_SystemRelated_SearchWord();
		if(formbean.getForumChildType().equals("热门搜索词")){
			if("collection".equals(displayType)){//集合
				List<String> searchWordList = new ArrayList<String>();
				//商品类型扩展属性  选择项参数
				String[] searchWord_arr = request.getParameterValues("collection_searchWord");//取得参数名对应的参数值数组
				if(searchWord_arr != null && searchWord_arr.length >0){
					for(int j = 0; j<searchWord_arr.length; j++){
						String searchWord = searchWord_arr[j];
						
						if(searchWord != null && !"".equals(searchWord.trim())){
							searchWordList.add(searchWord.trim());
							
						}
					}
				}
				
				
				collection_Forum_SystemRelated_SearchWord.setSearchWord_id(UUIDUtil.getUUID32());
				collection_Forum_SystemRelated_SearchWord.setSearchWordList(searchWordList);
				forum.setFormValue(JsonUtils.toJSONString(collection_Forum_SystemRelated_SearchWord));//加入表单值
			}
		}
		
		
		return forum;
	}
			

	/**
	 * 文件上传
	 * @param layoutId 布局Id
	 * @param dir: 上传类型，分别为image、flash、media、file 
	 */
	@ResponseBody
	@RequestMapping(params="method=upload",method=RequestMethod.POST)
	public String upload(ModelMap model,String layoutId,String dir,
			MultipartFile file, HttpServletResponse response) throws Exception {
		//当前图片类型
		Map<String,Object> returnJson = new HashMap<String,Object>();
		
		Layout layout = null;
		
		if(layoutId != null && !"".equals(layoutId.trim())){
			layout = templateService.findLayoutByLayoutId(layoutId);
			
		}

		if(dir != null && layout != null){
				
			if(file != null && !file.isEmpty()){
				//当前文件名称
				String fileName = file.getOriginalFilename();
				//文件大小
				Long size = file.getSize();
				//取得文件后缀
				String suffix = FileUtil.getExtension(fileName).toLowerCase();
				
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
					
					if(authentication && size/1024 <= imageSize){
						
						
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"template"+File.separator + layout.getDirName() + File.separator + "image"+ File.separator;
						//文件锁目录
						String lockPathDir = "file"+File.separator+"template"+File.separator+"lock"+File.separator;
						//构建文件名称
						String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
						
						//生成文件保存目录
						FileUtil.createFolder(pathDir);
						//生成锁文件保存目录
						FileUtil.createFolder(lockPathDir);
						//生成锁文件
						localFileManage.addLock(lockPathDir,layout.getDirName()+"_image_"+newFileName);
						//保存文件
						localFileManage.writeFile(pathDir, newFileName,file.getBytes());
						
						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/template/"+layout.getDirName()+"/image/"+newFileName);
						return JsonUtils.toJSONString(returnJson);
						
					}
				}else if(dir.equals("flash")){
					//允许上传flash格式	
					List<String> flashFormatList = new ArrayList<String>();
					flashFormatList.add("swf");
					
					//验证文件后缀
					boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),flashFormatList);
					
					if(authentication){
						
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"template"+File.separator + layout.getDirName()+ File.separator +"flash"+ File.separator;
						//文件锁目录
						String lockPathDir = "file"+File.separator+"template"+File.separator+"lock"+File.separator;
						//构建文件名称
						String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
						
						//生成文件保存目录
						FileUtil.createFolder(pathDir);
						//生成锁文件保存目录
						FileUtil.createFolder(lockPathDir);
						//生成锁文件
						localFileManage.addLock(lockPathDir,layout.getDirName() +"_flash_"+newFileName);
						//保存文件
						localFileManage.writeFile(pathDir, newFileName,file.getBytes());
						
						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/template/"+layout.getDirName()+"/flash/"+newFileName);
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
					boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
					
					if(authentication){
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"template"+File.separator + layout.getDirName()+ File.separator +"media"+ File.separator;
						//文件锁目录
						String lockPathDir = "file"+File.separator+"template"+File.separator+"lock"+File.separator;
						//构建文件名称
						String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
						
						//生成文件保存目录
						FileUtil.createFolder(pathDir);
						//生成锁文件保存目录
						FileUtil.createFolder(lockPathDir);
						//生成锁文件
						localFileManage.addLock(lockPathDir,layout.getDirName()+"_media_"+newFileName);
						//保存文件
						localFileManage.writeFile(pathDir, newFileName,file.getBytes());
						
						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/template/"+layout.getDirName()+"/media/"+newFileName);
						return JsonUtils.toJSONString(returnJson);
					}
				}else if(dir.equals("file")){
					//允许上传文件格式
					List<String> formatList = CommentedProperties.readRichTextAllowFileUploadFormat();
					
					//验证文件后缀
					boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
					if(authentication){
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"template"+File.separator + layout.getDirName()+ File.separator +"file"+ File.separator;
						//文件锁目录
						String lockPathDir = "file"+File.separator+"template"+File.separator+"lock"+File.separator;
						//构建文件名称
						String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
						
						//生成文件保存目录
						FileUtil.createFolder(pathDir);
						//生成锁文件保存目录
						FileUtil.createFolder(lockPathDir);
						//生成锁文件
						localFileManage.addLock(lockPathDir,layout.getDirName()+"_file_"+newFileName);
						//保存文件
						localFileManage.writeFile(pathDir, newFileName,file.getBytes());
						
						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/template/"+layout.getDirName()+"/file/"+newFileName);
						returnJson.put("title", file.getOriginalFilename());//旧文件名称
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
	 * 版块管理 删除
	 */
	@ResponseBody
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	public String delete(ModelMap model,Integer forumId,
			HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		if(forumId != null && forumId >0){
			Forum forum = templateService.findForumById(forumId);
			if(forum != null){
				List<Forum> forumList = new ArrayList<Forum>();
				forumList.add(forum);
				//删除布局版块上传文件
				layoutManage.deleteUploadFile(forumList);
				
				
				templateService.deleteForumByForumId(forumId);
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}
		}else{
			error.put("forumId", "版块Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
