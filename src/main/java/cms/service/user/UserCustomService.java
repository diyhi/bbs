package cms.service.user;

import java.util.List;

import cms.bean.user.UserCustom;
import cms.bean.user.UserInputValue;
import cms.service.besa.DAO;

/**
 * 用户自定义注册功能项接口
 *
 */
public interface UserCustomService extends DAO<UserCustom>{
	/**
	 * 根据Id查询用户自定义注册功能项
	 */
	public UserCustom findUserCustomById(Integer userCustomId);
	/**
	 * 查询所有用户自定义注册功能项
	 */
	public List<UserCustom> findAllUserCustom();
	
	/**
	 * 查询所有用户自定义注册功能项
	 */
	public List<UserCustom> findAllUserCustom_cache();
	/**
	 * 根据用户名称查询自定义项值
	 * @param userId 用户Id
	 * @return
	 */
	public List<UserInputValue> findUserInputValueByUserName(Long userId);
	/**
	 * 保存自定义项
	 * @param userCustom 用户自定义项
	 */
	public void saveUserCustom(UserCustom userCustom);
	/**
	 * 修改自定义项值
	 * @param userCustom 自定义项
	 * @param deleteItem 删除自定义单选按钮.多选按钮.下拉列表  key参数
	 */
	public Integer updateUserCustom(UserCustom userCustom,List<String> deleteItem);
	/**
	 * 删除自定义项值
	 * @param userCustomId 自定义项Id
	 */
	public Integer deleteUserCustom(Integer userCustomId);
}
