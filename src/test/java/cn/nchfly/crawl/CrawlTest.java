package cn.nchfly.crawl;

import cn.nchfly.crawl.utils.HttpClientUtils;
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

        //爬取网站整个html
        String htmlStr = HttpClientUtils.doGetString(url, null);
        System.out.println(htmlStr);
        //使用jsoup解析html
        Document dom = Jsoup.parse(htmlStr);
        //解析出节点
        Elements elements = dom.select("body > div.content.clearfix > div.content-list > ul.article-list2 > li");
        for (Element element : elements){
            String detailUrl = element.select("a").attr("href");
            String infoType = element.select("div.cs_li_div").text();
            detailUrlLists.add(detailUrl);
            infoTypeLists.add(infoType);

        }

        FileUtils.writeLines(new File("C:\\Users\\Administrator\\Desktop\\crawl\\file\\urlList.txt"), detailUrlLists,true);
        //本地测试用
        //String htmlStr = FileUtils.readFileToString(new File("D:\\ts\\jsoup\\aaa.html"),"utf-8");
    }
}
