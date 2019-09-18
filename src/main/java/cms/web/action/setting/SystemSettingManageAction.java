package cms.web.action.setting;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.setting.EditorTag;
import cms.bean.setting.SystemNode;
import cms.bean.setting.SystemSetting;
import cms.service.setting.SettingService;
import cms.utils.CommentedProperties;
import cms.utils.DruidTool;
import cms.utils.JsonUtils;
import cms.utils.RedirectPath;
import cms.utils.Verification;
import cms.web.action.cache.CacheManage;
import cms.web.action.cache.CacheStatus;
import cms.web.action.cache.SelectCache;
import cms.web.action.lucene.TopicIndexManage;
import cms.web.action.lucene.TopicLuceneInit;
import net.rubyeye.xmemcached.MemcachedClient;
import net.sf.ehcache.CacheManager;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 系统设置
 *
 */
@Controller
@RequestMapping("/control/systemSetting/manage") 
public class SystemSettingManageAction {
	@Resource SettingService settingService;
	@Resource SettingManage settingManage;
	
	@Resource(name = "memcachedClient")
	private MemcachedClient memcachedClient;
	@Resource(name = "cacheManagerFactory")
	private CacheManager ehCacheManager;
	
	@Resource(name = "selectCache")
	private SelectCache selectCache;
	
	@Resource CacheManage cacheManage;
	
	@Resource(name = "systemSettingValidator") 
	private Validator validator; 
	
	@Resource TopicIndexManage topicIndexManage;
	
	/**
	 * 系统设置 修改界面显示
	 */
	@RequestMapping(value="edit",method=RequestMethod.GET)
	public String editSystemSettingUI(ModelMap model,SystemSetting formbean,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		SystemSetting systemSite = settingService.findSystemSetting();
		
		
		if(systemSite.getEditorTag() != null && !"".equals(systemSite.getEditorTag().trim())){
			EditorTag editorTag = JsonUtils.toObject(systemSite.getEditorTag(), EditorTag.class);
			if(editorTag != null){
				systemSite.setEditorTagObject(editorTag);
			}
		}
		if(systemSite.getTopicEditorTag() != null && !"".equals(systemSite.getTopicEditorTag().trim())){
			EditorTag editorTag = JsonUtils.toObject(systemSite.getTopicEditorTag(), EditorTag.class);
			if(editorTag != null){
				systemSite.setTopicEditorTagObject(editorTag);
			}
		}
		model.addAttribute("systemSetting",systemSite);
		return "jsp/setting/edit_systemSetting";
	}
	/**
	 * 系统设置 修改
	 */
	@RequestMapping(value="edit",method=RequestMethod.POST)
	public String editSystemSetting(ModelMap model,SystemSetting formbean,BindingResult result,String[] supportBank,String[] couponMore_couponType,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) {  
			return "jsp/setting/edit_systemSetting";
		}
		formbean.setTopicEditorTag(JsonUtils.toJSONString(formbean.getTopicEditorTagObject()));
		formbean.setEditorTag(JsonUtils.toJSONString(formbean.getEditorTagObject()));
		
		
		formbean.setId(1);
		formbean.setVersion(new Date().getTime());
		settingService.updateSystemSetting(formbean);
		
