package cms.model.message;

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
import lombok.Getter;
import lombok.Setter;

/**
 * 系统通知
 *
 */
@Getter
@Setter
@Entity
@Table(name="systemnotify",indexes = {@Index(name="systemNotify_1_idx", columnList="sendTime")})
public class SystemNotify implements Serializable{
	@Serial
    private static final long serialVersionUID = 796537226963121494L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 员工名称 **/
	@Column(length=30)
	private String staffName;
	/** 通知内容 **/
	@Lob
	private String content;

	/** 发送时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime sendTime = LocalDateTime.now();


}
