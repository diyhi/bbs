package cms.web.action.redEnvelope;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.payment.PaymentVerificationLog;
import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.redEnvelope.ReceiveRedEnvelope;
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.redEnvelope.RedEnvelopeService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.UserManage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

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
	@RequestMapping("/control/redEnvelope/giveRedEnvelope/list")  
	public String giveRedEnvelopePage(ModelMap model, PageForm pageForm,Long id,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
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
			request.setAttribute("pageView", pageView);

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
			
		}
		
		

		return "jsp/redEnvelope/giveRedEnvelopeList";
	}
	
	
	/**
	 * 发红包金额分配
	 * @param model
	 * @param pageForm
	 * @param giveRedEnvelopeId 发红包Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/control/redEnvelope/redEnvelopeAmountDistribution/list")  
	public String redEnvelopeAmountDistributionPage(ModelMap model, PageForm pageForm,String giveRedEnvelopeId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	

		//调用分页算法代码
		PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		if(giveRedEnvelopeId != null && !"".equals(giveRedEnvelopeId.trim())){
			GiveRedEnvelope giveRedEnvelope = redEnvelopeService.findById(giveRedEnvelopeId);
			if(giveRedEnvelope != null){
				//排序
				boolean sort = true;//true:正序 false:倒序
				
				QueryResult<ReceiveRedEnvelope> qr = redEnvelopeManage.queryReceiveRedEnvelopeByCondition(giveRedEnvelope,true,firstindex, pageView.getMaxresult(),sort);
				    

		
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
				model.addAttribute("pageView", pageView);
				
				model.addAttribute("giveRedEnvelope", giveRedEnvelope);
			}
		}
		
		return "jsp/redEnvelope/redEnvelopeAmountDistributionList";
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
	@RequestMapping("/control/redEnvelope/receiveRedEnvelope/list")  
	public String receiveRedEnvelopePage(ModelMap model, PageForm pageForm,Long id,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//调用分页算法代码
		PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		if(id != null && id >0L){
			QueryResult<ReceiveRedEnvelope> qr = redEnvelopeService.findReceiveRedEnvelopeByUserId(id, firstIndex, pageView.getMaxresult());
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			request.setAttribute("pageView", pageView);
			
			if(qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(ReceiveRedEnvelope receiveRedEnvelope : qr.getResultlist()){
					User user = userManage.query_cache_findUserById(receiveRedEnvelope.getGiveUserId());
	        		if(user != null){
	        			receiveRedEnvelope.setGiveNickname(user.getNickname());
	        			receiveRedEnvelope.setGiveUserName(user.getUserName());
	        			if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
	        				receiveRedEnvelope.setGiveAvatarPath(user.getAvatarPath());
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
		}
		return "jsp/redEnvelope/receiveRedEnvelopeList";
	}
	
}

