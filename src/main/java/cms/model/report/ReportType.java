package cms.model.report;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;


/**
 * 举报分类
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="reportType_1_idx", columnList="sort")})
public class ReportType implements Serializable{
	@Serial
    private static final long serialVersionUID = -6736014509164078586L;

	/** ID **/
	@Id @Column(length=36)
	private String id;
	
	/** 分类名称 **/
	@Column(length=190)
	private String name;
	
	/** 所属父类Id 根节点为0 **/
	@Column(length=36)
	private String parentId;
	/** 子分类**/
	@Transient
	private List<ReportType> childType = new ArrayList<ReportType>();
	
	/** 排序 **/
	private Integer sort = 0;
	
	/** 是否需要说明理由 **/
	private Boolean giveReason = false;
	
	
	/** 父Id组  顺序存放**/
	@Column(length=190)
	private String parentIdGroup = ",0,";
	
	/** 子节点数量 **/
	private Integer childNodeNumber = 0;
	
	
	/** 添加子分类  **/
	public void addChildType(ReportType reportType){
		this.getChildType().add(reportType);
	}
	

}
