package cms.web.action.topic;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AtomicLongMap;

import cms.bean.staff.SysUsers;
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.staff.StaffService;
import cms.service.topic.CommentService;
import cms.service.topic.TopicService;
import cms.service.user.UserService;
import cms.web.action.FileManage;
import cms.web.action.TextFilterManage;
import cms.web.action.cache.CacheManage;
/**
 * 话题管理
 *
 */
@Component("topicManage")
public class TopicManage {
	@Resource TopicManage topicManage;
	@Resource TextFilterManage textFilterManage;
	@Resource TopicService topicService;
	@Resource CommentService commentService;
	@Resource StaffService staffService;
	@Resource UserService userService;
	@Resource FileManage fileManage;
	
	@Resource CacheManage cacheManage;
	
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
						Boolean state_ = fileManage.removeDirectory("file"+File.separator+"comment"+File.separator+topicId+File.separator);
						if(state_ != null && state_ == false){
							//创建删除失败目录文件
							fileManage.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+"#"+topicId);
						}
						
					}
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
							 if(!topicManage.getFileNumber(fileManage.getBaseName(imagePath.trim())).equals(fileNumber)){
								 continue;
							 }
							
							
							//替换路径中的..号
							imagePath = fileManage.toRelativePath(imagePath);
							//替换路径分割符
							imagePath = StringUtils.replace(imagePath, "/", File.separator);
							
							Boolean state = fileManage.deleteFile(imagePath);
							
							if(state != null && state == false){	
								//替换指定的字符，只替换第一次出现的
								imagePath = StringUtils.replaceOnce(imagePath, "file"+File.separator+"comment"+File.separator, "");
								imagePath = StringUtils.replace(imagePath, File.separator, "_");//替换所有出现过的字符
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+imagePath);
							
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
		return (Topic)cacheManage.getCache("topicManage_cache_topic", String.valueOf(topicId));
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
	
}
