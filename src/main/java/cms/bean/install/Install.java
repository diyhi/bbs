package cms.bean.install;

import java.io.Serializable;

/**
 * 安装系统
 *
 */
public class Install implements Serializable{
	private static final long serialVersionUID = 4850162512520063350L;
	
	/** 数据库IP **/
	private String databaseIP;
	/** 数据库端口 **/
	private String databasePort;
	/** 数据库名称 **/
	private String databaseName;
	/** 数据库用户名 **/
	private String databaseUser;
	/** 数据库密码 **/
	private String databasePassword;
	/** 数据库连接地址 **/
	private String databaseURL;
	
	/** 管理员账号 **/
	private String userAccount;
	/** 管理员密码 **/
	private String userPassword;
	
	/** 选择缓存服务器  ehcache  memcache **/
	private String cacheServer = "ehcache";
	/** Memcache缓存服务器IP **/
	private String memcacheIP;
	/** Memcache缓存服务器端口 **/
	private String memcachePort;
	
	public String getDatabaseIP() {
		return databaseIP;
	}
	public void setDatabaseIP(String databaseIP) {
		this.databaseIP = databaseIP;
	}
	public String getDatabasePort() {
		return databasePort;
	}
	public void setDatabasePort(String databasePort) {
		this.databasePort = databasePort;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getDatabaseUser() {
		return databaseUser;
	}
	public void setDatabaseUser(String databaseUser) {
		this.databaseUser = databaseUser;
	}
	public String getDatabasePassword() {
		return databasePassword;
	}
	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}
	public String getCacheServer() {
		return cacheServer;
	}
	public void setCacheServer(String cacheServer) {
		this.cacheServer = cacheServer;
	}
	public String getMemcacheIP() {
		return memcacheIP;
	}
	public void setMemcacheIP(String memcacheIP) {
		this.memcacheIP = memcacheIP;
	}
	public String getMemcachePort() {
		return memcachePort;
	}
	public void setMemcachePort(String memcachePort) {
		this.memcachePort = memcachePort;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getDatabaseURL() {
		return databaseURL;
	}
	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}

}
