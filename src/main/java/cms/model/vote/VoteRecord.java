package cms.model.vote;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;


/**
 * 投票记录
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="voteRecord_1_idx", columnList="userName,voteTime"),@Index(name="voteRecord_2_idx", columnList="voteThemeId,userName"),@Index(name="voteRecord_3_idx", columnList="voteOptionId,voteTime"),@Index(name="voteRecord_4_idx", columnList="voteThemeId,voteTime"),@Index(name="voteRecord_5_idx", columnList="sourceParameterId,module")})
public class VoteRecord implements Serializable{
	@Serial
    private static final long serialVersionUID = 842944453853941515L;
	
	/** ID 格式：单选（用户名称_投票主题Id_投票选项Id）；多选（用户名称_投票主题Id） **/
	@Id
	@Column(length=96)
	private String id;
	/** 投票主题Id **/
	@Column(length=32)
	private String voteThemeId;
	/** 投票主题标题 **/
	@Transient
	private String voteThemeTitle;
	/** 投票选项Id **/
	@Column(length=32)
	private String voteOptionId;
	
	/** 参与投票用户名称 **/
	@Column(length=30)
	private String userName;
	/** 参与投票用户Id**/
	@Transient
	private Long userId;
	/** 参与投票账号 **/
	@Transient
	private String account;
	/** 参与投票用户呢称 **/
	@Transient
	private String nickname;
	/** 参与投票用户头像路径 **/
	@Transient
	private String avatarPath;
	/** 参与投票用户头像名称 **/
	@Transient
	private String avatarName;
	
	/** IP **/
	@Column(length=45)
	private String ip;
	
	/** 投票时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime voteTime = LocalDateTime.now();
	
	/** 模块 10:话题  20:问答**/
	private Integer module;
	/** 来源参数Id(话题Id、问题Id) **/
	@Column(length=70)
	private String sourceParameterId;
	/** 来源参数标题(话题标题、问题标题) **/
	@Transient
	private String sourceParameterTitle;
	
	/** 投票选项Id集合
	@Transient
	private List<String> voteOptionIdList = new ArrayList<String>(); **/
	/** 投票选项Id数据(存储JSON格式的voteOptionIdList数据)
	@Lob
	private String voteOptionIdData; **/

	
}
