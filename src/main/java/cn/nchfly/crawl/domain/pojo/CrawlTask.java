package cn.nchfly.crawl.domain.pojo;

import lombok.Data;

/**
 * @Description:
 * @Author: xtq
 * @Date: 2020/7/19 16:40
 */
@Data
public class CrawlTask {

    private Integer taskId;//id

    private String taskName;//任务名称

    private String taskClassName;// service服务全包名

}
