package me.bytebeats;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.net.URLDecoder;

public class HttpClientPool {
    private HttpClientPool() {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(20);
        connectionManager.setDefaultMaxPerRoute(100);
    }

    private static volatile HttpClientPool INSTANCE;
    private PoolingHttpClientConnectionManager connectionManager;

    public static synchronized HttpClientPool getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpClientPool.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpClientPool();
                }
            }
        }
        return INSTANCE;
    }

    private CloseableHttpClient getHttpClient() {
        return getHttpClient(2000, 2000);
    }

    private CloseableHttpClient getHttpClient(int connectionTimeout, int socketTimeOut) {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionTimeout).setSocketTimeout(socketTimeOut).build();
        return HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
    }

    public String get(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        return getResponseContent(url, httpGet);
    }

    public String post(String url) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        return getResponseContent(url, httpPost);
    }

    public String postJson(String url, String json) throws Exception {
        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        post.setEntity(entity);
        return getResponseContent(url, post);
    }

    private String getResponseContent(String url, HttpRequestBase request) throws Exception {
        HttpResponse response = null;
        try {
            response = this.getHttpClient().execute(request);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw new Exception("got an error from HTTP for url : " + URLDecoder.decode(url, "UTF-8"), e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
            request.releaseConnection();
        }
    }


//    public static void main(String[] args) throws Exception {
//        System.out.println(HttpClientPool.getInstance().get("http://qt.gtimg.cn/q=s_sh600519,s_sz000001,s_usAAPL"));
//    }
}
