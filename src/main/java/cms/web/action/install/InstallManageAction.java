package cms.web.action.install;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import cms.bean.install.Install;
import cms.utils.CommentedProperties;
import cms.utils.DruidTool;
import cms.utils.FileUtil;
import cms.utils.PathUtil;
import cms.utils.SHA;
import cms.utils.SqlFile;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.utils.WebUtil;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;


@WebServlet("/install")
public class InstallManageAction extends HttpServlet{
	private static final long serialVersionUID = 4687049294910496944L;
	
	private static final Logger logger = LogManager.getLogger(InstallManageAction.class);
	 

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!this.installSystem()){
			return;
		}
		
		String path = request.getContextPath();
		request.setAttribute("config_url", request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/");
		
		Install install = new Install();
		request.setAttribute("install", install);
		request.getRequestDispatcher("/WEB-INF/jsp/install/install.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,String> error = new HashMap<String,String>();
		String databaseLink = "";
		
		Install install = new Install();
		if(!this.installSystem()){
			error.put("installSystem", "不允许安装系统");
		}else{
			String databaseIP = request.getParameter("databaseIP");
			String databasePort = request.getParameter("databasePort");
			String databaseName = request.getParameter("databaseName");
			String databaseUser = request.getParameter("databaseUser");
			String databasePassword = request.getParameter("databasePassword");
			String userAccount = request.getParameter("userAccount");
			String userPassword = request.getParameter("userPassword");
			
			String cacheServer = request.getParameter("cacheServer");
			String memcacheIP = request.getParameter("memcacheIP");
			String memcachePort = request.getParameter("memcachePort");
			
			
			
			if(cacheServer != null && cacheServer.equals("memcache")){
				install.setCacheServer("memcache");
			}
			install.setDatabaseIP(databaseIP);
			install.setDatabasePort(databasePort);
			install.setDatabaseName(databaseName);
			install.setDatabaseUser(databaseUser);
			install.setDatabasePassword(databasePassword);
			install.setUserAccount(userAccount);
			install.setUserPassword(userPassword);
			install.setMemcacheIP(memcacheIP);
			install.setMemcachePort(memcachePort);
			
			if(install.getDatabaseIP() == null || "".equals(install.getDatabaseIP().trim())){
				error.put("databaseIP", "数据库IP不能为空");
			}
			if(install.getDatabasePort() != null && !"".equals(install.getDatabasePort().trim())){
	
				boolean verification = Verification.isPositiveIntegerZero(install.getDatabasePort().trim());
				if(!verification){
					error.put("databasePort", "请填写数字类型");	
				}
			}else{
				error.put("databasePort", "数据库端口不能为空");
			}
			if(install.getDatabaseName() == null || "".equals(install.getDatabaseName().trim())){
				error.put("databaseName", "数据库名称不能为空");
			}
			if(install.getDatabaseUser() == null || "".equals(install.getDatabaseUser().trim())){
				error.put("databaseUser", "数据库用户名不能为空");
			}
			if(install.getDatabasePassword() == null || "".equals(install.getDatabasePassword().trim())){
				error.put("databasePassword", "数据库密码不能为空");
			}
			if(install.getUserAccount() == null || "".equals(install.getUserAccount().trim())){
				error.put("userAccount", "轻论坛管理员账号不能为空");
			}else{
				boolean verification = Verification.isNumericLettersUnderscore(install.getUserAccount().trim());
				if(!verification){
					error.put("userAccount", "只能输入由数字、26个英文字母或者下划线组成");	
				}
			}
			if(install.getUserPassword() == null || "".equals(install.getUserPassword().trim())){
				error.put("userPassword", "轻论坛管理员密码不能为空");
			}
			
			if(install.getCacheServer().equals("memcache")){
				if(install.getMemcacheIP() == null || "".equals(install.getMemcacheIP().trim())){
					error.put("memcacheIP", "缓存服务器IP不能为空");
				}
				if(install.getMemcachePort() != null && !"".equals(install.getMemcachePort().trim())){
					
					boolean verification = Verification.isPositiveIntegerZero(install.getMemcachePort().trim());;
					if(!verification){
						error.put("memcachePort", "请填写数字类型");	
					}
				}else{
					error.put("memcachePort", "缓存服务器端口不能为空");
				}
			}
			
			
			//校验数据库连接
			if(error.size() ==0){
				Connection conn = null;
				ResultSet rs = null;
				ResultSet rs2 = null;
				try {
					databaseLink = "jdbc:mysql://"+install.getDatabaseIP().trim()+":"+install.getDatabasePort().trim()+"/"+install.getDatabaseName().trim()+"?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&rewriteBatchedStatements=true";
					//linux下5.7必须加这句,不然会报错java.sql.SQLException: No suitable driver found for
					DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());//注册驱动
				
					conn = DriverManager.getConnection(databaseLink, install.getDatabaseUser().trim(), install.getDatabasePassword().trim());	
					
					//获取所有表
					rs = conn.getMetaData().getTables(conn.getCatalog(), "%", "%", new String[]{"TABLE"});
					int count = 0;
					while(rs.next()){ 
						count++;
						
					} 
				
					//通过查询运行设置字符集的命令
					rs2 = conn.prepareStatement("show variables like 'char%'").executeQuery();
					
					String character_set_database = "";
					
					while(rs2.next()){ 
						if("character_set_database".equalsIgnoreCase(rs2.getString(1))){
							character_set_database = rs2.getString(2);
						}	
					}
					if(!"utf8mb4".equalsIgnoreCase(character_set_database)){
						error.put("databaseName", "数据库必须为utf8mb4编码");
					}
					
					
					//如果存在数据库表则不允许安装
					if(count >0){
						error.put("installSystem", "数据库表已存在");
					}
	
				}catch (Exception e) {
					error.put("databaseLink", "数据库连接错误");
					if (logger.isErrorEnabled()) {
    		            logger.error("校验数据库连接错误",e);
    		        }
				//	e.printStackTrace();
				}finally {
					if(rs != null){
						try {
							rs.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
							error.put("databaseLink", "数据库关闭查询所有表错误");
						}
					}
					if(rs2 != null){
						try {
							rs2.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
							error.put("databaseLink", "数据库关闭查询字符集编码错误");
						}
					}
					if(conn != null){
						try {
							conn.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
							error.put("databaseLink", "数据库关闭错误");
						}
					}
				}
				
			}
			
			if(error.size() ==0){
				if(install.getCacheServer().equals("memcache")){
					//校验Memcache缓存连接
					MemcachedClientBuilder builder = new XMemcachedClientBuilder(  
					        AddrUtil.getAddresses(install.getMemcacheIP().trim()+":"+install.getMemcachePort().trim()));  
					//宕机报警  
			        builder.setFailureMode(true);  
			        //使用二进制文件  
					builder.setCommandFactory( new BinaryCommandFactory());  
					MemcachedClient client = null;
					try {
						client = builder.build();
						
						client.set("hello", 10, "Hello,xmemcached");  
						
					} catch (Exception e) {
						error.put("cacheServer", "缓存服务器连接错误");
						// TODO Auto-generated catch block
					//	e.printStackTrace();
					}finally {
						if(client != null){
							client.shutdown();//关闭
						}
					}
				}
			}
			
			

			
			List<String> fileList = new ArrayList<String>();//待检测文件
			fileList.add("WEB-INF"+File.separator+"data"+File.separator+"install"+File.separator+"status.txt");
			fileList.add("WEB-INF"+File.separator+"classes"+File.separator+"druid.properties");
			fileList.add("WEB-INF"+File.separator+"classes"+File.separator+"memcache.properties");
			fileList.add("WEB-INF"+File.separator+"data"+File.separator+"install"+File.separator+"web.xml");
			fileList.add("WEB-INF"+File.separator+"web.xml");
			
			for(String f : fileList){
				File file = new File(PathUtil.path()+File.separator+f);
				//检测文件权限
				if(!file.canRead() || !file.canWrite()){
					error.put("filePermissions", "文件权限不足 "+f +"  "+(file.canRead() == false ? "[不可读]":"")+(file.canWrite() == false ? "[不可写]":""));
				}
			}
		}
		
		if(error.size() ==0){
			
			String cacheName = "ehcache";
			if(install.getCacheServer().equals("memcache")){
				cacheName = "memcache";
    			//写入Memcache缓存配置文件
        		org.springframework.core.io.Resource memcache_resource = new ClassPathResource("/memcache.properties");//读取配置文件
        		CommentedProperties memcache_props = new CommentedProperties();
        		
        		BufferedWriter memcache_bw = null;
        		try {
        			memcache_props.load(memcache_resource.getInputStream(),"utf-8");

        			memcache_props.setProperty("memcache.server_1", install.getMemcacheIP().trim());
        			memcache_props.setProperty("memcache.port_1", install.getMemcachePort().trim());
    				
        			memcache_bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(memcache_resource.getFile()),"UTF-8"));
    				
    				
    				memcache_props.store(memcache_bw);
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    			//	e.printStackTrace();
    				error.put("installSystem", "读取memcache.properties配置文件错误");
    				if (logger.isErrorEnabled()) {
    		            logger.error("安装轻论坛系统读取memcache.properties配置文件错误",e);
    		        }
    			}finally {
    				if(memcache_bw != null){
    					memcache_bw.close();
    				}
    				
				}
    		}
		
			//写入数据库配置文件
    		org.springframework.core.io.Resource database_resource = new ClassPathResource("/druid.properties");//读取配置文件
    		CommentedProperties database_props = new CommentedProperties();
    		
    		BufferedWriter bw = null;
    		try {
    			//私钥
    			String privateKey = "";
    			//公钥
    			String publicKey = "";

    			Map<String, String> rsaKey = DruidTool.generateRsaKey();
    			if(rsaKey != null && rsaKey.size() >0){
    				for(Map.Entry<String, String> entry : rsaKey.entrySet()){
    					if("privateKey".equals(entry.getKey())){
    						privateKey = entry.getValue();
    					}else if("publicKey".equals(entry.getKey())){
    						publicKey = entry.getValue();
    					}
    				}
    			}

    			String encryptPassword = "";
    			try {
    				encryptPassword = DruidTool.encryptString(privateKey, install.getDatabasePassword().trim());
				} catch (Exception e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					error.put("installSystem", "加密数据库密码错误");
					if (logger.isErrorEnabled()) {
			            logger.error("安装轻论坛系统加密数据库密码错误",e);
			        }
				}

    			database_props.load(database_resource.getInputStream(),"utf-8");
    			database_props.setProperty("cacheName", cacheName);
    			database_props.setProperty("jdbc_driver", "com.mysql.cj.jdbc.Driver");
    			database_props.setProperty("jdbc_url", databaseLink);
    			database_props.setProperty("jdbc_user", install.getDatabaseUser().trim());
				database_props.setProperty("jdbc_password", encryptPassword);
				database_props.setProperty("jdbc_publickey", "config.decrypt=true;config.decrypt.key="+publicKey);
	
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(database_resource.getFile()),"UTF-8"));
				database_props.store(bw);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				error.put("installSystem", "写入druid.properties配置文件错误");
				if (logger.isErrorEnabled()) {
		            logger.error("安装轻论坛系统写入druid.properties配置文件错误",e);
		        }
			}finally {
				if(bw != null){
					bw.close();
				}
				
			}
		}
		
		
		if(error.size() ==0){
			Connection conn = null;
    		Statement stmt = null;
			try {
				databaseLink = "jdbc:mysql://"+install.getDatabaseIP().trim()+":"+install.getDatabasePort().trim()+"/"+install.getDatabaseName().trim()+"?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&rewriteBatchedStatements=true";
				//linux下5.7必须加这句,不然会报错java.sql.SQLException: No suitable driver found for
				DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());//注册驱动
				
				conn = DriverManager.getConnection(databaseLink, install.getDatabaseUser().trim(), install.getDatabasePassword().trim());	
				
				//通过查询运行设置字符集的命令
                conn.prepareStatement("set names utf8mb4").executeQuery();
		
				String path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator+"install"+File.separator;
				//导入SQL结构文件
				SqlFile.importSQL(conn,path+"structure_tables_mysql.sql","utf-8");
					
				//导入SQL数据文件
				SqlFile.importSQL(conn,path+"data_tables_mysql.sql","utf-8");
				
				//插入管理员数据
				//INSERT INTO `sysusers` (`userId`,`enabled`,`fullName`,`issys`,`userAccount`,`userDesc`,`userDuty`,`userPassword`) VALUES ('0e2abc06-a71a-40ed-b449-a55c1a5b6a68',b'1','fdsf',b'0','fdsfds',NULL,NULL,'5cf74b96bcc721bf1a97674550dff37e225d72766c3d5969e8638f57d8d4809e7e7b7b87f796582c')
				PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();//BCrypt密码算法,Bcrypt加密最长为72个字符，超过72字符的部分被截断丢弃了
				
				// 密码通过盐值加密以备存储入数据库
				String newPassword = passwordEncoder.encode(SHA.sha256Hex(install.getUserPassword().trim()));
				
				String sql = "INSERT INTO `sysusers` (`userId`,`enabled`,`fullName`,`issys`,`securityDigest`,`userAccount`,`userDesc`,`userDuty`,`userPassword`) VALUES ('"+UUIDUtil.getUUID32()+"',b'1','"+install.getUserAccount().trim()+"',b'1','"+UUIDUtil.getUUID32()+"','"+install.getUserAccount().trim()+"',NULL,'管理员','"+newPassword+"')";
				
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				conn.commit();  
				
				
			}catch (RuntimeException e) {
				error.put("installSystem", "导入数据库错误");
				
			}catch (Exception e) {
				error.put("installSystem", "安装轻论坛系统数据库连接数据库错误");
				if (logger.isErrorEnabled()) {
		            logger.error("安装轻论坛系统数据库连接数据库错误",e);
		        }
		//		e.printStackTrace();
			}finally {
				if(stmt != null){
					try {
						stmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
					//	e.printStackTrace();
						if (logger.isErrorEnabled()) {
				            logger.error("安装轻论坛系统数据库关闭Statement错误",e);
				        }
					}
				}
				
				
				if(conn != null){
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
					//	e.printStackTrace();
						if (logger.isErrorEnabled()) {
				            logger.error("安装轻论坛系统数据库关闭错误",e);
				        }
					}
				}
			}
		}
		
		
		
		LocalFileManage localFileManage = new LocalFileManage();
		if(error.size() ==0){
    		//复制文件
			Map<String,String> copyFileMap = new HashMap<String,String>();//key:旧文件路径  value:新文件路径
			copyFileMap.put("WEB-INF"+File.separator+"data"+File.separator+"install"+File.separator+"web.xml", "WEB-INF");
			for (Map.Entry<String,String> entry : copyFileMap.entrySet()) {  
				localFileManage.copyFile(entry.getKey(), entry.getValue());
			}
		}
    	
		if(error.size() ==0){
			//写入禁止安装系统
			FileUtil.writeStringToFile("WEB-INF"+File.separator+"data"+File.separator+"install"+File.separator+"status.txt","1","utf-8",false);
		}
		
		
		
		if(error != null && error.size() >0){
			request.setAttribute("error", error);
			request.setAttribute("install", install);
			request.getRequestDispatcher("/WEB-INF/jsp/install/install.jsp").forward(request, response);
		}else{
			WebUtil.writeToWeb("安装轻论坛系统成功，请重启服务器自动初始化数据", "html", response);
		}
		
	}
	

	/**
	 * 是否允许安装系统
	 * @return
	 */
	private boolean installSystem(){
		
		//读取版本文件
    	String version = FileUtil.readFileToString("WEB-INF"+File.separator+"data"+File.separator+"install"+File.separator+"status.txt","UTF-8");
    	if(version.equals("0")){
    		return true;
    	}else{
    		return false;
    	}
	}
}

