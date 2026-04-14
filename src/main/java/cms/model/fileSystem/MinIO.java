package cms.model.fileSystem;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * MinIO参数
 *
 */
@Getter
@Setter
public class MinIO implements Serializable{
	@Serial
    private static final long serialVersionUID = 1189381452811624174L;
	
	/** 访问密钥Id **/
	private String accessKeyId;
	/** 签名密钥 **/
	private String secretAccessKey;
	/** 存储空间  Bucket是S3的存储桶，S3是对象存储服务，Bucket就是存放Object的容器，每个Object必须存放在特定的Bucket中 **/
	private String bucket="bbs-bucket";
	/** 区域 区域的标准格式是“国家-区域-编号”，如“中国-华北-1号”就可写成“cn-north-1”，又比如“美国-西部-2号”可写成“us-west-2” **/
	private String region="default";
	/** 访问存储空间类型 10.路径类型访问(https://s3.Region.amazonaws.com/bucket-name/key name) 20.虚拟托管类型访问(https://bucket-name.s3.Region.amazonaws.com/key name) **/
	private Integer accessBucketType = 10;
	
	
	/** 访问域名集合 格式http://IP:port  例如http://s3-1.diyhi.com或http://localhost:8333 **/
	private List<String> endpointList = new ArrayList<String>();

}
