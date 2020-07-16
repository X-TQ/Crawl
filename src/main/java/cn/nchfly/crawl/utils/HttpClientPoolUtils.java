package cn.nchfly.crawl.utils;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @Description: HttpClient连接池
 * @Author: YangYangen
 * @Date: 2020/5/3 18:03
 * Copyright (c) 2020, Samton. All rights reserved
*/
public class HttpClientPoolUtils {
    private static PoolingHttpClientConnectionManager cm;

    static {
        //创建连接池管理器
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        //设置最大连接数
        cm.setMaxTotal(200);
        //设置每个主机的最大连接数，访问每一个网站指定的连接数，不会影响其他网站的访问
        cm.setDefaultMaxPerRoute(20);
    }

    public static CloseableHttpClient getHttpClient(){
        return HttpClients.custom().setConnectionManager(cm).build();
    }

}
