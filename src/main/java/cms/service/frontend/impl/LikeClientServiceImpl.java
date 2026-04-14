package cms.service.frontend.impl;

import cms.component.TextFilterComponent;
import cms.component.filterWord.SensitiveWordFilterComponent;
import cms.component.like.LikeCacheManager;
import cms.component.like.LikeComponent;
import cms.component.like.LikeConfig;
import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.question.AnswerCacheManager;
import cms.component.question.QuestionCacheManager;
import cms.component.topic.CommentCacheManager;
import cms.component.topic.HotTopicComponent;
import cms.component.topic.TopicCacheManager;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.frontendModule.*;
import cms.dto.riskControl.BlockedFeaturesEnum;
import cms.dto.user.AccessUser;
import cms.model.like.*;
import cms.model.message.Remind;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.model.setting.SystemSetting;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.like.LikeRepository;
import cms.repository.message.RemindRepository;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.LikeClientService;
import cms.utils.AccessUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * 前台点赞服务
 */
@Service
public class LikeClientServiceImpl implements LikeClientService {

    @Resource
    LikeCacheManager likeCacheManager;
    @Resource
    LikeConfig likeConfig;
    @Resource
    SettingRepository settingRepository;
    @Resource RemindCacheManager remindCacheManager;
    @Resource UserCacheManager userCacheManager;
    @Resource TopicCacheManager topicCacheManager;
    @Resource CommentCacheManager commentCacheManager;
    @Resource QuestionCacheManager questionCacheManager;
    @Resource AnswerCacheManager answerCacheManager;
    @Resource
    LikeComponent likeComponent;
    @Resource
    HotTopicComponent hotTopicComponent;
    @Resource
    LikeRepository likeRepository;
    @Resource
    RemindConfig remindConfig;
    @Resource
    RemindComponent remindComponent;
    @Resource
    RemindRepository remindRepository;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    SensitiveWordFilterComponent sensitiveWordFilterComponent;

    /**
     * 获取点赞总数
     *
     * @param itemId 项Id
     * @param module 模块 10:话题 20:评论 30:评论回复 40:问题 50:答案 60:答案回复
     * @return
     */
    public LikeCountDTO getTotalLikes(Long itemId, Integer module){
        Long count = 0L;
        if (itemId == null || itemId <= 0L || module == null) {
            return new LikeCountDTO(count);
        }
        switch (module) {
            case 10:
                // 话题
                count = likeCacheManager.query_cache_findLikeCountByTopicId(itemId);
                break;
            case 20:
                // 评论
                count = likeCacheManager.query_cache_findLikeCountByCommentId(itemId);
                break;
            case 30:
                // 评论回复
                count = likeCacheManager.query_cache_findLikeCountByCommentReplyId(itemId);
                break;
            case 40:
                // 问题
                count = likeCacheManager.query_cache_findLikeCountByQuestionId(itemId);
                break;
            case 50:
                // 答案
                count = likeCacheManager.query_cache_findLikeCountByAnswerId(itemId);
                break;
            case 60:
                // 答案回复
                count = likeCacheManager.query_cache_findLikeCountByAnswerReplyId(itemId);
                break;
            default:
                break;
        }
        return new LikeCountDTO(count != null ? count : 0L);
    }

    /**
     * 用户是否已经点赞该项
     *
     * @param itemId 项Id
     * @param module 模块 10:话题 20:评论 30:评论回复 40:问题 50:答案 60:答案回复
     * @return
     */
    public UserLikeStatusDTO hasUserLiked(Long itemId, Integer module){
        AccessUser accessUser = AccessUserThreadLocal.get();

        if(accessUser != null && module != null && itemId != null && itemId>0L){
            //项目收藏Id
            String itemLikeId = likeConfig.createItemLikeId(itemId, accessUser.getUserId());

            if(module.equals(10)){
                TopicLike topicLike = likeCacheManager.query_cache_findTopicLikeById(itemLikeId);
                if(topicLike != null){
                    return new UserLikeStatusDTO(true);
                }
            }else if(module.equals(20)){
                CommentLike commentLike = likeCacheManager.query_cache_findCommentLikeById(itemLikeId);
                if(commentLike != null){
                    return new UserLikeStatusDTO(true);
                }
            }else if(module.equals(30)){
                CommentReplyLike commentReplyLike = likeCacheManager.query_cache_findCommentReplyLikeById(itemLikeId);
                if(commentReplyLike != null){
                    return new UserLikeStatusDTO(true);
                }
            }else if(module.equals(40)){
                QuestionLike questionLike = likeCacheManager.query_cache_findQuestionLikeById(itemLikeId);
                if(questionLike != null){
                    return new UserLikeStatusDTO(true);
                }
            }else if(module.equals(50)){
                AnswerLike answerLike = likeCacheManager.query_cache_findAnswerLikeById(itemLikeId);
                if(answerLike != null){
                    return new UserLikeStatusDTO(true);
                }
            }else if(module.equals(60)){
                AnswerReplyLike answerReplyLike = likeCacheManager.query_cache_findAnswerReplyLikeById(itemLikeId);
                if(answerReplyLike != null){
                    return new UserLikeStatusDTO(true);
                }
            }

        }
        return new UserLikeStatusDTO(false);
    }

