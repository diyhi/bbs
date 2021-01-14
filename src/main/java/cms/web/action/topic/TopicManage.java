package cms.web.action.topic;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AtomicLongMap;

import cms.bean.setting.EditorTag;
import cms.bean.staff.SysUsers;
import cms.bean.thumbnail.Thumbnail;
import cms.bean.topic.HideTagType;
import cms.bean.topic.ImageInfo;
import cms.bean.topic.Topic;
import cms.bean.topic.TopicUnhide;
import cms.bean.user.User;
import cms.service.staff.StaffService;
import cms.service.thumbnail.ThumbnailService;
import cms.service.topic.CommentService;
import cms.service.topic.TopicService;
import cms.service.user.UserService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.cache.CacheManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.setting.SettingManage;
import net.sf.cglib.beans.BeanCopier;
/**
 * 话题管理
 *
 */
@Component("topicManage")
public class TopicManage {
	private static final Logger logger = LogManager.getLogger(TopicManage.class);
	
	@Resource TopicManage topicManage;
	@Resource TextFilterManage textFilterManage;
	@Resource TopicService topicService;
	@Resource CommentService commentService;
	@Resource StaffService staffService;
	@Resource UserService userService;
	@Resource ThumbnailService thumbnailService;
	@Resource CacheManage cacheManage;
	@Resource FileManage fileManage;
	@Resource TopicUnhideConfig topicUnhideConfig;
	@Resource SettingManage settingManage;
	
	//key: 话题Id value:展示次数
    private AtomicLongMap<Long> countMap = AtomicLongMap.create();
	
	/**
	 * 增加话题展示次数
	 * @param topicId 话题Id
	 * @param ip 
	 */
	public void addView(Long topicId,String ip){
		
		Long time = new Date().getTime();
		Long oldTime = topicManage.ipRecord(topicId+"_"+ip, time);
		if(oldTime != null &&  time.equals(oldTime)){
			//增加
			countMap.incrementAndGet(topicId);
		}
	}
	
	/**
	 * 提交话题展示次数到数据库(定时提交)
	 */
	@Scheduled(fixedDelay=600000)//10分钟
	public void commitCount(){
		//计数
		int i = 0;
		
		Map<Long,Long> batchCount = new HashMap<Long,Long>();
		
		for (Map.Entry<Long, Long> entry : countMap.asMap().entrySet()) {
			Long topicId = entry.getKey();
			Long count = entry.getValue();
			
			i++;
			if(count > 0L){
				batchCount.put(topicId, count);
				countMap.addAndGet(topicId, count * -1L);//减少
			}
			if(i== 100){//每100条提交一次到数据库
				if(batchCount.size() >0){
					topicService.addViewCount(batchCount);
					batchCount.clear();
				}
				i = 0;
			}
		}
		
		//将剩余的展示次数提交到数据库
		if(batchCount.size() >0){
			topicService.addViewCount(batchCount);
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
	@Cacheable(value="topicManage_cache_ipRecord",key="#key")
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
	 * 根据用户名称删除用户下的所有话题文件
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 */
	public void deleteTopicFile(String userName, boolean isStaff){
		int firstIndex = 0;//起始页
		int maxResult = 100;// 每页显示记录数
		
		String fileNumber = topicManage.generateFileNumber(userName, isStaff);
		
		while(true){
			Map<Long,String> topicContentMap = topicService.findTopicContentByPage(firstIndex, maxResult,userName,isStaff);
			if(topicContentMap == null || topicContentMap.size() == 0){
				break;
			}
			firstIndex = firstIndex+maxResult;
			for (Map.Entry<Long,String> entry : topicContentMap.entrySet()) { 
				Long topicId = entry.getKey();
				String topicContent = entry.getValue();
				
				if(topicContent != null && !"".equals(topicContent.trim())){
					Object[] obj = textFilterManage.readPathName(topicContent,"topic");
					List<Thumbnail> thumbnailList = thumbnailService.findAllThumbnail_cache();
					if(obj != null && obj.length >0){
						List<String> filePathList = new ArrayList<String>();
						List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();//删除缩略图
						
						//删除图片
						List<String> imageNameList = (List<String>)obj[0];		
						for(String imageName :imageNameList){
							filePathList.add(imageName);
							
							if(thumbnailList != null && thumbnailList.size() >0){
								//获取文件路径
								String path = FileUtil.getFullPath(imageName);
								//获取文件名,含后缀
								String name = FileUtil.getName(imageName);
								
								imageInfoList.add(new ImageInfo(path,name));
							}
							
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
							 if(!topicManage.getFileNumber(FileUtil.getBaseName(filePath.trim())).equals(fileNumber)){
								 continue;
							 }
							
							//替换路径中的..号
							filePath = FileUtil.toRelativePath(filePath);
							filePath  = FileUtil.toSystemPath(filePath);
							//删除旧路径文件
							Boolean state = fileManage.deleteFile(filePath);
							if(state != null && state == false){
								 //替换指定的字符，只替换第一次出现的
								filePath = StringUtils.replaceOnce(filePath, "file"+File.separator+"topic"+File.separator, "");
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"topic"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
							}
						}
						//删除缩略图
						if(imageInfoList != null && imageInfoList.size()>0){
							fileManage.deleteThumbnail(thumbnailList, imageInfoList);
						}
						
					}
				}
				//清空目录
				Boolean state_ = fileManage.removeDirectory("file"+File.separator+"comment"+File.separator+topicId+File.separator);
				if(state_ != null && state_ == false){
					//创建删除失败目录文件
					fileManage.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+"#"+topicId);
				}
			}
			
		}
	}
	
	
	
	/**
	 * 根据用户名称删除用户下的所有评论文件
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 */
	public void deleteCommentFile(String userName, boolean isStaff){
		int firstIndex = 0;//起始页
		int maxResult = 100;// 每页显示记录数
		
		String fileNumber = topicManage.generateFileNumber(userName, isStaff);
		
		while(true){
			List<String> topicContentList = commentService.findCommentContentByPage(firstIndex, maxResult,userName,isStaff);
			if(topicContentList == null || topicContentList.size() == 0){
				break;
			}
			firstIndex = firstIndex+maxResult;
			for (String topicContent: topicContentList) { 
				if(topicContent != null && !"".equals(topicContent.trim())){
					//删除图片
					List<String> imageNameList = textFilterManage.readImageName(topicContent,"comment");
					if(imageNameList != null && imageNameList.size() >0){
						for(String imagePath : imageNameList){
							//如果验证不是当前用户上传的文件，则不删除锁
							 if(!topicManage.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
								 continue;
							 }
							
							
							//替换路径中的..号
							imagePath = FileUtil.toRelativePath(imagePath);
							imagePath  = FileUtil.toSystemPath(imagePath);
							
							Boolean state = fileManage.deleteFile(imagePath);
							
							if(state != null && state == false){	
								//替换指定的字符，只替换第一次出现的
								imagePath = StringUtils.replaceOnce(imagePath, "file"+File.separator+"comment"+File.separator, "");
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+FileUtil.toUnderline(imagePath));
							
							}
						}
					}
				}
			}
		}
	}
	
	
	
