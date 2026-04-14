package cms.model.statistic;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * 页面访问量
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="pv_1_idx", columnList="times")})
public class PV implements Serializable{
	@Serial
    private static final long serialVersionUID = -8183252635387484081L;
	
	/** ID **/
	@Id @Column(length=32)
	private String id;
	/** 页面来源 **/
	@Lob
	private String referrer;
	/** 受访URL **/
	@Lob
	private String url;
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	
	/** 访问时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime times = LocalDateTime.now();
	
	
	/** 浏览器名称 **/
	private String browserName;
	/** 访问设备系统 **/
	private String operatingSystem;
	/** 访问设备类型 **/
	private String deviceType;

}
