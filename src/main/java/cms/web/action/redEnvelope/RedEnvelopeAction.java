package cms.web.action.redEnvelope;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.redEnvelope.ReceiveRedEnvelope;
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.redEnvelope.RedEnvelopeService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.UserManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 红包管理
 * @author gao
 *
 */
@Controller
public class RedEnvelopeAction{

	@Resource RedEnvelopeService redEnvelopeService;//通过接口引用代理返回的对象
	@Resource UserService userService;
	@Resource SettingService settingService;
	@Resource TopicManage topicManage;
	@Resource RedEnvelopeManage redEnvelopeManage;
	@Resource UserManage userManage;
	@Resource FileManage fileManage;
	
	/**
	 * 发红包
	 * @param model
	 * @param pageForm
	 * @param id 用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/redEnvelope/giveRedEnvelope/list")  
	public String giveRedEnvelopePage(ModelMap model, PageForm pageForm,Long id,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		//调用分页算法代码
		PageView<GiveRedEnvelope> pageView = new PageView<GiveRedEnvelope>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		if(id != null && id >0L){
			StringBuffer jpql = new StringBuffer("");
			//存放参数值
			List<Object> params = new ArrayList<Object>();

			
			jpql.append(" and o.userId=?"+ (params.size()+1));
			params.add(id);
			
			//删除第一个and
			String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
			
			
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			
			orderby.put("giveTime", "desc");//根据giveTime字段降序排序
			QueryResult<GiveRedEnvelope> qr = redEnvelopeService.getScrollData(GiveRedEnvelope.class,firstIndex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			

			if(qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(GiveRedEnvelope giveRedEnvelope : qr.getResultlist()){
					if(giveRedEnvelope.getBindTopicId() != null && giveRedEnvelope.getBindTopicId() >0L){
						Topic topic = topicManage.queryTopicCache(giveRedEnvelope.getBindTopicId());//查询缓存
						if(topic != null){
							giveRedEnvelope.setBindTopicTitle(topic.getTitle());
						}
					}
				}
			}
			
			User user = userService.findUserById(id);
			if(user != null){
				User currentUser = new User();
				currentUser.setId(user.getId());
				currentUser.setAccount(user.getAccount());
				currentUser.setNickname(user.getNickname());
				if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
					currentUser.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
					currentUser.setAvatarName(user.getAvatarName());
				}
				returnValue.put("currentUser", currentUser);
			}
			returnValue.put("pageView", pageView);
		}else{
			error.put("userId", "用户Id不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	
	/**
	 * 发红包金额分配
	 * @param model
	 * @param pageForm
	 * @param giveRedEnvelopeId 发红包Id
	 * @param id 用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/redEnvelope/redEnvelopeAmountDistribution/list")  
	public String redEnvelopeAmountDistributionPage(ModelMap model, PageForm pageForm,String giveRedEnvelopeId,Long id,Long topicId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		//调用分页算法代码
		PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		if(giveRedEnvelopeId != null && !"".equals(giveRedEnvelopeId.trim())){
			GiveRedEnvelope giveRedEnvelope = redEnvelopeService.findById(giveRedEnvelopeId);
			if(giveRedEnvelope != null){
				//排序
				boolean sort = true;//true:正序 false:倒序
				
				QueryResult<ReceiveRedEnvelope> qr = redEnvelopeManage.queryReceiveRedEnvelopeByCondition(giveRedEnvelope,true,firstindex, pageView.getMaxresult(),sort,true);
				    

		
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
				
				if(id != null){
					User user = userService.findUserById(id);
					if(user != null){
						User currentUser = new User();
						currentUser.setId(user.getId());
						currentUser.setAccount(user.getAccount());
						currentUser.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							currentUser.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							currentUser.setAvatarName(user.getAvatarName());
						}
						returnValue.put("currentUser", currentUser);
					}
				}
				if(topicId != null){
					Topic topic = topicManage.queryTopicCache(topicId);
					if(topic != null){
						returnValue.put("currentTopic", topic);
					}
				}
				returnValue.put("pageView", pageView);
				
				returnValue.put("giveRedEnvelope", giveRedEnvelope);
			}else{
				error.put("giveRedEnvelopeId", "发红包不存在");
			}
		}else{
			error.put("giveRedEnvelopeId", "发红包Id不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	
	
	
	/**
	 * 收红包
	 * @param model
	 * @param pageForm
	 * @param id 用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/redEnvelope/receiveRedEnvelope/list")  
	public String receiveRedEnvelopePage(ModelMap model, PageForm pageForm,Long id,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		//调用分页算法代码
		PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		if(id != null && id >0L){
			QueryResult<ReceiveRedEnvelope> qr = redEnvelopeService.findReceiveRedEnvelopeByUserId(id, firstIndex, pageView.getMaxresult());
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			User _user = userService.findUserById(id);
			if(_user != null){
				User currentUser = new User();
				currentUser.setId(_user.getId());
				currentUser.setAccount(_user.getAccount());
				currentUser.setNickname(_user.getNickname());
				if(_user.getAvatarName() != null && !"".equals(_user.getAvatarName().trim())){
					currentUser.setAvatarPath(fileManage.fileServerAddress(request)+_user.getAvatarPath());
					currentUser.setAvatarName(_user.getAvatarName());
				}
				returnValue.put("currentUser", currentUser);
			}
			
			returnValue.put("pageView", pageView);
			
			if(qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(ReceiveRedEnvelope receiveRedEnvelope : qr.getResultlist()){
					User user = userManage.query_cache_findUserById(receiveRedEnvelope.getGiveUserId());
	        		if(user != null){
	        			receiveRedEnvelope.setGiveAccount(user.getAccount());
	        			receiveRedEnvelope.setGiveNickname(user.getNickname());
	        			receiveRedEnvelope.setGiveUserName(user.getUserName());
	        			if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
	        				receiveRedEnvelope.setGiveAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
	        				receiveRedEnvelope.setGiveAvatarName(user.getAvatarName());
	        			}		
	        		}
	        		
	        		//如果红包还没拆，则执行拆红包
    				if(receiveRedEnvelope.getAmount() != null && receiveRedEnvelope.getAmount().compareTo(new BigDecimal("0")) ==0 && user != null){
    					GiveRedEnvelope giveRedEnvelope = redEnvelopeService.findById(receiveRedEnvelope.getGiveRedEnvelopeId());
    					if(giveRedEnvelope != null){
    						//查询用户领取到的红包金额
        				    BigDecimal amount = redEnvelopeManage.queryReceiveRedEnvelopeAmount(giveRedEnvelope,user.getId());
        					if(amount != null && amount.compareTo(new BigDecimal("0")) >0 ){
        						redEnvelopeManage.unwrapRedEnvelope(receiveRedEnvelope,amount,user.getId(),user.getUserName());
        					}
    					}
    					
    				}
	        		
				}
			}
		}else{
			error.put("userId", "用户Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
}

