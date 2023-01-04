package com.bingshan.es.controller;
import com.bingshan.es.model.entity.Hotel;
import com.bingshan.es.service.EsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * @author bingshan
 * @date 2023/1/4 19:35
 */
@Slf4j
@RestController
public class TestController {
    @Autowired
    EsService esService;

    @RequestMapping(value = "/test")
    public String getRec()throws  Exception{
        //调用Service完成搜索
        List<Hotel> hotelList = esService.getHotelFromTitle("再来");
        if(hotelList != null && hotelList.size() > 0){//搜索到结果后将其打印到前端
            log.info("RestHighLevelClient: hotel= {}", hotelList.toString());
            return hotelList.toString();
        }else{
            return "no data.";
        }
    }
}

