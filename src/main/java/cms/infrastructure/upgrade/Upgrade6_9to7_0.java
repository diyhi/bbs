package cms.infrastructure.upgrade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import cms.component.JsonComponent;
import cms.component.upgrade.UpgradeCacheManager;
import cms.model.upgrade.UpgradeLog;
import cms.model.upgrade.UpgradeSystem;
import cms.repository.upgrade.UpgradeRepository;
import cms.service.staff.ACLService;
import cms.utils.FileUtil;
import cms.utils.SqlFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cms.utils.SpringConfigTool;
import org.springframework.core.io.ClassPathResource;


/**
 * 6.9升级到7.0版本执行程序
 *
 */
public class Upgrade6_9to7_0 {
	private static final Logger logger = LogManager.getLogger(Upgrade6_9to7_0.class);

	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeRepository upgradeRepository = (UpgradeRepository)SpringConfigTool.getContext().getBean("upgradeRepositoryImpl");
        UpgradeCacheManager upgradeCacheManager = (UpgradeCacheManager)SpringConfigTool.getContext().getBean("upgradeCacheManager");
        JsonComponent jsonComponent = (JsonComponent)SpringConfigTool.getContext().getBean("jsonComponent");



    	for(int i =0; i< 100; i++){
            upgradeCacheManager.taskRunMark_delete();
            upgradeCacheManager.taskRunMark_add(1L);
    		UpgradeSystem upgradeSystem = upgradeRepository.findUpgradeSystemById(upgradeId);
    		if(upgradeSystem == null || upgradeSystem.getRunningStatus().equals(9999)){
    			break;
    		}
    		if(upgradeSystem.getRunningStatus()>=40 && upgradeSystem.getRunningStatus()<200){

                insertSQL_syspermissionresources(upgradeRepository);
                upgradeRepository.addLog(upgradeId, jsonComponent.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");

                insertSQL_syspermission(upgradeRepository);
                upgradeRepository.addLog(upgradeId, jsonComponent.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");

                insertSQL_sysresources(upgradeRepository);
                upgradeRepository.addLog(upgradeId, jsonComponent.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");


                insertSQL_frontend(upgradeRepository);
                upgradeRepository.addLog(upgradeId, jsonComponent.toJSONString(new UpgradeLog(new Date(),"表frontendapi , frontendsettings插入SQL成功",1))+",");


                removeDeprecatedResources(upgradeRepository);
                upgradeRepository.addLog(upgradeId, jsonComponent.toJSONString(new UpgradeLog(new Date(),"删除已废弃的资源成功",1))+",");

                //更改运行状态
				upgradeRepository.updateRunningStatus(upgradeId ,200,jsonComponent.toJSONString(new UpgradeLog(new Date(),"升级流程结束",1))+",");

    		}
    		
    		
    		if(upgradeSystem.getRunningStatus()>=200 && upgradeSystem.getRunningStatus()<9999){
    			//更改运行状态
				upgradeRepository.updateRunningStatus(upgradeId ,9999,jsonComponent.toJSONString(new UpgradeLog(new Date(),"升级完成",1))+",");

    		}
    		
    		
    		
    		
    	}
        upgradeCacheManager.taskRunMark_delete();
    }


    /**
     * 插入升级SQL
     * @param upgradeRepository 升级管理
     */
    private static void insertSQL_syspermission(UpgradeRepository upgradeRepository){
        String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('1ce68f0ad8b646f9be1417679267675b','GET','AUTH_130726edf14b42b5b02cc66285bb7083_GET_READ',1,'站点栏目列表'),('26b1be4867e94c13bd659e4fcb2fbde6','GET','AUTH_01ae0b63bb10452183e650cfaa59e1e6_GET_READ',1,'前台文档列表'),('6c689be4086c4a5180eb15f1fee99009','POST','AUTH_0d2cd75aa5a349ddb85baeed2c30a8a0_POST_UPDATE',1,'修改站点栏目'),('76f3fee59cac41c68f2d5ef7e6a215d7','POST','AUTH_ecd1d15e17cd437bb6fa355bdf373a7b_POST_ADD',2,'添加前台API'),('90408973a3ba45f7812d9a701212c8a2','POST','AUTH_94bf1b582f814930800c9b67b53c7228_POST_UPDATE',2,'修改前台API'),('9b91ada24b3d4e0c8ac1addae7ee3298','GET','AUTH_94bf1b582f814930800c9b67b53c7228_GET_READ',1,'修改前台API页'),('b844e6457f884a0ea4f7147b18556ae6','POST','AUTH_741ce561d0ea4e8eb008563d83872447_POST_DELETE',1,'删除前台API'),('baf34f903bac4d8f930be06ed6bdb1ee','GET','AUTH_53f978cfb8df40559bf651dca0f53912_GET_READ',1,'前台文档内容'),('bb427c35ec5443529c5146128c559365','GET','AUTH_ecd1d15e17cd437bb6fa355bdf373a7b_GET_READ',1,'添加前台API页'),('c6360c8f480941b291160fb342eb4af8','POST','AUTH_334e3ea939d14f36ac7c695e1bacf3d9_POST_DELETE',1,'删除站点栏目'),('d3f004b7fb574c4c8cb8b03c490a4015','GET','AUTH_98a8501ae3e645f2b7593777fa58fed0_GET_READ',1,'前台API列表'),('fd58769d59c34beda6747f003183ea16','POST','AUTH_e562e87b50bf466994aa735778716b6f_POST_ADD',1,'添加站点栏目');";

        upgradeRepository.insertNativeSQL(sql);
    }
    /**
     * 插入升级SQL
     * @param upgradeRepository 升级管理
     */
    private static void insertSQL_syspermissionresources(UpgradeRepository upgradeRepository){

        String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'d3f004b7fb574c4c8cb8b03c490a4015','98a8501ae3e645f2b7593777fa58fed0'),(NULL,'bb427c35ec5443529c5146128c559365','ecd1d15e17cd437bb6fa355bdf373a7b'),(NULL,'bb427c35ec5443529c5146128c559365','f284e5f6c51646c8916c3bca2f8ebed2'),(NULL,'76f3fee59cac41c68f2d5ef7e6a215d7','ecd1d15e17cd437bb6fa355bdf373a7b'),(NULL,'76f3fee59cac41c68f2d5ef7e6a215d7','84142f3fbc5a46b8b9cf4063fca59c99'),(NULL,'9b91ada24b3d4e0c8ac1addae7ee3298','94bf1b582f814930800c9b67b53c7228'),(NULL,'9b91ada24b3d4e0c8ac1addae7ee3298','ef0e197ffaef415bbdb57d4bb51ef39d'),(NULL,'90408973a3ba45f7812d9a701212c8a2','94bf1b582f814930800c9b67b53c7228'),(NULL,'90408973a3ba45f7812d9a701212c8a2','3b8c369ad6be486196e9dad8ffb01f09'),(NULL,'b844e6457f884a0ea4f7147b18556ae6','741ce561d0ea4e8eb008563d83872447'),(NULL,'26b1be4867e94c13bd659e4fcb2fbde6','01ae0b63bb10452183e650cfaa59e1e6'),(NULL,'baf34f903bac4d8f930be06ed6bdb1ee','53f978cfb8df40559bf651dca0f53912'),(NULL,'1ce68f0ad8b646f9be1417679267675b','130726edf14b42b5b02cc66285bb7083'),(NULL,'fd58769d59c34beda6747f003183ea16','e562e87b50bf466994aa735778716b6f'),(NULL,'6c689be4086c4a5180eb15f1fee99009','0d2cd75aa5a349ddb85baeed2c30a8a0'),(NULL,'c6360c8f480941b291160fb342eb4af8','334e3ea939d14f36ac7c695e1bacf3d9');";
        upgradeRepository.insertNativeSQL(sql);

    }
    /**
     * 插入升级SQL
     * @param upgradeRepository 升级管理
     */
    private static void insertSQL_sysresources(UpgradeRepository upgradeRepository){
        String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('01ae0b63bb10452183e650cfaa59e1e6','前台模块管理','前台文档列表',90500,'','/control/frontendDocument/list*',NULL,NULL),('0d2cd75aa5a349ddb85baeed2c30a8a0','前台模块管理','修改站点栏目',91200,'','/control/section/manage?method=edit*',NULL,NULL),('130726edf14b42b5b02cc66285bb7083','前台模块管理','站点栏目列表',91000,'','/control/section/manage?method=list*',NULL,NULL),('334e3ea939d14f36ac7c695e1bacf3d9','前台模块管理','删除站点栏目',91300,'','/control/section/manage?method=delete*',NULL,NULL),('3b8c369ad6be486196e9dad8ffb01f09','前台模块管理',NULL,0,NULL,'/control/frontendApi/manage?method=upload*','94bf1b582f814930800c9b67b53c7228',2),('53f978cfb8df40559bf651dca0f53912','前台模块管理','前台文档内容',90600,'','/control/frontendDocument/details*',NULL,NULL),('741ce561d0ea4e8eb008563d83872447','前台模块管理','删除前台API',90300,'','/control/frontendApi/manage?method=deleteFrontendApi*',NULL,NULL),('84142f3fbc5a46b8b9cf4063fca59c99','前台模块管理','文件上传',0,NULL,'/control/frontendApi/manage?method=upload*','ecd1d15e17cd437bb6fa355bdf373a7b',2),('94bf1b582f814930800c9b67b53c7228','前台模块管理','修改前台API',90200,'','/control/frontendApi/manage?method=editFrontendApi*',NULL,NULL),('98a8501ae3e645f2b7593777fa58fed0','前台模块管理','前台API列表',90000,'','/control/frontendApi/list*',NULL,NULL),('e562e87b50bf466994aa735778716b6f','前台模块管理','添加站点栏目',91100,'','/control/section/manage?method=add*',NULL,NULL),('ecd1d15e17cd437bb6fa355bdf373a7b','前台模块管理','添加前台API',90100,'','/control/frontendApi/manage?method=addFrontendApi*',NULL,NULL),('ef0e197ffaef415bbdb57d4bb51ef39d','前台模块管理',NULL,0,NULL,'/control/frontendApi/manage?method=checkUrl*','94bf1b582f814930800c9b67b53c7228',1),('f284e5f6c51646c8916c3bca2f8ebed2','前台模块管理','校验URL路径',0,NULL,'/control/frontendApi/manage?method=checkUrl*','ecd1d15e17cd437bb6fa355bdf373a7b',1);";
        upgradeRepository.insertNativeSQL(sql);

    }


    /**
     * 插入升级SQL
     * @param upgradeRepository 升级管理
     */
    private static void insertSQL_frontend(UpgradeRepository upgradeRepository){
        ClassPathResource data_classPathResource = new ClassPathResource("data/install/data_tables_mysql.sql");
        List<String> targetTables = Arrays.asList("frontendapi", "frontendsettings");
        try (InputStream inputStream = data_classPathResource.getInputStream()) {

            List<String> sqlList = SqlFile.readTableInsertStatements(inputStream, targetTables);

            if (!sqlList.isEmpty()) {
                for(String sql : sqlList){
                    upgradeRepository.insertNativeSQL(sql);
                }
            }
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("无法读取data_tables_mysql.sql资源文件",e);
            }
        }


    }



