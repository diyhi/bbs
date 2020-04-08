package cms.bean;

import java.io.Serializable;

/**
 * 媒体信息
 * @author Gao
 *
 */
public class MediaInfo implements Serializable{
	private static final long serialVersionUID = -4032600666415370715L;

	/** 媒体文件URL **/
	private String mediaUrl;
	/** 封面文件 **/
	private String cover;
	
	/** 缩略图文件 **/
	private String thumbnail;

	public String getMediaUrl() {
		return mediaUrl;
	}

	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	
	
}
