package com.bingshan.es.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author bingshan
 * @date 2023/1/6 15:34
 */
@Slf4j
@Service
public class SingleIndexDocService {
    @Autowired
    RestHighLevelClient client; //自动装配RestHighLevelClient类型

    public void singleIndexDoc(Map<String, Object> dataMap, String indexName, String indexId) {
        IndexRequest indexRequest = new IndexRequest(indexName).id(indexId).source(dataMap); //构建IndexRequest对象并设置对应的索引和_id字段名称
        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT); //执行写入
            //通过IndexResponse获取索引名称
            String index = indexResponse.getIndex();
            String id = indexResponse.getId();//通过IndexResponse获取文档ID
            //通过IndexResponse获取文档版本
            Long version = indexResponse.getVersion();
            log.info("IndexDoc index={} ,id={} ,version={} ", index, id, version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bulkIndexDoc(String indexName, String docIdKey, List<Map<String, Object>> recordMapList) {
        //构建批量操作BulkRequest对象
        BulkRequest bulkRequest = new BulkRequest(indexName);
        for (Map<String, Object> dataMap : recordMapList) { //遍历数据
            //获取主键作为ES索引的主键
            String docId = dataMap.get(docIdKey).toString();
            dataMap.remove(docIdKey);
            IndexRequest indexRequest = new IndexRequest().id(docId).source(dataMap); //构建IndexRequest对象
            bulkRequest.add(indexRequest); //添加IndexRequest
        }
        bulkRequest.timeout(TimeValue.timeValueSeconds(5)); //设置超时时间
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT); //执行批量写入
            if (bulkResponse.hasFailures()) { //判断执行状态
                log.info("bulk fail,message: {}",  bulkResponse.buildFailureMessage());
            } else {
                log.info("bullk sucess.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void singleUpdate(String indexName, String docIdKey, Map<String, Object> recordMap) {
        UpdateRequest updateRequest = new UpdateRequest(indexName, docIdKey);
        updateRequest.doc(recordMap);
        try {
            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
            //通过IndexResponse获取索引名称
            String index = updateResponse.getIndex();
            //通过IndexResponse获取文档ID
            String id = updateResponse.getId();
            //通过IndexResponse获取文档版本
            Long version = updateResponse.getVersion();
            log.info("Update index={} ,id={} ,version={} ", index, id, version);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void singleUpsert(String indexName, String docIdKey, Map<String, Object> recordMap,Map<String, Object> upRecordMap) {
        //构建UpdateRequest
        UpdateRequest updateRequest = new UpdateRequest(indexName, docIdKey);
        updateRequest.doc(recordMap);        //设置更新逻辑
        updateRequest.upsert(upRecordMap);   //设置插入逻辑
        try {
            //执行upsert命令
            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
            //通过IndexResponse获取索引名称
            String index = updateResponse.getIndex();
            //通过IndexResponse获取文档ID
            String id = updateResponse.getId();
            //通过IndexResponse获取文档版本
            Long version = updateResponse.getVersion();
            log.info("Upsert index={} ,id={} ,version={} ", index, id, version);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bulkUpdate(String index, String docIdKey, List<Map<String, Object>> recordMapList) {
        BulkRequest bulkRequest = new BulkRequest();//构建BulkRequest对象
        for (Map<String, Object> dataMap : recordMapList) {//遍历数据列表
            String docId = dataMap.get(docIdKey).toString();
            dataMap.remove(docIdKey);            //将ID字段从map中删除
            //创建UpdateRequest对象
            bulkRequest.add(new UpdateRequest(index, docId).doc(dataMap));
        }
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);  //执行批量更新
            if (bulkResponse.hasFailures()) { //判断执行状态
                log.info("bulk fail,message: {}",  bulkResponse.buildFailureMessage());
            } else {
                log.info("bullk sucess.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCityByQuery(String index, String oldCity, String newCity) {
        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(index);  //构建UpdateByQueryRequest对象
        //设置按照城市查找文档的query
        updateByQueryRequest.setQuery(new TermQueryBuilder("city",oldCity));
        updateByQueryRequest.setScript(new Script("ctx._source['city']= '" + newCity + "';"));  //设置更新城市字段的脚本逻辑
        try {
            BulkByScrollResponse bulkByScrollResponse = client.updateByQuery(updateByQueryRequest,RequestOptions.DEFAULT);   //执行更新
            log.info("updateByQuery sucess.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void singleDelete(String indexName, String docId) {
        //构建删除请求
        DeleteRequest deleteRequest=new DeleteRequest(indexName, docId);
        try {
            DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);//执行删除
            //通过IndexResponse获取索引名称
            String index = deleteResponse.getIndex();
            //通过IndexResponse获取文档ID
            String id = deleteResponse.getId();
            //通过IndexResponse获取文档版本
            Long version = deleteResponse.getVersion();
            log.info("Delete index={} ,id={} ,version={} ", index, id, version);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bulkDelete(String index, String docIdKey, List<String> docIdList) {
        BulkRequest bulkRequest = new BulkRequest();    //构建BulkRequest对象
        for (String docId : docIdList) {            //遍历文档_id列表
            //构建删除请求
            DeleteRequest deleteRequest=new DeleteRequest(index,docId);
            bulkRequest.add(deleteRequest);        //创建UpdateRequest对象
        }
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest,RequestOptions.DEFAULT);                                      //执行批量删除
            if (bulkResponse.hasFailures()) { //判断执行状态
                log.info("Bulk Delete fail,message: {}",  bulkResponse.buildFailureMessage());
            } else {
                log.info("Bullk Delete sucess.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteByQuery(String index,String city) {
        //构建DeleteByQueryRequest对象
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(index);
        //设置按照城市查找文档的query
        deleteByQueryRequest.setQuery(new TermQueryBuilder("city",city));
        try {
            //执行删除命令
            client.deleteByQuery(deleteByQueryRequest,RequestOptions.DEFAULT);
            log.info("deleteByQuery sucess.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
