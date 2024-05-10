package cms.bean.setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 编辑器标签
 *
 */
public class EditorTag implements Serializable{
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

	public boolean isFontname() {
		return fontname;
	}
	public void setFontname(boolean fontname) {
		this.fontname = fontname;
	}
	public boolean isFontsize() {
		return fontsize;
	}
	public void setFontsize(boolean fontsize) {
		this.fontsize = fontsize;
	}
	public boolean isForecolor() {
		return forecolor;
	}
	public void setForecolor(boolean forecolor) {
		this.forecolor = forecolor;
	}
	public boolean isHilitecolor() {
		return hilitecolor;
	}
	public void setHilitecolor(boolean hilitecolor) {
		this.hilitecolor = hilitecolor;
	}
	public boolean isBold() {
		return bold;
	}
	public void setBold(boolean bold) {
		this.bold = bold;
	}
	public boolean isItalic() {
		return italic;
	}
	public void setItalic(boolean italic) {
		this.italic = italic;
	}
	public boolean isUnderline() {
		return underline;
	}
	public void setUnderline(boolean underline) {
		this.underline = underline;
	}
	public boolean isRemoveformat() {
		return removeformat;
	}
	public void setRemoveformat(boolean removeformat) {
		this.removeformat = removeformat;
	}
	public boolean isLink() {
		return link;
	}
	public void setLink(boolean link) {
		this.link = link;
	}
	public boolean isUnlink() {
		return unlink;
	}
	public void setUnlink(boolean unlink) {
		this.unlink = unlink;
	}
	public boolean isJustifyleft() {
		return justifyleft;
	}
	public void setJustifyleft(boolean justifyleft) {
		this.justifyleft = justifyleft;
	}
	public boolean isJustifycenter() {
		return justifycenter;
	}
	public void setJustifycenter(boolean justifycenter) {
		this.justifycenter = justifycenter;
	}
	public boolean isJustifyright() {
		return justifyright;
	}
	public void setJustifyright(boolean justifyright) {
		this.justifyright = justifyright;
	}
	public boolean isInsertorderedlist() {
		return insertorderedlist;
	}
	public void setInsertorderedlist(boolean insertorderedlist) {
		this.insertorderedlist = insertorderedlist;
	}
	public boolean isInsertunorderedlist() {
		return insertunorderedlist;
	}
	public void setInsertunorderedlist(boolean insertunorderedlist) {
		this.insertunorderedlist = insertunorderedlist;
	}
	public boolean isEmoticons() {
		return emoticons;
	}
	public void setEmoticons(boolean emoticons) {
		this.emoticons = emoticons;
	}
	public boolean isImage() {
		return image;
	}
	public void setImage(boolean image) {
		this.image = image;
	}

	public List<String> getImageFormat() {
		return imageFormat;
	}


	public void setImageFormat(List<String> imageFormat) {
		this.imageFormat = imageFormat;
	}


	public Long getImageSize() {
		return imageSize;
	}
	public void setImageSize(Long imageSize) {
		this.imageSize = imageSize;
	}

	public boolean isHidePassword() {
		return hidePassword;
	}

	public void setHidePassword(boolean hidePassword) {
		this.hidePassword = hidePassword;
	}

	public boolean isHideComment() {
		return hideComment;
	}

	public void setHideComment(boolean hideComment) {
		this.hideComment = hideComment;
	}

	public boolean isHideGrade() {
		return hideGrade;
	}

	public void setHideGrade(boolean hideGrade) {
		this.hideGrade = hideGrade;
	}

	public boolean isHidePoint() {
		return hidePoint;
	}

	public void setHidePoint(boolean hidePoint) {
		this.hidePoint = hidePoint;
	}

	public boolean isHideAmount() {
		return hideAmount;
	}

	public void setHideAmount(boolean hideAmount) {
		this.hideAmount = hideAmount;
	}

	public List<String> getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(List<String> fileFormat) {
		this.fileFormat = fileFormat;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public boolean isFile() {
		return file;
	}

	public void setFile(boolean file) {
		this.file = file;
	}

	

	public boolean isEmbedVideo() {
		return embedVideo;
	}

	public void setEmbedVideo(boolean embedVideo) {
		this.embedVideo = embedVideo;
	}

	public boolean isUploadVideo() {
		return uploadVideo;
	}

	public void setUploadVideo(boolean uploadVideo) {
		this.uploadVideo = uploadVideo;
	}

	public List<String> getVideoFormat() {
		return videoFormat;
	}

	public void setVideoFormat(List<String> videoFormat) {
		this.videoFormat = videoFormat;
	}

	public Long getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(Long videoSize) {
		this.videoSize = videoSize;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public boolean isCode() {
		return code;
	}

	public void setCode(boolean code) {
		this.code = code;
	}

	public boolean isMention() {
		return mention;
	}

	public void setMention(boolean mention) {
		this.mention = mention;
	}

	public boolean isAi() {
		return ai;
	}

	public void setAi(boolean ai) {
		this.ai = ai;
	}

}
