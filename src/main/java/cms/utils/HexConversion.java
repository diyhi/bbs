package cms.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 进制转换
 *
 */
public class HexConversion {
	private static final Logger logger = LogManager.getLogger(HexConversion.class);
	
	
	final static char[] digits = {
	    	'0' , '1' , '2' , '3' , '4' , '5' ,
	    	'6' , '7' , '8' , '9' , 'a' , 'b' ,
	    	'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
	    	'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
	    	'o' , 'p' , 'q' , 'r' , 's' , 't' ,
	    	'u' , 'v' , 'w' , 'x' , 'y' , 'z' ,
	    	'A' , 'B' , 'C' , 'D' , 'E' , 'F' ,
	    	'G' , 'H' , 'I' , 'J' , 'K' , 'L' ,
	    	'M' , 'N' , 'O' , 'P' , 'Q' , 'R' ,
	    	'S' , 'T' , 'U' , 'V' , 'W' , 'X' ,
	    	'Y' , 'Z' , '+' , '/'  ,
	        };
	
	
	/**
	 * 字符串转换为十六进制    
	 * @param strValue
	 * @return
	 */
	public static String getStringToHex(String strValue){
		byte byteData[] = null;
		int intHex = 0;
		String strHex = "";
		String strReturn = "";
		try{
		    byteData = strValue.getBytes("UTF-8");
		    for(int intI=0;intI<byteData.length;intI++){
		    intHex = (int)byteData[intI];
		    if (intHex<0)
		    	intHex += 256;
		    if (intHex<16)
		    	strHex += "0" + Integer.toHexString(intHex).toUpperCase();
		    else
		    	strHex += Integer.toHexString(intHex).toUpperCase();
		    }
		    strReturn = strHex;
		}catch (Exception ex) {
		//	ex.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("字符串转换为十六进制",ex);
	        }
		}
    	return strReturn;
    }

	  /**
	   * 十六进制转换为字符串
	   * @param strValue
	   * @return
	   */
      public static String getHexToString(String strValue) {
	      int intCounts = strValue.length() / 2;
	      String strReturn = "";
		  String strHex = "";
		  int intHex = 0;
		  byte byteData[] = new byte[intCounts];
		  try {
			  for (int intI = 0; intI < intCounts; intI++) {
				  strHex = strValue.substring(0, 2);
				  strValue = strValue.substring(2);
				  intHex = Integer.parseInt(strHex, 16);
				  if (intHex > 128)
				  intHex = intHex - 256;
				  byteData[intI] = (byte) intHex;
			  }
			  strReturn = new String(byteData,"UTF-8");
		  }catch (Exception ex) {
		//	  ex.printStackTrace();
			  if (logger.isErrorEnabled()) {
		          logger.error("十六进制转换为字符串",ex);
		      }
		  }
		  return strReturn;
      }
      
      /**
       * byte[]转换为十六进制
       * @param b
       * @return
       */
       public static String byteToHex(byte[] b) {
     	  if (b == null || b.length <= 0) {  
               return null;  
           }  
           return Hex.encodeHexString(b);
       }
       /**
        * 把16进制字符串转换成字节数组
        * @param hexString
        * @return byte[]
        */
       public static byte[] hexStringToByte(String hex) {
     	  if (hex == null || "".equals(hex)) {  
               return null;  
           }  
     	  char[] cs = hex.toCharArray();  
           try {
 			return Hex.decodeHex(cs);
 		} catch (DecoderException e) {
 			// TODO Auto-generated catch block
 			//e.printStackTrace();
 			if (logger.isErrorEnabled()) {
 	            logger.error("把16进制字符串转换成字节数组错误",e);
 	        }
 		}   
           return null; 
       }
      
   
  	/**
  	 * 把10进制的数字转换成64进制
  	 * @param number
  	 * @param shift 进制
  	 * @return
  	 */
      public static String CompressNumber(long number, int shift) {
      	char[] buf = new char[64];
      	int charPos = 64;
      	int radix = 1 << shift;
      	long mask = radix - 1;
      	do {
      	    buf[--charPos] = digits[(int)(number & mask)];
      	    number >>>= shift;
      	} while (number != 0);
      	return new String(buf, charPos, (64 - charPos));
         }
      /**
       * 把64进制的字符串转换成10进制
       * @param decompStr
       * @return
       */
      public static long UnCompressNumber(String decompStr){
      	long result=0;
      	for (int i =  decompStr.length()-1; i >=0; i--) {
      		if(i==decompStr.length()-1)
      		{
      			result+=getCharIndexNum(decompStr.charAt(i));
      			continue;
      		}
      		for (int j = 0; j < digits.length; j++) {
      			if(decompStr.charAt(i)==digits[j])
          		{
      				result+=((long)j)<<6*(decompStr.length()-1-i);
          		}
  			}
  		}
      	return result;
      }   
      /**
       * 
       * @param ch
       * @return
       */
      private static long getCharIndexNum(char ch)
      {
      	int num=((int)ch);
      	if(num>=48&&num<=57)
      	{
      		return num-48;
      	}
      	else if(num>=97&&num<=122)
      	{
      		return num-87;
      	}else if(num>=65&&num<=90)
      	{
      		return num-29;
      	}else if(num==43)
      	{
      		return 62;
      	}
      	else if (num == 47)
      	{
      		return 63;
  		}
      	return 0;
      }
      
      /**
    	 * @param args
    	 
    	public static void main(String[] args) {
    		System.out.println(CompressNumber(999999999999999999L,6)); 
    		System.out.println(UnCompressNumber(CompressNumber(999999999999999999L,6)));
    	}
    	*/
}
