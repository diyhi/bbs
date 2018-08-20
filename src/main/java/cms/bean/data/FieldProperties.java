package cms.bean.data;

import java.io.Serializable;

/**
 * 字段属性
 * @author Administrator
 *
 */
public class FieldProperties implements Serializable{
	private static final long serialVersionUID = 7800052324920277475L;
	
	/** 字段名称 **/
	private String fieldName;
	/** 字段类型**/
	private Integer fieldType;
	/** 字段类型名称**/
	private String fieldTypeName;
	/** 字段索引**/
	private String fieldIndex;

	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Integer getFieldType() {
		return fieldType;
	}
	public void setFieldType(Integer fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldIndex() {
		return fieldIndex;
	}
	public void setFieldIndex(String fieldIndex) {
		this.fieldIndex = fieldIndex;
	}
	public String getFieldTypeName() {
		return fieldTypeName;
	}
	public void setFieldTypeName(String fieldTypeName) {
		this.fieldTypeName = fieldTypeName;
	}
	
	
	
}
