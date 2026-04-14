package cms.model.fileSystem;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 阿里云OSS参数
 *
 */
@Getter
@Setter
public class OSS implements Serializable{
	@Serial
    private static final long serialVersionUID = -7087326324715867437L;
	
	/** 访问密钥Id **/
	private String accessKeyId;
	/** 签名密钥 **/
	private String accessKeySecret;
	/** 存储空间  Bucket是OSS的存储桶，OSS是对象存储服务，Bucket就是存放Object的容器，每个Object必须存放在特定的Bucket中 **/
	private String bucket;
	
	/** 访问域名 例如https://oss-cn-hangzhou.aliyuncs.com **/
	private String endpoint;
	/** 跨域请求的来源组 例如https://www.diyhi.com **/
	private String originGroup;
	/** 点播服务接入区域 例如：接入区域在上海，请使用cn-shanghai **/
	private String regionId;
	/** 管道Id **/
	private String pipelineId;
	/** MNS消息队列名称 **/
	private String mnsQueueName;
	/** MNS消息队列服务端地址 **/
	private String accountEndpoint;

}
