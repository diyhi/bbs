package cms.web.action.question;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.util.concurrent.AtomicLongMap;

import cms.bean.question.AppendQuestionItem;
import cms.bean.question.Question;
import cms.bean.question.QuestionTagAssociation;
import cms.bean.setting.EditorTag;
import cms.bean.staff.SysUsers;
import cms.bean.user.User;
import cms.service.question.AnswerService;
import cms.service.question.QuestionService;
import cms.service.staff.StaffService;
import cms.service.user.UserService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.setting.SettingManage;
/**
 * 问题管理
 *
 */
@Component("questionManage")
public class QuestionManage {

	@Resource QuestionManage questionManage;
	@Resource TextFilterManage textFilterManage;
	@Resource QuestionService questionService;
	@Resource StaffService staffService;
	@Resource UserService userService;
	@Resource SettingManage settingManage;
	@Resource AnswerService answerService;
	@Resource FileManage fileManage;
	
	//key: 问题Id value:展示次数
    private AtomicLongMap<Long> countMap = AtomicLongMap.create();
	
	/**
	 * 增加问题展示次数
	 * @param questionId 问题Id
	 * @param ip 
	 */
	public void addView(Long questionId,String ip){
		
		Long time = new Date().getTime();
		Long oldTime = questionManage.ipRecord(questionId+"_"+ip, time);
		if(oldTime != null &&  time.equals(oldTime)){
			//增加
			countMap.incrementAndGet(questionId);
		}
	}
	
	/**
	 * 提交问题展示次数到数据库(定时提交)
	 */
	@Scheduled(fixedDelay=600000)//10分钟
	public void commitCount(){
		//计数
		int i = 0;
		
		Map<Long,Long> batchCount = new HashMap<Long,Long>();
		
		for (Map.Entry<Long, Long> entry : countMap.asMap().entrySet()) {
			Long questionId = entry.getKey();
			Long count = entry.getValue();
			
			i++;
			if(count > 0L){
				batchCount.put(questionId, count);
				countMap.addAndGet(questionId, count * -1L);//减少
			}
			if(i== 100){//每100条提交一次到数据库
				if(batchCount.size() >0){
					questionService.addViewCount(batchCount);
					batchCount.clear();
				}
				i = 0;
			}
		}
		
		//将剩余的展示次数提交到数据库
		if(batchCount.size() >0){
			questionService.addViewCount(batchCount);
		}

		//删除所有计数器值为零的映射
		countMap.removeAllZeros();
	}
	
	
	
	/**
	 * 话题展示IP记录(每话题每IP 24小时内只统计一次点击次数)
	 * @param key 话题Id_用户IP
	 * @param time 当前时间
	 * @return
	 */
	@Cacheable(value="questionManage_cache_ipRecord",key="#key")
	public Long ipRecord(String key,Long time){
		return time;
	}
	
	
	/**
	 * 生成上传文件编号
	 * @param userName 用户名称
	 * @param isStaff 是否是员工   true:员工   false:会员
	 * @return
	 */
	public String generateFileNumber(String userName, Boolean isStaff){
		String number = "";

		if(isStaff != null && userName != null && !"".equals(userName.trim())){
			if(isStaff == true){//员工
				
				SysUsers sysUsers = staffService.findByUserAccount(userName.trim());
				if(sysUsers != null){
					number = "a"+sysUsers.getUserId();
				}
			}else{//会员
				User user = userService.findUserByUserName(userName.trim());
				if(user != null){
					number = "b"+user.getId();
				}
			}
		}
		return number;
	}
	/**
	 * 根据文件名获取文件编号
	 * @param fileName 文件名称(不含后缀)
	 * @return
	 */
	public String getFileNumber(String fileName){
		String number = "";

		if(fileName != null && !"".equals(fileName.trim())){
			fileName = fileName.trim();
			if(fileName.length() >33){
				String isStaff = fileName.substring(32,33);
				if(isStaff.equals("a") || isStaff.equals("b")){
					number = fileName.substring(32, fileName.length());
				}
			}
		}
		return number;
	}
	
	
	
