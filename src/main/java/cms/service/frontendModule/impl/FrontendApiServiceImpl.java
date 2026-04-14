package cms.service.frontendModule.impl;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteGroupEnum;
import cms.annotation.RouteMetadataResolver;
import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.frontendModule.FrontendApiCacheManager;
import cms.component.frontendModule.FrontendApiComponent;
import cms.config.BusinessException;
import cms.dto.ReadPathResult;
import cms.dto.frontendModule.DynamicRouteDTO;
import cms.dto.frontendModule.DynamicRouteGroupDTO;
import cms.dto.frontendModule.FrontendApiForm;
import cms.model.frontendModule.*;
import cms.model.help.Help;
import cms.model.help.HelpType;
import cms.model.question.QuestionTag;
import cms.model.setting.SystemSetting;
import cms.repository.frontendModule.FrontendApiRepository;
import cms.repository.help.HelpRepository;
import cms.repository.help.HelpTypeRepository;
import cms.repository.question.QuestionTagRepository;
import cms.repository.setting.SettingRepository;
import cms.service.frontendModule.FrontendApiService;
import cms.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 前台API服务
 */
@Service
public class FrontendApiServiceImpl implements FrontendApiService {
    private static final Logger logger = LogManager.getLogger(FrontendApiServiceImpl.class);

    @Resource FrontendApiRepository frontendApiRepository;
    @Resource JsonComponent jsonComponent;
    @Resource RouteMetadataResolver routeMetadataResolver;
    @Resource FrontendApiComponent frontendApiComponent;
    @Resource QuestionTagRepository questionTagRepository;
    @Resource FileComponent fileComponent;
    @Resource TextFilterComponent textFilterComponent;
    @Resource HelpTypeRepository helpTypeRepository;
    @Resource HelpRepository helpRepository;
    @Resource
    FrontendApiCacheManager frontendApiCacheManager;
    @Resource
    SettingRepository settingRepository;


    //?  匹配任何单字符
    //*  匹配0或者任意数量的字符
    //** 匹配0或者更多的目录
    private final PathMatcher matcher = new AntPathMatcher();




    /**
     * 添加前台API
     * @param frontendApiForm 前台API功能参数配置对象表单
     * @param imageAdFile 图片广告文件
     * @param request 请求信息
     */
    public void addFrontendApi(FrontendApiForm frontendApiForm, MultipartFile[] imageAdFile, HttpServletRequest request){

        checkUrl(null,frontendApiForm.getFrontendApi().getUrl(),frontendApiForm.getFrontendApi().getHttpMethod());

        processConfigData(frontendApiForm,imageAdFile,request);


        frontendApiRepository.saveFrontendApi(frontendApiForm.getFrontendApi());

        frontendApiCacheManager.delete_cache_findAllFrontendApi();

        DynamicRouteEnum routeEnum = DynamicRouteEnum.valueOf(frontendApiForm.getFrontendApi().getRouteEnumMapper());
        //刷新路由
        frontendApiComponent.refreshRoutes(null,null,routeEnum,frontendApiForm.getFrontendApi().getUrl(),frontendApiForm.getFrontendApi().getHttpMethod());


        if(frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6100100.name())) {//图片广告
            List<ConfigImageAd> configImageAdList = frontendApiForm.getConfigImageAdList();
            if (configImageAdList != null && !configImageAdList.isEmpty()) {
                for (int i = 0; i < configImageAdList.size(); i++) {
                    ConfigImageAd configImageAd = configImageAdList.get(i);
                    if(configImageAd.getImagePath() != null && !configImageAd.getImagePath().trim().isEmpty()){
                        //替换指定的字符，只替换第一次出现的
                        String pathFile = Strings.CS.replaceOnce(configImageAd.getImagePath(), "file/frontendApi/", "");
                        if(pathFile != null && !pathFile.trim().isEmpty()){
                            fileComponent.deleteLock("file"+File.separator+"frontendApi"+File.separator+"lock"+File.separator,pathFile.replaceAll("/","_"));
                        }
                    }
                }
            }
        }

