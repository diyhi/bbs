package cms;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import cms.component.install.InstallComponent;
import cms.dto.install.Install;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Strings;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeansException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import cms.utils.JasyptUtil;
import cms.utils.SHA;
import cms.utils.SqlFile;
import cms.utils.UUIDUtil;
import cms.utils.YmlUtils;

/**
 * 初始化数据
 *
 */
@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = Application.class)
public class Init {

	@Test
	public void install() {
		String userAccount = "admin";//管理员账号
		String userPassword = "1234567";//管理员密码
		
		
		String user_dir = System.getProperty("user.dir"); 
		
		//默认外部目录
		String defaultExternalDirectory = "";
		
		//论坛外部目录
		Object externalDirectory = YmlUtils.getYmlProperty("application.yml","bbs.externalDirectory");
		if(externalDirectory != null && !externalDirectory.toString().trim().isEmpty()){//如果已设置了论坛外部目录
			defaultExternalDirectory = externalDirectory.toString();
		}else{
			defaultExternalDirectory = user_dir + File.separator + "target"+ File.separator + "bbs";
		}
		
		//生成文件目录
		Path file = Paths.get(defaultExternalDirectory);
		if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {//目录不存在
			try {
				Files.createDirectories(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//生成外部文件夹
        InstallComponent installComponent = new InstallComponent();
		List<String> folderList = installComponent.folderList();
		if(folderList != null && !folderList.isEmpty()){
			for(String folderPath : folderList){
				//生成文件目录
				Path path = Paths.get(defaultExternalDirectory+File.separator+ Strings.CS.replace(folderPath, "/", File.separator));
				if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {//目录不存在
					try {
						Files.createDirectories(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		
		//写入禁止安装系统
		try {
			FileUtils.writeStringToFile(new File(defaultExternalDirectory+File.separator+"data"+File.separator+"install"+File.separator+"status.txt"), "1","utf-8",false);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
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
            conn.prepareStatement("set names utf8mb4").executeUpdate();
            
            
			String path = user_dir+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"data"+File.separator+"install"+File.separator;
		
			//导入SQL结构文件
			SqlFile.importSQL(conn,path+"structure_tables_mysql.sql","utf-8");
			
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
		Properties properties = YmlUtils.getYml("application.yml");
		//数据库连接地址
		String jdbc_url = properties.getProperty("spring.datasource.url");
		//数据库用户名
		String jdbc_user = properties.getProperty("spring.datasource.username");
		//数据库密码
		String jdbc_password = properties.getProperty("spring.datasource.password");
		//盐值 数据库密码加密所需的salt(盐)
		String salt = properties.getProperty("jasypt.encryptor.password");
		
		if(salt != null && !salt.trim().isEmpty()){
			PooledPBEStringEncryptor encryptor = JasyptUtil.config(salt);
			encryptor.setPassword(salt.trim());//加密所需的salt(盐)
			
			if(jdbc_url != null && !jdbc_url.trim().isEmpty() && PropertyValueEncryptionUtils.isEncryptedValue(jdbc_url.trim())){//判断是否为加密值 被ENC()包裹
				jdbc_url = PropertyValueEncryptionUtils.decrypt(jdbc_url, encryptor);
			}
			if(jdbc_user != null && !jdbc_user.trim().isEmpty() && PropertyValueEncryptionUtils.isEncryptedValue(jdbc_user.trim())){//判断是否为加密值 被ENC()包裹
				jdbc_user = PropertyValueEncryptionUtils.decrypt(jdbc_user, encryptor);
			}
			if(jdbc_password != null && !jdbc_password.trim().isEmpty() && PropertyValueEncryptionUtils.isEncryptedValue(jdbc_password.trim())){//判断是否为加密值 被ENC()包裹
				jdbc_password = PropertyValueEncryptionUtils.decrypt(jdbc_password, encryptor);
			}
			
		}
		
		install.setDatabaseUser(jdbc_user);//数据库用户名
		install.setDatabasePassword(jdbc_password);//数据库密码
		install.setDatabaseURL(jdbc_url);//数据库连接地址
		
		return install;
	}
	
	
	/**
	 * 创建加密的数据库参数。结果由ENC()包裹 例如 ENC(j0BA4INUYKECR2idso1VQQ==)
	 * @return
	 */
	@Test
	public void createEncryptionDatabaseParameter(){
		String datasourcePassword = "1234567";//数据库密码
		
		
		//读取数据库配置文件
		Properties properties = YmlUtils.getYml("application.yml");
		//盐值 数据库密码加密所需的salt(盐)
		String salt = properties.getProperty("jasypt.encryptor.password");
		
		if(salt != null && !salt.trim().isEmpty()){
			PooledPBEStringEncryptor encryptor = JasyptUtil.config(salt);
			encryptor.setPassword(salt.trim());//加密所需的salt(盐)
			
			
			System.out.println("数据库密码 "+encryptor.encrypt(datasourcePassword));
		}
		
	}
}
