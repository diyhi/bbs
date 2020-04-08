package cms.bean.mediaProcess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


/**
 * 媒体处理队列
 * @author Gao
 *
 */
@Entity
@Table(indexes = {@Index(name="mediaProcessQueue_1_idx", columnList="processProgress"),@Index(name="mediaProcessQueue_2_idx", columnList="fileName")})
public class MediaProcessQueue implements Serializable{
	private static final long serialVersionUID = 7242129516093781064L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 模块 10:话题  **/
	private Integer module;
	/** 参数 话题Id  **/
	@Column(length=100)
	private String parameter;
	/** 标题  **/
	@Transient
	private String title;
	/** 提交时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date postTime = new Date();
	
	/** 类型 10:视频 20:音频 **/
	private Integer type;
	/** 待处理文件路径 **/
	private String filePath;
	/** 待处理文件名称 **/
	@Column(length=100)
	private String fileName;
	/** 处理文件的客户端IP **/
	@Column(length=45)
	private String ip;
	
	/** 处理进度 -1为还没有客户端领取本任务 0为已有客户端领取本任务 0以上为处理进度  **/
	private Double processProgress = -1d;
	/** 错误信息  组合成json List<String>类型 **/
	@Lob
	private String errorInfo = "[";
	@Transient
	private List<String> errorInfoList = new ArrayList<String>();
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getModule() {
		return module;
	}
	public void setModule(Integer module) {
		this.module = module;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Double getProcessProgress() {
		return processProgress;
	}
	public void setProcessProgress(Double processProgress) {
		this.processProgress = processProgress;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public Date getPostTime() {
		return postTime;
	}
	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	public List<String> getErrorInfoList() {
		return errorInfoList;
	}
	public void setErrorInfoList(List<String> errorInfoList) {
		this.errorInfoList = errorInfoList;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	
	
	
}