    /**
     * 删除已废弃的资源
     */
    private static void removeDeprecatedResources(UpgradeRepository upgradeRepository){
        ACLService aclService = (ACLService)SpringConfigTool.getContext().getBean("ACLServiceImpl");

        //删除已废弃的资源
        List<String> obsoleteResourcesIdList = new ArrayList<>();
        obsoleteResourcesIdList.add("b9512c1642a54356b453ef6e9df32aee");//模板列表
        obsoleteResourcesIdList.add("70889956fee3475fb8487313d46f94c7");//添加模板
        obsoleteResourcesIdList.add("cbe2d7086fee4d5390afe686e9cddad1");//修改模板
        obsoleteResourcesIdList.add("d0482512ccd5439bb6610672c11f0cb2");//删除模板
        obsoleteResourcesIdList.add("ad32af8a5d784a979da7b5362ca77882");//模板设为使用
        obsoleteResourcesIdList.add("3c98bf1cf2294c059cf0241fba5cfa00");//导入模板列表
        obsoleteResourcesIdList.add("129aa71dc14049ccb0715268f24281aa");//上传模板
        obsoleteResourcesIdList.add("b77f796a65694fed859fe1684d365589");//目录重命名
        obsoleteResourcesIdList.add("9c5bebaa673b45bd9d1c5cba7a9dead9");//导入模板
        obsoleteResourcesIdList.add("e0aa4b8b3c51428eb6dd8227ffffe283");//导出模板
        obsoleteResourcesIdList.add("4de9ba43424146678772a69d0cd6ea4d");//下载导出模板
        obsoleteResourcesIdList.add("fb9bce585d7d4aa89ddf30262540e660");//删除导出模板
        obsoleteResourcesIdList.add("90cd4aa4e6ef4154849630ae701fe37d");//版块代码列表
        obsoleteResourcesIdList.add("aa971ce1dc6f43e3b026d235fa7263e6");//添加版块代码
        obsoleteResourcesIdList.add("70737a78770041a18bccc71b4d17ecf8");//修改版块代码
        obsoleteResourcesIdList.add("a0116147816444319d37e0e1562189b0");//删除版块代码
        obsoleteResourcesIdList.add("a2b73c63005149699adb7fb395467b49");//版块代码源码编辑
        obsoleteResourcesIdList.add("d72acdb07d1540bab400c194ba3329f8");//资源列表
        obsoleteResourcesIdList.add("24bac9f0b1c84b0394a39c8933c7739d");//资源文件查看
        obsoleteResourcesIdList.add("6b17f9cef55a4705a8ff445c5eb37e0e");//资源文件编辑
        obsoleteResourcesIdList.add("59bb52174d2e4f318e0c5ed09f9bedb3");//资源文件上传
        obsoleteResourcesIdList.add("1800baca07bc44ac986556dd30d37254");//资源新建文件夹
        obsoleteResourcesIdList.add("a8b4ea7341944f03b4c468b0354371cc");//资源重命名
        obsoleteResourcesIdList.add("3d630529412f44c8be0c409bf12bbba7");//资源文件下载
        obsoleteResourcesIdList.add("bc248879066f4af58c99a822f4228015");//删除资源
        obsoleteResourcesIdList.add("5b89f2dc65b54197940c9f1b614c669e");//布局列表
        obsoleteResourcesIdList.add("412fd399deac43cb8354a15db3b47bbf");//添加布局
        obsoleteResourcesIdList.add("5af3e8652bd14b48aea03fac91e1e0b7");//修改布局
        obsoleteResourcesIdList.add("ab73276d43184b2db5fa5a6bd76b1def");//删除布局
        obsoleteResourcesIdList.add("0d87e1fe8fdd45d48c5f259b8e423b2e");//布局编辑
        obsoleteResourcesIdList.add("7db8b118e2ec4ce9992e027eddda1648");//版块列表
        obsoleteResourcesIdList.add("922f6908c5a1434aba4b0f6f8f008c1c");//添加版块
        obsoleteResourcesIdList.add("ef3e2b9c32634f4088f4066880c70677");//修改版块
        obsoleteResourcesIdList.add("685794fae29a45b6a5d61416b39d7f71");//删除版块
        obsoleteResourcesIdList.add("4bac717853cb4a7faf79905ea6cad412");//版块源码编辑
        obsoleteResourcesIdList.add("11f96911716c4498b562658224750b02");//栏目列表
        obsoleteResourcesIdList.add("32361852ba844d1f9ccdb220ce3b3cb1");//版块引用代码
        obsoleteResourcesIdList.add("0e2aa6a61c994088819af6a79c519ebf");//添加栏目
        obsoleteResourcesIdList.add("2690d4cb2beb425daa06ff9faf8d97ec");//修改栏目
        obsoleteResourcesIdList.add("f285c691b0f74b9ca549344722e5d780");//删除栏目


        obsoleteResourcesIdList.add("dd231daf558d4526bf0d95ee6cc6970e");//数据库备份/还原列表
        obsoleteResourcesIdList.add("6b12cf5fb0ce4927979775f3c76419ab");//数据库备份
        obsoleteResourcesIdList.add("2e51f370f3c34d0bbfaf4a9f1ca7ee02");//数据库还原


        for(String resourcesId : obsoleteResourcesIdList){
            aclService.deleteResources(resourcesId);
        }

        //写入禁止安装系统
        FileUtil.writeStringToFile("data"+ File.separator+"install"+File.separator+"status.txt","1","utf-8",false);

    }
    
}
