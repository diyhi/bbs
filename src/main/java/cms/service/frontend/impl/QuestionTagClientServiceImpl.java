package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.question.QuestionTagComponent;
import cms.model.question.QuestionTag;
import cms.model.setting.SystemSetting;
import cms.repository.question.QuestionTagRepository;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.QuestionTagClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 前台问答标签服务
 */
@Service
public class QuestionTagClientServiceImpl implements QuestionTagClientService {

    @Resource
    QuestionTagRepository questionTagRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    QuestionTagComponent questionTagComponent;

    /**
     * 获取全部问题标签
     * @param request   请求信息
     * @return
     */
    public List<QuestionTag> getAllQuestionTagList(HttpServletRequest request){
        List<QuestionTag> questionTagList =  questionTagRepository.findAllQuestionTag_cache();
        List<QuestionTag> new_questionTagList = new ArrayList<QuestionTag>();//排序后标签

        if(questionTagList != null && questionTagList.size() >0){

            List<String> languageFormExtensionCodeList = null;
            SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
            if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
                languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
            }


            //组成排序数据
            Iterator<QuestionTag> questionTag_iter = questionTagList.iterator();
            while(questionTag_iter.hasNext()){
                QuestionTag questionTag = questionTag_iter.next();


                if(questionTag.getImage() != null && !questionTag.getImage().trim().isEmpty()){
                    questionTag.setImage(fileComponent.fileServerAddress(request)+questionTag.getImage());
                }


                //如果是根节点
                if(questionTag.getParentId().equals(0L)){

                    new_questionTagList.add(questionTag);
                    questionTag_iter.remove();
                }
            }
            //组合子标签
            for(QuestionTag questionTag :new_questionTagList){
                questionTagComponent.childQuestionTag(questionTag,questionTagList);
            }
            //排序
            questionTagComponent.questionTagSort(new_questionTagList);

        }
        return new_questionTagList;
    }

}
