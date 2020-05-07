package cms.web.action;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;


import cms.bean.HttpResult;


/**
 * Http请求配置
 * @author Gao
 *
 */
@Component("httpRequestConfig")
public class HttpRequestConfig {
	@Resource CloseableHttpClient httpClient;
	@Resource RequestConfig requestConfig;
	
	
	 /**
	 * 执行post提交
	 * @param url 提交的URL
	 * @param paramMap 参数集合
	 * @return
	 * @throws IOException
	 */
    public HttpResult doPost(String url, Map<String, String> paramMap) throws IOException {
    	HttpPost httpPost = new HttpPost(url);
        
        
    //	httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
      
    	//设置请求参数
    	httpPost.setConfig(requestConfig);
    	if (paramMap != null) {
    		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    		for (String s : paramMap.keySet()) {
    			parameters.add(new BasicNameValuePair(s, paramMap.get(s)));
    		}
    		//构建一个form表单式的实体
    		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, Charset.forName("UTF-8"));
    		//将请求实体放入到httpPost中
    		httpPost.setEntity(formEntity);
    	}
    	//创建httpClient对象
    	CloseableHttpResponse response = null;
    	try {
    		//执行请求
    		response = httpClient.execute(httpPost);
    		return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(),"UTF-8"));
    	} catch (IOException e) {
        	throw e;
        } finally {
    		if (response != null) {
    			response.close();
    		}
    	}
    }
	
    
    
}
