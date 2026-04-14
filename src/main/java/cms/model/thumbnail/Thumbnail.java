package cms.model.thumbnail;

import java.io.Serial;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * 缩略图
 *
 */
@Getter
@Setter
@Entity
@Table(name="thumbnail",uniqueConstraints = {
		@UniqueConstraint(columnNames={"specificationGroup"}
   )}
)
public class Thumbnail implements Serializable{
	@Serial
    private static final long serialVersionUID = 2981137798741140950L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 缩略图名称 **/
	@Column(length=200)
	private String name;
	/** 宽 **/
	private Integer width;
	/** 高 **/
	private Integer high;
	/** 规格组 **/
	@Column(length=25)
	private String specificationGroup;

}
