package com.mengzhiayuan.naruto.event;

import com.alibaba.fastjson.JSONObject;
import com.mengzhiayuan.naruto.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @Auther: 梦致A远
 * @Date: 2021/9/8 15:37
 * @Description:
 */

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void pushEvent(Event event){
        //将事件发送到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}