	/**
	 * 根据用户名称删除用户下的所有问题文件
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 */
	public void deleteQuestionFile(String userName, boolean isStaff){
		int firstIndex = 0;//起始页
		int maxResult = 100;// 每页显示记录数
		
		String fileNumber = questionManage.generateFileNumber(userName, isStaff);
		
		while(true){
			List<Question> questionContentList = questionService.findQuestionContentByPage(firstIndex, maxResult,userName,isStaff);
			if(questionContentList == null || questionContentList.size() == 0){
				break;
			}
			firstIndex = firstIndex+maxResult;
			for (Question question :questionContentList) { 
				Long questionId = question.getId();
				String questionContent = question.getContent();
				
				
				//删除最后一个逗号
				String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

				List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
				if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
					for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
						questionContent += appendQuestionItem.getContent();
					}
				}
				
				if(questionContent != null && !"".equals(questionContent.trim())){
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
							 if(!questionManage.getFileNumber(FileUtil.getBaseName(filePath.trim())).equals(fileNumber)){
								 continue;
							 }
							
							//替换路径中的..号
							filePath = FileUtil.toRelativePath(filePath);
							filePath = FileUtil.toSystemPath(filePath);
							//删除旧路径文件
							Boolean state = fileManage.deleteFile(filePath);
							if(state != null && state == false){
								 //替换指定的字符，只替换第一次出现的
								filePath = StringUtils.replaceOnce(filePath, "file"+File.separator+"question"+File.separator, "");
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
							}
						}
						
					}
				}
				//清空目录
				Boolean state_ = fileManage.removeDirectory("file"+File.separator+"answer"+File.separator+questionId+File.separator);
				if(state_ != null && state_ == false){
					//创建删除失败目录文件
					fileManage.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+"#"+questionId);
				}
			}
			
		}
	}
	
	
	
	/**
	 * 根据用户名称删除用户下的所有答案文件
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 */
	public void deleteAnswerFile(String userName, boolean isStaff){
		int firstIndex = 0;//起始页
		int maxResult = 100;// 每页显示记录数
		
		String fileNumber = questionManage.generateFileNumber(userName, isStaff);
		
		while(true){
			List<String> contentList = answerService.findAnswerContentByPage(firstIndex, maxResult,userName,isStaff);
			if(contentList == null || contentList.size() == 0){
				break;
			}
			firstIndex = firstIndex+maxResult;
			for (String content: contentList) { 
				if(content != null && !"".equals(content.trim())){
					//删除图片
					List<String> imageNameList = textFilterManage.readImageName(content,"answer");
					if(imageNameList != null && imageNameList.size() >0){
						for(String imagePath : imageNameList){
							//如果验证不是当前用户上传的文件，则不删除锁
							 if(!questionManage.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
								 continue;
							 }
							
							
							//替换路径中的..号
							imagePath = FileUtil.toRelativePath(imagePath);
							
							imagePath = FileUtil.toSystemPath(imagePath);
							
							Boolean state = fileManage.deleteFile(imagePath);
							if(state != null && state == false){	
								//替换指定的字符，只替换第一次出现的
								imagePath = StringUtils.replaceOnce(imagePath, "file"+File.separator+"answer"+File.separator, "");
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+FileUtil.toUnderline(imagePath));
							
							}
						}
					}
				}
			}
		}
	}
	
	
	
	
	
	

	
	/**
	 * 问题编辑器允许使用标签
	 * @return List<String> 类型json格式
	*/
	public String availableTag(){
		List<String> tag = new ArrayList<String>();
		EditorTag editor = settingManage.readQuestionEditorTag();
		if(editor != null){
			//标签区域1
			boolean area_1 = false;
			//标签区域2
			boolean area_2 = false;
			//标签区域3
			boolean area_3 = false;
			
			tag.add("source");
		//	tag.add("|");
			if(editor.isCode()){//代码
				tag.add("code");
				area_1 = true;
			}
			if(editor.isFontname()){//字体
				tag.add("fontname");
				area_1 = true;
			}
			if(editor.isFontsize()){//文字大小
				tag.add("fontsize");
				area_1 = true;
			}
			if(area_1 == true){
		//		tag.add("|");
			}
			
			if(editor.isForecolor()){//文字颜色
				tag.add("forecolor");
				area_2 = true;
			}
			if(editor.isHilitecolor()){//文字背景
				tag.add("hilitecolor");
				area_2 = true;
			}
			if(editor.isBold()){//粗体
				tag.add("bold");
				area_2 = true;
			}
			if(editor.isItalic()){//斜体
				tag.add("italic");
				area_2 = true;
			}
			if(editor.isUnderline()){//下划线
				tag.add("underline");
				area_2 = true;
			}
			if(editor.isRemoveformat()){//删除格式
				tag.add("removeformat");
				area_2 = true;
			}
			if(editor.isLink()){//超级链接
				tag.add("link");
				area_2 = true;
			}
			if(editor.isUnlink()){//取消超级链接
				tag.add("unlink");
				area_2 = true;
			}
			if(area_2 == true){
			//	tag.add("|");
			}
			
			if(editor.isJustifyleft()){//左对齐
				tag.add("justifyleft");
				area_3 = true;
			}
			if(editor.isJustifycenter()){//居中
				tag.add("justifycenter");
				area_3 = true;
			}
			if(editor.isJustifyright()){//右对齐
				tag.add("justifyright");
				area_3 = true;
			}
			if(editor.isInsertorderedlist()){//编号
				tag.add("insertorderedlist");
				area_3 = true;
			}
			if(editor.isInsertunorderedlist()){//项目符号
				tag.add("insertunorderedlist");
				area_3 = true;
			}
			if(area_3 == true){
			//	tag.add("|");
			}
			
			if(editor.isEmoticons()){//插入表情
				tag.add("emoticons");
			}
			if(editor.isImage()){//图片
				tag.add("image");
			}
			/**
			if(editor.isFile()){//文件
				tag.add("insertfile");
			}**/
			if(editor.isFullscreen()){//全屏显示
				tag.add("fullscreen");
			}
		}
		return JsonUtils.toJSONString(tag);
	}
	
	
	/**
	 * 查询缓存 根据Id查询问题
	 * @param questionId 问题Id
	 * @return
	 */
	@Cacheable(value="questionManage_cache_findById",key="#questionId")
	public Question query_cache_findById(Long questionId){
		return questionService.findById(questionId);
	}
	/**
	 * 删除缓存 根据Id查询问题
	 * @param questionId 问题Id
	 * @return
	 */
	@CacheEvict(value="questionManage_cache_findById",key="#questionId")
	public void delete_cache_findById(Long questionId){
	}
	
	/**
	 * 查询缓存 根据问题Id查询问题标签关联
	 * @param questionId 问题Id
	 * @return
	 */
	@Cacheable(value="questionManage_cache_findQuestionTagAssociationByQuestionId",key="#questionId")
	public List<QuestionTagAssociation> query_cache_findQuestionTagAssociationByQuestionId(Long questionId){
		return questionService.findQuestionTagAssociationByQuestionId(questionId);
	}
	/**
	 * 删除缓存 根据问题Id查询问题标签关联
	 * @param questionId 问题Id
	 * @return
	 */
	@CacheEvict(value="questionManage_cache_findQuestionTagAssociationByQuestionId",key="#questionId")
	public void delete_cache_findQuestionTagAssociationByQuestionId(Long questionId){
	}

	
}
