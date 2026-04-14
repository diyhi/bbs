package cms.component.topic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import cms.component.JsonComponent;
import cms.component.setting.SettingComponent;
import cms.component.user.UserCacheManager;
import cms.model.setting.EditorTag;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.model.user.User;
import cms.repository.topic.CommentRepository;
import cms.repository.user.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 评论组件
 * @author Gao
 *
 */
@Component("commentComponent")
public class CommentComponent {
	@Resource SettingComponent settingComponent;
	@Resource CommentRepository commentRepository;
	@Resource UserCacheManager userCacheManager;
	@Resource UserRepository userRepository;
    @Resource CommentCacheManager commentCacheManager;
    @Resource JsonComponent jsonComponent;
	/**
	 * 评论编辑器允许使用标签
	 * @return List<String> 类型json格式
	*/
	public String availableTag(){
		List<String> tag = new ArrayList<String>();
		EditorTag editor = settingComponent.readEditorTag();
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
			if(area_1){
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
			if(area_2){
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
			if(area_3){
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
		return jsonComponent.toJSONString(tag);
	}
	
	
	/**
	 * 回复编辑器允许使用标签
	 * @return List<String> 类型json格式
	*/
	public String replyAvailableTag(){
		List<String> tag = new ArrayList<String>();
		EditorTag editor = settingComponent.readEditorTag();
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
		return jsonComponent.toJSONString(tag);
	}
	
	
	/**
	 * 回复排序
	 * @param replyList 回复集合
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
     * 修改评论为待审核
     * @param commentId 评论Id
     */
    public void updateCommentPendingReview(Long commentId){
    	int i = commentRepository.updateCommentPendingReview(commentId);
		
		Comment comment = commentCacheManager.query_cache_findByCommentId(commentId);
		if(i >0 && comment != null){
			User user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
			if(user != null){
				//修改评论状态
				userRepository.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),10);
			}
		}
		
		//删除缓存
        commentCacheManager.delete_cache_findByCommentId(commentId);
    	
    }
	
    /**
     * 修改回复为待审核
     * @param replyId 回复Id
     */
    public void updateReplyPendingReview(Long replyId){
    	int i = commentRepository.updateReplyPendingReview(replyId);
		
		Reply reply = commentCacheManager.query_cache_findReplyByReplyId(replyId);
		if(reply != null){
			User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
			if(i >0 && user != null){
				//修改回复状态
				userRepository.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),10);
			}
		}
		
		//删除缓存
        commentCacheManager.delete_cache_findReplyByReplyId(replyId);
    	
    }
    
    /**
     * 标记删除评论
     * @param commentId 评论Id
     */
    public void markDeleteComment(Long commentId){
    	Comment comment = commentCacheManager.query_cache_findByCommentId(commentId);
    	if(comment != null && comment.getStatus() <100){
    		Integer constant = 100000;
    		int i = commentRepository.markDeleteComment(commentId,constant);
    		
    		if(i >0){
    			User user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
    			if(user != null){
    				//修改评论状态
    				userRepository.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),comment.getStatus()+constant);
    			}
    			//删除缓存
                commentCacheManager.delete_cache_findByCommentId(comment.getId());
    		}
    	}
    }
    
    /**
     * 标记删除评论回复
     * @param replyId 回复Id
     */
    public void markDeleteReply(Long replyId){
    	Reply reply = commentCacheManager.query_cache_findReplyByReplyId(replyId);
    	if(reply != null && reply.getStatus() <100){
    		Integer constant = 100000;
    		int i = commentRepository.markDeleteReply(replyId,constant);
    		
    		
    		if(i >0){
    			User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
    			if(user != null){
    				//修改回复状态
    				userRepository.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),reply.getStatus()+constant);
    			}
    			//删除缓存
                commentCacheManager.delete_cache_findReplyByReplyId(reply.getId());
    		}
        	
    	}
    	
    }
    

}
