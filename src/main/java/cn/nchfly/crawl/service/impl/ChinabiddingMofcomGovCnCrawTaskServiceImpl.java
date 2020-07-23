package cn.nchfly.crawl.service.impl;

import cn.nchfly.crawl.dao.ProjectNewMapper;
import cn.nchfly.crawl.domain.pojo.Flag;
import cn.nchfly.crawl.domain.pojo.ProjectNew;
import cn.nchfly.crawl.service.ChinabiddingMofcomGovCnCrawTaskService;
import cn.nchfly.crawl.service.CrawlService;
import cn.nchfly.crawl.utils.HttpClientUtils;
import cn.nchfly.crawl.utils.YPCrawlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description:中国国际招标网-爬虫Service-https://chinabidding.mofcom.gov.cn/channel/business/bulletinList.sht
 * @Author: xtq
 * @Date: 2020/7/19 8:56
 */
@Service
public class ChinabiddingMofcomGovCnCrawTaskServiceImpl implements ChinabiddingMofcomGovCnCrawTaskService, CrawlService {

    @Autowired
    private ProjectNewMapper projectNewMapper;

    @Value("${crawl.ChinabiddingMofcomGovCn.request.typeCode}")
    private String typeCode;//项目公告类型
    @Value("${crawl.ChinabiddingMofcomGovCn.request.industryCode}")//行业类型
    private String industryCode;
    @Value("${crawl.ChinabiddingMofcomGovCn.detailPre}")
    private String detailPre;
    @Value("${crawl.ChinabiddingMofcomGovCn.pageTotal}")
    private Integer pageTotal;
    @Value("${crawl.ChinabiddingMofcomGovCn.maxLength}")
    private Integer maxLength;

    /**
     * 是否需要继续分页获取数据标识  默认为true
     * 如果为true,说明当前项目发布时间,依旧是当天的;需要进行分页操作
     * 如果为false,说明当前项目发布时间 已经不是当天的了,无需进行后续分页操作
     */
    boolean flag = true;

    /**
     * 初始化参数
     * @param flag
     */
    public void init(Flag flag){
        typeCode = flag.getTypeCode();
        industryCode = flag.getIndustryCode();
        pageTotal = flag.getPageTotal();
        maxLength = flag.getMaxLength();
    }

    /**
     * 执行爬虫
     * @return
     * @throws Exception
     */
    public int crawlTask(Flag flag) throws Exception {
        //初始化参数
        init(flag);

        String url = "https://chinabidding.mofcom.gov.cn/zbwcms/front/bidding/bulletinInfoList";
        //获取项目基本信息,和项目详情url
        Map<String, ProjectNew> resMap = crawlWebDetailUrl(url ,true);
        //爬取详情信息
        int crawlTotal = crawlWebDetail(resMap);
        return crawlTotal;
    }

    /**
     * 爬取主页及分页，返回所有详情页的url地址
     * @param url
     * @param isCurrent
     * @return
     * @throws Exception
     */
    public Map<String, ProjectNew> crawlWebDetailUrl(String url, boolean isCurrent) throws Exception {
        //构建请求参数
        Map<String,String> params = new HashMap<>();
        params.put("typeCode", typeCode);
        if( !industryCode.equals("0")){
            params.put("industryCode", industryCode);
        }


        //构建返回集
        Map<String, ProjectNew> resMap = new HashMap<>();


        //首次
        params.put("pageNumber", "1");
        resMap = processCrawlWebDetailUrl(url,params,resMap,true);

        //分页
        /*for (int page = 2; page <= pageTotal; page ++){
            params.put("pageNumber", page+"");
            resMap = processCrawlWebDetailUrl(url,params,resMap,true);
        }*/
        if(flag){
            for (int page = 2; page <= pageTotal; page ++){
                params.put("pageNumber", page+"");
                resMap = processCrawlWebDetailUrl(url,params,resMap,true);
            }
        }
        return resMap;
    }

    /**
     * 爬取招标项目详情
     * @param resMap
     * @return
     * @throws Exception
     */
    @Transactional
    public int crawlWebDetail(Map<String, ProjectNew> resMap) throws Exception {
        int crawTotal = 0;
        Set<Map.Entry<String, ProjectNew>> entrySet = resMap.entrySet();
        for (Map.Entry<String, ProjectNew> entry : entrySet){
            String url = entry.getKey();
            //获得处理完成的详情页面,和项目编号
            Map<String, String> response = processCrawlWebDetail(url);

            ProjectNew project = entry.getValue();
            //设置项目详情
            project.setProjectDetail(response.get("detailHtml"));
            //设置项目编号
            project.setProjectCode(response.get("projectCode"));

            //添加前,先查询该项目是否已经存在
            //通过项目编号查询,项目是否已经存在,在数据库中
            ProjectNew resProject = projectNewMapper.fingByProjectCode(response.get("projectCode"));
            //resProject为空,项目编号不为空才入库
            if(resProject == null && response.get("projectCode").length() > 0){
                //不存在,添加
                //将该项目保存到数据库
                projectNewMapper.add(project);
                crawTotal ++;
            }else {
                //更新
            }

        }
        return crawTotal;
    }











