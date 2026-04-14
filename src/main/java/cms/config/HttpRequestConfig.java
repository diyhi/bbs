package cms.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import cms.dto.HttpResult;
import jakarta.annotation.Resource;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;




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
	 * 执行get提交
	 * @param url 提交的URL
	 * @param headerMap 请求头集合
	 * @return
	 * @throws IOException
	 */
    public HttpResult doGet(String url, Map<String, String> headerMap) throws IOException {
    	HttpGet httpGet = new HttpGet(url);
        
        
    //	httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
    	if (headerMap != null && headerMap.size() >0) {
	        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
	        	httpGet.setHeader(entry.getKey(),entry.getValue());//键: Authorization 值: BEARER eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaWQiOiI1LDE2Nzg4ZTMwMTEiLCJleHAiOjE1NTcyNDM0MzZ9.ZwhqVIbfFgtTB2ZDsFceXWzb2d5PjkuDGVqXB8IKoOw
	        }
	    }
    	
    	//设置请求参数
    	httpGet.setConfig(requestConfig);

    	//创建httpClient对象
    	CloseableHttpResponse response = null;
    	try {
    		//执行请求
    		response = httpClient.execute(httpGet);
    		return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(),"UTF-8"));
    	} catch (IOException e) {
        	throw e;
        } finally {
    		if (response != null) {
    			response.close();
    		}
    	}
    }
	
	
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
    		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8);
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
	
    /**
	 * 执行post提交
	 * @param url 提交的URL
	 * @param paramMap 参数集合
	 * @param headerMap 请求头集合
	 * @return
	 * @throws IOException
	 */
    public HttpResult doPost(String url, Map<String, String> paramMap,Map<String, String> headerMap) throws IOException {
    	HttpPost httpPost = new HttpPost(url);
        
        
    //	httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
    	if (headerMap != null && headerMap.size() >0) {
	        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
	        	httpPost.setHeader(entry.getKey(),entry.getValue());//键: Authorization 值: BEARER eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaWQiOiI1LDE2Nzg4ZTMwMTEiLCJleHAiOjE1NTcyNDM0MzZ9.ZwhqVIbfFgtTB2ZDsFceXWzb2d5PjkuDGVqXB8IKoOw
	        }
	    }
    	
    	
    	//设置请求参数
    	httpPost.setConfig(requestConfig);
    	if (paramMap != null) {
    		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    		for (String s : paramMap.keySet()) {
    			parameters.add(new BasicNameValuePair(s, paramMap.get(s)));
    		}
    		//构建一个form表单式的实体
    		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8 );
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
