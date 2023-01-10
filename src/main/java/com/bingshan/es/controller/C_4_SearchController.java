package com.bingshan.es.controller;

import com.bingshan.es.model.entity.Hotel;
import com.bingshan.es.service.SearchFunctionService;
import com.bingshan.es.service.SingleIndexDocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author bingshan
 * @date 2023/1/7 18:42
 */
@Slf4j
@RestController
public class C_4_SearchController {

    @Autowired
    SearchFunctionService searchFunctionService;

    @RequestMapping(value = "/testFetchSource")
    public String fetchSource()throws  Exception{
        searchFunctionService.fetchSource( );
        return "success";
    }

    @RequestMapping(value = "/testGetCityCount")
    public Long getCityCount()throws  Exception{
        Long count = searchFunctionService.getCityCount( );
        return count;
    }

    @RequestMapping(value = "/testQueryFromSize")
    public String queryFromSize()throws  Exception{
        //调用Service完成搜索
        List<Hotel> hotelList = searchFunctionService.queryFromSize();
        if(hotelList != null && hotelList.size() > 0){//搜索到结果后将其打印到前端
            log.info("query From Size: hotel= {}", hotelList.toString());
            return hotelList.toString();
        }else{
            return "no data.";
        }
    }

    @RequestMapping(value = "/testMatchAllSearch")
    public String fetmatchAllSearchchSource()throws  Exception{
        searchFunctionService.matchAllSearch( );
        return "success";
    }

    @RequestMapping(value = "/testTermDateSearch")
    public String termDateSearch()throws  Exception{
        searchFunctionService.termDateSearch();
        return "success";
    }

    @RequestMapping(value = "/testTermsSearch")
    public String termsSearch()throws  Exception{
        searchFunctionService.termsSearch();
        return "success";
    }

    @RequestMapping(value = "/testRangeSearch")
    public String rangeSearch()throws  Exception{
        searchFunctionService.rangeSearch();
        return "success";
    }

    @RequestMapping(value = "/testExistsSearch")
    public String existsSearch()throws  Exception{
        searchFunctionService.existsSearch();
        return "success";
    }

}
