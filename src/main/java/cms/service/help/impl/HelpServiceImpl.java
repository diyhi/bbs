package cms.service.help.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.config.BusinessException;
import cms.dto.*;
import cms.model.help.Help;
import cms.model.help.HelpType;
import cms.model.setting.SystemSetting;
import cms.repository.help.HelpRepository;
import cms.repository.help.HelpTypeRepository;
import cms.repository.setting.SettingRepository;
import cms.service.help.HelpService;
import cms.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 帮助服务
 */
@Service
public class HelpServiceImpl implements HelpService {
    private static final Logger logger = LogManager.getLogger(HelpServiceImpl.class);


    @Resource HelpRepository helpRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;
    @Resource HelpTypeRepository helpTypeRepository;

    //Html过滤结果
    private record HtmlFilterResult(HtmlProcessingResult htmlProcessingResult, String formatterMarkdown,String content) {
    }

    /**
     * 获取帮助
     * @param helpId 帮助Id
     * @param host 域名
     * @param serverAddressList 文件服务器地址集合
     * @return
     */
    public Help getHelp(Long helpId, String host,List<String> serverAddressList){
        if (helpId == null || helpId < 0L) {
            throw new BusinessException(Map.of("helpId", "帮助Id不存在"));
        }
        Help help = helpRepository.findById(helpId);
        if(help == null){
            throw new BusinessException(Map.of("helpId", "帮助不存在"));
        }
        HelpType helpType = helpTypeRepository.findById(help.getHelpTypeId());
        if(helpType != null){
            help.setHelpTypeName(helpType.getName());
        }
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        //处理富文本路径
        help.setContent(fileComponent.processRichTextFilePath(help.getContent(),"help"));

        if(help.getContent() != null && !help.getContent().trim().isEmpty() && systemSetting.getFileSecureLinkSecret() != null && !systemSetting.getFileSecureLinkSecret().trim().isEmpty()){

            //解析上传的文件完整路径名称
            Map<String,String> analysisFullFileNameMap = textFilterComponent.analysisFullFileName(help.getContent(),"help",serverAddressList);
            if(analysisFullFileNameMap != null && analysisFullFileNameMap.size() >0){


                Map<String,String> newFullFileNameMap = new HashMap<String,String>();//新的完整路径名称 key: 完整路径名称 value: 重定向接口
                for (Map.Entry<String,String> entry : analysisFullFileNameMap.entrySet()) {

                    newFullFileNameMap.put(entry.getKey(), host+ SecureLink.createDownloadRedirectLink(entry.getKey(),entry.getValue(),-1L,systemSetting.getFileSecureLinkSecret()));
                }

                help.setContent(textFilterComponent.processFullFileName(help.getContent(),"help",newFullFileNameMap,serverAddressList));

            }
        }

        if(help.getContent() != null && !help.getContent().trim().isEmpty()){
            //处理视频播放器标签
            String content = textFilterComponent.processVideoPlayer(host,help.getContent(),-1L,systemSetting.getFileSecureLinkSecret());
            help.setContent(content);
        }
        return help;
    }

