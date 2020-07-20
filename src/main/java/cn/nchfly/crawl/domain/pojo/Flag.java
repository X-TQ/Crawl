package cn.nchfly.crawl.domain.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: xtq
 * @Date: 2020/7/20 20:38
 */
@Data
public class Flag implements Serializable {

    private Integer id;//id

    private String typeCode;//项目公告类型

    private String industryCode;//行业类型

    private Integer pageTotal;//总页数

    private Integer maxLength;//最大长度

    private Integer flag;//是否需要执行 0：不执行 1：执行
}
