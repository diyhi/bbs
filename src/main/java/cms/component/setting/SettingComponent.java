package cms.component.setting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import cms.component.JsonComponent;
import cms.component.lucene.QuestionIndexComponent;
import cms.component.lucene.TopicIndexComponent;
import cms.model.setting.AllowLoginAccountType;
import cms.model.setting.AllowRegisterAccountType;
import cms.model.setting.EditorTag;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import cms.repository.statistic.PageViewRepository;
import cms.repository.user.UserRepository;
import jakarta.annotation.Resource;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

/**
 * 设置组件
 *
 */
@Component("settingComponent")
public class SettingComponent {

	@Resource TopicIndexComponent topicIndexComponent;
	@Resource QuestionIndexComponent questionIndexComponent;
	@Resource SettingRepository settingRepository;
	
	@Resource UserRepository userRepository;
	@Resource PageViewRepository pageViewRepository;
    @Resource JsonComponent jsonComponent;

	/** 支持账户类型 **/
	private final Map<Integer,String> supportAccountTypeParameter = ImmutableMap.of(10,"本地账号密码用户", 20,"手机用户", 30,"邮箱用户", 40,"微信用户");

	/**
	 * 获取账户类型
	 * @return
	 */
	public Map<Integer,String> getSupportAccountType(){
		return supportAccountTypeParameter;
	}


	
	
	/**
	 * 添加全部话题索引(异步)
     */
	@Async
	public void addAllTopicIndex(){
		topicIndexComponent.addAllTopicIndex();
	}
	/**
	 * 添加全部问题索引(异步)
     */
	@Async
	public void addAllQuestionIndex(){
		questionIndexComponent.addAllQuestionIndex();
	}
	/**
	 * 删除浏览量数据 (异步)
	 * @param endTime 结束时间
     */
	@Async
	public void executeDeletePageViewData(LocalDateTime endTime){
		pageViewRepository.deletePageView(endTime);
	}
	
	/**
	 * 删除用户登录日志数据 (异步)
	 * @param endTime 结束时间
     */
	@Async
	public void executeDeleteUserLoginLogData(LocalDateTime endTime){
		userRepository.deleteUserLoginLog(endTime);
	}
	

	/**
	 * 读取允许注册账号类型
	 * @return
	*/
	public AllowRegisterAccountType readAllowRegisterAccountType(){
		AllowRegisterAccountType allowRegisterAccountType = new AllowRegisterAccountType();
		SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
		if(systemSetting.getAllowRegisterAccountType() != null && !systemSetting.getAllowRegisterAccountType().trim().isEmpty()){
			List<Integer> allowRegisterAccountTypeList = jsonComponent.toObject(systemSetting.getAllowRegisterAccountType(), List.class);
			if(allowRegisterAccountTypeList != null && allowRegisterAccountTypeList.size() >0){
				
				for(Integer type : allowRegisterAccountTypeList){
					if(type.equals(10)){//本地账号密码用户
						allowRegisterAccountType.setLocal(true);
					}else if(type.equals(20)){//手机用户
						allowRegisterAccountType.setMobile(true);
					}else if(type.equals(30)){//邮箱用户
						allowRegisterAccountType.setEmail(true);
					}else if(type.equals(40)){//微信用户
						allowRegisterAccountType.setWeChat(true);
					}else if(type.equals(80)){//其他用户
						allowRegisterAccountType.setOther(true);
					}
				}
			}
		}
		return allowRegisterAccountType;
		
	}
	
	
	/**
	 * 获取允许登录账号类型
	 * @return
	*/
	public List<Integer> getAllowLoginAccountType(){
		SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
		if(systemSetting.getAllowLoginAccountType() != null && !systemSetting.getAllowLoginAccountType().trim().isEmpty()){
			return jsonComponent.toObject(systemSetting.getAllowLoginAccountType(), List.class);
		
		}
		return null;
	}
	/**
	 * 读取允许登录账号类型
	 * @return
	 */
	public AllowLoginAccountType readAllowLoginAccountType(){
		AllowLoginAccountType allowLoginAccountType = new AllowLoginAccountType();
		SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
		if(systemSetting.getAllowLoginAccountType() != null && !systemSetting.getAllowLoginAccountType().trim().isEmpty()){
			List<Integer> allowLoginAccountTypeList = jsonComponent.toObject(systemSetting.getAllowLoginAccountType(), List.class);
			if(allowLoginAccountTypeList != null && allowLoginAccountTypeList.size() >0){
				
				for(Integer type : allowLoginAccountTypeList){
					if(type.equals(10)){//本地账号密码用户
						allowLoginAccountType.setLocal(true);
					}else if(type.equals(20)){//手机用户
						allowLoginAccountType.setMobile(true);
					}else if(type.equals(30)){//邮箱用户
						allowLoginAccountType.setEmail(true);
					}else if(type.equals(40)){//微信用户
						allowLoginAccountType.setWeChat(true);
					}else if(type.equals(80)){//其他用户
						allowLoginAccountType.setOther(true);
					}
				}
			}
		}
		return allowLoginAccountType;
		
	}
	
	
	/**
	 * 读取话题编辑器允许使用标签
	 * @return
	*/
	public EditorTag readTopicEditorTag(){
		SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
		if(systemSetting.getTopicEditorTag() != null && !systemSetting.getTopicEditorTag().trim().isEmpty()){
			return jsonComponent.toObject(systemSetting.getTopicEditorTag(), EditorTag.class);
		}
		return null;
	} 
	
	/**
	 * 读取评论编辑器允许使用标签
	 * @return
	 */
	public EditorTag readEditorTag(){
		SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
		if(systemSetting.getEditorTag() != null && !systemSetting.getEditorTag().trim().isEmpty()){
			return jsonComponent.toObject(systemSetting.getEditorTag(), EditorTag.class);
		}
		return null;
	}
	
	/**
	 * 读取问题编辑器允许使用标签
	 * @return
	 */
	public EditorTag readQuestionEditorTag(){
		SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
		if(systemSetting.getQuestionEditorTag() != null && !systemSetting.getQuestionEditorTag().trim().isEmpty()){
			return jsonComponent.toObject(systemSetting.getQuestionEditorTag(), EditorTag.class);
		}
		return null;
	}
	
	/**
	 * 读取答案编辑器允许使用标签
	 * @return
	 */
	public EditorTag readAnswerEditorTag(){
		SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
		if(systemSetting.getAnswerEditorTag() != null && !systemSetting.getAnswerEditorTag().trim().isEmpty()){
			return jsonComponent.toObject(systemSetting.getAnswerEditorTag(), EditorTag.class);
		}
		return null;
	}

	
	/**  
     * 总内存  
     *   
     * @return  
     */  
    public long totalMemory() {   
        long l = Runtime.getRuntime().totalMemory();   
        return (l / 1024 / 1024);   
    }   
  
    /**  
     * 分配最大内存  
     *   
     * @return  
     */  
    public long maxMemory() {   
        long l = Runtime.getRuntime().maxMemory();   
        return (l / 1024 / 1024);   
    }   
  
    /**  
     * 空闲内存 
     * @return  
     */  
    public long freeMemory() {   
        long l = Runtime.getRuntime().freeMemory();   
        return (l / 1024 / 1024);   
    }   
	

}
