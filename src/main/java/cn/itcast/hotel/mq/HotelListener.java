package cn.itcast.hotel.mq;

import cn.itcast.hotel.constants.MqConstants;
import cn.itcast.hotel.service.IHotelService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: hotel-demo
 * @BelongsPackage: cn.itcast.hotel.mq
 * @Author: ASUS
 * @CreateTime: 2023-12-04  22:05
 * @Description: TODO
 * @Version: 1.0
 */
@Component
public class HotelListener {
    @Autowired
    private IHotelService hotelService;

    @RabbitListener(queues = MqConstants.HOTEL_INSERT_QUEUE)
    public void listenHotelInsertOrUpdate(Long id){
        hotelService.insertById(id);
    }

    @RabbitListener(queues = MqConstants.HOTEL_DELETE_QUEUE)
    public void listenHotelDelete(Long id){
        hotelService.deleteById(id);
    }


}
