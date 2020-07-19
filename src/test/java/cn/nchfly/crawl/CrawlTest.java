package cn.nchfly.crawl;

import cn.nchfly.crawl.utils.HttpClientUtils;
import cn.nchfly.crawl.utils.JsoupUtils;
import cn.nchfly.crawl.utils.YPCrawlUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: xtq
 */
public class CrawlTest {


    /**
     * @Description-中国国际招标
     * https://chinabidding.mofcom.gov.cn/channel/business/bulletinList.shtml
     */
    @Test
    public void ZhongGuoGjZb() throws Exception {
        String baseUrl = "https://chinabidding.mofcom.gov.cn/channel/business/bulletinList.shtml";
        //爬取网站整个html
        String baseHtmlStr = HttpClientUtils.doGetString(baseUrl, null);
        //解析 需要的ul li列表
        Elements baseEles = Jsoup.parse(baseHtmlStr).select("body > section");

        String html = baseEles.html();
        System.out.println(html);
    }

    /**
     * @Description-北京公共资源交易网
     * @throws Exception
     */
    @Test
    public void beiJingGongGongZyJxw() throws Exception {
        List<String> detailUrlLists = new ArrayList<>();
        List<String> infoTypeLists = new ArrayList<>();

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

        List<String> readlist = FileUtils.readLines(new File("E:\\Crawl\\file\\url.txt"), "UTF-8");

        //取出一个url，抓取详情
        String reqUrl = readlist.get(0);
        String detailHtml = HttpClientUtils.doGetString(reqUrl, null);
        Elements e = Jsoup.parse(detailHtml).select("body > div.content.clearfix > div.content-list > div.newsCon > div#base1 ");
        String html = e.html();
        System.out.println(html);

        //移除不需要的节点
        e.select("ul").first().remove();
        e.select("div").last().remove();
        e.select("table").last().remove();
        e.select("script").last().remove();
        e.select("div").last().remove();
        e.select("div").last().remove();
        String html1 = e.html();
        System.out.println(html1);

        //加样式
        //String html2 = JsoupUtils.jsoupTableAttrTd(e).html();
        e.select("table").attr("style", "border: 1px solid #ccc;border-collapse: collapse;");
        e.select("tbody tr th").attr("style", "border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;");
        e.select("tbody tr td").attr("style", "text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;");
        e.select("div").attr("style", "margin-bottom:20px;");

        String html2 = e.html();
        System.out.println(html2);
    }
}
