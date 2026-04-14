package cms.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;


import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * Spring 获取Connection 
 *
 */
public class SpringConnection {
	public static Connection getConnection()throws SQLException{  
		return DataSourceUtils.getConnection((DataSource)SpringConfigTool.getContext().getBean("dataSource"));
	}
}
