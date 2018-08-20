package cms.service.besa;

import java.util.LinkedHashMap;

import javax.persistence.EntityManager;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.QueryResult;

/**
 * 抽取出公用的对象
 *
 */
public interface DAO<T> {
	/**
	 * 取得实体管理器(测试)
	 * @param em
	 */
	public EntityManager EntityManagerService();
	/**
	 * 保存实体
	 * @param entity 实体id
	 */
	public void save(Object entity);

	/**
	 * 更新实体
	 * @param entity 实体id
	 */
	public void update(Object entity);
	
	/**
	 * 删除实体
	 * @param entityClass 实体类
	 * @param entityids 实体id
	 */
	public <T> void delete(Class<T> entityClass,Object entityid);
	/**
	 * 删除实体
	 * @param entityClass 实体类
	 * @param entityids 实体id数组
	 */
	public <T> void delete(Class<T> entityClass,Object[] entityids);
	
	/**
	 * 获取实体(不进行类型转换,使用泛型)
	 * @param <T>代表类型
	 * @param entityClass 实体类
	 * @param entityId 实体id
	 * @return 返回值T
	 */
	public <T> T find(Class<T> entityClass, Object entityId);
	
	/**
	 * 获取分页数据 情况一
	 * @param <T>
	 * @param entityClass 实体类
	 * @param firstindex 开始索引
	 * @param maxresult 需要获取的记录数
	 * @param wherejpql 查询条件语句
	 * @param queryParams 对象数组,用来接收查询参数
	 * @param orderby 排序参数
	 * @param associate 关联(用来加上表名后的参数，如隐含关联)
	 * @return
	 * queryParams接收参数形式:o.property=? and o.xxx like ?2
	 * * LinkedHashMap<实体属性 , 升序或降序 > 主要是构建排序语句如:order by key1 desc,key2,asc
	 * 使用LinkedHashMap而不用HashMap,是因为HashMap这个方法内部会重排序,而现在需要的是排序顺序按照添加元素顺序来排,如先key1 desc后key2,asc
	 * 因为不需要开事务readOnly=true设置只读属性;propagation=Propagation.NOT_SUPPORTED不开事务
	 */
	public <T> QueryResult<T> getScrollData(Class<T> entityClass, int firstindex, int maxresult,String wherejpql,Object[] queryParams, LinkedHashMap<String , String >orderby,String associate);
	/**
	 * 获取分页数据情况二
	 * @param <T>
	 * @param entityClass 实体类
	 * @param firstindex 开始索引
	 * @param maxresult 需要获取的记录数
	 * @param wherejpql 查询条件语句
	 * @param queryParams 对象数组,用来接收查询参数
	 * @param orderby 排序参数
	 * @param associate 关联(用来加上表名后的参数，如隐含关联)
	 * @return
	 * queryParams接收参数形式:o.property=? and o.xxx like ?2
	 * * LinkedHashMap<实体属性 , 升序或降序 > 主要是构建排序语句如:order by key1 desc,key2,asc
	 * 使用LinkedHashMap而不用HashMap,是因为HashMap这个方法内部会重排序,而现在需要的是排序顺序按照添加元素顺序来排,如先key1 desc后key2,asc
	 * 因为不需要开事务readOnly=true设置只读属性;propagation=Propagation.NOT_SUPPORTED不开事务
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public <T> QueryResult<T> getScrollData(Class<T> entityClass, int firstindex, int maxresult,String wherejpql,Object[] queryParams, LinkedHashMap<String , String >orderby);
	/**
	 * 获取分页数据情况三
	 * @param <T>
	 * @param entityClass 实体类
	 * @param firstindex 开始索引
	 * @param maxresult 需要获取的记录数
	 * @param wherejpql 查询条件语句
	 * @param queryParams 对象数组,用来接收查询参数
	 * @param orderby 排序参数
	 * @return
	 */
	public <T> QueryResult<T> getScrollData(Class<T> entityClass, int firstindex, int maxresult,String wherejpql, Object[] queryParams);
	
	/**
	 * 获取分页数据情况四
	 * @param <T>
	 * @param entityClass 实体类
	 * @param firstindex 开始索引
	 * @param maxresult 需要获取的记录数
	 * @param orderby 排序参数
	 * @return
	 * LinkedHashMap<实体属性 , 升序或降序 > 主要是构建排序语句如:order by key1 desc,key2,asc
	 * 使用LinkedHashMap而不用HashMap,是因为HashMap这个方法内部会重排序,而现在需要的是排序顺序按照添加元素顺序来排,如先key1 desc后key2,asc
	 */
	public <T> QueryResult<T> getScrollData(Class<T> entityClass, int firstindex, int maxresult,LinkedHashMap<String , String >orderby);
	
	/**
	 * 获取分页数据情况五
	 * @param <T>
	 * @param entityClass 实体类
	 * @param firstindex 开始索引
	 * @param maxresult 需要获取的记录数
	 * @return
	 */
	public <T> QueryResult<T> getScrollData(Class<T> entityClass, int firstindex, int maxresult);
	
	/**
	 * 获取分页数据情况六
	 * @param <T>
	 * @param entityClass 实体类
	 * @return
	 */
	public <T> QueryResult<T> getScrollData(Class<T> entityClass);
	
	/**
	 * 清除一级缓存的数据
	 * 将实体管理器的托管状态变为游离状态
	 */
	public void clear();
}
