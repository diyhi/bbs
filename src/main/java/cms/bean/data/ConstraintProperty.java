package cms.bean.data;

import java.io.Serializable;

/**
 * 约束属性
 * @author Administrator
 *
 */
public class ConstraintProperty implements Serializable{
	private static final long serialVersionUID = 6773312637867342207L;
	/** 本表名称 **/
	private String pk_Table_Name;
	/** 本表约束字段**/
	private String pk_Column_Name;
	/** 被哪个表约束**/
	private String fk_Table_Name;
	/** 被哪个表的某个键约束**/
	private String fk_Column_Name;
	/** 约束名称**/
	private String fk_Name;
	/** 外键序号 **/
	private Integer key_SEQ;
	
	public String getFk_Table_Name() {
		return fk_Table_Name;
	}
	public void setFk_Table_Name(String fk_Table_Name) {
		this.fk_Table_Name = fk_Table_Name;
	}
	public String getFk_Column_Name() {
		return fk_Column_Name;
	}
	public void setFk_Column_Name(String fk_Column_Name) {
		this.fk_Column_Name = fk_Column_Name;
	}
	public String getFk_Name() {
		return fk_Name;
	}
	public void setFk_Name(String fk_Name) {
		this.fk_Name = fk_Name;
	}
	public Integer getKey_SEQ() {
		return key_SEQ;
	}
	public void setKey_SEQ(Integer key_SEQ) {
		this.key_SEQ = key_SEQ;
	}
	public String getPk_Table_Name() {
		return pk_Table_Name;
	}
	public void setPk_Table_Name(String pk_Table_Name) {
		this.pk_Table_Name = pk_Table_Name;
	}
	public String getPk_Column_Name() {
		return pk_Column_Name;
	}
	public void setPk_Column_Name(String pk_Column_Name) {
		this.pk_Column_Name = pk_Column_Name;
	}
	
	
}
