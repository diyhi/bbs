package cms.bean.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 模板运行对象
 *
 */
public class TemplateRunObject implements Serializable{
	private static final long serialVersionUID = 920363926035083837L;
	
	/** 布局文件名称 **/
	private List<String> layoutFileList = new ArrayList<String>();
	/** 正在运行版块的引用代码 **/
	private String referenceCode = "";
	/** 提交参数 **/
	private Map<String,Object> parameter = new HashMap<String,Object>();
	
	/** 运行参数 **/
	private Map<String,Object> runtimeParameter = new HashMap<String,Object>();
	
	
	/** 分页参数 
	private Integer page;**/
	/** '更多'版块 
	private Forum more_forum;**/
	
	/**
	 * 添加布局文件名称
	 * @param layoutFile 布局文件名称
	 */
	public void addLayoutFile(String layoutFile){
		this.layoutFileList.add(layoutFile);
	}
	/**
	 * 添加布局文件名称
	 * @param layoutFileList 布局文件名称集合
	 */
	public void addAllLayoutFile(List<String> layoutFileList){
		this.layoutFileList.addAll(layoutFileList);
	}
	/**
	 * 添加提交参数
	 */
	public void addParameter(String key,Object value){
		this.parameter.put(key, value);
	}
	/**
	 * 添加提交参数
	 */
	public void addParameter(Map<String,Object> parameter){
		this.parameter.putAll(parameter);
	}
	/**
	 * 添加运行参数
	 */
	public void addRuntimeParameter(String key,Object value){
		this.runtimeParameter.put(key, value);
	}
	/**
	 * 添加运行参数
	 */
	public void addRuntimeParameter(Map<String,Object> parameter){
		this.runtimeParameter.putAll(parameter);
	}
	
	/**
	 * 添加正在运行的版块引用代码
	 * @param referenceCode 版块引用代码
	 
	public void addReferenceCodeList(String referenceCode){
		this.referenceCodeList.add(referenceCode);
	}
	/**
	 * 添加正在运行的版块引用代码
	 * @param referenceCodeList 版块引用代码集合
	 
	public void addAllReferenceCodeList(Set<String> referenceCodeList){
		this.referenceCodeList.addAll(referenceCodeList);
	}*/
	
	public List<String> getLayoutFileList() {
		return layoutFileList;
	}
	public void setLayoutFileList(List<String> layoutFileList) {
		this.layoutFileList = layoutFileList;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	public Map<String, Object> getParameter() {
		return parameter;
	}
	public void setParameter(Map<String, Object> parameter) {
		this.parameter = parameter;
	}
	
	public Map<String, Object> getRuntimeParameter() {
		return runtimeParameter;
	}
	public void setRuntimeParameter(Map<String, Object> runtimeParameter) {
		this.runtimeParameter = runtimeParameter;
	}

	
}
