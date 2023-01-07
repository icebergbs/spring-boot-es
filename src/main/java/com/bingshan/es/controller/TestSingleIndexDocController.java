package com.bingshan.es.controller;

import com.bingshan.es.model.entity.Hotel;
import com.bingshan.es.service.EsService;
import com.bingshan.es.service.SingleIndexDocService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bingshan
 * @date 2023/1/6 15:38
 */
@Slf4j
@RestController
public class TestSingleIndexDocController {

    @Autowired
    SingleIndexDocService singleIndexDocService;

    @RequestMapping(value = "/testSingleIndexDoc")
    public String getSingleIndexDoc(String indexId)throws  Exception{
        //调用Service完成搜索
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("title", "好再来酒店");
        dataMap.put("city", "青岛");
        dataMap.put("price", 578.23);
        singleIndexDocService.singleIndexDoc(dataMap, "hotel_hotel", indexId);
        return "success";
    }

    @RequestMapping(value = "/testBulkIndexDoc")
    public String getBulkIndexDoc()throws  Exception{
        //调用Service完成搜索
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("title", "文雅酒店");
        dataMap.put("city", "北京");
        dataMap.put("price", 556.00);
        dataMap.put("_id", "005");
        Map<String, Object> dataMap1 = new HashMap<>();
        dataMap1.put("title", "嘉怡假日酒店");
        dataMap1.put("city", "北京");
        dataMap1.put("price", 337.00);
        dataMap1.put("_id", "006");
        List<Map<String, Object>> recodeMapList = new ArrayList<>();
        recodeMapList.add(dataMap);
        recodeMapList.add(dataMap1);

        singleIndexDocService.bulkIndexDoc("hotel_hotel", "_id", recodeMapList);
        return "success";
    }

    @RequestMapping(value = "/testSingleUpdate")
    public String getSingleUpdate()throws  Exception{
        //调用Service完成搜索
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("title", "好再来酒店");
        dataMap.put("city", "北京");
        dataMap.put("price", 659.45);
        singleIndexDocService.singleUpdate("hotel_hotel", "001", dataMap);
        return "success";
    }

    @RequestMapping(value = "/testSingleUpsert")
    public String getSingleUpsert()throws  Exception{
        //调用Service完成搜索
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("title", "好再来酒店-doc");
        dataMap.put("city", "北京-doc");
        dataMap.put("price", 659.45);

        Map<String, Object> upDataMap = new HashMap<>();
        upDataMap.put("title", "好再来酒店-up");
        upDataMap.put("city", "北京-up");
        upDataMap.put("price", 659.45);
        singleIndexDocService.singleUpsert("hotel_hotel", "001", dataMap, upDataMap);
        return "success";
    }

    @RequestMapping(value = "/testBulkUpdate")
    public String getBulkUpdate()throws  Exception{
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("title", "文雅豪情酒店-bulk");
        dataMap.put("city", "北京-bulk");
        dataMap.put("price", 556.00);
        dataMap.put("_id", 005);
        Map<String, Object> dataMap1 = new HashMap<>();
        dataMap1.put("title", "嘉怡七天酒店-bulk");
        dataMap1.put("city", "北京-bulk");
        dataMap1.put("price", 337.00);
        dataMap1.put("_id", 006);
        List<Map<String, Object>> recodeMapList = new ArrayList<>();
        recodeMapList.add(dataMap);
        recodeMapList.add(dataMap1);
        singleIndexDocService.bulkUpdate("hotel_hotel", "_id", recodeMapList);
        return "success";
    }

    @RequestMapping(value = "/testUpdateCityByQuery")
    public String updateCityByQuery()throws  Exception{
        singleIndexDocService.updateCityByQuery("hotel_hotel", "上海", "北京");
        return "success";
    }

    @RequestMapping(value = "/testSingleDelete")
    public String singleDelete()throws  Exception{
        singleIndexDocService.singleDelete("hotel_hotel", "003");
        return "success";
    }

    @RequestMapping(value = "/testBulkDelete")
    public String bulkDelete()throws  Exception{
        List<String> docIdList = new ArrayList<>();
        docIdList.add("005");
        docIdList.add("006");
        singleIndexDocService.bulkDelete("hotel_hotel", "003", docIdList);
        return "success";
    }







}
