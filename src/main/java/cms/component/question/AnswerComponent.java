package cms.component.question;

import cms.component.JsonComponent;
import cms.component.setting.SettingComponent;
import cms.component.user.UserCacheManager;
import cms.component.user.UserComponent;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.setting.EditorTag;
import cms.model.user.User;
import cms.repository.question.AnswerRepository;
import cms.repository.user.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 答案组件
 * @author Gao
 *
 */
@Component("answerComponent")
public class AnswerComponent {
    @Resource
    SettingComponent settingComponent;
    @Resource
    AnswerRepository answerRepository;
    @Resource
    UserComponent userComponent;
    @Resource
    UserRepository userRepository;
    @Autowired
    private AnswerCacheManager answerCacheManager;
    @Autowired
    private UserCacheManager userCacheManager;
    @Resource
    JsonComponent jsonComponent;

    /**
     * 答案编辑器允许使用标签
     * @return List<String> 类型json格式
     */
    public String availableTag(){
        List<String> tag = new ArrayList<String>();
        EditorTag editor = settingComponent.readAnswerEditorTag();
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
                //	tag.add("|");
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
        EditorTag editor = settingComponent.readAnswerEditorTag();
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
     * @param replyList 回复列表
     */
    public void replySort(List<AnswerReply> replyList){
        Collections.sort(replyList, new Comparator<AnswerReply>(){
            @Override
            public int compare(AnswerReply o1, AnswerReply o2) {
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
     * 修改答案为待审核
     * @param answerId 答案Id
     */
    public void updateAnswerPendingReview(Long answerId){
        int i = answerRepository.updateAnswerPendingReview(answerId);

        Answer answer = answerCacheManager.query_cache_findByAnswerId(answerId);
        if(i >0 && answer != null){
            User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
            if(user != null){
                //修改答案状态
                userRepository.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),10);
            }
            //删除缓存
            answerCacheManager.delete_cache_findByAnswerId(answerId);
            answerCacheManager.delete_cache_answerCount(answer.getUserName());
        }
    }

    /**
     * 修改答案回复为待审核
     * @param answerReplyId 答案回复Id
     */
    public void updateAnswerReplyPendingReview(Long answerReplyId){
        int i = answerRepository.updateAnswerReplyPendingReview(answerReplyId);

        AnswerReply answerReply = answerCacheManager.query_cache_findReplyByReplyId(answerReplyId);
        if(answerReply != null){
            User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
            if(i >0 && user != null){
                //修改答案回复状态
                userRepository.updateUserDynamicAnswerReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),10);
            }
        }

        //删除缓存
        answerCacheManager.delete_cache_findReplyByReplyId(answerReplyId);

    }

    /**
     * 标记删除答案
     * @param answerId 答案Id
     */
    public void markDeleteAnswer(Long answerId){
        Answer answer = answerCacheManager.query_cache_findByAnswerId(answerId);
        if(answer != null && answer.getStatus() <100){
            Integer constant = 100000;
            int i = answerRepository.markDeleteAnswer(answer.getId(),constant);

            if(i >0){
                User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
                if(user != null){
                    //修改答案状态
                    userRepository.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),answer.getStatus()+constant);
                }
                //删除缓存
                answerCacheManager.delete_cache_findByAnswerId(answer.getId());
            }

        }
    }

    /**
     * 标记删除答案回复
     * @param answerReplyId 答案回复Id
     */
    public void markDeleteAnswerReply(Long answerReplyId){
        AnswerReply answerReply = answerCacheManager.query_cache_findReplyByReplyId(answerReplyId);
        if(answerReply != null && answerReply.getStatus() <100){
            Integer constant = 100000;
            int i = answerRepository.markDeleteReply(answerReply.getId(),constant);


            if(i >0){
                User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
                if(user != null){
                    //修改回复状态
                    userRepository.updateUserDynamicAnswerReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),answerReply.getStatus()+constant);
                }
                //删除缓存
                answerCacheManager.delete_cache_findReplyByReplyId(answerReply.getId());
            }
        }
    }
}
