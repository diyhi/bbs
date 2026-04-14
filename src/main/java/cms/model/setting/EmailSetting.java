package cms.model.setting;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;

/**
 * 邮箱设置
 * @author Gao
 *
 */
@Entity
@Getter
@Setter
public class EmailSetting implements Serializable{
	@Serial
    private static final long serialVersionUID = 6319218594788894261L;
	
	/** Id **/
	@Id
	private Integer id;
	
	/** 发送邮件SMTP服务器 **/
	private String host;
	/** 邮件服务器端口 **/
	private Integer port;
	/** 邮箱账号 **/
	private String account;
	/** 邮箱密码 **/
	private String password;
	/** 支持收信的邮箱  存储JSON格式的List<String>类型 **/
	@Lob
	private String supportedMailboxes;
	/** 支持收信的邮箱 List<String> **/
	@Transient
	private List<String> supportedMailboxesList = new ArrayList<String>();
	
	/** 邮件 存储JSON格式的LinkedHashMap<Integer,Mail>类型 **/
	@Lob
	private String mail;
	/** 邮件 key:类型(10、验证码   20、注册完成消息)  value: Mail **/
	@Transient
	private Map<Integer,Mail> mailMap = new LinkedHashMap<Integer,Mail>();
	
	
	/** 版本 **/
	private Long version = 0L;

}
