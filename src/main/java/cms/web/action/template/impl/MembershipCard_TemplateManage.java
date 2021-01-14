package cms.web.action.template.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.membershipCard.MembershipCard;
import cms.bean.membershipCard.Specification;
import cms.bean.template.Forum;
import cms.service.membershipCard.MembershipCardService;
import cms.utils.JsonUtils;
import cms.utils.Verification;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.membershipCard.MembershipCardManage;

/**
 * 会员卡 -- 模板方法实现
 *
 */
@Component("membershipCard_TemplateManage")
public class MembershipCard_TemplateManage {
	@Resource MembershipCardService membershipCardService; 
	@Resource MembershipCardManage membershipCardManage;
	@Resource FileManage fileManage;
	
	/**
	 * 会员卡列表  -- 集合
	 * @param forum
	 */
	public List<MembershipCard> membershipCard_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		List<MembershipCard> new_membershipCardList = new ArrayList<MembershipCard>();

		List<MembershipCard> membershipCardList = membershipCardService.findAllMembershipCard();
		if(membershipCardList != null && membershipCardList.size() >0){
			for(MembershipCard membershipCard : membershipCardList){
				if(membershipCard.getState().equals(1)){//状态 1:上架 
					String descriptionTagFormat = membershipCard.getDescriptionTagFormat();
					if(descriptionTagFormat != null && !"".equals(descriptionTagFormat.trim())){
						List<String> descriptionTagList = JsonUtils.toGenericObject(descriptionTagFormat.trim(), new TypeReference< List<String> >(){});
						membershipCard.setDescriptionTagList(descriptionTagList);
					}
					
					new_membershipCardList.add(membershipCard);
				}
			}
		}
		return new_membershipCardList;
	}
	
	/**
	 * 会员卡内容  -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public MembershipCard membershipCardContent_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		Long membershipCardId = null;
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("membershipCardId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							membershipCardId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		

		if(membershipCardId != null){
			
			MembershipCard membershipCard = membershipCardManage.query_cache_findById(membershipCardId);
	  		
	  		if(membershipCard != null){
	  			if(membershipCard.getIntroduction() != null && !"".equals(membershipCard.getIntroduction().trim())){
					//处理富文本路径
	  				membershipCard.setIntroduction(fileManage.processRichTextFilePath(membershipCard.getIntroduction(),"membershipCard"));
				}
	  			
	  			String descriptionTagFormat = membershipCard.getDescriptionTagFormat();
				if(descriptionTagFormat != null && !"".equals(descriptionTagFormat.trim())){
					List<String> descriptionTagList = JsonUtils.toGenericObject(descriptionTagFormat.trim(), new TypeReference< List<String> >(){});
					membershipCard.setDescriptionTagList(descriptionTagList);
				}
			
	  			
	  			List<Specification> specificationList = membershipCardManage.query_cache_findSpecificationByMembershipCardId(membershipCardId);
	  			if(specificationList != null && specificationList.size() >0){
	  				
	  				for(Specification specification : specificationList){
	  					specification.setStockOccupy(0L);//占用库存量(已出售库存量)不显示
	  					if(specification.isEnable()){//启用
	  						membershipCard.addSpecification(specification);
	  					}
	  				}
	  				
	  				return membershipCard;
	  			}
	  		}
			
			
		}
		return null;
	}
	
	
	/**
	 * 购买会员卡"
	 * @param forum
	 */
	public Map<String,Object> buyMembershipCard_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		return value;
	}
}
