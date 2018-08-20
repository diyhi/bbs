package cms.utils;

import java.math.BigDecimal;

/**
 * 文件大小换算
 * @author Administrator
 *
 */
public class FileSize {
	//bt字节参考量
	public static final long SIZE_BT=1024L;
	//KB字节参考量
	public static final long SIZE_KB=SIZE_BT*1024L;
	//MB字节参考量
	public static final long SIZE_MB=SIZE_KB*1024L;
	//GB字节参考量
	public static final long SIZE_GB=SIZE_MB*1024L;
	//TB字节参考量
	public static final long SIZE_TB=SIZE_GB*1024L;
	//精确到小数后位数
	public static final int SACLE=2;


	public static String conversion(long longSize){
	    if(longSize>=0&&longSize<SIZE_BT){
	    	return longSize+"B";
	    }else if(longSize>=SIZE_BT&&longSize<SIZE_KB){
	    	return longSize/SIZE_BT+"KB";
	    }else if(longSize>=SIZE_KB&&longSize<SIZE_MB){
	    	return longSize/SIZE_KB+"MB";
	    }else if(longSize>=SIZE_MB&&longSize<SIZE_GB){
		    BigDecimal longs=new BigDecimal(Double.valueOf(longSize+"").toString());
		    BigDecimal sizeMB=new BigDecimal(Double.valueOf(SIZE_MB+"").toString());
		    String result=longs.divide(sizeMB, SACLE,BigDecimal.ROUND_HALF_UP).toString();
		    //double result=this.longSize/(double)SIZE_MB;
		    return result+"GB";
	    }else{
		     BigDecimal longs=new BigDecimal(Double.valueOf(longSize+"").toString());
		     BigDecimal sizeMB=new BigDecimal(Double.valueOf(SIZE_GB+"").toString());
		     String result=longs.divide(sizeMB, SACLE,BigDecimal.ROUND_HALF_UP).toString();
		     return result+"TB";
	    }   
	   
	}



}
