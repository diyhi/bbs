package cms.model.setting;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 编辑器标签
 *
 */
@Getter
@Setter
public class EditorTag implements Serializable{
	@Serial
    private static final long serialVersionUID = 3488998270047501318L;

	
	/** 字体 **/
	private boolean fontname = false;
	/** 文字大小 **/
	private boolean fontsize = false;
	/** 文字颜色 **/
	private boolean forecolor = false;
	/** 文字背景 **/
	private boolean hilitecolor = false;
	/** 粗体 **/
	private boolean bold = false;
	/** 斜体 **/
	private boolean italic = false;
	/** 下划线 **/
	private boolean underline = false;
	/** 删除格式 **/
	private boolean removeformat = false;
	/** 超级链接 **/
	private boolean link = false;
	/** 取消超级链接 **/
	private boolean unlink = false;
	/** 左对齐 **/
	private boolean justifyleft = false;
	/** 居中 **/
	private boolean justifycenter = false;
	/** 右对齐 **/
	private boolean justifyright = false;
	/** 编号 **/
	private boolean insertorderedlist = false;
	/** 项目符号 **/
	private boolean insertunorderedlist = false;
	/** 代码 **/
	private boolean code = false;
	
	/** 插入表情 **/
	private boolean emoticons = false;
	/** 允许上传图片 **/
	private boolean image = false;
	
	/** 允许上传图片格式 **/
	private List<String> imageFormat = new ArrayList<String>();
	/** 允许上传图片大小 **/
	private Long imageSize;
	
	/** 允许上传文件 **/
	private boolean file = false;
	/** 允许上传文件格式 **/
	private List<String> fileFormat = new ArrayList<String>();
	/** 允许上传文件大小 **/
	private Long fileSize;
	
	/** 允许嵌入视频 **/
	private boolean embedVideo = false;
	
	/** 允许上传视频 **/
	private boolean uploadVideo = false;
	/** 允许上传视频格式 **/
	private List<String> videoFormat = new ArrayList<String>();
	/** 允许上传视频大小 **/
	private Long videoSize;
	
	/** 输入密码可见 **/
	private boolean hidePassword = false;
	/** 评论话题可见 **/
	private boolean hideComment = false;
	/** 达到等级可见 **/
	private boolean hideGrade = false;
	/** 积分购买可见 **/
	private boolean hidePoint = false;
	/** 余额购买可见 **/
	private boolean hideAmount = false;
	
	/** 全屏显示 **/
	private boolean fullscreen = false;
	
	/** 提及 **/
	private boolean mention = false;
	/** 人工智能 **/
	private boolean ai = false;
	
	
	
	/**
	 * 添加图片格式
	 * @param suffix 图片后缀
	 */
	public void addImageFormat(String suffix){
		this.imageFormat.add(suffix);
	}
}
