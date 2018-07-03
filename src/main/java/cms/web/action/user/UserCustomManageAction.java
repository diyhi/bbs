package cms.web.action.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.user.UserCustom;
import cms.service.user.UserCustomService;
import cms.utils.JsonUtils;
import cms.utils.RedirectPath;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.SystemException;

/**
 * 用户自定义注册功能项
 *
 */

@Controller
@RequestMapping("/control/userCustom/manage") 
public class UserCustomManageAction {
	//注入业务bean
	@Resource(name="userCustomServiceBean")
	private UserCustomService userCustomService;

	/**
	 * 用户自定义注册功能项 添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(UserCustom userCustom,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return "jsp/user/add_userCustom";
	}
	/**
	 * 用户自定义注册功能项 添加
	 * @param String[] _itemValue; 单选按钮值,多选按钮值,下拉列表值
	 */
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(ModelMap model,UserCustom formbean,String _sort,String _size,String _maxlength,
			String[] _itemValue,String _selete_size,String _rows,String _cols,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		UserCustom userCustom = new UserCustom();
		LinkedHashMap<String,String> itemValue_map = new LinkedHashMap<String,String>();//key:选项值 value:选项文本
		
		
		if(formbean.getName() != null && !"".equals(formbean.getName())){//验证注册项名称
			if(formbean.getName().length() >50){
				error.put("name", "注册项名称不能超过50个字符");
			}
			userCustom.setName(formbean.getName());
		}else{
			error.put("name", "请填写注册项名称");
		}
		
		if(_sort != null && !"".equals(_sort.trim())){//排序
			boolean sort_verification = Verification.isPositiveInteger(_sort.trim());//正整数
			if(!sort_verification){
				error.put("sort", "请填写大于0的整数");
			}else{
				userCustom.setSort(Integer.parseInt(_sort.trim()));
			}
		}
		userCustom.setRequired(formbean.isRequired());//是否必填
		userCustom.setSearch(formbean.isSearch());//后台可搜索
		userCustom.setVisible(formbean.isVisible());//是否显示
		
		userCustom.setChooseType(formbean.getChooseType());//选框类型
		
		
		if(userCustom.getChooseType().equals(1)){//输入框
			if(_size != null && !"".equals(_size.trim())){//输入框的宽度
				boolean size_verification = Verification.isPositiveInteger(_size.trim());//正整数
				if(!size_verification){
					error.put("size", "请填写正整数");
				}else{
					userCustom.setSize(Integer.parseInt(_size.trim()));
				}
			}
			if(_maxlength != null && !"".equals(_maxlength.trim())){//输入框中字符的最大长度
				boolean maxlength_verification = Verification.isPositiveInteger(_maxlength.trim());//正整数
				if(!maxlength_verification){
					error.put("maxlength", "请填写正整数");
				}else{
					userCustom.setMaxlength(Integer.parseInt(_maxlength.trim()));
				}
			}
			userCustom.setFieldFilter(formbean.getFieldFilter());
			if(userCustom.getFieldFilter().equals(5)){//5.正则表达式
				if(formbean.getRegular() == null || "".equals(formbean.getRegular().trim())){
					error.put("regular", "请填写正则表达式");
				}else{
					userCustom.setRegular(formbean.getRegular().trim());
				}
			}
			
		}
		if(userCustom.getChooseType().equals(2) || userCustom.getChooseType().equals(3) || userCustom.getChooseType().equals(4)){
			
			if(_itemValue != null && _itemValue.length >0){
				for(String item : _itemValue){
					if(item != null && !"".equals(item.trim())){
						itemValue_map.put(UUIDUtil.getUUID32(), item);
					}
				}
			}else{
				error.put("itemValue", "请填写选项值");
			}
			if(itemValue_map.size() == 0){
				error.put("itemValue", "请填写选项值");
			}
			userCustom.setValue(JsonUtils.toJSONString(itemValue_map));
		}
		if(userCustom.getChooseType().equals(4)){//4.下拉列表
			userCustom.setMultiple(formbean.isMultiple());//是否可选择多个选项
			
			if(_selete_size != null && !"".equals(_selete_size.trim())){//下拉列表中可见选项的数目
				boolean selete_size_verification = Verification.isPositiveInteger(_selete_size.trim());//正整数
				if(!selete_size_verification){
					error.put("selete_size", "请填写正整数");
				}else{
					userCustom.setSelete_size(Integer.parseInt(_selete_size.trim()));
				}
			}
			
		}
		if(userCustom.getChooseType().equals(5)){//5.文本域
			if(_rows != null && !"".equals(_rows.trim())){//文本域内的可见行数
				boolean rows_verification = Verification.isPositiveInteger(_rows.trim());//正整数
				if(!rows_verification){
					error.put("rows", "请填写正整数");
				}else{
					userCustom.setRows(Integer.parseInt(_rows.trim()));
				}
			}
			if(_cols != null && !"".equals(_cols.trim())){//文本域内的可见宽度
				boolean cols_verification = Verification.isPositiveInteger(_cols.trim());//正整数
				if(!cols_verification){
					error.put("cols", "请填写正整数");
				}else{
					userCustom.setCols(Integer.parseInt(_cols.trim()));
				}
			}
		}
		
		
		if(formbean.getTip() != null && !"".equals(formbean.getTip())){//提示
			if(formbean.getTip().length() >250){
				error.put("tip", "提示不能超过50个字符");
			}
			userCustom.setTip(formbean.getTip());
		}
		
		
		if(error.size() >0){
			model.addAttribute("error", error);
			
			formbean.setSort(userCustom.getSort());
			formbean.setSize(userCustom.getSize());
			formbean.setMaxlength(userCustom.getMaxlength());
			formbean.setSelete_size(userCustom.getSelete_size());
			formbean.setRows(userCustom.getRows());
			formbean.setCols(userCustom.getCols());
			model.addAttribute("userCustom", formbean);
			model.addAttribute("itemValue_map", itemValue_map);
			
			return "jsp/user/add_userCustom";
		}
		userCustomService.saveUserCustom(userCustom);
		
		request.setAttribute("message", "添加用户自定义注册功能项成功");
		request.setAttribute("urladdress", RedirectPath.readUrl("control.userCustom.list"));
		return "jsp/common/message";
	}
	

