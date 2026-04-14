package cms.dto.statistic;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 访问信息
 *
 */
@Getter
@Setter
public class AccessInfo implements Serializable{
	@Serial
    private static final long serialVersionUID = -100218312963154112L;
	
	/** 浏览器名称 **/
	private String browserName;
	/** 浏览器大类 **/
	private String group;
	/** 详细版本 **/
	private String browserVersion;
	/** 浏览器主版本 **/
	private String version;
	/** 访问设备系统 **/
	private String operatingSystem;
	/** 访问设备类型 **/
	private String deviceType;
}
