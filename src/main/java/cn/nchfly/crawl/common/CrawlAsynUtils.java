package cn.nchfly.crawl.common;

import cn.nchfly.crawl.utils.HttpClientPoolUtils;
import cn.nchfly.crawl.utils.HttpClientUtils;
import cn.nchfly.crawl.utils.JsonUtils;
import cn.nchfly.crawl.utils.YPCrawlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: xtq
 * @Date: 2020/7/18 14:11
 */
public class CrawlAsynUtils {

    public static String post(String url, Map<String,String> params,String charset){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        if(charset == null) charset = "utf-8";

        try {
            // 模拟（创建）一个浏览器用户
            //httpClient = HttpClients.createDefault();
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

            //设置请求参数
            if (params != null) {
                List<NameValuePair> form = new ArrayList<NameValuePair>();
                for (String name : params.keySet()) {
                    form.add(new BasicNameValuePair(name, params.get(name)));
                }

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form,
                        "utf-8");
                httpPost.setEntity(entity);
            }

            //发送请求
            response = httpClient.execute(httpPost);

            //判断响应的状态码是否200
            if(response.getStatusLine().getStatusCode() == 200){
                //返回200成功，获取响应数据
                return EntityUtils.toString(response.getEntity(), charset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws IOException {
        String url = "https://chinabidding.mofcom.gov.cn/zbwcms/front/bidding/bulletinInfoList";
        //解析官网域名
        String siteDomain = YPCrawlUtils.getContextPath(url);
        Map<String,String> params = new HashMap<>();
        params.put("typeCode", "1");
        //String content = post(url, params, "utf-8");
        String content = HttpClientUtils.doPostString(url, new ObjectMapper().writeValueAsString(params), "utf-8");
        Map map = new ObjectMapper().readValue(content, Map.class);
        List<Map<String,String>> rows = (List<Map<String, String>>) map.get("rows");

        Map<String, String> entity = rows.get(5);
        String createTime = entity.get("createTime");
        String industryName = entity.get("industryName");
        String areaName = entity.get("areaName");
        String publishTime = entity.get("publishTime");
        String name = entity.get("name");
        String capitalSourceName = entity.get("capitalSourceName");
        String digest = entity.get("digest");
        //详情url
        String filePath = siteDomain + "/bidDetail" +entity.get("filePath");




        //请求详情url
        String dhtml = HttpClientUtils.doGetString(filePath, null);
        //解析出详情页面
        Elements elements = Jsoup.parse(dhtml).select("body > section > div.w1000 > div.mt20.clearfix > div.article");
        //移除多余元素
        elements.select("script").first().remove();
        //获得第一个元素
        Element divEle = elements.get(0);
        //获得所有的p元素节点
        Elements allPEles = divEle.select("p");


        //创建table 文档
        Document doc = Jsoup.parse(" <div class=\"zt-child-title\" id=\"lc-11\" style=\"margin-bottom:20px;\">\n" +
                " • 招标项目信息\n" +
                "</div><table style='border: 1px solid #ccc;border-collapse: collapse;'>\n" +
                "\t<tbody>\n" +
                "\n" +
                "\t</tbody>\n" +
                "</table>");
        //获得tbody元素
        Element tbody = doc.select("tbody").first();
        //缓存字符串
        StringBuffer strHtml = new StringBuffer();
        //判断行是否结束标识,2结束
        int flag = 0;
        //遍历所有的p元素(allPEles)
        for(int i = 0; i <allPEles.size(); i++){
            /**
             * ps：太恶心了,这网站。一会p标签包含所有内容，一会多个p标签
             */

            //当allPEles大小等于1时,内容都在一个p标签中; 反之则在多个p标签中
            if(allPEles.size() != 1){
                //开始tr 行
                if(flag == 0 || i == 0){
                    strHtml.append("<tr>");
                }

                /**
                 * ps：这网站好恶心,一会 中文冒号、一会英文冒号
                 */
                String[] split = null;
                //获得p元素 的文本内容
                String text = allPEles.get(i).text().trim();
                split = text.split(":");
                if(split.length < 2){
                    split = text.split("：");
                }

                if(split.length == 2){
                    flag++;
                    strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">"+split[0]+"</th> ");
                    strHtml.append("<td style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">"+split[1]+"</td>");

                    //重置状态
                    split = null;
                    //进来2次后, tr结束(该行结束)
                    if(flag == 2){
                        strHtml.append("</tr>");
                        //重置状态
                        flag = 0;
                    }
                }

            }else {
                //内容都在一个p标签中
                List<TextNode> textNodeList = allPEles.textNodes();
                for (TextNode node : textNodeList){
                    String str = node.text();
                    /**
                     * ps：这网站好恶心,一会 中文冒号、一会英文冒号
                     */
                    String[] split = null;
                    //获得p元素 的文本内容
                    String text = str.trim();
                    split = text.split(":");
                    if(split.length < 2){
                        split = text.split("：");
                    }

                    if(split.length == 2){
                        flag++;
                        strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">"+split[0]+"</th> ");
                        strHtml.append("<td style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">"+split[1]+"</td>");

                        //重置状态
                        split = null;
                        //进来2次后, tr结束(该行结束)
                        if(flag == 2){
                            strHtml.append("</tr>");
                            //重置状态
                            flag = 0;
                        }
                    }
                }
            }
        }

        //最终处理完成的 tr td 字符串节点元素信息
        String resStrEle = strHtml.toString();
        //将字符串节点元素信息添加到tbody中
        tbody.html(resStrEle);
        //最终处理完成的html 招标项目详情
        String html = doc.html();
        System.out.println(html);















        /*for (Map<String,String> entity : rows){
            String createTime = entity.get("createTime");
            String industryName = entity.get("industryName");
            String areaName = entity.get("areaName");
            String publishTime = entity.get("publishTime");
            String name = entity.get("name");
            String capitalSourceName = entity.get("capitalSourceName");
            String digest = entity.get("digest");
            //详情url
            String filePath = siteDomain + "/bidDetail" +entity.get("filePath");

            //请求详情url
            String dhtml = HttpClientUtils.doGetString(filePath, null);
            //解析出详情页面
            Elements elements = Jsoup.parse(dhtml).select("body > section > div.w1000 > div.mt20.clearfix > div.article");
            //移除多余元素
            elements.select("script").first().remove();
            Element element = elements.get(0);
            Elements allElements = element.select("p");


            Document doc = Jsoup.parse(" <div class=\"zt-child-title\" id=\"lc-11\" style=\"margin-bottom:20px;\">\n" +
                    " • 招标项目信息\n" +
                    "</div><table style='border: 1px solid #ccc;border-collapse: collapse;'>\n" +
                    "\t<tbody>\n" +
                    "\n" +
                    "\t</tbody>\n" +
                    "</table>");
            Element tbody = doc.select("tbody").first();
            //遍历elements
            StringBuffer strHtml = new StringBuffer();
            int flag = 1;
            for(int i = 0; i <= elements.size(); i++){
                *//**
                 * 偶数 为th
                 * 计数 为td
                 *//*
                if(flag > 4 || i == 0){
                    strHtml.append("<tr>");
                }

                if(i%2==0){
                    strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\"><p>一、招标条件</p></th> ");
                }else {
                    strHtml.append("<td style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\"><p>内容</p></td>");
                }

                flag++;

                if(flag > 4 || i == 0){
                    strHtml.append("</tr>");
                    flag = 1;
                }
            }

            String resHtml = strHtml.toString();
            tbody.html(resHtml);
            String html = doc.html();
            System.out.println(html);


        }*/

    }
}