	/**
	 * 用户自定义注册功能项  修改界面显示
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Integer id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(id == null){
			throw new SystemException("参数错误");
		}
		
		UserCustom userCustom = userCustomService.findUserCustomById(id);
		if(userCustom == null){
			throw new SystemException("自定义注册功能项不存在");
		}
		
		if(userCustom.getValue() != null && !"".equals(userCustom.getValue())){
			
			LinkedHashMap<String,String> itemValue_map = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});//key:选项值 value:选项文本
			model.addAttribute("itemValue_map", itemValue_map);
		}
		
		
		model.addAttribute("userCustom", userCustom);
		return "jsp/user/edit_userCustom";
	}
	/**
	 * 用户自定义注册功能项 修改
	 * @param String[] _itemValue; 单选按钮值,多选按钮值,下拉列表值
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,UserCustom formbean,String _sort,String _size,String _maxlength,
			String[] _itemValue,String[] itemKey,String _selete_size,String _rows,String _cols,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		
		UserCustom old_userCustom = userCustomService.findUserCustomById(formbean.getId());
		if(old_userCustom == null){
			throw new SystemException("自定义注册功能项不存在");
		}
		LinkedHashMap<String,String> itemValue_map = new LinkedHashMap<String,String>();//key:选项值 value:选项文本
		
		
		if(old_userCustom.getValue() != null && !"".equals(old_userCustom.getValue())){
			old_userCustom.setItemValue(JsonUtils.toGenericObject(old_userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){}));//key:选项值 value:选项文本
		}
		
		
		UserCustom userCustom = new UserCustom();
		userCustom.setId(formbean.getId());
		if(formbean.getName() != null && !"".equals(formbean.getName())){//验证注册项名称
			if(formbean.getName().length() >50){
				error.put("name", "注册项名称不能超过50个字符");
			}
			userCustom.setName(formbean.getName());
		}else{
			error.put("name", "请填写注册项名称");
		}
		
		if(_sort != null && !"".equals(_sort.trim())){//排序
			boolean sort_verification = Verification.isPositiveInteger(_sort.trim());//正整数
			if(!sort_verification){
				error.put("sort", "请填写大于0的整数");
			}else{
				userCustom.setSort(Integer.parseInt(_sort.trim()));
			}
		}
		userCustom.setRequired(formbean.isRequired());//是否必填
		userCustom.setSearch(formbean.isSearch());//后台可搜索
		userCustom.setVisible(formbean.isVisible());//是否显示
		
		userCustom.setChooseType(old_userCustom.getChooseType());//选框类型不能改
		
		
		if(userCustom.getChooseType().equals(1)){//输入框
			if(_size != null && !"".equals(_size.trim())){//输入框的宽度
				boolean size_verification = Verification.isPositiveInteger(_size.trim());//正整数
				if(!size_verification){
					error.put("size", "请填写正整数");
				}else{
					userCustom.setSize(Integer.parseInt(_size.trim()));
				}
			}
			if(_maxlength != null && !"".equals(_maxlength.trim())){//输入框中字符的最大长度
				boolean maxlength_verification = Verification.isPositiveInteger(_maxlength.trim());//正整数
				if(!maxlength_verification){
					error.put("maxlength", "请填写正整数");
				}else{
					userCustom.setMaxlength(Integer.parseInt(_maxlength.trim()));
				}
			}
			userCustom.setFieldFilter(formbean.getFieldFilter());
			if(userCustom.getFieldFilter().equals(5)){//5.正则表达式
				if(formbean.getRegular() == null || "".equals(formbean.getRegular().trim())){
					error.put("regular", "请填写正则表达式");
				}else{
					userCustom.setRegular(formbean.getRegular().trim());
				}
			}
			
		}
		if(userCustom.getChooseType().equals(2) || userCustom.getChooseType().equals(3) || userCustom.getChooseType().equals(4)){
			
			if(_itemValue != null && _itemValue.length >0){
				for(int i = 0; i< _itemValue.length; i++){
					String item = _itemValue[i];
					if(item != null && !"".equals(item.trim())){
						String oldItemKey = itemKey[i];//旧key
						if(oldItemKey != null && !"".equals(oldItemKey.trim())){
							LinkedHashMap<String,String> oldItemValue_map = old_userCustom.getItemValue();			
							if(oldItemValue_map.get(oldItemKey) != null){//已存在key
								itemValue_map.put(oldItemKey, item);
								continue;
							}	
						}
						itemValue_map.put(UUIDUtil.getUUID32(), item);
					}
				}
			}else{
				error.put("itemValue", "请填写选项值");
			}
			if(itemValue_map.size() == 0){
				error.put("itemValue", "请填写选项值");
			}
			userCustom.setValue(JsonUtils.toJSONString(itemValue_map));
		}
		if(userCustom.getChooseType().equals(4)){//4.下拉列表
			userCustom.setMultiple(formbean.isMultiple());//是否可选择多个选项
			
			if(_selete_size != null && !"".equals(_selete_size.trim())){//下拉列表中可见选项的数目
				boolean selete_size_verification = Verification.isPositiveInteger(_selete_size.trim());//正整数
				if(!selete_size_verification){
					error.put("selete_size", "请填写正整数");
				}else{
					userCustom.setSelete_size(Integer.parseInt(_selete_size.trim()));
				}
			}
			
		}
		if(userCustom.getChooseType().equals(5)){//5.文本域
			if(_rows != null && !"".equals(_rows.trim())){//文本域内的可见行数
				boolean rows_verification = Verification.isPositiveInteger(_rows.trim());//正整数
				if(!rows_verification){
					error.put("rows", "请填写正整数");
				}else{
					userCustom.setRows(Integer.parseInt(_rows.trim()));
				}
			}
			if(_cols != null && !"".equals(_cols.trim())){//文本域内的可见宽度
				boolean cols_verification = Verification.isPositiveInteger(_cols.trim());//正整数
				if(!cols_verification){
					error.put("cols", "请填写正整数");
				}else{
					userCustom.setCols(Integer.parseInt(_cols.trim()));
				}
			}
		}
		
		
		if(formbean.getTip() != null && !"".equals(formbean.getTip())){//提示
			if(formbean.getTip().length() >250){
				error.put("tip", "提示不能超过50个字符");
			}
			userCustom.setTip(formbean.getTip());
		}
		
		
		if(error.size() >0){
			model.addAttribute("error", error);
			formbean.setChooseType(old_userCustom.getChooseType());//选框类型不能改
			formbean.setSort(userCustom.getSort());
			formbean.setSize(userCustom.getSize());
			formbean.setMaxlength(userCustom.getMaxlength());
			formbean.setSelete_size(userCustom.getSelete_size());
			formbean.setRows(userCustom.getRows());
			formbean.setCols(userCustom.getCols());
			model.addAttribute("userCustom", formbean);
			model.addAttribute("itemValue_map", itemValue_map);
			
			return "jsp/user/edit_userCustom";
		}
		//删除自定义单选按钮.多选按钮.下拉列表
		List<String> deleteItem = new ArrayList<String>();
		
		if(userCustom.getChooseType().equals(2) || userCustom.getChooseType().equals(3) || userCustom.getChooseType().equals(4)){
			LinkedHashMap<String,String> oldItemValue_map = old_userCustom.getItemValue();
			
			for(Map.Entry<String, String> itemValue : itemValue_map.entrySet()){
				oldItemValue_map.remove(itemValue.getKey());
			}
			deleteItem.addAll(oldItemValue_map.keySet());
		}
		
		userCustomService.updateUserCustom(userCustom, deleteItem);
		
		request.setAttribute("message", "修改用户自定义注册功能项成功");
		request.setAttribute("urladdress", RedirectPath.readUrl("control.userCustom.list"));
		return "jsp/common/message";
	}
	
	
	/**
	 * 用户自定义注册功能项 删除
	 */
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Integer id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(id != null && id >0){
			UserCustom userCustom = userCustomService.findUserCustomById(id);
			if(userCustom != null){
				userCustomService.deleteUserCustom(id);
				return "1";
			}
		}
		return "0";
	}
}
