package com.code.global;


import com.code.producer.ProducerTemplate;

/**
 * 管理控制和rabbitmq的通信
 * @author huotianyu
 * date 2024.4.2
 */
public interface EventController {

    /**
     * 获取发送模版
     */
    ProducerTemplate getEopEventTemplate();


}
