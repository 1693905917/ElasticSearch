package cn.itcast.hotel.constants;

/**
 * @BelongsProject: hotel-demo
 * @BelongsPackage: cn.itcast.hotel.constants
 * @Author: ASUS
 * @CreateTime: 2023-12-04  21:50
 * @Description: TODO
 * @Version: 1.0
 */
public class MqConstants {

    /*
     * @description:交换机
     * @author:  HZP
     * @date: 2023/12/4 21:53
     * @param:
     * @return:
     **/
    public final static String HOTEL_EXCHANGE="hotel.topic";

    /*
     * @description:监听新增和修改的队列
     * @author:  HZP
     * @date: 2023/12/4 21:53
     * @param:
     * @return:
     **/
    public final static String HOTEL_INSERT_QUEUE="hotel.insert.queue";

    /*
     * @description:监听删除的队列
     * @author:  HZP
     * @date: 2023/12/4 21:53
     * @param:
     * @return:
     **/
    public final static String HOTEL_DELETE_QUEUE="hotel.delete.queue";

    /*
     * @description:新增或修改的RoutingKey
     * @author:  HZP
     * @date: 2023/12/4 21:53
     * @param:
     * @return:
     **/
    public final static String HOTEL_INSERT_KEY="hotel.insert";

    /*
     * @description:删除的RoutingKey
     * @author:  HZP
     * @date: 2023/12/4 21:54
     * @param:
     * @return:
     **/
    public final static String HOTEL_DELETE_KEY="hotel.delete";
}
