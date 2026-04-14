package cms.model.fileSystem;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

/**
 * 文件存储系统
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
public class FileSystemInterface implements Serializable{
	@Serial
    private static final long serialVersionUID = -358551106506951569L;

	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称  **/
	@Column(length=100)
	private String name;
	/** 接口产品 10:SeaweedFS  20.MinIO 30.阿里云OSS **/
	private Integer interfaceProduct;
	/** 是否选择  true:启用 false: 禁用 **/
	private boolean enable = true;
	/** 接口动态参数 **/
	@Lob
	private String dynamicParameter;
	/** 版本 **/
	private Integer version = 0;
}
