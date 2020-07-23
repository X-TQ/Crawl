package cn.nchfly.crawl.task;

import cn.nchfly.crawl.dao.CrawTaskMapper;
import cn.nchfly.crawl.domain.pojo.Flag;
import cn.nchfly.crawl.thread.CrawlThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CrawTaskMapper crawTaskMapper;

    private static Logger log = LoggerFactory.getLogger(CrawlTaskService.class);


    //@Scheduled(cron = "0/59 0 0-2 * * ?")//每2小时执行一次
    //@Scheduled(cron = "0/30 * * * * ?")//每30执行一次
    //@Scheduled(cron = "0/59 0/3 * * * ?")//每3分钟执行
    @Scheduled(cron = "30/50 * * * * ?")//每50执行一次
    public void craw(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //手动控制,执行任务
        Flag flag = crawTaskMapper.getIsExecutionFlag();

        //延迟3秒
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(flag.getFlag() == 1){
            log.info("[定时任务【爬虫执行标识|{}】]时间: {}" ,flag.getFlag(), sdf.format(new Date()));
            log.info("[定时任务【招标爬虫即将执行】]时间: {}" ,sdf.format(new Date()));
            new Thread(new CrawlThread(flag)).start();
        }else {
            log.info("[定时任务【爬虫执行标识|{}】]时间: {}" ,flag.getFlag(), sdf.format(new Date()));
            log.info("[定时任务【招标爬虫无需执行】]时间: {}" ,sdf.format(new Date()));
        }

    }
}
