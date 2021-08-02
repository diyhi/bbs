package cms.web.action.template;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import cms.bean.template.Forum;
import cms.bean.template.Forum_AdvertisingRelated_Image;
import cms.bean.template.Forum_CustomForumRelated_CustomHTML;
import cms.bean.template.Layout;
import cms.service.template.TemplateService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.localImpl.LocalFileManage;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

/**
 * 布局管理
 *
 */
@Component("layoutManage")
public class LayoutManage{
	private static final Logger logger = LogManager.getLogger(LayoutManage.class);
	
	@Resource TemplateService templateService;//通过接口引用代理返回的对象
	@Resource TextFilterManage textFilterManage;
	@Resource LocalFileManage localFileManage;
	@Resource LayoutManage layoutManage;
	
	
	private AtomicLong number = new AtomicLong(new Random().nextLong());
	
	private List<AntPathRequestMatcher> antPathRequestMatcherList = new ArrayList<AntPathRequestMatcher>(Arrays.asList(new AntPathRequestMatcher("/user/**")));
	private String layoutUrlVersion = "";
	private String antPathRequestMatcherVersion = "";
	
	
	/**
	 * 查询‘更多’
	 * @param dirName 目录名称
	 * @param forumChildType 子版块名称
	 * @return
	 */
	public Map<String,String> queryMore(String dirName,String forumChildType){
		Map<String,String> more = new LinkedHashMap<String,String>();//更多  key:布局文件名称  value: 布局文件名称+布局名称
		Integer forumData = 1;
		
		if("在线帮助列表".equals(forumChildType)){
			forumData = 3;
			more.put("more_help.html", "more_help.html (默认'在线帮助'更多)");
		}
		
		List<Layout> layoutList = templateService.findMore(dirName,forumData);
		if(layoutList != null && layoutList.size() >0){
			for(Layout layout : layoutList){
				more.put(layout.getLayoutFile(), layout.getLayoutFile()+" ("+layout.getName()+")");
			}
		}
		return more;
	}
	
	/**
	 * 根据布局文件名称查询布局名称
	 * @param dirName
	 * @param forumChildType
	 * @return
	 */
	public String getMoreName(String dirName,String forumChildType,String layoutFileName){
		Map<String,String> more = this.queryMore(dirName,forumChildType);
		for (Map.Entry<String,String> entry : more.entrySet()) {
			if(entry.getKey().equals(layoutFileName)){
				return entry.getValue();
			}
		}
		return null;
	}
	
	
	
	
	/**
	 * 删除布局版块上传文件
	 * @param forumList 版块列表
	 * @return
	 */
	public void deleteUploadFile(List<Forum> forumList){
		if(forumList != null && forumList.size() >0){
			for(Forum forum : forumList){
				if(forum.getForumChildType().equals("图片广告")){	
					if("collection".equals(forum.getDisplayType())){//单层
						String formValueJSON = forum.getFormValue();//表单值
						if(formValueJSON != null && !"".equals(formValueJSON)){
							List<Forum_AdvertisingRelated_Image> forum_AdvertisingRelated_ImageList = JsonUtils.toGenericObject(formValueJSON, new TypeReference< List<Forum_AdvertisingRelated_Image> >(){});
							
							//广告部分 - 图片广告   删除图片锁
							if(forum_AdvertisingRelated_ImageList != null && forum_AdvertisingRelated_ImageList.size() >0){
								String path = "file"+File.separator+"template"+File.separator+ forum.getDirName() +File.separator;
								
								for(Forum_AdvertisingRelated_Image forum_AdvertisingRelated_Image :forum_AdvertisingRelated_ImageList){
									if(forum_AdvertisingRelated_Image.getImage_fileName() != null && !"".equals(forum_AdvertisingRelated_Image.getImage_fileName().trim())){
										
										//删除旧文件
										localFileManage.deleteFile(path+forum_AdvertisingRelated_Image.getImage_fileName());
											
										
									}
								}
							}
							
						}
						
					}
				}
				if(forum.getForumChildType().equals("用户自定义HTML")){
					if("entityBean".equals(forum.getDisplayType())){//实体对象
						String formValueJSON = forum.getFormValue();//表单值
						if(formValueJSON != null && !"".equals(formValueJSON)){
							Forum_CustomForumRelated_CustomHTML forum_CustomForumRelated_CustomHTML = JsonUtils.toObject(formValueJSON, Forum_CustomForumRelated_CustomHTML.class);
							if(forum_CustomForumRelated_CustomHTML.getHtml_content() != null && !"".equals(forum_CustomForumRelated_CustomHTML.getHtml_content())){
								Object[] htmlContent_obj = textFilterManage.readPathName(forum_CustomForumRelated_CustomHTML.getHtml_content(), "template");
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
								//分隔符
								String separator = "";
								if("\\".equals(File.separator)){
									separator = "\\\\";
								}else{
									separator = "/";
								}
								//删除路径文件
								if(pathFileList != null && pathFileList.size() >0){
									for(String oldPathFile :pathFileList){
										//替换路径中的..号
										oldPathFile = FileUtil.toRelativePath(oldPathFile);
										//删除路径文件
										Boolean state = localFileManage.deleteFile(oldPathFile.replaceAll("/",separator));
										if(state != null && state == false){
											 //替换指定的字符，只替换第一次出现的
											oldPathFile = StringUtils.replaceOnce(oldPathFile, "file/template/", "");
											//创建删除失败文件
											localFileManage.failedStateFile("file"+File.separator+"template"+File.separator+"lock"+File.separator+oldPathFile.replaceAll("/","_"));
										}
									}
								}
							}
							
						}
					}
				}
			}	
		}
	}
	
	
	
