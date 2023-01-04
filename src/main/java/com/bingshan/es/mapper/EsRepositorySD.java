package com.bingshan.es.mapper;
import com.bingshan.es.model.entity.HotelSD;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
/**
 * Spring Data  EsRepository
 * @author bingshan
 * @date 2023/1/4 20:04
 */
public interface EsRepositorySD extends CrudRepository<HotelSD,String>{
    List<HotelSD> findByTitleLike(String title);
}

