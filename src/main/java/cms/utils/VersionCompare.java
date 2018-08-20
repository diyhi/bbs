package cms.utils;

/**
 * 版本号比较
 *
 */
public class VersionCompare {
	
	/**
	 * 比较 不含字母比较 例如：5.5.3和5.7.10
	 * @param v1
	 * @param v2
	 * @return -1 0 1
	 */
	public static int compare(String v1,String v2){
        int i=0,j=0,x=0,y=0;
        int v1Len=v1.length();
        int v2Len=v2.length();
        char c;
        do {
            while(i<v1Len){//计算出V1中的点之前的数字
                c=v1.charAt(i++);
                if(c>='0' && c<='9'){
                    x=x*10+(c-'0');//c-‘0’表示两者的ASCLL差值
                }else if(c=='.'){
                    break;//结束
                }else{
                    //无效的字符
                }
            }
            while(j<v2Len){//计算出V2中的点之前的数字
                c=v2.charAt(j++);
                if(c>='0' && c<='9'){
                    y=y*10+(c-'0');
                }else if(c=='.'){
                    break;//结束
                }else{
                    //无效的字符
                }
            }
            if(x<y){
                return -1;
            }else if(x>y){
                return 1;
            }else{
                x=0;y=0;
                continue;
            }
             
        } while ((i<v1Len) || (j<v2Len));
        return 0;
    }

}
