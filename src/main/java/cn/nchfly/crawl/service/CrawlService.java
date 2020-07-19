package cn.nchfly.crawl.service;

import cn.nchfly.crawl.domain.pojo.ProjectNew;

import java.util.List;
import java.util.Map;

/**
 * @Description:超类爬虫服务接口
*/
public interface CrawlService {


    /**
     * 执行爬虫
     * @return
     * @throws Exception
     */
    int crawlTask() throws Exception;

    /**
     * 爬取主页及分页，返回所有详情页的url地址
     * @param crawlUrl
     * @param isCurrent
     * @return
     * @throws Exception
     */
    Map<String, ProjectNew> crawlWebDetailUrl(String crawlUrl, boolean isCurrent) throws Exception;


    /**
     * 爬取招标项目详情
     * @return
     * @throws Exception
     */
    int crawlWebDetail(Map<String, ProjectNew> resMap) throws Exception;

}
