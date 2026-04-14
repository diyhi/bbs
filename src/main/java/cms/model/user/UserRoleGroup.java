package cms.model.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户角色组(保存用户所属的角色)
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="userRoleGroup_1_idx", columnList="userName,validPeriodEnd"),@Index(name="userRoleGroup_3_idx", columnList="userRoleId,userName")})
public class UserRoleGroup implements Serializable{
	@Serial
    private static final long serialVersionUID = -1406832022665184431L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=30)
	private String userName;
	
	/** 角色Id **/
	@Column(length=32)
	private String userRoleId;

	/** 有效期结束  默认：2999-1-1 0:0 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime validPeriodEnd;


	
}
