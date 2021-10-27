package utils;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import cms.utils.FileUtil;
import cms.utils.RsaUtil;
import cms.utils.ZipCallback;
import cms.utils.ZipUtil;
import cms.web.action.upgrade.UpgradeManage;

/**
 * 升级包文件签名
 * 例如 bbs-4.5to4.6.zip
 * 本功能是将打包好的升级包文件写入签名，其中/WEB-INF/classes/upgradePublicKey.pem是公钥。运行签名方法后，会往升级包zip文件里写入signature.pem文件,signature.pem文件内容包含本地私钥加密的全部升级包文件sha256内容摘要
 * RSA密钥长度：2048   密钥格式：PKCS#8  没有证书密码  
 * 保存的pem文件请将第三方生成密钥工具加入的“-----BEGIN PUBLIC KEY-----”或“-----END PUBLIC KEY-----”或“-----BEGIN PRIVATE KEY-----”或“-----END PRIVATE KEY-----”等这些信息删除，否则会报“密钥规范错误”
 * pem文件必须保存为“UTF-8无BOM” 格式
 * 第三方生成密钥工具有： OpenSSL工具、各种在线生成RSA私钥公钥网站
 */
public class UpdatePackageSignature {
	
	//升级包源文件完整路径
	private static String sourceFileFullPath = "D:\\测试\\bbs-5.3to5.4.zip";//升级包签名后文件路径和源文件路径同目录
	//私钥文件完整路径
	private static String privateKeyFullPath = "D:\\测试\\upgradePrivateKey.pem";//RSA PKCS#8 2048
			
	
	/**
	 * 签名
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		//读取升级包
		File updatePackage = new File(sourceFileFullPath);
		

		if (updatePackage.exists()) {//如果文件存在
			//升级包源文件路径
			String sourceFilePath = FileUtil.getFullPath(updatePackage.getAbsolutePath());
			//升级包源文件名称(不包含后缀)
			String sourceFileName = FileUtil.getBaseName(sourceFileFullPath);
			//升级包源文件后缀
			String extension = FileUtil.getExtension(sourceFileFullPath);
			
			DateTime dateTime = new DateTime();     
			String date = dateTime.toString("yyyy-MM-dd-HH-mm");
			String randomNumeric = RandomStringUtils.randomNumeric(6);//6位随机数
			//临时目录路径
			String temp_path = sourceFilePath+"论坛升级包_"+date+"_"+randomNumeric+File.separator;

			//升级包签名后文件完整路径
			String newFileFullPath = sourceFilePath+sourceFileName+"_"+date+"_"+randomNumeric+"."+extension;
			
			
			
			//解压到临时目录
			try {
				ZipUtil.unZip(sourceFileFullPath, temp_path);
				
				
				//目录参数
				class DirectoryParameter { 
					//第一个目录
					private String firstDirectory = null;
					
					public String getFirstDirectory() { 
			            return firstDirectory;
			        }
			        public void setFirstDirectory(String firstDirectory) { 
			            this.firstDirectory = firstDirectory;
			        } 
			    }
				
				DirectoryParameter directoryParameter = new DirectoryParameter(); 

				ZipUtil.iterate(new File(sourceFileFullPath), new ZipCallback() {
					public void process(ZipArchiveEntry zipEntry) throws Exception {
						 if(directoryParameter.getFirstDirectory() == null || "".equals(directoryParameter.getFirstDirectory().trim())){
							  directoryParameter.setFirstDirectory(StringUtils.substringBefore(zipEntry.getName(), "/"));
						 }
					 }
				});
				if(directoryParameter != null && directoryParameter.getFirstDirectory() != null && !"".equals(directoryParameter.getFirstDirectory().trim())){
				
					String privateKey_str = "";
					//读取私钥文件
					try {
			     		File file = new File(privateKeyFullPath);
			     		if(file != null && file.exists()){
			     			privateKey_str = FileUtils.readFileToString(file, "utf-8");
			     		}
			     		
			 		} catch (IOException e) {
			 			// TODO Auto-generated catch block
			 			e.printStackTrace();
			 		}
					if(privateKey_str != null && !"".equals(privateKey_str.trim())){
						//文件签名
						String signature = new UpgradeManage().getFileSignature(temp_path+directoryParameter.getFirstDirectory()+File.separator);
						PrivateKey privateKey = RsaUtil.getPrivateKey(privateKey_str);
						//RSA加密签名
						String encryptSignature = RsaUtil.encryptData(signature, privateKey);
						
						//将加密的签名写入文件
						org.apache.commons.io.FileUtils.writeStringToFile(new File(temp_path+directoryParameter.getFirstDirectory()+File.separator+"signature.pem"), encryptSignature, "utf-8",false);
						//将临时文件夹打包
						ZipUtil.pack(temp_path, //压缩文件
								newFileFullPath,
								null
						);
						System.out.println("签名后的文件--> "+newFileFullPath);
					}else{
						System.err.println("私钥文件不能为空");
					}
				}else{
					System.err.println("读取第一个目录为空");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.err.println("升级包源文件不存在");
		}
		
		
		
	}
	
	
	
}
