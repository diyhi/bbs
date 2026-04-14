package cms.component.frontendModule;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.RouteMetadataResolver;
import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.dto.frontendModule.ApiMetadata;
import cms.dto.frontendModule.HandlerMethodPair;
import cms.model.frontendModule.*;
import cms.repository.filterWord.RiskSensitiveWordsRepository;
import cms.repository.frontendModule.FrontendApiRepository;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import tools.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 前台API组件
 * @author Gao
 *
 */
@Component("frontendApiComponent")
public class FrontendApiComponent {
	
	@Resource RiskSensitiveWordsRepository riskSensitiveWordsRepository;
    @Resource JsonComponent jsonComponent;

    // 路由注册的核心组件
    @Resource
    RequestMappingHandlerMapping handlerMapping;

    // 查找 Controller 所需的组件
    @Resource
    ListableBeanFactory beanFactory;
    @Resource
    RouteMetadataResolver routeMetadataResolver;
    @Resource
    FrontendApiRepository frontendApiRepository;
    @Resource FrontendApiCacheManager frontendApiCacheManager;
    @Resource
    FileComponent fileComponent;




    /**
     * 获取前台默认API
     * @return
     */
    public List<ApiMetadata> getFrontendDefaultApiList(){
        List<ApiMetadata> apiMetadataList=  routeMetadataResolver.findAllMetadata();

        // 按映射路由枚举值中的数字排序
        Comparator<ApiMetadata> numericComparator = Comparator.comparing(
                metadata -> extractNumericSuffix(metadata.getRouteEnumMapper())
        );

        apiMetadataList.sort(numericComparator);
        return apiMetadataList;
    }
    /**
     * 获取前台自定义API
     * @return
     */
    public List<FrontendApi> getFrontendCustomApiList(){
        List<FrontendApi> frontendApiList = frontendApiRepository.findAllFrontendApi();
        if(frontendApiList != null && !frontendApiList.isEmpty()){
            frontendApiList.forEach(frontendApi -> {
                Optional<DynamicRouteEnum> optionalRoute = DynamicRouteEnum.getByNameSafe(frontendApi.getRouteEnumMapper());
                optionalRoute.ifPresent(routeEnum -> {
                    frontendApi.setRouteEnumName(routeEnum.getName());
                });
            });
        }

        return frontendApiList;
    }


