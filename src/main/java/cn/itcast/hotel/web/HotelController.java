package cn.itcast.hotel.web;

import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: hotel-demo
 * @BelongsPackage: cn.itcast.hotel.web
 * @Author: ASUS
 * @CreateTime: 2023-11-21  20:15
 * @Description: TODO
 * @Version: 1.0
 *
 */
@RestController
@RequestMapping("/hotel")
public class HotelController {
    @Autowired
    private IHotelService hotelService;

    /*
     * @description:酒店搜索
     * @author:  HZP
     * @date: 2023/11/21 20:19
     * @param:
     * @return:
     **/
    @RequestMapping("/list")
    public PageResult search(@RequestBody RequestParams params){
        return hotelService.search(params);
    }

    /*
     * @description:酒店搜索
     * @author:  HZP
     * @date: 2023/11/28 22:07
     * @param:
     * @return:
     **/
    @PostMapping
    public Map<String, List<String>> getFilters(@RequestBody RequestParams params){
        return hotelService.filters(params);
    }



}
