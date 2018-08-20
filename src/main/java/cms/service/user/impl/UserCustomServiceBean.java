package cms.service.user.impl;

import java.util.List;
import javax.persistence.Query;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.user.UserCustom;
import cms.bean.user.UserInputValue;
import cms.service.besa.DaoSupport;
import cms.service.user.UserCustomService;

/**
 * 用户自定义注册功能项
 *
 */
@Service
@Transactional
public class UserCustomServiceBean extends DaoSupport<UserCustom> implements UserCustomService {

	/**
	 * 根据Id查询用户自定义注册功能项
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public UserCustom findUserCustomById(Integer userCustomId){
		Query query =  em.createQuery("select o from UserCustom o where o.id=?1");
		query.setParameter(1, userCustomId);
		List<UserCustom> userCustomList = query.getResultList();
		if(userCustomList != null && userCustomList.size() >0){
			for(UserCustom userCustom : userCustomList){
				return userCustom;
			}
		}
		return null;
	}
	
	/**
	 * 查询所有用户自定义注册功能项
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<UserCustom> findAllUserCustom(){
		Query query =  em.createQuery("select o from UserCustom o ORDER BY o.sort desc");
		return query.getResultList();
	}
	/**
	 * 查询所有用户自定义注册功能项
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="userCustomServiceBean_cache",key="'findAllUserCustom_default'")
	public List<UserCustom> findAllUserCustom_cache(){
		return this.findAllUserCustom();
	}
	
	/**
	 * 根据用户Id查询自定义项值
	 * @param userId 用户Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<UserInputValue> findUserInputValueByUserName(Long userId){
		Query query =  em.createQuery("select o from UserInputValue o where o.userId=?1");
		query.setParameter(1, userId);
		return query.getResultList();
		
	}
	
	/**
	 * 保存自定义项
	 * @param userCustom 用户自定义项
	 */
	@CacheEvict(value="userCustomServiceBean_cache",allEntries=true)
	public void saveUserCustom(UserCustom userCustom){
		this.save(userCustom);
	}
	
	/**
	 * 修改自定义项值
	 * @param userCustom 用户自定义项
	 * @param deleteItem 删除自定义单选按钮.多选按钮.下拉列表  key参数
	 */
	@CacheEvict(value="userCustomServiceBean_cache",allEntries=true)
	public Integer updateUserCustom(UserCustom userCustom,List<String> deleteItem){
		Query query = em.createQuery("update UserCustom o set " +
				" o.name=?1,o.required=?2, o.search=?3,o.visible=?4,o.value=?5,o.fieldFilter=?6," +
				" o.size=?7, o.maxlength=?8, o.multiple=?9, o.selete_size=?10,o.rows=?11,o.cols=?12, " +
				" o.regular=?13, o.tip=?14,o.sort=?15 where o.id=?16")
		.setParameter(1, userCustom.getName())//注册项名称
		.setParameter(2, userCustom.isRequired())//是否必填
		.setParameter(3, userCustom.isSearch())//后台可搜索
		.setParameter(4, userCustom.isVisible())//是否显示
		.setParameter(5, userCustom.getValue())//参数值  json LinkedHashMap<String,String>格式
		.setParameter(6, userCustom.getFieldFilter())//字段过滤方式
		.setParameter(7, userCustom.getSize())//输入框的宽度
		.setParameter(8, userCustom.getMaxlength())//输入框中字符的最大长度
		.setParameter(9, userCustom.isMultiple())//是否可选择多个选项
		.setParameter(10, userCustom.getSelete_size())//下拉列表中可见选项的数目
		.setParameter(11, userCustom.getRows())//文本域内的可见行数
		.setParameter(12, userCustom.getCols())//文本域内的可见宽度
		.setParameter(13, userCustom.getRegular())//过滤正则表达式
		.setParameter(14, userCustom.getTip())//提示
		.setParameter(15, userCustom.getSort())//排序
		.setParameter(16, userCustom.getId());//Id
		
		
		int i = query.executeUpdate();
		
		//删除旧多选项参数
		if(deleteItem != null && deleteItem.size() >0){
			Query delete = em.createQuery("delete from UserInputValue o where o.options in(:options)")
			.setParameter("options", deleteItem);
			delete.executeUpdate();
		}
		
		
		
		return i;
	}
	
	/**
	 * 删除自定义项值
	 * @param userCustomId 自定义项Id
	 */
	@CacheEvict(value="userCustomServiceBean_cache",allEntries=true)
	public Integer deleteUserCustom(Integer userCustomId){
		Query delete_userCustom = em.createQuery("delete from UserCustom o where o.id=?1")
				.setParameter(1, userCustomId);
		int i =	delete_userCustom.executeUpdate();
		
		
		//用户自定义注册功能项用户输入值
		Query delete = em.createQuery("delete from UserInputValue o where o.userCustomId=?1")
		.setParameter(1, userCustomId);
		delete.executeUpdate();
		return i;	
	}

}
