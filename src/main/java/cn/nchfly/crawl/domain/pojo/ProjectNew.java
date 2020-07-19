package cn.nchfly.crawl.domain.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Description:表t_project_new实体
 * @Author: xtq
 * @Date: 2020/7/19 10:57
 */
@Data
public class ProjectNew {

    private Integer projectId;//项目id

    private String projectCode;//项目编号

    private String projectTitle;//项目标题

    private String projectSubTitle;//项目副标题

    private Double projectMoney;//项目金额

    private String projectMoneySource;//项目金额来源

    private String projectDetail;//项目详情

    private Date projectPublishTime;//项目发布时间

    private Date projectCreateTime;//项目创建时间

    private Date projectUpdateTime;//项目更新时间

    private String provinceName;//省份名称

    private String cityName;//城市名称

    private String detailUrl;//详情url

    private String industryName;//所属行业名称
}
