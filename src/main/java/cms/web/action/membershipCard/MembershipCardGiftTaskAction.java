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
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.membershipCard.MembershipCardGiftTask;
import cms.bean.membershipCard.RestrictionGroup;
import cms.service.membershipCard.MembershipCardService;
import cms.service.setting.SettingService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.user.UserManage;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 会员卡赠送任务
 *
 */
@Controller
public class MembershipCardGiftTaskAction {
	@Resource MembershipCardService membershipCardService;
	@Resource SettingService settingService;
	@Resource UserManage userManage;
	@Resource FileManage fileManage;
	
	/**
	 * 会员卡赠送任务列表
	 * @param pageForm
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/membershipCardGiftTask/list") 
	public String execute(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		
		
		PageView<MembershipCardGiftTask> pageView = new PageView<MembershipCardGiftTask>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
		
		//调用分页算法类
		QueryResult<MembershipCardGiftTask> qr = membershipCardService.getScrollData(MembershipCardGiftTask.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);		
		
		
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(MembershipCardGiftTask membershipCardGiftTask : qr.getResultlist()){
				if(membershipCardGiftTask.getExpirationDate_start() != null){//默认初始时间则设为空
					//默认时间  年,月,日,时,分,秒,毫秒    
		            DateTime defaultTime = new DateTime(1970, 1, 1, 0, 0, 0);// 1970年1月1日0点0分0秒
		            
		            if(defaultTime.toLocalDateTime().isEqual(new DateTime(membershipCardGiftTask.getExpirationDate_start()).toLocalDateTime())){
		            	membershipCardGiftTask.setExpirationDate_start(null);
		            }
				}
				if(membershipCardGiftTask.getExpirationDate_end() != null){//默认初始时间则设为空
					//默认时间  年,月,日,时,分,秒,毫秒    
					 DateTime defaultTime = new DateTime(2999, 1, 1, 0, 0, 0);// 2999年1月1日0点0分0秒
		            
					 if(defaultTime.toLocalDateTime().isEqual(new DateTime(membershipCardGiftTask.getExpirationDate_end()).toLocalDateTime())){
		            	membershipCardGiftTask.setExpirationDate_end(null);
		            }
				}
				
				
				if(membershipCardGiftTask.getRestriction() != null && !"".equals(membershipCardGiftTask.getRestriction().trim())){
					RestrictionGroup restrictionGroup= JsonUtils.toObject(membershipCardGiftTask.getRestriction(), RestrictionGroup.class);
					if(restrictionGroup != null){
						membershipCardGiftTask.setRestrictionGroup(restrictionGroup);
					}
				}
			}
		}
		
		pageView.setQueryResult(qr);
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}

	
	
}
