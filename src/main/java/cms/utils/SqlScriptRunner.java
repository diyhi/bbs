package cms.utils;



import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 导入SQL工具
 * 提取自mybatis-3.4.1 org.apache.ibatis.jdbc.ScriptRunner
 * ScriptRunner runner = new ScriptRunner(conn);
 */
public class SqlScriptRunner {
	private static final Logger logger = LogManager.getLogger(SqlScriptRunner.class);
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	private static final String DEFAULT_DELIMITER = ";";

	private Connection connection;
	//遇到错误是否停止执行
	private boolean stopOnError;
	//遇到错误停止执行是否抛出异常
	private boolean throwWarning;
	
	//错误是否抛出异常
	private boolean throwException = false;
	
	//自动提交事务
	private boolean autoCommit;
	private boolean sendFullScript;
	private boolean removeCRs;
	private boolean escapeProcessing = true;

	private PrintWriter logWriter = new PrintWriter(System.out);
	private PrintWriter errorLogWriter = new PrintWriter(System.err);

	private String delimiter = DEFAULT_DELIMITER;
	private boolean fullLineDelimiter = false;

	public SqlScriptRunner(Connection connection) {
	    this.connection = connection;
	}

	public void setStopOnError(boolean stopOnError) {
	    this.stopOnError = stopOnError;
	}

	public void setThrowWarning(boolean throwWarning) {
	    this.throwWarning = throwWarning;
	}
	public void setThrowException(boolean throwException) {
	    this.throwException = throwException;
	}
	public void setAutoCommit(boolean autoCommit) {
	    this.autoCommit = autoCommit;
	}
	
	public void setSendFullScript(boolean sendFullScript) {
	    this.sendFullScript = sendFullScript;
	}
	
	public void setRemoveCRs(boolean removeCRs) {
	    this.removeCRs = removeCRs;
	}

	/**
	* @since 3.1.1
	*/
	public void setEscapeProcessing(boolean escapeProcessing) {
	    this.escapeProcessing = escapeProcessing;
	}
	
	public void setLogWriter(PrintWriter logWriter) {
	    this.logWriter = logWriter;
	}
	
	public void setErrorLogWriter(PrintWriter errorLogWriter) {
	    this.errorLogWriter = errorLogWriter;
	}
	
	public void setDelimiter(String delimiter) {
	    this.delimiter = delimiter;
	}
	
	public void setFullLineDelimiter(boolean fullLineDelimiter) {
	    this.fullLineDelimiter = fullLineDelimiter;
	}

