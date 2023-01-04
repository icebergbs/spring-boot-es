package com.bingshan.es.service;

import com.bingshan.es.model.entity.Hotel;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author bingshan
 * @date 2023/1/4 18:04
 */
@Service
public class EsService {
    @Autowired
    RestHighLevelClient client; //自动装配RestHighLevelClient类型

    public List<Hotel> getHotelFromTitle(String keyword) {
        SearchRequest searchRequest= new SearchRequest("hotel");//客户端请求
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //构建query
        searchSourceBuilder.query(QueryBuilders.matchQuery("title",keyword));
        searchRequest.source(searchSourceBuilder);
        List<Hotel> resultList=new ArrayList<>();
        try{
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            RestStatus status = searchResponse.status();
            if(status!= RestStatus.OK){
                return null;
            }
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
