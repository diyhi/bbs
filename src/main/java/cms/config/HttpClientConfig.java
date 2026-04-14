package cms.config;

import java.util.concurrent.TimeUnit;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HttpClient配置
 * @author Gao
 *
 */
@Configuration
public class HttpClientConfig {
	
	@Value("${http.maxTotal}")
	private int maxTotal = 500;//设置连接总数
	 
	@Value("${http.defaultMaxPerRoute}")
	private int defaultMaxPerRoute = 100;//设置每个主机最大的并发数
	
//	@Value("${http.validateAfterInactivity}")
//	private int validateAfterInactivity = 1000;
	
	@Value("${http.connectionRequestTimeout}")
	private int connectionRequestTimeout = 5000;//从连接池中获取到连接的最长时间
	
	@Value("${http.connectTimeout}")
	private int connectTimeout = 2000;//设置创建连接的最长时间
	   
	@Value("${http.socketTimeout}")
	private int socketTimeout = 8000;//数据传输的最长时间
	
	@Value("${http.maxIdleTime}")
	private long maxIdleTime = 1L;//空闲时间(用于定期清理空闲连接)
	    
	
	
	
	/**
	 * httpClient连接管理器
	 * @return
	 */
	@Bean(name = "httpClientConnectionManager")
	public PoolingHttpClientConnectionManager createPoolingHttpClientConnectionManager() {
		PoolingHttpClientConnectionManager poolmanager = new PoolingHttpClientConnectionManager();
		poolmanager.setMaxTotal(maxTotal);//最大连接数
		poolmanager.setDefaultMaxPerRoute(defaultMaxPerRoute);//设置每个主机最大的并发数
	//	poolmanager.setValidateAfterInactivity(validateAfterInactivity);//空闲永久连接检查间隔
		return poolmanager;
	}
	
	/**
     * 实例化连接池，设置连接池管理器。
     * 这里需要以参数形式注入上面实例化的连接池管理器
     * @param httpClientConnectionManager
     * @return
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder httpClientBuilder(@Qualifier("httpClientConnectionManager")PoolingHttpClientConnectionManager httpClientConnectionManager){
 
        //HttpClientBuilder中的构造方法被protected修饰，所以这里不能直接使用new来实例化一个HttpClientBuilder，可以使用HttpClientBuilder提供的静态方法create()来获取HttpClientBuilder对象
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
 
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);
 
        return httpClientBuilder;
    }
	
    /**
     * 注入连接池，用于获取httpClient
     * @param httpClientBuilder
     * @return
     */
    @Bean
    public CloseableHttpClient httpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder){
        return httpClientBuilder.build();
    }
	/**
     * 注册RequestConfig
     * @return
    */
    @Bean(name = "requestConfigBuilder")
    public RequestConfig.Builder createRequestConfigBuilder() {
        return RequestConfig.custom().setConnectTimeout(connectTimeout)
            .setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout);

    }
    

 
    /**
     * 使用builder构建一个RequestConfig对象
     * @param builder
     * @return
     */
    @Bean
    public RequestConfig requestConfig(@Qualifier("requestConfigBuilder") RequestConfig.Builder builder){
        return builder.build();
    }
    
    
    /**
     * 定期清理无效连接
     * @param httpClientConnectionManager
     * @return
     */
    @Bean
    public IdleConnectionEvictor createIdleConnectionEvictor(@Qualifier("httpClientConnectionManager")PoolingHttpClientConnectionManager httpClientConnectionManager) {
    	IdleConnectionEvictor idleConnectionEvictor = new IdleConnectionEvictor(httpClientConnectionManager, maxIdleTime, TimeUnit.MINUTES);
    	return idleConnectionEvictor;
    }
	
}
