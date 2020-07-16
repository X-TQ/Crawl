package cn.nchfly.crawl.utils;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: Jsoup解析HTML
 * @Author: YangYangen
 * @Date: 2020/5/3 18:03
 * Copyright (c) 2020, Samton. All rights reserved
*/
public class JsoupUtils {

    //jsoup API：https://jsoup.org/apidocs/

    /**
     * 输入HTML文本，解析出基本节点
     * @param html
     * @param xpath
     * @return
     */
    public static Elements jsoupBase(String html, String xpath){
        try {
            //使用jsoup解析html
            Document dom = Jsoup.parse(html);
            //解析出节点
            Elements base = dom.select(xpath);
            return base;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 过滤掉基本的标签及属性
     * @param html
     * @param xpath
     * @return
     */
    public static Elements jsoupFilterBase(String html, String xpath){
        Elements base = null;
        try {
            base = jsoupBase(html, xpath);
            //移除标签
            base.select("script").remove();
            base.select("style").remove();
            //移除属性及属性值
            base.select("[id]").removeAttr("id");
            base.select("[class]").removeAttr("class");
            base.select("[style]").removeAttr("style");
            base.select("[title]").removeAttr("title");
            //移除空元素
            base.select("div:empty").remove();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return base;
    }

    /**
     * 获取原始的html字符串信息
     * @param html
     * @param xpath
     * @param isSelf
     * @return
     */
    public static String jsoupBaseHtml(String html, String xpath, boolean isSelf){
        try {
            Elements base = jsoupBase(html, xpath);
            //包含本节点
            if(isSelf) {
                return base.toString();
            } else {
                //不包含本节点
                return base.html();
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取过滤后的html字符串信息
     * @param html
     * @param xpath
     * @param isSelf
     * @return
     */
    public static String jsoupFilterBaseHtml(String html, String xpath, boolean isSelf){
        try {
            Elements base = jsoupFilterBase(html, xpath);
            //包含本节点
            if(isSelf) {
                return base.toString();
            } else {
                //不包含本节点
                return base.html();
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取xpath指定的节点文本信息
     * @param html
     * @return
     */
    public static String jsoupXpathTxt(String html, String xpath){
        try {
            //使用jsoup解析html
            Document dom = Jsoup.parse(html);
            return dom.select(xpath).get(0).text();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取表格节点下的th对应的td文本内容
     * @param html 整个html网页
     * @param xpath 表格大节点路径
     * @param label th 或 td
     * @param labelTxt 标题内容
     * @return
     */
    public static String jsoupThNextTdTxt(String html, String xpath, String label, String labelTxt) {
        try {
            //使用jsoup解析html
            Document dom = Jsoup.parse(html);

            //以th开头的：dom.select(xpath).select("th:span:matchesOwn(^"+th+")").next().get(0).text()
            //以th结尾的：dom.select(xpath).select("th:span:matchesOwn("+th+"$)").next().get(0).text()
            //获取信息，包含th的
            return dom.select(xpath).select(label+":contains("+labelTxt+")").next().get(0).text();
        } catch (Exception e) {
//            e.printStackTrace();
            return "";
        }
    }

    /**
     * 表格节点，增加table、th、td标签的sytle样式属性
     * @return
     */
    public static Elements jsoupTableAttr(Elements elements){
        Elements base = elements;
        try {
            base.select("[height]").removeAttr("height");
            base.select("[width]").removeAttr("width");
            base.select("[align]").removeAttr("align");
            base.select("[cellpadding]").removeAttr("cellpadding");
            base.select("[cellspacing]").removeAttr("cellspacing");
            base.select("[border]").removeAttr("border");

            base.select("table").attr("style", "border: 1px solid #ccc;border-collapse: collapse;");
            base.select("th").attr("style", "border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;");
            base.select("td").attr("style","text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;");
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return base;
    }

    /**
     * 表格样式，针对表格中列全部是td
     * @param elements
     * @return
     */
    public static Elements jsoupTableAttrTd(Elements elements){
        Elements base = elements;
        try {
            base.select("[height]").removeAttr("height");
            base.select("[width]").removeAttr("width");
            base.select("[align]").removeAttr("align");
            base.select("[cellpadding]").removeAttr("cellpadding");
            base.select("[cellspacing]").removeAttr("cellspacing");
            base.select("[border]").removeAttr("border");

            base.select("table").attr("style", "border: 1px solid #ccc;border-collapse: collapse;");
            for(int i=0;i<base.select("tr > td").size();i++){
                if(i%2==0){
                    base.select("tr > td").get(i).attr("style", "border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;");
                }else{
                    base.select("tr > td").get(i).attr("style", "text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;");
                }
            }
            base.select("div").attr("style", "margin-bottom:20px;");
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return base;
    }

    /**
     * 清除所有元素属性
     * @param htmlStr
     * @return
     */
    public static String removeEleProp(String htmlStr) {
        String regEx_tag = "<(\\w[^>|\\s]*)[\\s\\S]*?>";
        Pattern p = Pattern.compile(regEx_tag, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlStr);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String tagWithProp= m.group(0);
            String tag =m.group(1);
            if ("img".equals(tag)) {
                //img标签保留属性，可进一步处理删除无用属性，仅保留src等必要属性
                m.appendReplacement(sb, tagWithProp);
            }else if ("a".equals(tag)) {
                //a标签保留属性，可进一步处理删除无用属性，仅保留href等必要属性
                m.appendReplacement(sb, tagWithProp);
            }else{
                m.appendReplacement(sb, "<" + tag + ">");
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        String html = FileUtils.readFileToString(new File("D:\\ts\\jsoup\\aaa.html"),"utf-8");
        Elements base = JsoupUtils.jsoupBase(html, "#base1");
        //过滤基本元素及属性
        base = JsoupUtils.jsoupFilterBase(html, "#base1");
        //给表格增加table、th、td标签的sytle样式属性
        base = JsoupUtils.jsoupTableAttr(base);
        base.select("ul:first-child").remove();
        System.out.println(base.toString());
    }
}
