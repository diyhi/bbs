package cms.service.filterWord.impl;

import cms.component.fileSystem.localImpl.LocalFileComponent;
import cms.config.BusinessException;
import cms.dto.filterWord.FilterWord;
import cms.model.filterWord.RiskSensitiveWords;
import cms.repository.filterWord.RiskSensitiveWordsRepository;
import cms.service.filterWord.FilterWordService;
import cms.utils.FileSize;
import cms.utils.FileUtil;
import cms.utils.PathUtil;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 过滤词服务
 */
@Service
public class FilterWordServiceImpl implements FilterWordService {
    private static final Logger logger = LogManager.getLogger(FilterWordServiceImpl.class);


    @Resource
    RiskSensitiveWordsRepository riskSensitiveWordsRepository;
    @Resource
    LocalFileComponent localFileComponent;



    /**
     * 获取过滤词展示信息
     * @return
     */
    public Map<String,Object> getFilterWordView(){
        Map<String,Object> returnValue = new HashMap<String,Object>();


        String path = PathUtil.defaultExternalDirectory()+File.separator+"data"+File.separator+"filterWord"+File.separator;
        File file = new File(path+"word.txt");

        FilterWord filterWord = null;


        if(file.exists()){
            filterWord = new FilterWord();
            List<String> wordList = FileUtil.readLines(file,"utf-8");
            if(wordList != null){
                filterWord.setWordNumber(wordList.size());
                for(int i=0; i<wordList.size(); i++){
                    filterWord.addBeforeWord(wordList.get(i));
                    if(i == 2){
                        break;
                    }
                }
            }

            filterWord.setSize(FileSize.conversion(file.length()));
            filterWord.setLastModified(new Date(file.lastModified()));
        }
        returnValue.put("filterWord", filterWord);


        List<RiskSensitiveWords> riskSensitiveWordsList = riskSensitiveWordsRepository.findAllRiskSensitiveWords();
        returnValue.put("riskSensitiveWordsList", riskSensitiveWordsList);
        return  returnValue;
    }

    /**
     * 词库上传
     * @param file 文件
     */
    public void uploadFilterWord(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw new BusinessException(Map.of("file", "请选择文件"));
        }

        List<String> validFormats = Collections.singletonList("txt");
        boolean isFormatValid = FileUtil.validateFileSuffix(file.getOriginalFilename(), validFormats);
        if(!isFormatValid){
            throw new BusinessException(Map.of("file", "文件格式错误"));
        }

        //文件保存目录
        String pathDir = "data"+File.separator+"filterWord"+File.separator;
        //生成文件保存目录
        FileUtil.createFolder(pathDir);
        //保存文件
        try {
            localFileComponent.writeFile(pathDir, "word.txt",file.getBytes());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("上传词库错误", e);
            }
            throw new BusinessException(Map.of("file", "上传词库错误"));
        }
    }

    /**
     * 删除词库
     */
    public void deleteFilterWord(){
        Boolean state = localFileComponent.deleteFile("data"+File.separator+"filterWord"+File.separator+"word.txt");
        if(state == null || !state){
            throw new BusinessException(Map.of("filterWord", "删除失败"));
        }
    }

}