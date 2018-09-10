package cms.service.data;

import java.util.List;

import cms.bean.data.TableInfoObject;
import cms.bean.data.TableProperty;
import cms.service.besa.DAO;
/**
 * 数据备份/还原管理
 *
 */
public interface DataService extends DAO {
	/**
	 * 查询数据库版本
	 */
	public String findDatabaseVersion();
	/**
	 * 查询所有表信息
	 * @param formsgroupid
	 */
	public List<TableInfoObject> findTable();
	
	/**
	 * 根据表名查询字段属性
	 * @param tableName 表名称
	 * @return
	 */
	public TableProperty findFieldBytableName(String tableName);
	
	/**
	 * 根据表名分页读取数据
	 * @param param 返回值参数
	 * @param tableName 表名
	 * @param currentPage  当前页
	 * @param maxResult 每页显示记录数
	 * @param tableProperty 表属性
	 * @return
	 */
	public List<List<String>> findPageByTableName(String param,String tableName,Long currentPage,Long maxResult,TableProperty tableProperty );
	/**
	 * 查询总页数
	 * @param tableName 表名
	 * @return
	 */
	public Long findCount(String tableName);
	
	/**
	 * 还原数据
	 * @param tableProperty 数据库表属性
	 * @param path 路径
	 */
	public void restoreData(TableProperty tableProperty,String path );
	
	/**
     * 清空MySQL数据库
     * @param table
     */
    public void deleteDatabase(List<String> table);
 
}
