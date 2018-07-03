package cms.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 根据16进制文件头获取文件类型
 *
 */
public class FileType {
	private static final Logger logger = LogManager.getLogger(FileType.class);
	
	private static Map<String,String> type= new HashMap<String,String>();
	static{
		type.put("FFD8FF", "JPG");
		type.put("89504E", "PNG");
		type.put("474946", "GIF");
		type.put("424D", "BMP");
		type.put("255044", "PDF");
		type.put("464C56", "FLV");
		type.put("465753", "SWF");
		type.put("494433", "MP3");
		type.put("FFFA", "MP3");
		type.put("FFFB", "MP3");
		type.put("FFF340", "MP3");
		type.put("524946", "WAV");
		type.put("574156", "WAV");
	//	type.put("", "WMA");
		type.put("664C61", "FLAC");
		type.put("4D4143", "APE");
		type.put("2E524D", "RM/RMVB");
		type.put("000000", "MP4");
		type.put("524946", "AVI");
		type.put("415649", "AVI");
		type.put("1A45DF", "MKV");
		type.put("3026B2", "WMV");
		type.put("504B03", "ZIP");
		type.put("526172", "RAR");
		type.put("377ABC", "7Z");
	//	type.put("", "TXT");
	}   
	
	/**  
     * 将文件头转换成16进制字符串  
     *   
     * @param 原生byte  
     * @return 16进制字符串  
     */  
    private static String bytesToHexString(byte[] src){   
           
        StringBuilder stringBuilder = new StringBuilder();      
        if (src == null || src.length <= 0) {      
            return null;      
        }      
        for (int i = 0; i < src.length; i++) {      
            int v = src[i] & 0xFF;      
            String hv = Integer.toHexString(v);      
            if (hv.length() < 2) {      
                stringBuilder.append(0);      
            }      
            stringBuilder.append(hv);      
        }      
        return stringBuilder.toString();      
    }   
      
    /**  
     * 得到文件头  
     *   
     * @param filePath 文件路径  
     * @return 文件头  
     * @throws IOException  
     */  
    private static String getFileContent(InputStream inputStream) throws IOException {   
           
        byte[] b = new byte[28];   
      
           
        try {   
            inputStream.read(b, 0, 28);   
        } catch (IOException e) {   
        	if (logger.isErrorEnabled()) {
	            logger.error("得到文件头",e);
	        }
         //   e.printStackTrace();   
            throw e;   
        } finally {   
            if (inputStream != null) {   
                try {   
                    inputStream.close();   
                } catch (IOException e) {   
                 //   e.printStackTrace();  
                	if (logger.isErrorEnabled()) {
        	            logger.error("得到文件头",e);
        	        }
                    throw e;   
                }   
            }   
        }   
        return bytesToHexString(b);   
    }   
       
    /**  
     * 判断文件类型  
     *   
     * @param filePath 文件路径  
     * @return 文件类型  
     */  
    public static String getType(InputStream inputStream) throws IOException {   
        String fileHead = getFileContent(inputStream);   
        if (fileHead == null || fileHead.length() == 0) {   
            return null;   
        }   
           
        fileHead = fileHead.toUpperCase();   

        for (Map.Entry<String,String> entry : type.entrySet()) {
        	if(fileHead.startsWith(entry.getKey())){
        		return entry.getValue();
        	}
        }
        return null;   
    } 
}
