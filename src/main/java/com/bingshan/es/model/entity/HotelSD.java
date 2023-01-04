package com.bingshan.es.model.entity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
/**
 * Spring Data POJO
 * @author bingshan
 * @date 2023/1/4 20:01
 */
@Document(indexName = "hotel")
@Data
public class HotelSD {
    @Id                 //对应Elasticsearch的_id
    String id;
    String title;      //对应索引中的title
    String city;       //对应索引中的city
    String price;      //对应索引中的price
}
