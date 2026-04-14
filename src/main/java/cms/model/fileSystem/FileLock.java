package cms.model.fileSystem;



import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件锁
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="fileLock_1_idx", columnList="filePath"), @Index(name="fileLock_2_idx",columnList="lockTime")})
public class FileLock implements Serializable{
	@Serial
    private static final long serialVersionUID = 5402376625049966376L;

	/** Id **/
	@Id @Column(length=36)
	private String id;
	/** 文件路径 **/
	@Column(length=190)
	private String filePath;
	/** 锁定时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime lockTime = LocalDateTime.now();
	
	
	public FileLock() {}
	public FileLock(String id, String filePath, LocalDateTime lockTime) {
		this.id = id;
		this.filePath = filePath;
		this.lockTime = lockTime;
	}

}