        if(frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6150100.name())) {//自定义HTML
            ConfigCustomHtml configCustomHtml = frontendApiForm.getConfigCustomHtml();
            if(configCustomHtml != null && configCustomHtml.getContent() != null && !configCustomHtml.getContent().trim().isEmpty()){

                String content = textFilterComponent.deleteLocalRichTextFilePath(request,configCustomHtml.getContent(),"frontendApi");

                ReadPathResult readPathResult = textFilterComponent.readPathName(content, "frontendApi");
                List<String> pathFileList = new ArrayList<String>();//路径文件
                //图片
                List<String> imageNameList = readPathResult.getImageNameList();
                if(imageNameList != null && !imageNameList.isEmpty()){
                    pathFileList.addAll(imageNameList);
                }
                //影音
                List<String> mediaNameList = readPathResult.getMediaNameList();
                if(mediaNameList != null && !mediaNameList.isEmpty()){
                    pathFileList.addAll(mediaNameList);
                }
                //文件
                List<String> fileNameList = readPathResult.getFileNameList();
                if(fileNameList != null && !fileNameList.isEmpty()){
                    pathFileList.addAll(fileNameList);
                }
                for(String pathFile :pathFileList){

                    //替换指定的字符，只替换第一次出现的
                    pathFile = Strings.CS.replaceOnce(pathFile, "file/frontendApi/", "");
                    if(pathFile != null && !pathFile.trim().isEmpty()){
                        fileComponent.deleteLock("file"+File.separator+"frontendApi"+File.separator+"lock"+File.separator,pathFile.replaceAll("/","_"));
                    }
                }
            }
        }
    }

    /**
     * 获取修改前台API界面信息
     * @param frontendApiId 前台ApiId
     * @return
     */
    public FrontendApi getEditFrontendApiViewModel(Integer frontendApiId, HttpServletRequest request){
        if(frontendApiId == null || frontendApiId <0){
            throw new BusinessException(Map.of("frontendApiId", "前台ApiId不能为空"));
        }
        FrontendApi frontendApi = frontendApiRepository.findById(frontendApiId);
        if(frontendApi == null){
            throw new BusinessException(Map.of("frontendApi", "前台API不存在"));
        }

        //解析并设置前台API的配置对象
        frontendApiComponent.resolveConfigObject(frontendApi,request);

        return frontendApi;
    }





    /**
     * 修改前台API
     * @param frontendApiForm 前台API表单
     * @param imageAdFile 图片广告文件
     * @param request 请求信息
     */
    public void editFrontendApi(FrontendApiForm frontendApiForm, MultipartFile[] imageAdFile, HttpServletRequest request) {
        if (frontendApiForm.getFrontendApi().getId() == null || frontendApiForm.getFrontendApi().getId() < 0) {
            throw new BusinessException(Map.of("frontendApiId", "前台ApiId不能为空"));
        }

        FrontendApi oldFrontendApi = frontendApiRepository.findById(frontendApiForm.getFrontendApi().getId());


        checkUrl(frontendApiForm.getFrontendApi().getId(), frontendApiForm.getFrontendApi().getUrl(), frontendApiForm.getFrontendApi().getHttpMethod());

        processConfigData(frontendApiForm, imageAdFile, request);

        frontendApiRepository.updateFrontendApi(frontendApiForm.getFrontendApi());
        frontendApiCacheManager.delete_cache_findAllFrontendApi();

        DynamicRouteEnum newRouteEnum = DynamicRouteEnum.valueOf(frontendApiForm.getFrontendApi().getRouteEnumMapper());
        if (oldFrontendApi != null) {
            //刷新路由
            frontendApiComponent.refreshRoutes(oldFrontendApi.getUrl(), oldFrontendApi.getHttpMethod(), newRouteEnum, frontendApiForm.getFrontendApi().getUrl(), frontendApiForm.getFrontendApi().getHttpMethod());
        } else {
            //刷新路由
            frontendApiComponent.refreshRoutes(null, null, newRouteEnum, frontendApiForm.getFrontendApi().getUrl(), frontendApiForm.getFrontendApi().getHttpMethod());
        }

        if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6100100.name())) {//图片广告
            //旧图片
            List<String> oldImageList = new ArrayList<String>();
            //新图片
            List<String> newImageList = new ArrayList<String>();

            //删除锁
            List<ConfigImageAd> configImageAdList = frontendApiForm.getConfigImageAdList();
            if (configImageAdList != null && !configImageAdList.isEmpty()) {
                for (int i = 0; i < configImageAdList.size(); i++) {
                    ConfigImageAd configImageAd = configImageAdList.get(i);
                    if (configImageAd.getImagePath() != null && !configImageAd.getImagePath().trim().isEmpty()) {
                        newImageList.add(configImageAd.getImagePath());

                        //替换指定的字符，只替换第一次出现的
                        String pathFile = Strings.CS.replaceOnce(configImageAd.getImagePath(), "file/frontendApi/", "");
                        if (pathFile != null && !pathFile.trim().isEmpty()) {
                            fileComponent.deleteLock("file" + File.separator + "frontendApi" + File.separator + "lock" + File.separator, pathFile.replaceAll("/", "_"));
                        }
                    }
                }
            }

            if (oldFrontendApi.getConfigData() != null && !oldFrontendApi.getConfigData().isEmpty()) {
                List<ConfigImageAd> oldConfigImageAdList = jsonComponent.toGenericObject(oldFrontendApi.getConfigData().trim(), new TypeReference<List<ConfigImageAd>>() {
                });
                if (oldConfigImageAdList != null && !oldConfigImageAdList.isEmpty()) {
                    oldConfigImageAdList.forEach(oldConfigImageAd -> {
                        if (oldConfigImageAd.getImagePath() != null && !oldConfigImageAd.getImagePath().isEmpty()) {
                            oldImageList.add(oldConfigImageAd.getImagePath());
                        }
                    });
                }
            }


            //删除旧图片集合中和新图片相同路径的文件
            oldImageList.removeAll(newImageList);


            //删除旧图片
            for (String oldImage : oldImageList) {
                //旧图片
                String oldPathFile = FileUtil.toRelativePath(oldImage);
                //删除旧文件
                fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));
            }
        }
        if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6150100.name())) {//自定义HTML
            List<String> oldPathFileList = new ArrayList<String>();//旧路径文件
            List<String> newPathFileList = new ArrayList<String>();// 新路径文件

            //删除锁
            if (frontendApiForm.getConfigCustomHtml() != null && frontendApiForm.getConfigCustomHtml().getContent() != null && !frontendApiForm.getConfigCustomHtml().getContent().trim().isEmpty()) {

                String content = textFilterComponent.deleteLocalRichTextFilePath(request, frontendApiForm.getConfigCustomHtml().getContent(), "frontendApi");

                ReadPathResult readPathResult = textFilterComponent.readPathName(content, "frontendApi");

                //图片
                List<String> imageNameList = readPathResult.getImageNameList();
                if (imageNameList != null && !imageNameList.isEmpty()) {
                    newPathFileList.addAll(imageNameList);
                }
                //影音
                List<String> mediaNameList = readPathResult.getMediaNameList();
                if (mediaNameList != null && !mediaNameList.isEmpty()) {
                    newPathFileList.addAll(mediaNameList);
                }
                //文件
                List<String> fileNameList = readPathResult.getFileNameList();
                if (fileNameList != null && !fileNameList.isEmpty()) {
                    newPathFileList.addAll(fileNameList);
                }
                for (String pathFile : newPathFileList) {
                    //替换指定的字符，只替换第一次出现的
                    pathFile = Strings.CS.replaceOnce(pathFile, "file/frontendApi/", "");
                    if (pathFile != null && !pathFile.trim().isEmpty()) {

                        fileComponent.deleteLock("file" + File.separator + "frontendApi" + File.separator + "lock" + File.separator, pathFile.replaceAll("/", "_"));
                    }
                }
            }
            if (oldFrontendApi.getConfigData() != null && !oldFrontendApi.getConfigData().isEmpty()) {
                ConfigCustomHtml oldConfigCustomHtml = jsonComponent.toObject(oldFrontendApi.getConfigData().trim(), ConfigCustomHtml.class);
                if (oldConfigCustomHtml.getContent() != null && !oldConfigCustomHtml.getContent().trim().isEmpty()) {
                    String content = textFilterComponent.deleteLocalRichTextFilePath(request, oldConfigCustomHtml.getContent(), "frontendApi");

                    ReadPathResult readPathResult = textFilterComponent.readPathName(content, "frontendApi");
                    //图片
                    List<String> imageNameList = readPathResult.getImageNameList();
                    if (imageNameList != null && !imageNameList.isEmpty()) {
                        oldPathFileList.addAll(imageNameList);
                    }
                    //影音
                    List<String> mediaNameList = readPathResult.getMediaNameList();
                    if (mediaNameList != null && !mediaNameList.isEmpty()) {
                        oldPathFileList.addAll(mediaNameList);
                    }
                    //文件
                    List<String> fileNameList = readPathResult.getFileNameList();
                    if (fileNameList != null && !fileNameList.isEmpty()) {
                        oldPathFileList.addAll(fileNameList);
                    }
                }
            }
            //删除旧文件集合中和新文件相同路径的文件
            oldPathFileList.removeAll(newPathFileList);

            for (String oldPathFile : oldPathFileList) {
                //替换路径中的..号
                oldPathFile = FileUtil.toRelativePath(oldPathFile);
                oldPathFile = FileUtil.toSystemPath(oldPathFile);
                Boolean state = fileComponent.deleteFile(oldPathFile);

                if (state != null && !state) {
                    //替换指定的字符，只替换第一次出现的
                    oldPathFile = Strings.CS.replaceOnce(oldPathFile, "file" + File.separator + "frontendApi" + File.separator, "");
                    oldPathFile = FileUtil.toUnderline(oldPathFile);//替换所有出现过的字符
                    //创建删除失败文件
                    fileComponent.failedStateFile("file" + File.separator + "frontendApi" + File.separator + "lock" + File.separator + oldPathFile);
                }
            }
        }


        //如果修改了‘功能’选项，并且旧功能含有文件
        if (oldFrontendApi.getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6100100.name())
                && !frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6100100.name())) {//图片广告
            if (oldFrontendApi.getConfigData() != null && !oldFrontendApi.getConfigData().isEmpty()) {
                List<ConfigImageAd> oldConfigImageAdList = jsonComponent.toGenericObject(oldFrontendApi.getConfigData().trim(), new TypeReference<List<ConfigImageAd>>() {
                });
                if (oldConfigImageAdList != null && !oldConfigImageAdList.isEmpty()) {
                    oldConfigImageAdList.forEach(oldConfigImageAd -> {
                        if (oldConfigImageAd.getImagePath() != null && !oldConfigImageAd.getImagePath().isEmpty()) {
                            //旧图片
                            String oldPathFile = FileUtil.toRelativePath(oldConfigImageAd.getImagePath());
                            //删除旧文件
                            fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));
                        }
                    });
                }
            }
        }
        if (oldFrontendApi.getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6150100.name())
                && !frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6150100.name())) {//自定义HTML
            List<String> oldPathFileList = new ArrayList<String>();//旧路径文件
            if (oldFrontendApi.getConfigData() != null && !oldFrontendApi.getConfigData().isEmpty()) {
                ConfigCustomHtml oldConfigCustomHtml = jsonComponent.toObject(oldFrontendApi.getConfigData().trim(), ConfigCustomHtml.class);
                if (oldConfigCustomHtml.getContent() != null && !oldConfigCustomHtml.getContent().trim().isEmpty()) {
                    String content = textFilterComponent.deleteLocalRichTextFilePath(request, oldConfigCustomHtml.getContent(), "frontendApi");

                    ReadPathResult readPathResult = textFilterComponent.readPathName(content, "frontendApi");
                    //图片
                    List<String> imageNameList = readPathResult.getImageNameList();
                    if (imageNameList != null && !imageNameList.isEmpty()) {
                        oldPathFileList.addAll(imageNameList);
                    }
                    //影音
                    List<String> mediaNameList = readPathResult.getMediaNameList();
                    if (mediaNameList != null && !mediaNameList.isEmpty()) {
                        oldPathFileList.addAll(mediaNameList);
                    }
                    //文件
                    List<String> fileNameList = readPathResult.getFileNameList();
                    if (fileNameList != null && !fileNameList.isEmpty()) {
                        oldPathFileList.addAll(fileNameList);
                    }
                }
            }

            for (String oldPathFile : oldPathFileList) {
                //替换路径中的..号
                oldPathFile = FileUtil.toRelativePath(oldPathFile);
                oldPathFile = FileUtil.toSystemPath(oldPathFile);
                Boolean state = fileComponent.deleteFile(oldPathFile);

                if (state != null && !state) {
                    //替换指定的字符，只替换第一次出现的
                    oldPathFile = Strings.CS.replaceOnce(oldPathFile, "file" + File.separator + "frontendApi" + File.separator, "");
                    oldPathFile = FileUtil.toUnderline(oldPathFile);//替换所有出现过的字符
                    //创建删除失败文件
                    fileComponent.failedStateFile("file" + File.separator + "frontendApi" + File.separator + "lock" + File.separator + oldPathFile);
                }
            }

        }
    }
    /**
     * 删除前台API
     * @param frontendApiId 前台ApiId
     * @param request 请求信息
     */
    public void deleteFrontendApi(Integer frontendApiId, HttpServletRequest request){
        if(frontendApiId == null || frontendApiId <0){
            throw new BusinessException(Map.of("frontendApiId", "前台ApiId不能为空"));
        }
        FrontendApi frontendApi = frontendApiRepository.findById(frontendApiId);
        frontendApiRepository.deleteFrontendApi(frontendApiId);
        frontendApiCacheManager.delete_cache_findAllFrontendApi();

        if(frontendApi != null){
            //刷新路由
            frontendApiComponent.refreshRoutes(frontendApi.getUrl(),frontendApi.getHttpMethod(),null,null,null);

            if(frontendApi.getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6100100.name())) {//图片广告
                List<ConfigImageAd> configImageAdList = jsonComponent.toGenericObject(frontendApi.getConfigData().trim(), new TypeReference< List<ConfigImageAd> >(){});
                if(configImageAdList != null && !configImageAdList.isEmpty()){
                    configImageAdList.forEach(configImageAd -> {
                        if(configImageAd.getImagePath() != null && !configImageAd.getImagePath().isEmpty()){
                            //图片
                            String pathFile = FileUtil.toRelativePath(configImageAd.getImagePath());
                            //删除旧文件
                            fileComponent.deleteFile(FileUtil.toSystemPath(pathFile));
                        }
                    });
                }
            }

            if(frontendApi.getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6150100.name())) {//自定义HTML
                ConfigCustomHtml configCustomHtml = jsonComponent.toObject(frontendApi.getConfigData().trim(), ConfigCustomHtml.class);
                List<String> pathFileList = new ArrayList<String>();// 新路径文件
                if (configCustomHtml.getContent() != null && !configCustomHtml.getContent().trim().isEmpty()) {
                    String content = textFilterComponent.deleteLocalRichTextFilePath(request,configCustomHtml.getContent(),"frontendApi");

                    ReadPathResult readPathResult = textFilterComponent.readPathName(content, "frontendApi");
                    //图片
                    List<String> imageNameList = readPathResult.getImageNameList();
                    if(imageNameList != null && !imageNameList.isEmpty()){
                        pathFileList.addAll(imageNameList);
                    }
                    //影音
                    List<String> mediaNameList = readPathResult.getMediaNameList();
                    if(mediaNameList != null && !mediaNameList.isEmpty()){
                        pathFileList.addAll(mediaNameList);
                    }
                    //文件
                    List<String> fileNameList = readPathResult.getFileNameList();
                    if(fileNameList != null && !fileNameList.isEmpty()){
                        pathFileList.addAll(fileNameList);
                    }

                    for(String oldPathFile : pathFileList){
                        //替换路径中的..号
                        oldPathFile = FileUtil.toRelativePath(oldPathFile);
                        oldPathFile = FileUtil.toSystemPath(oldPathFile);
                        Boolean state = fileComponent.deleteFile(oldPathFile);

                        if(state != null && !state){
                            //替换指定的字符，只替换第一次出现的
                            oldPathFile = Strings.CS.replaceOnce(oldPathFile, "file"+File.separator+"frontendApi"+File.separator, "");
                            oldPathFile = FileUtil.toUnderline(oldPathFile);//替换所有出现过的字符
                            //创建删除失败文件
                            fileComponent.failedStateFile("file"+File.separator+"frontendApi"+File.separator+"lock"+File.separator+oldPathFile);
                        }
                    }
                }
            }
        }
    }

    /**
     * 文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileServerAddress 文件服务器随机地址
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     */
    public Map<String,Object> upload(String dir,String fileServerAddress,String fileName,MultipartFile file){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        if (dir == null) {
            returnValue.put("error", 1);
            returnValue.put("message", "参数不能为空");
            return returnValue;
        }
        int fileSystem = fileComponent.getFileSystem();
        if (fileSystem == 10 || fileSystem == 20 || fileSystem == 30) { // SeaweedFS, MinIO, 阿里云OSS
            return handleCloudUpload(dir, fileName, returnValue);
        } else { // 本地系统
            return handleLocalUpload(dir, file, fileServerAddress, returnValue);
        }
    }

    /**
     * 校验URL路径
     * @param frontendApiId 前台ApiId
     * @param url URL路径
     * @param httpMethod 请求方法
     * @return
     */
    public boolean checkUrl(Integer frontendApiId,String url,String httpMethod) {
        if (url == null || url.trim().isEmpty()) {
            throw new BusinessException(Map.of("frontendApi.url", "URL不能为空"));
        }

        if (!url.trim().matches("/.+?")) {
            throw new BusinessException(Map.of("frontendApi.url", "必须以/开头"));
        }
        if (url.trim().matches(".+?/")) {
            throw new BusinessException(Map.of("frontendApi.url", "不能以/结尾"));
        }
        if (url.trim().matches(".*/{2,}.*")) {
            throw new BusinessException(Map.of("frontendApi.url", "左斜杆不能连续出现"));
        }
        if (!url.trim().matches("[\\d\\w_/]+")) {
            throw new BusinessException(Map.of("frontendApi.url", "只能由数字、26个英文字母、下划线和或者左斜杆组成！"));
        }

        //?  匹配任何单字符
        //*  匹配0或者任意数量的字符
        //** 匹配0或者更多的目录
        Set<String> pathSet = DisablePath.getPath();//禁止路径
        if (!pathSet.isEmpty()) {
            for (String path : pathSet) {
                boolean flag = matcher.match(path, url.trim().toLowerCase());  //参数一: ant匹配风格   参数二:输入URL
                if (flag) {
                    throw new BusinessException(Map.of("frontendApi.url", "当前URL禁止使用"));
                }
            }
        }


        List<FrontendApi> frontendApiList = frontendApiRepository.findFrontendApiByUrl(url.trim());
        if(frontendApiList != null && !frontendApiList.isEmpty()){
            for(FrontendApi frontendApi : frontendApiList){
                if(frontendApiId != null && frontendApiId.equals(frontendApi.getId())){
                    continue;
                }
                if(httpMethod == null || httpMethod.trim().isEmpty()){
                    throw new BusinessException(Map.of("frontendApi.url", "请求方式和已添加URL路径重合"));
                }
                if(frontendApi.getHttpMethod() != null && !frontendApi.getHttpMethod().trim().isEmpty()){
                    if(httpMethod.trim().equals(frontendApi.getHttpMethod().trim())){
                        throw new BusinessException(Map.of("frontendApi.url", "请求方式重复"));
                    }
                }else{
                    throw new BusinessException(Map.of("frontendApi.url", "已添加URL路径的请求方式已包含当前的请求方式"));
                }
            }
        }
        return true;
    }

    /**
     * 获取自定义动态路由组
     * @return
     */
    public List<DynamicRouteGroupDTO> getAllCustomRouteGroup(){
        // 获取所有自定义路由枚举并按组分组。
        Map<DynamicRouteGroupEnum, List<DynamicRouteEnum>> groupedEnums = Arrays.stream(DynamicRouteEnum.values())
                .filter(routeEnum -> routeEnum.getGroup() != null && routeEnum.getType() == 2)
                .collect(Collectors.groupingBy(DynamicRouteEnum::getGroup));

        // 获取所有路由组的 DTO 列表
        List<DynamicRouteGroupDTO> allRouteGroupDTOs = DynamicRouteGroupDTO.fromEnumList();

        //遍历路由组列表，并填充每个组的路由集合
        for (DynamicRouteGroupDTO groupDTO : allRouteGroupDTOs) {
            // 根据 DTO 的枚举名称获取对应的枚举，这是安全的
            DynamicRouteGroupEnum groupEnum = DynamicRouteGroupEnum.valueOf(groupDTO.getGroupEnum());

            // 从分组后的 Map 中查找该组对应的路由枚举列表，如果没有则返回一个空列表
            List<DynamicRouteEnum> enumsInGroup = groupedEnums.getOrDefault(groupEnum, new ArrayList<>());

            // 将每个路由枚举转换为 DTO
            List<DynamicRouteDTO> routesInGroup = enumsInGroup.stream()
                    .map(DynamicRouteDTO::fromEnum)
                    .collect(Collectors.toList());

            // 将转换后的 DTO 列表填充到路由组 DTO 中
            groupDTO.setDynamicRouteList(routesInGroup);
        }
        return allRouteGroupDTOs;
    }

    /**
     * 处理功能配置
     * @param frontendApiForm 前台API功能参数配置对象表单
     * @param imageAdFile 图片广告文件
     * @param request 请求信息
     */
    private void processConfigData(FrontendApiForm frontendApiForm, MultipartFile[] imageAdFile, HttpServletRequest request) {

        if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6010100.name())) { //话题分页

            ConfigTopicPage configTopicPage = frontendApiForm.getConfigTopicPage();
            if (configTopicPage != null) {
                configTopicPage.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configTopicPage));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6010300.name())) {//评论分页
            ConfigCommentPage configCommentPage = frontendApiForm.getConfigCommentPage();
            if (configCommentPage != null) {
                configCommentPage.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configCommentPage));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6011300.name())) {//相似话题
            ConfigSimilarTopic configSimilarTopic = frontendApiForm.getConfigSimilarTopic();
            if (configSimilarTopic != null) {
                configSimilarTopic.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configSimilarTopic));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6011400.name())) {//热门话题
            ConfigHotTopic configHotTopic = frontendApiForm.getConfigHotTopic();
            if (configHotTopic != null) {
                configHotTopic.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configHotTopic));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6011500.name())) {//话题精华分页
            ConfigTopicFeaturedPage configTopicFeaturedPage = frontendApiForm.getConfigTopicFeaturedPage();
            if (configTopicFeaturedPage != null) {
                configTopicFeaturedPage.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configTopicFeaturedPage));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6020100.name())) {//问题分页
            ConfigQuestionPage configQuestionPage = frontendApiForm.getConfigQuestionPage();
            if (configQuestionPage != null) {
                //标签
                if (configQuestionPage.getTagId() != null && configQuestionPage.getTagId() > 0L && !configQuestionPage.isTag_transferPrameter()) {
                    QuestionTag questionTag = questionTagRepository.findById(configQuestionPage.getTagId());
                    if (questionTag != null) {
                        configQuestionPage.setTagId(questionTag.getId());
                        configQuestionPage.setTagName(questionTag.getName());
                    }
                }
                configQuestionPage.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configQuestionPage));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6020300.name())) {//答案分页
            ConfigAnswerPage configAnswerPage = frontendApiForm.getConfigAnswerPage();
            if (configAnswerPage != null) {
                configAnswerPage.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configAnswerPage));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6021100.name())) {//相似问题
            ConfigSimilarQuestion configSimilarQuestion = frontendApiForm.getConfigSimilarQuestion();
            if (configSimilarQuestion != null) {
                configSimilarQuestion.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configSimilarQuestion));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6070200.name())) {//领取红包用户分页
            ConfigRedEnvelopeUserPage configRedEnvelopeUserPage = frontendApiForm.getConfigRedEnvelopeUserPage();
            if (configRedEnvelopeUserPage != null) {
                configRedEnvelopeUserPage.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configRedEnvelopeUserPage));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6100100.name())) {//图片广告
            List<ConfigImageAd> configImageAdList = frontendApiForm.getConfigImageAdList();
            if (configImageAdList != null && !configImageAdList.isEmpty()) {

                Map<String, String> errors = new HashMap<>();
                for (int i = 0; i < configImageAdList.size(); i++) {
                    ConfigImageAd configImageAd = configImageAdList.get(i);

                    MultipartFile file = imageAdFile[i];
                    String directory = "";
                    String fileName = "";

                    if (file != null && !file.isEmpty()) {
                        //验证文件类型
                        List<String> formatList = new ArrayList<String>();
                        formatList.add("gif");
                        formatList.add("jpg");
                        formatList.add("jpeg");
                        formatList.add("bmp");
                        formatList.add("png");
                        formatList.add("webp");
                        formatList.add("svg");
                        boolean isFileTypeValid = FileUtil.validateFileSuffix(file.getOriginalFilename(), formatList);
                        if (!isFileTypeValid) {
                            throw new BusinessException(Map.of("configImageAd[" + i + "].fileName", "图片格式错误"));
                        }
                        //取得文件后缀
                        String ext = FileUtil.getExtension(file.getOriginalFilename());
                        //文件保存目录
                        String pathDir = "file" + File.separator + "frontendApi" + File.separator+"image"+ File.separator;
                        //构建文件名称
                        fileName = UUIDUtil.getUUID32() + "." + ext;
                        directory = "file/frontendApi/image/";

                        //生成文件保存目录
                        FileUtil.createFolder(pathDir);

                        //添加文件锁
                        try {
                            fileComponent.addLock("file" + File.separator + "frontendApi" + File.separator + "lock" + File.separator, "image_"+fileName);
                            //保存文件
                            fileComponent.writeFile(pathDir, fileName, file.getBytes());
                        } catch (IOException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error("上传图片错误", e);
                            }
                            throw new BusinessException(Map.of("configImageAd[" + i + "].fileName", "上传图片错误"));
                        }
                    } else {//如果图片已上传
                        if (configImageAd.getImagePath() != null && !configImageAd.getImagePath().trim().isEmpty()) {
                            String imagePath = textFilterComponent.deleteBindURL(request, configImageAd.getImagePath().trim());

                            if (imagePath != null && imagePath.length() > 17) {
                                //取得文件名称
                                fileName = FileUtil.getName(imagePath);
                                //旧路径必须为file/frontendApi/开头
                                if (!imagePath.substring(0, 17).equals("file/frontendApi/")) {
                                    continue;
                                }
                                directory = "file/frontendApi/image/";
                            }
                        }


                    }
                    if (!errors.isEmpty()) {
                        throw new BusinessException(errors);
                    }
                    configImageAd.setId(UUIDUtil.getUUID32());
                    configImageAd.setImagePath(directory + fileName);
                }


                //
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configImageAdList));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6110100.name())) {//在线帮助分页
            ConfigHelpPage configHelpPage = frontendApiForm.getConfigHelpPage();
            if (configHelpPage != null) {
                //标签
                if (configHelpPage.getHelpTypeId() != null && configHelpPage.getHelpTypeId() > 0L && !configHelpPage.isHelpType_transferPrameter()) {
                    HelpType helpType = helpTypeRepository.findById(configHelpPage.getHelpTypeId());
                    if (helpType != null) {
                        configHelpPage.setHelpTypeId(helpType.getId());
                        configHelpPage.setHelpTypeName(helpType.getName());
                    }
                }
                configHelpPage.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configHelpPage));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6110200.name())) {//推荐在线帮助
            ConfigRecommendedHelp configRecommendedHelp = frontendApiForm.getConfigRecommendedHelp();
            //有效的在线帮助
            List<Help> validRecommendedHelpList = new ArrayList<Help>();//id:在线帮助Id name:在线帮助名称
            if (configRecommendedHelp != null) {
                List<Long> helpIdList = configRecommendedHelp.getRecommendHelpList().stream()
                        .filter(help -> help.getId() != null && help.getId() > 0L)
                        .map(Help::getId)
                        .toList();

                List<Help> helpList = helpRepository.findByIdList(helpIdList);
                if (helpList != null && !helpList.isEmpty()) {
                    for (Long id : helpIdList) {//按添加的顺序
                        for (Help help : helpList) {
                            if (id.equals(help.getId())) {
                                validRecommendedHelpList.add(new Help(help.getId(), help.getName()));
                                break;
                            }
                        }
                    }
                }
                configRecommendedHelp.setId(UUIDUtil.getUUID32());
                configRecommendedHelp.setRecommendHelpList(validRecommendedHelpList);
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configRecommendedHelp));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6150100.name())) {//自定义HTML
            ConfigCustomHtml configCustomHtml = frontendApiForm.getConfigCustomHtml();
            if (configCustomHtml != null) {
                configCustomHtml.setId(UUIDUtil.getUUID32());
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configCustomHtml));
            }
        } else if (frontendApiForm.getFrontendApi().getRouteEnumMapper().equals(DynamicRouteEnum.CUSTOM_6150200.name())) {//热门搜索词
            ConfigHotSearchTerm configHotSearchTerm = frontendApiForm.getConfigHotSearchTerm();
            if (configHotSearchTerm != null) {
                List<String> searchTermList = configHotSearchTerm.getSearchTermList().stream()
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .toList();
                configHotSearchTerm.setId(UUIDUtil.getUUID32());
                configHotSearchTerm.setSearchTermList(searchTermList);
                frontendApiForm.getFrontendApi().setConfigData(jsonComponent.toJSONString(configHotSearchTerm));
            }
        }
    }


    /**
     * 处理云存储系统（如SeaweedFS, MinIO, OSS）的文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleCloudUpload(String dir, String fileName, Map<String, Object> result){
        if (fileName == null || fileName.trim().isEmpty()) {
            result.put("error", 1);
            result.put("message", "名称不能为空");
            return result;
        }

        List<String> formatList = getAllowFormats(dir,result);
        if (formatList == null || formatList.isEmpty()) {
            return result;
        }

        if(!FileUtil.validateFileSuffix(fileName.trim(),formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }


        //文件锁目录
        String lockPathDir = "file"+File.separator+"frontendApi"+File.separator+"lock"+File.separator;
        //取得文件后缀
        String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ "." + suffix;


        //生成锁文件
        try {
            fileComponent.addLock(lockPathDir,dir+"_"+newFileName);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }
        String presigne = fileComponent.createPresigned("file/frontendApi/"+dir+"/"+newFileName,null);//创建预签名


        //返回预签名URL
        result.put("error", 0);//0成功  1错误
        result.put("url", presigne);
        result.put("title", HtmlEscape.escape(fileName));//旧文件名称
        return result;
    }

    /**
     * 根据文件类型获取允许上传的格式列表。
     */
    private List<String> getAllowFormats(String dir,Map<String, Object> result) {
        List<String> formatList = new ArrayList<>();
        switch (dir) {
            case "image":
                formatList = CommentedProperties.readRichTextAllowImageUploadFormat();
                break;
            case "media":
                formatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
                break;
            case "file":
                formatList = CommentedProperties.readRichTextAllowFileUploadFormat();
                break;
            default:
                result.put("error", 1);
                result.put("message", "缺少dir参数");
        }
        return formatList;
    }

    /**
     * 处理本地文件系统上传
     * @param dir dir 上传类型，分别为image、media、file
     * @param file 文件
     * @param fileServerAddress 文件服务器地址
     * @param result 返回内容
     * @return
     * @throws Exception
     */
    private Map<String, Object> handleLocalUpload(String dir, MultipartFile file, String fileServerAddress, Map<String, Object> result){
        if (file == null || file.isEmpty()) {
            result.put("error", 1);
            result.put("message", "文件不能为空");
            return result;
        }

        //当前文件名称
        String sourceFileName = file.getOriginalFilename();
        String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();
        List<String> formatList = getAllowFormats(dir,result);
        if (formatList == null || formatList.isEmpty()) {
            return result;
        }
        //验证文件类型
        if(!FileUtil.validateFileSuffix(sourceFileName.trim(),formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }

        //文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
        String pathDir = "file"+File.separator+"frontendApi"+File.separator +dir+ File.separator;
        //文件锁目录
        String lockPathDir = "file"+File.separator+"frontendApi"+File.separator+"lock"+File.separator;

        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ "." + suffix;

        //生成文件保存目录
        FileUtil.createFolder(pathDir);
        //生成锁文件保存目录
        FileUtil.createFolder(lockPathDir);

        try {
            //生成锁文件
            fileComponent.addLock(lockPathDir,dir+"_"+newFileName);
            //保存文件
            fileComponent.writeFile(pathDir, newFileName,file.getBytes());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }



        //上传成功
        result.put("error", 0);//0成功  1错误
        result.put("url", fileServerAddress+"file/frontendApi/"+dir+"/"+newFileName);
        return result;
    }


}