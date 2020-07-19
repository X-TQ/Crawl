package cn.nchfly.crawl.domain.vo;

import lombok.Data;

/**
 * @Description: 爬虫数据类型对应表
*/
@Data
public class ProjectRangeVO {
    private Integer datasourceTypeId;
    private String datasourceTypeName;
    private Integer datasourceId;
    private String datasourceWebname;
    private Integer datasourceIndustryId;
    private String datasourceIndustryName;
    private Integer datasourceInfotypeId;
    private String datasourceInfotypeName;
    private Integer infotypeId;
    private String infotypeName;
    private Integer industryId;
    private String industryName;
    private Integer provinceId;
    private Integer cityId;
    private String crawlUrl;



}
