package cms.model.help;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;


/**
 * 帮助
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="help_idx", columnList="helpTypeId,visible")})
public class Help implements Serializable{
	@Serial
    private static final long serialVersionUID = 2548461575859641680L;
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
	/** 帮助分类Id **/
	private Long helpTypeId;
	/** 帮助分类名称 **/
	@Transient
	private String helpTypeName;
	
	/** 帮助名称 **/
	@Column(length=200)
	private String name;
	/** 帮助内容 **/
	@Lob
	private String content;
	/** 是否使用Markdown **/
	private Boolean isMarkdown;
	/** Markdown内容 **/
	@Lob
	private String markdownContent;
	/** 发表时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime times = LocalDateTime.now();
	
	/** 是否可见 **/
	private boolean visible = true;

	
	
	public Help() {}
	public Help(Long id, String name) {
		this.id = id;
		this.name = name;
	}


}
