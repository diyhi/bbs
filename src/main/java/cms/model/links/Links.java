package cms.model.links;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * 友情链接
 *
 */
@Getter
@Setter
@Entity
public class Links implements Serializable{
	@Serial
    private static final long serialVersionUID = -7640610316706427936L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称**/
	@Column(length=190)
	private String name;
	/** 网址 **/
	@Column(length=250)
	private String website;
	/** 排序 **/
	private Integer sort = 0;
	/** 图片 **/
	@Column(length=100)
	private String image;
	/** 创建时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate = LocalDateTime.now();

}