    /**
     * 添加点赞
     * @param itemId   项目Id 例如：话题Id
     * @param module   模块
     * @return
     */
    public Map<String,Object> addLike(Long itemId, Integer module){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("itemLike", "只读模式不允许提交数据"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        Topic topic = null;
        Comment comment = null;
        Reply commentReply = null;

        Question question = null;
        Answer answer = null;
        AnswerReply answerReply = null;

        LocalDateTime postTime = LocalDateTime.now();

        if(itemId != null && itemId >0L){
            if(module != null){
                if(module.equals(10)){//话题
                    topic = topicCacheManager.queryTopicCache(itemId);//查询缓存

                    if(topic != null){
                        //话题点赞Id
                        String topicLikeId = likeConfig.createItemLikeId(itemId, accessUser.getUserId());

                        TopicLike topicLike = likeCacheManager.query_cache_findTopicLikeById(topicLikeId);

                        if(topicLike != null){
                            errors.put("itemLike", "当前项目已经点赞");
                        }
                    }
                }else if(module.equals(20)){//评论
                    comment = commentCacheManager.query_cache_findByCommentId(itemId);
                    if(comment != null){
                        //评论点赞Id
                        String commentLikeId = likeConfig.createItemLikeId(itemId, accessUser.getUserId());

                        CommentLike commentLike = likeCacheManager.query_cache_findCommentLikeById(commentLikeId);

                        if(commentLike != null){
                            errors.put("itemLike", "当前项目已经点赞");
                        }
                    }
                }else if(module.equals(30)){//评论回复
                    commentReply =  commentCacheManager.query_cache_findReplyByReplyId(itemId);
                    if(commentReply != null){
                        //评论回复点赞Id
                        String commentReplyLikeId = likeConfig.createItemLikeId(itemId, accessUser.getUserId());

                        CommentReplyLike commentReplyLike = likeCacheManager.query_cache_findCommentReplyLikeById(commentReplyLikeId);

                        if(commentReplyLike != null){
                            errors.put("itemLike", "当前项目已经点赞");
                        }
                    }
                }else if(module.equals(40)){//问题
                    question = questionCacheManager.query_cache_findById(itemId);//查询缓存

                    if(question != null){
                        //问题点赞Id
                        String questionLikeId = likeConfig.createItemLikeId(itemId, accessUser.getUserId());

                        QuestionLike questionLike = likeCacheManager.query_cache_findQuestionLikeById(questionLikeId);

                        if(questionLike != null){
                            errors.put("itemLike", "当前项目已经点赞");
                        }
                    }
                }else if(module.equals(50)){//答案
                    answer = answerCacheManager.query_cache_findByAnswerId(itemId);
                    if(answer != null){
                        //答案点赞Id
                        String answerLikeId = likeConfig.createItemLikeId(itemId, accessUser.getUserId());

                        AnswerLike answerLike = likeCacheManager.query_cache_findAnswerLikeById(answerLikeId);

                        if(answerLike != null){
                            errors.put("itemLike", "当前项目已经点赞");
                        }
                    }
                }else if(module.equals(60)){//答案回复
                    answerReply =  answerCacheManager.query_cache_findReplyByReplyId(itemId);
                    if(answerReply != null){
                        //答案回复点赞Id
                        String answerReplyLikeId = likeConfig.createItemLikeId(itemId, accessUser.getUserId());

                        AnswerReplyLike answerReplyLike = likeCacheManager.query_cache_findAnswerReplyLikeById(answerReplyLikeId);

                        if(answerReplyLike != null){
                            errors.put("itemLike", "当前项目已经点赞");
                        }
                    }
                }else{
                    errors.put("likeModule", "点赞模块不存在");
                }
            }else{
                errors.put("likeModule", "点赞模块不能为空");
            }
        }else{
            errors.put("itemLike", "项目点赞Id不能为空");
        }


        if(errors.size() == 0){
            LocalDateTime time = LocalDateTime.now();
            Like like = new Like();
            like.setId(likeConfig.createLikeId(accessUser.getUserId()));
            like.setAddtime(time);
            like.setUserName(accessUser.getUserName());
            like.setModule(module);

            if(module.equals(10)){//话题
                like.setTopicId(topic.getId());

                like.setPostUserName(topic.getUserName());

                TopicLike topicLike = new TopicLike();
                String topicLikeId = likeConfig.createItemLikeId(topic.getId(), accessUser.getUserId());
                topicLike.setId(topicLikeId);
                topicLike.setAddtime(time);
                topicLike.setTopicId(topic.getId());
                topicLike.setUserName(accessUser.getUserName());
                topicLike.setPostUserName(topic.getUserName());
                topicLike.setLikeId(like.getId());
                try {
                    //保存点赞
                    likeRepository.saveLike(likeComponent.createLikeObject(like),likeComponent.createTopicLikeObject(topicLike));

                    //删除点赞缓存
                    likeCacheManager.delete_cache_findTopicLikeById(topicLikeId);
                    likeCacheManager.delete_cache_findLikeCountByTopicId(like.getTopicId());


                    //添加热门话题
                    hotTopicComponent.addHotTopic(topic);

                    User _user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
                    if(_user != null && !_user.getId().equals(accessUser.getUserId())){//楼主点赞不发提醒给自己

                        //提醒楼主
                        Remind remind = new Remind();
                        remind.setId(remindConfig.createRemindId(_user.getId()));
                        remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                        remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                        remind.setTypeCode(70);//70.别人点赞了我的话题
                        remind.setSendTimeFormat(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                        remind.setTopicId(topic.getId());//话题Id

                        Object remind_object = remindComponent.createRemindObject(remind);
                        remindRepository.saveRemind(remind_object);

                        //删除提醒缓存
                        remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
                    }

                } catch (org.springframework.orm.jpa.JpaSystemException e) {
                    errors.put("like", "重复点赞");

                }
            }else if(module.equals(20)){//评论
                like.setTopicId(comment.getTopicId());
                like.setCommentId(comment.getId());

                like.setPostUserName(comment.getUserName());

                CommentLike commentLike = new CommentLike();
                String commentLikeId = likeConfig.createItemLikeId(comment.getId(), accessUser.getUserId());
                commentLike.setId(commentLikeId);
                commentLike.setAddtime(time);
                commentLike.setTopicId(comment.getTopicId());
                commentLike.setCommentId(comment.getId());
                commentLike.setUserName(accessUser.getUserName());
                commentLike.setPostUserName(comment.getUserName());
                commentLike.setLikeId(like.getId());
                try {
                    //保存点赞
                    likeRepository.saveLike(likeComponent.createLikeObject(like),likeComponent.createCommentLikeObject(commentLike));

                    //删除点赞缓存
                    likeCacheManager.delete_cache_findCommentLikeById(commentLikeId);
                    likeCacheManager.delete_cache_findLikeCountByCommentId(like.getCommentId());


                    if(!comment.getIsStaff()){
                        User _user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
                        if(_user != null && !_user.getId().equals(accessUser.getUserId())){//作者回答不发提醒给自己

                            //提醒作者
                            Remind remind = new Remind();
                            remind.setId(remindConfig.createRemindId(_user.getId()));
                            remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                            remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                            remind.setTypeCode(260);//260.别人点赞了我的评论
                            remind.setSendTimeFormat(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                            remind.setTopicId(comment.getTopicId());//话题Id
                            remind.setTopicCommentId(comment.getId());//我的话题评论Id

                            Object remind_object = remindComponent.createRemindObject(remind);
                            remindRepository.saveRemind(remind_object);

                            //删除提醒缓存
                            remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
                        }
                    }

                } catch (org.springframework.orm.jpa.JpaSystemException e) {
                    errors.put("like", "重复点赞");

                }
            }else if(module.equals(30)){//评论回复
                like.setTopicId(commentReply.getTopicId());
                like.setCommentId(commentReply.getCommentId());
                like.setCommentReplyId(commentReply.getId());
                like.setPostUserName(commentReply.getUserName());

                CommentReplyLike commentReplyLike = new CommentReplyLike();
                String commentReplyLikeId = likeConfig.createItemLikeId(commentReply.getId(), accessUser.getUserId());
                commentReplyLike.setId(commentReplyLikeId);
                commentReplyLike.setAddtime(time);
                commentReplyLike.setTopicId(commentReply.getTopicId());
                commentReplyLike.setReplyId(commentReply.getId());
                commentReplyLike.setUserName(accessUser.getUserName());
                commentReplyLike.setPostUserName(commentReply.getUserName());
                commentReplyLike.setLikeId(like.getId());
                try {
                    //保存点赞
                    likeRepository.saveLike(likeComponent.createLikeObject(like),likeComponent.createCommentReplyLikeObject(commentReplyLike));

                    //删除点赞缓存
                    likeCacheManager.delete_cache_findCommentReplyLikeById(commentReplyLikeId);
                    likeCacheManager.delete_cache_findLikeCountByCommentReplyId(like.getCommentReplyId());

                    if(!commentReply.getIsStaff()){
                        User _user = userCacheManager.query_cache_findUserByUserName(commentReply.getUserName());
                        if(_user != null
                                && !_user.getId().equals(accessUser.getUserId())){//不发提醒给自己

                            Remind remind = new Remind();
                            remind.setId(remindConfig.createRemindId(_user.getId()));
                            remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                            remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                            remind.setTypeCode(270);//270.别人点赞了我的评论回复
                            remind.setSendTimeFormat(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                            remind.setTopicId(commentReply.getTopicId());//话题Id
                            remind.setTopicCommentId(commentReply.getCommentId());//我的话题评论Id
                            remind.setTopicReplyId(commentReply.getId());//我的话题回复Id


                            Object remind_object = remindComponent.createRemindObject(remind);
                            remindRepository.saveRemind(remind_object);

                            //删除提醒缓存
                            remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
                        }

                    }


                } catch (org.springframework.orm.jpa.JpaSystemException e) {
                    errors.put("like", "重复点赞");

                }
            }else if(module.equals(40)){//问题
                like.setQuestionId(question.getId());

                like.setPostUserName(question.getUserName());

                QuestionLike questionLike = new QuestionLike();
                String questionLikeId = likeConfig.createItemLikeId(question.getId(), accessUser.getUserId());
                questionLike.setId(questionLikeId);
                questionLike.setAddtime(time);
                questionLike.setQuestionId(question.getId());
                questionLike.setUserName(accessUser.getUserName());
                questionLike.setPostUserName(question.getUserName());
                questionLike.setLikeId(like.getId());
                try {
                    //保存点赞
                    likeRepository.saveLike(likeComponent.createLikeObject(like),likeComponent.createQuestionLikeObject(questionLike));

                    //删除点赞缓存
                    likeCacheManager.delete_cache_findQuestionLikeById(questionLikeId);
                    likeCacheManager.delete_cache_findLikeCountByQuestionId(like.getQuestionId());



                    User _user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
                    if(_user != null && !_user.getId().equals(accessUser.getUserId())){//楼主点赞不发提醒给自己

                        //提醒楼主
                        Remind remind = new Remind();
                        remind.setId(remindConfig.createRemindId(_user.getId()));
                        remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                        remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                        remind.setTypeCode(280);// 280.别人点赞了我的问题
                        remind.setSendTimeFormat(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                        remind.setQuestionId(question.getId());//问题Id

                        Object remind_object = remindComponent.createRemindObject(remind);
                        remindRepository.saveRemind(remind_object);

                        //删除提醒缓存
                        remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
                    }

                } catch (org.springframework.orm.jpa.JpaSystemException e) {
                    errors.put("like", "重复点赞");

                }
            }else if(module.equals(50)){//答案
                like.setQuestionId(answer.getQuestionId());
                like.setAnswerId(answer.getId());

                like.setPostUserName(answer.getUserName());

                AnswerLike answerLike = new AnswerLike();
                String answerLikeId = likeConfig.createItemLikeId(answer.getId(), accessUser.getUserId());
                answerLike.setId(answerLikeId);
                answerLike.setAddtime(time);
                answerLike.setQuestionId(answer.getQuestionId());
                answerLike.setAnswerId(answer.getId());
                answerLike.setUserName(accessUser.getUserName());
                answerLike.setPostUserName(answer.getUserName());
                answerLike.setLikeId(like.getId());
                try {
                    //保存点赞
                    likeRepository.saveLike(likeComponent.createLikeObject(like),likeComponent.createAnswerLikeObject(answerLike));

                    //删除点赞缓存
                    likeCacheManager.delete_cache_findAnswerLikeById(answerLikeId);
                    likeCacheManager.delete_cache_findLikeCountByAnswerId(like.getAnswerId());


                    if(!answer.getIsStaff()){
                        User _user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
                        if(_user != null && !_user.getId().equals(accessUser.getUserId())){//作者回答不发提醒给自己

                            //提醒作者
                            Remind remind = new Remind();
                            remind.setId(remindConfig.createRemindId(_user.getId()));
                            remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                            remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                            remind.setTypeCode(290);//290.别人点赞了我的答案
                            remind.setSendTimeFormat(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                            remind.setQuestionId(answer.getQuestionId());//问题Id
                            remind.setQuestionAnswerId(answer.getId());//我的问题答案Id

                            Object remind_object = remindComponent.createRemindObject(remind);
                            remindRepository.saveRemind(remind_object);

                            //删除提醒缓存
                            remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
                        }
                    }

                } catch (org.springframework.orm.jpa.JpaSystemException e) {
                    errors.put("like", "重复点赞");

                }
            }else if(module.equals(60)){//答案回复
                like.setQuestionId(answerReply.getQuestionId());
                like.setAnswerId(answerReply.getAnswerId());
                like.setAnswerReplyId(answerReply.getId());
                like.setPostUserName(answerReply.getUserName());

                AnswerReplyLike answerReplyLike = new AnswerReplyLike();
                String answerReplyLikeId = likeConfig.createItemLikeId(answerReply.getId(), accessUser.getUserId());
                answerReplyLike.setId(answerReplyLikeId);
                answerReplyLike.setAddtime(time);
                answerReplyLike.setQuestionId(answerReply.getQuestionId());
                answerReplyLike.setReplyId(answerReply.getId());
                answerReplyLike.setUserName(accessUser.getUserName());
                answerReplyLike.setPostUserName(answerReply.getUserName());
                answerReplyLike.setLikeId(like.getId());
                try {
                    //保存点赞
                    likeRepository.saveLike(likeComponent.createLikeObject(like),likeComponent.createAnswerReplyLikeObject(answerReplyLike));

                    //删除点赞缓存
                    likeCacheManager.delete_cache_findAnswerReplyLikeById(answerReplyLikeId);
                    likeCacheManager.delete_cache_findLikeCountByAnswerReplyId(like.getAnswerReplyId());

                    if(!answerReply.getIsStaff()){
                        User _user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
                        if(_user != null
                                && !_user.getId().equals(accessUser.getUserId())){//不发提醒给自己

                            Remind remind = new Remind();
                            remind.setId(remindConfig.createRemindId(_user.getId()));
                            remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                            remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                            remind.setTypeCode(300);//300.别人点赞了我的答案回复
                            remind.setSendTimeFormat(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                            remind.setQuestionId(answerReply.getQuestionId());//问题Id
                            remind.setQuestionAnswerId(answerReply.getAnswerId());//我的答案Id
                            remind.setQuestionReplyId(answerReply.getId());//我的问题回复Id


                            Object remind_object = remindComponent.createRemindObject(remind);
                            remindRepository.saveRemind(remind_object);

                            //删除提醒缓存
                            remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
                        }

                    }


                } catch (org.springframework.orm.jpa.JpaSystemException e) {
                    errors.put("like", "重复点赞");

                }

            }
        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
        }else{
            returnValue.put("success", true);
        }
        return returnValue;
    }
    /**
     * 删除点赞
     * @param likeId  点赞Id 只用本参数或下面两个参数
     * @param module  模块  10:话题  20:评论  30:评论回复  40:问题  50:答案  60:答案回复
     * @param itemId  项Id  话题Id、评论Id、评论回复Id、问题Id、答案Id、答案回复Id
     */
    public Map<String,Object> deleteLike(String likeId, Integer module, Long itemId){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("like", "只读模式不允许提交数据"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        Like like = null;
        //项点赞Id
        String itemLikeId = null;
        Integer itemModule = null;
        if(module != null && module >0 && itemId != null && itemId >0L){


            itemLikeId = likeConfig.createItemLikeId(itemId, accessUser.getUserId());
            itemModule = module;

            if(module.equals(10)){//话题
                TopicLike topicLike = likeRepository.findTopicLikeById(itemLikeId);
                if(topicLike != null){
                    if(topicLike.getLikeId() != null && !topicLike.getLikeId().isEmpty()){
                        likeId = topicLike.getLikeId();
                    }else{
                        //兼容6.3及之前版本的数据，6.4及之后的版本TopicLike、CommentLike、CommentReplyLike、QuestionLike、AnswerLike、AnswerReplyLike类增加了likeId字段
                        Like _like = likeRepository.findLikeByCondition(module, accessUser.getUserId(), accessUser.getUserName(), itemId);
                        if(_like != null){
                            likeId = _like.getId();
                        }
                    }
                }
            }else if(module.equals(20)){//评论
                CommentLike commentLike = likeRepository.findCommentLikeById(itemLikeId);
                if(commentLike != null){
                    if(commentLike.getLikeId() != null && !commentLike.getLikeId().isEmpty()){
                        likeId = commentLike.getLikeId();
                    }else{
                        //兼容6.3及之前版本的数据，6.4及之后的版本TopicLike、CommentLike、CommentReplyLike、QuestionLike、AnswerLike、AnswerReplyLike类增加了likeId字段
                        Like _like = likeRepository.findLikeByCondition(module, accessUser.getUserId(), accessUser.getUserName(), itemId);
                        if(_like != null){
                            likeId = _like.getId();
                        }
                    }
                }
            }else if(module.equals(30)){//评论回复
                CommentReplyLike commentReplyLike = likeRepository.findCommentReplyLikeById(itemLikeId);
                if(commentReplyLike != null){
                    if(commentReplyLike.getLikeId() != null && !commentReplyLike.getLikeId().isEmpty()){
                        likeId = commentReplyLike.getLikeId();
                    }else{
                        //兼容6.3及之前版本的数据，6.4及之后的版本TopicLike、CommentLike、CommentReplyLike、QuestionLike、AnswerLike、AnswerReplyLike类增加了likeId字段
                        Like _like = likeRepository.findLikeByCondition(module, accessUser.getUserId(), accessUser.getUserName(), itemId);
                        if(_like != null){
                            likeId = _like.getId();
                        }
                    }
                }
            }else if(module.equals(40)){//问题
                QuestionLike questionLike = likeRepository.findQuestionLikeById(itemLikeId);
                if(questionLike != null){
                    if(questionLike.getLikeId() != null && !questionLike.getLikeId().isEmpty()){
                        likeId = questionLike.getLikeId();
                    }else{
                        //兼容6.3及之前版本的数据，6.4及之后的版本TopicLike、CommentLike、CommentReplyLike、QuestionLike、AnswerLike、AnswerReplyLike类增加了likeId字段
                        Like _like = likeRepository.findLikeByCondition(module, accessUser.getUserId(), accessUser.getUserName(), itemId);
                        if(_like != null){
                            likeId = _like.getId();
                        }
                    }
                }
            }else if(module.equals(50)){//答案
                AnswerLike answerLike = likeRepository.findAnswerLikeById(itemLikeId);
                if(answerLike != null){
                    if(answerLike.getLikeId() != null && !answerLike.getLikeId().isEmpty()){
                        likeId = answerLike.getLikeId();
                    }else{
                        //兼容6.3及之前版本的数据，6.4及之后的版本TopicLike、CommentLike、CommentReplyLike、QuestionLike、AnswerLike、AnswerReplyLike类增加了likeId字段
                        Like _like = likeRepository.findLikeByCondition(module, accessUser.getUserId(), accessUser.getUserName(), itemId);
                        if(_like != null){
                            likeId = _like.getId();
                        }
                    }
                }
            }else if(module.equals(60)){//答案回复
                AnswerReplyLike answerReplyLike = likeRepository.findAnswerReplyLikeById(itemLikeId);
                if(answerReplyLike != null){
                    if(answerReplyLike.getLikeId() != null && !answerReplyLike.getLikeId().isEmpty()){
                        likeId = answerReplyLike.getLikeId();
                    }else{
                        //兼容6.3及之前版本的数据，6.4及之后的版本TopicLike、CommentLike、CommentReplyLike、QuestionLike、AnswerLike、AnswerReplyLike类增加了likeId字段
                        Like _like = likeRepository.findLikeByCondition(module, accessUser.getUserId(), accessUser.getUserName(), itemId);
                        if(_like != null){
                            likeId = _like.getId();
                        }
                    }
                }
            }


        }



        if(likeId == null || likeId.trim().isEmpty()){
            errors.put("like", "点赞Id不存在");
        }else{
            like  = likeRepository.findById(likeId.trim());
            if(like != null){
                itemModule = like.getModule();
                if(like.getUserName().equals(accessUser.getUserName())){
                    if(like.getModule().equals(10)){
                        itemLikeId = likeConfig.createItemLikeId(like.getTopicId(), accessUser.getUserId());
                    }else if(like.getModule().equals(20)){
                        itemLikeId = likeConfig.createItemLikeId(like.getCommentId(), accessUser.getUserId());
                    }else if(like.getModule().equals(30)){
                        itemLikeId = likeConfig.createItemLikeId(like.getCommentReplyId(), accessUser.getUserId());
                    }else if(like.getModule().equals(40)){
                        itemLikeId = likeConfig.createItemLikeId(like.getQuestionId(), accessUser.getUserId());
                    }else if(like.getModule().equals(50)){
                        itemLikeId = likeConfig.createItemLikeId(like.getAnswerId(), accessUser.getUserId());
                    }else if(like.getModule().equals(60)){
                        itemLikeId = likeConfig.createItemLikeId(like.getAnswerReplyId(), accessUser.getUserId());
                    }


                }else{
                    errors.put("like", "本点赞不属于当前用户");
                }
            }else{
                errors.put("like", "点赞不存在");
            }
        }

        if(errors.size() == 0){
            int i = likeRepository.deleteLike(likeId.trim(),itemLikeId,itemModule);
            if(i == 0){
                errors.put("like", "删除点赞失败");
            }

            if(like.getModule().equals(10)){
                //删除点赞缓存
                likeCacheManager.delete_cache_findTopicLikeById(itemLikeId);
                likeCacheManager.delete_cache_findLikeCountByTopicId(like.getTopicId());
            }else if(like.getModule().equals(20)){
                //删除点赞缓存
                likeCacheManager.delete_cache_findCommentLikeById(itemLikeId);
                likeCacheManager.delete_cache_findLikeCountByCommentId(like.getCommentId());
            }else if(like.getModule().equals(30)){
                //删除点赞缓存
                likeCacheManager.delete_cache_findCommentReplyLikeById(itemLikeId);
                likeCacheManager.delete_cache_findLikeCountByCommentReplyId(like.getCommentReplyId());
            }else if(like.getModule().equals(40)){
                //删除点赞缓存
                likeCacheManager.delete_cache_findQuestionLikeById(itemLikeId);
                likeCacheManager.delete_cache_findLikeCountByQuestionId(like.getQuestionId());
            }else if(like.getModule().equals(50)){
                //删除点赞缓存
                likeCacheManager.delete_cache_findAnswerLikeById(itemLikeId);
                likeCacheManager.delete_cache_findLikeCountByAnswerId(like.getAnswerId());
            }else if(like.getModule().equals(60)){
                //删除点赞缓存
                likeCacheManager.delete_cache_findAnswerReplyLikeById(itemLikeId);
                likeCacheManager.delete_cache_findLikeCountByAnswerReplyId(like.getAnswerReplyId());
            }

        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
        }else{
            returnValue.put("success", true);

        }
        return returnValue;
    }

    /**
     * 获取话题点赞列表
     * @param page 页码
     * @param topicId  话题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Like> getTopicLikeList(int page, Long topicId, String fileServerAddress){
        //调用分页算法代码
        PageView<Like> pageView = new PageView<Like>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(topicId != null && topicId > 0L){
            Topic topicInfo = topicCacheManager.queryTopicCache(topicId);//查询缓存
            if(topicInfo != null && topicInfo.getUserName().equals(accessUser.getUserName())){
                //当前页
                int firstIndex = (page-1)*pageView.getMaxresult();


                QueryResult<Like> qr = likeRepository.findLikePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
                if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                    for(Like like : qr.getResultlist()){
                        Topic topic = topicCacheManager.queryTopicCache(like.getTopicId());//查询缓存
                        if(topic != null){
                            like.setTopicTitle(topic.getTitle());
                        }
                        User user = userCacheManager.query_cache_findUserByUserName(like.getUserName());
                        if(user != null && user.getCancelAccountTime().equals(-1L)){
                            like.setAccount(user.getAccount());
                            like.setNickname(user.getNickname());
                            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                                like.setAvatarPath(fileServerAddress+user.getAvatarPath());//头像路径
                                like.setAvatarName(user.getAvatarName());//头像名称
                            }
                        }else{
                            like.setUserName(null);
                        }
                    }
                }
                //将查询结果集传给分页List
                pageView.setQueryResult(qr);
            }
        }
        return pageView;
    }

    /**
     * 获取点赞列表
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Like> getLikeList(int page, String fileServerAddress){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        //调用分页算法代码
        PageView<Like> pageView = new PageView<Like>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<Like> qr = likeRepository.findLikeByUserId(accessUser.getUserId(),accessUser.getUserName(),firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Like like : qr.getResultlist()){

                if(like.getModule().equals(10) || like.getModule().equals(20) || like.getModule().equals(30)){
                    if(like.getModule().equals(10)){//10:话题
                        Topic topic = topicCacheManager.queryTopicCache(like.getTopicId());//查询缓存
                        if(topic != null){
                            like.setTopicTitle(topic.getTitle());
                            like.setSummary(topic.getSummary());
                        }
                    }else if(like.getModule().equals(20)){//20:评论
                        Comment comment = commentCacheManager.query_cache_findByCommentId(like.getCommentId());//查询缓存
                        if(comment != null && comment.getStatus().equals(20)){
                            //不含标签内容
                            String text = textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(comment.getContent()));
                            //清除空格&nbsp;
                            String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
                            //摘要
                            if(trimSpace != null && !trimSpace.isEmpty()){
                                if(systemSetting.isAllowFilterWord()){
                                    String wordReplace = "";
                                    if(systemSetting.getFilterWordReplace() != null){
                                        wordReplace = systemSetting.getFilterWordReplace();
                                    }
                                    trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
                                }
                                if(trimSpace.length() >180){
                                    like.setSummary(trimSpace.substring(0, 180)+"..");
                                }else{
                                    like.setSummary(trimSpace+"..");
                                }
                            }
                        }
                    }else if(like.getModule().equals(30)){//30:评论回复
                        Reply reply = commentCacheManager.query_cache_findReplyByReplyId(like.getCommentReplyId());//查询缓存
                        if(reply != null && reply.getStatus().equals(20)){
                            //清除空格&nbsp;
                            String trimSpace = cms.utils.StringUtil.replaceSpace(reply.getContent()).trim();
                            //摘要
                            if(trimSpace != null && !trimSpace.isEmpty()){
                                if(systemSetting.isAllowFilterWord()){
                                    String wordReplace = "";
                                    if(systemSetting.getFilterWordReplace() != null){
                                        wordReplace = systemSetting.getFilterWordReplace();
                                    }
                                    trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
                                }
                                if(trimSpace.length() >180){
                                    like.setSummary(trimSpace.substring(0, 180)+"..");
                                }else{
                                    like.setSummary(trimSpace+"..");
                                }
                            }
                        }
                    }
                }
                if(like.getModule().equals(40) || like.getModule().equals(50) || like.getModule().equals(60)){
                    Question question = questionCacheManager.query_cache_findById(like.getQuestionId());//查询缓存
                    if(question != null){
                        like.setQuestionTitle(question.getTitle());
                    }

                    if(like.getModule().equals(40)){//40:问题
                        like.setSummary(question.getSummary());
                    }else if(like.getModule().equals(50)){//50:答案
                        Answer answer = answerCacheManager.query_cache_findByAnswerId(like.getAnswerId());//查询缓存
                        if(answer != null && answer.getStatus().equals(20)){
                            //不含标签内容
                            String text = textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(answer.getContent()));
                            //清除空格&nbsp;
                            String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
                            //摘要
                            if(trimSpace != null && !trimSpace.isEmpty()){
                                if(systemSetting.isAllowFilterWord()){
                                    String wordReplace = "";
                                    if(systemSetting.getFilterWordReplace() != null){
                                        wordReplace = systemSetting.getFilterWordReplace();
                                    }
                                    trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
                                }
                                if(trimSpace.length() >180){
                                    like.setSummary(trimSpace.substring(0, 180)+"..");
                                }else{
                                    like.setSummary(trimSpace+"..");
                                }
                            }
                        }
                    }else if(like.getModule().equals(60)){//60:答案回复
                        AnswerReply answerReply = answerCacheManager.query_cache_findReplyByReplyId(like.getAnswerReplyId());//查询缓存
                        if(answerReply != null && answerReply.getStatus().equals(20)){
                            //清除空格&nbsp;
                            String trimSpace = cms.utils.StringUtil.replaceSpace(answerReply.getContent()).trim();
                            //摘要
                            if(trimSpace != null && !trimSpace.isEmpty()){
                                if(systemSetting.isAllowFilterWord()){
                                    String wordReplace = "";
                                    if(systemSetting.getFilterWordReplace() != null){
                                        wordReplace = systemSetting.getFilterWordReplace();
                                    }
                                    trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
                                }
                                if(trimSpace.length() >180){
                                    like.setSummary(trimSpace.substring(0, 180)+"..");
                                }else{
                                    like.setSummary(trimSpace+"..");
                                }
                            }
                        }
                    }
                }
            }
        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }
}
