package cms.utils;

import java.io.File;

/**
 * 普通JAVA获取   WEB项目下的WEB-INF目录
 * @author Administrator
 *
 */
public class PathUtil {
	private static String path = "";
	public static String path(){
		if("".equals(path)){
		//	PathUtil pathUtil = new PathUtil();
		//	path = pathUtil.getWebInfPath();
			path = getRootPath();
		}
		
		return path;
	}

	private static String getRootPath() {
		String classPath = PathUtil.class.getClassLoader().getResource("/").getPath();
		String rootPath  = "";
		//windows下
		if("\\".equals(File.separator)){   
			rootPath  = classPath.substring(1,classPath.indexOf("/WEB-INF/classes"));
			rootPath = rootPath.replace("/", "\\");
		}
		//linux下
		if("/".equals(File.separator)){   
			rootPath  = classPath.substring(0,classPath.indexOf("/WEB-INF/classes"));
			rootPath = rootPath.replace("\\", "/");
		}
		return rootPath;
	}

}
