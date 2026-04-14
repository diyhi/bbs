package cms.model.feedback;

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
 * 在线留言
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="feedback_1_idx", columnList="createDate")})
public class Feedback implements Serializable{
	@Serial
    private static final long serialVersionUID = -4891301979499383115L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 名称**/
	@Column(length=190)
	private String name;
	/** 联系方式 **/
	@Column(length=190)
	private String contact;
	/** 内容 **/
	@Lob
	private String content;
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	/** 创建时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate = LocalDateTime.now();

}
