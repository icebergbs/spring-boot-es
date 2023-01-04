package com.bingshan.es.model.entity;
import lombok.Data;
/**
 * @author bingshan
 * @date 2023/1/4 18:57
 */
@Data
public class Hotel {
    String id;                                                  //对应文档_id
    String index;                                               //对应索引名称
    Float score;                                                //对应文档得分

    String title;                                               //对应索引中的title
    String city;                                                //对应索引中的city
    Double price;                                               //对应索引中的price
}
