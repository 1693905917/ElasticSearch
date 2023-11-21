package cn.itcast.hotel.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: hotel-demo
 * @BelongsPackage: cn.itcast.hotel.pojo
 * @Author: ASUS
 * @CreateTime: 2023-11-21  20:11
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestParams {
    private String key;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String brand;
    private String starName;
    private String city;
    private Integer minPrice;
    private Integer maxPrice;
    private String location;
}
