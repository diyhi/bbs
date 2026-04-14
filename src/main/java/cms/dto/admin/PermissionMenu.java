package cms.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限菜单
 * @author Gao
 *
 */
@Getter
@Setter
public class PermissionMenu  implements Serializable{
	@Serial
    private static final long serialVersionUID = 1721622201354849134L;
	
	/** 名称 **/
	private String name;
	/** URL **/
	private String url;
	/** 请求类型 **/ 
	private String methods;
}
