package cms.web.action.setting;

import java.math.BigDecimal;
import java.util.List;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import cms.bean.setting.SystemSetting;
import cms.utils.CommentedProperties;

@Component("systemSettingValidator")
public class SystemSettingValidator implements Validator{
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(SystemSetting.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		SystemSetting systemSetting = (SystemSetting)obj;
		
		//站点名称
		if(systemSetting.getTitle() != null && !"".equals(systemSetting.getTitle())){
			if(systemSetting.getTitle().length() >200){
				errors.rejectValue("title","errors.required", new String[]{"不能超过200个字符"},"");
			}
		}
		//站点关键词
		if(systemSetting.getKeywords() != null && !"".equals(systemSetting.getKeywords())){
			if(systemSetting.getKeywords().length() >200){
				errors.rejectValue("keywords","errors.required", new String[]{"不能超过200个字符"},"");
			}
		}
		//站点描述
		if(systemSetting.getDescription() != null && !"".equals(systemSetting.getDescription())){
			if(systemSetting.getDescription().length() >200){
				errors.rejectValue("description","errors.required", new String[]{"不能超过200个字符"},"");
			}
		}

		//登录密码每分钟连续错误
		if(systemSetting.getLogin_submitQuantity() != null){
			if(systemSetting.getLogin_submitQuantity() <0){
				errors.rejectValue("login_submitQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("login_submitQuantity","errors.required", new String[]{"不能为空"},"");
		}
		//发表话题每分钟提交超过
		if(systemSetting.getTopic_submitQuantity() != null){
			if(systemSetting.getTopic_submitQuantity() <0){
				errors.rejectValue("topic_submitQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("topic_submitQuantity","errors.required", new String[]{"不能为空"},"");
		}
		//发表评论每分钟提交超过
		if(systemSetting.getComment_submitQuantity() != null){
			if(systemSetting.getComment_submitQuantity() <0){
				errors.rejectValue("comment_submitQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("comment_submitQuantity","errors.required", new String[]{"不能为空"},"");
		}
		//发表问题每分钟提交超过
		if(systemSetting.getQuestion_submitQuantity() != null){
			if(systemSetting.getQuestion_submitQuantity() <0){
				errors.rejectValue("question_submitQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("question_submitQuantity","errors.required", new String[]{"不能为空"},"");
		}
		//发表答案每分钟提交超过
		if(systemSetting.getAnswer_submitQuantity() != null){
			if(systemSetting.getAnswer_submitQuantity() <0){
				errors.rejectValue("answer_submitQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("answer_submitQuantity","errors.required", new String[]{"不能为空"},"");
		}
		//发表私信每分钟提交超过
		if(systemSetting.getPrivateMessage_submitQuantity() != null){
			if(systemSetting.getPrivateMessage_submitQuantity() <0){
				errors.rejectValue("privateMessage_submitQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("privateMessage_submitQuantity","errors.required", new String[]{"不能为空"},"");
		}
		
		//提交问题最多可选择标签数量
		if(systemSetting.getMaxQuestionTagQuantity() != null){
			if(systemSetting.getMaxQuestionTagQuantity() <0){
				errors.rejectValue("maxQuestionTagQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("maxQuestionTagQuantity","errors.required", new String[]{"不能为空"},"");
		}
		
		//发表话题奖励积分
		if(systemSetting.getTopic_rewardPoint() != null){
			if(systemSetting.getTopic_rewardPoint() <0L){
				errors.rejectValue("topic_rewardPoint","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("topic_rewardPoint","errors.required", new String[]{"不能为空"},"");
		}
		//发表评论奖励积分
		if(systemSetting.getComment_rewardPoint() != null){
			if(systemSetting.getComment_rewardPoint() <0L){
				errors.rejectValue("comment_rewardPoint","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("comment_rewardPoint","errors.required", new String[]{"不能为空"},"");
		}
		//发表回复奖励积分
		if(systemSetting.getReply_rewardPoint() != null){
			if(systemSetting.getReply_rewardPoint() <0L){
				errors.rejectValue("reply_rewardPoint","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("reply_rewardPoint","errors.required", new String[]{"不能为空"},"");
		}
		
		//提交问题奖励积分
		if(systemSetting.getQuestion_rewardPoint() != null){
			if(systemSetting.getQuestion_rewardPoint() <0L){
				errors.rejectValue("question_rewardPoint","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("question_rewardPoint","errors.required", new String[]{"不能为空"},"");
		}
		//提交答案奖励积分
		if(systemSetting.getAnswer_rewardPoint() != null){
			if(systemSetting.getAnswer_rewardPoint() <0L){
				errors.rejectValue("answer_rewardPoint","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("answer_rewardPoint","errors.required", new String[]{"不能为空"},"");
		}
		//提交答案回复奖励积分
		if(systemSetting.getAnswerReply_rewardPoint() != null){
			if(systemSetting.getAnswerReply_rewardPoint() <0L){
				errors.rejectValue("answerReply_rewardPoint","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("answerReply_rewardPoint","errors.required", new String[]{"不能为空"},"");
		}
		
		
		//问题悬赏积分下限
		if(systemSetting.getQuestionRewardPointMin() != null){
			if(systemSetting.getQuestionRewardPointMin() <0L){
				errors.rejectValue("questionRewardPointMin","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("questionRewardPointMin","errors.required", new String[]{"不能为空"},"");
		}
		//问题悬赏积分上限 空为无限制 0则不允许悬赏积分
		if(systemSetting.getQuestionRewardPointMax() != null){
			if(systemSetting.getQuestionRewardPointMax() <0L){
				errors.rejectValue("questionRewardPointMax","errors.required", new String[]{"必须大于或等于0"},"");
			}
			if(systemSetting.getQuestionRewardPointMax() >999999999999999L){
				errors.rejectValue("questionRewardPointMax","errors.required", new String[]{"不能大于999999999999999"},"");
			}
			if(systemSetting.getQuestionRewardPointMax() > 0L && systemSetting.getQuestionRewardPointMin() != null){
				if(systemSetting.getQuestionRewardPointMin() >=systemSetting.getQuestionRewardPointMax()){
					errors.rejectValue("questionRewardPointMax","errors.required", new String[]{"不能小于或等于问题悬赏积分下限"},"");
				}
			}
		}
		//问题悬赏金额下限
		if(systemSetting.getQuestionRewardAmountMin() != null){
			if(systemSetting.getQuestionRewardAmountMin().compareTo(new BigDecimal("0")) <0){
				errors.rejectValue("questionRewardAmountMin","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("questionRewardAmountMin","errors.required", new String[]{"不能为空"},"");
		}
		//问题悬赏金额上限
		if(systemSetting.getQuestionRewardAmountMax() != null){
			if(systemSetting.getQuestionRewardAmountMax().compareTo(new BigDecimal("0")) <0){
				errors.rejectValue("questionRewardAmountMax","errors.required", new String[]{"必须大于或等于0"},"");
			}
			if(systemSetting.getQuestionRewardAmountMax().compareTo(new BigDecimal("99999999")) >0){
				errors.rejectValue("questionRewardAmountMax","errors.required", new String[]{"不能大于99999999"},"");
			}
			if(systemSetting.getQuestionRewardAmountMax().compareTo(new BigDecimal("0")) >0 && systemSetting.getQuestionRewardAmountMin() != null){
				if(systemSetting.getQuestionRewardAmountMin().compareTo(systemSetting.getQuestionRewardAmountMax()) >=0){
					errors.rejectValue("questionRewardAmountMax","errors.required", new String[]{"不能小于或等于问题悬赏金额下限"},"");
				}
			}
		}
		
		//发红包金额下限
		if(systemSetting.getGiveRedEnvelopeAmountMin() != null){
			if(systemSetting.getGiveRedEnvelopeAmountMin().compareTo(new BigDecimal("0.01")) <0){
				errors.rejectValue("giveRedEnvelopeAmountMin","errors.required", new String[]{"必须大于或等于0.01"},"");
			}
		}else{
			errors.rejectValue("giveRedEnvelopeAmountMin","errors.required", new String[]{"不能为空"},"");
		}
		//发红包金额上限
		if(systemSetting.getGiveRedEnvelopeAmountMax() != null){
			if(systemSetting.getGiveRedEnvelopeAmountMax().compareTo(new BigDecimal("0")) <0){
				errors.rejectValue("giveRedEnvelopeAmountMax","errors.required", new String[]{"必须大于或等于0"},"");
			}
			if(systemSetting.getGiveRedEnvelopeAmountMax().compareTo(new BigDecimal("99999999")) >0){
				errors.rejectValue("giveRedEnvelopeAmountMax","errors.required", new String[]{"不能大于99999999"},"");
			}
			if(systemSetting.getGiveRedEnvelopeAmountMax().compareTo(new BigDecimal("0")) >0 && systemSetting.getGiveRedEnvelopeAmountMin() != null){
				if(systemSetting.getGiveRedEnvelopeAmountMin().compareTo(systemSetting.getGiveRedEnvelopeAmountMax()) >0){
					errors.rejectValue("giveRedEnvelopeAmountMax","errors.required", new String[]{"不能小于发红包金额下限"},"");
				}
			}
		}

		//解锁话题隐藏内容平台分成比例
		if(systemSetting.getTopicUnhidePlatformShareProportion() != null){
			if(systemSetting.getTopicUnhidePlatformShareProportion() <0){
				errors.rejectValue("topicUnhidePlatformShareProportion","errors.required", new String[]{"必须大于或等于0"},"");
			}
			if(systemSetting.getTopicUnhidePlatformShareProportion() >100){
				errors.rejectValue("topicUnhidePlatformShareProportion","errors.required", new String[]{"必须小于或等于100"},"");
			}
		}else{
			errors.rejectValue("topicUnhidePlatformShareProportion","errors.required", new String[]{"不能为空"},"");
		}
		//悬赏问答平台分成比例
		if(systemSetting.getQuestionRewardPlatformShareProportion() != null){
			if(systemSetting.getQuestionRewardPlatformShareProportion() <0){
				errors.rejectValue("questionRewardPlatformShareProportion","errors.required", new String[]{"必须大于或等于0"},"");
			}
			if(systemSetting.getQuestionRewardPlatformShareProportion() >100){
				errors.rejectValue("questionRewardPlatformShareProportion","errors.required", new String[]{"必须小于或等于100"},"");
			}
		}else{
			errors.rejectValue("questionRewardPlatformShareProportion","errors.required", new String[]{"不能为空"},"");
		}
		//文件防盗链密钥
		if(systemSetting.getFileSecureLinkSecret() != null && !"".equals(systemSetting.getFileSecureLinkSecret().trim())){
			if(systemSetting.getFileSecureLinkSecret().trim().length() != 16){
				errors.rejectValue("fileSecureLinkSecret","errors.required", new String[]{"密钥必须为16个字符"},"");
			}
		}
		//文件防盗链过期时间
		if(systemSetting.getFileSecureLinkExpire() != null){
			if(systemSetting.getFileSecureLinkExpire() <=0){
				errors.rejectValue("fileSecureLinkExpire","errors.required", new String[]{"不能小于或等于0"},"");
			}
		}else{
			errors.rejectValue("fileSecureLinkExpire","errors.required", new String[]{"不能为空"},"");
		}
		
		//前台分页数量
		if(systemSetting.getForestagePageNumber() != null){
			if(systemSetting.getForestagePageNumber() <=0){
				errors.rejectValue("forestagePageNumber","errors.required", new String[]{"不能小于或等于0"},"");
			}
		}else{
			errors.rejectValue("forestagePageNumber","errors.required", new String[]{"不能为空"},"");
		}
		
		//后台分页数量
		if(systemSetting.getBackstagePageNumber() != null){
			if(systemSetting.getBackstagePageNumber() <=0){
				errors.rejectValue("backstagePageNumber","errors.required", new String[]{"不能小于或等于0"},"");
			}
		}
	
		
		//上传临时文件有效期
		if(systemSetting.getTemporaryFileValidPeriod() != null){
			if(systemSetting.getTemporaryFileValidPeriod() <=0){
				errors.rejectValue("temporaryFileValidPeriod","errors.required", new String[]{"必须大于0"},"");
			}
		}else{
			errors.rejectValue("temporaryFileValidPeriod","errors.required", new String[]{"不能为空"},"");
		}
		if(systemSetting.getUserSentSmsCount() != null){
			if(systemSetting.getUserSentSmsCount() <=0){
				errors.rejectValue("userSentSmsCount","errors.required", new String[]{"必须大于0"},"");
			}
		}
		//话题编辑器标签
		if(systemSetting.getTopicEditorTagObject() != null){
			
			if(systemSetting.getTopicEditorTagObject().isUploadVideo()){//上传视频
				if(systemSetting.getTopicEditorTagObject().getVideoSize() == null || systemSetting.getTopicEditorTagObject().getVideoSize() <=0){
					errors.rejectValue("topicEditorTagObject.videoSize","errors.required", new String[]{"必须大于0"},"");
				}
			}
			
			if(systemSetting.getTopicEditorTagObject().isImage()){//图片
				if(systemSetting.getTopicEditorTagObject().getImageSize() == null || systemSetting.getTopicEditorTagObject().getImageSize() <=0){
					errors.rejectValue("topicEditorTagObject.imageSize","errors.required", new String[]{"必须大于0"},"");
				}
			}
			if(systemSetting.getTopicEditorTagObject().isFile()){//文件
				if(systemSetting.getTopicEditorTagObject().getFileSize() == null || systemSetting.getTopicEditorTagObject().getFileSize() <=0){
					errors.rejectValue("topicEditorTagObject.fileSize","errors.required", new String[]{"必须大于0"},"");
				}
			}
			//允许上传图片格式
			List<String> imageFormatList = systemSetting.getTopicEditorTagObject().getImageFormat();
			if(imageFormatList != null && imageFormatList.size() >0){
				List<String> imageUploadFormatList = CommentedProperties.readRichTextAllowImageUploadFormat();
				if(imageUploadFormatList != null && imageUploadFormatList.size() >0){
					A:for(String imageFormat : imageFormatList){
						if(imageFormat != null && !"".equals(imageFormat.trim())){
							for(String imageUploadFormat : imageUploadFormatList){
								if(imageUploadFormat != null && imageFormat.trim().equals(imageUploadFormat.trim())){
									continue A;
								}
							}
						}
						errors.rejectValue("topicEditorTagObject.imageFormat","errors.required", new String[]{imageFormat+"格式不属于预设格式"},"");
					}
				}
			}
			//允许上传文件格式
			List<String> fileFormatList = systemSetting.getTopicEditorTagObject().getFileFormat();
			if(fileFormatList != null && fileFormatList.size() >0){
				List<String> fileUploadFormatList = CommentedProperties.readRichTextAllowFileUploadFormat();
				if(fileUploadFormatList != null && fileUploadFormatList.size() >0){
					A:for(String fileFormat : fileFormatList){
						if(fileFormat != null && !"".equals(fileFormat.trim())){
							for(String fileUploadFormat : fileUploadFormatList){
								if(fileUploadFormat != null && fileFormat.trim().equals(fileUploadFormat.trim())){
									continue A;
								}
							}
						}
						errors.rejectValue("topicEditorTagObject.fileFormat","errors.required", new String[]{fileFormat+"格式不属于预设格式"},"");
					}
				}
			}	
			//允许上传视频格式
			List<String> videoFormatList = systemSetting.getTopicEditorTagObject().getVideoFormat();
			if(videoFormatList != null && videoFormatList.size() >0){
				List<String> videoUploadFormatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
				if(videoUploadFormatList != null && videoUploadFormatList.size() >0){
					A:for(String videoFormat : videoFormatList){
						if(videoFormat != null && !"".equals(videoFormat.trim())){
							for(String videoUploadFormat : videoUploadFormatList){
								if(videoUploadFormat != null && videoFormat.trim().equals(videoUploadFormat.trim())){
									continue A;
								}
							}
						}
						errors.rejectValue("topicEditorTagObject.videoFormat","errors.required", new String[]{videoFormat+"格式不属于预设格式"},"");
					}
				}
			}
		}
		//评论编辑器标签
		if(systemSetting.getEditorTagObject() != null){
			if(systemSetting.getEditorTagObject().isImage()){//图片
				if(systemSetting.getEditorTagObject().getImageSize() == null || systemSetting.getEditorTagObject().getImageSize() <=0){
					errors.rejectValue("editorTagObject.imageSize","errors.required", new String[]{"必须大于0"},"");
				}
			}
			//允许上传图片格式
			List<String> imageFormatList = systemSetting.getEditorTagObject().getImageFormat();
			if(imageFormatList != null && imageFormatList.size() >0){
				List<String> imageUploadFormatList = CommentedProperties.readRichTextAllowImageUploadFormat();
				if(imageUploadFormatList != null && imageUploadFormatList.size() >0){
					A:for(String imageFormat : imageFormatList){
						if(imageFormat != null && !"".equals(imageFormat.trim())){
							for(String imageUploadFormat : imageUploadFormatList){
								if(imageUploadFormat != null && imageFormat.trim().equals(imageUploadFormat.trim())){
									continue A;
								}
							}
						}
						errors.rejectValue("editorTagObject.imageFormat","errors.required", new String[]{imageFormat+"格式不属于预设格式"},"");
					}
				}
			}
		}
		//问题编辑器标签
		if(systemSetting.getQuestionEditorTagObject() != null){
			if(systemSetting.getQuestionEditorTagObject().isImage()){//图片
				if(systemSetting.getQuestionEditorTagObject().getImageSize() == null || systemSetting.getQuestionEditorTagObject().getImageSize() <=0){
					errors.rejectValue("questionEditorTagObject.imageSize","errors.required", new String[]{"必须大于0"},"");
				}
			}
			//允许上传图片格式
			List<String> imageFormatList = systemSetting.getQuestionEditorTagObject().getImageFormat();
			if(imageFormatList != null && imageFormatList.size() >0){
				List<String> imageUploadFormatList = CommentedProperties.readRichTextAllowImageUploadFormat();
				if(imageUploadFormatList != null && imageUploadFormatList.size() >0){
					A:for(String imageFormat : imageFormatList){
						if(imageFormat != null && !"".equals(imageFormat.trim())){
							for(String imageUploadFormat : imageUploadFormatList){
								if(imageUploadFormat != null && imageFormat.trim().equals(imageUploadFormat.trim())){
									continue A;
								}
							}
						}
						errors.rejectValue("questionEditorTagObject.imageFormat","errors.required", new String[]{imageFormat+"格式不属于预设格式"},"");
					}
				}
			}
		}
		//答案编辑器标签
		if(systemSetting.getAnswerEditorTagObject() != null){
			if(systemSetting.getAnswerEditorTagObject().isImage()){//图片
				if(systemSetting.getAnswerEditorTagObject().getImageSize() == null || systemSetting.getAnswerEditorTagObject().getImageSize() <=0){
					errors.rejectValue("answerEditorTagObject.imageSize","errors.required", new String[]{"必须大于0"},"");
				}
			}
			//允许上传图片格式
			List<String> imageFormatList = systemSetting.getAnswerEditorTagObject().getImageFormat();
			if(imageFormatList != null && imageFormatList.size() >0){
				List<String> imageUploadFormatList = CommentedProperties.readRichTextAllowImageUploadFormat();
				if(imageUploadFormatList != null && imageUploadFormatList.size() >0){
					A:for(String imageFormat : imageFormatList){
						if(imageFormat != null && !"".equals(imageFormat.trim())){
							for(String imageUploadFormat : imageUploadFormatList){
								if(imageUploadFormat != null && imageFormat.trim().equals(imageUploadFormat.trim())){
									continue A;
								}
							}
						}
						errors.rejectValue("answerEditorTagObject.imageFormat","errors.required", new String[]{imageFormat+"格式不属于预设格式"},"");
					}
				}
			}
		}
		
	}
}