    /**
     * 获取帮助列表
     * @param visible 是否显示
     * @param page 页码
     * @return
     */
    public PageView<Help> getHelpList(Boolean visible, int page){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        if(visible != null && !visible){
            jpql.append(" and o.visible=?"+ (params.size()+1));
            params.add(false);
        }else{
            jpql.append(" and o.visible=?"+ (params.size()+1));
            params.add(true);
        }
        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Help> pageView = new PageView<Help>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据typeid字段降序排序


        //调用分页算法类
        QueryResult<Help> qr = helpRepository.getScrollData(Help.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);


        pageView.setQueryResult(qr);

        List<Long> helpTypeIdList = new ArrayList<Long>();

        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Help help :pageView.getRecords()){
                helpTypeIdList.add(help.getHelpTypeId());
            }
            //查询分类名称
            List<HelpType> helpTypeList = helpTypeRepository.findByIdList(helpTypeIdList);
            if(helpTypeList != null && helpTypeList.size() >0){
                for(Help help :pageView.getRecords()){
                    for(HelpType helpType : helpTypeList){
                        if(help.getHelpTypeId().equals(helpType.getId())){
                            help.setHelpTypeName(helpType.getName());
                            break;
                        }
                    }
                }
            }

        }
        return pageView;
    }

    /**
     * 添加帮助
     * @param helpTypeId 帮助分类Id
     * @param name 名称
     * @param content 内容
     * @param isMarkdown 是否为Markdown格式数据
     * @param markdownContent Markdown内容
     * @param request 请求信息
     */
    public void addHelp(Long helpTypeId,String name,String content,Boolean isMarkdown,String markdownContent, HttpServletRequest request){

        validateInput(helpTypeId, name,content);


        //复制文件锁
        List<String> fileLock_list = new ArrayList<String>();
        List<String> oldPathFileList = new ArrayList<String>();//旧路径文件

        // 处理内容过滤和文件路径
        HtmlFilterResult htmlFilterResult = processAndFilterContent(request, content, isMarkdown, markdownContent);

        if(htmlFilterResult == null){
            throw new BusinessException(Map.of("content", "帮助内容不能为空"));
        }
        content = htmlFilterResult.content;

        if(content == null || content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "帮助内容不能为空"));
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

        //修改文件路径
        FilePathUpdateResult filePathUpdateResult = textFilterComponent.updateTypePath(value,"help",helpTypeId);
        String new_value = filePathUpdateResult.getHtmlContent();
        Map<String,String> file_path = filePathUpdateResult.getOldToNewPathMap();//key:旧文件路径  value:新文件路径

        if(file_path != null && file_path.size() >0){
            for(Map.Entry<String,String> entry : file_path.entrySet()) {//key:旧文件路径  value:新文件路径

                //旧路径 左斜杆路径转为系统路径
                String old_path_Delimiter = FileUtil.toSystemPath(entry.getKey());

                //新路径 左斜杆路径转为系统路径
                String new_path_Delimiter = FileUtil.toSystemPath(entry.getValue());

                //替换路径中的..号
                old_path_Delimiter = FileUtil.toRelativePath(old_path_Delimiter);
                new_path_Delimiter = FileUtil.toRelativePath(new_path_Delimiter);

                try {
                    //复制文件到新路径
                    fileComponent.copyFile(old_path_Delimiter, new_path_Delimiter);
                    //取得文件名称
                    String fileName = FileUtil.getName(entry.getKey());

                    //新建文件锁到新路径
                    //生成锁文件名称
                    String lockFileName = Strings.CS.replaceOnce(entry.getValue(), "file/help/", "")+fileName;
                    lockFileName = lockFileName.replaceAll("/", "_");

                    fileLock_list.add(lockFileName);
                    //添加文件锁
                    fileComponent.addLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,lockFileName);
                    //旧路径文件
                    oldPathFileList.add(old_path_Delimiter);
                } catch (IOException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("修改文件路径错误",e);
                    }
                }
            }
        }


        //不含标签内容
        String text = textFilterComponent.filterText(content);
        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();

        if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                && !htmlFilterResult.htmlProcessingResult.isHasFile()
                && !htmlFilterResult.htmlProcessingResult.isHasMedia()
                && !htmlFilterResult.htmlProcessingResult.isHasMap()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "过滤标签内容后不能为空"));
        }


        Help help = new Help();
        String username = "";//用户名称

        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        help.setUserName(username);
        help.setContent(new_value);
        help.setIsMarkdown(isMarkdown == null ? false:isMarkdown);

        help.setHelpTypeId(helpTypeId);
        help.setName(name);

        helpRepository.saveHelp(help);



        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && htmlFilterResult.htmlProcessingResult.getImageNameList().size() >0){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){

                if(imageName != null && !imageName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

                }
            }
        }
        //音视频
        if(htmlFilterResult.htmlProcessingResult.getMediaNameList() != null && htmlFilterResult.htmlProcessingResult.getMediaNameList().size() >0){
            for(String mediaName :htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                if(mediaName != null && !mediaName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                }
            }
        }
        //文件
        if(htmlFilterResult.htmlProcessingResult != null && htmlFilterResult.htmlProcessingResult.getFileNameList().size() >0){
            for(String fileName :htmlFilterResult.htmlProcessingResult.getFileNameList()){
                if(fileName != null && !fileName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }
        //删除复制文件锁
        if(fileLock_list.size() >0){
            for(String fileName :fileLock_list){
                if(fileName != null && !fileName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }

        //删除旧路径文件
        if(oldPathFileList.size() >0){
            for(String oldPathFile :oldPathFileList){
                //替换路径中的..号
                oldPathFile = FileUtil.toRelativePath(oldPathFile);
                Boolean state = fileComponent.deleteFile(oldPathFile);
                if(state != null && !state){
                    //替换指定的字符，只替换第一次出现的
                    oldPathFile = Strings.CS.replaceOnce(oldPathFile, "file"+File.separator+"help"+File.separator, "");

                    //创建删除失败文件
                    fileComponent.failedStateFile("file"+File.separator+"help"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));

                }
            }
        }
    }


    /**
     * 获取修改帮助界面信息
     *
     * @param helpId 帮助Id
     * @return
     */
    public Help getEditHelpViewModel(Long helpId) {
        if (helpId == null || helpId < 0L) {
            throw new BusinessException(Map.of("helpId", "帮助Id不存在"));
        }
        Help help = helpRepository.findById(helpId);
        if(help == null){
            throw new BusinessException(Map.of("helpId", "帮助不存在"));
        }
        HelpType helpType = helpTypeRepository.findById(help.getHelpTypeId());
        if(helpType != null){
            help.setHelpTypeName(helpType.getName());
        }

        if(help.getContent() != null && !help.getContent().trim().isEmpty()){
            //处理富文本路径
            help.setContent(fileComponent.processRichTextFilePath(help.getContent(),"help"));
        }

        return help;
    }

    /**
     * 修改帮助
     * @param helpId 帮助Id
     * @param helpTypeId 帮助分类Id
     * @param name 名称
     * @param content 内容
     * @param isMarkdown 是否为Markdown格式数据
     * @param markdownContent Markdown内容
     * @param request 请求信息
     */
    public void editHelp(Long helpId, Long helpTypeId,String name,String content,Boolean isMarkdown,String markdownContent, HttpServletRequest request){

        validateInput(helpTypeId, name,content);

        if (helpId == null || helpId < 0L) {
            throw new BusinessException(Map.of("helpId", "帮助Id不存在"));
        }
        Help help = helpRepository.findById(helpId);
        if(help == null){
            throw new BusinessException(Map.of("helpId", "帮助不存在"));
        }
        String old_content = help.getContent();
        //复制文件锁
        List<String> fileLock_list = new ArrayList<String>();
        List<String> oldPathFileList = new ArrayList<String>();//旧路径文件

        // 处理内容过滤和文件路径
        HtmlFilterResult htmlFilterResult = processAndFilterContent(request, content, isMarkdown, markdownContent);

        if(htmlFilterResult == null){
            throw new BusinessException(Map.of("content", "帮助内容不能为空"));
        }
        content = htmlFilterResult.content;

        if(content == null || content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "帮助内容不能为空"));
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();


        //修改文件路径
        FilePathUpdateResult filePathUpdateResult = textFilterComponent.updateTypePath(value,"help",helpTypeId);
        String new_value = filePathUpdateResult.getHtmlContent();
        Map<String,String> file_path = filePathUpdateResult.getOldToNewPathMap();//key:旧文件路径  value:新文件路径

        if(file_path != null && file_path.size() >0){
            for(Map.Entry<String,String> entry : file_path.entrySet()) {//key:旧文件路径  value:新文件路径

                //旧路径 左斜杆路径转为系统路径
                String old_path_Delimiter = FileUtil.toSystemPath(entry.getKey());

                //新路径 左斜杆路径转为系统路径
                String new_path_Delimiter = FileUtil.toSystemPath(entry.getValue());

                //替换路径中的..号
                old_path_Delimiter = FileUtil.toRelativePath(old_path_Delimiter);
                new_path_Delimiter = FileUtil.toRelativePath(new_path_Delimiter);

                try {
                    //复制文件到新路径
                    fileComponent.copyFile(old_path_Delimiter, new_path_Delimiter);
                    //取得文件名称
                    String fileName = FileUtil.getName(entry.getKey());

                    //新建文件锁到新路径
                    //生成锁文件名称
                    String lockFileName = Strings.CS.replaceOnce(entry.getValue(), "file/help/", "")+fileName;
                    lockFileName = lockFileName.replaceAll("/", "_");

                    fileLock_list.add(lockFileName);
                    //添加文件锁
                    fileComponent.addLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,lockFileName);
                    //旧路径文件
                    oldPathFileList.add(old_path_Delimiter);



                    //判断路径类型
                    int type = textFilterComponent.isPathType(entry.getKey(),"help");
                    if(type == 30 && !Strings.CI.startsWith(entry.getKey(), "file/help/null/")){//如果不是新影音

                        String newDirectory = FileUtil.getFullPath(old_path_Delimiter)+FileUtil.getBaseName(entry.getKey())+"/";
                        fileComponent.copyDirectory(newDirectory,new_path_Delimiter);

                    }

                } catch (IOException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("修改文件路径错误",e);
                    }
                }


            }
        }


        //不含标签内容
        String text = textFilterComponent.filterText(content);
        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();

        if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                && !htmlFilterResult.htmlProcessingResult.isHasFile()
                && !htmlFilterResult.htmlProcessingResult.isHasMedia()
                && !htmlFilterResult.htmlProcessingResult.isHasMap()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "过滤标签内容后不能为空"));
        }

        String username = "";//用户名称

        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        Help newHelp = new Help();
        newHelp.setId(helpId);
        newHelp.setUserName(username);
        newHelp.setContent(new_value);
        newHelp.setIsMarkdown(isMarkdown == null ? false:isMarkdown);

        newHelp.setHelpTypeId(helpTypeId);
        newHelp.setName(name);

        helpRepository.updateHelp(newHelp);


        ReadPathResult readPathResult = textFilterComponent.readPathName(old_content,"help");



        //旧图片
        List<String> old_imageNameList = readPathResult.getImageNameList();
        if(old_imageNameList != null && old_imageNameList.size() >0){

            Iterator<String> iter = old_imageNameList.iterator();
            while (iter.hasNext()) {
                String imageName = iter.next();
                for(String new_imageName : htmlFilterResult.htmlProcessingResult.getImageNameList()){
                    if(imageName.equals("file/help/"+new_imageName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_imageNameList.size() >0){
                for(String imageName : old_imageNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(imageName));
                }

            }
        }

        //旧影音
        List<String> old_mediaNameList = readPathResult.getMediaNameList();
        if(old_mediaNameList != null && old_mediaNameList.size() >0){
            Iterator<String> iter = old_mediaNameList.iterator();
            while (iter.hasNext()) {
                String mediaName = iter.next();
                for(String new_mediaName : htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                    if(mediaName.equals("file/help/"+new_mediaName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_mediaNameList.size() >0){
                for(String mediaName : old_mediaNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(mediaName));

                }

            }
        }

        //旧文件
        List<String> old_fileNameList = readPathResult.getFileNameList();
        if(old_fileNameList != null && old_fileNameList.size() >0){
            Iterator<String> iter = old_fileNameList.iterator();
            while (iter.hasNext()) {
                String fileName = iter.next();
                for(String new_fileName : htmlFilterResult.htmlProcessingResult.getFileNameList()){
                    if(fileName.equals("file/help/"+new_fileName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_fileNameList.size() >0){
                for(String fileName : old_fileNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(fileName));
                }

            }
        }



        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && htmlFilterResult.htmlProcessingResult.getImageNameList().size() >0){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){
                if(imageName != null && !imageName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }
            }
        }
        //删除音视频锁
        if(htmlFilterResult.htmlProcessingResult.getMediaNameList() != null && htmlFilterResult.htmlProcessingResult.getMediaNameList().size() >0){
            for(String mediaName :htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                if(mediaName != null && !mediaName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                }
            }
        }
        //删除文件锁
        if(htmlFilterResult.htmlProcessingResult.getFileNameList() != null && htmlFilterResult.htmlProcessingResult.getFileNameList().size() >0){
            for(String fileName :htmlFilterResult.htmlProcessingResult.getFileNameList()){
                if(fileName != null && !fileName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }
        //删除复制文件锁
        if(fileLock_list.size() >0){
            for(String fileName :fileLock_list){
                if(fileName != null && !fileName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }

        //删除旧路径文件
        if(oldPathFileList.size() >0){
            for(String oldPathFile :oldPathFileList){
                //替换路径中的..号
                oldPathFile = FileUtil.toRelativePath(oldPathFile);

                //删除旧路径文件
                Boolean state = fileComponent.deleteFile(oldPathFile);
                if(state != null && !state ){
                    //替换指定的字符，只替换第一次出现的
                    oldPathFile = Strings.CS.replaceOnce(oldPathFile, "file"+File.separator+"help"+File.separator, "");

                    //创建删除失败文件
                    fileComponent.failedStateFile("file"+File.separator+"help"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));

                }
            }
        }
    }


    /**
     * 删除帮助
     * @param helpId 帮助Id组
     */
    public void deleteHelp(Long[] helpId){
        if (helpId == null || helpId.length == 0) {
            throw new BusinessException(Map.of("helpId", "帮助Id不存在"));
        }
        List<Long> helpIdList = Arrays.stream(helpId)
                .filter(id -> id != null && id > 0L)
                .toList();

        if (helpIdList.isEmpty()) {
            throw new BusinessException(Map.of("helpId", "帮助Id为空值"));
        }

        List<Help> helpList = helpRepository.findByIdList(helpIdList);
        if(helpList == null || helpList.isEmpty()){
            throw new BusinessException(Map.of("helpId", "帮助不存在"));
        }


        for(Help help : helpList){
            if(help.isVisible()){//标记删除
                int i = helpRepository.markDelete(help.getHelpTypeId(),help.getId());
            }else{//物理删除
                int i = helpRepository.deleteHelp(help.getHelpTypeId(),help.getId(),0L);

                ReadPathResult readPathResult= textFilterComponent.readPathName(help.getContent(),"help");

                List<String> filePathList = new ArrayList<String>();


                //删除图片
                List<String> imageNameList = readPathResult.getImageNameList();
                for(String imageName :imageNameList){
                    filePathList.add(imageName);

                }
                //删除影音
                List<String> mediaNameList = readPathResult.getMediaNameList();
                for(String mediaName :mediaNameList){
                    filePathList.add(mediaName);

                }
                //删除文件
                List<String> fileNameList = readPathResult.getFileNameList();
                for(String fileName :fileNameList){
                    filePathList.add(fileName);
                }

                for(String filePath :filePathList){
                    //替换路径中的..号
                    filePath = FileUtil.toRelativePath(filePath);
                    filePath = FileUtil.toSystemPath(filePath);
                    //删除旧路径文件
                    Boolean state = fileComponent.deleteFile(filePath);
                    if(state != null && !state){
                        //替换指定的字符，只替换第一次出现的
                        filePath = Strings.CS.replaceOnce(filePath, "file"+File.separator+"help"+File.separator, "");

                        //创建删除失败文件
                        fileComponent.failedStateFile("file"+File.separator+"help"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
                    }
                }

            }
        }
    }

    /**
     * 还原帮助
     * @param helpId 帮助Id组
     */
    public void reductionHelp(Long[] helpId){
        if (helpId == null || helpId.length == 0) {
            throw new BusinessException(Map.of("helpId", "帮助Id不存在"));
        }
        List<Long> helpIdList = Arrays.stream(helpId)
                .filter(id -> id != null && id > 0L)
                .toList();

        if (helpIdList.isEmpty()) {
            throw new BusinessException(Map.of("helpId", "帮助Id为空值"));
        }

        List<Help> helpList = helpRepository.findByIdList(helpIdList);
        if(helpList == null || helpList.isEmpty()){
            throw new BusinessException(Map.of("helpId", "帮助不存在"));
        }

        helpRepository.reductionHelp(helpList);
    }

    /**
     * 移动帮助
     * @param helpId 帮助Id集合
     * @param helpTypeId 分类Id
     */
    public void moveHelp(Long[] helpId,Long helpTypeId){
        if (helpId == null || helpId.length == 0) {
            throw new BusinessException(Map.of("helpId", "帮助Id不存在"));
        }
        List<Long> helpIdList = Arrays.stream(helpId)
                .filter(id -> id != null && id > 0L)
                .toList();

        if (helpIdList.isEmpty()) {
            throw new BusinessException(Map.of("helpId", "帮助Id为空值"));
        }

        List<Help> helpList = helpRepository.findByIdList(helpIdList);
        if(helpList == null || helpList.isEmpty()){
            throw new BusinessException(Map.of("helpId", "帮助不存在"));
        }
        if(helpTypeId == null || helpTypeId <=0){
            throw new BusinessException(Map.of("helpTypeId", "分类不能为空"));
        }

        //复制文件锁
        List<String> fileLock_list = new ArrayList<String>();
        List<String> pathFileList = new ArrayList<String>();//旧路径文件


        if(helpList != null && helpList.size() >0){
            for(Help help : helpList){
                //修改文件路径
                FilePathUpdateResult filePathUpdateResult = textFilterComponent.updateTypePath(help.getContent(),"help",helpTypeId);
                String new_value = filePathUpdateResult.getHtmlContent();

                help.setContent(new_value);
                Map<String,String> file_path = filePathUpdateResult.getOldToNewPathMap();//key:旧文件路径  value:新文件路径

                if(file_path != null && file_path.size() >0){
                    for(Map.Entry<String,String> entry : file_path.entrySet()) {//key:旧文件路径  value:新文件路径

                        //旧路径 左斜杆路径转为系统路径
                        String old_path_Delimiter = FileUtil.toSystemPath(entry.getKey());

                        //新路径 左斜杆路径转为系统路径
                        String new_path_Delimiter = FileUtil.toSystemPath(entry.getValue());

                        //替换路径中的..号
                        old_path_Delimiter = FileUtil.toRelativePath(old_path_Delimiter);
                        new_path_Delimiter = FileUtil.toRelativePath(new_path_Delimiter);


                        try {
                            //复制文件到新路径
                            fileComponent.copyFile(old_path_Delimiter, new_path_Delimiter);

                            //取得文件名称
                            String fileName = FileUtil.getName(entry.getKey());

                            //新建文件锁到新路径
                            //生成锁文件名称
                            String lockFileName = Strings.CS.replaceOnce(entry.getValue(), "file/help/", "")+fileName;
                            lockFileName = lockFileName.replaceAll("/", "_");

                            fileLock_list.add(lockFileName);
                            //添加文件锁
                            fileComponent.addLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,lockFileName);
                            //旧路径文件
                            pathFileList.add(old_path_Delimiter);


                            //判断路径类型
                            int type = textFilterComponent.isPathType(entry.getKey(),"help");
                            if(type == 30){//影音
                                String newDirectory = FileUtil.getFullPath(old_path_Delimiter)+FileUtil.getBaseName(entry.getKey())+"/";
                                fileComponent.copyDirectory(newDirectory,new_path_Delimiter);

                            }
                        } catch (IOException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error("移动文件路径错误",e);
                            }
                        }

                    }
                }
            }
        }

        helpRepository.updateHelp(helpList,helpTypeId);


        //删除复制文件锁
        if(fileLock_list.size() >0){
            for(String fileName :fileLock_list){
                if(fileName != null && !fileName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }
        //删除旧路径文件
        if(pathFileList.size() >0){
            for(String pathFile :pathFileList){
                //替换路径中的..号
                pathFile = FileUtil.toRelativePath(pathFile);

                //删除旧路径文件
                Boolean state = fileComponent.deleteFile(pathFile);
                if(state != null && !state ){
                    //替换指定的字符，只替换第一次出现的
                    pathFile = Strings.CS.replaceOnce(pathFile, "file"+File.separator+"help"+File.separator, "");

                    //创建删除失败文件
                    fileComponent.failedStateFile("file"+File.separator+"help"+File.separator+"lock"+File.separator+FileUtil.toUnderline(pathFile));


                }
            }
        }

    }
    /**
     * 搜索帮助
     * @param keyword 关键调
     * @param page 页码
     * @return
     */
    public PageView<Help> searchHelp(String keyword,int page){
        StringBuffer jpql = new StringBuffer("");
        String sql = "";
        List<Object> params = new ArrayList<Object>();
        //调用分页算法代码
        PageView<Help> pageView = new PageView<Help>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        if(keyword != null && !keyword.trim().isEmpty()){
            String searchName_utf8 = URLDecoder.decode(keyword.trim(), StandardCharsets.UTF_8 );

            jpql.append(" and o.name like ?").append((params.size()+1)).append(" escape '/' ");
            params.add("%"+ cms.utils.StringUtil.escapeSQLLike(searchName_utf8.trim())+"%" );//加上查询参数

        }
        jpql.append(" and o.visible=?").append((params.size()+1));//and o.code=?1
        params.add(true);//设置o.visible=?1是否可见
        //删除第一个and
        sql = StringUtils.difference(" and", jpql.toString());
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        QueryResult<Help> qr = helpRepository.getScrollData(Help.class,firstindex, pageView.getMaxresult(), sql, params.toArray(),orderby);


        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 上传文件
     * @param helpTypeId 分类Id
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param fileServerAddress 文件服务器地址
     * @param file 文件
     * @return
     */
    public Map<String, Object> uploadFile(Long helpTypeId,String dir,String fileName,String fileServerAddress,MultipartFile file) {
        Map<String,Object> returnValue = new HashMap<String,Object>();
        if (dir == null) {
            returnValue.put("error", 1);
            returnValue.put("message", "参数不能为空");
            return returnValue;
        }
        DateTime dateTime = new DateTime();
        String date = dateTime.toString("yyyy-MM-dd");
        int fileSystem = fileComponent.getFileSystem();
        if (fileSystem == 10 || fileSystem == 20 || fileSystem == 30) { // SeaweedFS, MinIO, 阿里云OSS
            return handleCloudUpload(dir, fileName, date, helpTypeId, returnValue);
        } else { // 本地系统
            return handleLocalUpload(dir, file, date, helpTypeId, fileServerAddress, returnValue);
        }
    }

    /**
     * 处理云存储系统（如SeaweedFS, MinIO, OSS）的文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param date 日期
     * @param helpTypeId 分类Id
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleCloudUpload(String dir, String fileName, String date, Long helpTypeId, Map<String, Object> result){
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
        String lockPathDir = "file"+File.separator+"help"+File.separator+"lock"+File.separator;
        //取得文件后缀
        String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ "." + suffix;


        //生成锁文件
        try {
            fileComponent.addLock(lockPathDir,helpTypeId+"_"+date +"_"+dir+"_"+newFileName);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }
        String presigne = fileComponent.createPresigned("file/help/"+helpTypeId+"/"+date+"/"+dir+"/"+newFileName,null);//创建预签名


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
     * @param date 日期
     * @param helpTypeId  分类Id
     * @param fileServerAddress 文件服务器地址
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleLocalUpload(String dir, MultipartFile file, String date, Long helpTypeId, String fileServerAddress, Map<String, Object> result){
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
        String pathDir = "file"+File.separator+"help"+File.separator + helpTypeId + File.separator + date +File.separator +dir+ File.separator;
        //文件锁目录
        String lockPathDir = "file"+File.separator+"help"+File.separator+"lock"+File.separator;

        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ "." + suffix;

        //生成文件保存目录
        FileUtil.createFolder(pathDir);
        //生成锁文件保存目录
        FileUtil.createFolder(lockPathDir);

        try {
            //生成锁文件
            fileComponent.addLock(lockPathDir,helpTypeId+"_"+date +"_"+dir+"_"+newFileName);
            //保存文件
            fileComponent.writeFile(pathDir, newFileName,file.getBytes());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }



        //上传成功
        result.put("error", 0);//0成功  1错误
        result.put("url", fileServerAddress+"file/help/"+helpTypeId+"/"+date+"/"+dir+"/"+newFileName);
        result.put("title", HtmlEscape.escape(file.getOriginalFilename()));//旧文件名称
        return result;
    }




    /**
     * 校验表单字段
     * @param helpTypeId 分类Id
     * @param name 名称
     */
    private void validateInput(Long helpTypeId, String name,String content) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "帮助名称不能为空");
        }
        if (name != null && name.length() > 50) {
            errors.put("name", "不能大于100个字符");
        }
        if (helpTypeId == null || helpTypeId <= 0L) {
            errors.put("helpTypeId", "帮助分类不能为空");
        }
        HelpType helpType = helpTypeRepository.findById(helpTypeId);
        if(helpType == null){
            errors.put("helpTypeId", "帮助不存在");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }
    }



    /**
     * 处理并过滤内容
     * @param request 请求信息
     * @param content 富文本内容
     * @param isMarkdown 是否为markdown格式
     * @param markdownContent markdown格式内容
     * @return HtmlFilterResult Html过滤结果
     */
    private HtmlFilterResult processAndFilterContent(HttpServletRequest request, String content, Boolean isMarkdown, String markdownContent) {

        //过滤标签
        content = textFilterComponent.filterTag(request,content);
        HtmlProcessingResult htmlProcessingResult = textFilterComponent.filterHtml(request,content,"help",null);
        return new HtmlFilterResult(htmlProcessingResult,null,content);

    }

}