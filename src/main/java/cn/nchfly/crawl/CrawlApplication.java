package cn.nchfly.crawl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description:
 * @Author: xtq
 */
@EnableScheduling
@SpringBootApplication
public class CrawlApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrawlApplication.class,args);
    }
}
