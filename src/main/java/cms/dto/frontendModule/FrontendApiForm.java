package cms.dto.frontendModule;

import cms.model.frontendModule.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 前台API功能参数配置对象
 */
@Getter
@Setter
public class FrontendApiForm implements Serializable {
    @Serial
    private static final long serialVersionUID = -5670674005875267380L;

    private FrontendApi frontendApi;

    private ConfigAnswerPage configAnswerPage;

    private ConfigCommentPage configCommentPage;

    private ConfigCustomHtml configCustomHtml;

    private ConfigHelpPage configHelpPage;

    private ConfigHotSearchTerm configHotSearchTerm;

    private ConfigHotTopic configHotTopic;

    private List<ConfigImageAd> configImageAdList;

    private ConfigQuestionPage configQuestionPage;

    private ConfigRecommendedHelp configRecommendedHelp;

    private ConfigRedEnvelopeUserPage configRedEnvelopeUserPage;

    private ConfigSimilarQuestion configSimilarQuestion;

    private ConfigSimilarTopic configSimilarTopic;

    private ConfigTopicFeaturedPage configTopicFeaturedPage;

    private ConfigTopicPage configTopicPage;
}