		request.setAttribute("message", "修改设置成功");
		request.setAttribute("urladdress", RedirectPath.readUrl("control.systemSetting.manage.edit"));
		return "jsp/common/message";
	}
	
	/**
	 * 维护数据 界面显示
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=maintainData",method=RequestMethod.GET)
	public String rebuildIndexUI(ModelMap model
			) throws Exception {
		
		return "jsp/setting/maintainData";
	}
	
	
	
	/**
	 * 修改数据库密码
	 * @param model
	 * @param oldPassword 旧密码
	 * @param newPassword 新密码
	 * @return  1.修改成功  2.修改失败 3.读取数库配置文件错误 4.读取旧密码为空  5.旧密码错误 6.加密数据库密码错误 7.写入配置文件错误
	 * @throws Exception
	 */
	@RequestMapping(params="method=updateDatabasePassword",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String updateDatabasePassword(ModelMap model,String oldPassword, String newPassword
			) throws Exception {
		
		if(oldPassword != null && !"".equals(oldPassword.trim()) && newPassword != null && !"".equals(newPassword.trim())){
			//旧数据库密码密文
			String jdbc_password_ciphertext = null;
			//旧数据库密码原文
			String jdbc_password_original = null;
			//公钥参数组
			String jdbc_publickey = null;
			
			org.springframework.core.io.Resource database_resource = new ClassPathResource("/druid.properties");//读取配置文件
			CommentedProperties database_props = new CommentedProperties();
			
			
    		try {
    			database_props.load(database_resource.getInputStream(),"utf-8");
    			jdbc_password_ciphertext = database_props.getProperty("jdbc_password");
    			jdbc_publickey = database_props.getProperty("jdbc_publickey");
    			
			} catch (IOException e) {
				return "3";
			}
			
		  
			if(jdbc_password_ciphertext != null && !"".equals(jdbc_password_ciphertext.trim())){
				if(jdbc_publickey != null && !"".equals(jdbc_publickey.trim())){
					
					String publickey = null;
					String[] jdbc_publickey_group = jdbc_publickey.split(";");
					if(jdbc_publickey_group != null && jdbc_publickey_group.length >0){
						for(String str : jdbc_publickey_group){
							if(str.startsWith("config.decrypt.key")){
								publickey = str.substring(19, str.length());
							}
						}
					}
					
					//解密
					jdbc_password_original = DruidTool.decryptString(publickey, jdbc_password_ciphertext);
				}else{
					jdbc_password_original = jdbc_password_ciphertext;
				}
			}else{
				return "4";
			}
			
			if(!oldPassword.trim().equals(jdbc_password_original)){
				return "5";
			}
			
			//修改配置文件		
    		BufferedWriter bw = null;
    		try {
    			//公钥
    			String publicKey = "";
    			//私钥
    			String privateKey = "";
    			
    			Map<String, String> rsaKey = DruidTool.generateRsaKey();
    			if(rsaKey != null && rsaKey.size() >0){
    				for(Map.Entry<String, String> entry : rsaKey.entrySet()){
    					if("privateKey".equals(entry.getKey())){
    						privateKey = entry.getValue();
    					}else if("publicKey".equals(entry.getKey())){
    						publicKey = entry.getValue();
    					}
    				}
    			}

    			String encryptPassword = "";
    			try {
    				encryptPassword = DruidTool.encryptString(privateKey, newPassword.trim());
				} catch (Exception e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					return "6";
				}

    			database_props.load(database_resource.getInputStream(),"utf-8");
				database_props.setProperty("jdbc_password", encryptPassword);
				database_props.setProperty("jdbc_publickey", "config.decrypt=true;config.decrypt.key="+publicKey);
	
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(database_resource.getFile()),"UTF-8"));
				database_props.store(bw);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				return "7";
			}finally {
				if(bw != null){
					bw.close();
				}
				
			}
			
			return "1";
		}
		return "2";
	}
	
	
	/**
	 * 清空所有缓存
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=clearAllCache",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String clearAllCache(ModelMap model
			) throws Exception {
		
		cacheManage.clearAllCache();
		
		return "1";
	}
	
	/**
	 * 重建话题全文索引
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=rebuildTopicIndex",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String rebuildTopicIndex(ModelMap model
			) throws Exception {
		Long count = topicIndexManage.taskRunMark_add(-1L);
		
		if(count >=0L){
			return "2";
		}else{
			boolean allow = TopicLuceneInit.INSTANCE.allowCreateIndexWriter();//是否允许创建IndexWriter
			if(allow){
				settingManage.addAllTopicIndex();
				return "1";
			}else{
				return "3";
			}
			
			
		}
		// 1.任务开始运行;   2.任务正在运行  3.索引运行过程中，不能执行创建
	}
	
	/**
	 * 删除浏览量数据
	 * @param model
	 * @param beforeTime 删除指定时间之前的数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=deletePageViewData",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String deletePageViewData(ModelMap model,String deletePageViewData_beforeTime
			) throws Exception {	
		
		if(deletePageViewData_beforeTime != null && !"".equals(deletePageViewData_beforeTime.trim())){
			boolean beforeTimeVerification = Verification.isTime_minute(deletePageViewData_beforeTime.trim());
			if(beforeTimeVerification){
				DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
				settingManage.executeDeletePageViewData(dd.parse(deletePageViewData_beforeTime.trim()+":00"));	
				return "1";
			}else{
				return "4";
			}
		}else{
			return "3";
		}
		// 1.任务开始运行;  3.时间不能为空; 4.时间格式错误
	}
	
	/**
	 * 删除用户登录日志数据
	 * @param model
	 * @param beforeTime 删除指定时间之前的数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=deleteUserLoginLogData",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String deleteUserLoginLogData(ModelMap model,String deleteUserLoginLogData_beforeTime
			) throws Exception {	
		
		if(deleteUserLoginLogData_beforeTime != null && !"".equals(deleteUserLoginLogData_beforeTime.trim())){
			boolean deleteUserLoginLogData_beforeTimeVerification = Verification.isTime_minute(deleteUserLoginLogData_beforeTime.trim());
			if(deleteUserLoginLogData_beforeTimeVerification){
				DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
				settingManage.executeDeleteUserLoginLogData(dd.parse(deleteUserLoginLogData_beforeTime.trim()+":00"));	
				return "1";
			}else{
				return "4";
			}
		}else{
			return "3";
		}
		// 1.任务开始运行;  3.时间不能为空; 4.时间格式错误
	}
	
	
	
	/**
	 * 节点参数 界面显示
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=nodeParameter",method=RequestMethod.GET)
	public String nodeParameterUI(ModelMap model
			) throws Exception {
		SystemNode systemNode = new SystemNode();
		systemNode.setMaxMemory(settingManage.maxMemory());//分配最大内存
		systemNode.setTotalMemory(settingManage.totalMemory());//已分配内存
		systemNode.setFreeMemory(settingManage.freeMemory());//已分配内存中的剩余空间
		systemNode.setUsableMemory(settingManage.maxMemory() - settingManage.totalMemory() + settingManage.freeMemory());//空闲内存
		
		model.addAttribute("systemNode",systemNode);
		
		
		
		
		List<CacheStatus> cacheStatusList = new ArrayList<CacheStatus>();
		DecimalFormat format = new DecimalFormat("0.0");
		if("memcached".equals(selectCache.getCacheName())){//memcached服务器
			Map<InetSocketAddress,Map<String,String>> result = memcachedClient.getStats();
			if(result != null && result.size() >0){
				for(Map.Entry<InetSocketAddress,Map<String,String>> entry : result.entrySet()){
					CacheStatus cacheStatus = new CacheStatus();
					cacheStatus.setServiceAddress(entry.getKey().getHostName()+":"+entry.getKey().getPort());//服务器地址
					
					Map<String,String> stats_entry = entry.getValue();
					
					//当前已使用的内存容量
					cacheStatus.setMemCached_bytes(format.format(Double.parseDouble(stats_entry.get("bytes").replaceAll("\\r\\n", ""))/(1024*1024)) + "MB");
					//服务器本次启动以来，曾存储的Item总个数
					cacheStatus.setMemCached_total_items(stats_entry.get("total_items"));
					//MemCached服务版本
					cacheStatus.setMemCached_version(stats_entry.get("version"));
					//MemCached服务器架构 
					cacheStatus.setMemCached_pointer_size(stats_entry.get("pointer_size").replaceAll("\\r\\n", "") + "位");
					//服务器当前时间
					cacheStatus.setMemCached_time(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(stats_entry.get("time").replaceAll("\\r\\n", "")) * 1000)).toString());
					//允许服务支配的最大内存容量
					cacheStatus.setMemCached_limit_maxbytes(format.format(Double.parseDouble(stats_entry.get("limit_maxbytes").replaceAll("\\r\\n", ""))/(1024*1024)) + "MB");
					//服务器本次启动以来，读取的数据量
					cacheStatus.setMemCached_bytes_read(format.format(Double.parseDouble(stats_entry.get("bytes_read").replaceAll("\\r\\n", ""))/(1024)) + "KB");
					//服务器本次启动以来，写入的数据量
					cacheStatus.setMemCached_bytes_written( format.format(Double.parseDouble(stats_entry.get("bytes_written").replaceAll("\\r\\n", ""))/(1024)) + "KB");
					//服务器本次启动以来，累计响应连接总次数
					cacheStatus.setMemCached_total_connections(stats_entry.get("total_connections"));
					//服务器本次启动以来，总共运行时间 
					cacheStatus.setMemCached_uptime(format.format(Double.parseDouble(stats_entry.get("uptime").replaceAll("\\r\\n", ""))/(60*60)) + "小时");
					//当前存储的Item个数
					cacheStatus.setMemCached_curr_items(stats_entry.get("curr_items"));
					//服务器本次启动以来，执行Get命令总次数
					cacheStatus.setMemCached_cmd_get(stats_entry.get("cmd_get"));
					//服务器本次启动以来，执行Set命令总次数
					cacheStatus.setMemCached_cmd_set(stats_entry.get("cmd_set"));	
					cacheStatusList.add(cacheStatus);
				}

			}
			model.addAttribute("memcached_cacheStatusList", cacheStatusList);
		}else{//ehCache服务器
			List<CacheManager> cacheManagerList = ehCacheManager.ALL_CACHE_MANAGERS;
			if(cacheManagerList != null && cacheManagerList.size() >0){
			//	for(CacheManager cm : cacheManagerList){
			//		System.out.println("服务器"+cm.getCachePeerListener());
			//	}
			}
			
			
			model.addAttribute("ehCache_cacheStatusList", cacheStatusList);
		}
		
		model.addAttribute("cacheName", selectCache.getCacheName());
		
		
		return "jsp/setting/nodeParameter";
	}
	
	
}
