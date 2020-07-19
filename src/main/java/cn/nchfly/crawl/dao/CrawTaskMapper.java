package cn.nchfly.crawl.dao;

import cn.nchfly.crawl.domain.pojo.CrawlTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description:爬虫任务Dao
 * @Author: xtq
 * @Date: 2020/7/19 8:59
 */
@Mapper
public interface CrawTaskMapper {
    //获取所有爬虫任务
    List<CrawlTask> getAll();
}
