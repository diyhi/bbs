package cms.web.action.question;


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

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.question.QuestionTag;
import cms.bean.topic.Tag;
import cms.service.question.QuestionTagService;
import cms.service.setting.SettingService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;



/**
 * 问题标签管理
 *
 */
@Controller
@RequestMapping("/control/questionTag/manage") 
public class QuestionTagManageAction {

	@Resource QuestionTagService questionTagService; 
	@Resource QuestionTagManage questionTagManage;
	@Resource TextFilterManage textFilterManage;
	@Resource FileManage fileManage;
	@Resource SettingService settingService;
	
	/**
	 * 问题标签   添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,Tag tag,Long parentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(parentId != null){//判断父类ID是否存在;
			QuestionTag _tag = questionTagService.findById(parentId);
			if(_tag != null){
				returnValue.put("parentTag", _tag);//返回消息
				
				
				Map<Long,String> navigation = new LinkedHashMap<Long,String>();
				
				List<QuestionTag> parentProductTypeList = questionTagService.findAllParentById(_tag);
				for(QuestionTag p : parentProductTypeList){
					navigation.put(p.getId(), p.getName());
				}
				navigation.put(_tag.getId(),_tag.getName());
				returnValue.put("navigation", navigation);//标签导航
			}else{
				error.put("parentId", "父类不存在");
			}

			
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	/**
	 * 问题标签  添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,String name,String sort,String parentId,String imagePath,MultipartFile images,
		HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String,String> error = new HashMap<String,String>();//错误
		
		
		
		if(name == null || "".equals(name.trim())){
			error.put("name", "标签名称不能为空");
		}else{
			if(name.length() >190){
				error.put("name", "不能大于190个字符");
			}
		}
		if(sort != null && !"".equals(sort.trim())){
			boolean verification = Verification.isPositiveIntegerZero(sort.trim());//数字
			if(verification){
				if(sort.trim().length()>8){
					error.put("sort", "不能大于8位数字");
				}
			}else{
				error.put("sort", "排序必须为大于或等于0的数字");
			}
		}else{
			error.put("sort", "排序不能为空");
		}
		String parentIdGroup = ",0,";
		if(parentId != null && !"".equals(parentId.trim())){//判断父类ID是否存在;
			boolean verification = Verification.isPositiveIntegerZero(parentId.trim());//数字
			if(verification){
				if(Long.parseLong(parentId.trim()) >0L){
					//取得父对象
					QuestionTag t = questionTagService.findById(Long.parseLong(parentId.trim()));
					if(t != null){
						//根据标签查询所有父类标签
						List<QuestionTag> allParentTag = questionTagService.findAllParentById(t);
						if(allParentTag.size() >=1){
							error.put("tag", "标签已达到最大层数,添加失败");
						}
						parentIdGroup = t.getParentIdGroup()+parentId.trim()+",";
					}else{
						error.put("tag", "父标签不存在");
					}
				}
			}else{
				error.put("tag", "父Id参数不正确");
			}
			
		}else{
			parentId = "0";
		}
		
		QuestionTag tag = new QuestionTag(); 
		String _imagePath = "";
		String _fileName = "";
		if (images == null || images.isEmpty()) { 
			if(imagePath != null && !"".equals(imagePath.trim())){
				imagePath = textFilterManage.deleteBindURL(request, imagePath);
				
				String fileName = FileUtil.getName(imagePath);
				
				//取得路径名称
				String pathName = FileUtil.getFullPath(imagePath);
				
				//旧路径必须为file/questionTag/开头
				if(imagePath.substring(0, 17).equals("file/questionTag/")){
					//新路径名称
					String newPathName = "file/questionTag/";
					
					//如果新旧路径不一致
					if(!newPathName.equals(pathName)){
						
						//复制文件到新路径
						fileManage.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
						//新建文件锁到新路径
						//生成锁文件名称
						String lockFileName =fileName;
						//添加文件锁
						fileManage.addLock("file"+File.separator+"questionTag"+File.separator+"lock"+File.separator,lockFileName);
						
						
					}
				
					_imagePath = "file/questionTag/";
					_fileName = fileName;
				}
				
					  
			}
        }else{
        	//验证文件类型
			List<String> formatList = new ArrayList<String>();
			formatList.add("gif");
			formatList.add("jpg");
			formatList.add("jpeg");
			formatList.add("bmp");
			formatList.add("png");
			formatList.add("svg");
			boolean authentication = FileUtil.validateFileSuffix(images.getOriginalFilename(),formatList);
			if(authentication){
				//取得文件后缀		
				String ext = FileUtil.getExtension(images.getOriginalFilename());
				//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
				String pathDir = "file"+File.separator+"questionTag"+File.separator;
				//构建文件名称
				String fileName = UUIDUtil.getUUID32()+ "." + ext;
				_imagePath = "file/questionTag/";
				_fileName = fileName; 
				  
				//生成文件保存目录
				FileUtil.createFolder(pathDir);
				 
				//生成锁文件名称
				String lockFileName = fileName;
				//添加文件锁
				fileManage.addLock("file"+File.separator+"questionTag"+File.separator+"lock"+File.separator,lockFileName);
				
				//保存文件
				fileManage.writeFile(pathDir, fileName,images.getBytes());
				
			}else{
				error.put("images", "图片格式错误");
			}
        }
		tag.setImage(_imagePath+_fileName);
		
		if(error.size() == 0){
			
			tag.setId(questionTagManage.nextNumber());
			tag.setName(name.trim());
			tag.setParentId(Long.parseLong(parentId.trim()));
			tag.setParentIdGroup(parentIdGroup);
			tag.setSort(Integer.parseInt(sort));
			questionTagService.saveQuestionTag(tag);
			
			//删除图片锁
			if(_imagePath != null && !"".equals(_imagePath.trim())){	
				fileManage.deleteLock("file"+File.separator+"questionTag"+File.separator+"lock"+File.separator,_fileName);		
			}
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 标签   修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Long questionTagId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(questionTagId != null){//判断ID是否存在;
			QuestionTag tag = questionTagService.findById(questionTagId);
			if(tag != null){
				if(tag.getImage() != null && !"".equals(tag.getImage())){
					returnValue.put("imagePath",fileManage.fileServerAddress(request)+tag.getImage());
				}
				returnValue.put("tag",tag);//返回消息

				int i=0;
				Map<Long,String> navigation = new LinkedHashMap<Long,String>();
				List<QuestionTag> parentTagList = questionTagService.findAllParentById(tag);
				for(QuestionTag p : parentTagList){
					navigation.put(p.getId(), p.getName());
					i++;
					if(i ==1){
						returnValue.put("parentTag", p);
						
					}
					
				}
				returnValue.put("navigation", navigation);//分类导航
			}
		}else{
			error.put("questionTagId", "标签Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 标签   修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Long questionTagId,String name,String sort,String imagePath,
			MultipartFile images,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();//错误
		
		
		if(name == null || "".equals(name.trim())){
			error.put("name", "标签名称不能为空");
		}else{
			if(name.length() >190){
				error.put("name", "不能大于190个字符");
			}
		}
		if(sort != null && !"".equals(sort.trim())){
			boolean verification = Verification.isPositiveIntegerZero(sort.trim());//数字
			if(verification){
				if(sort.trim().length()>8){
					error.put("sort", "不能大于8位数字");
				}
			}else{
				error.put("sort", "排序必须为大于或等于0的数字");
			}
		}else{
			error.put("sort", "排序不能为空");
		}
		
		
		QuestionTag tag = null;

		String _imagePath = "";
		String _fileName = "";
		if(questionTagId != null && questionTagId >0L){
			//取得对象
			tag = questionTagService.findById(questionTagId);
			
			if(tag != null){
				if(images ==null || images.isEmpty()){//如果图片已上传
					if(imagePath != null && !"".equals(imagePath.trim())){
						imagePath = textFilterManage.deleteBindURL(request, imagePath);
						
						//取得文件名称
						String fileName = FileUtil.getName(imagePath);

						//取得路径名称
						String pathName = FileUtil.getFullPath(imagePath);
						
						//旧路径必须为file/questionTag/开头
						if(imagePath.substring(0, 17).equals("file/questionTag/")){
							//新路径名称
							String newPathName = "file/questionTag/";
							
							//如果新旧路径不一致
							if(!newPathName.equals(pathName)){
								
								//复制文件到新路径
								fileManage.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
								//新建文件锁到新路径
								//生成锁文件名称
								String lockFileName = fileName;
								//添加文件锁
								fileManage.addLock("file"+File.separator+"questionTag"+File.separator+"lock"+File.separator,lockFileName);
			
								
							}
							_imagePath = "file/questionTag/";
							_fileName = fileName;
						}
					}
				}
				
				if(images !=null && !images.isEmpty()){	
					//验证文件类型
					List<String> formatList = new ArrayList<String>();
					formatList.add("gif");
					formatList.add("jpg");
					formatList.add("jpeg");
					formatList.add("bmp");
					formatList.add("png");
					formatList.add("svg");
					boolean authentication = FileUtil.validateFileSuffix(images.getOriginalFilename(),formatList);
					if(authentication){
						//取得文件后缀		
						String ext = FileUtil.getExtension(images.getOriginalFilename());
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"questionTag"+File.separator;
						//构建文件名称
						String fileName = UUIDUtil.getUUID32()+ "." + ext;
						_imagePath = "file/questionTag/";
						_fileName = fileName;
						   
						//生成文件保存目录
						FileUtil.createFolder(pathDir);
						//生成锁文件名称
						String lockFileName = fileName;
						//添加文件锁
						fileManage.addLock("file"+File.separator+"questionTag"+File.separator+"lock"+File.separator,lockFileName);
						  
						//保存文件
						fileManage.writeFile(pathDir, fileName,images.getBytes());
				   }else{
						error.put("images", "图片格式错误");
				   }
				}
			}else{
				error.put("tag", "标签不存在");
			}
		}else{
			error.put("tag", "标签Id不能为空");
		}

		
		if(error.size() == 0){
			QuestionTag new_tag = new QuestionTag(); 
			new_tag.setId(questionTagId);
			new_tag.setName(name.trim());
			new_tag.setSort(Integer.parseInt(sort));
			new_tag.setImage(_imagePath+_fileName);
			
			questionTagService.updateQuestionTag(new_tag);
			
			if(tag.getImage() != null && !"".equals(tag.getImage().trim())){
				if(!(_imagePath+_fileName).equals(tag.getImage())){//如果图片有变化
					//删除旧图片
					//替换路径中的..号
					String oldPathFile = FileUtil.toRelativePath(tag.getImage());
					//删除旧文件
					fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
				}
			}
			
			//删除图片锁
			if(_imagePath != null && !"".equals(_imagePath.trim())){	
				fileManage.deleteLock("file"+File.separator+"questionTag"+File.separator+"lock"+File.separator,_fileName);		
			}
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 标签 删除
	*/
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String delete(ModelMap model,Long questionTagId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(questionTagId != null && questionTagId >0L){
			QuestionTag tag = questionTagService.findById(questionTagId);
			
			//根据标签Id查询子标签(下一节点)
			List<QuestionTag> childTagList = questionTagService.findChildTagById(questionTagId);

			List<Long> tagIdList = new ArrayList<Long>();
			tagIdList.add(tag.getId());
			if(childTagList != null && childTagList.size() >0){
				for(QuestionTag t : childTagList){
					tagIdList.add(t.getId());
				}
			}
			
			int i = questionTagService.deleteQuestionTag(tag);
			if(i >0){
				if(tag.getImage() != null && !"".equals(tag.getImage().trim())){
					
					//删除旧图片
					//替换路径中的..号
					String oldPathFile = FileUtil.toRelativePath(tag.getImage());
					//删除旧文件
					fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
					
				}
				
				if(childTagList != null && childTagList.size() >0){
					
					for(QuestionTag t : childTagList){
						if(t.getImage() != null && !"".equals(t.getImage().trim())){
							
							//删除旧图片
							//替换路径中的..号
							String oldPathFile = FileUtil.toRelativePath(t.getImage());
							//删除旧文件
							fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
							
						}
					}
				}
				
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}
			
		}else{
			error.put("questionTagId", "标签Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 问题标签 查询所有标签
	*/
	@ResponseBody
	@RequestMapping(params="method=allTag", method=RequestMethod.GET)
	public String queryAllTag(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<QuestionTag> questionTagList =  questionTagService.findAllQuestionTag();
		List<QuestionTag> new_questionTagList = new ArrayList<QuestionTag>();//排序后标签
		
		if(questionTagList != null && questionTagList.size() >0){
			
			//组成排序数据
			Iterator<QuestionTag> questionTag_iter = questionTagList.iterator();   
			while(questionTag_iter.hasNext()){   
				QuestionTag questionTag = questionTag_iter.next();
				
				//如果是根节点
				if(questionTag.getParentId().equals(0L)){
					
					new_questionTagList.add(questionTag);
					questionTag_iter.remove();   
			    }  
			}
			//组合子标签
			for(QuestionTag questionTag :new_questionTagList){
				questionTagManage.childQuestionTag(questionTag,questionTagList);
			}
			//排序
			questionTagManage.questionTagSort(new_questionTagList);
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,new_questionTagList));
	}
	
	
	/**
	 * 标签 查询标签分页
	 */
	@ResponseBody
	@RequestMapping(params="method=questionTagPage", method=RequestMethod.GET)
	public String queryQuestionTagPage(ModelMap model,PageForm pageForm,Long parentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
				
				
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		
		//如果所属父类有值
		if(parentId != null && parentId >0L){
			jpql.append(" and o.parentId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
			params.add(parentId);//设置o.parentId=?2参数
		}else{//如果没有父类
			jpql.append(" and o.parentId=?"+ (params.size()+1));
			params.add(0L);
		}
		
		PageView<QuestionTag> pageView = new PageView<QuestionTag>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("sort", "desc");//根据sort字段降序排序
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
				
		//调用分页算法类
		QueryResult<QuestionTag> qr = questionTagService.getScrollData(QuestionTag.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);

		pageView.setQueryResult(qr);
		returnValue.put("pageView", pageView);
		
		//分类导航
		if(parentId != null && parentId >0L){
			Map<Long,String> navigation = new LinkedHashMap<Long,String>();
			QuestionTag questionTag = questionTagService.findById(parentId);
			if(questionTag != null){
				List<QuestionTag> questionTagList = questionTagService.findAllParentById(questionTag);
				for(QuestionTag p : questionTagList){
					navigation.put(p.getId(), p.getName());
				}
				navigation.put(questionTag.getId(), questionTag.getName());
			}
			returnValue.put("navigation", navigation);
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}

}
