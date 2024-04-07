package com.code.producer;


import com.code.entity.MessageEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 *
 * 封装发送方法
 * @author huotianyu
 * date 2024.4.3
 */
public interface ProducerTemplate {

    /**
     * 发送消息，不限制消息类型
     * @param queueName 队列名
     * @param exchangeName 交换机
     * @param eventContent 发送对象
     * @param correlationData messageId
     * @throws Exception
     */
    void send(String queueName, String exchangeName, Object eventContent, CorrelationData correlationData) throws Exception;

    /**
     * 发送消息，限制消息类型,只能发送MessageEntity，方法内转换为JSONObject
     * @param queueName 队列名
     * @param exchangeName 交换机
     * @param entity 发送对象
     * @param correlationData messageId
     * @throws Exception
     */
    void sendMessageEntity(String queueName, String exchangeName, MessageEntity entity, CorrelationData correlationData) throws Exception;

}
