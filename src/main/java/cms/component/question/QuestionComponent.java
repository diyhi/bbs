package cms.component.question;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.setting.SettingComponent;
import cms.component.user.UserCacheManager;
import cms.component.user.UserComponent;
import cms.dto.ReadPathResult;
import cms.model.question.AppendQuestionItem;
import cms.model.question.Question;
import cms.model.question.QuestionIndex;
import cms.model.setting.EditorTag;
import cms.model.staff.SysUsers;
import cms.model.user.User;
import cms.repository.question.AnswerRepository;
import cms.repository.question.QuestionIndexRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.staff.StaffRepository;
import cms.repository.user.UserRepository;
import cms.utils.FileUtil;
import com.google.common.util.concurrent.AtomicLongMap;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.util.*;

/**
 * 问题组件
 *
 */
@Component("questionComponent")
public class QuestionComponent {

    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    QuestionRepository questionRepository;
    @Resource
    StaffRepository staffRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    SettingComponent settingComponent;
    @Resource
    AnswerRepository answerRepository;
    @Resource
    FileComponent fileComponent;
    @Resource
    UserComponent userComponent;
    @Resource
    QuestionIndexRepository questionIndexRepository;
    @Resource
    JsonComponent jsonComponent;


    //key: 问题Id value:展示次数
    private final AtomicLongMap<Long> countMap = AtomicLongMap.create();
    @Autowired
    private QuestionCacheManager questionCacheManager;
    @Autowired
    private UserCacheManager userCacheManager;

    /**
     * 增加问题展示次数
     * @param questionId 问题Id
     * @param ip
     */
    public void addView(Long questionId,String ip){

        Long time = new Date().getTime();
        Long oldTime = questionCacheManager.ipRecord(questionId+"_"+ip, time);
        if(oldTime != null &&  time.equals(oldTime)){
            //增加
            countMap.incrementAndGet(questionId);
        }
    }

    /**
     * 读取问题本地展示次数
     * @param questionId 问题Id
     */
    public Long readLocalView(Long questionId){
        Long count = countMap.asMap().get(questionId);
        if(count != null){
            return count;
        }
        return 0L;
    }

    /**
     * 提交问题展示次数到数据库(定时提交)
     */
    @Scheduled(fixedDelay=600000)//10分钟
    public void commitCount(){
        //计数
        int i = 0;

        Map<Long,Long> batchCount = new HashMap<Long,Long>();

        for (Map.Entry<Long, Long> entry : countMap.asMap().entrySet()) {
            Long questionId = entry.getKey();
            Long count = entry.getValue();

            i++;
            if(count > 0L){
                batchCount.put(questionId, count);
                countMap.addAndGet(questionId, count * -1L);//减少
            }
            if(i== 100){//每100条提交一次到数据库
                if(batchCount.size() >0){
                    questionRepository.addViewCount(batchCount);
                    batchCount.clear();
                }
                i = 0;
            }
        }

        //将剩余的展示次数提交到数据库
        if(batchCount.size() >0){
            questionRepository.addViewCount(batchCount);
        }

        //删除所有计数器值为零的映射
        countMap.removeAllZeros();
    }



    /**
     * 生成上传文件编号
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @return
     */
    public String generateFileNumber(String userName, Boolean isStaff){
        String number = "";

        if(isStaff != null && userName != null && !userName.trim().isEmpty()){
            if(isStaff){//员工

                SysUsers sysUsers = staffRepository.findByUserAccount(userName.trim());
                if(sysUsers != null){
                    number = "a"+sysUsers.getUserId();
                }
            }else{//会员
                User user = userRepository.findUserByUserName(userName.trim());
                if(user != null){
                    number = "b"+user.getId();
                }
            }
        }
        return number;
    }
    /**
     * 根据文件名获取文件编号
     * @param fileName 文件名称(不含后缀)
     * @return
     */
    public String getFileNumber(String fileName){
        String number = "";

        if(fileName != null && !fileName.trim().isEmpty()){
            fileName = fileName.trim();
            if(fileName.length() >33){
                String isStaff = fileName.substring(32,33);
                if(isStaff.equals("a") || isStaff.equals("b")){
                    number = fileName.substring(32, fileName.length());
                }
            }
        }
        return number;
    }