	/**
	 * 查询布局路径集合
	 * @return
	 */
	@Cacheable(value="templateServiceBean_cache",key="'queryLoginValidationPath'")
	public List<String> queryLayoutUrlList(){ 
		//查询需要登录权限的布局URL
		List<String> layoutUrlList = new ArrayList<String>();
		layoutUrlList.add("/user/**");
		
		String dirName = templateService.findTemplateDir();
		
		List<Layout> layoutList = templateService.findLayout(dirName);
		if(layoutList != null && layoutList.size() >0){
			for(Layout layout : layoutList){
				if((layout.getType().equals(1) || layout.getType().equals(4)) && layout.getReferenceCode() != null && !"".equals(layout.getReferenceCode().trim())){
					if(!StringUtils.startsWithIgnoreCase(layout.getReferenceCode(), "user/") && layout.isAccessRequireLogin()){//判断开始部分是否与二参数相同。不区分大小写
						layoutUrlList.add("/"+layout.getReferenceCode());
						
					}
				}
			}
		}
		
		layoutUrlVersion = RandomStringUtils.random(16);
		
		return 	layoutUrlList;
	}
	
	
	/**
	 * 查询需要登录验证的路径
	 * @return
	 */
	public AntPathRequestMatcher[] queryLoginValidationPath(){
   
		number.incrementAndGet();

		return antPathRequestMatcherList.toArray(new AntPathRequestMatcher[antPathRequestMatcherList.size()]);
	}
	
	
	/**
	 * 定时处理布局路径
	 * 由Spring定时器调用 每3秒钟运行一次
	 */
	public void timerProcessLayoutUrl(){
		if(number.get() !=0L){
			this.processLayoutUrl();
			number.set(0L);
		}
	}
	
	
	/**
	 * 处理布局路径
	 * 由Spring定时器调用 每3秒钟运行一次
	 */
	private void processLayoutUrl(){
		try {
			List<String> layoutUrlList = layoutManage.queryLayoutUrlList();
			if(!layoutUrlVersion.equals(antPathRequestMatcherVersion)){
				
				//指定的URL下工作
				List<AntPathRequestMatcher> filterMatchers = new ArrayList<AntPathRequestMatcher>();
				for(int i=0; i<layoutUrlList.size(); i++){
					filterMatchers.add(new AntPathRequestMatcher(layoutUrlList.get(i))); 
				}
				antPathRequestMatcherList = filterMatchers;
				antPathRequestMatcherVersion = layoutUrlVersion;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("处理布局路径异常",e);
	        }
		}
		
	}
	
	
}
