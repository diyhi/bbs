package cms.web.action.template.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.redEnvelope.ReceiveRedEnvelope;
import cms.bean.template.Forum;
import cms.bean.template.Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser;
import cms.bean.topic.Topic;
import cms.bean.user.AccessUser;
import cms.bean.user.ResourceEnum;
import cms.bean.user.User;
import cms.service.setting.SettingService;
import cms.utils.JsonUtils;
import cms.utils.Verification;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.redEnvelope.RedEnvelopeManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;

/**
 * 红包
 * @author Gao
 *
 */
@Component("redEnvelope_TemplateManage")
public class RedEnvelope_TemplateManage {
	@Resource RedEnvelopeManage redEnvelopeManage;
	@Resource TopicManage topicManage;
	@Resource UserRoleManage userRoleManage;
	@Resource UserManage userManage;
	@Resource SettingService settingService;
	@Resource FileManage fileManage;
	
	/**
	 * 发红包-- 发红包内容 -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public GiveRedEnvelope content_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
		String giveRedEnvelopeId = null;

		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(parameter != null && parameter.size() >0){
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("giveRedEnvelopeId".equals(paramIter.getKey())){
					giveRedEnvelopeId = paramIter.getValue().toString();	
				}
			}
		}
		
		if(giveRedEnvelopeId != null && !"".equals(giveRedEnvelopeId.trim())){
			GiveRedEnvelope giveRedEnvelope = redEnvelopeManage.query_cache_findById(giveRedEnvelopeId);//查询缓存
			
			if(giveRedEnvelope != null){
				if(giveRedEnvelope.getBindTopicId() != null && giveRedEnvelope.getBindTopicId() >0L){
					Topic topic = topicManage.queryTopicCache(giveRedEnvelope.getBindTopicId());//查询缓存
					if(topic != null){
						//判断权限
						boolean permission = userRoleManage.isPermission(ResourceEnum._1001000,topic.getTagId());
						if(!permission){
							return null;
						}
					}
				}
				
				User user = userManage.query_cache_findUserById(giveRedEnvelope.getUserId());
				if(user != null){
					giveRedEnvelope.setUserName(user.getUserName());
					if(user.getCancelAccountTime().equals(-1L)){
						giveRedEnvelope.setAccount(user.getAccount());
						giveRedEnvelope.setNickname(user.getNickname());
						giveRedEnvelope.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
						giveRedEnvelope.setAvatarName(user.getAvatarName());
					}
				}
				
				if(accessUser != null){
					ReceiveRedEnvelope receiveRedEnvelope = redEnvelopeManage.query_cache_findByReceiveRedEnvelopeId(redEnvelopeManage.createReceiveRedEnvelopeId(giveRedEnvelopeId, accessUser.getUserId()));
		  			if(receiveRedEnvelope != null){
		  				giveRedEnvelope.setAccessUserUnwrap(true);//当前用户已拆开红包
		  			}
					
				}
				
				
				
				return giveRedEnvelope;
				
			}
		}
		
		return null;
	}
	
	/**
	 * 领取红包用户列表 -- 分页
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public PageView<ReceiveRedEnvelope> receiveRedEnvelopeUser_page(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		String giveRedEnvelopeId = null;
		int page = 1;//分页 当前页
		int pageCount=10;// 页码显示总数
		boolean sort = false;//true:正序 false:倒序
		//获取提交参数
		if(parameter != null && parameter.size() >0){
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("page".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=9){
							page = Integer.parseInt(paramIter.getValue().toString());
						}
					}
				}else if("giveRedEnvelopeId".equals(paramIter.getKey())){
					giveRedEnvelopeId = paramIter.getValue().toString();	
				}
			}
		}
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser forum_ReceiveRedRelated_ReceiveRedEnvelope = JsonUtils.toObject(formValueJSON,Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.class);
			if(forum_ReceiveRedRelated_ReceiveRedEnvelope != null){
				int maxResult = settingService.findSystemSetting_cache().getForestagePageNumber();
				//每页显示记录数
				if(forum_ReceiveRedRelated_ReceiveRedEnvelope.getReceiveRedEnvelopeUser_maxResult() != null && forum_ReceiveRedRelated_ReceiveRedEnvelope.getReceiveRedEnvelopeUser_maxResult() >0){
					maxResult = forum_ReceiveRedRelated_ReceiveRedEnvelope.getReceiveRedEnvelopeUser_maxResult();
				}
				//顺序
				if(forum_ReceiveRedRelated_ReceiveRedEnvelope.getReceiveRedEnvelopeUser_sort() != null && forum_ReceiveRedRelated_ReceiveRedEnvelope.getReceiveRedEnvelopeUser_sort() >0){
					if(forum_ReceiveRedRelated_ReceiveRedEnvelope.getReceiveRedEnvelopeUser_sort().equals(10)){
						sort = true;
					}else if(forum_ReceiveRedRelated_ReceiveRedEnvelope.getReceiveRedEnvelopeUser_sort().equals(20)){
						sort = false;
					}
				}
				
				if(giveRedEnvelopeId != null && !"".equals(giveRedEnvelopeId.trim())){
					GiveRedEnvelope giveRedEnvelope = redEnvelopeManage.query_cache_findById(giveRedEnvelopeId);//查询缓存
					if(giveRedEnvelope != null && giveRedEnvelope.getBindTopicId() != null && giveRedEnvelope.getBindTopicId() >0L){
						PageForm pageForm = new PageForm();
						pageForm.setPage(page);
						
						//调用分页算法代码
						PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(maxResult,pageForm.getPage(),pageCount);
						//当前页
						int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
						
						QueryResult<ReceiveRedEnvelope> qr = redEnvelopeManage.queryReceiveRedEnvelopeByCondition(giveRedEnvelope,false,firstIndex, pageView.getMaxresult(),sort,false);
						    
						//将查询结果集传给分页List
						pageView.setQueryResult(qr);
						
						return pageView;
					}
				}
			}
			
		}
		return null;
	}
	

	
	
	
	
	
	/**
	 * 抢红包
	 * @param forum
	 */
	public Map<String,Object> addReceiveRedEnvelope_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		return value;
	}
}
