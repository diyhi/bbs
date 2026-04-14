package cms.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Sql文件处理
 *
 */
public class SqlFile {
    private static final Logger logger = LogManager.getLogger(SqlFile.class);
	
	/**
	 * 导入SQL文件
	 * @param connection 数据库连接
	 * @param content SQL内容
	 */
	public static void importSQL(Connection connection, Reader content) throws Exception { 
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
		runner.runScript(content);
		
		
	}
	
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
	 * @param sqlPath SQL路径
	 * @param charset 编码
	 * @return
	 * @throws IOException
	 */
	private static Reader getResourceAsReader(String sqlPath,String charset) throws IOException {
		Charset c = null;
		if(charset != null && !charset.isEmpty()){
			  c = Charset.forName(charset);
		}

		Reader reader;
        if(c == null) {	
        	reader = new InputStreamReader(new FileInputStream(sqlPath));
		}else {
		    reader = new InputStreamReader(new FileInputStream(sqlPath), c);
		}

		return reader;
	}


    /**
     * 从InputStream中读取指定表列表的所有INSERT语句。
     * @param inputStream SQL文件的输入流。
     * @param tableNames 目标表的名称列表。目标表的名称列表（例如：["frontendapi", "frontendsettings"]）。
     * @return 包含所有匹配的完整INSERT语句的列表。
     */
    public static List<String> readTableInsertStatements(InputStream inputStream, List<String> tableNames) {
        List<String> insertStatements = new ArrayList<>();
        // 预处理表名，创建匹配前缀集合，并转换为大写以进行不敏感匹配
        Set<String> prefixes = tableNames.stream()
                .map(name -> "INSERT INTO `" + name.toUpperCase() + "`")
                .collect(Collectors.toSet());

        // 如果表名在SQL中没有引号（例如：INSERT INTO TABLENAME...），需要添加额外的匹配
        Set<String> altPrefixes = tableNames.stream()
                .map(name -> "INSERT INTO " + name.toUpperCase() + " ")
                .collect(Collectors.toSet());

        // 🎯 关键修改：使用 InputStreamReader 和 UTF-8 编码读取输入流
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            StringBuilder currentStatement = new StringBuilder();
            boolean inBlock = false;

            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                String upperLine = trimmedLine.toUpperCase();// 统一转大写，用于匹配

                // 忽略空行或注释行
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("--")) {
                    continue;
                }

                // 检查行是否是目标表的 INSERT 语句的开始
                if (isTargetInsert(upperLine, prefixes, altPrefixes)) {
                    inBlock = true;
                }

                if (inBlock) {
                    currentStatement.append(trimmedLine).append(" ");

                    // 检查语句是否以分号结束（我们假设SQL语句以分号结束）
                    if (trimmedLine.endsWith(";")) {
                        insertStatements.add(currentStatement.toString().trim());

                        // 重置状态
                        currentStatement = new StringBuilder();
                        inBlock = false;
                    }
                }
            }

        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("读取SQL文件失败",e);
            }
        }

        return insertStatements;
    }


    /**
     * 检查当前行是否与任何目标表的 INSERT 语句前缀匹配
     */
    private static boolean isTargetInsert(String upperLine, Set<String> prefixes, Set<String> altPrefixes) {
        for (String prefix : prefixes) {
            if (upperLine.startsWith(prefix)) {
                return true;
            }
        }
        for (String prefix : altPrefixes) {
            if (upperLine.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
