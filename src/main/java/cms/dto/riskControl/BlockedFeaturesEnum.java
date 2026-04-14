package cms.dto.riskControl;

/**
 * 禁止的功能枚举
 * @author Gao
 *
 */
public enum BlockedFeaturesEnum {
	/** 禁止发表话题 **/
	BAN_POST_TOPIC("禁止发表话题", 100), 
	/** 禁止修改话题 **/
	BAN_EDIT_TOPIC("禁止修改话题", 200),
	/** 禁止发表评论 **/
	BAN_POST_COMMENT("禁止发表评论", 300),
	/** 禁止修改评论 **/
	BAN_EDIT_COMMENT("禁止修改评论", 400),
	/** 禁止删除评论 **/
	BAN_DELETE_COMMENT("禁止删除评论", 500),
	/** 禁止发表评论回复 **/
	BAN_POST_COMMENT_REPLY("禁止发表评论回复", 600),
	/** 禁止修改评论回复 **/
	BAN_EDIT_COMMENT_REPLY("禁止修改评论回复", 700),
	/** 禁止删除评论回复 **/
	BAN_DELETE_COMMENT_REPLY("禁止删除评论回复", 800),
	/** 禁止提交问题 **/
	BAN_POST_QUESTION("禁止提交问题", 900),
	/** 禁止追加问题 **/
	BAN_ADDITIONAL_QUESTION("禁止追加问题", 1000),
	/** 禁止提交答案 **/
	BAN_POST_ANSWER("禁止提交答案", 1100),
	/** 禁止修改答案 **/
	BAN_EDIT_ANSWER("禁止修改答案", 1200),
	/** 禁止删除答案 **/
	BAN_DELETE_ANSWER("禁止删除答案", 1300),
	/** 禁止提交答案回复 **/
	BAN_POST_ANSWER_REPLY("禁止提交答案回复", 1400),
	/** 禁止修改答案回复 **/
	BAN_EDIT_ANSWER_REPLY("禁止修改答案回复", 1500),
	/** 禁止删除答案回复 **/
	BAN_DELETE_ANSWER_REPLY("禁止删除答案回复", 1600),
	/** 禁止上传头像 **/
	BAN_UPLOAD_AVATAR("禁止上传头像", 1700),
	/** 禁止上传图片 **/
	BAN_UPLOAD_IMAGE("禁止上传图片", 1800),
	/** 禁止上传文件 **/
	BAN_UPLOAD_FILE("禁止上传文件", 1900),
	/** 禁止上传视频 **/
	BAN_UPLOAD_VIDEO("禁止上传视频", 2000),
	/** 禁止添加收藏 **/
	BAN_ADD_FAVORITE("禁止添加收藏", 2100),
	/** 禁止删除收藏 **/
	BAN_DELETE_FAVORITE("禁止删除收藏", 2200),
	/** 禁止添加点赞 **/
	BAN_ADD_LIKE("禁止添加点赞", 2300),
	/** 禁止删除点赞 **/
	BAN_DELETE_LIKE("禁止删除点赞", 2400),
	/** 禁止添加关注 **/
	BAN_ADD_FOLLOW("禁止添加关注", 2500),
	/** 禁止删除关注 **/
	BAN_DELETE_FOLLOW("禁止删除关注", 2600),
	/** 禁止发送私信 **/
	BAN_POST_PRIVATE_MESSAGE("禁止发送私信", 2700),
	/** 禁止删除私信 **/
	BAN_DELETE_PRIVATE_MESSAGE("禁止删除私信", 2800),
	/** 禁止使用AI助手 **/
	BAN_USE_AI_ASSISTANT("禁止使用AI助手", 2900),
	/** 禁止参与投票 **/
	BAN_ADD_VOTE_RECORD("禁止参与投票", 3000);
	
	/**
	 * 
	 * @param name 名称
	 * @param code 编号
	 */
    private BlockedFeaturesEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }
    /** 名称 **/
    private String name;
    /** 编号 **/
    private Integer code;
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
	
    
}
