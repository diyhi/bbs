package cms.web.filter;

import java.io.Serializable;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * 使用命名策略，为映射到的数据库起别名，注意到linux环境下，数据库表名区分大小写
 *
 */
public class MyPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl implements PhysicalNamingStrategy,Serializable  {
	private static final long serialVersionUID = 8254304940510564348L;
	
	
	public static final MyPhysicalNamingStrategy INSTANCE = new MyPhysicalNamingStrategy();
	
	/**
     * 自定义 entity 名称与 table 名称的映射关系
     * @param name
     * @param context
     * @return
     */
	@Override
    public Identifier toPhysicalTableName(Identifier name,JdbcEnvironment context) {
		//数据库小写表名
        return new Identifier(name.getText().toLowerCase(), name.isQuoted());
    }
}
