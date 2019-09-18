package cms.web.action.membershipCard;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.membershipCard.MembershipCard;
import cms.bean.membershipCard.MembershipCardOrder;
import cms.service.membershipCard.MembershipCardService;
import cms.service.setting.SettingService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 会员卡
 *
 */
@Controller
public class MembershipCardAction {
	@Resource MembershipCardService membershipCardService;
	@Resource SettingService settingService;
	
	/**
	 * 会员卡列表
	 * @param pageForm
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/control/membershipCard/list") 
	public String execute(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		
		
		PageView<MembershipCard> pageView = new PageView<MembershipCard>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
		
		//调用分页算法类
		QueryResult<MembershipCard> qr = membershipCardService.getScrollData(MembershipCard.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);		
		
		pageView.setQueryResult(qr);
		model.addAttribute("pageView", pageView);
		
		return "jsp/membershipCard/membershipCardList";
	}
	
	/**
	 * 会员卡订单列表
	 * @param pageForm
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/control/membershipCardOrder/list") 
	public String queryMembershipCardOrderList(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		
		PageView<MembershipCardOrder> pageView = new PageView<MembershipCardOrder>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("createDate", "desc");//根据id字段降序排序
		
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
		
		//调用分页算法类
		QueryResult<MembershipCardOrder> qr = membershipCardService.getScrollData(MembershipCardOrder.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);		
		
		pageView.setQueryResult(qr);
		model.addAttribute("pageView", pageView);
		
		return "jsp/membershipCard/membershipCardOrderList";
	}
	
	
}
