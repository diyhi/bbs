package cms.model.riskControl;

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
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * 处罚贴子信息
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="penaltyPostInfo_1_idx", columnList="createTime"),@Index(name="penaltyPostInfo_2_idx", columnList="parameterId,module")})
public class PenaltyPostInfo implements Serializable{
	@Serial
    private static final long serialVersionUID = -3053309808423548579L;
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 用户名称**/
	@Column(length=30)
	private String userName;
	/** 用户Id**/
	@Transient
	private Long userId;
	/** 账号 **/
	@Transient
	private String account;
	/** 呢称 **/
	@Transient
	private String nickname;
	/** 头像路径 **/
	@Transient
	private String avatarPath;
	/** 头像名称 **/
	@Transient
	private String avatarName;
	
	/** 创建时间**/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createTime;
	/** 模块  10.发表话题、20.修改话题、30.发表评论、40.修改评论、50.发表评论回复、60.修改评论回复、70.发表问题、80.追加问题、90.发表答案、100.修改答案、110.发表答案回复、120.修改答案回复 **/
	private Integer module;
	/** 参数Id(话题Id、评论Id、评论回复Id、问题Id、追加问题Id、答案Id、答案回复Id) **/
	@Column(length=70)
	private String parameterId;
	/** 扩展参数Id (模块值10或20：空；  模块值30或40：话题Id； 模块值50或60：话题Id-评论Id；  模块值70或80：空；  模块值90或100：问题Id；  模块值110或120：问题Id-答案Id) **/
	@Column(length=130)
	private String extraParameterId;
	/** 风控模型Id **/
	private Integer riskControlModelId;
	/** 风控模型名称**/
	@Transient
	private String riskControlModelName;
	
	/** 本次触发敏感词(存储JSON格式的sensitiveWordList数据) **/
	@Lob
	private String sensitiveWord;
	/** 本次触发敏感词集合 **/
	@Transient
	private List<String> sensitiveWordList = new ArrayList<String>();
	
	/** 风控处罚参数(处罚贴子部分) Penalty类型的json格式 **/
	@Lob
	private String penaltyParameter;
	/** 风控处罚 **/
	@Transient
	private Penalty penalty = new Penalty();
	

}
