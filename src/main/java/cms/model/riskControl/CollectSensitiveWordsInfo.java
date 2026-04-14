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
 * 采集敏感词信息
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="collectSensitiveWordsInfo_1_idx", columnList="userName,postTime,sensitiveWordsNumber,riskControlModelId")})
public class CollectSensitiveWordsInfo implements Serializable{
	@Serial
    private static final long serialVersionUID = -9122903542716388130L;

	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 风控模型Id **/
	private Integer riskControlModelId;
	
	/** 模块  10.发表话题、20.修改话题、30.发表评论、40.修改评论、50.发表评论回复、60.修改评论回复、70.发表问题、80.追加问题、90.发表答案、100.修改答案、110.发表答案回复、120.修改答案回复 **/
	private Integer module;
	/** 参数Id(话题Id、评论Id、评论回复Id、问题Id、追加问题Id、答案Id、答案回复Id) **/
	@Column(length=70)
	private String parameterId;
	/** 扩展参数Id (模块值10或20：空；  模块值30或40：话题Id； 模块值50或60：话题Id-评论Id；  模块值70或80：空；  模块值90或100：问题Id；  模块值110或120：问题Id-答案Id) **/
	@Column(length=130)
	private String extraParameterId;
	/** 用户名称**/
	@Column(length=30)
	private String userName;
	/** IP地址**/
	@Column(length=45)
	private String ip;
	/** 发贴时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime postTime;
	/** 触发敏感词数量 **/
	private Integer sensitiveWordsNumber = 0;
	
	/** 触发敏感词(存储JSON格式的sensitiveWordList数据) **/
	@Lob
	private String sensitiveWord;
	/** 触发敏感词集合 **/
	@Transient
	private List<String> sensitiveWordList = new ArrayList<String>();

}
