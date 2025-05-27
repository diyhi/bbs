package cms.bean.setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

/**
 * 邮箱设置
 * @author Gao
 *
 */
@Entity
public class EmailSetting implements Serializable{
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


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	public Integer getPort() {
		return port;
	}


	public void setPort(Integer port) {
		this.port = port;
	}


	public String getAccount() {
		return account;
	}


	public void setAccount(String account) {
		this.account = account;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getMail() {
		return mail;
	}


	public void setMail(String mail) {
		this.mail = mail;
	}


	public Map<Integer, Mail> getMailMap() {
		return mailMap;
	}


	public void setMailMap(Map<Integer, Mail> mailMap) {
		this.mailMap = mailMap;
	}


	public Long getVersion() {
		return version;
	}


	public void setVersion(Long version) {
		this.version = version;
	}


	public String getSupportedMailboxes() {
		return supportedMailboxes;
	}


	public void setSupportedMailboxes(String supportedMailboxes) {
		this.supportedMailboxes = supportedMailboxes;
	}


	public List<String> getSupportedMailboxesList() {
		return supportedMailboxesList;
	}


	public void setSupportedMailboxesList(List<String> supportedMailboxesList) {
		this.supportedMailboxesList = supportedMailboxesList;
	}


	
	
	
}
