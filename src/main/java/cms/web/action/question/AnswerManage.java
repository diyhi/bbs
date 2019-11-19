package cms.web.action.question;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.setting.EditorTag;
import cms.service.question.AnswerService;
import cms.utils.JsonUtils;
import cms.web.action.setting.SettingManage;


@Component("answerManage")
public class AnswerManage {
	@Resource SettingManage settingManage;
	@Resource AnswerService answerService;
	
	
	/**
	 * 答案编辑器允许使用标签
	 * @return List<String> 类型json格式
	*/
	public String availableTag(){
		List<String> tag = new ArrayList<String>();
		EditorTag editor = settingManage.readAnswerEditorTag();
		if(editor != null){
			//标签区域1
			boolean area_1 = false;
			//标签区域2
			boolean area_2 = false;
			//标签区域3
			boolean area_3 = false;
			
			tag.add("source");
			tag.add("|");
			if(editor.isFontname()){//字体
				tag.add("fontname");
				area_1 = true;
			}
			if(editor.isFontsize()){//文字大小
				tag.add("fontsize");
				area_1 = true;
			}
			if(area_1 == true){
				tag.add("|");
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
				tag.add("|");
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
				tag.add("|");
			}
			
			if(editor.isEmoticons()){//插入表情
				tag.add("emoticons");
			}
			if(editor.isImage()){//图片
				tag.add("image");
			}
		}
		return JsonUtils.toJSONString(tag);
	}
	
	
	/**
	 * 查询答案缓存
	 * @param answerId 答案Id
	 */
	@Cacheable(value="answerManage_cache_findByAnswerId",key="#answerId")
	public Answer query_cache_findByAnswerId(Long answerId){
		return answerService.findByAnswerId(answerId);
	}
	/**
	 * 删除答案缓存
	 * @param answerId 答案Id
	 */
	@CacheEvict(value="answerManage_cache_findByAnswerId",key="#answerId")
	public void delete_cache_findByAnswerId(Long answerId){

	}
	
	/**
	 * 查询答案回复缓存
	 * @param answerReplyId 答案回复Id
	 */
	@Cacheable(value="answerManage_cache_findReplyByReplyId",key="#answerReplyId")
	public AnswerReply query_cache_findReplyByReplyId(Long answerReplyId){
		return answerService.findReplyByReplyId(answerReplyId);
	}
	/**
	 * 删除答案回复缓存
	 * @param answerReplyId 答案回复Id
	 */
	@CacheEvict(value="answerManage_cache_findReplyByReplyId",key="#answerReplyId")
	public void delete_cache_findReplyByReplyId(Long answerReplyId){
	}
	
	/**
   	 * 查询缓存 查询回答数量
   	 * @param userName 用户名称
   	 * @return
   	 */
   	@Cacheable(value="answerManage_cache_answerCount",key="#userName")
   	public Long query_cache_answerCount(String userName){
   		
   		return answerService.findAnswerCountByUserName(userName);
   	}
   	/**
   	 * 删除缓存 查询回答数量
   	 * @param userName 用户名称
   	 * @return
   	 */
   	@CacheEvict(value="answerManage_cache_answerCount",key="#userName")
   	public void delete_cache_answerCount(String userName){
   	}
}
