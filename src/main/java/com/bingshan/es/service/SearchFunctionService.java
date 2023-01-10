package com.bingshan.es.service;

import com.bingshan.es.model.entity.Hotel;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sun.xml.internal.ws.api.message.Packet.Status.Request;

/**
 * @author bingshan
 * @date 2023/1/7 18:41
 */
@Slf4j
@Service
public class SearchFunctionService {

    @Autowired
    RestHighLevelClient client; //自动装配RestHighLevelClient类型

    /**
     * 指定返回的字段
     */
    public void fetchSource() {
        SearchRequest searchRequest = new SearchRequest("hotel_source"); //客户端请求
        //创建搜索builder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); //构建query
        searchSourceBuilder.query(new TermQueryBuilder("city","北京")); //设定希望返回的字段数组
        searchSourceBuilder.fetchSource(new String[]{"title","city"} , null);
        SearchRequest searchRequest1 = searchRequest.source(searchSourceBuilder);
        log.info("Fetch Source : " , searchRequest1.toString());
    }

    /**
     * 对搜索结果进行计数
     * @return
     */
    public long getCityCount() {
        //客户端的count请求
        CountRequest countRequest=new CountRequest("hotel_source");
        //创建搜索builder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建query
        searchSourceBuilder.query(new TermQueryBuilder("city","北京"));
        countRequest.source(searchSourceBuilder);   //设置查询
        try {
            CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT); //执行count
            log.info("getCityCount count={}", countResponse.getCount());
            return countResponse.getCount();       //返回count结果
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 结果分页
     * @return
     * @throws IOException
     */
    public List<Hotel> queryFromSize() throws IOException {
        SearchRequest searchRequest = new SearchRequest("hotel_source"); //客户端请求
        // 创建搜索的builder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建query
        searchSourceBuilder.query(new TermQueryBuilder("city","北京"));
        searchSourceBuilder.from(0);      //设置from参数
        searchSourceBuilder.size(10);      //设置size参数
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        RestStatus status = searchResponse.status();
        if(status!= RestStatus.OK){
            return null;
        }
        List<Hotel> resultList=new ArrayList<>();
        SearchHits searchHits = searchResponse.getHits();
        for(SearchHit searchHit : searchHits){
            Hotel hotel=new Hotel();
            hotel.setId(searchHit.getId());                                 //文档_id
            hotel.setIndex(searchHit.getIndex());                           //索引名称
            hotel.setScore(searchHit.getScore());                           //文档得分

            //转换为Map
            Map<String, Object> dataMap= searchHit.getSourceAsMap();
            hotel.setTitle((String) dataMap.get("title"));  //设置标题
            hotel.setCity((String) dataMap.get("city"));    //设置城市
            hotel.setPrice((Double) dataMap.get("price"));  //设置价格
            resultList.add(hotel);
        }
        return resultList;
    }

    /**
     * 查询所有文档
     */
    public void matchAllSearch() {
        //新建搜索请求
        SearchRequest searchRequest = new SearchRequest("hotel_hotel");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery().boost(2.0f); //新建match_all查询，并设置boost值为2.0
        searchSourceBuilder.query(matchAllQueryBuilder);
        searchRequest.source(searchSourceBuilder);    //设置查询
        printResult(searchRequest);                    //打印结果
    }

    /**
     * 构建日期类型的term查询
     */
    public void termDateSearch() {
        //创建搜索请求
        SearchRequest searchRequest = new SearchRequest("hotel_hotel");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("create_time", "20210509160000"));  //构建term查询
        searchRequest.source(searchSourceBuilder);  //设置查询请求
        printResult(searchRequest);                  //打印搜索结果
    }

    /**
     * terms查询
     */
    public void termsSearch() {
        //创建搜索请求
        SearchRequest searchRequest = new SearchRequest("hotel_hotel");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建terms查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("city","北京","天津"));
        searchRequest.source(searchSourceBuilder);  //设置查询请求
        printResult(searchRequest);                  //打印搜索结果
    }

    /**
     * range查询用于范围查询，
     */
    public void rangeSearch() {
        //创建搜索请求
        SearchRequest searchRequest = new SearchRequest("hotel_hotel");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建range查询
        QueryBuilder queryBuilder = QueryBuilders.rangeQuery("create_time")
                .gte("20210115120000")
                .lte("20220116120000");
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);   //设置查询请求
        printResult(searchRequest);                  //打印搜索结果
    }

    /**
     * exists查询
     */
    public void existsSearch(){
        SearchRequest searchRequest=new SearchRequest("hotel_1");
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.existsQuery("tag"));
        searchRequest.source(searchSourceBuilder);
        printResult(searchRequest);
    }


    /**
     * 打印搜索结果的方法
     * @param searchRequest
     */
    public void printResult(SearchRequest searchRequest) {
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);                                  //执行搜索
            SearchHits searchHits = searchResponse.getHits(); //获取搜索结果集
            for (SearchHit searchHit : searchHits) {          //遍历搜索结果集
                String index = searchHit.getIndex();          //获取索引名称
                String id = searchHit.getId();                //获取文档_id
                Float score = searchHit.getScore();           //获取得分
                String source = searchHit.getSourceAsString(); //获取文档内容
                System.out.println("index=" + index + ",id=" + id  + ",score= " + score + ",source=" + source); //打印数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