    /**
     * 根据用户名称删除用户下的所有问题文件
     * @param userName 用户名称
     * @param isStaff 是否为员工
     */
    public void deleteQuestionFile(String userName, boolean isStaff){
        int firstIndex = 0;//起始页
        int maxResult = 100;// 每页显示记录数

        String fileNumber = generateFileNumber(userName, isStaff);

        while(true){
            List<Question> questionContentList = questionRepository.findQuestionContentByPage(firstIndex, maxResult,userName,isStaff);
            if(questionContentList == null || questionContentList.size() == 0){
                break;
            }
            firstIndex = firstIndex+maxResult;
            for (Question question :questionContentList) {
                Long questionId = question.getId();
                String questionContent = question.getContent();


                //删除最后一个逗号
                String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

                List<AppendQuestionItem> appendQuestionItemList = jsonComponent.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
                if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
                    for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
                        questionContent += appendQuestionItem.getContent();
                    }
                }

                if(questionContent != null && !questionContent.trim().isEmpty()){
                    ReadPathResult readPathResult = textFilterComponent.readPathName(questionContent,"question");

                    List<String> filePathList = new ArrayList<String>();


                    //删除图片
                    List<String> imageNameList = readPathResult.getImageNameList();
                    filePathList.addAll(imageNameList);
                    //删除影音
                    List<String> mediaNameList = readPathResult.getMediaNameList();
                    filePathList.addAll(mediaNameList);
                    //删除文件
                    List<String> fileNameList = readPathResult.getFileNameList();
                    filePathList.addAll(fileNameList);

                    for(String filePath :filePathList){

                        //如果验证不是当前用户上传的文件，则不删除
                        if(!getFileNumber(FileUtil.getBaseName(filePath.trim())).equals(fileNumber)){
                            continue;
                        }

                        //替换路径中的..号
                        filePath = FileUtil.toRelativePath(filePath);
                        filePath = FileUtil.toSystemPath(filePath);
                        //删除旧路径文件
                        Boolean state = fileComponent.deleteFile(filePath);
                        if(state != null && !state){
                            //替换指定的字符，只替换第一次出现的
                            filePath = Strings.CS.replaceOnce(filePath, "file"+File.separator+"question"+File.separator, "");

                            //创建删除失败文件
                            fileComponent.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
                        }
                    }


                }
                //清空目录
                Boolean state = fileComponent.removeDirectory("file"+File.separator+"answer"+File.separator+questionId+File.separator);
                if(state != null && state){
                    //创建删除失败目录文件
                    fileComponent.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+"#"+questionId);
                }
            }

        }
    }



    /**
     * 根据用户名称删除用户下的所有答案文件
     * @param userName 用户名称
     * @param isStaff 是否为员工
     */
    public void deleteAnswerFile(String userName, boolean isStaff){
        int firstIndex = 0;//起始页
        int maxResult = 100;// 每页显示记录数

        String fileNumber = generateFileNumber(userName, isStaff);

        while(true){
            List<String> contentList = answerRepository.findAnswerContentByPage(firstIndex, maxResult,userName,isStaff);
            if(contentList == null || contentList.size() == 0){
                break;
            }
            firstIndex = firstIndex+maxResult;
            for (String content: contentList) {
                if(content != null && !content.trim().isEmpty()){
                    //删除图片
                    List<String> imageNameList = textFilterComponent.readImageName(content,"answer");
                    if(imageNameList != null && imageNameList.size() >0){
                        for(String imagePath : imageNameList){
                            //如果验证不是当前用户上传的文件，则不删除锁
                            if(!getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
                                continue;
                            }


                            //替换路径中的..号
                            imagePath = FileUtil.toRelativePath(imagePath);

                            imagePath = FileUtil.toSystemPath(imagePath);

                            Boolean state = fileComponent.deleteFile(imagePath);
                            if(state != null && !state){
                                //替换指定的字符，只替换第一次出现的
                                imagePath = Strings.CS.replaceOnce(imagePath, "file"+ File.separator+"answer"+File.separator, "");

                                //创建删除失败文件
                                fileComponent.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+FileUtil.toUnderline(imagePath));

                            }
                        }
                    }
                }
            }
        }
    }








    /**
     * 问题编辑器允许使用标签
     * @return List<String> 类型json格式
     */
    public String availableTag(){
        List<String> tag = new ArrayList<String>();
        EditorTag editor = settingComponent.readQuestionEditorTag();
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
            /**
             if(editor.isFile()){//文件
             tag.add("insertfile");
             }**/
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
     * 修改问题为待审核
     * @param userName 用户名称
     * @param questionId 问题Id
     */
    public void updatePendingReview(String userName,Long questionId){
        int i = questionRepository.updateQuestionPendingReview(questionId);

        if(i >0){
            User user = userCacheManager.query_cache_findUserByUserName(userName);
            if(user != null){
                //修改问题状态
                userRepository.updateUserDynamicQuestionStatus(user.getId(),userName,questionId,10);
            }
        }

        //更新索引
        questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(questionId),2));
        questionCacheManager.delete_cache_findById(questionId);//删除缓存
    }

    /**
     * 标记删除问题
     * @param userName 用户名称
     * @param questionId 问题Id
     */
    public void markDeleteQuestion(String userName,Long questionId){

        int i = questionRepository.markDelete(questionId);
        User user = userCacheManager.query_cache_findUserByUserName(userName);

        if(i >0 && user != null){
            //修改问题状态
            userRepository.softDeleteUserDynamicByQuestionId(user.getId(),userName,questionId);

            //更新索引
            questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(questionId),2));
            questionCacheManager.delete_cache_findById(questionId);//删除缓存
            questionCacheManager.delete_cache_findQuestionTagAssociationByQuestionId(questionId);//删除'根据问题Id查询问题标签关联'缓存
        }


    }
}
