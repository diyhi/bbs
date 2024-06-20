package cms.web.action.topic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import cms.bean.setting.EditorTag;
import cms.bean.topic.Comment;
import cms.bean.topic.Reply;
import cms.service.topic.CommentService;
import cms.utils.JsonUtils;
import cms.web.action.setting.SettingManage;


@Component("commentManage")
public class CommentManage {
	@Resource SettingManage settingManage;
	@Resource CommentService commentService;
	
	
	/**
	 * 评论编辑器允许使用标签
	 * @return List<String> 类型json格式
	*/
	public String availableTag(){
		List<String> tag = new ArrayList<String>();
		EditorTag editor = settingManage.readEditorTag();
		if(editor != null){
			//标签区域1
			boolean area_1 = false;
			//标签区域2
			boolean area_2 = false;
			//标签区域3
			boolean area_3 = false;
			
			tag.add("source");
		//	tag.add("|");
			if(editor.isCode()){//代码
				tag.add("code");
				area_1 = true;
			}
			if(editor.isFontname()){//字体
				tag.add("fontname");
				area_1 = true;
			}
			if(editor.isFontsize()){//文字大小
				tag.add("fontsize");
				area_1 = true;
			}
			if(area_1 == true){
		//		tag.add("|");
			}
			
			if(editor.isForecolor()){//文字颜色
				tag.add("forecolor");
				area_2 = true;
			}
			if(editor.isHilitecolor()){//文字背景
				tag.add("hilitecolor");
				area_2 = true;
			}
			if(editor.isBold()){//粗体
				tag.add("bold");
				area_2 = true;
			}
			if(editor.isItalic()){//斜体
				tag.add("italic");
				area_2 = true;
			}
			if(editor.isUnderline()){//下划线
				tag.add("underline");
				area_2 = true;
			}
			if(editor.isRemoveformat()){//删除格式
				tag.add("removeformat");
				area_2 = true;
			}
			if(editor.isLink()){//超级链接
				tag.add("link");
				area_2 = true;
			}
			if(editor.isUnlink()){//取消超级链接
				tag.add("unlink");
				area_2 = true;
			}
			if(area_2 == true){
			//	tag.add("|");
			}
			
			if(editor.isJustifyleft()){//左对齐
				tag.add("justifyleft");
				area_3 = true;
			}
			if(editor.isJustifycenter()){//居中
				tag.add("justifycenter");
				area_3 = true;
			}
			if(editor.isJustifyright()){//右对齐
				tag.add("justifyright");
				area_3 = true;
			}
			if(editor.isInsertorderedlist()){//编号
				tag.add("insertorderedlist");
				area_3 = true;
			}
			if(editor.isInsertunorderedlist()){//项目符号
				tag.add("insertunorderedlist");
				area_3 = true;
			}
			if(area_3 == true){
			//	tag.add("|");
			}
			
			if(editor.isEmoticons()){//插入表情
				tag.add("emoticons");
			}
			if(editor.isImage()){//图片
				tag.add("image");
			}
			if(editor.isMention()){//提及
				tag.add("mention");
			}
			if(editor.isAi()){//人工智能
				tag.add("ai");
			}
			if(editor.isFullscreen()){//全屏显示
				tag.add("fullscreen");
			}
		}
		return JsonUtils.toJSONString(tag);
	}
	
	/**
	 * 回复编辑器允许使用标签
	 * @return List<String> 类型json格式
	*/
	public String replyAvailableTag(){
		List<String> tag = new ArrayList<String>();
		EditorTag editor = settingManage.readEditorTag();
		if(editor != null){
			//tag.add("source");
		
			if(editor.isEmoticons()){//插入表情
				tag.add("emoticons");
			}
			if(editor.isMention()){//提及
				tag.add("mention");
			}
			if(editor.isAi()){//人工智能
				tag.add("ai");
			}
		}
		return JsonUtils.toJSONString(tag);
	}
	
	/**
	 * 回复排序
	 * @param replyList
	 */
	public void replySort(List<Reply> replyList){
		Collections.sort(replyList, new Comparator<Reply>(){
			@Override
			public int compare(Reply o1, Reply o2) {
				long s_1 = o1.getId();
				long s_2 = o2.getId();
				if(s_1 <s_2){
        			return -1;
        			
        		}else{
        			if(s_1 == s_2){
            			return 0;
            		}else{
            			return 1;
            		}
        		}  
			}   
		});
	}
	
	/**
	 * 查询评论缓存
	 * @param commentId 评论Id
	 */
	@Cacheable(value="commentManage_cache_findByCommentId",key="#commentId")
	public Comment query_cache_findByCommentId(Long commentId){
		return commentService.findByCommentId(commentId);
	}
	/**
	 * 删除评论缓存
	 * @param commentId 评论Id
	 */
	@CacheEvict(value="commentManage_cache_findByCommentId",key="#commentId")
	public void delete_cache_findByCommentId(Long commentId){

	}
	
	/**
	 * 查询回复缓存
	 * @param replyId 回复Id
	 */
	@Cacheable(value="commentManage_cache_findReplyByReplyId",key="#replyId")
	public Reply query_cache_findReplyByReplyId(Long replyId){
		return commentService.findReplyByReplyId(replyId);
	}
	/**
	 * 删除回复缓存
	 * @param replyId 回复Id
	 */
	@CacheEvict(value="commentManage_cache_findReplyByReplyId",key="#replyId")
	public void delete_cache_findReplyByReplyId(Long replyId){
	}
}
