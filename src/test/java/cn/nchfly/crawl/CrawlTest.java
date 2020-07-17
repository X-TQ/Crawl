package cn.nchfly.crawl;

import cn.nchfly.crawl.utils.HttpClientUtils;
import cn.nchfly.crawl.utils.YPCrawlUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: xtq
 */
public class CrawlTest {
    public static void main(String[] args) throws Exception {
        List<String> detailUrlLists = new ArrayList<>();
        List<String> infoTypeLists = new ArrayList<>();
        //中国国际招标
        //String url = "https://chinabidding.mofcom.gov.cn/channel/business/bulletinList.shtml";

        //北京公共资源交易网
        String url = "https://ggzyfw.beijing.gov.cn/jylcgcjs/index.html";

        //解析官网域名
        String siteDomain = YPCrawlUtils.getContextPath(url);

        //爬取网站整个html
        String htmlStr = HttpClientUtils.doGetString(url, null);
        System.out.println(htmlStr);
        //使用jsoup解析html
        Document dom = Jsoup.parse(htmlStr);
        //解析出节点
        Elements elements = dom.select("body > div.content.clearfix > div.content-list > ul.article-list2 > li");
        for (Element element : elements){
            String detailUrl = siteDomain + element.select("a").attr("href");
            String infoType = element.select("div.cs_li_div").text();
            detailUrlLists.add(detailUrl);
            infoTypeLists.add(infoType);
        }

        //分页
        Elements pageElements = dom.select("body > div.content.clearfix > div.content-list > div.pagesite > div.pages > ul > li:nth-child(1)");
        Element element = pageElements.get(0);
        String str = element.select("a").text();
        //当前页码
        Integer totalPage = Integer.parseInt(str.substring(str.indexOf("/") +1 , str.indexOf("页")));
        for(int page = 2 ; page <= totalPage; page ++){
            String newUrl = "https://ggzyfw.beijing.gov.cn/jylcgcjs/index_"+page+".html";
            String cHtmlStr = HttpClientUtils.doGetString(newUrl, null);

            //使用jsoup解析html
            Document dom2 = Jsoup.parse(cHtmlStr);
            //解析出节点
            Elements elements2 = dom2.select("body > div.content.clearfix > div.content-list > ul.article-list2 > li");
            for (Element element2 : elements2){
                String detailUrl2 = siteDomain + element2.select("a").attr("href");
                String infoType2 = element2.select("div.cs_li_div").text();
                detailUrlLists.add(detailUrl2);
                infoTypeLists.add(infoType2);
            }
        }


        FileUtils.writeLines(new File("E:\\Crawl\\file\\url.txt"), detailUrlLists,true);

        //本地测试用
        //String htmlStr = FileUtils.readFileToString(new File("D:\\ts\\jsoup\\aaa.html"),"utf-8");
    }
}
