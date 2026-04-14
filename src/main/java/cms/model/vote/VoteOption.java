package cms.model.vote;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * 投票选项
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="voteOption_1_idx", columnList="userName"),@Index(name="voteOption_2_idx", columnList="voteThemeId,sort"),@Index(name="voteOption_3_idx", columnList="sourceParameterId,module")})
public class VoteOption implements Serializable{
	@Serial
    private static final long serialVersionUID = 2066754679433623910L;
	
	/** Id **/
	@Id @Column(length=32)
	private String id;
	/** 选项内容 **/
	@Column(length=60)
	private String text;
	/** 投票主题Id **/
	@Column(length=32)
	private String voteThemeId;
	/** 排序 **/
	private Integer sort;
	/** 是否选中 **/
	@Transient
	private boolean selected = false;
	/** 总票数 **/
	@Transient
	private Long totalVotes = 0L;
	
	/** 发起投票用户名称 **/
	@Column(length=30)
	private String userName;
	
	/** 模块 10:话题  20:问答**/
	private Integer module;
	/** 来源参数Id(话题Id、问题Id) **/
	@Column(length=70)
	private String sourceParameterId;
	

	
}
