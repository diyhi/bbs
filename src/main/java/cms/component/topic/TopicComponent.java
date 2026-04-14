package cms.component.topic;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.setting.SettingComponent;
import cms.component.user.UserCacheManager;
import cms.dto.ReadPathResult;
import cms.dto.topic.HideTagType;
import cms.dto.topic.ImageInfo;
import cms.model.setting.EditorTag;
import cms.model.staff.SysUsers;
import cms.model.thumbnail.Thumbnail;
import cms.model.topic.TopicIndex;
import cms.model.topic.TopicUnhide;
import cms.model.user.User;
import cms.repository.staff.StaffRepository;
import cms.repository.thumbnail.ThumbnailRepository;
import cms.repository.topic.CommentRepository;
import cms.repository.topic.TopicIndexRepository;
import cms.repository.topic.TopicRepository;
import cms.repository.user.UserRepository;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AtomicLongMap;

import cms.utils.FileUtil;

import org.springframework.cglib.beans.BeanCopier;
/**
 * 话题组件
 *
 */
@Component("topicComponent")
public class TopicComponent {
	private static final Logger logger = LogManager.getLogger(TopicComponent.class);

	@Resource TopicCacheManager topicCacheManager;
	@Resource TextFilterComponent textFilterComponent;
	@Resource TopicRepository topicRepository;
	@Resource CommentRepository commentRepository;
	@Resource StaffRepository staffRepository;
	@Resource UserRepository userRepository;
	@Resource ThumbnailRepository thumbnailRepository;
	@Resource FileComponent fileComponent;
	@Resource TopicUnhideConfig topicUnhideConfig;
	@Resource SettingComponent settingComponent;
	@Resource UserCacheManager userCacheManager;
	@Resource TopicIndexRepository topicIndexRepository;
    @Resource JsonComponent jsonComponent;
	
	//key: 话题Id value:展示次数
    private final AtomicLongMap<Long> countMap = AtomicLongMap.create();
    
   
	
	/**
	 * 增加话题展示次数
	 * @param topicId 话题Id
	 * @param ip IP
	 */
	public void addView(Long topicId,String ip){
		
		Long time = new Date().getTime();
		Long oldTime = topicCacheManager.ipRecord(topicId+"_"+ip, time);
		if(oldTime != null &&  time.equals(oldTime)){
			//增加
			countMap.incrementAndGet(topicId);
		}
	}
	
