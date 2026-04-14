package cms.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户资源
 *
 */
@Getter
@Setter
public class UserResource implements Serializable{
	@Serial
    private static final long serialVersionUID = -3605121621902616792L;

	/** 编号 **/
	private Integer code;
	/** 名称 **/
	private String name;
	/** 资源组编号 **/
	private Integer resourceGroupCode;
	
	/** 选中 **/
	private Boolean selected = false;

}
