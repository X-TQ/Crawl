package cn.nchfly.crawl.task;

import cn.nchfly.crawl.thread.CrawlThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:爬虫定时执行任务
 * @Author: xtq
 * @Date: 2020/7/19 8:54
 */
@Component
public class CrawlTaskService {

    private static Logger log = LoggerFactory.getLogger(CrawlTaskService.class);

    //@Scheduled(cron = "0/30 * * * * ?")//每30执行一次
    @Scheduled(cron = "0/59 0 0-2 * * ?")//每2小时执行一次
    public void craw(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        new Thread(new CrawlThread()).start();
    }
}