    /**
     * 处理 爬取主页及分页，返回所有详情页的url地址
     * @param url
     * @param params
     * @param resMap
     * @param isCurrent
     * @return resMap 处理完成之后的项目基本信息,和项目详情url
     * @throws Exception
     */
    public Map<String, ProjectNew> processCrawlWebDetailUrl(String url, Map<String,String> params, Map<String, ProjectNew> resMap, boolean isCurrent) throws Exception{
        //解析官网域名
        String siteDomain = YPCrawlUtils.getContextPath(url);

        //发送请求(ps:该请求是该网站,异步发起的)
        String response = HttpClientUtils.doPostString(url, new ObjectMapper().writeValueAsString(params), "utf-8");
        Map map = new ObjectMapper().readValue(response, Map.class);
        //获得响应list结果集
        List<Map<String,String>> responseList = (List<Map<String, String>>) map.get("rows");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(Map<String,String> entity : responseList){
            //只爬取当天发布的项目
            if(isCurrent){
                /*Date publishTime = sdf.parse(entity.get("publishTime"));
                Date currentDate = sdf.parse(sdf.format(new Date()));*/
                String publishTime = entity.get("publishTime").trim();
                String currentDate = sdf.format(new Date());
                //只爬取今天的
                if(!publishTime.equals(currentDate)){
                    flag = false;
                    //结束循环
                    break;
                }
            }

            ProjectNew project = new ProjectNew();

            //项目id(标识)
            String fdid = entity.get("fdid");

            //项目名(标题)
            String name = entity.get("name");
            //副标题
            String digest = entity.get("digest");
            //资金来源
            String capitalSourceName = entity.get("capitalSourceName");
            //项目发布时间
            Date publishTime = sdf.parse(entity.get("publishTime"));
            //项目创建时间
            Date createTime = sdf.parse(entity.get("createTime"));
            //处理 省
            int areaNameLength = entity.get("areaName").length();
            String areaName = entity.get("areaName").substring(entity.get("areaName").indexOf("国") + 1,areaNameLength) ;
            //拼装出 详情url
            String detailUrl = siteDomain + detailPre +entity.get("filePath");
            //行业名称
            String industryName = entity.get("industryName");

            project.setProjectTitle(name);
            project.setProjectSubTitle(digest);
            //project.setProjectMoney();
            project.setProjectMoneySource(capitalSourceName);
            project.setProjectPublishTime(publishTime);
            project.setProjectCreateTime(createTime);
            project.setProvinceName(areaName);
            project.setDetailUrl(detailUrl);
            project.setIndustryName(industryName);

            //设置返回参数
            resMap.put(detailUrl,project);
        }

        return resMap;
    }

