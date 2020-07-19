package cn.nchfly.crawl.dao;

import cn.nchfly.crawl.domain.pojo.ProjectNew;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: xtq
 * @Date: 2020/7/19 12:52
 */
@Mapper
public interface ProjectNewMapper {
    //保存
    void add(ProjectNew project);
    //通过项目编号查询
    ProjectNew fingByProjectCode(String projectCode);
}
