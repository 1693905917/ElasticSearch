package cn.itcast.hotel;

import cn.itcast.hotel.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @BelongsProject: hotel-demo
 * @BelongsPackage: cn.itcast.hotel
 * @Author: ASUS
 * @CreateTime: 2023-11-17  15:27
 * @Description: TODO
 * @Version: 1.0
 */
public class HotelISearchTest {
    private RestHighLevelClient client;

    @BeforeEach
    void setUp() {
        this.client=new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.249.128:9200")));
    }

    @Test
    void testMatchAll() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchAllQuery());
        handleResponse(request);
    }

    @Test
    void testMatch() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchQuery("all","如家"));
        handleResponse(request);
    }

    @Test
    void testMultiMatch() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.multiMatchQuery("如家","name","business"));
        handleResponse(request);
    }

    @Test
    void testTermMatch() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.termQuery("city","深圳"));
        handleResponse(request);
    }

    @Test
    void testRangeMatch() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.rangeQuery("price").gte(100).lte(150));
        handleResponse(request);
    }
    @Test
    void testBool() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        BoolQueryBuilder boolQuery= QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("city","深圳"));
        boolQuery.filter(QueryBuilders.rangeQuery("price").lte(250));
        request.source().query(boolQuery);
        handleResponse(request);
    }

    @Test
    void testPageAndSort() throws IOException {
        int page=2,size=5;
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchAllQuery());
        request.source().sort("price", SortOrder.ASC);
        request.source().from((page-1)*size).size(5);
        handleResponse(request);
    }
    @Test
    void testHighLight() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchQuery("all","如家"));

        request.source().highlighter(new HighlightBuilder()
                .field("name")
                .requireFieldMatch(false));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(!CollectionUtils.isEmpty(highlightFields)){
                HighlightField highlightField = highlightFields.get("name");
                if(highlightField!=null){
                    String name = highlightField.getFragments()[0].string();
                    hotelDoc.setName(name);
                }
            }
            System.out.println("hotelDoc:"+hotelDoc);
        }


    }

    private void handleResponse(SearchRequest request) throws IOException {
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //System.out.println(response);
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            System.out.println("hotelDoc"+hotelDoc);
        }
    }


    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }
}
