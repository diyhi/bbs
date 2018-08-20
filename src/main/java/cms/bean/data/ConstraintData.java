package cms.bean.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 组装约束数据
 * @author Administrator
 *
 */
public class ConstraintData implements Serializable{
	private static final long serialVersionUID = -3620000921116073601L;
	
	/** 表名 **/
	private String pk_Table_Name;
	/** 约束键名 **/
	private List<String>  pk_Column_Name = new ArrayList<String>();
	/** 约束名称 **/
	private String fk_Name;
	/** 被哪个表约束**/
	private String fk_Table_Name;
	/** 被哪个表的某个键约束 **/
	private List<String>  fk_Column_Name = new ArrayList<String>();
	
	public void addPk_Column_Name(String pk_Column_Name) {
		this.pk_Column_Name.add(pk_Column_Name);
	}
	public void addFk_Column_Name(String fk_Column_Name) {
		this.fk_Column_Name.add(fk_Column_Name);
	}
	
	

	public String getPk_Table_Name() {
		return pk_Table_Name;
	}
	public void setPk_Table_Name(String pk_Table_Name) {
		this.pk_Table_Name = pk_Table_Name;
	}
	public List<String> getPk_Column_Name() {
		return pk_Column_Name;
	}
	public void setPk_Column_Name(List<String> pk_Column_Name) {
		this.pk_Column_Name = pk_Column_Name;
	}
	public String getFk_Name() {
		return fk_Name;
	}
	public void setFk_Name(String fk_Name) {
		this.fk_Name = fk_Name;
	}
	public String getFk_Table_Name() {
		return fk_Table_Name;
	}
	public void setFk_Table_Name(String fk_Table_Name) {
		this.fk_Table_Name = fk_Table_Name;
	}
	public List<String> getFk_Column_Name() {
		return fk_Column_Name;
	}
	public void setFk_Column_Name(List<String> fk_Column_Name) {
		this.fk_Column_Name = fk_Column_Name;
	}

}
