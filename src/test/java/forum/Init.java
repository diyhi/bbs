package forum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.beans.BeansException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import cms.bean.install.Install;
import cms.utils.CommentedProperties;
import cms.utils.DruidTool;
import cms.utils.SHA;
import cms.utils.SqlFile;
import cms.utils.UUIDUtil;

/**
 * 安装数据库
 *
 */
public class Init {

	public static void main(String[] args) {
		String userAccount = "admin";//管理员账号
		String userPassword = "1234567";//管理员密码
		
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			
		
			//通过查询运行设置字符集的命令
			rs = conn.prepareStatement("show variables like 'char%'").executeQuery();
			
			String character_set_database = "";
			
			while(rs.next()){ 
				if("character_set_database".equalsIgnoreCase(rs.getString(1))){
					character_set_database = rs.getString(2);
				}	
			}
			if(!"utf8mb4".equalsIgnoreCase(character_set_database)){
				 throw new RuntimeException("数据库必须为utf8mb4编码");
			}

			//通过查询运行设置字符集的命令
            conn.prepareStatement("set names utf8mb4").executeQuery();
            String user_dir = System.getProperty("user.dir"); 
			String path = user_dir+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+File.separator+"WEB-INF"+File.separator+"data"+File.separator+"install"+File.separator;
		
			//导入SQL结构文件
		//	SqlFile.importSQL(conn,path+"structure_tables_mysql.sql","utf-8");
			
			//导入SQL数据文件
			SqlFile.importSQL(conn,path+"data_tables_mysql.sql","utf-8");
			
			//插入管理员数据
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();//BCrypt密码算法,Bcrypt加密最长为72个字符，超过72字符的部分被截断丢弃了
			
			// 密码通过盐值加密以备存储入数据库
			String newPassword = passwordEncoder.encode(SHA.sha256Hex(userPassword.trim()));
			
			String sql = "INSERT INTO `sysusers` (`userId`,`enabled`,`fullName`,`issys`,`securityDigest`,`userAccount`,`userDesc`,`userDuty`,`userPassword`) VALUES ('"+UUIDUtil.getUUID32()+"',b'1','"+userAccount.trim()+"',b'1','"+UUIDUtil.getUUID32()+"','"+userAccount.trim()+"',NULL,'管理员','"+newPassword+"')";
			
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			conn.commit();  
			
			System.out.println("导入数据完成");
			
		} catch (CannotGetJdbcConnectionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BeansException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	/**
	 * 获取数据库连接
	 * @return
	 */
	private static Connection getConnection() throws Exception{
		Install install = getDatabaseParameter();
	
		//linux下5.7必须加这句,不然会报错java.sql.SQLException: No suitable driver found for
		DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());//注册驱动
		
		return DriverManager.getConnection(install.getDatabaseURL().trim(), install.getDatabaseUser().trim(), install.getDatabasePassword().trim());	
		
	}
	
	
	/**
	 * 获取数据库参数
	 * @return
	 */
	private static Install getDatabaseParameter(){
		Install install = new Install();
		
		//读取数据库配置文件
		InputStream in = null;
		
		CommentedProperties database_props = new CommentedProperties();
		//数据库连接地址
		String jdbc_url = null;
		//数据库用户名
		String jdbc_user = null;
		//数据库密码密文
		String jdbc_password_ciphertext = null;
		//数据库密码原文
		String jdbc_password_original = null;
		//公钥参数组
		String jdbc_publickey = null;
		try {
			String user_dir = System.getProperty("user.dir"); 
			String path = user_dir + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator +"druid.properties";

			
			in = new FileInputStream(new File(path));
			
			database_props.load(in,"utf-8");
			
			
			jdbc_url = database_props.getProperty("jdbc_url");
			jdbc_user = database_props.getProperty("jdbc_user");
			jdbc_password_ciphertext = database_props.getProperty("jdbc_password");
			jdbc_publickey = database_props.getProperty("jdbc_publickey");
			
			if(jdbc_password_ciphertext != null && !"".equals(jdbc_password_ciphertext.trim())){
				if(jdbc_publickey != null && !"".equals(jdbc_publickey.trim())){
					
					String publickey = null;
					String[] jdbc_publickey_group = jdbc_publickey.split(";");
					if(jdbc_publickey_group != null && jdbc_publickey_group.length >0){
						for(String str : jdbc_publickey_group){
							if(str.startsWith("config.decrypt.key")){
								publickey = str.substring(19, str.length());
							}
						}
					}
					
					//解密
					jdbc_password_original = DruidTool.decryptString(publickey, jdbc_password_ciphertext);
					
				}else{
					jdbc_password_original = jdbc_password_ciphertext;
				}
			}else{
				jdbc_password_original = jdbc_password_ciphertext;
			}
			
			
			install.setDatabaseUser(jdbc_user);//数据库用户名
			install.setDatabasePassword(jdbc_password_original);//数据库密码
			install.setDatabaseURL(jdbc_url);//数据库连接地址
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		return install;
	}

}
