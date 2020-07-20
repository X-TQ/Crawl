package cn.nchfly.crawl;

import cn.nchfly.crawl.dao.CrawTaskMapper;
import cn.nchfly.crawl.domain.pojo.Flag;
import cn.nchfly.crawl.service.CrawlService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @Author: xtq
 * @Date: 2020/7/19 12:04
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CrawlSpringbootTest {

    @Autowired
    private CrawlService service;
    @Autowired
    private CrawTaskMapper crawTaskMapper;

    @Test
    public void testDeatilUrl() throws Exception {
        //手动控制,执行任务
        /*Flag flag = crawTaskMapper.getIsExecutionFlag();
        int size = service.crawlTask(flag);
        System.out.println(size);*/
    }
}
