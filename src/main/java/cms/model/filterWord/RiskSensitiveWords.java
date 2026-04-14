package cms.model.filterWord;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;

/**
 * 风控敏感词
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
public class RiskSensitiveWords implements Serializable{
	@Serial
    private static final long serialVersionUID = -7001632324620990893L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称  **/
	@Column(length=100)
	private String name;
	
	/** 最后修改时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime lastModified;
	
	/** 敏感词  存储JSON格式的List<String>类型 **/
	@Lob
	private String words;
	/** 敏感词 List<String> **/
	@Transient
	private List<String> wordsList = new ArrayList<String>();
	
	/** 版本 **/
	private Integer version = 0;

	
}
