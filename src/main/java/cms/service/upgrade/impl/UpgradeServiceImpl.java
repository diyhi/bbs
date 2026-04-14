package cms.service.upgrade.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.localImpl.LocalFileComponent;
import cms.component.upgrade.UpgradeCacheManager;
import cms.component.upgrade.UpgradeComponent;
import cms.config.BusinessException;
import cms.model.upgrade.UpgradeLog;
import cms.model.upgrade.UpgradeSystem;
import cms.repository.upgrade.UpgradeRepository;
import cms.service.upgrade.UpgradeService;
import cms.utils.CommentedProperties;
import cms.utils.FileUtil;
import cms.utils.PathUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 升级服务
 */
@Service
public class UpgradeServiceImpl implements UpgradeService {
    private static final Logger logger = LogManager.getLogger(UpgradeServiceImpl.class);


    @Resource
    UpgradeRepository upgradeRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    UpgradeComponent upgradeComponent;
    @Resource
    LocalFileComponent localFileComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource UpgradeCacheManager upgradeCacheManager;


    /**
     * 获取升级列表
     * @return
     */
    public Map<String,Object> getUpgradeSystemList(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //读取当前系统版本
        String currentVersion = upgradeComponent.readCurrentVersion();


        List<UpgradeSystem> upgradeSystemList = upgradeRepository.findAllUpgradeSystem();
        UpgradeSystem notCompletedUpgrade = null;//未完成升级

        boolean isCompletedUpgrade = false;//是否完成升级


        if(upgradeSystemList != null && upgradeSystemList.size() >0){
            for(UpgradeSystem upgradeSystem : upgradeSystemList){
                //删除最后一个逗号
                String _upgradeLog = StringUtils.substringBeforeLast(upgradeSystem.getUpgradeLog(), ",");//从右往左截取到相等的字符,保留左边的

                List<UpgradeLog> upgradeLogList = jsonComponent.toGenericObject(_upgradeLog+"]", new TypeReference< List<UpgradeLog> >(){});
                upgradeSystem.setUpgradeLogList(upgradeLogList);
            }

            for(int i =0; i<upgradeSystemList.size(); i++ ){
                UpgradeSystem upgradeSystem = upgradeSystemList.get(i);

                if(upgradeSystem.getRunningStatus() <9999){
                    //未完成升级
                    notCompletedUpgrade = upgradeSystem;
                    break;
                }else{
                    if(currentVersion.equals(upgradeSystem.getId())){//如果当前系统版本已升级完成
                        isCompletedUpgrade = true;
                    }
                }

            }
        }
        if(notCompletedUpgrade == null && !isCompletedUpgrade){
            List<String> folderList = upgradeComponent.querySubdirectoryList("data/upgrade");
            for(String folder : folderList){
                if(folder.endsWith("to"+currentVersion)){
                    ClassPathResource resource_prop = new ClassPathResource("data/upgrade/"+folder+"/config.properties");

                    try (InputStream inputStream = resource_prop.getInputStream()){


                        CommentedProperties props = new CommentedProperties();
                        try {
                            props.load(inputStream,"utf-8");
                            //旧版本
                            String oldSystemVersion = props.getProperty("oldSystemVersion");
                            //升级包版本
                            String updatePackageVersion = props.getProperty("updatePackageVersion");
                            //新版本
                            String newSystemVersion = props.getProperty("newSystemVersion");
                            //说明
                            String explanation = props.getProperty("explanation");
                            //排序
                            String sort = props.getProperty("sort");

                            if(currentVersion.equals(newSystemVersion)){
                                notCompletedUpgrade = new UpgradeSystem();
                                notCompletedUpgrade.setId(newSystemVersion);
                                notCompletedUpgrade.setOldSystemVersion(oldSystemVersion);
                                notCompletedUpgrade.setUpdatePackageVersion(updatePackageVersion);
                                notCompletedUpgrade.setSort(Long.parseLong(sort));
                                notCompletedUpgrade.setExplanation(textFilterComponent.filterTag_br(explanation));
                                notCompletedUpgrade.setUpdatePackageFirstDirectory(folder);
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            //	e.printStackTrace();
                            if (logger.isErrorEnabled()) {
                                logger.error("读取配置文件config.properties错误",e);
                            }
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        //e1.printStackTrace();
                        if (logger.isErrorEnabled()) {
                            logger.error("读取配置文件IO异常 data/upgrade/"+folder+"/config.properties",e);
                        }
                    }
                    break;
                }
            }



        }

        returnValue.put("currentVersion", currentVersion);
        returnValue.put("notCompletedUpgrade", notCompletedUpgrade);
        returnValue.put("upgradeSystemList", upgradeSystemList);

        return returnValue;
    }

    /**
     * 获取升级明细
     * @param upgradeSystemId 升级Id
     * @return
     */
    public UpgradeSystem getUpgradeDetails(String upgradeSystemId){
        if(upgradeSystemId == null || upgradeSystemId.trim().isEmpty()){
            throw new BusinessException(Map.of("upgradeSystemId", "系统升级Id不能为空"));
        }

        UpgradeSystem upgradeSystem = upgradeRepository.findUpgradeSystemById(upgradeSystemId);
        if(upgradeSystem == null) {
            throw new BusinessException(Map.of("upgradeSystemId", "系统升级不存在"));
        }
        //删除最后一个逗号
        String _upgradeLog = StringUtils.substringBeforeLast(upgradeSystem.getUpgradeLog(), ",");//从右往左截取到相等的字符,保留左边的

        List<UpgradeLog> upgradeLogList = jsonComponent.toGenericObject(_upgradeLog+"]", new TypeReference< List<UpgradeLog> >(){});
        upgradeSystem.setUpgradeLogList(upgradeLogList);

        return upgradeSystem;
    }

    /**
     * 立即升级
     * @param updatePackageFirstDirectory 升级包目录
     */
    public void upgradeNow(String updatePackageFirstDirectory){
        Long count = upgradeCacheManager.taskRunMark_add(-1L);
        if(count >=0L){
            throw new BusinessException(Map.of("upgradeNow", "任务正在运行,不能升级"));
        }

        if(updatePackageFirstDirectory == null || updatePackageFirstDirectory.trim().isEmpty()){
            throw new BusinessException(Map.of("upgradeNow", "当前操作已完成"));
        }

        upgradeCacheManager.taskRunMark_delete();
        upgradeCacheManager.taskRunMark_add(1L);

        //校验版本
        boolean verificationVersion = false;

        //上一版本号
        String beforeVersion = StringUtils.substringBefore(updatePackageFirstDirectory, "to");//截取到等于第二个参数的字符串为止

        UpgradeSystem notCompletedUpgrade = null;//未完成升级
        List<UpgradeSystem> upgradeSystemList = upgradeRepository.findAllUpgradeSystem();

        if(upgradeSystemList != null && upgradeSystemList.size() >0){
            for(int i =0; i<upgradeSystemList.size(); i++ ){
                UpgradeSystem upgradeSystem = upgradeSystemList.get(i);


                if(upgradeSystem.getId().equals(beforeVersion)){
                    verificationVersion = true;
                }


                if(updatePackageFirstDirectory.equals(upgradeSystem.getUpdatePackageFirstDirectory())){
                    if(upgradeSystem.getRunningStatus().equals(9999)){
                        throw new BusinessException(Map.of("upgradeNow", "升级已完成，不能执行本操作"));
                    }else{
                        //未完成升级
                        notCompletedUpgrade = upgradeSystem;
                        break;
                    }
                }
            }
            if(!verificationVersion){
                throw new BusinessException(Map.of("upgradeNow", "版本校验不通过"));
            }
        }else{

            String currentVersion = upgradeComponent.readCurrentVersion();

            String originalVersion = upgradeComponent.readOriginalVersion();

            if(originalVersion != null && !originalVersion.trim().isEmpty()){
                if(currentVersion.trim().equals(originalVersion.trim())){
                    throw new BusinessException(Map.of("upgradeNow", "初始版本号和当前系统版本号相同，不允许升级"));
                }else{
                    verificationVersion = true;
                }
            }else{
                throw new BusinessException(Map.of("upgradeNow", "读取初始版本号为空"));
            }
        }




        if(verificationVersion && notCompletedUpgrade == null){
            ClassPathResource resource_prop = new ClassPathResource("data/upgrade/"+ FileUtil.toRelativePath(updatePackageFirstDirectory)+"/config.properties");

            try (InputStream inputStream = resource_prop.getInputStream()){


                CommentedProperties props = new CommentedProperties();
                try {
                    props.load(inputStream,"utf-8");
                    //旧版本
                    String oldSystemVersion = props.getProperty("oldSystemVersion");
                    //升级包版本
                    String updatePackageVersion = props.getProperty("updatePackageVersion");
                    //新版本
                    String newSystemVersion = props.getProperty("newSystemVersion");
                    //说明
                    String explanation = props.getProperty("explanation");
                    //排序
                    String sort = props.getProperty("sort");

                    UpgradeSystem upgradeSystem = new UpgradeSystem();

                    upgradeSystem.setId(newSystemVersion);
                    upgradeSystem.setOldSystemVersion(oldSystemVersion);
                    upgradeSystem.setUpdatePackageVersion(updatePackageVersion);
                    upgradeSystem.setSort(Long.parseLong(sort));
                    upgradeSystem.setRunningStatus(1);
                    upgradeSystem.setExplanation(textFilterComponent.filterTag_br(explanation));
                    upgradeSystem.setUpdatePackageFirstDirectory(updatePackageFirstDirectory);

                    List<String> deleteFilePathList = new ArrayList<String>();;

                    Set<String> keyList = props.propertyNames();
                    if(keyList != null && keyList.size() >0){
                        for(String key : keyList){
                            if(key != null && !key.trim().isEmpty()){
                                if(key.startsWith("delete_")){

                                    String value = props.getProperty(key);
                                    if(value != null && !value.trim().isEmpty()){
                                        deleteFilePathList.add(value.trim());
                                    }
                                }

                            }

                        }
                    }
                    upgradeSystem.setDeleteFilePath(jsonComponent.toJSONString(deleteFilePathList));

                    UpgradeLog upgradeLog = new UpgradeLog();
                    upgradeLog.setTime(new Date());
                    upgradeLog.setGrade(1);
                    upgradeLog.setContent("升级初始化成功");
                    String upgradeLog_json = jsonComponent.toJSONString(upgradeLog);
                    upgradeSystem.setUpgradeLog("["+upgradeLog_json+",");






                    try {
                        upgradeRepository.save(upgradeSystem);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        if (logger.isErrorEnabled()) {
                            logger.error("升级错误",e);
                        }
                        throw new BusinessException(Map.of("upgradeNow", "升级错误"));
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    //	e.printStackTrace();
                    if (logger.isErrorEnabled()) {
                        logger.error("读取配置文件config.properties错误",e);
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                //e1.printStackTrace();
                if (logger.isErrorEnabled()) {
                    logger.error("读取配置文件IO异常 data/upgrade/"+updatePackageFirstDirectory+"/config.properties",e);
                }
            }
        }


        upgradeCacheManager.taskRunMark_delete();
    }


    /**
     * 继续升级
     * @param upgradeId 升级Id
     */
    public void continueUpgrade(String upgradeId){
        Long count = upgradeCacheManager.taskRunMark_add(-1L);
        if(count >=0L){
            throw new BusinessException(Map.of("upgradeNow", "任务正在运行,不能升级"));
        }
        if(upgradeId == null || upgradeId.trim().isEmpty()){
            throw new BusinessException(Map.of("upgradeNow", "升级参数错误"));
        }

        UpgradeSystem upgradeSystem = upgradeRepository.findUpgradeSystemById(upgradeId);
        if(upgradeSystem == null || upgradeSystem.getRunningStatus() >=9999){
            throw new BusinessException(Map.of("upgradeNow", "当前升级不存在"));
        }
        upgradeCacheManager.taskRunMark_delete();
        upgradeCacheManager.taskRunMark_add(1L);

        //复制文件
        UpgradeSystem upgradeSystem_1 = upgradeRepository.findUpgradeSystemById(upgradeId);
        if(upgradeSystem_1.getInterruptStatus() != 1 && upgradeSystem_1.getRunningStatus() <20){
            boolean flag = true;

            //复制cms目录的文件到外部目录
            boolean upgrade_flag = FileUtil.copyResourceDirectory("data/upgrade/"+upgradeSystem.getUpdatePackageFirstDirectory()+"/cms", "");
            if(upgrade_flag){
                upgradeRepository.addLog(upgradeId, jsonComponent.toJSONString(new UpgradeLog(new Date(),"新文件从包内 data/upgrade/"+upgradeSystem.getUpdatePackageFirstDirectory()+"/cms 复制到外部目录成功",1))+",");

            }else{
                upgradeRepository.addLog(upgradeId, jsonComponent.toJSONString(new UpgradeLog(new Date(),"新文件从包内 data/upgrade/"+upgradeSystem.getUpdatePackageFirstDirectory()+"/cms 复制到外部目录失败",2))+",");
                flag = false;
                //添加错误中断
                upgradeRepository.updateInterruptStatus(upgradeId, 1, jsonComponent.toJSONString(new UpgradeLog(new Date(),"出现错误中断升级过程",2))+",");
            }



            if(flag){
                //更改运行状态
                upgradeRepository.updateRunningStatus(upgradeId ,20,jsonComponent.toJSONString(new UpgradeLog(new Date(),"复制文件到外部目录完成",1))+",");
            }

        }

        UpgradeSystem upgradeSystem_2 = upgradeRepository.findUpgradeSystemById(upgradeId);
        //删除文件
        if(upgradeSystem_2.getInterruptStatus() != 1 && upgradeSystem_2.getRunningStatus() >=20 && upgradeSystem_2.getRunningStatus()<40){
            boolean flag = true;
            if(upgradeSystem_2.getDeleteFilePath() != null && !upgradeSystem_2.getDeleteFilePath().trim().isEmpty()){
                upgradeRepository.updateRunningStatus(upgradeId ,30,jsonComponent.toJSONString(new UpgradeLog(new Date(),"执行删除文件任务",1))+",");

                List<String> deleteFilePathList = jsonComponent.toGenericObject(upgradeSystem_2.getDeleteFilePath(), new TypeReference< List<String> >(){});
                if(deleteFilePathList != null && deleteFilePathList.size() >0){

                    for(String deleteFilePath : deleteFilePathList){
                        File file = new File(PathUtil.defaultExternalDirectory()+File.separator+FileUtil.toSystemPath(deleteFilePath));
                        if(file.exists()){
                            if(file.isDirectory()){//目录
                                try {
                                    localFileComponent.removeDirectory(FileUtil.toSystemPath(deleteFilePath));
                                } catch (Exception e) {
                                    flag = false;
                                    upgradeRepository.addLog(upgradeId, jsonComponent.toJSONString(new UpgradeLog(new Date(),"删除目录失败--> "+deleteFilePath,1))+",");
                                    //添加错误中断
                                    upgradeRepository.updateInterruptStatus(upgradeId, 1, jsonComponent.toJSONString(new UpgradeLog(new Date(),"出现错误中断升级过程",2))+",");

                                    if (logger.isErrorEnabled()) {
                                        logger.error("删除目录失败"+deleteFilePath,e);
                                    }
                                    break;

                                }

                            }else{//文件
                                try {
                                    localFileComponent.deleteFile(FileUtil.toSystemPath(deleteFilePath));
                                } catch (Exception e) {
                                    flag = false;
                                    upgradeRepository.addLog(upgradeId, jsonComponent.toJSONString(new UpgradeLog(new Date(),"删除文件失败--> "+deleteFilePath,1))+",");
                                    //添加错误中断
                                    upgradeRepository.updateInterruptStatus(upgradeId, 1, jsonComponent.toJSONString(new UpgradeLog(new Date(),"出现错误中断升级过程",2))+",");

                                    if (logger.isErrorEnabled()) {
                                        logger.error("删除文件失败"+deleteFilePath,e);
                                    }
                                    break;

                                }

                            }

                        }
                    }
                }
            }
            if(flag){
                //更改运行状态
                upgradeRepository.updateRunningStatus(upgradeId ,40,jsonComponent.toJSONString(new UpgradeLog(new Date(),"删除文件任务结束",1))+",");

            }
        }

        UpgradeSystem upgradeSystem_4 = upgradeRepository.findUpgradeSystemById(upgradeId);
        if(upgradeSystem_4.getInterruptStatus() != 1 && upgradeSystem_4.getRunningStatus() >= 40){
            //执行处理数据
            upgradeComponent.manipulationData(upgradeId);
        }
    }


}