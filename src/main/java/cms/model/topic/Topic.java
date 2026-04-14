package cms.model.topic;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cms.dto.MediaInfo;
import cms.dto.topic.ImageInfo;
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
 * 话题
 *
 */
@Getter
@Setter
@Entity 
@Table(indexes = {@Index(name="topic_3_idx", columnList="userName,postTime"),@Index(name="topic_5_idx", columnList="status,sort,lastReplyTime"),@Index(name="topic_6_idx", columnList="weight"),@Index(name="topic_7_idx", columnList="tagIdGroup,status"),@Index(name="topic_8_idx", columnList="essence,status,sort")})
public class Topic implements Serializable{
	@Serial
    private static final long serialVersionUID = -684257451052921859L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 标题 **/
	@Column(length=190)
	private String title;
	
	/** 标签Id **/
	private Long tagId;
	/** 标签名称 **/
	@Transient
	private String tagName;
	
	/** 标签Id组 **/
	@Column(length=50)
	private String tagIdGroup;

	/** 话题内容 **/
	@Lob
	private String content;
	/** 内容摘要 **/
	@Lob
	private String summary;
	
	/** 是否使用Markdown **/
	private Boolean isMarkdown;
	/** Markdown内容 **/
	@Lob
	private String markdownContent;
	
	/** 发表时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime postTime = LocalDateTime.now();
	/** 最后回复时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime lastReplyTime;
	/** 最后修改时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime lastUpdateTime;
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	
	/** 图片 List<ImageInfo>json格式 **/
	@Lob
	private String image;
	@Transient
	private List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
	
	/** 媒体文件信息集合 **/
	@Transient
	private List<MediaInfo> mediaInfoList = new ArrayList<MediaInfo>();
	
	/** 评论总数 **/
	private Long commentTotal = 0L;
	/** 允许评论 **/
	private boolean allow = true;
	
	/** 查看总数 **/
	private Long viewTotal = 0L;
	
	
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
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
	/** 用户信息状态 -30.账号已注销(不显示数据) -20.账号已逻辑删除(不显示数据) -10.账号已禁用(不显示数据)  0.正常 10.账号已禁用(显示数据) 20.账号已逻辑删除(显示数据) **/
	@Transient
	private Integer userInfoStatus = 0;
	
	/** 用户角色名称集合 **/
	@Transient
	private List<String> userRoleNameList = new ArrayList<String>();
	
	/** 话题允许查看的角色名称集合(默认角色除外) **/
	@Transient
	private List<String> allowRoleViewList = new ArrayList<String>();
	
	/** key:内容含有隐藏标签类型  10.输入密码可见  20.评论话题可见  30.达到等级可见 40.积分购买可见 50.余额购买可见  value:当前用户是否已对隐藏内容解锁 **/
	@Transient
	private LinkedHashMap<Integer,Boolean> hideTagTypeMap = new LinkedHashMap<Integer,Boolean>();
	
	/** 发红包Id **/
	@Column(length=32)
	private String giveRedEnvelopeId;
	
	/** 投票主题Id **/
	@Column(length=32)
	private String voteThemeId;
	
	
	/** 是否为员工 true:员工  false:会员 **/
	private Boolean isStaff = false;
	/** 排序  **/
	private Integer sort = 0;
	/** 精华  **/
	private Boolean essence = false;
	
	/** 状态 10.待审核 20.已发布 110.待审核删除 120.已发布删除 **/
	private Integer status = 10;
	
	
	/** 权重 = P表示热度因子(评论数+点赞数+浏览数)；T表示距离发帖的时间（单位为小时）；G表示"重力因子"（gravityth power），即将帖子排名往下拉的力量，默认值为1.8;  权重小于0时表示强制下沉，不再在热门榜上显示;  超出时间限制的热门话题权重在重新计算时设置为0        KaTeX公式表示：Score = \dfrac{P-1}{(T+2)^G} **/
	private Double weight = 0d;
	

}
