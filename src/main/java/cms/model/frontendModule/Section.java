package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 站点栏目
 *
 */
@Getter
@Setter
public class Section implements Serializable{
	@Serial
    private static final long serialVersionUID = 4117213723041628243L;
	
	/** ID **/
	private Integer id;
	/** 栏目名称 **/
	private String name;
	/** 多语言扩展  key:字段-语言（例如：name-en_US） value:内容**/
	private Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();
	/** 所属父类ID **/
	private Integer parentId = 0;
	/** 子栏目**/
	private List<Section> childSection = new ArrayList<Section>();
	/** 排序 **/
	private Integer sort = 1;
	/** 链接方式   1.无   2.外部URL  3.内部URL**/
	private Integer linkMode = 1;
	/** URL **/
	private String url = "";

	/**
	 * 添加子栏目
	 * @param section 子栏目
	 */
	public void addSection(Section section){
		this.getChildSection().add(section);
	}
	/**
	 * 添加子栏目
	 * @param childSection 子栏目
	 */
	public void addSection(List<Section> childSection){
		this.getChildSection().addAll(childSection);
	}


}
