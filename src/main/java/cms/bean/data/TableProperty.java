package cms.bean.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库表
 * @author Administrator
 *
 */
public class TableProperty implements Serializable{
	private static final long serialVersionUID = -3348207851449566580L;
	
	/** 表名 **/
	private String tableName;
	/** 主键 **/
	private String pk_Column_Name;
	/** 约束属性 **/
	private List<ConstraintProperty> constraintProperty = new ArrayList<ConstraintProperty>();
	/** 字段属性 **/
	private List<FieldProperties> fieldProperties = new ArrayList<FieldProperties>();
	
	
	public void addConstraintProperty(ConstraintProperty constraintProperty) {
		this.constraintProperty.add(constraintProperty);
	}
	public void addFieldProperties(FieldProperties fieldProperties) {
		this.fieldProperties.add(fieldProperties);
	}
	
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPk_Column_Name() {
		return pk_Column_Name;
	}
	public void setPk_Column_Name(String pk_Column_Name) {
		this.pk_Column_Name = pk_Column_Name;
	}
	public List<ConstraintProperty> getConstraintProperty() {
		return constraintProperty;
	}
	public void setConstraintProperty(List<ConstraintProperty> constraintProperty) {
		this.constraintProperty = constraintProperty;
	}
	public List<FieldProperties> getFieldProperties() {
		return fieldProperties;
	}
	public void setFieldProperties(List<FieldProperties> fieldProperties) {
		this.fieldProperties = fieldProperties;
	}
	
	
}
