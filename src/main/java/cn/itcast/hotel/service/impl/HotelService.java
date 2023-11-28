package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javafx.beans.binding.BooleanBinding;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {
    @Autowired
    private RestHighLevelClient client;
    /*
     * @description:酒店搜索
     * @author:  HZP
     * @date: 2023/11/21 20:19
     * @param:
     * @return:
     **/
    @Override
    public PageResult search(RequestParams params) {
        SearchRequest request = new SearchRequest("hotel");

        buildBasicQuery(params, request);
        int page=params.getPage();
        int size=params.getSize();
        request.source().from((page-1)*size).size(size);
        String location = params.getLocation();
        if(location!=null && !location.equals("")){
            //距离排序
            request.source().sort(SortBuilders.geoDistanceSort("location",new GeoPoint(location)).order(SortOrder.ASC).unit(DistanceUnit.KILOMETERS));
        }
        try {
            SearchResponse response=client.search(request,RequestOptions.DEFAULT);
            return  handleResponse(response);
        } catch (IOException e) {
            throw  new RuntimeException(e);
        }

    }

    //RequestParams params
    @Override
    public Map<String, List<String>> filters(RequestParams params) {
        SearchRequest request = new SearchRequest("hotel");
        buildBasicQuery(params,request);
        request.source().size(0);
        buildAggregation(request);
        SearchResponse response=null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, List<String>> result = new HashMap<>();
        Aggregations aggregations = response.getAggregations();
        List<String> brandList = getAggByName(aggregations, "brandAgg");
        result.put("品牌",brandList);
        List<String> cityList = getAggByName(aggregations, "cityAgg");
        result.put("城市",cityList);
        List<String> starList = getAggByName(aggregations, "starAgg");
        result.put("星级",starList);
        return result;
    }



    private List<String> getAggByName(Aggregations aggregations,String aggName){
        Terms brandTerms = aggregations.get(aggName);
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        List<String> list = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            list.add(key);
        }
        return list;
    }

    private void buildAggregation(SearchRequest request){
        //品牌
        request.source().aggregation(
                AggregationBuilders.terms("brandAgg").field("brand").size(20)
        );
        //城市
        request.source().aggregation(
                AggregationBuilders.terms("cityAgg").field("city").size(20)
        );
        //星级
        request.source().aggregation(
                AggregationBuilders.terms("starAgg").field("starName").size(20)
        );
    }


    private void buildBasicQuery(RequestParams params, SearchRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        String key = params.getKey();
        //关键字查询
        if(key==null|| "".equals(key)){
            boolQuery.must(QueryBuilders.matchAllQuery());
        }else{
            boolQuery.must(QueryBuilders.matchQuery("all",key));
        }
        //城市条件
        if(params.getCity()!=null && !params.getCity().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
        }
        //品牌条件
        if(params.getBrand()!=null && !params.getBrand().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }
        //星级条件
        if(params.getStarName()!=null && !params.getStarName().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }
        //价格范围
        if(params.getMinPrice()!=null && params.getMaxPrice()!=null){
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(params.getMinPrice()).lte(params.getMaxPrice()));
        }
        request.source().query(boolQuery);
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                boolQuery,
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("isAD", true),
                                ScoreFunctionBuilders.weightFactorFunction(10)
                        )
                });
        request.source().query(functionScoreQuery);
    }

    private PageResult handleResponse(SearchResponse response) throws IOException {
        //System.out.println(response);
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        SearchHit[] hits = searchHits.getHits();
        ArrayList<HotelDoc> hotels = new ArrayList<>();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            Object[] sortValues = hit.getSortValues();
            if(sortValues.length>0){
                Object sortValue = sortValues[0];
                hotelDoc.setDistance(sortValue);
            }
            hotels.add(hotelDoc);
        }
        return new PageResult(total,hotels);
    }





}
