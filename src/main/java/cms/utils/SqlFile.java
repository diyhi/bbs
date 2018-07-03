package cms.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;


/**
 * Sql文件处理
 *
 */
public class SqlFile {
		
	/**
	 * 导入SQL文件
	 * @param connection 数据库连接
	 * @param sqlPath SQL路径
	 * @param charset SQL文件编码
	 */
	public static void importSQL(Connection connection, String sqlPath,String charset) throws Exception { 
		if(connection == null){
			return;
		}
        
		SqlScriptRunner runner = new SqlScriptRunner(connection);
		//错误是否抛出异常
		runner.setThrowException(true);
		//执行错误输出
		runner.setErrorLogWriter(null);
		//执行输出
		runner.setLogWriter(null);
		//执行脚本
		runner.runScript(getResourceAsReader(sqlPath,charset));
    
		
        /**
        finally{
			if(connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("导入SQL文件",e);
			        }
				}
			}
		}**/
       
	
	}
	
	/**
	 * 获取资源
	 * @param resource
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	private static Reader getResourceAsReader(String resource,String charset) throws IOException {
		Charset c = null;
		if(charset != null && !"".equals(charset.toString())){
			  c = Charset.forName(charset);
		}

		Reader reader;
        if(c == null) {	
        	reader = new InputStreamReader(new FileInputStream(resource));
		}else {
		    reader = new InputStreamReader(new FileInputStream(resource), c);
		}
		
		/**
		if(c == null) {	
			reader = new InputStreamReader(new DefaultResourceLoader().getResource(resource).getInputStream());
		}else {
		    reader = new InputStreamReader(new DefaultResourceLoader().getResource(resource).getInputStream(), c);
		}**/
		return reader;
	}
}
