package cms.bean.mediaProcess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 媒体处理设置
 * @author Gao
 *
 */
@Entity
public class MediaProcessSetting implements Serializable{
	private static final long serialVersionUID = -7938266500098151340L;
	
	/** Id **/
	@Id
	private Integer id;
	
	/** 切片每段时长 单位/秒 **/
	private Integer segmentTime = 10;
	
	/** 使用水印 **/
	private Boolean watermark = false;
	/** 水印文件路径名称 **/
	private String watermarkFullPathName;
	/** 水印位置 10:左上角 20:右上角 30:右下角 40:左下角**/
	private Integer watermarkPosition = 10;
	/** 水印水平距离 **/
	private Integer watermarkDistance_x = 10;
	/** 水印垂直距离 **/
	private Integer watermarkDistance_y = 10;
	/** 帧速率 24表示每秒24帧 空表示采用源视频帧速率 **/
	private Integer videoFrameRate;
	
	/** 切片地点 0:无 10:本地切片 20:远程切片 30:本地切片+远程切片 **/
	private Integer segmentLocation = 0;
	/** 远程切片验证密钥 **/
	@Column(length=100)
	private String remoteSegmentSecret;
	
	/** 版本 **/
	private Long version = 0L;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSegmentTime() {
		return segmentTime;
	}
	public void setSegmentTime(Integer segmentTime) {
		this.segmentTime = segmentTime;
	}
	public Boolean getWatermark() {
		return watermark;
	}
	public void setWatermark(Boolean watermark) {
		this.watermark = watermark;
	}
	public String getWatermarkFullPathName() {
		return watermarkFullPathName;
	}
	public void setWatermarkFullPathName(String watermarkFullPathName) {
		this.watermarkFullPathName = watermarkFullPathName;
	}
	public Integer getWatermarkPosition() {
		return watermarkPosition;
	}
	public void setWatermarkPosition(Integer watermarkPosition) {
		this.watermarkPosition = watermarkPosition;
	}
	public Integer getWatermarkDistance_x() {
		return watermarkDistance_x;
	}
	public void setWatermarkDistance_x(Integer watermarkDistance_x) {
		this.watermarkDistance_x = watermarkDistance_x;
	}
	public Integer getWatermarkDistance_y() {
		return watermarkDistance_y;
	}
	public void setWatermarkDistance_y(Integer watermarkDistance_y) {
		this.watermarkDistance_y = watermarkDistance_y;
	}
	public Integer getVideoFrameRate() {
		return videoFrameRate;
	}
	public void setVideoFrameRate(Integer videoFrameRate) {
		this.videoFrameRate = videoFrameRate;
	}
	public Integer getSegmentLocation() {
		return segmentLocation;
	}
	public void setSegmentLocation(Integer segmentLocation) {
		this.segmentLocation = segmentLocation;
	}
	public String getRemoteSegmentSecret() {
		return remoteSegmentSecret;
	}
	public void setRemoteSegmentSecret(String remoteSegmentSecret) {
		this.remoteSegmentSecret = remoteSegmentSecret;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	
	
}
