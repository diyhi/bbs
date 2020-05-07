package cms.service.thirdParty;

import java.util.List;

import cms.bean.thirdParty.ThirdPartyLoginInterface;
import cms.service.besa.DAO;

/**
 * 第三方登录管理接口
 *
 */
public interface ThirdPartyLoginService extends DAO<ThirdPartyLoginInterface> {
	/**
	 * 根据Id查询第三方登录接口
	 * @param thirdPartyLoginInterfaceId 第三方登录接口Id
	 * @return
	 */
	public ThirdPartyLoginInterface findThirdPartyLoginInterfaceById(Integer thirdPartyLoginInterfaceId);
	
	/**
	 * 查询所有有效的第三方登录接口
	 * @return
	 */
	public List<ThirdPartyLoginInterface> findAllValidThirdPartyLoginInterface();
	/**
	 * 查询所有有效的第三方登录接口(缓存)
	 * @return
	 */
	public List<ThirdPartyLoginInterface> findAllValidThirdPartyLoginInterface_cache();
	
	/**
	 * 查询所有第三方登录接口
	 * @return
	 */
	public List<ThirdPartyLoginInterface> findAllThirdPartyLoginInterface();
	/**
	 * 保存第三方登录接口
	 * @param thirdPartyLoginInterface 第三方登录接口
	 */
	public void saveThirdPartyLoginInterface(ThirdPartyLoginInterface thirdPartyLoginInterface);
	
	/**
	 * 修改第三方登录接口
	 * @param thirdPartyLoginInterface 第三方登录接口
	 */
	public void updateThirdPartyLoginInterface(ThirdPartyLoginInterface thirdPartyLoginInterface);
	
	
	
	/**
	 * 删除第三方登录接口
	 * @param thirdPartyLoginInterfaceId 第三方登录接口Id
	 */
	public Integer deleteThirdPartyLoginInterface(Integer thirdPartyLoginInterfaceId);
	
}