	public void runScript(Reader reader) {
	    setAutoCommit();
	
	    try {
	    	if (sendFullScript) {
	    		executeFullScript(reader);//执行完整脚本
	    	} else {
	    		executeLineByLine(reader);//逐行执行
	    	}
	    } finally {
	    	rollbackConnection();
	    }
	}
	/**
	 * 执行完整脚本
	 * @param reader
	 */
	private void executeFullScript(Reader reader) {
	    StringBuilder script = new StringBuilder();
	    try {
	    	BufferedReader lineReader = new BufferedReader(reader);
	    	String line;
	    	while ((line = lineReader.readLine()) != null) {
		        script.append(line);
		        script.append(LINE_SEPARATOR);
	    	}
	    	String command = script.toString();
	    	println(command);
	    	executeStatement(command);
	    	commitConnection();
	    } catch (Exception e) {
	    	String message = "执行完整脚本错误: " + script + ".  原因: " + e;
	    	printlnError(message);
	    	if (logger.isErrorEnabled()) {
		      logger.error("执行完整脚本错误: " + script ,e);
	    	}
	    	throw new RuntimeException(message, e);
	    }
	}
	/**
	 * 逐行执行
	 * @param reader
	 */
	private void executeLineByLine(Reader reader) {
	    StringBuilder command = new StringBuilder();
	    try {
	    	BufferedReader lineReader = new BufferedReader(reader);
	    	String line;
	    	while ((line = lineReader.readLine()) != null) {
	        command = handleLine(command, line);
	    	}
	    	commitConnection();
	    	checkForMissingLineTerminator(command);
	    } catch (Exception e) {
	    	String message = "逐行执行错误: " + command + ".  原因: " + e;
	    	printlnError(message);
	    	if (logger.isErrorEnabled()) {
			    logger.error("逐行执行错误: " + command ,e);
	    	}
	    	throw new RuntimeException(message, e);
	    }
	}
	/**
	 * 关闭连接
	 */
	public void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
	    	if (logger.isErrorEnabled()) {
			    logger.error("关闭连接",e);
	    	}
      // ignore
    	}
	}
	/**
	 * 设置自动提交
	 */
	private void setAutoCommit() {
		try {
	    	if (autoCommit != connection.getAutoCommit()) {
	    		connection.setAutoCommit(autoCommit);
	    	}
	    } catch (Throwable t) {
	    	if (logger.isErrorEnabled()) {
			    logger.error("设置自动提交",t);
	    	}
	    	throw new RuntimeException("无法将AutoCommit设置为 " + autoCommit + ". 原因: " + t, t);
	    }
	}
	/**
	 * 提交连接
	 */
	private void commitConnection() {
		try {
		  if (!connection.getAutoCommit()) {
			  connection.commit();
		  }
		} catch (Throwable t) {
			if (logger.isErrorEnabled()) {
			    logger.error("提交连接",t);
	    	}
			throw new RuntimeException("无法提交事务. 原因: " + t, t);
		}
	}
	/**
	 * 回滚连接
	 */
	private void rollbackConnection() {
		try {
			if (!connection.getAutoCommit()) {
				connection.rollback();
			}
		} catch (Throwable t) {
			if (logger.isErrorEnabled()) {
			    logger.error("回滚连接",t);
	    	}
      // ignore
		}
	}
	/**
	 * 检查丢失行终结符
	 * @param command
	 */
	private void checkForMissingLineTerminator(StringBuilder command) {
	    if (command != null && command.toString().trim().length() > 0) {
	    	throw new RuntimeException("Line missing end-of-line terminator (" + delimiter + ") => " + command);
	    }
	}

	private StringBuilder handleLine(StringBuilder command, String line) throws SQLException, UnsupportedEncodingException {
		String trimmedLine = line.trim();
	    if (lineIsComment(trimmedLine)) {
	        final String cleanedString = trimmedLine.substring(2).trim().replaceFirst("//", "");
	        if(cleanedString.toUpperCase().startsWith("@DELIMITER")) {
	            delimiter = cleanedString.substring(11,12);
	            return command;
	        }
	        println(trimmedLine);
	    } else if (commandReadyToExecute(trimmedLine)) {
	    	command.append(line.substring(0, line.lastIndexOf(delimiter)));
	    	command.append(LINE_SEPARATOR);
	    	println(command);
	    	executeStatement(command.toString());
	    	command.setLength(0);
	    } else if (trimmedLine.length() > 0) {
	      command.append(line);
	      command.append(LINE_SEPARATOR);
	    }
	    return command;
	}

	private boolean lineIsComment(String trimmedLine) {
		return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
	}

	private boolean commandReadyToExecute(String trimmedLine) {
	    // issue #561 remove anything after the delimiter
	    return !fullLineDelimiter && trimmedLine.contains(delimiter) || fullLineDelimiter && trimmedLine.equals(delimiter);
	}
	/**
	 * 执行语句
	 * @param command
	 * @throws SQLException
	 */
	private void executeStatement(String command) throws SQLException {
		boolean hasResults = false;
		Statement statement = connection.createStatement();
		statement.setEscapeProcessing(escapeProcessing);
		String sql = command;
		if (removeCRs) {
			sql = sql.replaceAll("\r\n", "\n");
		}
		if (stopOnError) {
			hasResults = statement.execute(sql);
	    	if (throwWarning) {
	    // In Oracle, CRATE PROCEDURE, FUNCTION, etc. returns warning
	    		// instead of throwing exception if there is compilation error.
	    		SQLWarning warning = statement.getWarnings();
	    		if (warning != null) {
	    			throw warning;
	    		}
	    	}
	    } else {
	    	try {
	    		hasResults = statement.execute(sql);
	    	} catch (SQLException e) {
	    		String message = "执行语句错误: " + command + ".  原因: " + e;
	    		printlnError(message);
	    	
	    		if (throwException) {
	    			SQLWarning warning = statement.getWarnings();
		    		if (warning != null) {
		    			try {
		    				statement.close();
		    			} catch (Exception e2) {
		    				if (logger.isErrorEnabled()) {
		    				    logger.error("当错误抛出异常时执行语句关闭Statement错误",e2);
		    		    	}
		    				// Ignore to workaround a bug in some connection pools
		    			}
		    			throw e;
		    		}
		    		
	    		}
	    		
	    	}
	    }
		printResults(statement, hasResults);
		try {
			statement.close();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
			    logger.error("执行语句关闭Statement错误",e);
	    	}
			// Ignore to workaround a bug in some connection pools
		}
	}
	/**
	 * 打印结果
	 * @param statement
	 * @param hasResults
	 */
	private void printResults(Statement statement, boolean hasResults) {
		try {
			if (hasResults) {
				ResultSet rs = statement.getResultSet();
				if (rs != null) {
					ResultSetMetaData md = rs.getMetaData();
					int cols = md.getColumnCount();
					for (int i = 0; i < cols; i++) {
						String name = md.getColumnLabel(i + 1);
						print(name + "\t");
					}
					println("");
					while (rs.next()) {
						for (int i = 0; i < cols; i++) {
							String value = rs.getString(i + 1);
							print(value + "\t");
						}
						println("");
					}
				}
			}
		} catch (SQLException e) {
			printlnError("打印结果出错: " + e.getMessage());
			if (logger.isErrorEnabled()) {
			    logger.error("打印结果出错",e);
	    	}
		}
	}

	private void print(Object o) {
	    if (logWriter != null) {
	    	logWriter.print(o);
	    	logWriter.flush();
	    }
	}

	private void println(Object o) {
	    if (logWriter != null) {
	    	logWriter.println(o);
	    	logWriter.flush();
	    }
	}

	private void printlnError(Object o) {
	    if (errorLogWriter != null) {
	    	errorLogWriter.println(o);
	    	errorLogWriter.flush();
	    }    
	}

}