    /**
     * 判断当前请求路径是否需要登录(前台)
     * @param request 请求信息
     * @return
     */
    public boolean isAuthRequired(HttpServletRequest request){
        //默认需要登录的API
        PathPattern defaultPattern = new PathPatternParser().parse("/user/**");
        RequestPath defaultPath = ServletRequestPathUtils.getParsedRequestPath(request);
        if (defaultPattern.matches(defaultPath.pathWithinApplication())) {
            return true;
        }

        //管理后台的API
        final List<PathPattern> adminApiPatterns = List.of(
                new PathPatternParser().parse("/admin/**"),
                new PathPatternParser().parse("/control/**")
        );
        //是否为管理后台的请求
        boolean isAdminRequest = adminApiPatterns.stream()
                .anyMatch(pattern -> pattern.matches(defaultPath));
        if(isAdminRequest){
            return false;
        }

        List<FrontendApi> frontendApiList = frontendApiCacheManager.query_cache_findAllFrontendApi();
        if(frontendApiList != null && !frontendApiList.isEmpty()){
            for(FrontendApi frontendApi : frontendApiList){
                if(frontendApi.getRequiresLogin()){

                    PathPattern loginPattern = new PathPatternParser().parse(frontendApi.getUrl().trim());
                    RequestPath path = ServletRequestPathUtils.getParsedRequestPath(request);//自动排除request.getContextPath()  例如请求是 /shop/login/auth，匹配的是 /login/auth
                    boolean methodMatch = (frontendApi.getHttpMethod() == null || frontendApi.getHttpMethod().isEmpty() || frontendApi.getHttpMethod().equals(request.getMethod()));
                    if (methodMatch && loginPattern.matches(path.pathWithinApplication())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取当前请求的前台API
     * @param request 请求信息
     * @return
     */
    public FrontendApi getRequestFrontendApi(HttpServletRequest request){
        List<FrontendApi> frontendApiList = frontendApiCacheManager.query_cache_findAllFrontendApi();
        if(frontendApiList != null && !frontendApiList.isEmpty()){
            for(FrontendApi frontendApi : frontendApiList){
                PathPattern loginPattern = new PathPatternParser().parse(frontendApi.getUrl().trim());
                RequestPath path = ServletRequestPathUtils.getParsedRequestPath(request);//自动排除request.getContextPath()  例如请求是 /shop/login/auth，匹配的是 /login/auth
                boolean methodMatch = (frontendApi.getHttpMethod() == null || frontendApi.getHttpMethod().isEmpty() || frontendApi.getHttpMethod().equals(request.getMethod()));
                if (methodMatch && loginPattern.matches(path.pathWithinApplication())) {
                    //解析并设置前台API的配置对象
                    resolveConfigObject(frontendApi,request);
                    return frontendApi;
                }
            }
        }
        return null;
    }


    /**
     * 解析并设置前台API的配置对象
     * @param frontendApi 前台Api
     * @param request 请求信息
     * @return
     */
    public void resolveConfigObject(FrontendApi frontendApi,HttpServletRequest request){
        if(frontendApi.getConfigData() != null && !frontendApi.getConfigData().trim().isEmpty()){
            if(DynamicRouteEnum.CUSTOM_6010100.name().equals(frontendApi.getRouteEnumMapper())){//话题分页
                ConfigTopicPage configTopicPage = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigTopicPage.class);
                if(configTopicPage != null){
                    frontendApi.setConfigObject(configTopicPage);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6010300.name().equals(frontendApi.getRouteEnumMapper())){//评论分页
                ConfigCommentPage configCommentPage = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigCommentPage.class);
                if(configCommentPage != null){
                    frontendApi.setConfigObject(configCommentPage);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6011300.name().equals(frontendApi.getRouteEnumMapper())){//相似话题
                ConfigSimilarTopic configSimilarTopic = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigSimilarTopic.class);
                if(configSimilarTopic != null){
                    frontendApi.setConfigObject(configSimilarTopic);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6011400.name().equals(frontendApi.getRouteEnumMapper())){//热门话题
                ConfigHotTopic configHotTopic = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigHotTopic.class);
                if(configHotTopic != null){
                    frontendApi.setConfigObject(configHotTopic);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6011500.name().equals(frontendApi.getRouteEnumMapper())){//话题精华分页
                ConfigTopicFeaturedPage configTopicFeaturedPage = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigTopicFeaturedPage.class);
                if(configTopicFeaturedPage != null){
                    frontendApi.setConfigObject(configTopicFeaturedPage);
                }
            }

            if(DynamicRouteEnum.CUSTOM_6020100.name().equals(frontendApi.getRouteEnumMapper())){//问题分页
                ConfigQuestionPage configQuestionPage = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigQuestionPage.class);
                if(configQuestionPage != null){
                    frontendApi.setConfigObject(configQuestionPage);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6020300.name().equals(frontendApi.getRouteEnumMapper())){//答案分页
                ConfigAnswerPage configAnswerPage = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigAnswerPage.class);
                if(configAnswerPage != null){
                    frontendApi.setConfigObject(configAnswerPage);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6021100.name().equals(frontendApi.getRouteEnumMapper())){//相似问题
                ConfigSimilarQuestion configSimilarQuestion = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigSimilarQuestion.class);
                if(configSimilarQuestion != null){
                    frontendApi.setConfigObject(configSimilarQuestion);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6070200.name().equals(frontendApi.getRouteEnumMapper())){//领取红包用户分页
                ConfigRedEnvelopeUserPage configRedEnvelopeUserPage = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigRedEnvelopeUserPage.class);
                if(configRedEnvelopeUserPage != null){
                    frontendApi.setConfigObject(configRedEnvelopeUserPage);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6100100.name().equals(frontendApi.getRouteEnumMapper())){//图片广告
                List<ConfigImageAd> configImageAdList = jsonComponent.toGenericObject(frontendApi.getConfigData().trim(), new TypeReference< List<ConfigImageAd> >(){});
                if(configImageAdList != null && !configImageAdList.isEmpty()){
                    configImageAdList.forEach(configImageAd -> {
                        if(configImageAd.getImagePath() != null && !configImageAd.getImagePath().isEmpty()){
                            configImageAd.setImagePath(fileComponent.fileServerAddress(request)+configImageAd.getImagePath());
                        }
                    });
                    frontendApi.setConfigObject(configImageAdList);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6110100.name().equals(frontendApi.getRouteEnumMapper())) {//在线帮助分页
                ConfigHelpPage configHelpPage = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigHelpPage.class);
                if(configHelpPage != null){
                    frontendApi.setConfigObject(configHelpPage);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6110200.name().equals(frontendApi.getRouteEnumMapper())) {//推荐在线帮助
                ConfigRecommendedHelp configRecommendedHelp = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigRecommendedHelp.class);
                if(configRecommendedHelp != null){
                    frontendApi.setConfigObject(configRecommendedHelp);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6150100.name().equals(frontendApi.getRouteEnumMapper())) {//自定义HTML
                ConfigCustomHtml configCustomHtml = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigCustomHtml.class);
                if(configCustomHtml != null){
                    frontendApi.setConfigObject(configCustomHtml);
                }
            }
            if(DynamicRouteEnum.CUSTOM_6150200.name().equals(frontendApi.getRouteEnumMapper())) {//热门搜索词
                ConfigHotSearchTerm configHotSearchTerm = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigHotSearchTerm.class);
                if(configHotSearchTerm != null){
                    frontendApi.setConfigObject(configHotSearchTerm);
                }
            }
        }

    }


    /**
     * 提取数字后缀
     * @param routeEnumMapper 映射路由枚举值
     * @return 数字
     */
    private Long extractNumericSuffix(String routeEnumMapper) {
        if (routeEnumMapper == null) {
            return 0L;
        }

        //查找最后一个 '_' 的位置
        int lastUnderscore = routeEnumMapper.lastIndexOf('_');

        // 检查格式确保 '_' 存在且不在末尾
        if (lastUnderscore == -1 || lastUnderscore == routeEnumMapper.length() - 1) {
            return 0L;
        }

        // 截取 '_' 之后的部分
        String numberPart = routeEnumMapper.substring(lastUnderscore + 1);

        // 将字符串转换为 Long 类型进行比较
        try {
            return Long.parseLong(numberPart);
        } catch (NumberFormatException e) {
            // 如果转换失败（例如后缀不是纯数字），返回 0L
            return 0L;
        }
    }


    /**
     * 刷新注册路由
     * @param oldUrl 旧URL路径
     * @param oldHttpMethod 旧请求方法
     * @param newDynamicRouteEnum 新路由枚举
     * @param newUrl 新URL路径
     * @param newHttpMethod 新请求方法
     */
    public void refreshRoutes(String oldUrl,String oldHttpMethod,DynamicRouteEnum newDynamicRouteEnum,String newUrl,String newHttpMethod){
        if(oldUrl != null && oldHttpMethod != null){
            unregisterRoutes(oldUrl,oldHttpMethod);
        }
        if(newDynamicRouteEnum != null && newUrl != null && newHttpMethod != null){
            registerRoutes(newDynamicRouteEnum,newUrl,newHttpMethod);
        }

    }




	/**
	 * 注册路由
	 * @param dynamicRouteEnum 路由枚举
     * @param url URL路径
     * @param httpMethod 请求方法
	 * @return
	 */
	public void registerRoutes(DynamicRouteEnum dynamicRouteEnum,String url,String httpMethod){
        HandlerMethodPair pair = routeMetadataResolver.findHandlerByRouteEnum(dynamicRouteEnum);
        if(pair != null){
            RequestMappingInfo mappingInfo = createRequestMappingInfo(url, httpMethod);
            handlerMapping.unregisterMapping(mappingInfo);//先取消可能存在旧的路由
            handlerMapping.registerMapping(mappingInfo, pair.getHandler(), pair.getMethod());
        }
	}

    /**
     * 取消注册路由
     * @param url URL路径
     * @param httpMethod 请求方法
     * @return
     */
    public void unregisterRoutes(String url,String httpMethod){
        RequestMappingInfo mappingInfo = createRequestMappingInfo(url, httpMethod);
        handlerMapping.unregisterMapping(mappingInfo);
    }




    /**
     * 创建 RequestMappingInfo 对象，处理 HTTP Method 为 null 或空字符串的情况。
     */
    public RequestMappingInfo createRequestMappingInfo(String path, String httpMethod) {

        // 尝试将 httpMethod 转换为有效的 RequestMethod
        Optional<RequestMethod> requestMethod = Optional.ofNullable(httpMethod)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(m -> {
                    try {
                        return RequestMethod.valueOf(m);
                    } catch (IllegalArgumentException e) {
                        // 如果方法名非法，返回 null（Optional.empty()）
                        return null;
                    }
                });

        // 构建 RequestMappingInfo.Builder
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths(path);

        // 根据解析结果决定是否设置 methods()
        if (requestMethod.isPresent()) {//注册指定的“请求方法” 例如GET、POST
            // 如果成功解析出有效的 RequestMethod，则只注册该类型
            builder = builder.methods(requestMethod.get());
        } //else {//GET、POST、PUT、DELETE 等
        // 如果 httpMethod 为 null、空串或非法，则不调用 .methods()  不调用 .methods() 等同于允许所有 HTTP Method**
        // }

        return builder.build();
    }

    /**
     * 获取所有自定义动态路由
     * @return 动态路由枚举集合
     */
    public List<DynamicRouteEnum> getAllCustomDynamicRoutes() {
        return Arrays.stream(DynamicRouteEnum.values())
                .filter(route -> route.getType() == 2)
                .collect(Collectors.toList());
    }


}
