package com.bingshan.es.service;
import com.bingshan.es.mapper.EsRepositorySD;
import com.bingshan.es.model.entity.HotelSD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * @author bingshan
 * @date 2023/1/4 20:06
 */
@Service
public class EsServiceSD {
    @Autowired
    EsRepositorySD esRepositorySD;

    public List<HotelSD> getHotelFromTitle(String keyword){
        return  esRepositorySD.findByTitleLike(keyword);//调用搜索方法
    }
}
