package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.pattern.PathPattern;

import java.io.IOException;
import java.util.List;

import static cn.itcast.hotel.constants.HotelConstants.MAPPING_TEMPLATE;

/**
 * @BelongsProject: hotel-demo
 * @BelongsPackage: cn.itcast.hotel
 * @Author: ASUS
 * @CreateTime: 2023-11-13  11:05
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootTest
public class HotelIndexTest {
    @Autowired
    private IHotelService hotelService;

    private RestHighLevelClient client;

    @BeforeEach
    void setUp() {
        this.client=new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.249.128:9200")));
    }

    @Test
    void testIndexDocument() throws IOException {
        Hotel hotel = hotelService.getById(36934L);
        HotelDoc hotelDoc = new HotelDoc(hotel);
        IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
        request.source(JSON.toJSONString(hotelDoc),XContentType.JSON);
        client.index(request,RequestOptions.DEFAULT);
    }

    @Test
    void testGetDocumentById() throws IOException {
        GetRequest request = new GetRequest("hotel", "36934");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String json = response.getSourceAsString();
        System.out.println(json);
    }

    @Test
    void testUpdateDocumentById() throws IOException {
        UpdateRequest request = new UpdateRequest("hotel", "36934");
        request.doc("price","952","starName","四钻");
        client.update(request,RequestOptions.DEFAULT);
    }

    @Test
    void testDeleteDocumentById() throws IOException {
        DeleteRequest request = new DeleteRequest("hotel","36934");
        client.delete(request,RequestOptions.DEFAULT);
    }

    @Test
    void testBulk() throws IOException {
        List<Hotel> hotels = hotelService.list();
        BulkRequest request = new BulkRequest();

        for (Hotel hotel : hotels) {
            HotelDoc hotelDoc = new HotelDoc(hotel);
            request.add(new IndexRequest("hotel").id(hotelDoc.getId().toString()).source(JSON.toJSONString(hotelDoc),XContentType.JSON));
        }
        client.bulk(request,RequestOptions.DEFAULT);
    }




//    @Test
//    void testInit() throws IOException {
//        //1.创建Request对象
//        CreateIndexRequest request=new CreateIndexRequest("hotel");
//        //2.准备请求的参数：DSL语句
//        request.source(MAPPING_TEMPLATE, XContentType.JSON);
//        //3.发送请求
//        client.indices().create(request, RequestOptions.DEFAULT);
//    }
//
//    @Test
//    void testDeleteHotelIndex() throws IOException {
//        //1.创建Request对象
//        DeleteIndexRequest request = new DeleteIndexRequest("hotel");
//        //2.发起请求
//        client.indices().delete(request,RequestOptions.DEFAULT);
//    }
//
//    @Test
//    void testExistsHotelIndex() throws IOException {
//        //1.创建Request对象
//        GetIndexRequest request = new GetIndexRequest("hotel");
//        //2.发起请求
//        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
//        System.out.println(exists);
//    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }
}