    /**
     * 处理爬取招标项目详情
     * @param url
     * @return resMap 项目详情页面、项目编号
     */
    public Map<String, String> processCrawlWebDetail(String url){
        //项目编号
        String projectCode = "";

        //请求详情url
        String dhtml = HttpClientUtils.doGetString(url, null);
        //解析出详情页面
        Elements elements = Jsoup.parse(dhtml).select("body > section > div.w1000 > div.mt20.clearfix > div.article");
        //移除多余元素
        elements.select("script").first().remove();
        //获得第一个元素
        Element divEle = elements.get(0);
        //获得所有的p元素节点
        Elements allPEles = divEle.select("p");


        //创建table 文档
        Document doc = Jsoup.parse(" <div class=\"zt-child-title\" id=\"lc-11\" style=\"margin-bottom:20px;\">\n" +
                " • 招标项目信息\n" +
                "</div><table style='border: 1px solid #ccc;border-collapse: collapse;'>\n" +
                "\t<tbody>\n" +
                "\n" +
                "\t</tbody>\n" +
                "</table>");
        //获得tbody元素
        Element tbody = doc.select("tbody").first();
        //缓存字符串
        StringBuffer strHtml = new StringBuffer();
        //判断行是否结束标识,2结束
        int flag = 0;
        //遍历所有的p元素(allPEles)
        for(int i = 0; i <allPEles.size(); i++){
            /**
             * ps：太恶心了,这网站。一会p标签包含所有内容，一会多个p标签
             */

            //当allPEles大小等于1时,内容都在一个p标签中; 反之则在多个p标签中
            if(allPEles.size() != 1){
                //开始tr 行
                if(flag == 0 || i == 0){
                    strHtml.append("<tr>");
                }

                /**
                 * ps：这网站好恶心,一会 中文冒号、一会英文冒号
                 */
                String[] split = null;
                //获得p元素 的文本内容
                String text = allPEles.get(i).text().trim();
                split = text.split(":");
                if(split.length < 2){
                    split = text.split("：");
                }

                if(split.length == 2){
                    flag++;

                    // 长度超过50 独占一行
                    if(split[1].trim().length() >= maxLength){
                        //如果是第二列,长度超过了maxLength,那么 <th></th><td></td>
                        if(flag == 2){
                            strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">&nbsp;&nbsp;</th> ");
                            strHtml.append("<td style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">&nbsp;&nbsp;</td>");
                            strHtml.append("<tr>");
                            strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">"+split[0].trim()+"</th> ");
                            strHtml.append("<td colspan='3' style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">"+split[1].trim()+"</td>");
                        }else {
                            flag = 2;
                            strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">"+split[0].trim()+"</th> ");
                            strHtml.append("<td colspan='3' style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">"+split[1].trim()+"</td>");
                        }


                    }else {
                        strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">"+split[0].trim()+"</th> ");
                        strHtml.append("<td style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">"+split[1].trim()+"</td>");
                    }



                    //处理项目编号,后面需要通过项目编号,来判断项目是否已经存在 在数据库中
                    if(split[0].contains("项目编号")){
                        projectCode = split[1].trim();
                    }

                    //补齐tr列
                    if(flag == 1 && i == allPEles.size()-1){
                        strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">"+"&nbsp;"+"</th> ");
                        strHtml.append("<td style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">"+"&nbsp;"+"</td>");
                    }

                    //重置状态
                    split = null;
                    //进来2次后, tr结束(该行结束)
                    if(flag == 2){
                        strHtml.append("</tr>");
                        //重置状态
                        flag = 0;
                    }
                }

            }else {
                //内容都在一个p标签中
                List<TextNode> textNodeList = allPEles.textNodes();
                /*for (TextNode node : textNodeList){*/
                for (int j = 0; j<textNodeList.size(); j++){
                    String str = textNodeList.get(j).text().trim();
                    //String str = node.text();
                    /**
                     * ps：这网站好恶心,一会 中文冒号、一会英文冒号
                     */
                    String[] split = null;
                    //获得p元素 的文本内容
                    String text = str.trim();
                    split = text.split(":");
                    if(split.length < 2){
                        split = text.split("：");
                    }

                    if(split.length == 2){
                        flag++;

                        // 长度超过50 独占一行
                        if(split[1].trim().length() >= maxLength){
                            //如果是第二列,长度超过了maxLength,那么 <th></th><td></td>
                            if(flag == 2){
                                strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">&nbsp;&nbsp;</th> ");
                                strHtml.append("<td style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">&nbsp;&nbsp;</td>");
                                strHtml.append("<tr>");
                                strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">"+split[0].trim()+"</th> ");
                                strHtml.append("<td colspan='3' style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">"+split[1].trim()+"</td>");
                            }else {
                                flag = 2;
                                strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">"+split[0].trim()+"</th> ");
                                strHtml.append("<td colspan='3' style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">"+split[1].trim()+"</td>");
                            }
                        }else {
                            strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">"+split[0].trim()+"</th> ");
                            strHtml.append("<td style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">"+split[1].trim()+"</td>");
                        }

                        //处理项目编号,后面需要通过项目编号,来判断项目是否已经存在 在数据库中
                        if(split[0].contains("项目编号")){
                            projectCode = split[1].trim();
                        }

                        //补齐tr列
                        if(flag == 1 && j == textNodeList.size()-1){
                            strHtml.append("<th style=\"border: 1px solid #ccc;background-color:#e3f1fb;width:160px;text-align:center;\">"+"&nbsp;&nbsp;"+"</th> ");
                            strHtml.append("<td style=\"text-align:left; text-indent: 5px; width:300px; border: 1px solid #ccc;\">"+"&nbsp;&nbsp;"+"</td>");
                        }

                        //重置状态
                        split = null;
                        //进来2次后, tr结束(该行结束)
                        if(flag == 2){
                            strHtml.append("</tr>");
                            //重置状态
                            flag = 0;
                        }
                    }
                }
            }
        }


        //最终处理完成的 tr td 字符串节点元素信息
        String resStrEle = strHtml.toString();
        //将字符串节点元素信息添加到tbody中
        tbody.html(resStrEle);
        //最终处理完成的html 招标项目详情
        String detailHtml = doc.html().trim();

        //返回详情页面和项目编号
        Map<String, String> resMap = new HashMap<>();
        resMap.put("detailHtml",detailHtml);
        resMap.put("projectCode",projectCode);
        return resMap;
    }

}
