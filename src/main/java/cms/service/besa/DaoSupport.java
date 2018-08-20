package cms.service.besa;

import java.util.LinkedHashMap;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.QueryResult;


/**
 * 抽取出公用的对象实现类
 *
 */
//Transactional让这个类有默认事务行为,即默认这个类下面的方法都开事务
@Transactional
public abstract class DaoSupport<T> implements DAO<T>{
	//让Spring注入实体管理器;protected属性允许在子类进行访问
	@PersistenceContext protected EntityManager em;
	
	/**
	 * 取得实体管理器(测试)
	 * @param em
	 */
	public EntityManager EntityManagerService() {
	    return em;
	}
	
	/**
	 * 删除实体
	 * @param entityClass 实体类
	 * @param entityids 实体id
	 */
	public <T> void delete(Class<T> entityClass, Object entityid) {
		//getReference取得实体对象
	//	em.remove(em.getReference(entityClass, entityid));
		//直接调用下面的类删除
		delete(entityClass, new Object[]{entityid});
		
	}
	/**
	 * 删除实体
	 * @param entityClass 实体类
	 * @param entityids 实体id数组
	 */
	public <T> void delete(Class<T> entityClass, Object[] entityids) {
		for(Object id : entityids){
			em.remove(em.getReference(entityClass, id));
		}
		
	}

	/**
	 * 获取实体(不进行类型转换,使用泛型)
	 * @param <T>代表类型
	 * @param entityClass 实体类
	 * @param entityId 实体id
	 * @return 返回值T
	 */
	//查看内容的方法不需要开事务,用Transactional这个方法来修改事务行为;readOnly=true设置只读属性,即这个方法不能出现更改动作;propagation=Propagation.NOT_SUPPORTED设置事务传播行为,即执行这个方法不需开启事务
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public <T> T find(Class<T> entityClass, Object entityId) {

		return em.find(entityClass, entityId);
	}

	/**
	 * 保存实体
	 * @param entity 实体id
	 */
	public void save(Object entity) {
		em.persist(entity);
		
	}

