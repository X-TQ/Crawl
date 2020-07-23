package cn.nchfly.crawl.thread;

import cn.nchfly.crawl.common.SpringBeanUtils;
import cn.nchfly.crawl.dao.CrawTaskMapper;
import cn.nchfly.crawl.domain.pojo.CrawlTask;
import cn.nchfly.crawl.domain.pojo.Flag;
import cn.nchfly.crawl.service.CrawlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description:爬虫线程
 * @Author: xtq
 * @Date: 2020/7/19 16:30
 */
public class CrawlThread implements Runnable {

    private static Logger log = LoggerFactory.getLogger(CrawlThread.class);

    private CrawTaskMapper crawTaskMapper;

    private Flag flag;

    public CrawlThread(Flag flag){
        this.flag = flag;
        this.crawTaskMapper = SpringBeanUtils.getBean(CrawTaskMapper.class);
    }

    //任务
    public void run() {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //获取所有爬虫任务
            List<CrawlTask> crawlTaskList = crawTaskMapper.getAll();
            for (CrawlTask crawlTask : crawlTaskList){
                //多个爬虫任务创建多少线程
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String taskName = crawlTask.getTaskName();
                            String taskClassName = crawlTask.getTaskClassName();
                            CrawlService service = (CrawlService) SpringBeanUtils.getBeanByName(taskClassName);
                            log.info("[定时任务【{}|招标爬虫】开始执行]时间: {}" ,taskName ,sdf.format(new Date()));
                            int crawlTotal = service.crawlTask(flag);
                            log.info("[定时任务【{}|招标爬虫】爬取总数]总数: {}" ,taskName ,crawlTotal);
                            log.info("[定时任务【{}|招标爬虫】结束执行]时间: {}" ,taskName ,sdf.format(new Date()));
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }).start();


            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
