package cn.nchfly.crawl;

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

    @Test
    public void testDeatilUrl() throws Exception {
        int size = service.crawlTask();
        System.out.println(size);
    }
}
