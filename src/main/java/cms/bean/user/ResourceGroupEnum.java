package cms.bean.user;

/**
 * 资源组枚举
 *
 */
public enum ResourceGroupEnum {
	TAG("标签", 1000000,20), 
	QUESTION("问答", 10000000,10), 
	UPLOAD("上传",  2000000,10),
	FAVORITE("收藏", 3000000,10),
	LIKE("点赞", 4000000,10),
	FOLLOW("关注", 5000000,10),
	PRIVATEMESSAGE("私信", 6000000,10);
	
    private ResourceGroupEnum(String name, Integer code,Integer type) {
        this.name = name;
        this.code = code;
        this.type = type;
    }
    /** 资源组名称 **/
    private String name;
    /** 编号 **/
    private Integer code;
    /** 类型 10:直接提交 20:预处理--标签 **/
    private Integer type;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	
}
