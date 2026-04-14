package cms;


import cms.component.install.InstallComponent;
import cms.utils.CreateEntityFile;
import cms.utils.YmlUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.freemarker.autoconfigure.FreeMarkerAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@EnableScheduling // 开启定时
@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class, JacksonAutoConfiguration.class})//取消freemarker、Jackson自动配置
@ServletComponentScan("cms.config")
public class Application {

    public static void main(String[] args) {
        boolean isMaven = false;//是否由maven插件启动
        if(args != null){
            for(String arg : args){
                if(arg.equals("maven")){
                    isMaven = true;
                }
            }
        }

        //创建数据库分表实体类文件
        CreateEntityFile.create();

        if(!isMaven){
            SpringApplication application = new SpringApplication(Application.class);
           // application.addListeners(new InitApplicationListener());//注册监听器
           // application.setAllowCircularReferences(Boolean.TRUE);//spring boot 2.6 允许循环引用
            application.run(args);


        }else{//maven命令执行
            String user_dir = System.getProperty("user.dir");


            //默认外部目录
            String defaultExternalDirectory = "";

            //论坛外部目录
            Object externalDirectory = YmlUtils.getYmlProperty("application.yml","bbs.externalDirectory");
            if(externalDirectory != null && !externalDirectory.toString().trim().isEmpty()){//如果已设置了论坛外部目录
                defaultExternalDirectory = externalDirectory.toString();
            }else{
                defaultExternalDirectory = user_dir + File.separator + "target"+ File.separator + "bbs";

            }
            //生成外部文件夹
            InstallComponent installComponent = new InstallComponent();
            List<String> folderList = installComponent.folderList();
            if(folderList != null && !folderList.isEmpty()){
                for(String folderPath : folderList){
                    //生成文件目录
                    Path path = Paths.get(defaultExternalDirectory+File.separator+ Strings.CS.replace(folderPath, "/", File.separator));

                    if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {//目录不存在
                        try {

                            Files.createDirectories(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

}
