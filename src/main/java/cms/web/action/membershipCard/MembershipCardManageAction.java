package cms.web.action.membershipCard;


import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.membershipCard.MembershipCard;
import cms.bean.membershipCard.MembershipCardOrder;
import cms.bean.membershipCard.Specification;
import cms.bean.user.User;
import cms.bean.user.UserRole;
import cms.service.membershipCard.MembershipCardService;
import cms.service.setting.SettingService;
import cms.service.user.UserRoleService;
import cms.service.user.UserService;
import cms.utils.CommentedProperties;
import cms.utils.FileType;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;

/**
 * 会员卡管理
 *
 */
@Controller
@RequestMapping("/control/membershipCard/manage") 
public class MembershipCardManageAction {

	@Resource MembershipCardService membershipCardService;
	@Resource MembershipCardManage membershipCardManage;
	@Resource TextFilterManage textFilterManage;
	@Resource UserRoleService userRoleService;
	@Resource SettingService settingService;
	@Resource FileManage fileManage;
	@Resource UserService userService;
	
	
	/**
	 * 查询用户会员卡订单列表
	 * @param pageForm
	 * @param model
	 * @param userName 用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=membershipCardOrderList",method=RequestMethod.GET)
	public String queryUserMembershipCardOrderList(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		if(userName != null && !"".equals(userName.trim())){
			jpql.append(" and o.userName=?"+ (params.size()+1));
			params.add(userName.trim());
			
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
			
			
			User user = userService.findUserByUserName(userName);
			if(user != null){
				returnValue.put("currentUser", user);
			}
			returnValue.put("pageView", pageView);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
			
		}else{
			error.put("userName", "用户名称不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	
	/**
	 * 会员卡管理 添加界面显示
	 * @param productInfo
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model
			) throws Exception {
		//错误
		Map<String,Object> returnValue = new HashMap<String,Object>();
		//查询所有角色
		List<UserRole> userRoleList = userRoleService.findAllRole();
		if(userRoleList != null && userRoleList.size() >0){
	        Iterator<UserRole> iterator = userRoleList.iterator();
	        while (iterator.hasNext()) {
	        	UserRole userRole = iterator.next();
	        	if(userRole.getDefaultRole()){//如果是默认角色
	                iterator.remove();
	            }
	        }
	        for(UserRole userRole : userRoleList){
	        	userRole.setSelected(true);//默认选中第一个
	        	break;
	        }
		}
		
		returnValue.put("userRoleList", userRoleList);
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	
	
	/**
	 * 会员卡管理 添加
	 * @param specificationRowTable 规格表格参数行
	 * @param specificationName 规格名称
	 * @param enable 是否启用
	 * @param stock 库存量
	 * @param point 积分
	 * @param marketPrice 市场价
	 * @param sellingPrice 销售价
	 * @param duration 时长
	 * @param unit 时长单位
	 * @param descriptionTag 说明标签
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,MembershipCard formbean,BindingResult result,
			Integer[] specificationRowTable,String[] specificationName,Boolean[] enable,String[] stock,
			String[] point,String[] marketPrice,String[] sellingPrice,String[] descriptionTag,
			String[] duration,Integer[] unit,
			HttpServletRequest request) throws Exception {

		
		Map<String,String> error = new HashMap<String,String>();//错误
		MembershipCard membershipCard = new MembershipCard();
		if(formbean.getName() != null && !"".equals(formbean.getName().trim())){
			if(formbean.getName().trim().length() >150){
				error.put("name", "不能大于150个字符");
			}else{
				membershipCard.setName(formbean.getName().trim());//名称
			}
			
		}else{
			error.put("name", "名称不能为空");
		}
		if(formbean.getSubtitle() != null && !"".equals(formbean.getSubtitle().trim())){
			if(formbean.getSubtitle().trim().length() >150){
				error.put("subtitle", "不能大于150个字符");
			}else{
				membershipCard.setSubtitle(formbean.getSubtitle().trim());
			}
		}
		
		
		//添加规格
		if(specificationRowTable!= null && specificationRowTable.length>0){		
			for (int i = 0; i < specificationRowTable.length; i++) {//循环全部提交的规格表项
				Specification specification = new Specification();
				
				//规格名称
				if(specificationName != null && specificationName.length>0 && specificationName[i] != null && specificationName[i].trim().length() >0){
					if(specificationName[i].trim().length() >50){
						error.put("specificationName_"+i, "规格名称超过50个字符");
					}else{
						specification.setSpecificationName(specificationName[i].trim());
					}
				}else{
					error.put("specificationName_"+i, "请填写规格名称");
				}
				//是否启用
				if(enable != null && enable.length>0 && enable[i] != null){
					specification.setEnable(enable[i]);
				}else{
					error.put("enable_"+i, "请填写库存");
				}
				
				
				if(stock != null && stock.length>0 && stock[i] != null && !"".equals(stock[i].trim())){
					if(!Verification.isPositiveIntegerZero(stock[i].trim())){
						error.put("stock_"+i, "库存必须为数字");
					}else{
						if(stock[i].trim().length() >17){
							error.put("stock_"+i, "数字最大为17位");
						}else{
							long _stock = Long.parseLong(stock[i].trim());//库存量
							specification.setStock(_stock);
						}
						
					}
				}else{
					error.put("stock_"+i, "请填写库存");
				}
				
				if(point != null && point.length>0 && point[i] != null && !"".equals(point[i].trim())){
					if(!Verification.isPositiveIntegerZero(point[i].trim())){
						error.put("point_"+i, "积分必须为整数");
					}else{
						if(point[i].trim().length() >17){
							error.put("point_"+i, "数字最大为17位");
						}else{
							long _point = Long.parseLong(point[i].trim());//积分
							specification.setPoint(_point);
						}
						
					}
				}
				
				
				
				//市场价
				if(marketPrice != null && marketPrice.length>0 && marketPrice[i] != null && !"".equals(marketPrice[i].trim())){
					if(!Verification.isAmount(marketPrice[i].trim())){
						error.put("marketPrice_"+i, "市场价必须为金额类型");
					}else{
						if(marketPrice[i].trim().length() >10){
							error.put("marketPrice_"+i, "数字最大为10位");
						}else{
							specification.setMarketPrice(new BigDecimal(marketPrice[i].trim()));
						}
						
					}
				}
				//销售价
				if(sellingPrice != null && sellingPrice.length>0 && sellingPrice[i] != null && !"".equals(sellingPrice[i].trim())){
					if(!Verification.isAmount(sellingPrice[i].trim())){
						error.put("sellingPrice_"+i, "销售价必须为金额类型");
					}else{
						if(sellingPrice[i].trim().length() >10){
							error.put("sellingPrice_"+i, "数字最大为10位");
						}else{
							specification.setSellingPrice(new BigDecimal(sellingPrice[i].trim()));
						}
						
					}
				}
				if((specification.getPoint() ==null || specification.getPoint() <= 0) && 
						(specification.getSellingPrice() == null || specification.getSellingPrice().compareTo(new BigDecimal("0"))<=0)){
					error.put("sellingPrice_"+i, "积分和销售价必须填一个");
					
				}
				
				if(duration != null && duration.length >0 && duration[i] != null && !"".equals(duration[i].trim())){
					if(!Verification.isPositiveIntegerZero(duration[i].trim())){
						error.put("duration_"+i, "时长必须为整数");
					}else{
						if(duration[i].trim().length() >8){
							error.put("duration_"+i, "数字最大为8位");
						}else{
							int _duration = Integer.parseInt(duration[i].trim());//时长
							specification.setDuration(_duration);
						}
						
					}
				}else{
					error.put("duration_"+i, "时长不能为空");
				}
				
				//时长单位
				if(unit != null && unit.length >0 && unit[i] != null){
					specification.setUnit(unit[i]);
				}else{
					error.put("unit_"+i, "时长单位不能为空");
				}
				
				//排序
				specification.setSort(i);
				
				
				membershipCard.addSpecification(specification);
			}
		}
		
		if(descriptionTag != null && descriptionTag.length >0){
			List<String> descriptionTagList = new ArrayList<String>();
			for(String tag : descriptionTag){
				if(tag != null && !"".equals(tag.trim())){
					descriptionTagList.add(tag.trim());
				}
				
			}
			membershipCard.setDescriptionTagList(descriptionTagList);
			membershipCard.setDescriptionTagFormat(JsonUtils.toJSONString(descriptionTagList));
		}
		
		if(formbean.getUserRoleId() != null && !"".equals(formbean.getUserRoleId().trim())){
			membershipCard.setUserRoleId(formbean.getUserRoleId().trim());
		}else{
			error.put("userRoleId", "角色不能为空");
		}
		
		
		membershipCard.setState(formbean.getState());//是否上架   1:上架   2:下架
		membershipCard.setSort(formbean.getSort());//排序
		Date date = new Date();
		membershipCard.setCreateDate(date);//创建时间
		
		List<String> imageNameList = null;//上传图片文件名称
		List<String> flashNameList = null;//上传Flash文件名称
		List<String> mediaNameList = null;//上传音视频文件名称
		List<String> fileNameList = null;//上传文件名称
		String introduction = formbean.getIntroduction();
		if(introduction != null && !"".equals(introduction.trim())){
			//过滤标签
			introduction = textFilterManage.filterTag(request,introduction);
			Object[] object = textFilterManage.filterHtml(request,introduction,"membershipCard",null);
			String value = (String)object[0];
			imageNameList = (List<String>)object[1];
			flashNameList = (List<String>)object[3];
			mediaNameList = (List<String>)object[5];
			fileNameList = (List<String>)object[7];
			
			
			membershipCard.setIntroduction(value);//简介
			formbean.setIntroduction(value);//简介  回显
			
			
		}
		
		//设置最低价和最高价
		if(membershipCard.getSpecificationList() != null && membershipCard.getSpecificationList().size() >0){
			if(membershipCard.getSpecificationList().size() == 1){
				membershipCard.setLowestPrice(membershipCard.getSpecificationList().get(0).getSellingPrice());
				membershipCard.setHighestPrice(membershipCard.getSpecificationList().get(0).getSellingPrice());
			}else{
				List<BigDecimal> priceList = new ArrayList<BigDecimal>();
				for(Specification specification :membershipCard.getSpecificationList()){
					if(specification.getSellingPrice() != null){
						priceList.add(specification.getSellingPrice());
					}
				}
				if(priceList != null && priceList.size() >0){
					//排序 默认从低到高
					Collections.sort(priceList);
					
					membershipCard.setLowestPrice(priceList.get(0));
					membershipCard.setHighestPrice(priceList.get(priceList.size()-1));
				}
				
			}
		}
		
		//设置最低积分和最高积分
		if(membershipCard.getSpecificationList() != null && membershipCard.getSpecificationList().size() >0){
			if(membershipCard.getSpecificationList().size() == 1){
				membershipCard.setLowestPoint(membershipCard.getSpecificationList().get(0).getPoint());
				membershipCard.setHighestPoint(membershipCard.getSpecificationList().get(0).getPoint());
			}else{
				List<Long> pointList = new ArrayList<Long>();
				for(Specification specification :membershipCard.getSpecificationList()){
					if(specification.getPoint() != null){
						pointList.add(specification.getPoint());
					}
				}
				if(pointList != null && pointList.size() >0){
					//排序 默认从低到高
					Collections.sort(pointList);
					
					membershipCard.setLowestPoint(pointList.get(0));
					membershipCard.setHighestPoint(pointList.get(pointList.size()-1));
				}
				
				
			}
		}
		
	
		
		if(error.size() ==0){
			membershipCardService.saveMembershipCard(membershipCard);
			
			//清除缓存
			membershipCardManage.delete_cache_findById(membershipCard.getId());
			membershipCardManage.delete_cache_findSpecificationByMembershipCardId(membershipCard.getId());
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
			
					 if(imageName != null && !"".equals(imageName.trim())){
						 fileManage.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

					 }
				}
			}
			//falsh
			if(flashNameList != null && flashNameList.size() >0){
				for(String flashName :flashNameList){
					
					 if(flashName != null && !"".equals(flashName.trim())){
						fileManage.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
	
					 }
				}
			}
			//音视频
			if(mediaNameList != null && mediaNameList.size() >0){
				for(String mediaName :mediaNameList){
					if(mediaName != null && !"".equals(mediaName.trim())){
						fileManage.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
					
					}
				}
			}
			//文件
			if(fileNameList != null && fileNameList.size() >0){
				for(String fileName :fileNameList){
					if(fileName != null && !"".equals(fileName.trim())){
						fileManage.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
					
					}
				}
			}
			
			
		}
		
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	/**
	 * 会员卡管理 修改界面显示
	 * @param membershipCardId 会员卡Id
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Long membershipCardId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(membershipCardId != null && membershipCardId >0L){
			MembershipCard membershipCard = membershipCardService.findById(membershipCardId);
			if(membershipCard != null){
				String descriptionTagFormat = membershipCard.getDescriptionTagFormat();
				if(descriptionTagFormat != null && !"".equals(descriptionTagFormat.trim())){
					List<String> descriptionTagList = JsonUtils.toGenericObject(descriptionTagFormat.trim(), new TypeReference< List<String> >(){});
					membershipCard.setDescriptionTagList(descriptionTagList);
				}
				if(membershipCard.getIntroduction() != null && !"".equals(membershipCard.getIntroduction().trim())){
					//处理富文本路径
					membershipCard.setIntroduction(fileManage.processRichTextFilePath(membershipCard.getIntroduction(),"membershipCard"));
				}
				returnValue.put("membershipCard",membershipCard);
				List<Specification> specificationList = membershipCardService.findSpecificationByMembershipCardId(membershipCardId);
				for (Specification specification : specificationList) {
					
					//库存状态
					specification.setStockStatus(1);
					

					
				}
				
				//查询所有角色
				List<UserRole> userRoleList = userRoleService.findAllRole();
				if(userRoleList != null && userRoleList.size() >0){
			        Iterator<UserRole> iterator = userRoleList.iterator();
			        while (iterator.hasNext()) {
			        	UserRole userRole = iterator.next();
			        	if(membershipCard.getUserRoleId() != null && userRole.getId().equals(membershipCard.getUserRoleId())){
			        		userRole.setSelected(true);
			        	}
			        	if(userRole.getDefaultRole()){//如果是默认角色
			                iterator.remove();
			            }
			        }
			        
				}
				returnValue.put("userRoleList", userRoleList);
				//规格
				returnValue.put("specificationList",specificationList);
			}else{
				error.put("membershipCard", "会员卡不存在");
			}
		}else{
			error.put("membershipCardId", "会员卡Id不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}	
	}
	
	
	
	
	
	/**
	 * 会员卡管理 修改
	 * @param membershipCardId 会员卡Id
	 * @param specificationRowTable 规格表格参数行
	 * @param specificationId 规格Id
	 * @param specificationName 规格名称
	 * @param enable 是否启用
	 * @param stock 库存量
	 * @param stockStatus 更改库存状态
	 * @param changeStock 更改库存
	 * @param point 积分
	 * @param marketPrice 市场价
	 * @param sellingPrice 销售价
	 * @param duration 时长
	 * @param unit 时长单位
	 * @param descriptionTag 说明标签
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,MembershipCard formbean,BindingResult result,Long membershipCardId,
			Integer[] specificationRowTable,Long[] specificationId,String[] specificationName,Boolean[] enable,String[] stock,Integer[] stockStatus,String[] changeStock,
			String[] point,String[] marketPrice,String[] sellingPrice,String[] descriptionTag,
			String[] duration,Integer[] unit,
			HttpServletRequest request) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();//错误
		
		MembershipCard membershipCard = new MembershipCard();
		if(membershipCardId != null && membershipCardId >0L){
			MembershipCard old_membershipCard = membershipCardService.findById(membershipCardId);
			if(old_membershipCard != null){
				//旧规格
				List<Specification> old_specificationList = membershipCardService.findSpecificationByMembershipCardId(membershipCardId);
				
				
				if(formbean.getName() != null && !"".equals(formbean.getName().trim())){
					if(formbean.getName().trim().length() >150){
						error.put("name", "不能大于150个字符");
					}else{
						membershipCard.setName(formbean.getName().trim());//名称
					}
					
				}else{
					error.put("name", "名称不能为空");
				}
				if(formbean.getSubtitle() != null && !"".equals(formbean.getSubtitle().trim())){
					if(formbean.getSubtitle().trim().length() >150){
						error.put("subtitle", "不能大于150个字符");
					}else{
						membershipCard.setSubtitle(formbean.getSubtitle().trim());
					}
				}
				//添加规格
				if(specificationRowTable!= null && specificationRowTable.length>0){		
					for (int i = 0; i < specificationRowTable.length; i++) {//循环全部提交的规格表项
						Specification specification = new Specification();
						
						specification.setMembershipCardId(membershipCardId);
						if(specificationId != null && specificationId.length >0 && specificationId[i] != null && specificationId[i] >0){
							specification.setId(specificationId[i]);
						}
						
						
						
						//规格名称
						if(specificationName != null && specificationName.length >0 && specificationName[i] != null && specificationName[i].trim().length() >0){
							if(specificationName[i].trim().length() >50){
								error.put("specificationName_"+i, "规格名称超过50个字符");
							}else{
								specification.setSpecificationName(specificationName[i].trim());
							}
						}else{
							error.put("specificationName_"+i, "请填写规格名称");
						}
						
						//是否启用
						if(enable != null && enable.length >0 && enable[i] != null){
							specification.setEnable(enable[i]);
						}else{
							error.put("enable_"+i, "请填写库存");
						}
						
						//更改库存状态
						if(stockStatus != null && stockStatus.length >0 && stockStatus[i] != null){
							specification.setStockStatus(stockStatus[i]);
						}
						
						if(specification.getStockStatus().equals(0)){// 0:不变  1:增加   2:减少
							if(stock != null && stock.length >0 && stock[i] != null && !"".equals(stock[i].trim())){
								if(!Verification.isPositiveIntegerZero(stock[i].trim())){
									error.put("stock_"+i, "库存必须为数字");
								}else{
									if(stock[i].trim().length() >17){
										error.put("stock_"+i, "数字最大为17位");
									}else{
										long _stock = Long.parseLong(stock[i].trim());//库存量
										specification.setStock(_stock);
									}
								}
							}else{
								error.put("stock_"+i, "请填写库存");
							}
							
						}else{
							//解决回显库存问题
							if(old_specificationList != null && old_specificationList.size() >0){
								for(Specification old_specification :old_specificationList){
									if(old_specification.getId().equals(specification.getId())){
										specification.setStock(old_specification.getStock());
										break;
									}
								}
							}
							
							//更改库存
							if(changeStock != null && changeStock.length >0 && changeStock[i] != null && !"".equals(changeStock[i].trim())){
								if(!Verification.isPositiveIntegerZero(changeStock[i].trim())){
									error.put("stock_"+i, "库存必须为数字");
								}else{
									if(changeStock[i].trim().length() >17){
										error.put("stock_"+i, "数字最大为17位");
									}else{
										specification.setChangeStock(Long.parseLong(changeStock[i].trim()));
										
										if(specification.getStockStatus().equals(1)){// 0:不变  1:增加 
											if((specification.getStock()+specification.getChangeStock()) > 99999999999999999L){
												error.put("stock_"+i, "总库存数量不能超过17位数字");
											}
										}
										if(specification.getStockStatus().equals(2) && specification.getChangeStock() > specification.getStock()){
											error.put("stock_"+i, "减少库存量不能大于已存在库存量");
										}
									}
								}
							}
						}
						
						
						
						
						if(point != null && point.length >0 && point[i] != null && !"".equals(point[i].trim())){
							if(!Verification.isPositiveIntegerZero(point[i].trim())){
								error.put("point_"+i, "积分必须为整数");
							}else{
								if(point[i].trim().length() >17){
									error.put("point_"+i, "数字最大为17位");
								}else{
									long _point = Long.parseLong(point[i].trim());//积分
									specification.setPoint(_point);
								}
								
							}
						}
						
						
						
						//市场价
						if(marketPrice != null && marketPrice.length >0 && marketPrice[i] != null && !"".equals(marketPrice[i].trim())){
							if(!Verification.isAmount(marketPrice[i].trim())){
								error.put("marketPrice_"+i, "市场价必须为金额类型");
							}else{
								if(marketPrice[i].trim().length() >10){
									error.put("marketPrice_"+i, "数字最大为10位");
								}else{
									specification.setMarketPrice(new BigDecimal(marketPrice[i].trim()));
								}
								
							}
						}
						//销售价
						if(sellingPrice != null && sellingPrice.length >0 && sellingPrice[i] != null && !"".equals(sellingPrice[i].trim())){
							if(!Verification.isAmount(sellingPrice[i].trim())){
								error.put("sellingPrice_"+i, "销售价必须为金额类型");
							}else{
								if(sellingPrice[i].trim().length() >10){
									error.put("sellingPrice_"+i, "数字最大为10位");
								}else{
									specification.setSellingPrice(new BigDecimal(sellingPrice[i].trim()));
								}
								
							}
						}
						if((specification.getPoint() ==null || specification.getPoint() <= 0) && 
								(specification.getSellingPrice() == null || specification.getSellingPrice().compareTo(new BigDecimal("0"))<=0)){
							error.put("sellingPrice_"+i, "积分和销售价必须填一个");
							
						}
						
						if(duration != null && duration.length >0 && duration[i] != null && !"".equals(duration[i].trim())){
							if(!Verification.isPositiveIntegerZero(duration[i].trim())){
								error.put("duration_"+i, "时长必须为整数");
							}else{
								if(duration[i].trim().length() >8){
									error.put("duration_"+i, "数字最大为8位");
								}else{
									int _duration = Integer.parseInt(duration[i].trim());//时长
									specification.setDuration(_duration);
								}
								
							}
						}else{
							error.put("duration_"+i, "时长不能为空");
						}
						
						//时长单位
						if(unit != null && unit.length >0 && unit[i] != null){
							specification.setUnit(unit[i]);
						}else{
							error.put("unit_"+i, "时长单位不能为空");
						}
						
						//排序
						specification.setSort(i);
						
						
						membershipCard.addSpecification(specification);
					}
				}
				
				if(descriptionTag != null && descriptionTag.length >0){
					List<String> descriptionTagList = new ArrayList<String>();
					for(String tag : descriptionTag){
						if(tag != null && !"".equals(tag.trim())){
							descriptionTagList.add(tag.trim());
						}
						
					}
					membershipCard.setDescriptionTagList(descriptionTagList);
					membershipCard.setDescriptionTagFormat(JsonUtils.toJSONString(descriptionTagList));
				}
				
				if(formbean.getUserRoleId() != null && !"".equals(formbean.getUserRoleId().trim())){
					membershipCard.setUserRoleId(formbean.getUserRoleId().trim());
				}else{
					error.put("userRoleId", "角色不能为空");
				}
				
				
				membershipCard.setState(formbean.getState());//是否上架   1:上架   2:下架
				
				membershipCard.setSort(formbean.getSort());//排序
				
				String old_introduction = old_membershipCard.getIntroduction();
				List<String> imageNameList = null;//上传图片文件名称
				List<String> flashNameList = null;//上传Flash文件名称
				List<String> mediaNameList = null;//上传音视频文件名称
				List<String> fileNameList = null;//上传文件名称
				String introduction = formbean.getIntroduction();
				if(introduction != null && !"".equals(introduction.trim())){
					//过滤标签
					introduction = textFilterManage.filterTag(request,introduction);
					Object[] object = textFilterManage.filterHtml(request,introduction,"membershipCard",null);
					String value = (String)object[0];
					imageNameList = (List<String>)object[1];
					flashNameList = (List<String>)object[3];
					mediaNameList = (List<String>)object[5];
					fileNameList = (List<String>)object[7];
					
					
					membershipCard.setIntroduction(value);//简介
					formbean.setIntroduction(value);//简介  回显
					
					
				}
				
				//设置最低价和最高价
				if(membershipCard.getSpecificationList() != null && membershipCard.getSpecificationList().size() >0){
					if(membershipCard.getSpecificationList().size() == 1){
						membershipCard.setLowestPrice(membershipCard.getSpecificationList().get(0).getSellingPrice());
						membershipCard.setHighestPrice(membershipCard.getSpecificationList().get(0).getSellingPrice());
					}else{
						List<BigDecimal> priceList = new ArrayList<BigDecimal>();
						for(Specification specification :membershipCard.getSpecificationList()){
							if(specification.getSellingPrice() != null){
								priceList.add(specification.getSellingPrice());
							}
						}
						if(priceList != null && priceList.size() >0){
							//排序 默认从低到高
							Collections.sort(priceList);
							
							membershipCard.setLowestPrice(priceList.get(0));
							membershipCard.setHighestPrice(priceList.get(priceList.size()-1));
						}
						
					}
				}
				
				//设置最低积分和最高积分
				if(membershipCard.getSpecificationList() != null && membershipCard.getSpecificationList().size() >0){
					if(membershipCard.getSpecificationList().size() == 1){
						membershipCard.setLowestPoint(membershipCard.getSpecificationList().get(0).getPoint());
						membershipCard.setHighestPoint(membershipCard.getSpecificationList().get(0).getPoint());
					}else{
						List<Long> pointList = new ArrayList<Long>();
						for(Specification specification :membershipCard.getSpecificationList()){
							if(specification.getPoint() != null){
								pointList.add(specification.getPoint());
							}
						}
						if(pointList != null && pointList.size() >0){
							//排序 默认从低到高
							Collections.sort(pointList);
							
							membershipCard.setLowestPoint(pointList.get(0));
							membershipCard.setHighestPoint(pointList.get(pointList.size()-1));
						}
						
						
					}
				}
				
				
				membershipCard.setState(formbean.getState());//是否上架   1:上架   2:下架
				membershipCard.setSort(formbean.getSort());//排序
				membershipCard.setId(old_membershipCard.getId());//Id
				
				List<Specification> add_specificationList = new ArrayList<Specification>();//添加规格表
				List<Specification> update_specificationList = new ArrayList<Specification>();//修改规格表
				List<Long> delete_specificationIdList = new ArrayList<Long>();//删除规格表Id
				if(old_specificationList != null && old_specificationList.size() >0){//删除相同Id的旧规格
					for(Specification ps : old_specificationList){
						delete_specificationIdList.add(ps.getId());
					}
				}
				
				//比较规格表
				List<Specification> new_productStyleList = membershipCard.getSpecificationList();//新规格
				if(new_productStyleList != null && new_productStyleList.size() >0){
					for(Specification new_specification : new_productStyleList){
						if(new_specification.getId() != null && new_specification.getId() >0L){
							if(old_specificationList != null && !old_specificationList.contains(new_specification)){
								update_specificationList.add(new_specification);
							}else{
								//如果有修改库存
								if(new_specification.getChangeStock() != null && new_specification.getChangeStock() >0L){
									update_specificationList.add(new_specification);
								}
							}
							
							//删除相同Id的旧规格
							if(delete_specificationIdList != null && delete_specificationIdList.contains(new_specification.getId())){
								delete_specificationIdList.remove(new_specification.getId());
							}
						}else{
							add_specificationList.add(new_specification);
						}
					}
				}
				if(error.size() ==0){
					membershipCardService.updateMembershipCard(membershipCard, add_specificationList, update_specificationList, delete_specificationIdList);
					
					//清除缓存
					membershipCardManage.delete_cache_findById(membershipCardId);
					membershipCardManage.delete_cache_findSpecificationByMembershipCardId(membershipCardId);
					
					
					List<String> oldPathFileList = new ArrayList<String>();//旧路径文件
					Object[] obj = textFilterManage.readPathName(old_introduction,"membershipCard");
					if(obj != null && obj.length >0){
						//旧图片
						List<String> old_imageNameList = (List<String>)obj[0];
						if(old_imageNameList != null && old_imageNameList.size() >0){
							
					        Iterator<String> iter = old_imageNameList.iterator();
					        while (iter.hasNext()) {
					        	String imageName = iter.next();  
								for(String new_imageName : imageNameList){
									if(imageName.equals("file/membershipCard/"+new_imageName)){
										iter.remove();
										break;
									}
								}
							}
							if(old_imageNameList != null && old_imageNameList.size() >0){
								for(String imageName : old_imageNameList){
									oldPathFileList.add(FileUtil.toSystemPath(imageName));
								}
								
							}
						}
						
						//旧Flash
						List<String> old_flashNameList = (List<String>)obj[1];		
						if(old_flashNameList != null && old_flashNameList.size() >0){		
					        Iterator<String> iter = old_flashNameList.iterator();
					        while (iter.hasNext()) {
					        	String flashName = iter.next();  
								for(String new_flashName : flashNameList){
									if(flashName.equals("file/membershipCard/"+new_flashName)){
										iter.remove();
										break;
									}
								}
							}
							if(old_flashNameList != null && old_flashNameList.size() >0){
								for(String flashName : old_flashNameList){
									oldPathFileList.add(FileUtil.toSystemPath(flashName));
								}
								
							}
						}

						//旧影音
						List<String> old_mediaNameList = (List<String>)obj[2];	
						if(old_mediaNameList != null && old_mediaNameList.size() >0){		
					        Iterator<String> iter = old_mediaNameList.iterator();
					        while (iter.hasNext()) {
					        	String mediaName = iter.next();  
								for(String new_mediaName : mediaNameList){
									if(mediaName.equals("file/membershipCard/"+new_mediaName)){
										iter.remove();
										break;
									}
								}
							}
							if(old_mediaNameList != null && old_mediaNameList.size() >0){
								for(String mediaName : old_mediaNameList){
									oldPathFileList.add(FileUtil.toSystemPath(mediaName));
								}
								
							}
						}
						
						//旧文件
						List<String> old_fileNameList = (List<String>)obj[3];		
						if(old_fileNameList != null && old_fileNameList.size() >0){		
					        Iterator<String> iter = old_fileNameList.iterator();
					        while (iter.hasNext()) {
					        	String fileName = iter.next();  
								for(String new_fileName : fileNameList){
									if(fileName.equals("file/membershipCard/"+new_fileName)){
										iter.remove();
										break;
									}
								}
							}
							if(old_fileNameList != null && old_fileNameList.size() >0){
								for(String fileName : old_fileNameList){
									oldPathFileList.add(FileUtil.toSystemPath(fileName));
								}
								
							}
						}
					}
					//删除旧路径文件
					if(oldPathFileList != null && oldPathFileList.size() >0){
						for(String oldPathFile :oldPathFileList){
							//替换路径中的..号
							oldPathFile = FileUtil.toRelativePath(oldPathFile);
							
							//删除旧路径文件
							Boolean state = fileManage.deleteFile(oldPathFile);
							if(state != null && state == false){
								//替换指定的字符，只替换第一次出现的
								oldPathFile = StringUtils.replaceOnce(oldPathFile, "file"+File.separator+"membershipCard"+File.separator, "");
								oldPathFile = StringUtils.replace(oldPathFile, File.separator, "_");//替换所有出现过的字符
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator+oldPathFile);

							}
						}
					}
					
					//删除图片锁
					if(imageNameList != null && imageNameList.size() >0){
						for(String imageName :imageNameList){
							 if(imageName != null && !"".equals(imageName.trim())){
								 fileManage.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
							 }
						}
					}
					//删除Falsh锁
					if(flashNameList != null && flashNameList.size() >0){
						for(String flashName :flashNameList){
							 if(flashName != null && !"".equals(flashName.trim())){
								 fileManage.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
							 }
						}
					}
					//删除音视频锁
					if(mediaNameList != null && mediaNameList.size() >0){
						for(String mediaName :mediaNameList){
							if(mediaName != null && !"".equals(mediaName.trim())){
								fileManage.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
								
							}
						}
					}
					//删除文件锁
					if(fileNameList != null && fileNameList.size() >0){
						for(String fileName :fileNameList){
							if(fileName != null && !"".equals(fileName.trim())){
								fileManage.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
								
							}
						}
					}
				}
			}else{
				error.put("membershipCardId", "会员卡不存在");
			}
		}else{
			error.put("membershipCardId", "会员卡Id不能为空");
		}
		

		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	
	
	
	/**
	 * 图片上传
	 * @param dir: 上传类型，分别为image、flash、media、file 
	 * @param fileName 文件名称 预签名时有值
	 */
	@ResponseBody
	@RequestMapping(params="method=upload",method=RequestMethod.POST)
	public String upload(ModelMap model,String dir,String fileName,
			MultipartFile file, HttpServletResponse response) throws Exception {

		Map<String,Object> returnJson = new HashMap<String,Object>();
		String errorMessage  = "";
		
		if(dir != null){
			DateTime dateTime = new DateTime();     
		     
			String date = dateTime.toString("yyyy-MM-dd");
			
			int fileSystem = fileManage.getFileSystem();
			if(fileSystem ==10 || fileSystem == 20 || fileSystem == 30){//10.SeaweedFS 20.MinIO 30.阿里云OSS
				if(fileName != null && !"".equals(fileName.trim())){
					//取得文件后缀
					String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
					if(dir.equals("image")){
						//允许上传格式
						List<String> formatList = CommentedProperties.readRichTextAllowImageUploadFormat();
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							
							//文件锁目录
							String lockPathDir = "file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator;
							
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_image_"+newFileName);
							String presigne = fileManage.createPresigned("file/membershipCard/"+date+"/image/"+newFileName,null);//创建预签名
							
							
				
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "文件格式不允许上传";
						}
						
						
					}else if(dir.equals("flash")){
						//允许上传格式
						List<String> formatList = new ArrayList<String>();
						formatList.add("swf");
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							//文件锁目录
							String lockPathDir = "file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator;
							
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_flash_"+newFileName);
							String presigne = fileManage.createPresigned("file/membershipCard/"+date+"/flash/"+newFileName,null);//创建预签名
							
							
				
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);
							
						}else{
							errorMessage = "文件格式不允许上传";
						}
						
					}else if(dir.equals("media")){
						//允许上传格式
						List<String> formatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							//文件锁目录
							String lockPathDir = "file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator;
							
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_media_"+newFileName);
							String presigne = fileManage.createPresigned("file/membershipCard/"+date+"/media/"+newFileName,null);//创建预签名
							
							
				
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);

						}else{
							errorMessage = "文件格式不允许上传";
						}
					}else if(dir.equals("file")){
						//允许上传格式
						List<String> formatList = CommentedProperties.readRichTextAllowFileUploadFormat();
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							//文件锁目录
							String lockPathDir = "file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator;
							
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_file_"+newFileName);
							String presigne = fileManage.createPresigned("file/membershipCard/"+date+"/file/"+newFileName,null);//创建预签名
							
							
				
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "文件格式不允许上传";
						}
					}else{
						errorMessage = "缺少dir参数";
					}
				}else{
					errorMessage = "文件名称不能为空";
				}
			}else{//0.本地系统
				if(file != null && !file.isEmpty()){
					//当前文件名称
					String sourceFileName = file.getOriginalFilename();
					
					String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();

					if(dir.equals("image")){
						//允许上传图片格式
						List<String> formatList = CommentedProperties.readRichTextAllowImageUploadFormat();
						

						//验证文件类型
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
						
						//如果用flash控件上传
						if(file.getContentType().equalsIgnoreCase("application/octet-stream")){
							String fileType = FileType.getType(file.getInputStream());
							for (String format :formatList) {
								if(format.equalsIgnoreCase(fileType)){
									authentication = true;
									break;
								}
							}
						}
						
						if(authentication){
							//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
							String pathDir = "file"+File.separator+"membershipCard"+File.separator + date +File.separator +"image"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator;
							
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							//生成文件保存目录
							fileManage.createFolder(pathDir);
							//生成锁文件保存目录
							fileManage.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_image_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
				
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/membershipCard/"+date+"/image/"+newFileName);
							return JsonUtils.toJSONString(returnJson);
							
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else if(dir.equals("flash")){
					
						List<String> flashFormatList = new ArrayList<String>();
						flashFormatList.add("swf");
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),flashFormatList);

						if(authentication){
							
							String pathDir = "file"+File.separator+"membershipCard"+File.separator + date+ File.separator +"flash"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator;
							 //构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							//生成文件保存目录
							fileManage.createFolder(pathDir);
							//生成锁文件保存目录
							fileManage.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_flash_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
							
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/membershipCard/"+date+"/flash/"+newFileName);
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
						
						
						
					}else if(dir.equals("media")){	
						//允许上传视音频格式
						List<String> formatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
						
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
						
						if(authentication){	
							//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
							String pathDir = "file"+File.separator+"membershipCard"+File.separator + date+ File.separator +"media"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							//生成文件保存目录
							fileManage.createFolder(pathDir);
							//生成锁文件保存目录
							fileManage.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_media_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());

							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/membershipCard/"+date+"/media/"+newFileName);
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else if(dir.equals("file")){
						//允许上传文件格式
						List<String> formatList = CommentedProperties.readRichTextAllowFileUploadFormat();
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
						if(authentication){
							//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
							String pathDir = "file"+File.separator+"membershipCard"+File.separator + date+ File.separator +"file"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							//生成文件保存目录
							fileManage.createFolder(pathDir);
							//生成锁文件保存目录
							fileManage.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,date +"_file_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/membershipCard/"+date+"/file/"+newFileName);
							returnJson.put("title", file.getOriginalFilename());//旧文件名称
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else{
						errorMessage = "缺少dir参数";
					}
				}else{
					errorMessage = "文件不能为空";
				}
				
				
			}
		}else{
			errorMessage = "参数不能为空";
		}
		
		//上传失败
		returnJson.put("error", 1);
		returnJson.put("message", errorMessage);
		return JsonUtils.toJSONString(returnJson);
	}
	
	
	/**
	 * 会员卡管理   删除
	 * @param model
	 * @param membershipCardId 会员卡Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String delete(ModelMap model,Long membershipCardId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		if(membershipCardId != null && membershipCardId >0L){
			int i = membershipCardService.deleteMembershipCard(membershipCardId);	
			
			//清除缓存
			membershipCardManage.delete_cache_findById(membershipCardId);
			membershipCardManage.delete_cache_findSpecificationByMembershipCardId(membershipCardId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("membershipCardId", "会员卡Id不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
