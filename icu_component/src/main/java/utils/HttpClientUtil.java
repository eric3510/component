package utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/1
 * HashedWheelTimer
 */
public class HttpClientUtil{
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    public HttpClientUtil(){
    }

    public static String doGet(String url, Map<String, String> headers, String encode){
        String out = "";
        CloseableHttpClient client = HttpClients.createDefault();

        try{
            HttpGet get = new HttpGet(url);
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
            if(headers != null){
                Iterator var6 = headers.keySet().iterator();

                while(var6.hasNext()){
                    String key = (String) var6.next();
                    get.setHeader(key, (String) headers.get(key));
                }
            }

            CloseableHttpResponse response = client.execute(get);
            HttpEntity ety = response.getEntity();
            out = EntityUtils.toString(ety, encode);
            EntityUtils.consume(ety);
            response.close();
        }catch(IOException var16){
            var16.printStackTrace();
            logger.error(var16.getMessage());
        }finally{
            try{
                client.close();
            }catch(IOException var15){
                var15.printStackTrace();
                logger.error(var15.getMessage());
            }

        }

        return out;
    }

    public static String doGet(String url){
        return doGet(url, (Map) null, "UTF-8");
    }

    public static String doPost(String url, Map<String, String> params, String encode){
        return HttpClientUtil.doPost(url, null, params, encode);
    }

    public static String doPost(String url, Map<String, String> headers, Map<String, String> params, String encode){
        String out = "";
        CloseableHttpClient client = HttpClients.createDefault();

        try{
            HttpPost post = new HttpPost(url);
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
            if(headers != null){
                Iterator var7 = headers.keySet().iterator();

                while(var7.hasNext()){
                    String key = (String) var7.next();
                    post.setHeader(key, (String) headers.get(key));
                }
            }

            if(params != null){
                String queryCase = "";
                Iterator var21 = params.keySet().iterator();

                while(var21.hasNext()){
                    String key = (String) var21.next();
                    if("".equals(queryCase)){
                        queryCase = key + "=" + (params.get(key) != null ? (String) params.get(key) : "");
                    }else{
                        queryCase = queryCase + "&" + key + "=" + (params.get(key) != null ? (String) params.get(key) : "");
                    }
                }

                logger.debug("HTTP Client post data:" + queryCase);
                StringEntity reqEntity = new StringEntity(queryCase);
                reqEntity.setContentType("application/x-www-form-urlencoded");
                post.setEntity(reqEntity);
            }

            CloseableHttpResponse response = client.execute(post);
            HttpEntity ety = response.getEntity();
            out = EntityUtils.toString(ety, encode);
            EntityUtils.consume(ety);
            response.close();
        }catch(IOException var18){
            var18.printStackTrace();
            logger.error(var18.getMessage());
        }finally{
            try{
                client.close();
            }catch(IOException var17){
                var17.printStackTrace();
                logger.error(var17.getMessage());
            }

        }

        return out;
    }

    public static String doPost(String url, Map<String, String> params){
        return doPost(url, (Map) null, params, "UTF-8");
    }
}
