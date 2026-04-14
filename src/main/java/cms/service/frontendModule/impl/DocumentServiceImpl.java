package cms.service.frontendModule.impl;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import cms.config.BusinessException;
import cms.dto.frontendModule.DynamicRouteDTO;
import cms.service.frontendModule.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 前台文档服务
 */
@Service
public class DocumentServiceImpl implements DocumentService {
    private static final Logger logger = LogManager.getLogger(DocumentServiceImpl.class);



    /**
     * 获取所有动态路由
     * @return
     */
    public List<DynamicRouteDTO> getAllDynamicRoute(){
        return DynamicRouteDTO.fromEnumList();
    }

    /**
     * 获取文档内容
     * @param routeEnumMapper 动态路由枚举
     * @return
     */
    public String getDocumentContent(String routeEnumMapper){
        if(routeEnumMapper == null || routeEnumMapper.trim().isEmpty()){
            throw new BusinessException(Map.of("document", "动态路由枚举不能为空"));
        }

        ClassPathResource classPathResource = new ClassPathResource("data/document/"+routeEnumMapper.trim()+".md");
        if(classPathResource.isReadable()){
            try (InputStream inputStream = classPathResource.getInputStream()){
                return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }catch (IOException e) {
                // TODO Auto-generated catch block
                //e1.printStackTrace();
                if (logger.isErrorEnabled()) {
                    logger.error("读取文档内容IO错误",e);
                }
            }
        }
        return "";
    }


}