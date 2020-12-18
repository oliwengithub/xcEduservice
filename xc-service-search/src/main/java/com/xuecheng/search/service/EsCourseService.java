package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.From;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class EsCourseService {

    @Value("${xuecheng.elasticsearch.course.index}")
    private String course_index;
    @Value("${xuecheng.elasticsearch.course.type}")
    private String course_type;
    @Value("${xuecheng.elasticsearch.course.source_field}")
    private String course_source_field;

    @Value("${xuecheng.elasticsearch.media.index}")
    private String media_index;
    @Value("${xuecheng.elasticsearch.media.type}")
    private String media_type;
    @Value("${xuecheng.elasticsearch.media.source_field}")
    private String media_source_field;

    @Autowired
    @Qualifier("restHighLevelClient")
    RestHighLevelClient restHighLevelClient;

    //课程搜索
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        if(courseSearchParam == null){
            courseSearchParam = new CourseSearchParam();
        }
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(course_index);
        //设置搜索类型
        searchRequest.types(course_type);
        // 构建查询对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        page = page <= 1 ? 0:page;
        size = size <= 0 ? 20:size;
        int from = page * size;
        // 设置分页参数
        searchSourceBuilder.from(from).size(size);
        //过虑源字段
        String[] sourceFieldArray = course_source_field.split(",");
        searchSourceBuilder.fetchSource(sourceFieldArray,new String[]{});
        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //搜索条件
        //根据关键字搜索
        if(StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                    .minimumShouldMatch("70%")
                    .field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getMt())){
            //根据一级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getSt())){
            //根据二级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            //根据难度等级
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }

        //设置boolQueryBuilder到searchSourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        QueryResult<CoursePub> queryResult = new QueryResult();
        List<CoursePub> list = new ArrayList<>();
        try {
            //执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //获取响应结果
            SearchHits hits = searchResponse.getHits();
            //匹配的总记录数
            long totalHits = hits.totalHits;
            queryResult.setTotal(totalHits);
            SearchHit[] searchHits = hits.getHits();
            //定义高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<font class='eslight'>");
            highlightBuilder.postTags("</font>");
            highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
            searchSourceBuilder.highlighter(highlightBuilder);
            for(SearchHit hit:searchHits){
                CoursePub coursePub = new CoursePub();
                //源文档
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出id
                String id = (String) sourceAsMap.get("id");
                coursePub.setId(id);
                //取出name
                String name = (String) sourceAsMap.get("name");
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if(highlightFields.get("name")!=null){
                    HighlightField highlightField = highlightFields.get("name");
                    Text[] fragments = highlightField.fragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for(Text text:fragments){
                        stringBuffer.append(text);
                    }
                    name = stringBuffer.toString();
                }
                coursePub.setName(name);
                //图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                //价格
                float price = 0f;
                try {
                    if(sourceAsMap.get("price")!=null ){

                        price = new Double((sourceAsMap.get("price").toString())).floatValue();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                //旧价格
                float priceOld = 0f;
                try {
                    if(sourceAsMap.get("price_old")!=null ){
                        priceOld = new Double(sourceAsMap.get("price_old").toString()).floatValue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(priceOld);
                // 获取课程收费规则
                String charge = sourceAsMap.get("charge").toString();
                coursePub.setCharge(charge);
                // 取出课程计划
                String teachplan = (String) sourceAsMap.get("teachplan");
                coursePub.setTeachplan(teachplan);
                //将coursePub对象放入list
                list.add(coursePub);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        queryResult.setList(list);
        QueryResponseResult<CoursePub> queryResponseResult = new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);

        return queryResponseResult;
    }

    /**
     *  根据课程id查询课程所有章节信息
     * @author: olw
     * @Date: 2020/10/11 10:54
     * @param ids
     * @returns: java.util.Map<java.lang.String,com.xuecheng.framework.domain.course.CoursePub>
    */
    public Map<String, CoursePub> getAll (String[] ids) {

        // 设置索引库
        SearchRequest searchRequest = new SearchRequest(course_index);
        // 设置类型
        searchRequest.types(course_type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 查询条件，根据课程id查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("id", ids));
        // 取消source源字段过虑，查询所有字段
        // searchSourceBuilder.fetchSource(new String[]{"name", "grade", "charge","pic"}, newString[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
        // 执行搜索
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取搜索结果
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        Map<String,CoursePub> map = new HashMap<>();
        for (SearchHit hit : searchHits) {
            String courseId = hit.getId();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String grade = (String) sourceAsMap.get("grade");
            String charge = (String) sourceAsMap.get("charge");
            String pic = (String) sourceAsMap.get("pic");
            String description = (String) sourceAsMap.get("description");
            String teachplan = (String) sourceAsMap.get("teachplan");
            //价格
            float price = 0f;
            try {
                if(sourceAsMap.get("price")!=null ){

                    price = new Double((sourceAsMap.get("price").toString())).floatValue();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //旧价格
            float priceOld = 0f;
            try {
                if(sourceAsMap.get("price_old")!=null ){
                    priceOld = new Double(sourceAsMap.get("price_old").toString()).floatValue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String valid = (String) sourceAsMap.get("valid");
            String startTime = (String) sourceAsMap.get("start_time");
            String endTime = (String) sourceAsMap.get("end_time");

            CoursePub coursePub = new CoursePub();
            coursePub.setId(courseId);
            coursePub.setName(name);
            coursePub.setPic(pic);
            coursePub.setGrade(grade);
            coursePub.setTeachplan(teachplan);
            coursePub.setDescription(description);
            coursePub.setCharge(charge);
            coursePub.setValid(valid);
            coursePub.setPrice(price);
            coursePub.setPrice_old(priceOld);
            coursePub.setStartTime(startTime);
            coursePub.setEndTime(endTime);

            map.put(courseId,coursePub);
        }
        return map;

    }

    /**
     *  根据课程计划id获取课程媒资信息 （方便扩展这里传入的时课程计划id数组）
     * @author: olw
     * @Date: 2020/10/12 20:34
     * @param teachplanId
     * @returns: com.xuecheng.framework.domain.course.TeachplanMediaPub
    */
    public QueryResponseResult<TeachplanMediaPub> getMedia (String[] teachplanId) {
        // 设置索引库
        SearchRequest searchRequest = new SearchRequest(media_index);
        // 设置类型
        searchRequest.types(media_type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // source 原字段过滤
        String[] source_fields = media_source_field.split(",");
        searchSourceBuilder.fetchSource(source_fields, new String[]{});
        // 查询条件，根据课程id查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id", teachplanId));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        long total = 0L;
        try {
            // 执行搜索
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取搜索结果
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        total = hits.totalHits;
        for (SearchHit hit : searchHits) {
            TeachplanMediaPub teachplanMediaPub =new TeachplanMediaPub();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            // 取出课程计划媒资信息
            String courseid = (String) sourceAsMap.get("courseid");
            String media_id = (String) sourceAsMap.get("media_id");
            String media_url = (String) sourceAsMap.get("media_url");
            String teachplan_id = (String) sourceAsMap.get("teachplan_id");
            String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");
            teachplanMediaPub.setCourseId(courseid);
            teachplanMediaPub.setMediaUrl(media_url);
            teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
            teachplanMediaPub.setMediaId(media_id);
            teachplanMediaPub.setTeachplanId(teachplan_id);
            // 将数据加入列表
            teachplanMediaPubList.add(teachplanMediaPub);
        }

        QueryResult<TeachplanMediaPub> queryResult = new QueryResult();

        queryResult.setList(teachplanMediaPubList);
        queryResult.setTotal(total);

        return new QueryResponseResult<TeachplanMediaPub>(CommonCode.SUCCESS,queryResult);
    }
}
