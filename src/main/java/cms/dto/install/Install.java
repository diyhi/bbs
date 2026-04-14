package cms.dto.install;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 安装系统
 *
 */
@Getter
@Setter
public class Install implements Serializable{
	@Serial
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
	/** 管理员账号 **/
	private String userAccount;
	/** 管理员密码 **/
	private String userPassword;
	/** 数据库连接地址 **/
	private String databaseURL;

}