	/**
	 * 读取话题本地展示次数
	 * @param topicId 话题Id
	 */
	public Long readLocalView(Long topicId){
		Long count = countMap.asMap().get(topicId);
		if(count != null){
			return count;
		}
		return 0L;
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
					topicRepository.addViewCount(batchCount);
					batchCount.clear();
				}
				i = 0;
			}
		}
		
		//将剩余的展示次数提交到数据库
		if(batchCount.size() >0){
			topicRepository.addViewCount(batchCount);
		}

		//删除所有计数器值为零的映射
		countMap.removeAllZeros();
	}
	
	
	

	
	/**
	 * 生成上传文件编号
	 * @param userName 用户名称
	 * @param isStaff 是否是员工   true:员工   false:会员
	 * @return
	 */
	public String generateFileNumber(String userName, Boolean isStaff){
		String number = "";

		if(isStaff != null && userName != null && !userName.trim().isEmpty()){
			if(isStaff){//员工
				
				SysUsers sysUsers = staffRepository.findByUserAccount(userName.trim());
				if(sysUsers != null){
					number = "a"+sysUsers.getUserId();
				}
			}else{//会员
				User user = userRepository.findUserByUserName(userName.trim());
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

		if(fileName != null && !fileName.trim().isEmpty()){
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
		
		String fileNumber = this.generateFileNumber(userName, isStaff);
		
		while(true){
			Map<Long,String> topicContentMap = topicRepository.findTopicContentByPage(firstIndex, maxResult,userName,isStaff);
			if(topicContentMap == null || topicContentMap.size() == 0){
				break;
			}
			firstIndex = firstIndex+maxResult;
			for (Map.Entry<Long,String> entry : topicContentMap.entrySet()) { 
				Long topicId = entry.getKey();
				String topicContent = entry.getValue();
				
				if(topicContent != null && !topicContent.trim().isEmpty()){
                    ReadPathResult readPathResult = textFilterComponent.readPathName(topicContent,"topic");
					List<Thumbnail> thumbnailList = thumbnailRepository.findAllThumbnail_cache();
                    List<String> filePathList = new ArrayList<String>();
                    List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();//删除缩略图

                    //删除图片
                    List<String> imageNameList = readPathResult.getImageNameList();
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
                    //删除影音
                    List<String> mediaNameList = readPathResult.getMediaNameList();
                    for(String mediaName :mediaNameList){
                        filePathList.add(mediaName);
                    }
                    //删除文件
                    List<String> fileNameList = readPathResult.getFileNameList();
                    for(String fileName :fileNameList){
                        filePathList.add(fileName);
                    }

                    for(String filePath :filePathList){


                         //如果验证不是当前用户上传的文件，则不删除
                         if(!this.getFileNumber(FileUtil.getBaseName(filePath.trim())).equals(fileNumber)){
                             continue;
                         }

                        //替换路径中的..号
                        filePath = FileUtil.toRelativePath(filePath);
                        filePath  = FileUtil.toSystemPath(filePath);
                        //删除旧路径文件
                        Boolean state = fileComponent.deleteFile(filePath);
                        if(state != null && !state){
                             //替换指定的字符，只替换第一次出现的
                            filePath = Strings.CS.replaceOnce(filePath, "file"+File.separator+"topic"+File.separator, "");
                            //创建删除失败文件
                            fileComponent.failedStateFile("file"+File.separator+"topic"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
                        }
                    }
                    //删除缩略图
                    if(imageInfoList != null && imageInfoList.size()>0){
                        fileComponent.deleteThumbnail(thumbnailList, imageInfoList);
                    }
						

				}
				//清空目录
				Boolean state_ = fileComponent.removeDirectory("file"+File.separator+"comment"+File.separator+topicId+File.separator);
				if(state_ != null && !state_){
					//创建删除失败目录文件
					fileComponent.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+"#"+topicId);
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
		
		String fileNumber = this.generateFileNumber(userName, isStaff);
		
		while(true){
			List<String> topicContentList = commentRepository.findCommentContentByPage(firstIndex, maxResult,userName,isStaff);
			if(topicContentList == null || topicContentList.size() == 0){
				break;
			}
			firstIndex = firstIndex+maxResult;
			for (String topicContent: topicContentList) { 
				if(topicContent != null && !topicContent.trim().isEmpty()){
					//删除图片
					List<String> imageNameList = textFilterComponent.readImageName(topicContent,"comment");
					if(imageNameList != null && imageNameList.size() >0){
						for(String imagePath : imageNameList){
							//如果验证不是当前用户上传的文件，则不删除锁
							 if(!this.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
								 continue;
							 }
							
							
							//替换路径中的..号
							imagePath = FileUtil.toRelativePath(imagePath);
							imagePath  = FileUtil.toSystemPath(imagePath);
							
							Boolean state = fileComponent.deleteFile(imagePath);
							
							if(state != null && !state){
								//替换指定的字符，只替换第一次出现的
								imagePath = Strings.CS.replaceOnce(imagePath, "file"+File.separator+"comment"+File.separator, "");
								
								//创建删除失败文件
								fileComponent.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+FileUtil.toUnderline(imagePath));
							
							}
						}
					}
				}
			}
		}
	}
	
	
	

	
	/**
	 * 话题编辑器允许使用标签
	 * @return List<String> 类型json格式
	*/
	public String availableTag(){
		List<String> tag = new ArrayList<String>();
		EditorTag editor = settingComponent.readTopicEditorTag();
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
			if(area_1){
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
			if(area_2){
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
			if(area_3){
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
			if(editor.isMention()){//提及
				tag.add("mention");
			}
			if(editor.isAi()){//人工智能
				tag.add("ai");
			}
			if(editor.isFullscreen()){//全屏显示
				tag.add("fullscreen");
			}
			
		}
		return jsonComponent.toJSONString(tag);
	}
	
	
	/**--------------------------------------------- 话题取消隐藏 -----------------------------------------------**/
	

	
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
				c = Class.forName("cms.model.topic.TopicUnhide_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成话题取消隐藏对象",e);
                }
            } catch (NoSuchMethodException e) {
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
     * 修改话题为待审核
     * @param userName 用户名称
     * @param topicId 话题Id
     */
    public void updatePendingReview(String userName,Long topicId){
    	int i = topicRepository.updateTopicPendingReview(topicId);
    	
		if(i >0){
			User user = userCacheManager.query_cache_findUserByUserName(userName);
			if(user != null){
				//修改话题状态
				userRepository.updateUserDynamicTopicStatus(user.getId(),userName,topicId,10);
			}
			//更新索引
			topicIndexRepository.addTopicIndex(new TopicIndex(String.valueOf(topicId),2));
            topicCacheManager.deleteTopicCache(topicId);//删除缓存
		}
	}
    
    
    /**
     * 标记删除话题
     * @param userName 用户名称
     * @param topicId 话题Id
     */
    public void markDeleteTopic(String userName,Long topicId){
    	int i = topicRepository.markDelete(topicId);

    	User user = userCacheManager.query_cache_findUserByUserName(userName);
    	if(i >0 && user != null){
    		//修改话题状态
    		userRepository.softDeleteUserDynamicByTopicId(user.getId(),userName,topicId);
    		
    		//更新索引
        	topicIndexRepository.addTopicIndex(new TopicIndex(String.valueOf(topicId),2));
            topicCacheManager.deleteTopicCache(topicId);//删除缓存
    	}
    	
    	
    	
    }
    

}
