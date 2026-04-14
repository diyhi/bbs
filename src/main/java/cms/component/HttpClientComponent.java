package cms.component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cms.dto.HttpResult;
import jakarta.annotation.Resource;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;



/**
 * HttpClient管理
 *
 */

@Component("httpClientComponent")
public class HttpClientComponent {
	@Resource CloseableHttpClient httpClient;
	@Resource RequestConfig requestConfig;
	
	 /**
     * 执行get请求,200返回响应内容，其他状态码返回null
     *
     * @param url URL地址
     * @return
     * @throws IOException
     */
    public String doGet(String url) throws IOException {
        //创建httpClient对象
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(url);
        //设置请求参数
        httpGet.setConfig(requestConfig);
        try {
            //执行请求
            response = httpClient.execute(httpGet);
            //判断返回状态码是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 执行带有参数的get请求
     *
     * @param url URL地址
     * @param paramMap 请求参数
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public String doGet(String url, Map<String, String> paramMap) throws IOException, URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        for (String s : paramMap.keySet()) {
            builder.addParameter(s, paramMap.get(s));
        }
        return doGet(builder.build().toString());
    }

    /**
     * 执行post请求
     *
     * @param url URL地址
     * @param paramMap 请求参数
     * @return
     * @throws IOException
     */
    public HttpResult doPost(String url, Map<String, String> paramMap) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        
        
      //  httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        
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
            return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 执行post请求
     *
     * @param url URL地址
     * @return
     * @throws IOException
     */
    public HttpResult doPost(String url) throws IOException {
        return doPost(url, null);
    }


    /**
     * 提交json数据
     *
     * @param url
     * @param json
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResult doPostJson(String url, String json) throws ClientProtocolException, IOException {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(this.requestConfig);

        if (json != null) {
            // 构造一个请求实体
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = this.httpClient.execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
    
    /**
	 * 执行post文件提交
	 * @param url 提交的URL
	 * @param paramMap 参数集合
	 * @param headerMap 请求头集合
	 * @return
	 * @throws IOException
	 */
    public HttpResult doPostFile(String url, Map<String, Object> paramMap,Map<String, String> headerMap) throws IOException {
    	
    	MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
    	mEntityBuilder.setCharset(CharsetUtils.get("UTF-8"));//设置请求的编码格式
    	mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
    	
		HttpPost httpPost = new HttpPost(url);
        
	//	httpPost.setHeader("Content-Type", "application/json");
      //  httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        
        //设置请求参数
        httpPost.setConfig(requestConfig);
        
         
        if (headerMap != null && headerMap.size() >0) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            	httpPost.setHeader(entry.getKey(),entry.getValue());//键: Authorization 值: BEARER eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaWQiOiI1LDE2Nzg4ZTMwMTEiLCJleHAiOjE1NTcyNDM0MzZ9.ZwhqVIbfFgtTB2ZDsFceXWzb2d5PjkuDGVqXB8IKoOw
            }
        }
        
        if (paramMap != null && paramMap.size() >0) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {

            	if(entry.getValue() instanceof byte[]){ 	
            		mEntityBuilder.addBinaryBody("file", (byte[])entry.getValue(),ContentType.DEFAULT_BINARY,entry.getKey());//请求后台的File upload;属性
            		
            	}else if(entry.getValue() instanceof InputStream){ 
            		mEntityBuilder.addBinaryBody("file", (InputStream)entry.getValue(),ContentType.DEFAULT_BINARY,entry.getKey());//请求后台的File upload;属性  使用ContentType.MULTIPART_FORM_DATA属性上传文件时在浏览器打开会直接下载
            		
            	}else{
            		mEntityBuilder.addTextBody(entry.getKey(), (String)entry.getValue());//请求后台的普通参数;属性
            	}
            }
          //将请求实体放入到httpPost中
            httpPost.setEntity(mEntityBuilder.build());
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
     * 执行put请求
     *
     * @param url URL地址
     * @param headerMap 请求头参数
     * @return
     * @throws IOException
     */
    public HttpResult doPut(String url,Map<String, String> headerMap) throws IOException {
    	HttpPut httpPut = new HttpPut(url);
        
        //设置请求参数
    	httpPut.setConfig(requestConfig);
    	
    	if (headerMap != null && headerMap.size() >0) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            	httpPut.setHeader(entry.getKey(),entry.getValue());//键: Authorization 值: BEARER eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaWQiOiI1LDE2Nzg4ZTMwMTEiLCJleHAiOjE1NTcyNDM0MzZ9.ZwhqVIbfFgtTB2ZDsFceXWzb2d5PjkuDGVqXB8IKoOw
            }
        }
    	
        //创建httpClient对象
        CloseableHttpResponse response = null;
        try {
            //执行请求
            response = httpClient.execute(httpPut);
            
            return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
    
    /**
	 * 将url参数转换成map
	 * @param param aa=11&bb=22&cc=33
	 * @return
	 */
	public Map<String, Object> getUrlParams(String param) {
		Map<String, Object> map = new HashMap<String, Object>(0);
		if (org.apache.commons.lang3.StringUtils.isBlank(param)) {
			return map;
		}
		String[] params = param.split("&");
		for (int i = 0; i < params.length; i++) {
			String[] p = params[i].split("=");
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}
		return map;
	}

	/**
	 * 将map转换成url
	 * @param map
	 * @return
	 */
	public String getUrlParamsByMap(Map<String, Object> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String s = sb.toString();
		if (s.endsWith("&")) {
			s = org.apache.commons.lang3.StringUtils.substringBeforeLast(s, "&");
		}
		return s;
	}
}