	/**
	 * 查询话题缓存
	 * @param topicId 话题Id
	 */
	public Topic queryTopicCache(Long topicId){
		
		Topic topic = (Topic)cacheManage.getCache("topicManage_cache_topic", String.valueOf(topicId));
		if(topic == null){
			topic = topicService.findById(topicId);
			if(topic != null){
				topicManage.addTopicCache(topic);//添加到缓存
			}
		}
		return topic;
	}
	
	/**
	 * 添加话题缓存
	 * @param topic
	 */
	public void addTopicCache(Topic topic){
		if(topic != null){
			cacheManage.addCache("topicManage_cache_topic",  String.valueOf(topic.getId()), topic);
		}
	}
	/**
	 * 删除话题缓存
	 * @param topicId 话题Id
	 */
	public void deleteTopicCache(Long topicId){
		cacheManage.deleteCache("topicManage_cache_topic",String.valueOf(topicId));
	}
	
	/**
	 * 话题编辑器允许使用标签
	 * @return List<String> 类型json格式
	*/
	public String availableTag(){
		List<String> tag = new ArrayList<String>();
		EditorTag editor = settingManage.readTopicEditorTag();
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
			if(editor.isFile()){//文件
				tag.add("insertfile");
			}
			if(editor.isEmbedVideo() || editor.isUploadVideo()){//视频
				tag.add("media");
				
				if(editor.isEmbedVideo()){//嵌入视频
					tag.add("embedVideo");
				}
				if(editor.isUploadVideo()){//上传视频
					tag.add("uploadVideo");
				}
			}
			
			
			if(editor.isHidePassword()){//输入密码可见
				tag.add("hidePassword");
			}
			if(editor.isHideComment()){//评论话题可见
				tag.add("hideComment");		
			}
			if(editor.isHideGrade()){//达到等级可见
				tag.add("hideGrade");	
			}
			if(editor.isHidePoint()){//积分购买可见
				tag.add("hidePoint");	
			}
			if(editor.isHideAmount()){//余额购买可见
				tag.add("hideAmount");
			}

			if(editor.isFullscreen()){//全屏显示
				tag.add("fullscreen");
			}
		}
		return JsonUtils.toJSONString(tag);
	}
	
	
	/**--------------------------------------------- 话题取消隐藏 -----------------------------------------------**/
	
	/**
     * 生成'话题取消隐藏Id'
     * 话题取消隐藏Id格式（取消隐藏的用户Id-隐藏标签类型-话题Id）
     * @param userId 用户Id
     * @param hideTagTypeId 隐藏标签类型Id
     * @param topicId 话题Id
     * @return
     */
    public String createTopicUnhideId(Long userId,Integer hideTagTypeId,Long topicId){
    	return userId+"_"+hideTagTypeId+"_"+topicId;
    }
	
	/**
	 * 解析'话题取消隐藏Id'的话题Id(后N位)
	 * @param topicUnhideId 话题取消隐藏Id
	 * @return
	 */
    public int analysisTopicId(String topicUnhideId){
    	String[] idGroup = topicUnhideId.split("_");
    	Long topicId = Long.parseLong(idGroup[2]);
    	
    	//选取得后N位话题Id
    	String after_topicId = String.format("%04d", Math.abs(topicId)%10000);
    	return Integer.parseInt(after_topicId);
    } 
	
	
    /**
     * 校验话题取消隐藏Id
     * @param topicUnhideId 话题取消隐藏Id
     * @return
     */
    public boolean verificationTopicUnhideId(String topicUnhideId){
    	if(topicUnhideId != null && !"".equals(topicUnhideId.trim())){
    		String[] idGroup = topicUnhideId.split("_");
    		for(String id : idGroup){
    			boolean verification = Verification.isPositiveIntegerZero(id);//数字
    			if(!verification){
    				return false;
    			}
    			return true;
    		}	
			
    	}
    	return false;
    }
	
	
    /**
     * 生成'话题取消隐藏'对象
     * @return
     */
    public Object createTopicUnhideObject(TopicUnhide topicUnhide){
    	//表编号
		int tableNumber = topicUnhideConfig.topicUnhideIdRemainder(topicUnhide.getId());
		if(tableNumber == 0){//默认对象
			return topicUnhide;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.topic.TopicUnhide_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(TopicUnhide.class,object.getClass(), false); 
			
				copier.copy(topicUnhide,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题取消隐藏对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题取消隐藏对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题取消隐藏对象",e);
		        }
			}	
		}
		return null;
    }
    
    /**
     * 生成处理'隐藏标签'Id
     * @param topicId 话题Id
     * @param topicContentUpdateMark 话题内容修改标记
     * @param visibleTagList 允许可见的隐藏标签
     * @return
     */
    public String createProcessHideTagId(Long topicId,Integer topicContentUpdateMark,List<Integer> visibleTagList){
    	String id = topicId+"|";
    	
    	if(visibleTagList != null && visibleTagList.size() >0){
    		if(visibleTagList.contains(HideTagType.PASSWORD.getName())){
    			id+="1,";
    		}else{
    			id+="0,";
    		}
    		if(visibleTagList.contains(HideTagType.COMMENT.getName())){
    			id+="1,";
    		}else{
    			id+="0,";
    		}
    		if(visibleTagList.contains(HideTagType.GRADE.getName())){
    			id+="1,";
    		}else{
    			id+="0,";
    		}
    		if(visibleTagList.contains(HideTagType.POINT.getName())){
    			id+="1,";
    		}else{
    			id+="0,";
    		}
    		if(visibleTagList.contains(HideTagType.AMOUNT.getName())){
    			id+="1,";
    		}else{
    			id+="0,";
    		}
    	}
    	if(topicContentUpdateMark != null){
			id+="|"+topicContentUpdateMark;
		}else{
			id+="|";
		}
    	return id;
    }
   
    /**
     * 生成处理'上传的文件完整路径名称'Id
     * @param topicId 话题Id
     * @param topicContentUpdateMark 话题内容修改标记
     * @param fullFileNameMap 完整路径名称 key: 完整路径名称 value: 重定向接口
     * @return
     */
    public String createProcessFullFileNameId(Long topicId,Integer topicContentUpdateMark,Map<String,String> fullFileNameMap){
    	String id = topicId+"|";

    	StringBuffer sb = new StringBuffer("");
    	for (Map.Entry<String,String> entry : fullFileNameMap.entrySet()) {
    		sb.append(entry.getValue()).append("|");
    		
    	}
    	id+= cms.utils.MD5.getMD5(sb.toString())+"|";
    	if(topicContentUpdateMark != null){
			id+="|"+topicContentUpdateMark;
		}else{
			id+="|";
		}
    	return id;
    }
    
    
    
    /**
	 * 查询缓存 查询'话题取消隐藏'
	 * @param topicUnhideId 话题取消隐藏Id
	 * @return
	 */
	@Cacheable(value="topicManage_cache_findTopicUnhideById",key="#topicUnhideId")
	public TopicUnhide query_cache_findTopicUnhideById(String topicUnhideId){
		return topicService.findTopicUnhideById(topicUnhideId);
	}
	/**
	 * 删除缓存 话题取消隐藏
	 * @param topicUnhideId 话题取消隐藏Id
	 * @return
	 */
	@CacheEvict(value="topicManage_cache_findTopicUnhideById",key="#topicUnhideId")
	public void delete_cache_findTopicUnhideById(String topicUnhideId){
	}
    
	/**
	 * 查询缓存 查询用户是否评论话题
	 * @param topicId 话题Id
	 * @param userName 用户名称
	 * @return
	 */
	@Cacheable(value="topicManage_cache_findWhetherCommentTopic",key="#topicId+'_'+#userName")
	public Boolean query_cache_findWhetherCommentTopic(Long topicId,String userName){
		return commentService.findWhetherCommentTopic(topicId, userName);
	}
	/**
	 * 删除缓存 查询用户是否评论话题
	 * @param topicId 话题Id
	 * @param userName 用户名称
	 * @return
	 */
	@CacheEvict(value="topicManage_cache_findWhetherCommentTopic",key="#topicId+'_'+#userName")
	public void delete_cache_findWhetherCommentTopic(Long topicId,String userName){
	}
	
	/**
	 * 查询缓存 解析隐藏标签
	 * @param html 富文本内容
	 * @param topicId 话题Id
	 * @return
	 */
	@Cacheable(value="topicManage_cache_analysisHiddenTag",key="#topicId")
	public Map<Integer,Object> query_cache_analysisHiddenTag(String html,Long topicId){
		return textFilterManage.analysisHiddenTag(html);
	}
	/**
	 * 删除缓存 解析隐藏标签
	 * @param topicId 话题Id
	 * @return
	 */
	@CacheEvict(value="topicManage_cache_analysisHiddenTag",key="#topicId")
	public void delete_cache_analysisHiddenTag(Long topicId){
	}
	
	
	/**
	 * 查询缓存 处理隐藏标签(缓存不做删除处理，到期自动失效，由话题'随机数'做参数,确保查询为最新值)
	 * @param html 富文本内容
	 * @param visibleTagList 允许可见的隐藏标签
	 * @param processHideTagId 处理'隐藏标签'Id
	 * @return
	 */
	@Cacheable(value="topicManage_cache_processHiddenTag",key="#processHideTagId")
	public String query_cache_processHiddenTag(String html,List<Integer> visibleTagList,String processHideTagId){
		
		return textFilterManage.processHiddenTag(html,visibleTagList);
	}


	
	/**
	 * 查询缓存 解析上传的文件完整路径名称
	 * @param html 富文本内容
	 * @param topicId 话题Id
	 * @param baseUrlList 基础URL集合
	 * @return
	 */
	@Cacheable(value="topicManage_cache_analysisFullFileName",key="#topicId")
	public Map<String,String> query_cache_analysisFullFileName(String html,Long topicId,List<String> baseUrlList){
		return textFilterManage.analysisFullFileName(html,"topic",baseUrlList);
	}
	/**
	 * 删除缓存 解析上传的文件完整路径名称
	 * @param topicId 话题Id
	 * @return
	 */
	@CacheEvict(value="topicManage_cache_analysisFullFileName",key="#topicId")
	public void delete_cache_analysisFullFileName(Long topicId){
	}
	
	/**
	 * 查询缓存 处理上传的文件完整路径名称(缓存不做删除处理，到期自动失效，由话题'最后修改时间'做参数,确保查询为最新值)
	 * @param html 富文本内容
	 * @param item 项目
	 * @param newFullFileNameMap 新的完整路径名称 key: 完整路径名称 value: 重定向接口
	 * @param processFullFileNameId 处理'上传的文件完整路径名称'Id
	 * @param baseUrlList 基础URL集合
	 * @return
	 */
	@Cacheable(value="topicManage_cache_processFullFileName",key="#processFullFileNameId")
	public String query_cache_processFullFileName(String html,String item,Map<String,String> newFullFileNameMap,String processFullFileNameId, List<String> baseUrlList){
		return textFilterManage.processFullFileName(html,item,newFullFileNameMap,baseUrlList);
	}
	
	
	/**
	 * 查询缓存 标记修改话题状态
	 * @param topicId 话题Id
	 * @param randomNumber 随机数
	 * @return
	 */
	@Cacheable(value="topicManage_cache_markUpdateTopicStatus",key="#topicId")
	public Integer query_cache_markUpdateTopicStatus(Long topicId, Integer randomNumber){
		return randomNumber;
	}
	/**
	 * 删除缓存 标记修改话题状态
	 * @param topicId 话题Id
	 * @return
	 */
	@CacheEvict(value="topicManage_cache_markUpdateTopicStatus",key="#topicId")
	public void delete_cache_markUpdateTopicStatus(Long topicId){
	}

}
