package cn.nchfly.crawl.utils;

//import com.yuepeng.common.enums.PlanCrawlService;
//import com.yuepeng.common.enums.ProjectCrawlService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 爬取数据后的解析类
 * @Author: YangYangen
 * @Date: 2020/6/16 17:58
 * Copyright (c) 2020, Samton. All rights reserved
*/
public class YPCrawlUtils {

    /**
     * 获取请求ContextPath
     * 如https://ggzyfw.beijing.gov.cn/jylcgcjs/index.html
     * 得到：https://ggzyfw.beijing.gov.cn
     * @param url
     * @return
     */
    public static String getContextPath(String url){
        if(StringUtils.isBlank(url)) return null;
        return url.substring(0, url.indexOf("/",8));
    }


    /**
     * 解析金额
     * @param money
     * @return
     */
    public static BigDecimal getMoney(String money){
        if(StringUtils.isBlank(money)) return null;
        String tmpMoney = money.replace(",", "").replace("元","");
        if(tmpMoney.contains("万亿")){
            return new BigDecimal(tmpMoney.replace("万亿","")).multiply(new BigDecimal(100000000L));
        }else if(tmpMoney.contains("千亿")){
            return new BigDecimal(tmpMoney.replace("千亿","")).multiply(new BigDecimal(10000000L));
        }else if(tmpMoney.contains("百亿")){
            return new BigDecimal(tmpMoney.replace("百亿","")).multiply(new BigDecimal(1000000L));
        }else if(tmpMoney.contains("十亿")){
            return new BigDecimal(tmpMoney.replace("十亿","")).multiply(new BigDecimal(100000L));
        }else if(tmpMoney.contains("亿")){
            return new BigDecimal(tmpMoney.replace("亿","")).multiply(new BigDecimal(10000L));
        }else if(tmpMoney.contains("千万")){
            return new BigDecimal(tmpMoney.replace("千万","")).multiply(new BigDecimal(1000L));
        }else if(tmpMoney.contains("百万")){
            return new BigDecimal(tmpMoney.replace("百万","")).multiply(new BigDecimal(100L));
        }else if(tmpMoney.contains("十万")){
            return new BigDecimal(tmpMoney.replace("十万","")).multiply(new BigDecimal(10L));
        }else if(tmpMoney.contains("万")){
            return new BigDecimal(tmpMoney.replace("万",""));
        }else{
            return new BigDecimal(tmpMoney.replace("元","")).divide(new BigDecimal(10000L));
        }
    }

    /**
     * 解析日期
     * @param date
     * @return
     */
    public static Date getDate(String date){
        if(StringUtils.isBlank(date)) return null;
        if(DateUtil.isValidDate(date, DateUtil.ISO_EXPANDED_DATE_FORMAT)){
            return DateUtil.stringToDate(date);
        }
        if(DateUtil.isValidDate(date, DateUtil.DATETIME_PATTERN)){
            return DateUtil.stringToDate(date, DateUtil.DATETIME_PATTERN);
        }
        return null;
    }

    /**
     * 获取招标各平台的定制详情
     * @param html
     * @param xpath
     * @param serviceName
     * @return
     *//*
    public static String jsoupProjectBaseDetailHtml(String html, String xpath, String serviceName){
        //默认解析所有
        Elements base = JsoupUtils.jsoupBase(html, xpath);
        //定制详情 - 北京市公共资源交易服务平台
        if(ProjectCrawlService.BEI_JING_ZB.value.equals(serviceName)){
            //过滤基本元素及属性
            base = JsoupUtils.jsoupFilterBase(html, xpath);
            //给表格增加table、th、td标签的sytle样式属性
            base = JsoupUtils.jsoupTableAttr(base);
            base.select("ul:first-child").remove();
        }else{
            //TODO 定制详情 - 实现其他资源平台的处理代码
        }

        return base.toString();
    }
    *//**
     * 获取拟建各平台的定制详情
     * @param html
     * @param xpath
     * @param serviceName
     * @return
     *//*
    public static String jsoupPlanBaseDetailHtml(String html, String xpath, String serviceName){
        //默认解析所有
        Elements base = JsoupUtils.jsoupBase(html, xpath);
        //定制详情 - 北京市公共资源交易服务平台
        if(PlanCrawlService.ZGAZXXW.value.equals(serviceName)){
            base = JsoupUtils.jsoupFilterBase(html, xpath);
            base.select("h5:first-child").remove();
            base.select("div:first-child").remove();
            base.select("div:contains(关键字)").remove();
            base.select("div > div:contains(一篇)").remove();
            base.select("div").last().remove();
            base.select("div").last().remove();
            base = JsoupUtils.jsoupTableAttrTd(base);
            base.select("td[colspan=4]").next().remove();
        }else{
            //TODO 定制详情 - 实现其他资源平台的处理代码
        }

        return base.toString();
    }*/
}

