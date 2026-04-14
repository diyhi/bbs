package cms.utils;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import cms.config.KaptchaConfig;
import org.springframework.boot.system.ApplicationHome;

import cms.Application;

/**
 * 获取项目jar的根目录
 * @author Administrator
 *
 */
public class PathUtil {
	//项目根目录
	private static String rootPath = "";
	
	//论坛外部目录
	private static String defaultExternalDirectory = "";



	public static String rootPath(){
		if("".equals(rootPath)){
			readPath();
		}
		return rootPath;
	}
	public static String defaultExternalDirectory(){
		if("".equals(defaultExternalDirectory)){
			readPath();
		}
		return defaultExternalDirectory;
	}

	private static void readPath() {
		ApplicationHome home = new ApplicationHome(Application.class);
        File source = home.getSource();
        String path = "";
        if (source != null) {//服务运行环境
            // 返回 JAR 包所在的文件夹
            path = source.getParentFile().getAbsolutePath();
        }else{//打包环境

            // 寻找 target 目录
            // 获取当前类文件的实际物理路径（可能是 .../target/classes/cms/Application.class）
            String classPath = Application.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            // 解码 URL（处理路径中的空格或中文字符）
            classPath = java.net.URLDecoder.decode(classPath, StandardCharsets.UTF_8);
            File classFile = new File(classPath);

            // 向上递归寻找名为 "target" 的文件夹
            File current = classFile;
            while (current != null) {
                if (current.getName().equalsIgnoreCase("target")) {
                    path = current.getAbsolutePath();
                    break;
                }
                current = current.getParentFile();
            }

            // 如果没找到 target（比如在极端的非 Maven 环境下）
            if (path.isEmpty()) {
                path = System.getProperty("user.dir");
            }
        }


		//File jarFile = home.getDir();
		//String path = jarFile.getParentFile().toString();
		rootPath = path + File.separator+"classes";//F:\JAVA\bbs-pro-jdk21\target\classes
		//论坛外部目录
		Object externalDirectory = YmlUtils.getYmlProperty("application.yml","bbs.externalDirectory");
		if(externalDirectory != null && !externalDirectory.toString().trim().isEmpty()){//如果已设置了论坛外部目录   G:\bbs
			defaultExternalDirectory = externalDirectory.toString();
		}else{
			defaultExternalDirectory = path + File.separator + "bbs";//F:\JAVA\bbs-pro-jdk21\target\bbs
		}
	}
	
	/**
	 * 自动路径 jar启动时使用外部路径 IDE启动时使用内部路径
	 * @return
	 */
	public static String autoRootPath() {
		if(isStartupFromJar()){//jar启动
			return defaultExternalDirectory();
		}else{//IDE启动
			return rootPath();
		}
	}
	
	/**
	 * 判断是在jar中运行,还是IDE中运行
	 * @return true: jar启动   false: IDE启动
	 */
	public static boolean isStartupFromJar() {
		URL url = KaptchaConfig.class.getResource("");
		String protocol = url.getProtocol();
		if ("jar".equals(protocol)) {
			return true;
		}	
		return false;
	}

}
