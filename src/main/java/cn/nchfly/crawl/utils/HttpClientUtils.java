package cn.nchfly.crawl.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 处理http请求和响应
 * @Author: YangYangen
 * @Date: 2020/5/3 16:07
 * Copyright (c) 2020, Samton. All rights reserved
*/
public class HttpClientUtils {

    /**
     * Get请求，返回HTML（字符串形式），默认utf-8编码
     * @param url
     * @param paramsJson
     * @return
     */
    public static String doGetString(String url, String paramsJson){
        return doGetString(url, paramsJson, "utf-8");
    }

    /**
     * Post请求，返回HTML（字符串形式），默认utf-8编码
     * @param url
     * @param paramsJson
     * @return
     */
    public static String doPostString(String url, String paramsJson){
        return doPostString(url, paramsJson, "utf-8");
    }


    /**
     * Get请求，返回HTML（字符串形式）
     * @param url
     * @return
     */
    public static String doGetString(String url, String paramsJson, String charset){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        if(charset == null) charset = "utf-8";

        try {
            //创建HttpClient对象
//            httpClient = HttpClients.createDefault();
//            httpClient = HttpClientPoolUtils.getHttpClient(HttpClientPoolUtils.getHttpClientPool());
            httpClient = HttpClientPoolUtils.getHttpClient();

            //初始化url
            URI uri = new URIBuilder(url).build();

            //创建URIBuilder设置参数
            if(paramsJson != null && paramsJson.length() > 0){
                List<NameValuePair> pairs = new ArrayList<>();
                Map<String, String> paramsMap = JsonUtils.jsonToPojo(paramsJson, Map.class);
                for(Map.Entry<String, String> map : paramsMap.entrySet()){
                    NameValuePair pair = new BasicNameValuePair(map.getKey(), map.getValue());
                    pairs.add(pair);
                }
                URIBuilder uriBuilder = new URIBuilder(url).setParameters(pairs);
                uri = uriBuilder.build();
            }

            //声明httpGet请求对象
            HttpGet httpGet = new HttpGet(uri);

            //设置用户代理，解决反爬虫访问问题
            httpGet.setHeader("User-Agent","");

            //创建连接请求参数
            RequestConfig config = RequestConfig.custom().setConnectTimeout(1000) //创建连接的最长时间
                .setConnectionRequestTimeout(500) //从连接池中获取到连接的最长时间
                .setSocketTimeout(10 * 1000) //数据传输的最长时间
                .build();

            //设置连接请求参数
            httpGet.setConfig(config);

            //使用HttpClient发起请求，获取response
            response = httpClient.execute(httpGet);

            //判断响应的状态码是否200
            if(response.getStatusLine().getStatusCode() == 200){
                //返回200成功，获取响应数据
                return EntityUtils.toString(response.getEntity(), charset);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(response != null){
                    response.close();
                }
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Post请求，返回HTML（字符串形式）
     * @param url
     * @param paramsJson
     * @return
     */
    public static String doPostString(String url, String paramsJson, String charset) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        if(charset == null) charset = "utf-8";

        try {
            //创建HttpClient对象
//            httpClient = HttpClients.createDefault();
//            httpClient = HttpClientPoolUtils.getHttpClient(HttpClientPoolUtils.getHttpClientPool());
            httpClient = HttpClientPoolUtils.getHttpClient();
            //声明httpPost请求对象
            HttpPost httpPost = new HttpPost(url);

            //设置用户代理，解决反爬虫访问问题
            httpPost.setHeader("User-Agent","");

            //创建连接请求参数
            RequestConfig config = RequestConfig.custom().setConnectTimeout(1000) //创建连接的最长时间
                    .setConnectionRequestTimeout(500) //从连接池中获取到连接的最长时间
                    .setSocketTimeout(10 * 1000) //数据传输的最长时间
                    .build();

            //设置连接请求参数
            httpPost.setConfig(config);

            //创建URIBuilder设置参数
            if(paramsJson != null && paramsJson.length() > 0){
                List<NameValuePair> pairs = new ArrayList<>();
                Map<String, String> paramsMap = JsonUtils.jsonToPojo(paramsJson, Map.class);
                for(Map.Entry<String, String> map : paramsMap.entrySet()){
                    NameValuePair pair = new BasicNameValuePair(map.getKey(), map.getValue());
                    pairs.add(pair);
                }
                //创建表单实体对象，封装请求参数
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs, charset);
                //把表单实体设置到请求HttpPost中
                httpPost.setEntity(formEntity);
            }

            //使用HttpClient发起请求，获取response
            response = httpClient.execute(httpPost);

            //判断响应的状态码是否200
            if(response.getStatusLine().getStatusCode() == 200){
                //返回200成功，获取响应数据
                return EntityUtils.toString(response.getEntity(), charset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(response != null){
                    response.close();
                }
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

        public static void main(String[] args) {
//        String html = doGetString("http://www.samton.net/");
//        String html = doGetString("https://www.oschina.net/search", "{\"scope\":\"project\",\"q\":\"HttpClient\"}");
//        String html = doPostString("https://www.oschina.net/search", "{\"scope\":\"project\",\"q\":\"HttpClient\"}");
            String html = doGetString("https://ggzyfw.beijing.gov.cn/jylczfcg/20200630/1175322.html", null, "utf-8");
        System.out.println(html);
    }
}
