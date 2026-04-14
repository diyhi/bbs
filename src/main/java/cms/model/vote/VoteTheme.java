package cms.model.vote;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;


/**
 * 投票主题
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="voteTheme_1_idx", columnList="userName"),@Index(name="voteTheme_2_idx", columnList="sourceParameterId,module"),@Index(name="voteTheme_3_idx", columnList="createDate"),@Index(name="voteTheme_4_idx", columnList="userName,isStaff,createDate")})
public class VoteTheme implements Serializable{
	@Serial
    private static final long serialVersionUID = 7473737686831724605L;
	
	/** ID **/
	@Id @Column(length=32)
	private String id;
	/** 投票标题 **/
	@Column(length=190)
	private String title;
	
	/** 模块 10:话题  20:问答**/
	private Integer module;
	/** 来源参数Id(话题Id、问题Id) **/
	@Column(length=70)
	private String sourceParameterId;
	/** 来源参数标题(话题标题、问题标题) **/
	@Transient
	private String sourceParameterTitle;
	
	/** 最大可选数 1:单选  -1:不限制/多选 **/
	private Integer maxChoice = 1;
	
	
	/** 创建时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate = LocalDateTime.now();
	/** 截止日期 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime endDate;
	
	
	/** 发起投票用户名称 **/
	@Column(length=30)
	private String userName;
	/** 用户Id**/
	@Transient
	private Long userId;
	/** 账号 **/
	@Transient
	private String account;
	/** 发起投票用户呢称 **/
	@Transient
	private String nickname;
	/** 发起投票用户头像路径 **/
	@Transient
	private String avatarPath;
	/** 发起投票用户头像名称 **/
	@Transient
	private String avatarName;
	
	/** 状态 10:未开始  20.进行中  30.已结束**/
	@Transient
	private Integer status = 10;
	
	
	/** 是否为员工 true:员工  false:会员 **/
	private Boolean isStaff = false;
	
	/** 投票选项集合 **/
	@Transient
	private List<VoteOption> voteOptionList = new ArrayList<VoteOption>();
	
	
	/** 投票结果展示方式  10:完全公开  20:仅向参与投票用户公开
	private Integer resultDisplay = 10; **/
	
	
	public void addVoteOption(VoteOption voteOption) {
		this.voteOptionList.add(voteOption);
	}
	

}