	/**
	 * 更新实体
	 * @param entity 实体id
	 */
	public void update(Object entity) {
		//当实体类变成游离状态时才调用update方法,在游离状态把数据同步到数据库时调用update方法
		//merge是把游离状态的实体类数据同步回数据库
		em.merge(entity);
		
	}
	/**
	 * 获取分页数据情况一
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
	public <T> QueryResult<T> getScrollData(Class<T> entityClass, int firstindex, int maxresult,String wherejpql,Object[] queryParams, LinkedHashMap<String , String >orderby,String associate) {
		QueryResult qr = new QueryResult<T>();
		String entityname = getEntityName(entityClass);
		// o代表实体对象; entityname实体名称
		Query query = em.createQuery("select o from "+ entityname +" o "+associate+" "+(wherejpql == null || "".equals(wherejpql) ? "" : "where "+wherejpql) + buildOrderby(orderby));
		
		setQueryParams(query,queryParams);
		
		if(firstindex != -1 && maxresult != -1){//设置主要是为"获取分页数据情况五"服务的,因为它不用分页,只显示全部记录
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstindex);
			//获取多少条数据
			query.setMaxResults(maxresult);
		}
		//把查询结果设进去
		qr.setResultlist(query.getResultList());
		
		//获取总记录数
		query = em.createQuery("select count(o) from "+ entityname +" o " +associate+" " + (wherejpql == null ||"".equals(wherejpql) ? "" : "where "+wherejpql));
		setQueryParams(query,queryParams);
		//因为统计返回的是一行一列的值,所以用getSingleResult()获取一行一列形式的值
		qr.setTotalrecord((Long)query.getSingleResult());
		return qr;
	}
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
	public <T> QueryResult<T> getScrollData(Class<T> entityClass, int firstindex, int maxresult,String wherejpql,Object[] queryParams, LinkedHashMap<String , String >orderby) {
		return getScrollData(entityClass,firstindex,maxresult,wherejpql, queryParams,orderby,"");
	}
	/**
	 * 获取实体名称,由上面的方法调用
	 * @param <T>
	 * @param entityClass 实体类
	 * @return
	 */
	protected <T> String getEntityName(Class<T> entityClass){
		//默认情况下实体名称为这个类的简单名称,即实体类上的这个标志@Entity
		String entityname = entityClass.getSimpleName();
		//获取实体类Entity注解上的属性,如Entity(name="xxx")这种情况
		Entity entity = entityClass.getAnnotation(Entity.class);
		//判断实体类Entity注解上是否设置了name属性
		if(entity.name() != null && !"".equals(entity.name())){
			//把实体名称修改为它的属性值
			entityname = entity.name();
		}
		return entityname;
	}
	/**
	 * 组装order by 语句,由上面的方法调用
	 * @param orderby
	 * @return
	 * order by o.key desc, o.key2 asc
	 */
	protected String buildOrderby(LinkedHashMap<String , String >orderby){
		StringBuffer orderbyql = new StringBuffer("");
		//先判断orderby对象是否为空并且大于0
		if(orderby != null && orderby.size()>0){
			orderbyql.append(" order by ");
			//order by key desc,key2 asc
			for(String key : orderby.keySet()){
				orderbyql.append("o.")
				   		 .append(key)
				   		 .append(" ")
				   		 .append(orderby.get(key))
				   		 .append(",");
			}
			//删除最后多余的逗号
			orderbyql.deleteCharAt(orderbyql.length()-1);
		}
		return orderbyql.toString();
	}
	/**
	* 组装order by 语句(不含别名)
	* @param orderby
	* @return
	* order by o.key desc, o.key2 asc
	*/
	protected String orderby(LinkedHashMap<String , String >orderby){
		StringBuffer orderbyql = new StringBuffer("");
		//先判断orderby对象是否为空并且大于0
		if(orderby != null && orderby.size()>0){
			orderbyql.append(" order by ");
			//order by key desc,key2 asc
			for(String key : orderby.keySet()){
				orderbyql.append(key)
				   		 .append(" ")
				   		 .append(orderby.get(key))
				   		 .append(",");
			}
			//删除最后多余的逗号
			orderbyql.deleteCharAt(orderbyql.length()-1);
		}
		return orderbyql.toString();
	}
	/**
	 * 为Query对象设置查询参数,由上面的方法调用
	 * @param query
	 * @param queryParams 查询参数数组集合
	 */
	protected void setQueryParams(Query query, Object[] queryParams){
		if(queryParams != null && queryParams.length > 0){
			for(int i = 0; i < queryParams.length; i++){
				query.setParameter(i+1, queryParams[i]);
			}
		}
	}
	/**
	 * 为SQLQuery对象设置查询参数(原生Hibernate)
	 * @param sqlQuery
	 * @param queryParams 查询参数数组集合
	 
	protected void setQueryParams(SQLQuery sqlQuery, Object[] queryParams){
		if(queryParams != null && queryParams.length > 0){
			for(int i = 0; i < queryParams.length; i++){
				sqlQuery.setParameter(i, queryParams[i]);
			}
		}
	}*/
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
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public <T> QueryResult<T> getScrollData(Class<T> entityClass, int firstindex, int maxresult,String wherejpql, Object[] queryParams){
		return getScrollData(entityClass,firstindex,maxresult,wherejpql, queryParams,null,"");
	}
	
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
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public <T> QueryResult<T> getScrollData(Class<T> entityClass, int firstindex, int maxresult,LinkedHashMap<String , String >orderby){
		return getScrollData(entityClass,firstindex,maxresult,null, null,orderby,"");
	}
	
	/**
	 * 获取分页数据情况五
	 * @param <T>
	 * @param entityClass 实体类
	 * @param firstindex 开始索引
	 * @param maxresult 需要获取的记录数
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public <T> QueryResult<T> getScrollData(Class<T> entityClass, int firstindex, int maxresult){
		return getScrollData(entityClass,firstindex,maxresult,null, null,null,"");
	}
	
	/**
	 * 获取分页数据情况六
	 * @param <T>
	 * @param entityClass 实体类
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public <T> QueryResult<T> getScrollData(Class<T> entityClass){
		return getScrollData(entityClass,-1,-1,null, null,null,"");
	}
	
	
	/**
	 * 清除一级缓存的数据
	 * 将实体管理器的托管状态变为游离状态
	 */
	public void clear(){
		em.clear();
	}
}
