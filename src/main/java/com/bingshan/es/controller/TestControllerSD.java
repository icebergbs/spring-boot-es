package com.bingshan.es.controller;
import com.bingshan.es.model.entity.HotelSD;
import com.bingshan.es.service.EsServiceSD;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
/**
 * @author bingshan
 * @date 2023/1/4 20:08
 */
@Slf4j
@RestController
public class TestControllerSD {
    @Autowired
    EsServiceSD esServiceSD;
    @RequestMapping(value = "/testSD")
    public String getRec()throws  Exception{
        //调用Service完成搜索
        List<HotelSD> hotelList = esServiceSD.getHotelFromTitle("再来");
        if(hotelList!=null && hotelList.size()>0){  //搜索到结果打印到前端
            log.info("Spring Data client: hotel= {}", hotelList.toString());
            return hotelList.toString();
        }else{
            return "no data.";
        }
    }
}