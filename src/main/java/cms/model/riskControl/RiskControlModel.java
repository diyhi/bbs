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
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;

/**
 * 风控模型
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
public class RiskControlModel implements Serializable{
	@Serial
    private static final long serialVersionUID = 8756113655347279000L;
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/** 名称**/
	@Column(length=190)
	private String name;
	
	/** 类型  10：发贴(发贴包含'发表话题'、'修改话题'、'发表评论'、'修改评论'、'发表评论回复'、'修改评论回复'、'发表问题'、追加问题'、'发表答案'、'修改答案'、'发表答案回复'、'修改答案回复'等操作)   20：注册 **/
	private Integer type = 10;
	
	/** 风控规则(公式脚本，公式计算结果返回true则进入下一流程) **/
	@Lob
	private String rules;
	/** 创建时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate = LocalDateTime.now();
	/** 排序 **/
	private Integer sort = 0;
	
	/** 是否启用 true:启用 false:禁用 **/
	@Column(name="\"enable\"")
	private boolean enable = true;
	
	/** 关联风控敏感词(存储JSON格式的associateSensitiveWordLibraryList数据) **/
	@Lob
	private String associateSensitiveWord;
	
	/** 关联风控敏感词 **/
	@Transient
	private List<AssociateSensitiveWord> associateSensitiveWordList = new ArrayList<AssociateSensitiveWord>();
	
	
	/** 风控处罚参数 **/
	@Lob
	private String penaltyParameter;
	
	/** 风控处罚 **/
	@Transient
	private Penalty penalty = new Penalty();
	
	/** 备注 **/
	@Lob
	private String remark;
	
	/** 版本 **/
	private Integer version = 0;
	
	

	public void addAssociateSensitiveWord(AssociateSensitiveWord associateSensitiveWord) {
		this.associateSensitiveWordList.add(associateSensitiveWord);
	}

}
