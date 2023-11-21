package cn.itcast.hotel.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @BelongsProject: hotel-demo
 * @BelongsPackage: cn.itcast.hotel.pojo
 * @Author: ASUS
 * @CreateTime: 2023-11-21  20:13
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult {
    private Long total;
    private List<HotelDoc> hotels;
}
