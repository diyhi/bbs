package cms.dto.fileSystem;


import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件存储系统接口表单
 */
@Getter
@Setter
public class FileSystemInterfaceRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5824479554097588678L;

    /** Id  **/
    private Integer fileSystemInterfaceId;
    /** 名称  **/
    private String name;
    /** 接口产品 10:SeaweedFS  20.MinIO 30.阿里云OSS **/
    private Integer interfaceProduct;
    /** 是否选择  true:启用 false: 禁用 **/
    private boolean enable = true;


    private String seaweedFS_accessKeyId;
    private String seaweedFS_secretAccessKey;
    private String seaweedFS_accessBucketType;
    private String[] seaweedFS_endpoint;

    private String minIO_accessKeyId;
    private String minIO_secretAccessKey;
    private String minIO_accessBucketType;
    private String[] minIO_endpoint;

    private String oss_accessKeyId;
    private String oss_accessKeySecret;
    private String oss_bucket;
    private String oss_endpoint;
    private String oss_originGroup;
    private String oss_regionId;
    private String oss_pipelineId;
    private String oss_mnsQueueName;
    private String oss_accountEndpoint;

}
