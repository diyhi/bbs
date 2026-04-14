package cms.service.frontend;

import cms.model.question.QuestionTag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 前台问答标签服务接口
 */
public interface QuestionTagClientService {
    /**
     * 获取全部问答标签
     * @param request   请求信息
     * @return
     */
    public List<QuestionTag> getAllQuestionTagList(HttpServletRequest request);
}

